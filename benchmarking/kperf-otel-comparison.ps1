param(
  [bool]$CleanBuild = $true,
  [int]$WarmupCount = 5,
  [int]$RunCount = 20
)

$ErrorActionPreference = "Stop"

$ScriptRoot = $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($ScriptRoot)) { $ScriptRoot = '.' }

# Rebuild $env:PATH from the registry so tools installed after this PowerShell
# (or its parent terminal) was started — e.g. node — are visible to the
# Invoke-WithTimeout cmd.exe child processes. Without this, a long-lived shell
# inherits a stale PATH and JS variants silently fail with "'node' is not
# recognized" even though node is installed.
$machinePath = [Environment]::GetEnvironmentVariable("PATH", "Machine")
$userPath    = [Environment]::GetEnvironmentVariable("PATH", "User")
$pathEntries = (@($machinePath, $userPath, $env:PATH) -join ';') -split ';' |
               Where-Object { $_ -and (Test-Path $_) } |
               Select-Object -Unique
$env:PATH    = $pathEntries -join ';'

. "$ScriptRoot\types.ps1"
. "$ScriptRoot\statistics_utils.ps1"

# Verify external prerequisites the script can't install itself. We catch the
# common causes of a long, frustrating "build runs for 20 minutes then fails"
# loop:
#   - Java/Node/Git not on PATH
#   - Docker installed but daemon not running (very common after a reboot)
#   - GitHub Packages credentials missing in ~/.gradle/gradle.properties
#     (required to fetch io.opentelemetry.kotlin.* from a private repo)
function Test-Prerequisites {
  $missing = @()

  # Native CLI probes use 2>&1 which under PS 5.1 + EAP=Stop wraps any stderr
  # line as a NativeCommandError; relax EAP locally just for the probes.
  $prevEap = $ErrorActionPreference
  $ErrorActionPreference = 'Continue'
  try {
    foreach ($tool in @(
        @{ Name = 'java';   Hint = 'Install JDK 17+ (e.g. Temurin) and ensure java is on PATH.' },
        @{ Name = 'node';   Hint = 'Install Node.js LTS and ensure node is on PATH.' },
        @{ Name = 'git';    Hint = 'Install Git for Windows and ensure git is on PATH.' },
        @{ Name = 'docker'; Hint = 'Install Docker Desktop and ensure docker is on PATH.' }
      )) {
      if (-not (Get-Command $tool.Name -ErrorAction SilentlyContinue)) {
        $missing += "  [missing] $($tool.Name): $($tool.Hint)"
      }
    }

    # Docker CLI present is not enough — `docker run` needs the daemon. `docker
    # info` is the cheapest probe that touches it.
    if (Get-Command docker -ErrorAction SilentlyContinue) {
      docker info --format '{{.ServerVersion}}' *> $null
      if ($LASTEXITCODE -ne 0) {
        $missing += "  [stopped] docker daemon: Start Docker Desktop and wait for it to finish initializing."
      }
    }
  }
  finally {
    $ErrorActionPreference = $prevEap
  }

  # GitHub Packages credentials. The build script consumes these via
  # project.property("GITHUB_USERNAME") / GITHUB_PASSWORD; without them, the
  # otlp-exporter / comparison-otel* projects fail to resolve dependencies.
  $gradleProps = Join-Path $env:USERPROFILE ".gradle\gradle.properties"
  if (-not (Test-Path $gradleProps)) {
    $missing += "  [missing] ${gradleProps}: Create it with GITHUB_USERNAME=<user> and GITHUB_PASSWORD=<PAT with read:packages scope>."
  }
  else {
    $propsContent = Get-Content $gradleProps -Raw
    if ($propsContent -notmatch '(?m)^\s*GITHUB_USERNAME\s*=') {
      $missing += "  [missing] GITHUB_USERNAME in ${gradleProps}"
    }
    if ($propsContent -notmatch '(?m)^\s*GITHUB_PASSWORD\s*=') {
      $missing += "  [missing] GITHUB_PASSWORD in ${gradleProps} (a GitHub PAT with read:packages scope)"
    }
  }

  if ($missing.Count -gt 0) {
    Write-Host ""
    Write-Host "Preflight failed: cannot run the comparison benchmark." -ForegroundColor Red
    foreach ($line in $missing) { Write-Host $line -ForegroundColor Red }
    Write-Host ""
    Write-Host "See benchmarking/README.md `"Comparison Benchmark (kperf-otel-comparison.ps1)`" for setup details."
    throw "Preflight failed"
  }
}

Test-Prerequisites

Push-Location "$ScriptRoot\.."



# Runs $Command via cmd.exe with a wall-clock timeout. If the process hasn't exited
# by $TimeoutSeconds, the entire process tree is killed via taskkill /t /f and the
# function returns a sentinel string so the caller's regex naturally fails to match,
# logging "Failed to parse time" instead of locking the whole benchmark.
#
# The taskkill invocation intentionally does its own stdout/stderr redirection
# *inside* cmd.exe (>nul 2>&1). In Windows PowerShell 5.1, `2>&1 | Out-Null`
# on a native exe wraps stderr lines as NativeCommandError records, which combined
# with $ErrorActionPreference='Stop' terminates the script even when we pipe to null.
# Redirecting inside cmd keeps PowerShell from seeing the stream at all.
function Invoke-WithTimeout {
  param(
    [string]$Command,
    [int]$TimeoutSeconds = 60
  )

  $psi = New-Object System.Diagnostics.ProcessStartInfo
  $psi.FileName = "cmd.exe"
  $psi.Arguments = "/c $Command 2>&1"
  $psi.RedirectStandardOutput = $true
  $psi.UseShellExecute = $false
  $psi.CreateNoWindow = $true
  $psi.WorkingDirectory = (Get-Location).Path

  $proc = [System.Diagnostics.Process]::Start($psi)
  $stdoutTask = $proc.StandardOutput.ReadToEndAsync()

  if (-not $proc.WaitForExit($TimeoutSeconds * 1000)) {
    $prevEap = $ErrorActionPreference
    $ErrorActionPreference = 'SilentlyContinue'
    try {
      & cmd.exe /c "taskkill /T /F /PID $($proc.Id) >nul 2>&1"
    } catch {}
    $ErrorActionPreference = $prevEap
    try { $proc.WaitForExit(2000) | Out-Null } catch {}
    return "[TIMEOUT after ${TimeoutSeconds}s]"
  }

  return $stdoutTask.Result
}

# Persist the raw stdout (or [TIMEOUT...] sentinel) of a run whose output couldn't
# be parsed into a number. Files land under <resultsDir>/failures/ so a subsequent
# run leaves behind exactly the bytes that caused the regex miss — making it easy
# to tell a node ENOENT, an exception trace, a hang killed at the wall-clock
# timeout, and a silent zero-byte exit apart.
function Save-FailureOutput {
  param(
    [string]$Phase,
    [string]$ExeName,
    [int]$Iteration,
    [string]$RawOutput
  )

  if (-not (Test-Path $failuresDir)) {
    New-Item -ItemType Directory -Path $failuresDir -Force | Out-Null
  }

  $safeName = ($ExeName -replace '[^A-Za-z0-9._-]+', '_').Trim('_')
  $filePath = Join-Path $failuresDir ("{0}-{1}-{2:D2}.txt" -f $safeName, $Phase, $Iteration)
  $RawOutput | Out-File -FilePath $filePath -Encoding utf8
}

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
  # Under PowerShell 5.1 with $ErrorActionPreference='Stop', any stderr line
  # from gradle (including benign Kotlin compiler warnings) is wrapped as a
  # NativeCommandError and terminates the script. Relax EAP locally — we still
  # detect real failures via $LASTEXITCODE below.
  $prevEap = $ErrorActionPreference
  $ErrorActionPreference = 'Continue'
  try {
    if ($CleanBuild -and -not $SkipClean) {
      & .\gradlew clean @Tasks
    }
    else {
      & .\gradlew @Tasks
    }

    if ($LASTEXITCODE -ne 0) {
      throw "Failed to build $Title"
    }
  }
  finally {
    $ErrorActionPreference = $prevEap
    Pop-Location
  }

  Write-Host "$Title built successfully."
}

Write-Host "=========================================="
Write-Host "Compiling Required Plugins and Dependencies"
Write-Host "=========================================="

Invoke-GradleBuild -Title "KIRHelperKit" -Path ".\KIRHelperKit" -Tasks @("publishToMavenLocal")
Invoke-GradleBuild -Title "k-perf plugin" -Path ".\plugins\k-perf" -Tasks @("publishToMavenLocal")

Invoke-GradleBuild -Title "OTel OTLP Exporter" -Path ".\otlp-exporter" -Tasks @("publishToMavenLocal")
Invoke-GradleBuild -Title "OTel Plugin Util" -Path ".\plugins\otel-plugin\util" -Tasks @("publishToMavenLocal")
Invoke-GradleBuild -Title "OTel Plugin" -Path ".\plugins\otel-plugin\plugin" -Tasks @("publishToMavenLocal")

Invoke-GradleBuild -Title "OTel OTLP Exporter (proto)" -Path ".\otlp-exporter-proto" -Tasks @("publishToMavenLocal")
Invoke-GradleBuild -Title "OTel Plugin Util (proto)" -Path ".\plugins\otel-plugin-proto\util" -Tasks @("publishToMavenLocal")
Invoke-GradleBuild -Title "OTel Plugin (proto)" -Path ".\plugins\otel-plugin-proto\plugin" -Tasks @("publishToMavenLocal")

Write-Host "=========================================="
Write-Host "Compiling Comparison Projects"
Write-Host "=========================================="

# Build all three targets for every comparison project: JVM jar, JS bundle, Windows native (mingwX64).
# linuxX64 targets stay declared in each build.gradle.kts for use on Linux hosts,
# but we don't link them here because the Windows toolchain can't produce Linux binaries.
# `kotlinNpmInstall` is required because `jsProductionExecutableCompileSync`
# does NOT transitively trigger it: the synced bundle only contains the
# Kotlin-emitted .js files, not the runtime npm packages it requires
# (e.g. @js-joda/core for kotlinx-datetime). Without npm install, build/js/
# has no node_modules and `node ...comparison-*.js` crashes with
# "Cannot find module '@js-joda/core'".
Invoke-GradleBuild -Title "Comparison Project (k-perf)" -Path ".\kmp-examples\comparison-k-perf" -Tasks @("jvmJar", "kotlinNpmInstall", "jsProductionExecutableCompileSync", "linkReleaseExecutableMingwX64") -SkipClean $true
Invoke-GradleBuild -Title "Comparison Project (otel)" -Path ".\kmp-examples\comparison-otel" -Tasks @("jvmJar", "kotlinNpmInstall", "jsProductionExecutableCompileSync", "linkReleaseExecutableMingwX64") -SkipClean $true
Invoke-GradleBuild -Title "Comparison Project (otel-proto)" -Path ".\kmp-examples\comparison-otel-proto" -Tasks @("jvmJar", "kotlinNpmInstall", "jsProductionExecutableCompileSync", "linkReleaseExecutableMingwX64") -SkipClean $true


# Path resolving for execution
$kperfJvm = "java -jar .\kmp-examples\comparison-k-perf\build\lib\comparison-k-perf-jvm-0.1.0-flushEarly-true.jar"
$kperfJs = "node .\kmp-examples\comparison-k-perf\build\js\packages\comparison-k-perf-flushEarly-true\kotlin\comparison-k-perf-flushEarly-true.js"
$kperfNative = ".\kmp-examples\comparison-k-perf\build\bin\mingwX64\releaseExecutable\comparison-k-perf-flushEarly-true.exe"

$otelJvm = "java -jar .\kmp-examples\comparison-otel\build\lib\comparison-otel-jvm-1.0.0.jar"
$otelJs = "node .\kmp-examples\comparison-otel\build\js\packages\comparison-otel\kotlin\comparison-otel.js"
$otelNative = ".\kmp-examples\comparison-otel\build\bin\mingwX64\releaseExecutable\main.exe"

$otelProtoJvm = "java -jar .\kmp-examples\comparison-otel-proto\build\lib\comparison-otel-proto-jvm-1.0.0.jar"
$otelProtoJs = "node .\kmp-examples\comparison-otel-proto\build\js\packages\comparison-otel-proto\kotlin\comparison-otel-proto.js"
$otelProtoNative = ".\kmp-examples\comparison-otel-proto\build\bin\mingwX64\releaseExecutable\main.exe"

$executables = @(
  @{ Name = "k-perf JVM"; Command = $kperfJvm },
  @{ Name = "otel JVM"; Command = $otelJvm },
  @{ Name = "otel-proto JVM"; Command = $otelProtoJvm },
  @{ Name = "k-perf JS (Node)"; Command = $kperfJs },
  @{ Name = "otel JS (Node)"; Command = $otelJs },
  @{ Name = "otel-proto JS (Node)"; Command = $otelProtoJs },
  @{ Name = "k-perf Native (Win)"; Command = $kperfNative },
  @{ Name = "otel Native (Win)"; Command = $otelNative },
  @{ Name = "otel-proto Native (Win)"; Command = $otelProtoNative }
)

Write-Host "=========================================="
Write-Host "Running Measurements"
Write-Host "Warmups: $WarmupCount | Runs: $RunCount"
Write-Host "=========================================="

$allResults = @()

# Compute the results directory up-front so we can drop per-run debug dumps
# under it the moment a run fails (instead of waiting until the loop ends).
$timestamp = Get-Date -Format "yyyy_MM_dd_HH_mm_ss"
$resultsDir = ".\measurements\comparison_run_$timestamp"
$failuresDir = Join-Path $resultsDir "failures"

$otelConfigPath = (Resolve-Path "$ScriptRoot\otel-config.yaml").Path

# All docker invocations below run with EAP=Continue locally because under
# PowerShell 5.1 + $ErrorActionPreference='Stop', any stderr line from a native
# command (e.g. "Error response from daemon: No such container: otel-collector"
# when starting a not-yet-created container) is wrapped as a NativeCommandError
# and throws — even when the stream is redirected to Out-Null. A `try/catch`
# would silently swallow that throw and skip the very next `docker run` call,
# leaving the benchmark to run with no collector and the otel-proto native exe
# hanging on gRPC for the 60s timeout. We use $LASTEXITCODE to detect failures
# instead.
function Invoke-Docker {
  param([string[]]$DockerArgs, [switch]$CaptureOutput)
  $prevEap = $ErrorActionPreference
  $ErrorActionPreference = 'Continue'
  try {
    if ($CaptureOutput) {
      return (& docker @DockerArgs 2>&1)
    }
    else {
      & docker @DockerArgs *> $null
    }
  }
  finally {
    $ErrorActionPreference = $prevEap
  }
}

# Stop any stale Jaeger container from previous runs so it doesn't hold ports
# 4317/4318 when we try to start otel-collector. Silent if it doesn't exist.
Invoke-Docker -DockerArgs @('stop', 'jaeger')

foreach ($exe in $executables) {
  Write-Host ""

  # Boot or stop the OTel Collector depending on whether this executable exports traces.
  # We use otel/opentelemetry-collector-contrib (not Jaeger all-in-one) because the
  # latter's OTLP gRPC endpoint does not bind reliably to 0.0.0.0 under Docker Desktop
  # for Windows, which causes kmpgrpc-native (tonic via FFI) gRPC calls from the proto
  # exporter to hang indefinitely. The explicit 0.0.0.0 binding in otel-config.yaml
  # matches the known-working sidequest setup.
  if ($exe.Name -match "otel") {
    Write-Host "--- Booting OTel Collector via Docker ---"
    Invoke-Docker -DockerArgs @('start', 'otel-collector')
    if ($LASTEXITCODE -ne 0) {
      # No existing container — create one. If a stale container of the same name
      # exists in any state, blow it away first so `docker run` doesn't fail with
      # "name already in use" (which used to be silently caught and leave us with
      # no collector running).
      Invoke-Docker -DockerArgs @('rm', '-f', 'otel-collector')
      Invoke-Docker -DockerArgs @(
        'run', '-d', '--name', 'otel-collector',
        '-p', '4317:4317', '-p', '4318:4318',
        '-v', "${otelConfigPath}:/etc/otel-collector-config.yaml",
        'otel/opentelemetry-collector-contrib:latest',
        '--config=/etc/otel-collector-config.yaml'
      )
      if ($LASTEXITCODE -ne 0) {
        throw "Failed to start otel-collector container (docker run exited $LASTEXITCODE). Check that ports 4317/4318 are free and Docker Desktop is healthy."
      }
    }

    # Wait until the collector is actually accepting connections on :4317. The
    # old fixed 2s sleep was a guess; on slower machines or first-pull boots the
    # gRPC port is not yet bound when the workload exe starts, and the kmpgrpc
    # native (tonic FFI) client hangs the whole 60s wall-clock timeout instead
    # of failing fast.
    $deadline = (Get-Date).AddSeconds(30)
    $ready = $false
    while ((Get-Date) -lt $deadline) {
      $probe = Test-NetConnection -ComputerName '127.0.0.1' -Port 4317 -WarningAction SilentlyContinue -InformationLevel Quiet
      if ($probe) { $ready = $true; break }
      Start-Sleep -Milliseconds 500
    }
    if (-not $ready) {
      Write-Host "otel-collector logs:" -ForegroundColor Yellow
      Invoke-Docker -DockerArgs @('logs', '--tail', '40', 'otel-collector') -CaptureOutput | ForEach-Object { Write-Host $_ }
      throw "otel-collector did not start listening on :4317 within 30s."
    }
  }
  else {
    Write-Host "--- Stopping active OTel Collector to prevent caching/CPU interference for baseline metrics ---"
    Invoke-Docker -DockerArgs @('stop', 'otel-collector')
  }

  Write-Host "--- Benchmarking: $($exe.Name) ---"
  
  # Warmup
  Write-Host "Warmup iterations ($WarmupCount):"
  for ($i = 0; $i -lt $WarmupCount; $i++) {
    $output = Invoke-WithTimeout -Command $exe.Command -TimeoutSeconds 60
    
    # Delete k-perf trace/symbol output files automatically
    Get-ChildItem -Path "." -Filter "trace*.txt" -ErrorAction SilentlyContinue | Remove-Item -Force
    Get-ChildItem -Path "." -Filter "symbol*.txt" -ErrorAction SilentlyContinue | Remove-Item -Force

    $outputStr = $output -join "`n"
    $flushTime = [regex]::Match($outputStr, "Flush finished - (\d+) ms elapsed")
    $execTime = [regex]::Match($outputStr, "Execution finished - (\d+) ms elapsed")
    
    if ($flushTime.Success) {
      Write-Host "  Warmup $($i+1): $($flushTime.Groups[1].Value) ms (Included async flush)"
    }
    elseif ($execTime.Success) {
      Write-Host "  Warmup $($i+1): $($execTime.Groups[1].Value) ms"
    }
    else {
      Write-Host "  Warmup $($i+1): Failed to parse time"
      Save-FailureOutput -Phase "warmup" -ExeName $exe.Name -Iteration ($i + 1) -RawOutput $outputStr
    }
  }

  # Actual measurements
  $runtimes = @()
  Write-Host "Measurement iterations ($RunCount):"
  for ($i = 0; $i -lt $RunCount; $i++) {
    $output = Invoke-WithTimeout -Command $exe.Command -TimeoutSeconds 60
    
    # Delete k-perf trace/symbol output files automatically
    Get-ChildItem -Path "." -Filter "trace*.txt" -ErrorAction SilentlyContinue | Remove-Item -Force
    Get-ChildItem -Path "." -Filter "symbol*.txt" -ErrorAction SilentlyContinue | Remove-Item -Force

    $outputStr = $output -join "`n"
    $flushTime = [regex]::Match($outputStr, "Flush finished - (\d+) ms elapsed")
    $execTime = [regex]::Match($outputStr, "Execution finished - (\d+) ms elapsed")
    
    if ($flushTime.Success) {
      $ms = [int]$flushTime.Groups[1].Value
      Write-Host "  Run $($i+1): $ms ms (Included async flush)" -ForegroundColor Green
      $runtimes += $ms
    }
    elseif ($execTime.Success) {
      $ms = [int]$execTime.Groups[1].Value
      Write-Host "  Run $($i+1): $ms ms" -ForegroundColor Green
      $runtimes += $ms
    }
    else {
      Write-Host "  Run $($i+1): Failed to parse time" -ForegroundColor Red
      Save-FailureOutput -Phase "run" -ExeName $exe.Name -Iteration ($i + 1) -RawOutput $outputStr
    }
  }

  # Statistical computations
  $numericTimes = $runtimes | ForEach-Object { [double]$_ }
  $stats = Get-BenchmarkStatistics -Values $numericTimes

  $allResults += [ordered]@{
    Executable = $exe.Name
    Count      = $stats.count
    Mean       = $stats.mean
    Median     = $stats.median
    StdDev     = $stats.stddev
    Min        = $stats.min
    Max        = $stats.max
    CI95Upper  = $stats.ci95.upper
    Times      = $numericTimes
  }
}

