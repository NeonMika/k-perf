param(
  [bool]$CleanBuild = $true,
  [int]$WarmupCount = 5,
  [int]$RunCount = 20
)

$ErrorActionPreference = "Stop"

$ScriptRoot = $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($ScriptRoot)) { $ScriptRoot = '.' }

. "$ScriptRoot\utils.ps1"

Push-Location "$ScriptRoot\.."



function Invoke-GradleBuild {
  param(
    [string]$Title,
    [string]$Path,
    [string[]]$Tasks,
    [bool]$SkipClean = $false
  )

  Write-Host ""
  Write-Host "=========================================="
  Write-Host "Building: $Title"
  Write-Host "Path: $Path"
  Write-Host "Tasks: $($Tasks -join ' ')"
  Write-Host "=========================================="

  Push-Location $Path
  try {
    if ($CleanBuild -and -not $SkipClean) {
      & .\gradlew clean @Tasks
    }
    else {
      & .\gradlew @Tasks
    }
    
    if ($LASTEXITCODE -ne 0) {
      throw "Failed to build $Title"
    }
  }
  finally {
    Pop-Location
  }

  Write-Host "$Title built successfully."
}

Write-Host "=========================================="
Write-Host "Compiling Required Plugins and Dependencies"
Write-Host "=========================================="

Invoke-GradleBuild -Title "KIRHelperKit" -Path ".\KIRHelperKit" -Tasks @("publishToMavenLocal")
Invoke-GradleBuild -Title "k-perf plugin" -Path ".\plugins\k-perf" -Tasks @("publishToMavenLocal")

Invoke-GradleBuild -Title "OTel OTLP Exporter" -Path ".\otlp-exporter" -Tasks @("publishToMavenLocal")
Invoke-GradleBuild -Title "OTel Plugin Util" -Path ".\plugins\otel-plugin\util" -Tasks @("publishToMavenLocal")
Invoke-GradleBuild -Title "OTel Plugin" -Path ".\plugins\otel-plugin\plugin" -Tasks @("publishToMavenLocal")

Write-Host "=========================================="
Write-Host "Compiling Comparison Projects"
Write-Host "=========================================="

# Build projects for all targets (excluding native for now)
Invoke-GradleBuild -Title "Comparison Project (k-perf)" -Path ".\kmp-examples\comparison-k-perf" -Tasks @("jvmJar", "jsProductionExecutableCompileSync") -SkipClean $true
Invoke-GradleBuild -Title "Comparison Project (otel)" -Path ".\kmp-examples\comparison-otel" -Tasks @("jvmJar", "jsProductionExecutableCompileSync") -SkipClean $true


# Path resolving for execution
$kperfJvm = "java -jar .\kmp-examples\comparison-k-perf\build\lib\comparison-k-perf-jvm-0.1.0-flushEarly-true.jar"
$kperfJs = "node .\kmp-examples\comparison-k-perf\build\js\packages\comparison-k-perf-flushEarly-true\kotlin\comparison-k-perf-flushEarly-true.js"

$otelJvm = "java -jar .\kmp-examples\comparison-otel\build\lib\comparison-otel-jvm-1.0.0.jar"
$otelJs = "cd .\kmp-examples\comparison-otel && .\gradlew jsNodeProductionRun -q"

$executables = @(
  @{ Name = "k-perf JVM"; Command = $kperfJvm },
  @{ Name = "otel JVM"; Command = $otelJvm },
  @{ Name = "k-perf JS (Node)"; Command = $kperfJs },
  @{ Name = "otel JS (Node)"; Command = $otelJs }
)

Write-Host "=========================================="
Write-Host "Running Measurements"
Write-Host "Warmups: $WarmupCount | Runs: $RunCount"
Write-Host "=========================================="

$allResults = @()

