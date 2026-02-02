param(
  [bool]$CleanBuild = $true
)

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
  Write-Host $Title
  Write-Host "Path: $Path"
  Write-Host "Tasks: $($Tasks -join ' ')"
  Write-Host "=========================================="

  Push-Location $Path
  try {
    if ($CleanBuild -and -not $SkipClean) {
      & .\gradlew clean @Tasks
    }
    else {
      & .\gradlew @Tasks
    }

    if ($LASTEXITCODE -ne 0) {
      throw "$Title failed with exit code $LASTEXITCODE"
    }
  }
  finally {
    Pop-Location
  }

  Write-Host "$Title completed successfully."
}

function Find-FirstGradleTask {
  param(
    [string[]]$TaskList,
    [string[]]$Candidates
  )

  foreach ($candidate in $Candidates) {
    $pattern = "(?m)^(?:\s*:)?$candidate\b"
    if ($TaskList -match $pattern) {
      return $candidate
    }
  }

  return $null
}

function Invoke-GradleTaskIfPresent {
  param(
    [string]$TaskName,
    [string]$Title
  )

  if ([string]::IsNullOrWhiteSpace($TaskName)) {
    Write-Host "Skipping $Title (task not found)"
    return
  }

  Write-Host ""
  Write-Host "=========================================="
  Write-Host $Title
  Write-Host "Task: $TaskName"
  Write-Host "=========================================="

  & .\gradlew $TaskName
  if ($LASTEXITCODE -ne 0) {
    throw "$Title failed with exit code $LASTEXITCODE"
  }

  Write-Host "$Title completed successfully."
}

function Invoke-KmpBuild {
  param(
    [string]$Title,
    [string]$Path
  )

  Write-Host ""
  Write-Host "=========================================="
  Write-Host "$Title (Kotlin Multiplatform)"
  Write-Host "Path: $Path"
  Write-Host "=========================================="

  Push-Location $Path
  try {
    if ($CleanBuild) {
      & .\gradlew clean
      if ($LASTEXITCODE -ne 0) {
        throw "$Title clean failed with exit code $LASTEXITCODE"
      }
    }

    $taskList = & .\gradlew -q tasks --all
    if ($LASTEXITCODE -ne 0) {
      throw "$Title task discovery failed with exit code $LASTEXITCODE"
    }

    $jvmTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("jvmJar", "compileKotlinJvm")
    $jsTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("jsProductionExecutableCompileSync", "jsProductionExecutableCompile", "jsNodeProductionExecutableCompileSync", "jsNodeProductionExecutableCompile", "jsBrowserProductionWebpack", "compileKotlinJs")
    $windowsTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableMingwX64", "linkDebugExecutableMingwX64")
    $linuxTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableLinuxX64", "linkDebugExecutableLinuxX64")
    $macTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableMacosX64", "linkDebugExecutableMacosX64", "linkReleaseExecutableMacosArm64", "linkDebugExecutableMacosArm64")

    Invoke-GradleTaskIfPresent -TaskName $jvmTask -Title "$Title - JVM build"
    Invoke-GradleTaskIfPresent -TaskName $jsTask -Title "$Title - JS build"
    Invoke-GradleTaskIfPresent -TaskName $windowsTask -Title "$Title - Windows build"
    Invoke-GradleTaskIfPresent -TaskName $linuxTask -Title "$Title - Linux build"
    Invoke-GradleTaskIfPresent -TaskName $macTask -Title "$Title - Mac build"
  }
  finally {
    Pop-Location
  }

  Write-Host "$Title completed successfully."
}

Write-Host "=========================================="
Write-Host "Starting full build (CleanBuild = $CleanBuild)"
Write-Host "=========================================="

# KIRHelperKit (publish to local Maven)
Invoke-GradleBuild -Title "Building KIRHelperKit" -Path ".\KIRHelperKit" -Tasks @("build", "publishToMavenLocal")

# Plugins (publish to local Maven)
Invoke-GradleBuild -Title "Building instrumentation-overhead-analyzer plugin" -Path ".\plugins\instrumentation-overhead-analyzer" -Tasks @("build", "publishToMavenLocal")
Invoke-GradleBuild -Title "Building k-perf plugin" -Path ".\plugins\k-perf" -Tasks @("build", "publishToMavenLocal")

# KMP examples
Invoke-KmpBuild -Title "Building game-of-life-kmp example" -Path ".\kmp-examples\game-of-life-kmp"
Invoke-KmpBuild -Title "Building game-of-life-kmp-ioa example" -Path ".\kmp-examples\game-of-life-kmp-ioa"
Invoke-KmpBuild -Title "Building game-of-life-kmp-k-perf example" -Path ".\kmp-examples\game-of-life-kmp-k-perf"

Write-Host ""
Write-Host "=========================================="
Write-Host "All builds completed successfully."
Write-Host "=========================================="