Write-Host ""
Write-Host "=========================================="
Write-Host "Processing Results & System Info"
Write-Host "=========================================="

$machineInfo = Get-MachineInfo -GradleProjectPath ".\kmp-examples\comparison-k-perf"

if (-Not (Test-Path $resultsDir)) {
  New-Item -ItemType Directory -Path $resultsDir -Force | Out-Null
}

$jsonOutput = [ordered]@{
  Parameters  = @{ WarmupCount = $WarmupCount; RunCount = $RunCount; CleanBuild = $CleanBuild }
  MachineInfo = $machineInfo
  Results     = $allResults
}

$jsonFile = "$resultsDir\results.json"
$mdFile = "$resultsDir\results.md"

$jsonOutput | ConvertTo-Json -Depth 6 | Out-File $jsonFile -Encoding utf8

# Generate Markdown file
$markdown = @"
# Benchmark Results ($timestamp)

## Parameters
- **Warmup Iterations:** $WarmupCount
- **Run Iterations:** $RunCount
- **Clean Build:** $CleanBuild

## System Information
- **OS:** $($machineInfo.OS) $($machineInfo.OSArchitecture)
- **CPU:** $($machineInfo.CPU) ($($machineInfo.CPUCores) Cores / $($machineInfo.CPULogicalProcessors) Logical Processors)
- **RAM:** $($machineInfo.TotalRAMGB) GB
- **Java Version:** $($machineInfo.JavaVersion) ($($machineInfo.JavaDistribution))
- **Node Version:** $($machineInfo.NodeVersion)

