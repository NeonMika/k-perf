# Gradle helper utilities for benchmarking scripts

# Use the correct gradlew wrapper for the current platform
$gradlewCmd = if ($IsWindows) { ".\gradlew" } else { "./gradlew" }

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

function Invoke-GradleTaskIfPresentTimed {
  param(
    [string]$TaskName,
    [string]$Title,
    [string[]]$GradleArgs = @()
  )

  if ([string]::IsNullOrWhiteSpace($TaskName)) {
    Write-Host "Skipping $Title (task not found)"
    return $null
  }

  Write-Host ""
  Write-Host "=========================================="
  Write-Host "### $Title"
  Write-Host "Task: $TaskName"
  Write-Host "=========================================="

  $taskStart = Get-Date
  & $gradlewCmd @GradleArgs $TaskName | Out-Host
  $taskEndTime = Get-Date
  if ($LASTEXITCODE -ne 0) {
    throw "$Title failed with exit code $LASTEXITCODE"
  }
  $taskDuration = ($taskEndTime - $taskStart).TotalMilliseconds

  Write-Host "$Title completed successfully in $([math]::Round($taskDuration, 2)) ms."
  return $taskDuration
}

function Invoke-GradleClean {
  param(
    [string]$Path,
    [string]$Name
  )

  Write-Host ""
  Write-Host "=========================================="
  Write-Host "## Cleaning $Name"
  Write-Host "=========================================="
  Push-Location $Path
  try {
    & $gradlewCmd clean | Out-Host
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: $Name clean failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
}

function Invoke-KmpBuild {
  param(
    [string]$Title,
    [string]$Path,
    [bool]$CleanBuild = $true
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

    $jvmTask    = Find-FirstGradleTask -TaskList $taskList -Candidates @("jvmJar", "compileKotlinJvm")
    $jsTask     = Find-FirstGradleTask -TaskList $taskList -Candidates @("jsProductionExecutableCompileSync", "jsProductionExecutableCompile", "jsNodeProductionExecutableCompileSync", "jsNodeProductionExecutableCompile", "jsBrowserProductionWebpack", "compileKotlinJs")
    $windowsTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableMingwX64", "linkDebugExecutableMingwX64")
    $linuxTask  = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableLinuxX64", "linkDebugExecutableLinuxX64")
    $macTask    = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableMacosX64", "linkDebugExecutableMacosX64", "linkReleaseExecutableMacosArm64", "linkDebugExecutableMacosArm64")

    Invoke-GradleTaskIfPresent -TaskName $jvmTask    -Title "$Title - JVM build"
    Invoke-GradleTaskIfPresent -TaskName $jsTask     -Title "$Title - JS build"
    Invoke-GradleTaskIfPresent -TaskName $windowsTask -Title "$Title - Windows build"
    Invoke-GradleTaskIfPresent -TaskName $linuxTask  -Title "$Title - Linux build"
    Invoke-GradleTaskIfPresent -TaskName $macTask    -Title "$Title - Mac build"
  }
  finally {
    Pop-Location
  }

  Write-Host "$Title completed successfully."
}

function Invoke-KmpBuildWithTimings {
  param(
    [string]$Title,
    [string]$Path,
    [string[]]$GradleArgs = @()
  )

  Write-Host ""
  Write-Host "=========================================="
  Write-Host "## $Title (Kotlin Multiplatform)"
  Write-Host "Path: $Path"
  Write-Host "=========================================="

  $timings = [ordered]@{}

  Push-Location $Path
  try {
    $taskList = & $gradlewCmd -q tasks --all | Out-String
    if ($LASTEXITCODE -ne 0) {
      throw "$Title task discovery failed with exit code $LASTEXITCODE"
    }

    $jvmTask    = Find-FirstGradleTask -TaskList $taskList -Candidates @("jvmJar", "compileKotlinJvm")
    $jsTask     = Find-FirstGradleTask -TaskList $taskList -Candidates @("jsProductionExecutableCompileSync", "jsProductionExecutableCompile", "jsNodeProductionExecutableCompileSync", "jsNodeProductionExecutableCompile", "jsBrowserProductionWebpack", "compileKotlinJs")
    $windowsTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableMingwX64", "linkDebugExecutableMingwX64")
    $linuxTask  = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableLinuxX64", "linkDebugExecutableLinuxX64")
    $macTask    = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableMacosX64", "linkDebugExecutableMacosX64", "linkReleaseExecutableMacosArm64", "linkDebugExecutableMacosArm64")

    $jvmDuration = Invoke-GradleTaskIfPresentTimed -TaskName $jvmTask    -Title "$Title - JVM build"    -GradleArgs $GradleArgs
    if ($null -ne $jvmDuration) { $timings.jvm = $jvmDuration }

    $jsDuration = Invoke-GradleTaskIfPresentTimed -TaskName $jsTask     -Title "$Title - JS build"     -GradleArgs $GradleArgs
    if ($null -ne $jsDuration) { $timings.js = $jsDuration }

    $windowsDuration = Invoke-GradleTaskIfPresentTimed -TaskName $windowsTask -Title "$Title - Windows build" -GradleArgs $GradleArgs
    if ($null -ne $windowsDuration) { $timings.windows = $windowsDuration }

    $linuxDuration = Invoke-GradleTaskIfPresentTimed -TaskName $linuxTask  -Title "$Title - Linux build"  -GradleArgs $GradleArgs
    if ($null -ne $linuxDuration) { $timings.linux = $linuxDuration }

    $macDuration = Invoke-GradleTaskIfPresentTimed -TaskName $macTask    -Title "$Title - Mac build"    -GradleArgs $GradleArgs
    if ($null -ne $macDuration) { $timings.mac = $macDuration }

  }
  finally {
    Pop-Location
  }

  Write-Host "$Title completed successfully."
  return $timings
}
