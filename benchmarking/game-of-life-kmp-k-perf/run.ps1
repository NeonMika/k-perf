[CmdletBinding()]
param(
  [int]$RepetitionCount = 50,
  [bool]$CleanBuild = $true,
  [int]$StepCount = 10,
  [string[]]$Filters = @("common", "dedicated", "flushEarlyFalse", "jar", "js", "native")
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

# Build phase: Compile dependencies and the application
$buildTimes = @{}
if ($CleanBuild) {
  Write-Host "=========================================="
  Write-Host "# Building k-perf benchmark dependencies..."
  Write-Host "=========================================="
  Write-Host "# Building k-perf benchmark dependencies..."
  Write-Host "=========================================="

  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-KirHelperKit -CleanBuild $True)
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-KperfPlugin -CleanBuild $True)
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeCommonMainReference -CleanBuild $True)
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeDedicatedMainReference -CleanBuild $True)
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeCommonMainKperfFlushEarlyTrue -CleanBuild $True)
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeCommonMainKperfFlushEarlyFalse -CleanBuild $False) # do not clean again, otherwise flushEarlyTrue build will be removed
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeDedicatedMainKperfFlushEarlyTrue -CleanBuild $True)
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeDedicatedMainKperfFlushEarlyFalse -CleanBuild $False) # do not clean again, otherwise flushEarlyTrue build will be removed

  Write-Host ""
  Write-Host "=========================================="
  Write-Host "# Build phase completed successfully!"
  Write-Host "=========================================="
}
else {
  Write-Host "Skipping k-perf benchmark build phase (CleanBuild = false)"
}

# Define build output roots to shorten executable paths
$commonMainBuildRoot = "..\..\kmp-examples\game-of-life-kmp-commonmain\build"
$commonMainKperfBuildRoot = "..\..\kmp-examples\game-of-life-kmp-commonmain-k-perf\build"
$dedicatedMainBuildRoot = "..\..\kmp-examples\game-of-life-kmp-dedicatedmain\build"
$dedicatedMainKperfBuildRoot = "..\..\kmp-examples\game-of-life-kmp-dedicatedmain-k-perf\build"

