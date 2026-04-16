[CmdletBinding()]
param(
  [int]$RepetitionCount = 50,
  [bool]$CleanBuild = $true,
  [int]$StepCount = 10,
  [bool]$Reference = $true,
  [bool]$IOA = $true,
  [bool]$JVM = $true,
  [bool]$JS = $true,
  [bool]$Native = $true,
  [string[]]$IoaKinds = @("None"),
  [string]$CILabel = "local"
)

# Import common utility functions
. "$PSScriptRoot\..\types.ps1"
. "$PSScriptRoot\..\statistics_utils.ps1"
. "$PSScriptRoot\..\build.ps1"

# Platform-specific native executable target and extension
$nativeTarget        = if ($IsWindows) { "mingwX64" } elseif ($IsMacOS) { "macosX64" } else { "linuxX64" }
$nativeExt           = if ($IsWindows) { ".exe" }     else               { ".kexe" }
$nativePlatformLabel = if ($IsWindows) { "win" }      elseif ($IsMacOS)  { "mac" } else { "linux" }

# Collect machine and environment information for reproducibility
Write-Host "=========================================="
Write-Host "# Collecting System Information..."
Write-Host "=========================================="

$machineInfo = Get-MachineInfo -GradleProjectPath "..\..\kmp-examples\game-of-life-kmp-commonmain"

Write-Host "System Information collected:"
foreach ($key in $machineInfo.Keys) {
  Write-Host "  $key : $($machineInfo[$key])"
}
Write-Host ""

# Validate IOA parameter arrays
if ($IoaKinds.Count -eq 0) {
  Write-Host "ERROR: -IoaKinds must contain at least one value."
  exit 1
}

# Generate combinations of IoaKind configurations
$ioaConfigurations = @()
foreach ($ioaKind in $IoaKinds) {
  $ioaConfigurations += [IoaConfig]::new($ioaKind)
}

Write-Host ""
Write-Host "=========================================="
Write-Host "# IOA Configurations to Build"
Write-Host "=========================================="
foreach ($config in $ioaConfigurations) {
  $suffix = Get-IoaSuffix -Config $config
  Write-Host "- $suffix"
}

# Clean phase
if ($CleanBuild) {
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "# Cleaning IOA benchmark dependencies..."
  Write-Host "=========================================="

  Invoke-GradleClean -Path "..\..\KIRHelperKit"                                        -Name "KIRHelperKit"
  Invoke-GradleClean -Path "..\..\plugins\instrumentation-overhead-analyzer"           -Name "instrumentation-overhead-analyzer plugin"
  Invoke-GradleClean -Path "..\..\kmp-examples\game-of-life-kmp-commonmain"            -Name "game-of-life-kmp-commonmain"
  Invoke-GradleClean -Path "..\..\kmp-examples\game-of-life-kmp-commonmain-ioa"        -Name "game-of-life-kmp-commonmain-ioa"
  Write-Host ""
}
else {
  Write-Host "Skipping clean phase (CleanBuild = false)"
  Write-Host ""
}

# Build phase: Compile dependencies and the application
$buildTimes = @{}
Write-Host ""
Write-Host "=========================================="
Write-Host "# Building IOA benchmark dependencies..."
Write-Host "=========================================="

$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-KirHelperKit)
$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-InstrumentationOverheadAnalyzerPlugin)
$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeCommonMainReference)

# Build IOA variants for all configurations
foreach ($config in $ioaConfigurations) {
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeCommonMainIoaVariant -Config $config)
}

Write-Host ""
Write-Host "=========================================="
Write-Host "# Build phase completed successfully!"
Write-Host "=========================================="

# Define project dist directories for artifacts
$commonMainDistRoot    = "..\..\kmp-examples\game-of-life-kmp-commonmain\dist"
$commonMainIoaDistRoot = "..\..\kmp-examples\game-of-life-kmp-commonmain-ioa\dist"

# Validate selection parameters
if (-not ($Reference -or $IOA)) {
  Write-Host "ERROR: At least one of -Reference or -IOA must be true."
  exit 1
}

