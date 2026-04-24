# Runtime execution and benchmark suite orchestration

. "$PSScriptRoot\types.ps1"
. "$PSScriptRoot\statistics_utils.ps1"

<#
.SYNOPSIS
  Runs a benchmark suite over a list of executables and writes per-executable JSON files
  plus summary _results.csv and _results.json files.

.DESCRIPTION
  For each executable:
    1. Runs $RepetitionCount timed iterations, parsing "### Elapsed time: <µs>" from stdout
       for the total time and "!!! Elapsed time <n>: <µs>" for per-step times.
    2. Calls $PostIterationAction (if provided) after each timed iteration, passing
       ($executable, $iterationNumber, $MeasurementDir) as positional arguments.
    3. Computes statistics (mean, median, stddev, CI95) over collected total times.
    4. Writes a per-executable JSON file to $MeasurementDir/<name>.json.
    5. Accumulates a CSV record for the summary files.
  After all executables: writes _results.csv and _results.json to $MeasurementDir.
  Prints [X/N] progress with an ETA estimate after each executable completes.

## Output Format (per-executable JSON)
  {
    "parameters":  { ... }              // caller-supplied Parameters hashtable
    "machineInfo": { ... }              // Get-MachineInfo result
    "buildTimeMs": <ms>|null
    "executable":  "<name>"
    "repetitions": <N>
    "timeUnit":    "microseconds"
    "times":       [<µs>, ...]          // total elapsed time per repetition
    "stepTimes":   [[<µs>, ...], ...]   // per-step times; outer=repetitions, inner=steps
    "statistics":  { count, mean, median, stddev, min, max, ci95 }
    "status":      "ok" | "failed"      // "failed" when no successful measurements
  }

.PARAMETER Executables
  Array of [BenchmarkExecutable] objects describing each executable to benchmark.

.PARAMETER RepetitionCount
  Number of timed iterations per executable.

.PARAMETER WarmupCount
  Number of warm-up iterations to run before timing (output discarded). Default: 0.

.PARAMETER StepCount
  Positional argument passed to each executable.

.PARAMETER MeasurementDir
  Pre-existing directory where per-executable JSON files and summary files are written.

.PARAMETER MachineInfo
  Machine/environment hashtable from Get-MachineInfo.

.PARAMETER BuildTimes
  Hashtable of executable-name → build-duration-ms, used to populate buildTimeMs in output.

.PARAMETER Parameters
  Ordered hashtable of test-suite-specific parameters embedded verbatim in each JSON payload.

.PARAMETER CleanBuild
  Forwarded to Build-BenchmarkCSVRecord. Should match the value in Parameters if present.

.PARAMETER PostIterationAction
  Optional scriptblock invoked after each timed iteration as:
    & $PostIterationAction $executable $iterationNumber $MeasurementDir