# Define a list of all executables to be benchmarked.
# Each item now has a 'Name' property for creating a unique and readable output file.
# Each item has 'Tags' to allow filtering by category
$executables = @(
  # Commonmain versions
  @{ Name = "commonmain-plain-jar"; Path = "$commonMainBuildRoot\lib\game-of-life-kmp-commonmain-jvm-0.0.3.jar"; Type = "jar"; Tags = @("common", "jar") },
  @{ Name = "commonmain-k-perf-flushEarlyTrue-jar"; Path = "$commonMainKperfBuildRoot\lib\game-of-life-kmp-commonmain-k-perf-jvm-0.0.3-flushEarly-true.jar"; Type = "jar"; Tags = @("common", "flushEarlyTrue", "jar") },
  @{ Name = "commonmain-k-perf-flushEarlyFalse-jar"; Path = "$commonMainKperfBuildRoot\lib\game-of-life-kmp-commonmain-k-perf-jvm-0.0.3-flushEarly-false.jar"; Type = "jar"; Tags = @("common", "flushEarlyFalse", "jar") },
  @{ Name = "commonmain-plain-exe"; Path = "$commonMainBuildRoot\bin\mingwX64\releaseExecutable\game-of-life-kmp-commonmain.exe"; Type = "exe"; Tags = @("common", "native") },
  @{ Name = "commonmain-k-perf-flushEarlyTrue-exe"; Path = "$commonMainKperfBuildRoot\bin\mingwX64\releaseExecutable\game-of-life-kmp-commonmain-k-perf-flushEarly-true.exe"; Type = "exe"; Tags = @("common", "flushEarlyTrue", "native") },
  @{ Name = "commonmain-k-perf-flushEarlyFalse-exe"; Path = "$commonMainKperfBuildRoot\bin\mingwX64\releaseExecutable\game-of-life-kmp-commonmain-k-perf-flushEarly-false.exe"; Type = "exe"; Tags = @("common", "flushEarlyFalse", "native") },
  @{ Name = "commonmain-plain-node"; Path = "$commonMainBuildRoot\js\packages\game-of-life-kmp-commonmain\kotlin\game-of-life-kmp-commonmain.js"; Type = "node"; Tags = @("common", "js") },
  @{ Name = "commonmain-k-perf-flushEarlyTrue-node"; Path = "$commonMainKperfBuildRoot\js\packages\game-of-life-kmp-commonmain-k-perf-flushEarly-true\kotlin\game-of-life-kmp-commonmain-k-perf-flushEarly-true.js"; Type = "node"; Tags = @("common", "flushEarlyTrue", "js") },
  @{ Name = "commonmain-k-perf-flushEarlyFalse-node"; Path = "$commonMainKperfBuildRoot\js\packages\game-of-life-kmp-commonmain-k-perf-flushEarly-false\kotlin\game-of-life-kmp-commonmain-k-perf-flushEarly-false.js"; Type = "node"; Tags = @("common", "flushEarlyFalse", "js") },
  # Dedicatedmain versions
  @{ Name = "dedicatedmain-plain-jar"; Path = "$dedicatedMainBuildRoot\lib\game-of-life-kmp-dedicatedmain-jvm-0.0.3.jar"; Type = "jar"; Tags = @("dedicated", "jar") },
  @{ Name = "dedicatedmain-k-perf-flushEarlyTrue-jar"; Path = "$dedicatedMainKperfBuildRoot\lib\game-of-life-kmp-dedicatedmain-k-perf-jvm-0.0.3-flushEarly-true.jar"; Type = "jar"; Tags = @("dedicated", "flushEarlyTrue", "jar") },
  @{ Name = "dedicatedmain-k-perf-flushEarlyFalse-jar"; Path = "$dedicatedMainKperfBuildRoot\lib\game-of-life-kmp-dedicatedmain-k-perf-jvm-0.0.3-flushEarly-false.jar"; Type = "jar"; Tags = @("dedicated", "flushEarlyFalse", "jar") },
  @{ Name = "dedicatedmain-plain-exe"; Path = "$dedicatedMainBuildRoot\bin\mingwX64\releaseExecutable\game-of-life-kmp-dedicatedmain.exe"; Type = "exe"; Tags = @("dedicated", "native") },
  @{ Name = "dedicatedmain-k-perf-flushEarlyTrue-exe"; Path = "$dedicatedMainKperfBuildRoot\bin\mingwX64\releaseExecutable\game-of-life-kmp-dedicatedmain-k-perf-flushEarly-true.exe"; Type = "exe"; Tags = @("dedicated", "flushEarlyTrue", "native") },
  @{ Name = "dedicatedmain-k-perf-flushEarlyFalse-exe"; Path = "$dedicatedMainKperfBuildRoot\bin\mingwX64\releaseExecutable\game-of-life-kmp-dedicatedmain-k-perf-flushEarly-false.exe"; Type = "exe"; Tags = @("dedicated", "flushEarlyFalse", "native") },
  @{ Name = "dedicatedmain-plain-node"; Path = "$dedicatedMainBuildRoot\js\packages\game-of-life-kmp-dedicatedmain\kotlin\game-of-life-kmp-dedicatedmain.js"; Type = "node"; Tags = @("dedicated", "js") },
  @{ Name = "dedicatedmain-k-perf-flushEarlyTrue-node"; Path = "$dedicatedMainKperfBuildRoot\js\packages\game-of-life-kmp-dedicatedmain-k-perf-flushEarly-true\kotlin\game-of-life-kmp-dedicatedmain-k-perf-flushEarly-true.js"; Type = "node"; Tags = @("dedicated", "flushEarlyTrue", "js") },
  @{ Name = "dedicatedmain-k-perf-flushEarlyFalse-node"; Path = "$dedicatedMainKperfBuildRoot\js\packages\game-of-life-kmp-dedicatedmain-k-perf-flushEarly-false\kotlin\game-of-life-kmp-dedicatedmain-k-perf-flushEarly-false.js"; Type = "node"; Tags = @("dedicated", "flushEarlyFalse", "js") }
)

# Create a test suite name from the executable names
$testSuiteName = "game-of-life-kmp-k-perf"

# Validate and normalize filters
if ($Filters.Count -eq 0 -or $Filters -eq @("")) {
  $Filters = @("common", "dedicated", "flushEarlyTrue", "flushEarlyFalse", "jar", "js", "native")
}

Write-Host ""
Write-Host "========================================="
Write-Host "## Filter Configuration"
Write-Host "========================================="
Write-Host "Filters: $($Filters -join ', ')"
Write-Host ""

