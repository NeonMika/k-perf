param(
  [bool]$CleanBuild = $true
)

$IsWindows = $PSVersionTable.OS -match 'Windows' -or [System.Environment]::OSVersion.Platform -eq 'Win32NT'
$ErrorActionPreference = "Stop"

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
    } else {
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

function Invoke-RunAndMeasure {
  param(
    [string]$Command,
    [string]$Label
  )
  
  Write-Host ""
  Write-Host "Measuring: $Label"
  Write-Host "Executing: $Command"
  
  $output = cmd.exe /c "$Command 2>&1"
  $output | ForEach-Object { Write-Host $_ }
  
  # Join output array into a single string for regex matching
  $outputStr = $output -join "`n"
  $execTime = [regex]::Match($outputStr, "Execution finished - (\d+) ms elapsed")
  if ($execTime.Success) {
      $ms = $execTime.Groups[1].Value
      Write-Host ""
      Write-Host ">>> [$Label] TIME: $ms ms <<<" -ForegroundColor Green
  } else {
      Write-Host ">>> [$Label] FAILED TO PARSE TIME <<<" -ForegroundColor Red
  }
}

Write-Host "=========================================="
Write-Host "Compiling Required Plugins and Dependencies"
Write-Host "=========================================="

# Invoke-GradleBuild -Title "KIRHelperKit" -Path ".\KIRHelperKit" -Tasks @("publishToMavenLocal")
# Invoke-GradleBuild -Title "k-perf plugin" -Path ".\plugins\k-perf" -Tasks @("publishToMavenLocal")

# Invoke-GradleBuild -Title "OTel OTLP Exporter" -Path ".\otel\otlp-exporter" -Tasks @("publishToMavenLocal")
# Invoke-GradleBuild -Title "OTel Plugin Util" -Path ".\plugins\otel-plugin\util" -Tasks @("publishToMavenLocal")
# Invoke-GradleBuild -Title "OTel Plugin" -Path ".\plugins\otel-plugin\plugin" -Tasks @("publishToMavenLocal")

Write-Host "=========================================="
Write-Host "Compiling Comparison Projects"
Write-Host "=========================================="

# Build common projects for all targets
Invoke-GradleBuild -Title "Comparison Project (k-perf)" -Path ".\kmp-examples\comparison-k-perf" -Tasks @("jvmJar", "jsProductionExecutableCompileSync") -SkipClean $true
Invoke-GradleBuild -Title "Comparison Project (otel)" -Path ".\kmp-examples\comparison-otel" -Tasks @("jvmJar", "jsProductionExecutableCompileSync") -SkipClean $true

Write-Host "=========================================="
Write-Host "Running Measurements"
Write-Host "=========================================="

# k-perf paths
$kperfJvm = "java -jar .\kmp-examples\comparison-k-perf\build\lib\comparison-k-perf-jvm-0.1.0-flushEarly-true.jar"
$kperfJs = "node .\kmp-examples\comparison-k-perf\build\js\packages\comparison-k-perf-flushEarly-true\kotlin\comparison-k-perf-flushEarly-true.js"
$kperfNativeLinux = ".\kmp-examples\comparison-k-perf\build\bin\linuxX64\releaseExecutable\comparison-k-perf-flushEarly-true.kexe"
$kperfNativeWin = ".\kmp-examples\comparison-k-perf\build\bin\mingwX64\releaseExecutable\comparison-k-perf-flushEarly-true.exe"

# otel paths
$otelJvm = "java -jar .\kmp-examples\comparison-otel\build\lib\comparison-otel-jvm-1.0.0.jar"
$otelJs = "node .\kmp-examples\comparison-otel\build\js\packages\comparison-otel\kotlin\comparison-otel.js"
$otelNativeLinux = ".\kmp-examples\comparison-otel\build\bin\linuxX64\releaseExecutable\main.kexe"
$otelNativeWin = ".\kmp-examples\comparison-otel\build\bin\mingwX64\releaseExecutable\main.exe"

Invoke-RunAndMeasure -Command $kperfJvm -Label "k-perf - JVM"
Invoke-RunAndMeasure -Command $otelJvm -Label "otel - JVM"

Invoke-RunAndMeasure -Command $kperfJs -Label "k-perf - JS (Node)"
Invoke-RunAndMeasure -Command $otelJs -Label "otel - JS (Node)"

Write-Host ""
Write-Host ">>> Skipping Native Measurements: The OpenTelemetry Kotlin SDK does not currently support Windows Native (mingwX64), and Linux Native (linuxX64) cannot be cross-compiled on Windows due to missing OpenSSL/cURL sysroots. <<<" -ForegroundColor Yellow
Write-Host ""

Write-Host "=========================================="
Write-Host "Measurements Complete"
Write-Host "=========================================="