if (-not ($JVM -or $JS -or $Native)) {
  Write-Host "ERROR: At least one of -JVM, -JS, or -Native must be true."
  exit 1
}

# Define a list of all executables to be benchmarked.
[BenchmarkExecutable[]]$executables = @()

if ($Reference -and $JVM) {
  $executables += [BenchmarkExecutable]::new("commonmain-plain-jar", "$commonMainDistRoot\game-of-life-kmp-commonmain-jvm-0.2.1.jar", [ExecutableType]::Jar, $null)
}
if ($IOA -and $JVM) {
  $executables += [BenchmarkExecutable]::new("commonmain-ioa-jar", "$commonMainIoaDistRoot\game-of-life-kmp-commonmain-ioa-jvm-0.2.1.jar", [ExecutableType]::Jar, $null)
}
if ($Reference -and $Native) {
  $executables += [BenchmarkExecutable]::new("commonmain-plain-$nativePlatformLabel-exe", "$commonMainDistRoot\game-of-life-kmp-commonmain$nativeExt", [ExecutableType]::Exe, $null)
}
if ($IOA -and $Native) {
  $executables += [BenchmarkExecutable]::new("commonmain-ioa-$nativePlatformLabel-exe", "$commonMainIoaDistRoot\game-of-life-kmp-commonmain-ioa$nativeExt", [ExecutableType]::Exe, $null)
}
if ($Reference -and $JS) {
  $executables += [BenchmarkExecutable]::new("commonmain-plain-node", "$commonMainDistRoot\game-of-life-kmp-commonmain.js", [ExecutableType]::Node, $null)
}
if ($IOA -and $JS) {
  $executables += [BenchmarkExecutable]::new("commonmain-ioa-node", "$commonMainIoaDistRoot\game-of-life-kmp-commonmain-ioa.js", [ExecutableType]::Node, $null)
}
if ($IOA) {
    foreach ($config in $ioaConfigurations) {
        $suffix = Get-IoaSuffix -Config $config
        if ($JVM) {
            $executables += [BenchmarkExecutable]::new("commonmmain-ioa-$suffix-jar", "$commonMainIoaDistRoot\game-of-life-kmp-commonmain-ioa-jvm-0.1.0-$suffix.jar", [ExecutableType]::Jar, $null)
        }
        if ($JS) {
            $executables += [BenchmarkExecutable]::new("commonmmain-ioa-$suffix-node", "$commonMainDistRoot\game-of-life-kmp-commonmain-ioa-$suffix.js", [ExecutableType]::Jar, $null)
        }
        if ($JVM) {
            $executables += [BenchmarkExecutable]::new("commonmmain-ioa-$suffix-exe", "$commonMainIoaDistRoot\game-of-life-kmp-commonmain-ioa-$suffix.exe", [ExecutableType]::Exe, $null)
        }
    }
}

# Create a test suite name from the executable names
$testSuiteName = "game-of-life-kmp-commonmain-ioa"

if ($executables.Count -eq 0) {
  Write-Host "ERROR: No executables match the provided parameters."
  exit 1
}

Write-Host "Selected $($executables.Count) executables for benchmarking:"
foreach ($exec in $executables) {
  Write-Host "  - $($exec.Name)"
}
Write-Host ""
Write-Host "=========================================="
Write-Host "Validating executables..."
Write-Host "=========================================="

[BenchmarkExecutable[]]$missingExecutables = @()

