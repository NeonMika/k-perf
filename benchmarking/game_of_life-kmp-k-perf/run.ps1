param(
  [int]$RepetitionCount = 10,
  [bool]$CleanBuild = $true
)

function Get-BenchmarkStatistics {
  param(
    [double[]]$Values
  )

  $count = $Values.Count
  if ($count -eq 0) {
    return [ordered]@{ count = 0; mean = $null; median = $null; stddev = $null; min = $null; max = $null; ci95 = $null }
  }

  $sorted = $Values | Sort-Object
  $sum = ($Values | Measure-Object -Sum).Sum
  $mean = $sum / $count

  # Median calculation
  if ($count % 2 -eq 1) {
    $median = $sorted[([int]($count / 2))]
  }
  else {
    $mid = $count / 2
    $median = ($sorted[$mid - 1] + $sorted[$mid]) / 2
  }

  $min = $sorted[0]
  $max = $sorted[$count - 1]

  if ($count -gt 1) {
    $varianceSum = 0.0
    foreach ($v in $Values) {
      $varianceSum += [math]::Pow(($v - $mean), 2)
    }
    $variance = $varianceSum / ($count - 1)
    $stddev = [math]::Sqrt($variance)
    $stderr = $stddev / [math]::Sqrt($count)
    
    # --- FIX STARTS HERE ---
    # T-Distribution Critical Values (Two-tailed, alpha=0.05)
    # Lookup table for degrees of freedom (df = count - 1)
    $tValues = @{
      1 = 12.71; 2 = 4.30; 3 = 3.18; 4 = 2.78; 5 = 2.57;
      6 = 2.45; 7 = 2.36; 8 = 2.31; 9 = 2.26; 10 = 2.23;
      11 = 2.20; 12 = 2.18; 13 = 2.16; 14 = 2.14; 15 = 2.13;
      16 = 2.12; 17 = 2.11; 18 = 2.10; 19 = 2.09; 20 = 2.09;
      21 = 2.08; 22 = 2.07; 23 = 2.07; 24 = 2.06; 25 = 2.06;
      26 = 2.06; 27 = 2.05; 28 = 2.05; 29 = 2.05
    }

    $df = $count - 1
    if ($tValues.ContainsKey($df)) {
      $tScore = $tValues[$df]
    }
    else {
      # Fallback to Z-score (1.96) for N > 30, which is statistically acceptable
      $tScore = 1.96 
    }

    $ciHalfWidth = $tScore * $stderr
    # --- FIX ENDS HERE ---

    $ci95 = [ordered]@{
      lower = $mean - $ciHalfWidth
      upper = $mean + $ciHalfWidth
    }
  }
  else {
    $stddev = 0.0
    $ci95 = [ordered]@{
      lower = $mean
      upper = $mean
    }
  }

  return [ordered]@{
    count  = $count
    mean   = $mean
    median = $median
    stddev = $stddev
    min    = $min
    max    = $max
    ci95   = $ci95
  }
}

# Build phase: Compile dependencies and the application
if ($CleanBuild) {
  Write-Host "=========================================="
  Write-Host "Starting build phase..."
  Write-Host "=========================================="

  # Build KIRHelperKit and publish to local maven
  Write-Host ""
  Write-Host "Building KIRHelperKit..."
  $kirHelperKitPath = "..\..\KIRHelperKit"
  Push-Location $kirHelperKitPath
  & .\gradlew clean build publishToMavenLocal
  if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: KIRHelperKit build failed!"
    exit 1
  }
  Pop-Location
  Write-Host "KIRHelperKit build completed successfully."

  # Build k-perf (Kotlin compiler plugin) and publish to local maven
  Write-Host ""
  Write-Host "Building k-perf (Kotlin compiler plugin)..."
  $kPerfPath = "..\..\plugins\k-perf"
  Push-Location $kPerfPath
  & .\gradlew clean build publishToMavenLocal
  if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: k-perf build failed!"
    exit 1
  }
  Pop-Location
  Write-Host "k-perf build completed successfully."

  # Build the reference application (without tracing)
  Write-Host ""
  Write-Host "Building game-of-life-kmp reference application (without plugin)..."
  $refAppPath = "..\..\kmp-examples\game-of-life-kmp"
  Push-Location $refAppPath
  & .\gradlew clean build
  if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: game-of-life-kmp reference application build failed!"
    exit 1
  }
  Pop-Location
  Write-Host "game-of-life-kmp reference application build completed successfully."

  # Build the client application (with k-perf tracing)
  Write-Host ""
  Write-Host "Building game-of-life-kmp-k-perf application..."
  $kmpExamplePath = "..\..\kmp-examples\game-of-life-kmp-k-perf"
  Push-Location $kmpExamplePath
  & .\gradlew clean build
  if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: game-of-life-kmp-k-perf build failed!"
    exit 1
  }
  Pop-Location
  Write-Host "game-of-life-kmp-k-perf build completed successfully."


  Write-Host ""
  Write-Host "=========================================="
  Write-Host "Build phase completed successfully!"
  Write-Host "=========================================="
}
else {
  Write-Host "Skipping build phase (CleanBuild = false)"
}

# Define a list of all executables to be benchmarked.
# Each item now has a 'Name' property for creating a unique and readable output file.
$executables = @(
  @{ Name = "plain_jar"; Path = "..\..\kmp-examples\game-of-life-kmp\build\lib\game-of-life-kmp-jvm-0.0.3.jar"; Type = "jar" },
  @{ Name = "k-perf_jar"; Path = "..\..\kmp-examples\game-of-life-kmp-k-perf\build\lib\game-of-life-kmp-k-perf-jvm-0.0.3.jar"; Type = "jar" },
  @{ Name = "plain_exe"; Path = "..\..\kmp-examples\game-of-life-kmp\build\bin\mingwX64\releaseExecutable\game-of-life-kmp.exe"; Type = "exe" },
  @{ Name = "k-perf_exe"; Path = "..\..\kmp-examples\game-of-life-kmp-k-perf\build\bin\mingwX64\releaseExecutable\game-of-life-kmp-k-perf.exe"; Type = "exe" },
  @{ Name = "plain_node"; Path = "..\..\kmp-examples\game-of-life-kmp\build\js\packages\game-of-life-kmp\kotlin\game-of-life-kmp.js"; Type = "node" },
  @{ Name = "k-perf_node"; Path = "..\..\kmp-examples\game-of-life-kmp-k-perf\build\js\packages\game-of-life-kmp-k-perf\kotlin\game-of-life-kmp-k-perf.js"; Type = "node" }
)

# Create a test suite name from the executable names
$testSuiteName = "game-of-life-kmp-k-perf"

# Validation phase: Check if all executables exist
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
    $missingExecutables += $executable.Name
  }
}

if ($missingExecutables.Count -gt 0) {
  Write-Host ""
  Write-Host "ERROR: The following executables were not found:"
  foreach ($missing in $missingExecutables) {
    Write-Host "  - $missing"
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
          $output = java -jar $filePath 2>&1
          $executionSuccess = $LASTEXITCODE -eq 0 
        }
        "exe" { 
          $output = & $filePath 2>&1
          $executionSuccess = $LASTEXITCODE -eq 0 
        }
        "node" { 
          $output = node $filePath 2>&1
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
  $payload = [ordered]@{
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