[CmdletBinding()]
param(
  [int]$RepetitionCount = 50,
  [bool]$CleanBuild = $true,
  [int]$StepCount = 10,
  [bool]$Reference = $true,
  [bool]$IOA = $true,
  [bool]$JVM = $true,
  [bool]$JS = $true,
  [bool]$Native = $true
)

# Import common utility functions
. "$PSScriptRoot\..\utils.ps1"
. "$PSScriptRoot\..\build.ps1"

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

# Clean phase
if ($CleanBuild) {
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "# Cleaning IOA benchmark dependencies..."
  Write-Host "=========================================="

  Clean-KirHelperKit
  Clean-GameOfLifeCommonMainReference
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
$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-InstrumentationOverheadAnalyzerPlugin -CleanBuild $CleanBuild)
$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeCommonMainReference)
$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeCommonMainIoa -CleanBuild $CleanBuild)

Write-Host ""
Write-Host "=========================================="
Write-Host "# Build phase completed successfully!"
Write-Host "=========================================="

# Define build output roots to shorten executable paths
$commonMainBuildRoot = "..\..\kmp-examples\game-of-life-kmp-commonmain\build"
$commonMainIoaBuildRoot = "..\..\kmp-examples\game-of-life-kmp-commonmain-ioa\build"

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
$executables = @()

