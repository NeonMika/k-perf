[CmdletBinding()]
param(
  [int]$RepetitionCount = 50,
  [bool]$CleanBuild = $false,
  [int]$StepCount = 500,
  [bool]$Reference = $true,
  [bool]$Common = $true,
  [bool]$Dedicated = $false,
  [bool]$JVM = $true,
  [bool]$JS = $true,
  [bool]$Native = $true,
  [bool[]]$FlushEarly = @($false),
  [bool[]]$InstrumentPropertyAccessors = @($false),
  [bool[]]$TestKIR = @($false),
  [string[]]$Methods = @(".*"),
  [string]$CILabel = "local"
)

# Import common utility functions
. "$PSScriptRoot\..\utils.ps1"
. "$PSScriptRoot\..\build.ps1"

# Platform-specific native executable target and extension
$nativeTarget = if ($IsWindows) { "mingwX64" } elseif ($IsMacOS) { "macosX64" } else { "linuxX64" }
$nativeExt    = if ($IsWindows) { ".exe" }     else                { ".kexe" }

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

# Define k-perf settings combinations to build based on parameters
if (-not ($Common -or $Dedicated)) {
  Write-Host "ERROR: At least one of -Common or -Dedicated must be true."
  exit 1
}

if (-not ($JVM -or $JS -or $Native)) {
  Write-Host "ERROR: At least one of -JVM, -JS, or -Native must be true."
  exit 1
}

# Validate k-perf parameter arrays
if ($FlushEarly.Count -eq 0) {
  Write-Host "ERROR: -FlushEarly must contain at least one boolean value."
  exit 1
}

if ($InstrumentPropertyAccessors.Count -eq 0) {
  Write-Host "ERROR: -InstrumentPropertyAccessors must contain at least one boolean value."
  exit 1
}

if ($TestKIR.Count -eq 0) {
  Write-Host "ERROR: -TestKIR must contain at least one boolean value."
  exit 1
}

if ($Methods.Count -eq 0) {
  Write-Host "ERROR: -Methods must contain at least one regex string value."
  exit 1
}

# Generate Cartesian product of k-perf combinations
$kPerfCombinations = @()
foreach ($flushEarlyValue in $FlushEarly) {
  foreach ($propAccessorsValue in $InstrumentPropertyAccessors) {
    foreach ($testKIRValue in $TestKIR) {
      foreach ($methodsValue in $Methods) {
        $kPerfCombinations += [KPerfConfig]::new(
          [bool]$flushEarlyValue,
          [bool]$propAccessorsValue,
          [bool]$testKIRValue,
          [string]$methodsValue
        )
      }
    }
  }
}

Write-Host ""
Write-Host "=========================================="
Write-Host "# K-perf Configurations to Build"
Write-Host "=========================================="
foreach ($config in $kPerfCombinations) {
  $suffix = Get-KPerfSuffix -Config $config
  Write-Host "- $suffix"
}

# Clean phase
if ($CleanBuild) {
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "# Cleaning k-perf benchmark dependencies..."
  Write-Host "=========================================="

  Clean-KirHelperKit
  Clean-KPerfPlugin
  Clean-GameOfLifeCommonMainReference
  Clean-GameOfLifeDedicatedMainReference
  Clean-GameOfLifeCommonKPerfVariant
  Clean-GameOfLifeDedicatedKPerfVariant
  Write-Host ""
}
else {
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "# Skipping clean phase (CleanBuild = false)..."
  Write-Host "=========================================="
  Write-Host ""
}

# Build phase: Compile dependencies and the application
$buildTimes = @{}
Write-Host ""
Write-Host "=========================================="
Write-Host "# Building k-perf benchmark dependencies..."
Write-Host "=========================================="

$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-KirHelperKit)
$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-KPerfPlugin)
$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeCommonMainReference)
$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeDedicatedMainReference)

# Build k-perf variants for all combinations
foreach ($config in $kPerfCombinations) {
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeKPerfVariant -GameType ([GameType]::CommonMain) -Config $config)
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeKPerfVariant -GameType ([GameType]::DedicatedMain) -Config $config)
}

Write-Host ""
Write-Host "=========================================="
Write-Host "# Build phase completed successfully!"
Write-Host "=========================================="

# Define project roots to locate artifacts in dist/ and bin/<suffix>/
$commonMainProjectRoot      = "..\..\kmp-examples\game-of-life-kmp-commonmain"
$commonMainKPerfProjectRoot = "..\..\kmp-examples\game-of-life-kmp-commonmain-k-perf"
$dedicatedMainProjectRoot      = "..\..\kmp-examples\game-of-life-kmp-dedicatedmain"
$dedicatedMainKPerfProjectRoot = "..\..\kmp-examples\game-of-life-kmp-dedicatedmain-k-perf"

