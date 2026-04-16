# Build functions for benchmarking applications

. "$PSScriptRoot\gradle_utils.ps1"
. "$PSScriptRoot\types.ps1"

# Artifact version used in all built JAR/binary names
$artifactVersion = "0.2.1"

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
  if ($timings.Contains('jvm'))     { $buildTimes['commonmain-plain-jar']        = $timings.jvm }
  if ($timings.Contains('js'))      { $buildTimes['commonmain-plain-node']       = $timings.js }
  if ($timings.Contains('windows')) { $buildTimes['commonmain-plain-win-exe']    = $timings.windows }
  if ($timings.Contains('linux'))   { $buildTimes['commonmain-plain-linux-exe']  = $timings.linux }
  if ($timings.Contains('mac'))     { $buildTimes['commonmain-plain-mac-exe']    = $timings.mac }
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
  if ($timings.Contains('jvm'))     { $buildTimes['dedicatedmain-plain-jar']       = $timings.jvm }
  if ($timings.Contains('js'))      { $buildTimes['dedicatedmain-plain-node']      = $timings.js }
  if ($timings.Contains('windows')) { $buildTimes['dedicatedmain-plain-win-exe']   = $timings.windows }
  if ($timings.Contains('linux'))   { $buildTimes['dedicatedmain-plain-linux-exe'] = $timings.linux }
  if ($timings.Contains('mac'))     { $buildTimes['dedicatedmain-plain-mac-exe']   = $timings.mac }
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
  if ($timings.Contains('jvm'))     { $buildTimes['commonmain-ioa-jar']        = $timings.jvm }
  if ($timings.Contains('js'))      { $buildTimes['commonmain-ioa-node']       = $timings.js }
  if ($timings.Contains('windows')) { $buildTimes['commonmain-ioa-win-exe']    = $timings.windows }
  if ($timings.Contains('linux'))   { $buildTimes['commonmain-ioa-linux-exe']  = $timings.linux }
  if ($timings.Contains('mac'))     { $buildTimes['commonmain-ioa-mac-exe']    = $timings.mac }
  Write-Host "game-of-life-kmp-commonmain-ioa build completed successfully."
  return $buildTimes
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
    "-PkperfEnabled=$($Config.Enabled)"
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
  $gameTypeString = Get-GameTypeString -GameType $GameType

  if ($timings.Contains('jvm')) {
    $buildTimes["$gameTypeString-k-perf-$suffix-jar"] = $timings.jvm
  }
  if ($timings.Contains('js')) {
    $buildTimes["$gameTypeString-k-perf-$suffix-node"] = $timings.js
  }
  if ($timings.Contains('windows')) {
    $buildTimes["$gameTypeString-k-perf-$suffix-win-exe"] = $timings.windows
  }
  if ($timings.Contains('linux')) {
    $buildTimes["$gameTypeString-k-perf-$suffix-linux-exe"] = $timings.linux
  }
  if ($timings.Contains('mac')) {
    $buildTimes["$gameTypeString-k-perf-$suffix-mac-exe"] = $timings.mac
  }

  Write-Host "$projectName build with $suffix completed successfully."
  return $buildTimes
}

function Get-KPerfSuffix {
  param([KPerfConfig]$Config)

  return "enabled-$(if ($Config.Enabled) { 'true' } else { 'false' })-flushEarly-$(if ($Config.FlushEarly) { 'true' } else { 'false' })-propAccessors-$(if ($Config.InstrumentPropertyAccessors) { 'true' } else { 'false' })-testKIR-$(if ($Config.TestKIR) { 'true' } else { 'false' })"
}

function Invoke-GetExecutables {
  param(
    [GameType[]]$GameTypes,
    [KPerfConfig[]]$KPerfCombinations,
    [bool]$Reference,
    [bool]$JVM,
    [bool]$JS,
    [bool]$Native,
    [string]$NativeExt,
    [string]$ArtifactVersion
  )

  # Compute a platform label for native executable names so they match the build-time keys
  # produced by Build-GameOfLife*Reference and Build-GameOfLifeKPerfVariant.
  $nativePlatformLabel = if ($IsWindows) { "win" } elseif ($IsMacOS) { "mac" } else { "linux" }

  [BenchmarkExecutable[]]$executables = @()

  foreach ($GameType in $GameTypes) {
    $gameTypeString   = Get-GameTypeString -GameType $GameType
    $projectName      = if ($GameType -eq [GameType]::CommonMain) { "game-of-life-kmp-commonmain"        } else { "game-of-life-kmp-dedicatedmain" }
    $kPerfProjectName = if ($GameType -eq [GameType]::CommonMain) { "game-of-life-kmp-commonmain-k-perf" } else { "game-of-life-kmp-dedicatedmain-k-perf" }
    $plainRoot        = "..\..\kmp-examples\$projectName"
    $kPerfRoot        = "..\..\kmp-examples\$kPerfProjectName"

    if ($Reference -and $JVM) {
      $executables += [BenchmarkExecutable]::new(
        "$gameTypeString-plain-jar",
        "$plainRoot\dist\$projectName-jvm-$ArtifactVersion.jar",
        [ExecutableType]::Jar,
        $null
      )
    }

    if ($Reference -and $JS) {
      $executables += [BenchmarkExecutable]::new(
        "$gameTypeString-plain-node",
        "$plainRoot\dist\$projectName.js",
        [ExecutableType]::Node,
        $null
      )
    }

    if ($Reference -and $Native) {
      $executables += [BenchmarkExecutable]::new(
        "$gameTypeString-plain-$nativePlatformLabel-exe",
        "$plainRoot\dist\$projectName$NativeExt",
        [ExecutableType]::Exe,
        $null
      )
    }

    foreach ($config in $KPerfCombinations) {
      $suffix = Get-KPerfSuffix -Config $config

      if ($JVM) {
        $executables += [BenchmarkExecutable]::new(
          "$gameTypeString-k-perf-$suffix-jar",
          "$kPerfRoot\bin\$suffix\$kPerfProjectName-jvm-$ArtifactVersion.jar",
          [ExecutableType]::Jar,
          $config
        )
      }

      if ($JS) {
        $executables += [BenchmarkExecutable]::new(
          "$gameTypeString-k-perf-$suffix-node",
          "$kPerfRoot\bin\$suffix\$kPerfProjectName.js",
          [ExecutableType]::Node,
          $config
        )
      }

      if ($Native) {
        $executables += [BenchmarkExecutable]::new(
          "$gameTypeString-k-perf-$suffix-$nativePlatformLabel-exe",
          "$kPerfRoot\bin\$suffix\$kPerfProjectName$NativeExt",
          [ExecutableType]::Exe,
          $config
        )
      }
    }
  }

  return $executables
}