if ($Reference -and $JVM) {
  $executables += @{ Name = "commonmain_plain_jar"; Path = "$commonMainBuildRoot\lib\game-of-life-kmp-commonmain-jvm-0.0.3.jar"; Type = "jar" }
}
if ($IOA -and $JVM) {
  $executables += @{ Name = "commonmain_ioa_jar"; Path = "$commonMainIoaBuildRoot\lib\game-of-life-kmp-commonmain-ioa-jvm-0.0.1.jar"; Type = "jar" }
}
if ($Reference -and $Native) {
  $executables += @{ Name = "commonmain_plain_exe"; Path = "$commonMainBuildRoot\bin\mingwX64\releaseExecutable\game-of-life-kmp-commonmain.exe"; Type = "exe" }
}
if ($IOA -and $Native) {
  $executables += @{ Name = "commonmain_ioa_exe"; Path = "$commonMainIoaBuildRoot\bin\mingwX64\releaseExecutable\game-of-life-kmp-commonmain-ioa.exe"; Type = "exe" }
}
if ($Reference -and $JS) {
  $executables += @{ Name = "commonmain_plain_node"; Path = "$commonMainBuildRoot\js\packages\game-of-life-kmp-commonmain\kotlin\game-of-life-kmp-commonmain.js"; Type = "node" }
}
if ($IOA -and $JS) {
  $executables += @{ Name = "commonmain_ioa_node"; Path = "$commonMainIoaBuildRoot\js\packages\game-of-life-kmp-commonmain-ioa\kotlin\game-of-life-kmp-commonmain-ioa.js"; Type = "node" }
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

$missingExecutables = @()

foreach ($executable in $executables) {
  $filePath = $executable.Path
  if (Test-Path $filePath) {
    Write-Host "OK: Found: $($executable.Name) at $filePath"
  }
  else {
    Write-Host "ERROR: NOT FOUND: $($executable.Name) at $filePath"
    $missingExecutables += [ordered]@{ Name = $executable.Name; Path = $filePath }
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
$measurementDirName = "{0}_{1}" -f $measurementTimestamp, $testSuiteName
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
  $fileType = $executable.Type

  Write-Host "--------------------------------------------------------"
  Write-Host "Starting benchmark for: $($executable.Name) ($filePath)"
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
        "jar" { 
          $output = java -jar $filePath $StepCount 2>&1
          $executionSuccess = $LASTEXITCODE -eq 0 
        }
        "exe" { 
          $output = & $filePath $StepCount 2>&1
          $executionSuccess = $LASTEXITCODE -eq 0 
        }
        "node" { 
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
      RepetitionCount = $RepetitionCount
      CleanBuild      = $CleanBuild
      StepCount       = $StepCount
      Reference       = $Reference
      IOA             = $IOA
      JVM             = $JVM
      JS              = $JS
      Native          = $Native
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

  # Collect statistics for CSV
  $csvRecord = [ordered]@{
    executable                        = $executable.Name
    buildTimeMs                       = $relevantBuildTime
    RepetitionCount                   = $RepetitionCount
    CleanBuild                        = $CleanBuild
    StepCount                         = $StepCount
    Reference                         = $Reference
    IOA                               = $IOA
    JVM                               = $JVM
    JS                                = $JS
    Native                            = $Native
    CollectionTimestamp               = $machineInfo.CollectionTimestamp
    GitCommitHash                     = $machineInfo.GitCommitHash
    GitBranch                         = $machineInfo.GitBranch
    DeviceManufacturer                = $machineInfo.DeviceManufacturer
    DeviceModel                       = $machineInfo.DeviceModel
    IsVirtualMachine                  = $machineInfo.IsVirtualMachine
    OS                                = $machineInfo.OS
    OSArchitecture                    = $machineInfo.OSArchitecture
    WindowsBuildNumber                = $machineInfo.WindowsBuildNumber
    TimeZone                          = $machineInfo.TimeZone
    Username                          = $machineInfo.Username
    BIOSVersion                       = $machineInfo.BIOSVersion
    BIOSManufacturer                  = $machineInfo.BIOSManufacturer
    SecureBootEnabled                 = $machineInfo.SecureBootEnabled
    HyperVEnabled                     = $machineInfo.HyperVEnabled
    CPU                               = $machineInfo.CPU
    CPUCores                          = $machineInfo.CPUCores
    CPULogicalProcessors              = $machineInfo.CPULogicalProcessors
    CPUMaxClockSpeedMHz               = $machineInfo.CPUMaxClockSpeedMHz
    TotalRAMGB                        = $machineInfo.TotalRAMGB
    AvailableRAMGB                    = $machineInfo.AvailableRAMGB
    RAMModuleCount                    = $machineInfo.RAMModuleCount
    RAMSpeedMHz                       = $machineInfo.RAMSpeedMHz
    RAMManufacturer                   = $machineInfo.RAMManufacturer
    RAMPartNumber                     = $machineInfo.RAMPartNumber
    RAMModuleCapacitiesGB             = $machineInfo.RAMModuleCapacitiesGB
    DiskModel                         = $machineInfo.DiskModel
    DiskSizeGB                        = $machineInfo.DiskSizeGB
    DiskMediaType                     = $machineInfo.DiskMediaType
    DiskInterfaceType                 = $machineInfo.DiskInterfaceType
    SystemDriveFreeSpaceGB            = $machineInfo.SystemDriveFreeSpaceGB
    PowerPlan                         = $machineInfo.PowerPlan
    SystemUptimeHours                 = $machineInfo.SystemUptimeHours
    RunningProcessCount               = $machineInfo.RunningProcessCount
    WindowsDefenderEnabled            = $machineInfo.WindowsDefenderEnabled
    WindowsDefenderRealTimeProtection = $machineInfo.WindowsDefenderRealTimeProtection
    PowerShellVersion                 = $machineInfo.PowerShellVersion
    JavaVersion                       = $machineInfo.JavaVersion
    JavaDistribution                  = $machineInfo.JavaDistribution
    NodeVersion                       = $machineInfo.NodeVersion
    PythonVersion                     = $machineInfo.PythonVersion
    GradleVersion                     = $machineInfo.GradleVersion
    KotlinVersion                     = $machineInfo.KotlinVersion
    mean                              = $stats.mean
    median                            = $stats.median
    stddev                            = $stats.stddev
    min                               = $stats.min
    max                               = $stats.max
    ci95_lower                        = $stats.ci95.lower
    ci95_upper                        = $stats.ci95.upper
  }
  $csvRecords += $csvRecord

} # End of executables loop

# Write results files with underscore prefix to sort first alphabetically
$csvFilePath = Join-Path $measurementDir "_results.csv"

# Convert OrderedDictionaries to PSCustomObjects for proper CSV output
$csvObjects = $csvRecords | ForEach-Object { New-Object PSObject -Property $_ }
$csvObjects | ConvertTo-Csv -NoTypeInformation | Out-File -FilePath $csvFilePath -Encoding utf8

$jsonFilePath = Join-Path $measurementDir "_results.json"
$csvRecords | ConvertTo-Json | Out-File -FilePath $jsonFilePath -Encoding utf8

Write-Host "Summary results saved to $csvFilePath and $jsonFilePath"
Write-Host "All benchmarks are complete."