## Hardware Overview Details
- **Device:** $($machineInfo.DeviceManufacturer) - $($machineInfo.DeviceModel)
- **Git Branch:** $($machineInfo.GitBranch)

## Execution Summary

| Executable | Iterations | Mean (ms) | Median (ms) | Min (ms) | Max (ms) | StdDev |
|------------|------------|-----------|-------------|----------|----------|--------|
"@

foreach ($res in $allResults) {
  if ($null -ne $res.Mean) { $meanStr = "{0:N2}" -f $res.Mean } else { $meanStr = "N/A" }
  if ($null -ne $res.Median) { $medStr = "{0:N2}" -f $res.Median } else { $medStr = "N/A" }
  if ($null -ne $res.Min) { $minStr = "{0:N0}" -f $res.Min } else { $minStr = "N/A" }
  if ($null -ne $res.Max) { $maxStr = "{0:N0}" -f $res.Max } else { $maxStr = "N/A" }
  if ($null -ne $res.StdDev) { $stdStr = "{0:N2}" -f $res.StdDev } else { $stdStr = "N/A" }

  $markdown += "`n| $($res.Executable) | $($res.Count) | {0} | {1} | {2} | {3} | {4} |" -f $meanStr, $medStr, $minStr, $maxStr, $stdStr
}

$markdown | Out-File $mdFile -Encoding utf8

Write-Host "Measurements and stats saved successfully to folder: `n -> $resultsDir"
Write-Host "Benchmark evaluation finished."
Pop-Location