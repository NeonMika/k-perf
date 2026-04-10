[CmdletBinding()]
param(
  [ValidateRange(1, [int]::MaxValue)]
  [int]$RepetitionCount = 50,

  [bool]$CleanBuild = $false,

  [ValidateRange(1, [int]::MaxValue)]
  [int]$StepCount = 500,

  [ValidateRange(0, [int]::MaxValue)]
  [int]$WarmupCount = 0,

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

  [ValidateNotNullOrEmpty()]
  [string]$CILabel = "local"
)

. "$PSScriptRoot\..\utils.ps1"
. "$PSScriptRoot\..\build.ps1"

$nativeExt = if ($IsWindows) { ".exe" } else { ".kexe" }

Write-Host "=========================================="
Write-Host "# Collecting System Information..."
Write-Host "=========================================="

$machineInfo = Get-MachineInfo -GradleProjectPath "..\..\kmp-examples\game-of-life-kmp-commonmain"

Write-Host "System Information collected:"
foreach ($key in $machineInfo.Keys) {
  Write-Host "  $key : $($machineInfo[$key])"
}
Write-Host ""

if (-not ($Common -or $Dedicated)) {
  Write-Host "ERROR: At least one of -Common or -Dedicated must be true."
  exit 1
}

if (-not ($JVM -or $JS -or $Native)) {
  Write-Host "ERROR: At least one of -JVM, -JS, or -Native must be true."
  exit 1
}

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

# Generate Cartesian product of k-perf configurations
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
  Write-Host "- $(Get-KPerfSuffix -Config $config)"
}

# Clean phase
if ($CleanBuild) {
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "# Cleaning k-perf benchmark dependencies..."
  Write-Host "=========================================="

  Invoke-GradleClean -Path "..\..\KIRHelperKit"                                    -Name "KIRHelperKit"
  Invoke-GradleClean -Path "..\..\plugins\k-perf"                                  -Name "k-perf plugin"
  Invoke-GradleClean -Path "..\..\kmp-examples\game-of-life-kmp-commonmain"         -Name "game-of-life-kmp-commonmain"
  Invoke-GradleClean -Path "..\..\kmp-examples\game-of-life-kmp-dedicatedmain"      -Name "game-of-life-kmp-dedicatedmain"
  Invoke-GradleClean -Path "..\..\kmp-examples\game-of-life-kmp-commonmain-k-perf"  -Name "game-of-life-kmp-commonmain-k-perf"
  Invoke-GradleClean -Path "..\..\kmp-examples\game-of-life-kmp-dedicatedmain-k-perf" -Name "game-of-life-kmp-dedicatedmain-k-perf"
  Write-Host ""
}
else {
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "# Skipping clean phase (CleanBuild = false)..."
  Write-Host "=========================================="
  Write-Host ""
}

# Build phase
$buildTimes = @{}
Write-Host ""
Write-Host "=========================================="
Write-Host "# Building k-perf benchmark dependencies..."
Write-Host "=========================================="

$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-KirHelperKit)
$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-KPerfPlugin)
$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeCommonMainReference)
$buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeDedicatedMainReference)

foreach ($config in $kPerfCombinations) {
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeKPerfVariant -GameType ([GameType]::CommonMain)    -Config $config)
  $buildTimes = Merge-Hashtable -Target $buildTimes -Source (Build-GameOfLifeKPerfVariant -GameType ([GameType]::DedicatedMain) -Config $config)
}

Write-Host ""
Write-Host "=========================================="
Write-Host "# Build phase completed successfully!"
Write-Host "=========================================="

# Locate project roots for artifact lookup
$commonMainProjectRoot         = "..\..\kmp-examples\game-of-life-kmp-commonmain"
$commonMainKPerfProjectRoot    = "..\..\kmp-examples\game-of-life-kmp-commonmain-k-perf"
$dedicatedMainProjectRoot      = "..\..\kmp-examples\game-of-life-kmp-dedicatedmain"
$dedicatedMainKPerfProjectRoot = "..\..\kmp-examples\game-of-life-kmp-dedicatedmain-k-perf"

