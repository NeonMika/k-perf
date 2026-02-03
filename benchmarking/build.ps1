# Build functions for benchmarking applications

. "$PSScriptRoot\utils.ps1"

function Build-KirHelperKit {
  param([bool]$CleanBuild = $true)
  
  Write-Host ""
  Write-Host "## Building KIRHelperKit..."
  Push-Location "..\..\KIRHelperKit"
  try {
    if ($CleanBuild) {
      & .\gradlew clean | Out-Host
      if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: KIRHelperKit clean failed!"
        exit 1
      }
    }
    
    $buildStartTime = Get-Date
    & .\gradlew build publishToMavenLocal | Out-Host
    $buildEndTime = Get-Date
    
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: KIRHelperKit build failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
  $buildDuration = ($buildEndTime - $buildStartTime).TotalMilliseconds
  Write-Host "KIRHelperKit build completed successfully in $([math]::Round($buildDuration, 2)) ms."
  return [ordered]@{ KirHelperKit = $buildDuration }
}

function Build-KperfPlugin {
  param([bool]$CleanBuild = $true)
  
  Write-Host ""
  Write-Host "## Building k-perf (Kotlin compiler plugin)..."
  Push-Location "..\..\plugins\k-perf"
  try {
    if ($CleanBuild) {
      & .\gradlew clean | Out-Host
      if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: k-perf clean failed!"
        exit 1
      }
    }
    
    $buildStartTime = Get-Date
    & .\gradlew build publishToMavenLocal | Out-Host
    $buildEndTime = Get-Date
    
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: k-perf build failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
  $buildDuration = ($buildEndTime - $buildStartTime).TotalMilliseconds
  Write-Host "k-perf build completed successfully in $([math]::Round($buildDuration, 2)) ms."
  return [ordered]@{ KperfPlugin = $buildDuration }
}

function Build-InstrumentationOverheadAnalyzerPlugin {
  param([bool]$CleanBuild = $true)
  
  Write-Host ""
  Write-Host "## Building instrumentation-overhead-analyzer (Kotlin compiler plugin)..."
  Push-Location "..\..\plugins\instrumentation-overhead-analyzer"
  try {
    if ($CleanBuild) {
      & .\gradlew clean | Out-Host
      if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: instrumentation-overhead-analyzer clean failed!"
        exit 1
      }
    }
    
    $buildStartTime = Get-Date
    & .\gradlew build publishToMavenLocal | Out-Host
    $buildEndTime = Get-Date
    
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: instrumentation-overhead-analyzer build failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
  $buildDuration = ($buildEndTime - $buildStartTime).TotalMilliseconds
  Write-Host "instrumentation-overhead-analyzer build completed successfully in $([math]::Round($buildDuration, 2)) ms."
  return [ordered]@{ InstrumentationOverheadAnalyzerPlugin = $buildDuration }
}

function Build-GameOfLifeCommonMainReference {
  param([bool]$CleanBuild = $true)
  
  Write-Host ""
  Write-Host "## Building game-of-life-kmp-commonmain reference application (without plugin)..."
  $timings = Invoke-KmpBuildWithTimings -Title "game-of-life-kmp-commonmain reference application" -Path "..\..\kmp-examples\game-of-life-kmp-commonmain" -CleanBuild $CleanBuild
  $buildTimes = [ordered]@{}
  if ($timings.Contains('jvm')) {
    $buildTimes['commonmain-plain-jar'] = $timings.jvm
    $buildTimes['commonmain_plain_jar'] = $timings.jvm
  }
  if ($timings.Contains('js')) {
    $buildTimes['commonmain-plain-node'] = $timings.js
    $buildTimes['commonmain_plain_node'] = $timings.js
  }
  if ($timings.Contains('windows')) {
    $buildTimes['commonmain-plain-exe'] = $timings.windows
    $buildTimes['commonmain_plain_exe'] = $timings.windows
  }
  Write-Host "game-of-life-kmp-commonmain reference application build completed successfully."
  return $buildTimes
}

function Build-GameOfLifeDedicatedMainReference {
  param([bool]$CleanBuild = $true)
  
  Write-Host ""
  Write-Host "## Building game-of-life-kmp-dedicatedmain reference application (without plugin)..."
  $timings = Invoke-KmpBuildWithTimings -Title "game-of-life-kmp-dedicatedmain reference application" -Path "..\..\kmp-examples\game-of-life-kmp-dedicatedmain" -CleanBuild $CleanBuild
  $buildTimes = [ordered]@{}
  if ($timings.Contains('jvm')) { $buildTimes['dedicatedmain-plain-jar'] = $timings.jvm }
  if ($timings.Contains('js')) { $buildTimes['dedicatedmain-plain-node'] = $timings.js }
  if ($timings.Contains('windows')) { $buildTimes['dedicatedmain-plain-exe'] = $timings.windows }
  Write-Host "game-of-life-kmp-dedicatedmain reference application build completed successfully."
  return $buildTimes
}