#>
function Invoke-BenchmarkSuite {
  param(
    [BenchmarkExecutable[]]$Executables,
    [int]$RepetitionCount,
    [int]$WarmupCount = 0,
    [int]$StepCount,
    [string]$MeasurementDir,
    [object]$MachineInfo,
    [System.Collections.IDictionary]$BuildTimes = @{},
    [object]$Parameters,
    [bool]$CleanBuild = $false,
    [scriptblock]$PostIterationAction = $null
  )

  # Validate that all executables exist before starting any measurements
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "## Validating Executables..."
  Write-Host "=========================================="

  [BenchmarkExecutable[]]$missingExecutables = @()
  foreach ($executable in $Executables) {
    $filePath = $executable.Path
    if (Test-Path $filePath) {
      Write-Host "OK: Found: $($executable.Name) at $filePath"
    }
    else {
      Write-Host "ERROR: NOT FOUND: $($executable.Name) at $filePath"
      $missingExecutables += $executable
    }
  }

  if ($missingExecutables.Count -gt 0) {
    Write-Host ""
    Write-Host "ERROR: The following executables were not found:"
    foreach ($missing in $missingExecutables) {
      Write-Host "  - $($missing.Name)"
      Write-Host "    expected at: $($missing.Path)"
    }
    Write-Host ""
    Write-Host "Cannot proceed with benchmarking. Please check the build output above."
    exit 1
  }

  Write-Host ""
  Write-Host "All executables found! Proceeding with benchmarks..."
  Write-Host "=========================================="

  $csvRecords = @()
  $totalExecutables = $Executables.Count
  $completedExecutables = 0
  $benchmarkStartTime = Get-Date

  foreach ($executable in $Executables) {
    $filePath = $executable.Path
    if (-not $IsWindows) { $filePath = $filePath -replace '\\', '/' }
    $fileType = $executable.Type

    Write-Host ""
    Write-Host "--------------------------------------------------------"

    # Warmup iterations (output discarded)
    if ($WarmupCount -gt 0) {
      Write-Host "[WARMUP] Running $WarmupCount warmup iteration(s) for $($executable.Name)..."
      for ($w = 1; $w -le $WarmupCount; $w++) {
        try {
          switch ($fileType) {
            ([ExecutableType]::Jar) { java -jar $filePath $StepCount 2>&1 | Out-Null }
            ([ExecutableType]::Exe) { & $filePath $StepCount 2>&1 | Out-Null }
            ([ExecutableType]::Node) { node $filePath $StepCount 2>&1 | Out-Null }
          }
        }
        catch { Write-Host "[WARMUP] Iteration $w failed: $_" }
      }
      Write-Host "[WARMUP] Done."
    }

    # Timed iterations (elapsed times in microseconds)
    $elapsedTimes = @()
    $stepTimesAllReps = @()

    for ($i = 1; $i -le $RepetitionCount; $i++) {
      Write-Host ""
      Write-Host "Running iteration $i of $RepetitionCount for $($executable.Name):"

      $output = $null
      $executionSuccess = $false

      try {
        switch ($fileType) {
          ([ExecutableType]::Jar) {
            $output = java -jar $filePath $StepCount
            $executionSuccess = $LASTEXITCODE -eq 0
          }
          ([ExecutableType]::Exe) {
            $output = & $filePath $StepCount 2>&1
            $executionSuccess = $LASTEXITCODE -eq 0
          }
          ([ExecutableType]::Node) {
            $output = node $filePath $StepCount 2>&1
            $executionSuccess = $LASTEXITCODE -eq 0
          }
        }
      }
      catch {
        Write-Host "ERROR: Failed to execute $($executable.Name): $_"
        $executionSuccess = $false
      }

      if (-not $executionSuccess) {
        Write-Host "ERROR: Execution failed for iteration $i"
        continue
      }

      $elapsedLine = $output | Select-String "^### Elapsed time: "
      if ($elapsedLine) {
        $elapsedTime = ($elapsedLine -replace "### Elapsed time:\s*", "").Trim()
        Write-Host ("- Ran {0:N3} ms" -f ([long]$elapsedTime / 1000))
        $elapsedTimes += $elapsedTime

        # Collect per-step times for this repetition
        $stepTimesThisRep = @()
        $stepLines = $output | Select-String "^!!! Elapsed time \d+: "
        foreach ($stepLine in $stepLines) {
          $stepMicros = ($stepLine -replace "^!!! Elapsed time \d+:\s*", "").Trim()
          $stepTimesThisRep += [double]$stepMicros
        }
        $stepTimesAllReps += , $stepTimesThisRep
      }
      else {
        Write-Host "- Elapsed time not found in iteration $i"
      }

      if ($null -ne $PostIterationAction) {
        & $PostIterationAction $executable $i $MeasurementDir
      }
    } # End of iteration loop

    # Warn and mark failed if no measurements were collected
    if ($elapsedTimes.Count -eq 0) {
      Write-Host ""
      Write-Host "========================================================"
      Write-Host "[WARN] No successful measurements for $($executable.Name)"
      Write-Host "========================================================"
    }

    # Compute statistics and write per-executable JSON
    $outputFilePath = Join-Path $MeasurementDir ("{0}.json" -f $executable.Name)
    $numericTimes = $elapsedTimes | ForEach-Object { [double]$_ }
    $stats = Get-BenchmarkStatistics -Values $numericTimes
    $relevantBuildTime = if ($BuildTimes.Contains($executable.Name)) { $BuildTimes[$executable.Name] } else { $null }
    $status = if ($elapsedTimes.Count -eq 0) { "failed" } else { "ok" }

    $payload = [ordered]@{
      parameters  = $Parameters
      machineInfo = $MachineInfo
      buildTimeMs = $relevantBuildTime
      executable  = $executable.Name
      repetitions = $RepetitionCount
      timeUnit    = "microseconds"
      times       = $numericTimes
      stepTimes   = $stepTimesAllReps
      statistics  = $stats
      status      = $status
    }

    $payload | ConvertTo-Json -Depth 6 | Out-File -FilePath $outputFilePath -Encoding utf8
    Write-Host "Results saved to $outputFilePath"

    $csvRecord = Build-BenchmarkCSVRecord `
      -ExecutableName $executable.Name `
      -Statistics $stats `
      -MachineInfo $MachineInfo `
      -RepetitionCount $RepetitionCount `
      -CleanBuild $CleanBuild `
      -StepCount $StepCount `
      -BuildTime $relevantBuildTime `
      -AdditionalParameters $Parameters
    $csvRecords += $csvRecord

    # Progress and ETA
    $completedExecutables++
    $elapsedSec = ((Get-Date) - $benchmarkStartTime).TotalSeconds
    $avgPerExec = $elapsedSec / $completedExecutables
    $remainingSec = ($totalExecutables - $completedExecutables) * $avgPerExec
    Write-Host ""
    Write-Host ("[{0}/{1}] Completed: {2}  (estimated ~{3:N0}s remaining)" -f $completedExecutables, $totalExecutables, $executable.Name, $remainingSec)

  } # End of executables loop

  # Write summary files
  $csvFilePath = Join-Path $MeasurementDir "_results.csv"
  $jsonFilePath = Join-Path $MeasurementDir "_results.json"

  Export-BenchmarkResultsToCSV  -Results $csvRecords -OutputPath $csvFilePath
  Export-BenchmarkResultsToJSON -Results $csvRecords -OutputPath $jsonFilePath

  Write-Host ""
  Write-Host "=========================================="
  Write-Host "# All benchmarks complete."
  Write-Host "  Summary: $csvFilePath"
  Write-Host "  Summary: $jsonFilePath"
  Write-Host "=========================================="
}
