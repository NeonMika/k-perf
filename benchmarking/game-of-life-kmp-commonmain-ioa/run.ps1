[CmdletBinding()]
param(
  [int]$RepetitionCount = 50,
  [bool]$CleanBuild = $true,
  [int]$StepCount = 10,
  [string[]]$Filters = @("common", "jar", "js", "native")
)

# Import common utility functions
. "$PSScriptRoot\..\utils.ps1"
. "$PSScriptRoot\..\build.ps1"

# Build phase: Compile dependencies and the application
$buildTimes = @{}
if ($CleanBuild) {
  Write-Host "=========================================="
  Write-Host "# Building IOA benchmark dependencies..."
  Write-Host "=========================================="

  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-KirHelperKit -CleanBuild $CleanBuild)
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-InstrumentationOverheadAnalyzerPlugin -CleanBuild $CleanBuild)
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeCommonMainReference -CleanBuild $CleanBuild)
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeCommonMainIoa -CleanBuild $CleanBuild)

  Write-Host ""
  Write-Host "=========================================="
  Write-Host "# Build phase completed successfully!"
  Write-Host "=========================================="
}
else {
  Write-Host "Skipping build phase (CleanBuild = false)"
}

# Define build output roots to shorten executable paths
$commonMainBuildRoot = "..\..\kmp-examples\game-of-life-kmp-commonmain\build"
$commonMainIoaBuildRoot = "..\..\kmp-examples\game-of-life-kmp-commonmain-ioa\build"

# Define a list of all executables to be benchmarked.
# Each item now has a 'Name' property for creating a unique and readable output file.
# Each item has 'Tags' to allow filtering by category
$executables = @(
  # Commonmain versions
  @{ Name = "commonmain_plain_jar"; Path = "$commonMainBuildRoot\lib\game-of-life-kmp-commonmain-jvm-0.0.3.jar"; Type = "jar"; Tags = @("common", "jar") },
  @{ Name = "commonmain_ioa_jar"; Path = "$commonMainIoaBuildRoot\lib\game-of-life-kmp-commonmain-ioa-jvm-0.0.1.jar"; Type = "jar"; Tags = @("common", "jar") },
  @{ Name = "commonmain_plain_exe"; Path = "$commonMainBuildRoot\bin\mingwX64\releaseExecutable\game-of-life-kmp-commonmain.exe"; Type = "exe"; Tags = @("common", "native") },
  @{ Name = "commonmain_ioa_exe"; Path = "$commonMainIoaBuildRoot\bin\mingwX64\releaseExecutable\game-of-life-kmp-commonmain-ioa.exe"; Type = "exe"; Tags = @("common", "native") },
  @{ Name = "commonmain_plain_node"; Path = "$commonMainBuildRoot\js\packages\game-of-life-kmp-commonmain\kotlin\game-of-life-kmp-commonmain.js"; Type = "node"; Tags = @("common", "js") },
  @{ Name = "commonmain_ioa_node"; Path = "$commonMainIoaBuildRoot\js\packages\game-of-life-kmp-commonmain-ioa\kotlin\game-of-life-kmp-commonmain-ioa.js"; Type = "node"; Tags = @("common", "js") }
)

# Create a test suite name from the executable names
$testSuiteName = "game-of-life-kmp-commonmain-ioa"

# Validate and normalize filters
if ($Filters.Count -eq 0 -or $Filters -eq @("")) {
  $Filters = @("common", "jar", "js", "native")
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
  Write-Host "Available filter options: common, jar, js, native"
  exit 1
}

Write-Host "Selected $($filteredExecutables.Count) executables for benchmarking:"
foreach ($exec in $filteredExecutables) {
  Write-Host "  - $($exec.Name) (Tags: $($exec.Tags -join ', '))"
}
Write-Host ""

# Use filtered executables for the rest of the script
$executables = $filteredExecutables
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
      Write-Host "$elapsedTime"

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
      Filters         = $Filters
    }
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
    executable  = $executable.Name
    repetitions = $RepetitionCount
    mean        = $stats.mean
    median      = $stats.median
    stddev      = $stats.stddev
    min         = $stats.min
    max         = $stats.max
    ci95_lower  = $stats.ci95.lower
    ci95_upper  = $stats.ci95.upper
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