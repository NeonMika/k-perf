# Build functions for benchmarking applications

. "$PSScriptRoot\utils.ps1"

# Define GameType enum
enum GameType {
  CommonMain
  DedicatedMain
}
# Map GameType enum values to their string representations and tags
$gameTypeTagMap = @{
  [GameType]::CommonMain    = "common"
  [GameType]::DedicatedMain = "dedicated"
}

# Map GameType enum values to their string representations
$gameTypeStringMap = @{
  [GameType]::CommonMain    = "commonmain"
  [GameType]::DedicatedMain = "dedicatedmain"
}

class KPerfConfig {
  [bool]$FlushEarly
  [bool]$InstrumentPropertyAccessors
  [bool]$TestKIR

  KPerfConfig([bool]$FlushEarly, [bool]$InstrumentPropertyAccessors, [bool]$TestKIR) {
    $this.FlushEarly = $FlushEarly
    $this.InstrumentPropertyAccessors = $InstrumentPropertyAccessors
    $this.TestKIR = $TestKIR
  }
}

function Get-KPerfSuffix {
  param([KPerfConfig]$Config)

  return "flushEarly-$(if ($Config.FlushEarly) { 'true' } else { 'false' })-propAccessors-$(if ($Config.InstrumentPropertyAccessors) { 'true' } else { 'false' })-testKIR-$(if ($Config.TestKIR) { 'true' } else { 'false' })"
}

function Clean-KirHelperKit {
  Write-Host ""
  Write-Host "## Cleaning KIRHelperKit..."
  Push-Location "..\..\KIRHelperKit"
  try {
    & .\gradlew clean | Out-Host
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: KIRHelperKit clean failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
}

function Clean-KPerfPlugin {
  Write-Host ""
  Write-Host "## Cleaning k-perf (Kotlin compiler plugin)..."
  Push-Location "..\..\plugins\k-perf"
  try {
    & .\gradlew clean | Out-Host
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: k-perf clean failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
}

function Clean-GameOfLifeCommonMainReference {
  Write-Host ""
  Write-Host "## Cleaning game-of-life-kmp-commonmain reference application..."
  Push-Location "..\..\kmp-examples\game-of-life-kmp-commonmain"
  try {
    & .\gradlew clean | Out-Host
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: game-of-life-kmp-commonmain clean failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
}

function Clean-GameOfLifeDedicatedMainReference {
  Write-Host ""
  Write-Host "## Cleaning game-of-life-kmp-dedicatedmain reference application..."
  Push-Location "..\..\kmp-examples\game-of-life-kmp-dedicatedmain"
  try {
    & .\gradlew clean | Out-Host
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: game-of-life-kmp-dedicatedmain clean failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
}

function Clean-GameOfLifeCommonKPerfVariant {
  Write-Host ""
  Write-Host "## Cleaning game-of-life-kmp-commonmain-k-perf..."
  Push-Location "..\..\kmp-examples\game-of-life-kmp-commonmain-k-perf"
  try {
    & .\gradlew clean | Out-Host
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: game-of-life-kmp-commonmain-k-perf clean failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
}

function Clean-GameOfLifeDedicatedKPerfVariant {
  Write-Host ""
  Write-Host "## Cleaning game-of-life-kmp-dedicatedmain-k-perf..."
  Push-Location "..\..\kmp-examples\game-of-life-kmp-dedicatedmain-k-perf"
  try {
    & .\gradlew clean | Out-Host
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: game-of-life-kmp-dedicatedmain-k-perf clean failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
}

function Build-KirHelperKit {
  Write-Host ""
  Write-Host "## Building KIRHelperKit..."
  Push-Location "..\..\KIRHelperKit"
  try {
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

function Build-KPerfPlugin {
  Write-Host ""
  Write-Host "## Building k-perf (Kotlin compiler plugin)..."
  Push-Location "..\..\plugins\k-perf"
  try {
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
  return [ordered]@{ KPerfPlugin = $buildDuration }
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
  Write-Host ""
  Write-Host "## Building game-of-life-kmp-commonmain reference application (without plugin)..."
  $timings = Invoke-KmpBuildWithTimings -Title "game-of-life-kmp-commonmain reference application" -Path "..\..\kmp-examples\game-of-life-kmp-commonmain" -CleanBuild $false
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
  Write-Host ""
  Write-Host "## Building game-of-life-kmp-dedicatedmain reference application (without plugin)..."
  $timings = Invoke-KmpBuildWithTimings -Title "game-of-life-kmp-dedicatedmain reference application" -Path "..\..\kmp-examples\game-of-life-kmp-dedicatedmain" -CleanBuild $false
  $buildTimes = [ordered]@{}
  if ($timings.Contains('jvm')) { $buildTimes['dedicatedmain-plain-jar'] = $timings.jvm }
  if ($timings.Contains('js')) { $buildTimes['dedicatedmain-plain-node'] = $timings.js }
  if ($timings.Contains('windows')) { $buildTimes['dedicatedmain-plain-exe'] = $timings.windows }
  Write-Host "game-of-life-kmp-dedicatedmain reference application build completed successfully."
  return $buildTimes
}

function Build-GameOfLifeCommonMainKPerfFlushEarlyTrue {
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

function Build-GameOfLifeCommonMainKPerfFlushEarlyFalse {
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

function Build-GameOfLifeDedicatedMainKPerfFlushEarlyTrue {
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

function Build-GameOfLifeDedicatedMainKPerfFlushEarlyFalse {
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

function Build-GameOfLifeKPerfVariant {
  param(
    [GameType]$GameType,
    [KPerfConfig]$Config
  )
  
  $projectName = if ($GameType -eq [GameType]::CommonMain) { "game-of-life-kmp-commonmain-k-perf" } else { "game-of-life-kmp-dedicatedmain-k-perf" }
  $projectPath = if ($GameType -eq [GameType]::CommonMain) { "..\..\kmp-examples\game-of-life-kmp-commonmain-k-perf" } else { "..\..\kmp-examples\game-of-life-kmp-dedicatedmain-k-perf" }
  $suffix = Get-KPerfSuffix -Config $Config
  $gradleArgs = @(
    "-PkperfFlushEarly=$($Config.FlushEarly)"
    "-PkperfInstrumentPropertyAccessors=$($Config.InstrumentPropertyAccessors)"
    "-PkperfTestKIR=$($Config.TestKIR)"
  )
  
  Write-Host ""
  Write-Host "## Building $projectName with suffix: $suffix..."
  $title = "$projectName ($suffix)"
  $timings = Invoke-KmpBuildWithTimings -Title $title -Path $projectPath -CleanBuild $false -GradleArgs $gradleArgs
  
  $buildTimes = [ordered]@{}
  $gameTypeString = $gameTypeStringMap[$GameType]
  
  if ($timings.Contains('jvm')) { 
    $buildTimes["$gameTypeString-k-perf-$suffix-jar"] = $timings.jvm 
  }
  if ($timings.Contains('js')) { 
    $buildTimes["$gameTypeString-k-perf-$suffix-node"] = $timings.js 
  }
  if ($timings.Contains('windows')) { 
    $buildTimes["$gameTypeString-k-perf-$suffix-exe"] = $timings.windows 
  }
  
  Write-Host "$projectName build with $suffix completed successfully."
  return $buildTimes
}