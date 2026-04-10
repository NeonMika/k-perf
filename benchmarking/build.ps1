# Build functions for benchmarking applications

. "$PSScriptRoot\utils.ps1"

# Use the correct gradlew wrapper for the current platform
$gradlewCmd = if ($IsWindows) { ".\gradlew" } else { "./gradlew" }

# Platform-specific native target and binary extension
$nativeTarget = if ($IsWindows) { "mingwX64" } elseif ($IsMacOS) { "macosX64" } else { "linuxX64" }
$nativeExt    = if ($IsWindows) { ".exe" }     else               { ".kexe" }

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
  [string]$Methods

  KPerfConfig([bool]$FlushEarly, [bool]$InstrumentPropertyAccessors, [bool]$TestKIR, [string]$Methods) {
    $this.FlushEarly = $FlushEarly
    $this.InstrumentPropertyAccessors = $InstrumentPropertyAccessors
    $this.TestKIR = $TestKIR
    $this.Methods = $Methods
  }
}

function Get-KPerfSuffix {
  param([KPerfConfig]$Config)

  return "flushEarly-$(if ($Config.FlushEarly) { 'true' } else { 'false' })-propAccessors-$(if ($Config.InstrumentPropertyAccessors) { 'true' } else { 'false' })-testKIR-$(if ($Config.TestKIR) { 'true' } else { 'false' })"
}

