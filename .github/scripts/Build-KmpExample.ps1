# Builds all supported KMP targets for the project in the current working directory.
# Called per-project by build-all-on-windows-separated.yml for discrete CI step visibility.
#
# Task discovery logic mirrors buildAll.ps1 — keep both in sync when changing target lists.
$ErrorActionPreference = "Stop"

.\gradlew clean
if ($LASTEXITCODE -ne 0) { throw "clean failed with exit code $LASTEXITCODE" }

$taskList = .\gradlew -q tasks --all
if ($LASTEXITCODE -ne 0) { throw "task discovery failed with exit code $LASTEXITCODE" }

function Find-FirstTask {
  param([string[]]$Candidates)
  foreach ($candidate in $Candidates) {
    $pattern = "(?m)^(?:\s*:)?$candidate\b"
    if ($taskList -match $pattern) { return $candidate }
  }
  return $null
}

function Invoke-IfFound {
  param([string[]]$Candidates)
  $task = Find-FirstTask $Candidates
  if ($task) {
    .\gradlew $task
    if ($LASTEXITCODE -ne 0) { throw "$task failed with exit code $LASTEXITCODE" }
  } else {
    Write-Host "Skipping: no matching task found among: $($Candidates -join ', ')"
  }
}

Invoke-IfFound @("jvmJar", "compileKotlinJvm")
Invoke-IfFound @("jsProductionExecutableCompileSync", "jsProductionExecutableCompile", "jsNodeProductionExecutableCompileSync", "jsNodeProductionExecutableCompile", "jsBrowserProductionWebpack", "compileKotlinJs")
Invoke-IfFound @("linkReleaseExecutableMingwX64", "linkDebugExecutableMingwX64")
Invoke-IfFound @("linkReleaseExecutableLinuxX64", "linkDebugExecutableLinuxX64")
Invoke-IfFound @("linkReleaseExecutableMacosX64", "linkDebugExecutableMacosX64", "linkReleaseExecutableMacosArm64", "linkDebugExecutableMacosArm64")