# Collect executables for the requested game types
$executables = @()
if ($Common) {
  $executables += Invoke-GetExecutables `
    -GameType            ([GameType]::CommonMain) `
    -KPerfCombinations   $kPerfCombinations `
    -Reference           $Reference `
    -JVM                 $JVM `
    -JS                  $JS `
    -Native              $Native `
    -NativeExt           $nativeExt `
    -PlainProjectRoot    $commonMainProjectRoot `
    -KPerfProjectRoot    $commonMainKPerfProjectRoot `
    -ArtifactVersion     $artifactVersion
}
if ($Dedicated) {
  $executables += Invoke-GetExecutables `
    -GameType            ([GameType]::DedicatedMain) `
    -KPerfCombinations   $kPerfCombinations `
    -Reference           $Reference `
    -JVM                 $JVM `
    -JS                  $JS `
    -Native              $Native `
    -NativeExt           $nativeExt `
    -PlainProjectRoot    $dedicatedMainProjectRoot `
    -KPerfProjectRoot    $dedicatedMainKPerfProjectRoot `
    -ArtifactVersion     $artifactVersion
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

# Clean up any leftover trace and symbol files from previous runs
Write-Host "Cleaning up existing trace and symbol files..."
$existingTraceFiles  = Get-ChildItem -Path "." -Filter "trace*.txt"  -ErrorAction SilentlyContinue
$existingSymbolFiles = Get-ChildItem -Path "." -Filter "symbol*.txt" -ErrorAction SilentlyContinue
$cleanedFiles = @()

foreach ($file in $existingTraceFiles)  { Remove-Item -Path $file.FullName -Force; $cleanedFiles += $file.Name }
foreach ($file in $existingSymbolFiles) { Remove-Item -Path $file.FullName -Force; $cleanedFiles += $file.Name }

if ($cleanedFiles.Count -gt 0) {
  Write-Host "Deleted: $($cleanedFiles -join ', ')"
}
else {
  Write-Host "No existing trace/symbol files found."
}
Write-Host ""

# Create measurement directory
$measurementTimestamp = Get-Date -Format "yyyy_MM_dd_HH_mm_ss"
$measurementDirName = "{0}_game-of-life-kmp-k-perf_{1}_{2}reps_{3}steps" -f $measurementTimestamp, $CILabel, $RepetitionCount, $StepCount
$measurementDir = Join-Path "..\..\measurements" $measurementDirName

if (Test-Path $measurementDir) {
  Write-Host "ERROR: Measurement directory already exists: $measurementDir"
  Write-Host "Please try again in a moment to get a different timestamp."
  exit 1
}

New-Item -ItemType Directory -Path $measurementDir -Force | Out-Null

# Post-iteration action: handle trace/symbol files generated by the k-perf runtime
$postIterationAction = {
  param($exec, $iteration, $dir)

  $traceFiles  = Get-ChildItem -Path "." -Filter "trace*.txt"  -ErrorAction SilentlyContinue
  $symbolFiles = Get-ChildItem -Path "." -Filter "symbol*.txt" -ErrorAction SilentlyContinue
  $deletedFiles = @()

  foreach ($traceFile in $traceFiles) {
    Write-Host "- Processing trace file: $($traceFile.Name)"

    try {
      $graphVisualizerPath = "..\..\analyzers\call_graph_visualizer\graph-visualizer.py"
      python $graphVisualizerPath $traceFile.FullName *>&1 | Out-Null

      if ($LASTEXITCODE -eq 0) {
        $pngFile = Get-ChildItem -Path "." -Filter "*.png" -ErrorAction SilentlyContinue |
                   Sort-Object -Property CreationTime -Descending |
                   Select-Object -First 1

        if ($pngFile) {
          $newPngName = "$($exec.Name)_$iteration.png"
          $newPngPath = Join-Path "." $newPngName
          Rename-Item -Path $pngFile.FullName -NewName $newPngName -Force
          Copy-Item -Path $newPngPath -Destination $dir -Force
          Remove-Item -Path $newPngPath -Force
          Write-Host "-- Copied $newPngName to measurements folder"
        }
      }
      else {
        Write-Host "-- Skipped call graph PNG (graph-visualizer.py returned non-zero)"
      }
    }
    catch {
      Write-Host "-- Skipped call graph PNG (python not available or error: $_)"
    }

    Remove-Item -Path $traceFile.FullName -Force
    $deletedFiles += $traceFile.Name
  }

  foreach ($symbolFile in $symbolFiles) {
    Remove-Item -Path $symbolFile.FullName -Force
    $deletedFiles += $symbolFile.Name
  }

  if ($deletedFiles.Count -gt 0) {
    Write-Host "-- Deleted: $($deletedFiles -join ', ')"
  }
}

# Suite-level parameters embedded in every result JSON
$suiteParameters = [ordered]@{
  CILabel                     = $CILabel
  RepetitionCount             = $RepetitionCount
  WarmupCount                 = $WarmupCount
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

# Run all benchmarks
Invoke-BenchmarkSuite `
  -Executables         $executables `
  -RepetitionCount     $RepetitionCount `
  -WarmupCount         $WarmupCount `
  -StepCount           $StepCount `
  -MeasurementDir      $measurementDir `
  -MachineInfo         $machineInfo `
  -BuildTimes          $buildTimes `
  -Parameters          $suiteParameters `
  -CleanBuild          $CleanBuild `
  -PostIterationAction $postIterationAction

exit 0