# Filter executables based on provided filters
# An executable is selected if ALL its tags match at least one filter
$filteredExecutables = @()
foreach ($executable in $executables) {
  $allTagsMatch = $true
  foreach ($tag in $executable.Tags) {
    if ($tag -notin $Filters) {
      $allTagsMatch = $false
      break
    }
  }
  if ($allTagsMatch) {
    $filteredExecutables += $executable
  }
}

if ($filteredExecutables.Count -eq 0) {
  Write-Host "ERROR: No executables match the provided filters!"
  Write-Host "Available filter options: common, dedicated, flushEarlyTrue, flushEarlyFalse, jar, js, native"
  exit 1
}

Write-Host "Selected $($filteredExecutables.Count) executables for benchmarking:"
foreach ($exec in $filteredExecutables) {
  Write-Host "  - $($exec.Name) (Tags: $($exec.Tags -join ', '))"
}
Write-Host ""

# Use filtered executables for the rest of the script
$executables = $filteredExecutables

# Validation phase: Check if all executables exist
Write-Host ""
Write-Host "=========================================="
Write-Host "## Validating Executables..."
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

# Clean up any existing trace and symbol files
Write-Host ""
Write-Host "Cleaning up existing trace and symbol files..."
$existingTraceFiles = Get-ChildItem -Path "." -Filter "trace*.txt" -ErrorAction SilentlyContinue
$existingSymbolFiles = Get-ChildItem -Path "." -Filter "symbol*.txt" -ErrorAction SilentlyContinue
$cleanedFiles = @()

foreach ($file in $existingTraceFiles) {
  Remove-Item -Path $file.FullName -Force
  $cleanedFiles += $file.Name
}

foreach ($file in $existingSymbolFiles) {
  Remove-Item -Path $file.FullName -Force
  $cleanedFiles += $file.Name
}

if ($cleanedFiles.Count -gt 0) {
  Write-Host "Deleted: $($cleanedFiles -join ', ')"
}
else {
  Write-Host "No existing trace/symbol files found."
}
Write-Host ""

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
    Write-Host ""
    Write-Host "Running iteration $i of $RepetitionCount for $($executable.Name):"

    # Run the program and capture the output based on its type
    $output = $null
    $executionSuccess = $false
    
    try {
      switch ($fileType) {
        "jar" { 
          $output = java -jar $filePath $StepCount
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
      Write-Host "- Elapsed time not found in iteration $i"
    }

    # Process trace and symbol files generated during this iteration
    $traceFiles = Get-ChildItem -Path "." -Filter "trace*.txt" -ErrorAction SilentlyContinue
    $symbolFiles = Get-ChildItem -Path "." -Filter "symbol*.txt" -ErrorAction SilentlyContinue
    $deletedFiles = @()

    foreach ($traceFile in $traceFiles) {
      Write-Host "- Processing trace file: $($traceFile.Name)"

      # Call the graph visualizer script
      
      $graphVisualizerPath = "..\..\analyzers\call_graph_visualizer\graph-visualizer.py"
      python $graphVisualizerPath $traceFile.FullName *>&1 | Out-Null

      if ($LASTEXITCODE -eq 0) {
        # Find the generated PNG (most recently created)
        $pngFiles = Get-ChildItem -Path "." -Filter "*.png" -ErrorAction SilentlyContinue | Sort-Object -Property CreationTime -Descending | Select-Object -First 1

        if ($pngFiles) {  
          Write-Host "-- Generated call graph PNG"
        
          $pngFile = $pngFiles
          # Rename it based on executable name and iteration number
          $newPngName = "$($executable.Name)_$i.png"
          $newPngPath = Join-Path "." $newPngName
          Rename-Item -Path $pngFile.FullName -NewName $newPngName -Force

          # Copy to measurements folder
          Copy-Item -Path $newPngPath -Destination $measurementDir -Force
          Write-Host "-- Copied $newPngName to measurements folder"

          # Delete the PNG from current directory
          Remove-Item -Path $newPngPath -Force
        }
      }
      else {
        Write-Host "-- ERROR: graph-visualizer.py failed for $($traceFile.Name)"
      }

      # Delete the trace file
      Remove-Item -Path $traceFile.FullName -Force
      $deletedFiles += $traceFile.Name
    }

    # Delete any symbol files
    foreach ($symbolFile in $symbolFiles) {
      Remove-Item -Path $symbolFile.FullName -Force
      $deletedFiles += $symbolFile.Name
    }

    # Output summary of deleted files
    if ($deletedFiles.Count -gt 0) {
      Write-Host "-- Deleted: $($deletedFiles -join ', ')"
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
      Filters         = $Filters
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