function Build-GameOfLifeCommonMainKperfFlushEarlyTrue {
  param([bool]$CleanBuild = $true)
  
  Write-Host ""
  Write-Host "## Building game-of-life-kmp-commonmain-k-perf with -PkperfFlushEarly=true..."
  $timings = Invoke-KmpBuildWithTimings -Title "game-of-life-kmp-commonmain-k-perf (flushEarly=true)" -Path "..\..\kmp-examples\game-of-life-kmp-commonmain-k-perf" -CleanBuild $CleanBuild -GradleArgs @("-PkperfFlushEarly=true")
  $buildTimes = [ordered]@{}
  if ($timings.Contains('jvm')) { $buildTimes['commonmain-k-perf-flushEarlyTrue-jar'] = $timings.jvm }
  if ($timings.Contains('js')) { $buildTimes['commonmain-k-perf-flushEarlyTrue-node'] = $timings.js }
  if ($timings.Contains('windows')) { $buildTimes['commonmain-k-perf-flushEarlyTrue-exe'] = $timings.windows }
  Write-Host "game-of-life-kmp-commonmain-k-perf with flushEarly=true build completed successfully."
  return $buildTimes
}

function Build-GameOfLifeCommonMainKperfFlushEarlyFalse {
  param([bool]$CleanBuild = $true)
  
  Write-Host ""
  Write-Host "## Building game-of-life-kmp-commonmain-k-perf with -PkperfFlushEarly=false..."
  $timings = Invoke-KmpBuildWithTimings -Title "game-of-life-kmp-commonmain-k-perf (flushEarly=false)" -Path "..\..\kmp-examples\game-of-life-kmp-commonmain-k-perf" -CleanBuild $CleanBuild -GradleArgs @("-PkperfFlushEarly=false")
  $buildTimes = [ordered]@{}
  if ($timings.Contains('jvm')) { $buildTimes['commonmain-k-perf-flushEarlyFalse-jar'] = $timings.jvm }
  if ($timings.Contains('js')) { $buildTimes['commonmain-k-perf-flushEarlyFalse-node'] = $timings.js }
  if ($timings.Contains('windows')) { $buildTimes['commonmain-k-perf-flushEarlyFalse-exe'] = $timings.windows }
  Write-Host "game-of-life-kmp-commonmain-k-perf with flushEarly=false build completed successfully."
  return $buildTimes
}

function Build-GameOfLifeDedicatedMainKperfFlushEarlyTrue {
  param([bool]$CleanBuild = $true)
  
  Write-Host ""
  Write-Host "## Building game-of-life-kmp-dedicatedmain-k-perf with -PkperfFlushEarly=true..."
  $timings = Invoke-KmpBuildWithTimings -Title "game-of-life-kmp-dedicatedmain-k-perf (flushEarly=true)" -Path "..\..\kmp-examples\game-of-life-kmp-dedicatedmain-k-perf" -CleanBuild $CleanBuild -GradleArgs @("-PkperfFlushEarly=true")
  $buildTimes = [ordered]@{}
  if ($timings.Contains('jvm')) { $buildTimes['dedicatedmain-k-perf-flushEarlyTrue-jar'] = $timings.jvm }
  if ($timings.Contains('js')) { $buildTimes['dedicatedmain-k-perf-flushEarlyTrue-node'] = $timings.js }
  if ($timings.Contains('windows')) { $buildTimes['dedicatedmain-k-perf-flushEarlyTrue-exe'] = $timings.windows }
  Write-Host "game-of-life-kmp-dedicatedmain-k-perf with flushEarly=true build completed successfully."
  return $buildTimes
}