function Clean-KirHelperKit {
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "## Cleaning KIRHelperKit"
  Write-Host "=========================================="
  Push-Location "..\..\KIRHelperKit"
  try {
    & $gradlewCmd clean | Out-Host
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
  Write-Host "=========================================="
  Write-Host "## Cleaning k-perf (Kotlin compiler plugin)"
  Write-Host "=========================================="
  Push-Location "..\..\plugins\k-perf"
  try {
    & $gradlewCmd clean | Out-Host
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: k-perf clean failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
}

function Clean-InstrumentationOverheadAnalyzerPlugin {
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "## Cleaning instrumentation-overhead-analyzer (Kotlin compiler plugin)"
  Write-Host "=========================================="
  Push-Location "..\..\plugins\instrumentation-overhead-analyzer"
  try {
    & $gradlewCmd clean | Out-Host
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: instrumentation-overhead-analyzer clean failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
}

function Clean-GameOfLifeCommonMainReference {
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "## Cleaning game-of-life-kmp-commonmain reference application"
  Write-Host "=========================================="
  Push-Location "..\..\kmp-examples\game-of-life-kmp-commonmain"
  try {
    & $gradlewCmd clean | Out-Host
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: game-of-life-kmp-commonmain clean failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
}

function Clean-GameOfLifeCommonMainIoa {
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "## Cleaning game-of-life-kmp-commonmain-ioa application"
  Write-Host "=========================================="
  Push-Location "..\..\kmp-examples\game-of-life-kmp-commonmain-ioa"
  try {
    & $gradlewCmd clean | Out-Host
    if ($LASTEXITCODE -ne 0) {
      Write-Host "ERROR: game-of-life-kmp-commonmain-ioa clean failed!"
      exit 1
    }
  }
  finally {
    Pop-Location
  }
}

function Clean-GameOfLifeDedicatedMainReference {
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "## Cleaning game-of-life-kmp-dedicatedmain reference application"
  Write-Host "=========================================="
  Push-Location "..\..\kmp-examples\game-of-life-kmp-dedicatedmain"
  try {
    & $gradlewCmd clean | Out-Host
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
  Write-Host "=========================================="
  Write-Host "## Cleaning game-of-life-kmp-commonmain-k-perf"
  Write-Host "=========================================="
  Push-Location "..\..\kmp-examples\game-of-life-kmp-commonmain-k-perf"
  try {
    & $gradlewCmd clean | Out-Host
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
  Write-Host "=========================================="
  Write-Host "## Cleaning game-of-life-kmp-dedicatedmain-k-perf"
  Write-Host "=========================================="
  Push-Location "..\..\kmp-examples\game-of-life-kmp-dedicatedmain-k-perf"
  try {
    & $gradlewCmd clean | Out-Host
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
  Write-Host "=========================================="
  Write-Host "## Building KIRHelperKit"
  Write-Host "=========================================="
  Push-Location "..\..\KIRHelperKit"
  try {
    $buildStartTime = Get-Date
    & $gradlewCmd build publishToMavenLocal | Out-Host
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
  Write-Host "=========================================="
  Write-Host "## Building k-perf (Kotlin compiler plugin)"
  Write-Host "=========================================="
  Push-Location "..\..\plugins\k-perf"
  try {
    $buildStartTime = Get-Date
    & $gradlewCmd build publishToMavenLocal | Out-Host
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
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "## Building instrumentation-overhead-analyzer (Kotlin compiler plugin)"
  Write-Host "=========================================="
  Push-Location "..\..\plugins\instrumentation-overhead-analyzer"
  try {
    $buildStartTime = Get-Date
    & $gradlewCmd build publishToMavenLocal | Out-Host
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
  Write-Host "=========================================="
  Write-Host "## Building game-of-life-kmp-commonmain reference application (without plugin)"
  Write-Host "=========================================="
  $timings = Invoke-KmpBuildWithTimings -Title "game-of-life-kmp-commonmain reference application" -Path "..\..\kmp-examples\game-of-life-kmp-commonmain"
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
  if ($timings.Contains('linux')) {
    $buildTimes['commonmain-plain-exe'] = $timings.linux
    $buildTimes['commonmain_plain_exe'] = $timings.linux
  }
  if ($timings.Contains('mac')) {
    $buildTimes['commonmain-plain-exe'] = $timings.mac
    $buildTimes['commonmain_plain_exe'] = $timings.mac
  }
  Write-Host "game-of-life-kmp-commonmain reference application build completed successfully."
  return $buildTimes
}

function Build-GameOfLifeDedicatedMainReference {
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "## Building game-of-life-kmp-dedicatedmain reference application (without plugin)"
  Write-Host "=========================================="
  $timings = Invoke-KmpBuildWithTimings -Title "game-of-life-kmp-dedicatedmain reference application" -Path "..\..\kmp-examples\game-of-life-kmp-dedicatedmain"
  $buildTimes = [ordered]@{}
  if ($timings.Contains('jvm')) { $buildTimes['dedicatedmain-plain-jar'] = $timings.jvm }
  if ($timings.Contains('js')) { $buildTimes['dedicatedmain-plain-node'] = $timings.js }
  if ($timings.Contains('windows')) { $buildTimes['dedicatedmain-plain-exe'] = $timings.windows }
  if ($timings.Contains('linux')) { $buildTimes['dedicatedmain-plain-exe'] = $timings.linux }
  if ($timings.Contains('mac')) { $buildTimes['dedicatedmain-plain-exe'] = $timings.mac }
  Write-Host "game-of-life-kmp-dedicatedmain reference application build completed successfully."
  return $buildTimes
}

function Build-GameOfLifeCommonMainIoa {
  Write-Host ""
  Write-Host "=========================================="
  Write-Host "## Building game-of-life-kmp-commonmain-ioa application"
  Write-Host "=========================================="
  $timings = Invoke-KmpBuildWithTimings -Title "game-of-life-kmp-commonmain-ioa application" -Path "..\..\kmp-examples\game-of-life-kmp-commonmain-ioa"
  $buildTimes = [ordered]@{}
  if ($timings.Contains('jvm')) { $buildTimes['commonmain_ioa_jar'] = $timings.jvm }
  if ($timings.Contains('js')) { $buildTimes['commonmain_ioa_node'] = $timings.js }
  if ($timings.Contains('windows')) { $buildTimes['commonmain_ioa_exe'] = $timings.windows }
  if ($timings.Contains('linux')) { $buildTimes['commonmain_ioa_exe'] = $timings.linux }
  if ($timings.Contains('mac')) { $buildTimes['commonmain_ioa_exe'] = $timings.mac }
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
  & $gradlewCmd @GradleArgs $TaskName | Out-Host
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

    # Copy artifacts to dist/
    $projectName = Split-Path -Leaf (Get-Location)
    $distDir = "dist"
    New-Item -ItemType Directory -Path $distDir -Force | Out-Null

    # JVM: copy JAR + runtime deps from build/lib/
    if (Test-Path "build\lib") {
      Copy-Item -Path "build\lib\*" -Destination $distDir -Force
    }
    # JS: copy bundled .js file
    $jsFile = Get-ChildItem -Path "build\js\packages\$projectName\kotlin\$projectName.js" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($null -ne $jsFile) {
      Copy-Item -Path $jsFile.FullName -Destination $distDir -Force
    }
    # Native: copy binary for current platform
    $nativeBinary = "build\bin\$nativeTarget\releaseExecutable\$projectName$nativeExt"
    if (Test-Path $nativeBinary) {
      Copy-Item -Path $nativeBinary -Destination $distDir -Force
    }
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
    "-PkperfMethods=$($Config.Methods)"
  )
  
  Write-Host ""
  Write-Host "## Building $projectName with suffix: $suffix..."
  $title = "$projectName ($suffix)"
  $timings = Invoke-KmpBuildWithTimings -Title $title -Path $projectPath -GradleArgs $gradleArgs

  # Copy artifacts from dist/ into bin/<suffix>/ so multiple configs can coexist
  $binDir = Join-Path $projectPath "bin\$suffix"
  New-Item -ItemType Directory -Path $binDir -Force | Out-Null
  $distDir = Join-Path $projectPath "dist"
  if (Test-Path $distDir) {
    Copy-Item -Path "$distDir\*" -Destination $binDir -Force
  }

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
  if ($timings.Contains('linux')) { 
    $buildTimes["$gameTypeString-k-perf-$suffix-exe"] = $timings.linux 
  }
  if ($timings.Contains('mac')) { 
    $buildTimes["$gameTypeString-k-perf-$suffix-exe"] = $timings.mac 
  }
  
  Write-Host "$projectName build with $suffix completed successfully."
  return $buildTimes
}