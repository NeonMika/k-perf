param(
  [bool]$CleanBuild = $true,
  [switch]$SkipNative
)

$ErrorActionPreference = "Stop"

function Invoke-Publish {
  param(
    [string]$Title,
    [string]$Path
  )

  Write-Host ""
  Write-Host "=========================================="
  Write-Host "Publishing: $Title"
  Write-Host "Path: $Path"
  Write-Host "=========================================="

  Push-Location $Path
  try {
    if ($CleanBuild) { & .\gradlew clean publishToMavenLocal }
    else             { & .\gradlew publishToMavenLocal }

    if ($LASTEXITCODE -ne 0) { throw "$Title failed with exit code $LASTEXITCODE" }
  }
  finally {
    Pop-Location
  }

  Write-Host "$Title published successfully."
}

function Find-FirstGradleTask {
  param([string[]]$TaskList, [string[]]$Candidates)
  foreach ($candidate in $Candidates) {
    $pattern = "(?m)^(?:\s*:)?$candidate\b"
    if ($TaskList -match $pattern) { return $candidate }
  }
  return $null
}

function Invoke-GradleTaskIfPresent {
  param([string]$TaskName, [string]$Title)
  if ([string]::IsNullOrWhiteSpace($TaskName)) {
    Write-Host "Skipping $Title (task not found)"
    return
  }
  Write-Host ""
  Write-Host "------ $Title (task: $TaskName) ------"
  & .\gradlew $TaskName
  if ($LASTEXITCODE -ne 0) { throw "$Title failed with exit code $LASTEXITCODE" }
}

function Invoke-ExampleBuild {
  param(
    [string]$Title,
    [string]$Path
  )

  Write-Host ""
  Write-Host "=========================================="
  Write-Host "Building example: $Title"
  Write-Host "Path: $Path"
  Write-Host "=========================================="

  Push-Location $Path
  try {
    if ($CleanBuild) {
      & .\gradlew clean
      if ($LASTEXITCODE -ne 0) { throw "$Title clean failed with exit code $LASTEXITCODE" }
    }

    $taskList = & .\gradlew -q tasks --all
    if ($LASTEXITCODE -ne 0) { throw "$Title task discovery failed with exit code $LASTEXITCODE" }

    $jvmTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("jvmJar", "compileKotlinJvm")
    $jsTask  = Find-FirstGradleTask -TaskList $taskList -Candidates @(
      "jsProductionExecutableCompileSync", "jsNodeProductionExecutableCompileSync",
      "jsProductionExecutableCompile", "compileKotlinJs")
    $winTask = Find-FirstGradleTask -TaskList $taskList -Candidates @(
      "linkReleaseExecutableMingwX64", "linkDebugExecutableMingwX64")

    Invoke-GradleTaskIfPresent -TaskName $jvmTask -Title "$Title - JVM"
    Invoke-GradleTaskIfPresent -TaskName $jsTask  -Title "$Title - JS"
    if (-not $SkipNative) {
      Invoke-GradleTaskIfPresent -TaskName $winTask -Title "$Title - mingwX64"
    }
  }
  finally {
    Pop-Location
  }

  Write-Host "$Title built successfully."
}

Write-Host "=========================================="
Write-Host "Full variant build (CleanBuild = $CleanBuild, SkipNative = $SkipNative)"
Write-Host "=========================================="

Invoke-Publish -Title "KIRHelperKit" -Path ".\KIRHelperKit"

Invoke-Publish -Title "otlp-exporter (JSON)"         -Path ".\otlp-exporter"
Invoke-Publish -Title "otlp-exporter-proto"          -Path ".\otlp-exporter-proto"
Invoke-Publish -Title "otel-plugin util (JSON)"      -Path ".\plugins\otel-plugin\util"
Invoke-Publish -Title "otel-plugin util (proto)"     -Path ".\plugins\otel-plugin-proto\util"

Invoke-Publish -Title "k-perf plugin"                          -Path ".\plugins\k-perf"
Invoke-Publish -Title "instrumentation-overhead-analyzer"      -Path ".\plugins\instrumentation-overhead-analyzer"
Invoke-Publish -Title "otel plugin (JSON)"                     -Path ".\plugins\otel-plugin\plugin"
Invoke-Publish -Title "otel plugin (proto)"                    -Path ".\plugins\otel-plugin-proto\plugin"
Invoke-Publish -Title "otel plugin (proto-timesource)"         -Path ".\plugins\otel-plugin-proto-timesource\plugin"
Invoke-Publish -Title "otel plugin (proto-anchored)"           -Path ".\plugins\otel-plugin-proto-anchored\plugin"
Invoke-Publish -Title "otel plugin (proto-sampler)"            -Path ".\plugins\otel-plugin-proto-sampler\plugin"
Invoke-Publish -Title "otel plugin (proto-fastbatch)"          -Path ".\plugins\otel-plugin-proto-fastbatch\plugin"

Invoke-ExampleBuild -Title "game-of-life-kmp-commonmain"         -Path ".\kmp-examples\game-of-life-kmp-commonmain"
Invoke-ExampleBuild -Title "game-of-life-kmp-commonmain-ioa"     -Path ".\kmp-examples\game-of-life-kmp-commonmain-ioa"
Invoke-ExampleBuild -Title "game-of-life-kmp-commonmain-k-perf"  -Path ".\kmp-examples\game-of-life-kmp-commonmain-k-perf"
Invoke-ExampleBuild -Title "game-of-life-kmp-dedicatedmain"      -Path ".\kmp-examples\game-of-life-kmp-dedicatedmain"
Invoke-ExampleBuild -Title "game-of-life-kmp-dedicatedmain-k-perf" -Path ".\kmp-examples\game-of-life-kmp-dedicatedmain-k-perf"

Invoke-ExampleBuild -Title "comparison-baseline"                -Path ".\kmp-examples\comparison-baseline"
Invoke-ExampleBuild -Title "comparison-k-perf"                  -Path ".\kmp-examples\comparison-k-perf"
Invoke-ExampleBuild -Title "comparison-otel"                    -Path ".\kmp-examples\comparison-otel"
Invoke-ExampleBuild -Title "comparison-otel-proto"              -Path ".\kmp-examples\comparison-otel-proto"
Invoke-ExampleBuild -Title "comparison-otel-proto-timesource"   -Path ".\kmp-examples\comparison-otel-proto-timesource"
Invoke-ExampleBuild -Title "comparison-otel-proto-anchored"     -Path ".\kmp-examples\comparison-otel-proto-anchored"
Invoke-ExampleBuild -Title "comparison-otel-proto-sampler"      -Path ".\kmp-examples\comparison-otel-proto-sampler"
Invoke-ExampleBuild -Title "comparison-otel-proto-fastbatch"    -Path ".\kmp-examples\comparison-otel-proto-fastbatch"

Write-Host ""
Write-Host "=========================================="
Write-Host "All 26 builds completed successfully."
Write-Host "=========================================="