function Build-GameOfLifeDedicatedMainKperfFlushEarlyFalse {
  param([bool]$CleanBuild = $true)
  
  Write-Host ""
  Write-Host "## Building game-of-life-kmp-dedicatedmain-k-perf with -PkperfFlushEarly=false..."
  $timings = Invoke-KmpBuildWithTimings -Title "game-of-life-kmp-dedicatedmain-k-perf (flushEarly=false)" -Path "..\..\kmp-examples\game-of-life-kmp-dedicatedmain-k-perf" -CleanBuild $CleanBuild -GradleArgs @("-PkperfFlushEarly=false")
  $buildTimes = [ordered]@{}
  if ($timings.Contains('jvm')) { $buildTimes['dedicatedmain-k-perf-flushEarlyFalse-jar'] = $timings.jvm }
  if ($timings.Contains('js')) { $buildTimes['dedicatedmain-k-perf-flushEarlyFalse-node'] = $timings.js }
  if ($timings.Contains('windows')) { $buildTimes['dedicatedmain-k-perf-flushEarlyFalse-exe'] = $timings.windows }
  Write-Host "game-of-life-kmp-dedicatedmain-k-perf with flushEarly=false build completed successfully."
  return $buildTimes
}

function Build-GameOfLifeCommonMainIoa {
  param([bool]$CleanBuild = $true)
  
  Write-Host ""
  Write-Host "## Building game-of-life-kmp-commonmain-ioa application..."
  $timings = Invoke-KmpBuildWithTimings -Title "game-of-life-kmp-commonmain-ioa application" -Path "..\..\kmp-examples\game-of-life-kmp-commonmain-ioa" -CleanBuild $CleanBuild
  $buildTimes = [ordered]@{}
  if ($timings.Contains('jvm')) { $buildTimes['commonmain_ioa_jar'] = $timings.jvm }
  if ($timings.Contains('js')) { $buildTimes['commonmain_ioa_node'] = $timings.js }
  if ($timings.Contains('windows')) { $buildTimes['commonmain_ioa_exe'] = $timings.windows }
  Write-Host "game-of-life-kmp-commonmain-ioa build completed successfully."
  return $buildTimes
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
  & .\gradlew @GradleArgs $TaskName | Out-Host
  $taskEndTime = Get-Date
  if ($LASTEXITCODE -ne 0) {
    throw "$Title failed with exit code $LASTEXITCODE"
  }
  $taskDuration = ($taskEndTime - $taskStart).TotalMilliseconds

  Write-Host "$Title completed successfully in $([math]::Round($taskDuration, 2)) ms."
  return $taskDuration
}

function Invoke-KmpBuildWithTimings {
  param(
    [string]$Title,
    [string]$Path,
    [bool]$CleanBuild = $true,
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
    if ($CleanBuild) {
      & .\gradlew clean | Out-Host
      if ($LASTEXITCODE -ne 0) {
        throw "$Title clean failed with exit code $LASTEXITCODE"
      }
    }

    $taskList = & .\gradlew -q tasks --all | Out-String
    if ($LASTEXITCODE -ne 0) {
      throw "$Title task discovery failed with exit code $LASTEXITCODE"
    }

    $jvmTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("jvmJar", "compileKotlinJvm")
    $jsTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("jsProductionExecutableCompileSync", "jsProductionExecutableCompile", "jsNodeProductionExecutableCompileSync", "jsNodeProductionExecutableCompile", "jsBrowserProductionWebpack", "compileKotlinJs")
    $windowsTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableMingwX64", "linkDebugExecutableMingwX64")
    $linuxTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableLinuxX64", "linkDebugExecutableLinuxX64")
    $macTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableMacosX64", "linkDebugExecutableMacosX64", "linkReleaseExecutableMacosArm64", "linkDebugExecutableMacosArm64")
  
    $jvmDuration = Invoke-GradleTaskIfPresentTimed -TaskName $jvmTask -Title "$Title - JVM build" -GradleArgs $GradleArgs
    if ($null -ne $jvmDuration) { $timings.jvm = $jvmDuration }

    $jsDuration = Invoke-GradleTaskIfPresentTimed -TaskName $jsTask -Title "$Title - JS build" -GradleArgs $GradleArgs
    if ($null -ne $jsDuration) { $timings.js = $jsDuration }

    $windowsDuration = Invoke-GradleTaskIfPresentTimed -TaskName $windowsTask -Title "$Title - Windows build" -GradleArgs $GradleArgs
    if ($null -ne $windowsDuration) { $timings.windows = $windowsDuration }

    $linuxDuration = Invoke-GradleTaskIfPresentTimed -TaskName $linuxTask -Title "$Title - Linux build" -GradleArgs $GradleArgs
    if ($null -ne $linuxDuration) { $timings.linux = $linuxDuration }

    $macDuration = Invoke-GradleTaskIfPresentTimed -TaskName $macTask -Title "$Title - Mac build" -GradleArgs $GradleArgs
    if ($null -ne $macDuration) { $timings.mac = $macDuration }
  }
  finally {
    Pop-Location
  }

  Write-Host "$Title completed successfully."
  return $timings
}