# Function to get executables
function Get-Executables {
  param(
    [GameType]$GameType
  )
  
  $executables = @()
  $plainProjectRoot = if ($GameType -eq [GameType]::CommonMain) { $commonMainProjectRoot } else { $dedicatedMainProjectRoot }
  $kPerfProjectRoot = if ($GameType -eq [GameType]::CommonMain) { $commonMainKPerfProjectRoot } else { $dedicatedMainKPerfProjectRoot }
  $projectName = if ($GameType -eq [GameType]::CommonMain) { "game-of-life-kmp-commonmain" } else { "game-of-life-kmp-dedicatedmain" }
  $kPerfProjectName = if ($GameType -eq [GameType]::CommonMain) { "game-of-life-kmp-commonmain-k-perf" } else { "game-of-life-kmp-dedicatedmain-k-perf" }
  
  # Add reference (plain) versions if requested — artifacts are in dist/
  if ($Reference -and $JVM) {
    $executables += @{
      Name   = "$GameType-plain-jar"
      Path   = "$plainProjectRoot\dist\$projectName-jvm-0.2.1.jar"
      Type   = "jar"
      Config = $null
    }
  }
  
  if ($Reference -and $JS) {
    $executables += @{
      Name   = "$GameType-plain-node"
      Path   = "$plainProjectRoot\dist\$projectName.js"
      Type   = "node"
      Config = $null
    }
  }
  
  if ($Reference -and $Native) {
    $executables += @{
      Name   = "$GameType-plain-exe"
      Path   = "$plainProjectRoot\dist\$projectName$nativeExt"
      Type   = "exe"
      Config = $null
    }
  }
  
  # Add k-perf variants — artifacts are in bin/<suffix>/
  if ($JVM) {
    foreach ($config in $kPerfCombinations) {
      $suffix = Get-KPerfSuffix -Config $config
      
      $executables += @{
        Name   = "$GameType-k-perf-$suffix-jar"
        Path   = "$kPerfProjectRoot\bin\$suffix\$kPerfProjectName-jvm-0.2.1.jar"
        Type   = "jar"
        Config = $config
      }
    }
  }
  
  if ($JS) {
    foreach ($config in $kPerfCombinations) {
      $suffix = Get-KPerfSuffix -Config $config
      
      $executables += @{
        Name   = "$GameType-k-perf-$suffix-node"
        Path   = "$kPerfProjectRoot\bin\$suffix\$kPerfProjectName.js"
        Type   = "node"
        Config = $config
      }
    }
  }
  
  if ($Native) {
    foreach ($config in $kPerfCombinations) {
      $suffix = Get-KPerfSuffix -Config $config
      
      $executables += @{
        Name   = "$GameType-k-perf-$suffix-exe"
        Path   = "$kPerfProjectRoot\bin\$suffix\$kPerfProjectName$nativeExt"
        Type   = "exe"
        Config = $config
      }
    }
  }
  
  return $executables
}

# Get executables matching the specified parameters
$executables = @()
if ($Common) {
  $executables += Get-Executables -GameType ([GameType]::CommonMain)
}
if ($Dedicated) {
  $executables += Get-Executables -GameType ([GameType]::DedicatedMain)
}

if ($executables.Count -eq 0) {
  Write-Host "ERROR: No executables match the provided parameters."
  exit 1
}

Write-Host ""
Write-Host "Selected $($executables.Count) executables for benchmarking:"
foreach ($exec in $executables) {
  Write-Host "  - $($exec.Name)"
}
Write-Host ""

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
$measurementDirName = "{0}_game-of-life-kmp-k-perf_{1}_{2}reps_{3}steps" -f $measurementTimestamp, $CILabel, $RepetitionCount, $StepCount
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
  if (-not $IsWindows) { $filePath = $filePath -replace '\\', '/' }
  $fileType = $executable.Type
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

      # Call the graph visualizer script (silently skip if Python/matplotlib unavailable)
      try {
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
          Write-Host "-- Skipped call graph PNG (graph-visualizer.py returned non-zero)"
        }
      }
      catch {
        Write-Host "-- Skipped call graph PNG (python not available or error: $_)"
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
      CILabel                     = $CILabel
      RepetitionCount             = $RepetitionCount
      CleanBuild                  = $CleanBuild
      StepCount                   = $StepCount
      Reference                   = $Reference
      Common                      = $Common
      Dedicated                   = $Dedicated
      JVM                         = $JVM
      JS                          = $JS
      Native                      = $Native
      FlushEarly                  = $FlushEarly
      InstrumentPropertyAccessors = $InstrumentPropertyAccessors
      TestKIR                     = $TestKIR
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
      CILabel                         = $CILabel
       Reference                       = $Reference
      Common                          = $Common
      Dedicated                       = $Dedicated
      JVM                             = $JVM
      JS                              = $JS
      Native                          = $Native
      FlushEarly                      = $FlushEarly
      InstrumentPropertyAccessors     = $InstrumentPropertyAccessors
      TestKIR                         = $TestKIR
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