foreach ($executable in $executables) {
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

# Create measurement directory with timestamp and test suite name
$measurementTimestamp = Get-Date -Format "yyyy_MM_dd_HH_mm_ss"
$measurementDirName = "{0}_{1}_{2}_{3}reps_{4}steps" -f $measurementTimestamp, $testSuiteName, $CILabel, $RepetitionCount, $StepCount
$measurementDir = Join-Path "..\..\measurements" $measurementDirName

# Check if measurement directory already exists
if (Test-Path $measurementDir) {
  Write-Host ""
  Write-Host "ERROR: Measurement directory already exists: $measurementDir"
  Write-Host "Please try again in a moment to get a different timestamp."
  exit 1
}

# Create the measurement directory
New-Item -ItemType Directory -Path $measurementDir -Force | Out-Null

# Initialize array to collect CSV results
$csvRecords = @()

# Loop through each executable defined above
foreach ($executable in $executables) {
  $filePath = $executable.Path
  if (-not $IsWindows) { $filePath = $filePath -replace '\\', '/' }
  $fileType = $executable.Type
  Write-Host "--------------------------------------------------------"

  # Initialize an array to hold the elapsed times for the current file
  $elapsedTimes = @()

  # Run the loop for the specified number of repetitions
  for ($i = 1; $i -le $RepetitionCount; $i++) {
    Write-Host "Running iteration $i of $RepetitionCount for $($executable.Name):"

    # Run the program and capture the output based on its type
    $output = $null
    $executionSuccess = $false
    
    try {
      switch ($fileType) {
        ([ExecutableType]::Jar) {
          $output = java -jar $filePath $StepCount 2>&1
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

    # Find the line that starts with "### Elapsed time: "
    $elapsedLine = $output | Select-String "^### Elapsed time: "

    if ($elapsedLine) {
      # Extract the number after "### Elapsed time: "
      $elapsedTime = $elapsedLine -replace "### Elapsed time:\s*", ""

      # Trim any whitespace
      $elapsedTime = $elapsedTime.Trim()

      # Print the result for the current iteration
      Write-Host ("- Ran {0:N3} ms" -f ([long]$elapsedTime / 1000))

      # Add to the array
      $elapsedTimes += $elapsedTime
    }
    else {
      Write-Host "Elapsed time not found in iteration $i"
    }
  } # End of iteration loop

  # Generate the output file path: measurements/YYYY_MM_DD_HH_MM_<test-name>/<executable>.json
  $outputFileName = "{0}.json" -f $executable.Name
  $outputFilePath = Join-Path $measurementDir $outputFileName

  # Prepare JSON output with statistics
  $numericTimes = $elapsedTimes | ForEach-Object { [double]$_ }
  $stats = Get-BenchmarkStatistics -Values $numericTimes
  
  # Get relevant build time for this executable
  $relevantBuildTime = if ($buildTimes.Contains($executable.Name)) { $buildTimes[$executable.Name] } else { $null }
  
  $payload = [ordered]@{
    parameters  = [ordered]@{
      CILabel         = $CILabel
      RepetitionCount = $RepetitionCount
      CleanBuild      = $CleanBuild
      StepCount       = $StepCount
      Reference       = $Reference
      IOA             = $IOA
      JVM             = $JVM
      JS              = $JS
      Native          = $Native
      IoaKinds        = $IoaKinds
    }
    machineInfo = $machineInfo
    buildTimeMs = $relevantBuildTime
    executable  = $executable.Name
    repetitions = $RepetitionCount
    times       = $numericTimes
    statistics  = $stats
  }

  # Write JSON to the target file
  $payload | ConvertTo-Json -Depth 6 | Out-File -FilePath $outputFilePath -Encoding utf8

  Write-Host "All elapsed times for this run have been collected and saved to $outputFilePath"

  # Build CSV record using utility function
  $csvRecord = Build-BenchmarkCSVRecord `
    -ExecutableName $executable.Name `
    -Statistics $stats `
    -MachineInfo $machineInfo `
    -RepetitionCount $RepetitionCount `
    -CleanBuild $CleanBuild `
    -StepCount $StepCount `
    -BuildTime $relevantBuildTime `
    -AdditionalParameters @{
      CILabel    = $CILabel
      Reference = $Reference
      IOA       = $IOA
      JVM       = $JVM
      JS        = $JS
      Native    = $Native
    }
  $csvRecords += $csvRecord

} # End of executables loop

# Write results files with underscore prefix to sort first alphabetically
$csvFilePath = Join-Path $measurementDir "_results.csv"
$jsonFilePath = Join-Path $measurementDir "_results.json"

Export-BenchmarkResultsToCSV -Results $csvRecords -OutputPath $csvFilePath
Export-BenchmarkResultsToJSON -Results $csvRecords -OutputPath $jsonFilePath

Write-Host "Summary results saved to $csvFilePath and $jsonFilePath"
Write-Host "All benchmarks are complete."
exit 0