foreach ($exe in $executables) {
  Write-Host ""
  
  # up Docker resources directly when OTel trace collection tests.
  if ($exe.Name -match "otel") {
    Write-Host "--- Booting Jaeger Collector via Docker ---"
    try {
      docker start jaeger 2>&1 | Out-Null
      if ($LASTEXITCODE -ne 0) {
        docker run -d --name jaeger -e COLLECTOR_OTLP_ENABLED=true -p 16686:16686 -p 4317:4317 -p 4318:4318 -e SPAN_STORAGE_TYPE=badger -e BADGER_EPHEMERAL=false -e BADGER_DIRECTORY=/badger -v jaeger-data:/badger jaegertracing/all-in-one:latest 2>&1 | Out-Null
      }
    }
    catch {}
    Start-Sleep -Seconds 2
  }
  else {
    Write-Host "--- Stopping active Jaeger containers to prevent caching/CPU interference for baseline metrics ---"
    try {
      docker stop jaeger 2>&1 | Out-Null
    }
    catch {}
  }

  Write-Host "--- Benchmarking: $($exe.Name) ---"
  
  # Warmup
  Write-Host "Warmup iterations ($WarmupCount):"
  for ($i = 0; $i -lt $WarmupCount; $i++) {
    $output = cmd.exe /c "$($exe.Command) 2>&1"
    
    # Delete k-perf trace/symbol output files automatically
    Get-ChildItem -Path "." -Filter "trace*.txt" -ErrorAction SilentlyContinue | Remove-Item -Force
    Get-ChildItem -Path "." -Filter "symbol*.txt" -ErrorAction SilentlyContinue | Remove-Item -Force

    $outputStr = $output -join "`n"
    $flushTime = [regex]::Match($outputStr, "Flush finished - (\d+) ms elapsed")
    $execTime = [regex]::Match($outputStr, "Execution finished - (\d+) ms elapsed")
    
    if ($flushTime.Success) {
      Write-Host "  Warmup $($i+1): $($flushTime.Groups[1].Value) ms (Included async flush)"
    }
    elseif ($execTime.Success) {
      Write-Host "  Warmup $($i+1): $($execTime.Groups[1].Value) ms"
    }
    else {
      Write-Host "  Warmup $($i+1): Failed to parse time"
    }
  }

  # Actual measurements
  $runtimes = @()
  Write-Host "Measurement iterations ($RunCount):"
  for ($i = 0; $i -lt $RunCount; $i++) {
    $output = cmd.exe /c "$($exe.Command) 2>&1"
    
    # Delete k-perf trace/symbol output files automatically
    Get-ChildItem -Path "." -Filter "trace*.txt" -ErrorAction SilentlyContinue | Remove-Item -Force
    Get-ChildItem -Path "." -Filter "symbol*.txt" -ErrorAction SilentlyContinue | Remove-Item -Force

    $outputStr = $output -join "`n"
    $flushTime = [regex]::Match($outputStr, "Flush finished - (\d+) ms elapsed")
    $execTime = [regex]::Match($outputStr, "Execution finished - (\d+) ms elapsed")
    
    if ($flushTime.Success) {
      $ms = [int]$flushTime.Groups[1].Value
      Write-Host "  Run $($i+1): $ms ms (Included async flush)" -ForegroundColor Green
      $runtimes += $ms
    }
    elseif ($execTime.Success) {
      $ms = [int]$execTime.Groups[1].Value
      Write-Host "  Run $($i+1): $ms ms" -ForegroundColor Green
      $runtimes += $ms
    }
    else {
      Write-Host "  Run $($i+1): Failed to parse time" -ForegroundColor Red
    }
  }

  # Statistical computations
  $numericTimes = $runtimes | ForEach-Object { [double]$_ }
  $stats = Get-BenchmarkStatistics -Values $numericTimes

  $allResults += [ordered]@{
    Executable = $exe.Name
    Count      = $stats.count
    Mean       = $stats.mean
    Median     = $stats.median
    StdDev     = $stats.stddev
    Min        = $stats.min
    Max        = $stats.max
    CI95Upper  = $stats.ci95.upper
    Times      = $numericTimes
  }
}

Write-Host ""
Write-Host "=========================================="
Write-Host "Processing Results & System Info"
Write-Host "=========================================="

$machineInfo = Get-MachineInfo -GradleProjectPath ".\kmp-examples\comparison-k-perf"

$timestamp = Get-Date -Format "yyyy_MM_dd_HH_mm_ss"
$resultsDir = ".\measurements\comparison_run_$timestamp"
if (-Not (Test-Path $resultsDir)) {
  New-Item -ItemType Directory -Path $resultsDir -Force | Out-Null
}

$jsonOutput = [ordered]@{
  Parameters  = @{ WarmupCount = $WarmupCount; RunCount = $RunCount; CleanBuild = $CleanBuild }
  MachineInfo = $machineInfo
  Results     = $allResults
}

$jsonFile = "$resultsDir\results.json"
$mdFile = "$resultsDir\results.md"

$jsonOutput | ConvertTo-Json -Depth 6 | Out-File $jsonFile -Encoding utf8

# Generate Markdown file
$markdown = @"
# Benchmark Results ($timestamp)

## Parameters
- **Warmup Iterations:** $WarmupCount
- **Run Iterations:** $RunCount
- **Clean Build:** $CleanBuild

## System Information
- **OS:** $($machineInfo.OS) $($machineInfo.OSArchitecture)
- **CPU:** $($machineInfo.CPU) ($($machineInfo.CPUCores) Cores / $($machineInfo.CPULogicalProcessors) Logical Processors)
- **RAM:** $($machineInfo.TotalRAMGB) GB
- **Java Version:** $($machineInfo.JavaVersion) ($($machineInfo.JavaDistribution))
- **Node Version:** $($machineInfo.NodeVersion)

## Hardware Overview Details
- **Device:** $($machineInfo.DeviceManufacturer) - $($machineInfo.DeviceModel)
- **Git Branch:** $($machineInfo.GitBranch)

## Execution Summary

| Executable | Iterations | Mean (ms) | Median (ms) | Min (ms) | Max (ms) | StdDev |
|------------|------------|-----------|-------------|----------|----------|--------|
"@

foreach ($res in $allResults) {
  if ($null -ne $res.Mean) { $meanStr = "{0:N2}" -f $res.Mean } else { $meanStr = "N/A" }
  if ($null -ne $res.Median) { $medStr = "{0:N2}" -f $res.Median } else { $medStr = "N/A" }
  if ($null -ne $res.Min) { $minStr = "{0:N0}" -f $res.Min } else { $minStr = "N/A" }
  if ($null -ne $res.Max) { $maxStr = "{0:N0}" -f $res.Max } else { $maxStr = "N/A" }
  if ($null -ne $res.StdDev) { $stdStr = "{0:N2}" -f $res.StdDev } else { $stdStr = "N/A" }

  $markdown += "`n| $($res.Executable) | $($res.Count) | {0} | {1} | {2} | {3} | {4} |" -f $meanStr, $medStr, $minStr, $maxStr, $stdStr
}

$markdown | Out-File $mdFile -Encoding utf8

Write-Host "Measurements and stats saved successfully to folder: `n -> $resultsDir"
Write-Host "Benchmark evaluation finished."
Pop-Location