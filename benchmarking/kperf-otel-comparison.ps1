param(
  [bool]$CleanBuild = $true,
  [int]$WarmupCount = 0,
  [int]$RunCount = 20,
  [int]$StepCount = 150
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

# Verify external prerequisites the script can't install itself.
function Test-Prerequisites {
  $missing = @()

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

# Per-step wall-clock budget scales with StepCount so long otel-JS runs at high
# StepCount don't blow the timeout. Floor at 60s for tiny smoke runs.
$RunTimeoutSeconds = [Math]::Max(60, 5 * $StepCount)

# Runs $Command via cmd.exe with a wall-clock timeout. See original commentary.
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

# Extract (variant, platform) from an exe display name. Used for grouping the
# overhead table and looking up per-platform method counts.
function Get-VariantAndPlatform {
  param([string]$ExeName)
  $platform =
    if     ($ExeName -match 'JVM$')             { 'JVM' }
    elseif ($ExeName -match 'JS \(Node\)$')     { 'JS' }
    elseif ($ExeName -match 'Native \(Win\)$')  { 'Native' }
    else                                         { 'Unknown' }
  $variant = $ExeName -replace ' (JVM|JS \(Node\)|Native \(Win\))$', ''
  return @{ Variant = $variant; Platform = $platform }
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

Invoke-GradleBuild -Title "OTel Plugin (proto-timesource)" -Path ".\plugins\otel-plugin-proto-timesource\plugin" -Tasks @("publishToMavenLocal")
Invoke-GradleBuild -Title "OTel Plugin (proto-anchored)" -Path ".\plugins\otel-plugin-proto-anchored\plugin" -Tasks @("publishToMavenLocal")

Write-Host "=========================================="
Write-Host "Compiling Comparison Projects"
Write-Host "=========================================="

Invoke-GradleBuild -Title "Comparison Project (baseline)" -Path ".\kmp-examples\comparison-baseline" -Tasks @("jvmJar", "kotlinNpmInstall", "jsProductionExecutableCompileSync", "linkReleaseExecutableMingwX64") -SkipClean $true
Invoke-GradleBuild -Title "Comparison Project (k-perf)" -Path ".\kmp-examples\comparison-k-perf" -Tasks @("jvmJar", "kotlinNpmInstall", "jsProductionExecutableCompileSync", "linkReleaseExecutableMingwX64") -SkipClean $true
Invoke-GradleBuild -Title "Comparison Project (otel)" -Path ".\kmp-examples\comparison-otel" -Tasks @("jvmJar", "kotlinNpmInstall", "jsProductionExecutableCompileSync", "linkReleaseExecutableMingwX64") -SkipClean $true
Invoke-GradleBuild -Title "Comparison Project (otel-proto)" -Path ".\kmp-examples\comparison-otel-proto" -Tasks @("jvmJar", "kotlinNpmInstall", "jsProductionExecutableCompileSync", "linkReleaseExecutableMingwX64") -SkipClean $true
Invoke-GradleBuild -Title "Comparison Project (otel-proto-timesource)" -Path ".\kmp-examples\comparison-otel-proto-timesource" -Tasks @("jvmJar", "kotlinNpmInstall", "jsProductionExecutableCompileSync", "linkReleaseExecutableMingwX64") -SkipClean $true
Invoke-GradleBuild -Title "Comparison Project (otel-proto-anchored)" -Path ".\kmp-examples\comparison-otel-proto-anchored" -Tasks @("jvmJar", "kotlinNpmInstall", "jsProductionExecutableCompileSync", "linkReleaseExecutableMingwX64") -SkipClean $true


# Path resolving for execution. StepCount is appended at run time as the
# positional argv[0] each Main.kt reads.
$baselineJvm    = "java -jar .\kmp-examples\comparison-baseline\build\lib\comparison-baseline-jvm-0.1.0.jar"
$baselineJs     = "node .\kmp-examples\comparison-baseline\build\js\packages\comparison-baseline\kotlin\comparison-baseline.js"
$baselineNative = ".\kmp-examples\comparison-baseline\build\bin\mingwX64\releaseExecutable\comparison-baseline.exe"

$kperfJvm    = "java -jar .\kmp-examples\comparison-k-perf\build\lib\comparison-k-perf-jvm-0.1.0-flushEarly-false.jar"
$kperfJs     = "node .\kmp-examples\comparison-k-perf\build\js\packages\comparison-k-perf-flushEarly-false\kotlin\comparison-k-perf-flushEarly-false.js"
$kperfNative = ".\kmp-examples\comparison-k-perf\build\bin\mingwX64\releaseExecutable\comparison-k-perf-flushEarly-false.exe"

$otelJvm    = "java -jar .\kmp-examples\comparison-otel\build\lib\comparison-otel-jvm-1.0.0.jar"
$otelJs     = "node .\kmp-examples\comparison-otel\build\js\packages\comparison-otel\kotlin\comparison-otel.js"
$otelNative = ".\kmp-examples\comparison-otel\build\bin\mingwX64\releaseExecutable\main.exe"

$otelProtoJvm    = "java -jar .\kmp-examples\comparison-otel-proto\build\lib\comparison-otel-proto-jvm-1.0.0.jar"
$otelProtoJs     = "node .\kmp-examples\comparison-otel-proto\build\js\packages\comparison-otel-proto\kotlin\comparison-otel-proto.js"
$otelProtoNative = ".\kmp-examples\comparison-otel-proto\build\bin\mingwX64\releaseExecutable\main.exe"

$otelProtoTsJvm    = "java -jar .\kmp-examples\comparison-otel-proto-timesource\build\lib\comparison-otel-proto-timesource-jvm-1.0.0.jar"
$otelProtoTsJs     = "node .\kmp-examples\comparison-otel-proto-timesource\build\js\packages\comparison-otel-proto-timesource\kotlin\comparison-otel-proto-timesource.js"
$otelProtoTsNative = ".\kmp-examples\comparison-otel-proto-timesource\build\bin\mingwX64\releaseExecutable\main.exe"

$otelProtoAnchoredJvm    = "java -jar .\kmp-examples\comparison-otel-proto-anchored\build\lib\comparison-otel-proto-anchored-jvm-1.0.0.jar"
$otelProtoAnchoredJs     = "node .\kmp-examples\comparison-otel-proto-anchored\build\js\packages\comparison-otel-proto-anchored\kotlin\comparison-otel-proto-anchored.js"
$otelProtoAnchoredNative = ".\kmp-examples\comparison-otel-proto-anchored\build\bin\mingwX64\releaseExecutable\main.exe"

$executables = @(
  @{ Name = "baseline JVM";                       Command = $baselineJvm },
  @{ Name = "k-perf JVM";                         Command = $kperfJvm },
  @{ Name = "otel JVM";                           Command = $otelJvm },
  @{ Name = "otel-proto JVM";                     Command = $otelProtoJvm },
  @{ Name = "otel-proto-timesource JVM";          Command = $otelProtoTsJvm },
  @{ Name = "otel-proto-anchored JVM";            Command = $otelProtoAnchoredJvm },
  @{ Name = "baseline JS (Node)";                 Command = $baselineJs },
  @{ Name = "k-perf JS (Node)";                   Command = $kperfJs },
  @{ Name = "otel JS (Node)";                     Command = $otelJs },
  @{ Name = "otel-proto JS (Node)";               Command = $otelProtoJs },
  @{ Name = "otel-proto-timesource JS (Node)";    Command = $otelProtoTsJs },
  @{ Name = "otel-proto-anchored JS (Node)";      Command = $otelProtoAnchoredJs },
  @{ Name = "baseline Native (Win)";              Command = $baselineNative },
  @{ Name = "k-perf Native (Win)";                Command = $kperfNative },
  @{ Name = "otel Native (Win)";                  Command = $otelNative },
  @{ Name = "otel-proto Native (Win)";            Command = $otelProtoNative },
  @{ Name = "otel-proto-timesource Native (Win)"; Command = $otelProtoTsNative },
  @{ Name = "otel-proto-anchored Native (Win)";   Command = $otelProtoAnchoredNative }
)

Write-Host "=========================================="
Write-Host "Running Measurements"
Write-Host "Warmups: $WarmupCount | Runs: $RunCount | StepCount: $StepCount | Run timeout: ${RunTimeoutSeconds}s"
Write-Host "=========================================="

$allResults = @()

# Compute the results directory up-front so we can drop per-run debug dumps
# under it the moment a run fails.
$timestamp = Get-Date -Format "yyyy_MM_dd_HH_mm_ss"
$resultsDir = ".\measurements\comparison_run_$timestamp"
$failuresDir = Join-Path $resultsDir "failures"
$tracesDir   = Join-Path $resultsDir "traces"

# methods_per_step keyed by platform name ("JVM"/"JS"/"Native"). Filled in on the
# first k-perf measurement iteration for each platform; used later for overhead.
$methodsPerStep = @{}
$tracePreserved = @{}

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


# Clean up any stale containers from previous runs. Jaeger (all-in-one) is the
# only container used now — it accepts OTLP/gRPC on :4317 and OTLP/HTTP on
# :4318 directly (since Jaeger 1.49), so the previous otel-collector hop is
# redundant. Jaeger is started/stopped per-variant below so baseline and
# k-perf rows don't see background CPU from an idle Jaeger container.
Invoke-Docker -DockerArgs @('rm', '-f', 'otel-collector', 'jaeger')
Write-Host "Jaeger UI will boot on demand at http://localhost:16686 once an otel-* variant runs." -ForegroundColor Cyan

# Parse `### Elapsed time: <ns>` (total) and `!!! Elapsed time <i>: <ns>` (per-step).
# Main.kt emits nanoseconds via `Duration.inWholeNanoseconds` — this picks up the
# ~100 ns resolution of the underlying QPC/hrtime clocks and avoids the µs-truncation
# that previously made Native baseline reports collapse to 0. Returns
# @{ TotalNanos = <long?>; StepNanos = <double[]> }; TotalNanos is null when the
# regex didn't match.
function Get-ElapsedFromOutput {
  param([string]$OutputStr)

  $totalMatch = [regex]::Match($OutputStr, '(?m)^### Elapsed time:\s*(\d+)\s*$')
  $totalNanos = if ($totalMatch.Success) { [long]$totalMatch.Groups[1].Value } else { $null }

  $stepNanos = @()
  $stepMatches = [regex]::Matches($OutputStr, '(?m)^!!! Elapsed time (\d+):\s*(\d+)\s*$')
  foreach ($m in $stepMatches) {
    $stepNanos += [double]$m.Groups[2].Value
  }

  return @{ TotalNanos = $totalNanos; StepNanos = $stepNanos }
}

# k-perf writes `trace_<platform>_<random>.txt` and `symbols_<platform>_<random>.txt`
# to cwd. On the first measurement iteration of each k-perf variant we move
# the trace to <resultsDir>/traces/<exe-safe-name>.txt and count lines/2/StepCount
# to derive methods_per_step. Subsequent iterations delete trace/symbol files.
function Invoke-PostRunCleanup {
  param(
    [string]$ExeName,
    [bool]$IsMeasurement
  )

  $traceFiles  = @(Get-ChildItem -Path "." -Filter "trace*.txt"  -ErrorAction SilentlyContinue)
  $symbolFiles = @(Get-ChildItem -Path "." -Filter "symbols*.txt" -ErrorAction SilentlyContinue)

  $isKperf = $ExeName -match 'k-perf'
  $vp = Get-VariantAndPlatform -ExeName $ExeName
  $shouldPreserve = $IsMeasurement -and $isKperf -and (-not $tracePreserved.ContainsKey($ExeName)) -and ($traceFiles.Count -gt 0)

  if ($shouldPreserve) {
    if (-not (Test-Path $tracesDir)) {
      New-Item -ItemType Directory -Path $tracesDir -Force | Out-Null
    }
    $safeName = ($ExeName -replace '[^A-Za-z0-9._-]+', '_').Trim('_')
    $targetTrace = Join-Path $tracesDir "$safeName.txt"
    Move-Item -Path $traceFiles[0].FullName -Destination $targetTrace -Force

    $lineCount = (Get-Content $targetTrace | Measure-Object -Line).Lines
    if ($lineCount % 2 -ne 0) {
      Write-Host "  WARN: trace for $ExeName has odd line count $lineCount; truncating to even" -ForegroundColor Yellow
      $lineCount = $lineCount - 1
    }
    if ($StepCount -gt 0) {
      $mps = [Math]::Floor($lineCount / 2 / $StepCount)
      $methodsPerStep[$vp.Platform] = $mps
      Write-Host "  Preserved trace $safeName.txt ($lineCount lines) → methods/step = $mps for platform $($vp.Platform)" -ForegroundColor Cyan
    }
    $tracePreserved[$ExeName] = $true

    # Remove any remaining trace files plus all symbol files
    if ($traceFiles.Count -gt 1) {
      $traceFiles | Select-Object -Skip 1 | ForEach-Object { Remove-Item -Force $_.FullName }
    }
  }
  else {
    $traceFiles | ForEach-Object { Remove-Item -Force -ErrorAction SilentlyContinue $_.FullName }
  }

  $symbolFiles | ForEach-Object { Remove-Item -Force -ErrorAction SilentlyContinue $_.FullName }
}

foreach ($exe in $executables) {
  Write-Host ""

  # Boot or stop the Jaeger backend depending on whether this executable
  # exports traces. Jaeger all-in-one accepts OTLP natively on :4317 (gRPC)
  # and :4318 (HTTP), so the apps talk to it directly with no OTel Collector
  # hop in between. Stopped between non-otel rows so background CPU from an
  # idle Jaeger container doesn't bias baseline / k-perf measurements.
  if ($exe.Name -match "otel") {
    Write-Host "--- Booting Jaeger (OTLP backend + UI) via Docker ---"
    Invoke-Docker -DockerArgs @('start', 'jaeger')
    if ($LASTEXITCODE -ne 0) {
      Invoke-Docker -DockerArgs @('rm', '-f', 'jaeger')
      Invoke-Docker -DockerArgs @(
        'run', '-d', '--name', 'jaeger',
        '-p', '4317:4317', '-p', '4318:4318', '-p', '16686:16686',
        'jaegertracing/all-in-one:1.65.0',
        '--collector.otlp.enabled=true'
      )
      if ($LASTEXITCODE -ne 0) {
        throw "Failed to start jaeger container (docker run exited $LASTEXITCODE). Check that ports 4317/4318/16686 are free and Docker Desktop is healthy."
      }
    }

    $deadline = (Get-Date).AddSeconds(30)
    $ready = $false
    while ((Get-Date) -lt $deadline) {
      $probe = Test-NetConnection -ComputerName '127.0.0.1' -Port 4317 -WarningAction SilentlyContinue -InformationLevel Quiet
      if ($probe) { $ready = $true; break }
      Start-Sleep -Milliseconds 500
    }
    if (-not $ready) {
      Write-Host "jaeger logs:" -ForegroundColor Yellow
      Invoke-Docker -DockerArgs @('logs', '--tail', '40', 'jaeger') -CaptureOutput | ForEach-Object { Write-Host $_ }
      throw "jaeger did not start listening on :4317 within 30s."
    }
  }
  else {
    Write-Host "--- Stopping Jaeger to prevent CPU interference for baseline / k-perf metrics ---"
    Invoke-Docker -DockerArgs @('stop', 'jaeger')
  }

  Write-Host "--- Benchmarking: $($exe.Name) ---"
  $invocation = "$($exe.Command) $StepCount"

  # Warmup
  Write-Host "Warmup iterations ($WarmupCount):"
  for ($i = 0; $i -lt $WarmupCount; $i++) {
    $output = Invoke-WithTimeout -Command $invocation -TimeoutSeconds $RunTimeoutSeconds
    $outputStr = $output -join "`n"
    Invoke-PostRunCleanup -ExeName $exe.Name -IsMeasurement $false

    $parsed = Get-ElapsedFromOutput -OutputStr $outputStr
    if ($null -ne $parsed.TotalNanos) {
      Write-Host ("  Warmup {0}: total {1:N3} ms ({2} steps)" -f ($i+1), ($parsed.TotalNanos / 1000000.0), $parsed.StepNanos.Count)
    }
    else {
      Write-Host "  Warmup $($i+1): Failed to parse time"
      Save-FailureOutput -Phase "warmup" -ExeName $exe.Name -Iteration ($i + 1) -RawOutput $outputStr
    }
  }

  # Actual measurements
  $totalNanosList = @()
  $perRunStepNanos = @()
  Write-Host "Measurement iterations ($RunCount):"
  for ($i = 0; $i -lt $RunCount; $i++) {
    $output = Invoke-WithTimeout -Command $invocation -TimeoutSeconds $RunTimeoutSeconds
    $outputStr = $output -join "`n"

    # Cleanup runs *before* parsing so a k-perf trace from a successful run is
    # preserved by Invoke-PostRunCleanup. Parsing only consumes stdout, not
    # the trace file, so the order is correct.
    Invoke-PostRunCleanup -ExeName $exe.Name -IsMeasurement $true

    $parsed = Get-ElapsedFromOutput -OutputStr $outputStr
    if ($null -ne $parsed.TotalNanos) {
      $totalMs = $parsed.TotalNanos / 1000000.0
      Write-Host ("  Run {0}: total {1:N3} ms ({2} steps)" -f ($i+1), $totalMs, $parsed.StepNanos.Count) -ForegroundColor Green
      $totalNanosList += [double]$parsed.TotalNanos
      $perRunStepNanos += , @($parsed.StepNanos)
    }
    else {
      Write-Host "  Run $($i+1): Failed to parse time" -ForegroundColor Red
      Save-FailureOutput -Phase "run" -ExeName $exe.Name -Iteration ($i + 1) -RawOutput $outputStr
    }
  }

  # Flatten per-step times across all runs to compute a per-step mean/median.
  $flatStepNanos = @()
  foreach ($runSteps in $perRunStepNanos) {
    foreach ($s in $runSteps) { $flatStepNanos += [double]$s }
  }

  $totalStats = Get-BenchmarkStatistics -Values $totalNanosList
  $stepStats  = Get-BenchmarkStatistics -Values $flatStepNanos

  $allResults += [ordered]@{
    Executable       = $exe.Name
    Count            = $totalStats.count
    TotalMeanNanos   = $totalStats.mean
    TotalMedianNanos = $totalStats.median
    TotalStdDevNanos = $totalStats.stddev
    TotalMinNanos    = $totalStats.min
    TotalMaxNanos    = $totalStats.max
    StepMeanNanos    = $stepStats.mean
    StepMedianNanos  = $stepStats.median
    StepStdDevNanos  = $stepStats.stddev
    StepMinNanos     = $stepStats.min
    StepMaxNanos     = $stepStats.max
    TotalsNanos      = $totalNanosList
    PerRunStepNanos  = $perRunStepNanos
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

# --- Per-step median curve + steady-state detection -----------------------
# For each (variant, platform), compute the per-step median across runs
# (one median per step index, shape: StepCount). Then auto-detect a
# "steady-state start" — the first step index whose median falls within
# K × (tail-median) of the tail. Used to compute steady-state overhead so
# the cold-start prefix (very expensive on JVM) doesn't dominate.

function Get-PerStepMedians {
  param([object[]]$PerRunStepValues)
  if ($null -eq $PerRunStepValues -or $PerRunStepValues.Count -eq 0) { return @() }
  $stepCount = ($PerRunStepValues | ForEach-Object { $_.Count } | Measure-Object -Maximum).Maximum
  if ($null -eq $stepCount -or $stepCount -le 0) { return @() }
  $medians = @()
  for ($s = 0; $s -lt $stepCount; $s++) {
    $values = @()
    foreach ($run in $PerRunStepValues) {
      if ($s -lt $run.Count) { $values += [double]$run[$s] }
    }
    if ($values.Count -eq 0) { $medians += $null; continue }
    $sorted = @($values | Sort-Object)
    $mid = [Math]::Floor($sorted.Count / 2)
    if ($sorted.Count % 2 -eq 1) {
      $medians += [double]$sorted[$mid]
    } else {
      $medians += ([double]$sorted[$mid - 1] + [double]$sorted[$mid]) / 2.0
    }
  }
  return ,$medians
}

# Returns the first step index where the per-step median falls within
# $ThresholdMultiplier × tail-median, where tail-median is the median of
# step medians in the last 50% of step indices. With sawtooth (otel-*)
# this picks a relatively late index since many tail samples are spikes;
# that's intentional — the "steady mean" is then averaged over a mix of
# spike and quiet samples, which approximates real steady-state cost.
function Get-SteadyStateStart {
  param(
    [object[]]$Medians,
    [double]$ThresholdMultiplier = 2.0
  )
  $n = $Medians.Count
  if ($n -lt 8) { return 0 }

  $tailStart = [Math]::Floor($n / 2)
  $tail = @()
  for ($i = $tailStart; $i -lt $n; $i++) {
    if ($null -ne $Medians[$i]) { $tail += [double]$Medians[$i] }
  }
  if ($tail.Count -eq 0) { return 0 }
  $tailSorted = @($tail | Sort-Object)
  $tailMedian = [double]$tailSorted[[Math]::Floor($tailSorted.Count / 2)]
  if ($tailMedian -le 0) { return 0 }

  $limit = $ThresholdMultiplier * $tailMedian
  for ($i = 0; $i -lt $n; $i++) {
    if ($null -ne $Medians[$i] -and [double]$Medians[$i] -le $limit) { return $i }
  }
  return $tailStart
}

# Mean of $Values[$Start..end] (inclusive), ignoring nulls.
function Get-MeanFromIndex {
  param([object[]]$Values, [int]$Start)
  $acc = 0.0; $n = 0
  for ($i = $Start; $i -lt $Values.Count; $i++) {
    if ($null -ne $Values[$i]) { $acc += [double]$Values[$i]; $n++ }
  }
  if ($n -eq 0) { return $null }
  return $acc / $n
}

# Percentile of $Values[$Start..end] (inclusive), ignoring nulls.
# $Percentile is in [0,1]; uses linear interpolation between the two
# closest order statistics (same convention as numpy.percentile default).
# Used to compute a lower-envelope (P10) "clean per-method cost" that
# is robust against batch-flush spikes in otel-* rows.
function Get-PercentileFromIndex {
  param([object[]]$Values, [int]$Start, [double]$Percentile)
  $samples = New-Object 'System.Collections.Generic.List[double]'
  for ($i = $Start; $i -lt $Values.Count; $i++) {
    if ($null -ne $Values[$i]) { [void]$samples.Add([double]$Values[$i]) }
  }
  if ($samples.Count -eq 0) { return $null }
  $sorted = $samples | Sort-Object
  $n = $sorted.Count
  if ($n -eq 1) { return [double]$sorted[0] }
  $rank = ($n - 1) * $Percentile
  $lo = [Math]::Floor($rank)
  $hi = [Math]::Ceiling($rank)
  if ($lo -eq $hi) { return [double]$sorted[[int]$lo] }
  $frac = $rank - $lo
  return [double]$sorted[[int]$lo] * (1.0 - $frac) + [double]$sorted[[int]$hi] * $frac
}

# Build a per-(variant,platform) summary: medians + steady-state start +
# steady-state mean step time. Store on $allResults for downstream use.
# All values in nanoseconds; convert at display time.
foreach ($res in $allResults) {
  $medians = Get-PerStepMedians -PerRunStepValues $res.PerRunStepNanos
  $steady  = Get-SteadyStateStart -Medians $medians -ThresholdMultiplier 2.0
  $steadyMean     = Get-MeanFromIndex      -Values $medians -Start $steady
  $steadyEnvelope = Get-PercentileFromIndex -Values $medians -Start $steady -Percentile 0.10
  $res['PerStepMedianNanos'] = $medians
  $res['SteadyStateStart']   = $steady
  $res['SteadyStepNanos']    = $steadyMean
  $res['EnvelopeStepNanos']  = $steadyEnvelope
}

# --- Overhead calculation --------------------------------------------------
# Compute two flavours of overhead (both in ns/method):
#   1) Full-run flat:  uses StepMeanNanos / baseline.StepMeanNanos (all samples)
#   2) Steady-state:   uses SteadyStepNanos (per-step median, averaged over
#                      the auto-detected steady region) for both terms.
# Steady-state is the one to quote when reporting "the cost of one
# instrumented method call at warm steady state".
# Formula:  overhead_ns_per_method = (instr_ns - base_ns) / methods_per_step

$baselineFullByPlatform     = @{}
$baselineSteadyByPlatform   = @{}
$baselineEnvelopeByPlatform = @{}
foreach ($res in $allResults) {
  $vp = Get-VariantAndPlatform -ExeName $res.Executable
  if ($vp.Variant -eq 'baseline') {
    if ($null -ne $res.StepMeanNanos)     { $baselineFullByPlatform[$vp.Platform]     = $res.StepMeanNanos }
    if ($null -ne $res.SteadyStepNanos)   { $baselineSteadyByPlatform[$vp.Platform]   = $res.SteadyStepNanos }
    if ($null -ne $res.EnvelopeStepNanos) { $baselineEnvelopeByPlatform[$vp.Platform] = $res.EnvelopeStepNanos }
  }
}

$overheadRows = @()
foreach ($res in $allResults) {
  $vp = Get-VariantAndPlatform -ExeName $res.Executable
  if ($vp.Variant -eq 'baseline') { continue }

  $mps          = $methodsPerStep[$vp.Platform]
  $stepMean     = $res.StepMeanNanos
  $stepSteady   = $res.SteadyStepNanos
  $stepEnvelope = $res.EnvelopeStepNanos
  $baselineFull     = $baselineFullByPlatform[$vp.Platform]
  $baselineSteady   = $baselineSteadyByPlatform[$vp.Platform]
  $baselineEnvelope = $baselineEnvelopeByPlatform[$vp.Platform]

  $ohFull = $null
  if ($null -ne $baselineFull -and $null -ne $stepMean -and $null -ne $mps -and $mps -gt 0) {
    $ohFull = ($stepMean - $baselineFull) / $mps
  }
  $ohSteady = $null
  if ($null -ne $baselineSteady -and $null -ne $stepSteady -and $null -ne $mps -and $mps -gt 0) {
    $ohSteady = ($stepSteady - $baselineSteady) / $mps
  }
  $ohEnvelope = $null
  if ($null -ne $baselineEnvelope -and $null -ne $stepEnvelope -and $null -ne $mps -and $mps -gt 0) {
    $ohEnvelope = ($stepEnvelope - $baselineEnvelope) / $mps
  }

  $overheadRows += [ordered]@{
    Variant                    = $vp.Variant
    Platform                   = $vp.Platform
    StepMeanNanos              = $stepMean
    BaselineStepNanos          = $baselineFull
    SteadyStepNanos            = $stepSteady
    BaselineSteadyStepNanos    = $baselineSteady
    EnvelopeStepNanos          = $stepEnvelope
    BaselineEnvelopeStepNanos  = $baselineEnvelope
    SteadyStateStart           = $res.SteadyStateStart
    MethodsPerStep             = $mps
    OverheadNsFullRun          = $ohFull
    OverheadNsSteadyState      = $ohSteady
    OverheadNsEnvelope         = $ohEnvelope
  }
}

# --- Emit per-step median CSV ---------------------------------------------
# One row per (variant, platform, step). Use this in Excel/Python to plot
# the JIT warmup curve. Values in nanoseconds (divide by 1000 for µs).
$csvPath = Join-Path $resultsDir "per_step_medians.csv"
$csvLines = @("variant,platform,step,median_ns,steady_state_start")
foreach ($res in $allResults) {
  $vp = Get-VariantAndPlatform -ExeName $res.Executable
  $medians = $res.PerStepMedianNanos
  $steady  = $res.SteadyStateStart
  if ($null -eq $medians -or $medians.Count -eq 0) { continue }
  for ($i = 0; $i -lt $medians.Count; $i++) {
    $m = if ($null -ne $medians[$i]) { "{0:F0}" -f [double]$medians[$i] } else { "" }
    $csvLines += "$($vp.Variant),$($vp.Platform),$i,$m,$steady"
  }
}
$csvLines | Out-File -FilePath $csvPath -Encoding utf8

$jsonOutput = [ordered]@{
  Parameters     = @{ WarmupCount = $WarmupCount; RunCount = $RunCount; StepCount = $StepCount; CleanBuild = $CleanBuild }
  MachineInfo    = $machineInfo
  MethodsPerStep = $methodsPerStep
  Results        = $allResults
  Overhead       = $overheadRows
}

$jsonFile = "$resultsDir\results.json"
$mdFile = "$resultsDir\results.md"

$jsonOutput | ConvertTo-Json -Depth 10 | Out-File $jsonFile -Encoding utf8

# Generate Markdown file
$markdown = @"
# Benchmark Results ($timestamp)

## Parameters
- **Warmup Iterations:** $WarmupCount
- **Run Iterations:** $RunCount
- **Step Count (workload calls per process):** $StepCount
- **Clean Build:** $CleanBuild
- **Run timeout (s):** $RunTimeoutSeconds

## System Information
- **OS:** $($machineInfo.OS) $($machineInfo.OSArchitecture)
- **CPU:** $($machineInfo.CPU) ($($machineInfo.CPUCores) Cores / $($machineInfo.CPULogicalProcessors) Logical Processors)
- **RAM:** $($machineInfo.TotalRAMGB) GB
- **Java Version:** $($machineInfo.JavaVersion) ($($machineInfo.JavaDistribution))
- **Node Version:** $($machineInfo.NodeVersion)

## Hardware Overview Details
- **Device:** $($machineInfo.DeviceManufacturer) - $($machineInfo.DeviceModel)
- **Git Branch:** $($machineInfo.GitBranch)

## Methods per step (k-perf trace, lines/2/StepCount)

| Platform | methods_per_step |
|---|---:|
"@

foreach ($plat in @('JVM','JS','Native')) {
  $val = if ($methodsPerStep.ContainsKey($plat)) { $methodsPerStep[$plat] } else { 'N/A' }
  $markdown += "`n| $plat | $val |"
}

$markdown += @"


## Execution Summary

Total = wall-clock per process. Step = mean across RunCount × StepCount samples.

| Executable | Iterations | Total mean (ms) | Total median (ms) | Step mean (µs) | Step median (µs) | Step stddev (µs) |
|------------|-----------:|----------------:|------------------:|---------------:|-----------------:|-----------------:|
"@

foreach ($res in $allResults) {
  $totalMean   = if ($null -ne $res.TotalMeanNanos)   { "{0:N2}" -f ($res.TotalMeanNanos / 1000000.0) } else { "N/A" }
  $totalMedian = if ($null -ne $res.TotalMedianNanos) { "{0:N2}" -f ($res.TotalMedianNanos / 1000000.0) } else { "N/A" }
  $stepMean    = if ($null -ne $res.StepMeanNanos)    { "{0:N2}" -f ($res.StepMeanNanos / 1000.0) } else { "N/A" }
  $stepMedian  = if ($null -ne $res.StepMedianNanos)  { "{0:N2}" -f ($res.StepMedianNanos / 1000.0) } else { "N/A" }
  $stepStdDev  = if ($null -ne $res.StepStdDevNanos)  { "{0:N2}" -f ($res.StepStdDevNanos / 1000.0) } else { "N/A" }

  $markdown += "`n| $($res.Executable) | $($res.Count) | $totalMean | $totalMedian | $stepMean | $stepMedian | $stepStdDev |"
}

$markdown += @"


## Overhead per instrumented method

``overhead_ns/method = (step_ns_instrumented − step_ns_baseline) / methods_per_step``

- **Full**: mean over all steps. Cold-JVM-biased.
- **Steady**: mean from ``SS-start`` onward (first step ≤ 2× tail-median). Quote this.
- **Envelope (P10)**: 10th percentile of steady-region per-step medians. Sawtooth-aware lower bound.

methods/step from preserved k-perf trace (``traces/``); otel-* underestimate by ~0.5 % (skipped ``repeat { }`` lambda body).

| Variant | Platform | SS-start | Step mean (µs) | Baseline mean (µs) | Steady (µs) | Baseline steady (µs) | Envelope P10 (µs) | Baseline envelope (µs) | Methods/step | Overhead full (ns) | Overhead steady (ns) | Overhead envelope (ns) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
"@

foreach ($row in $overheadRows) {
  $ss           = if ($null -ne $row.SteadyStateStart)           { "$($row.SteadyStateStart)" }                                else { "N/A" }
  $stepMean     = if ($null -ne $row.StepMeanNanos)              { "{0:N2}" -f ($row.StepMeanNanos / 1000.0) }                  else { "N/A" }
  $baseMean     = if ($null -ne $row.BaselineStepNanos)          { "{0:N2}" -f ($row.BaselineStepNanos / 1000.0) }              else { "N/A" }
  $stepSteady   = if ($null -ne $row.SteadyStepNanos)            { "{0:N2}" -f ($row.SteadyStepNanos / 1000.0) }                else { "N/A" }
  $baseSteady   = if ($null -ne $row.BaselineSteadyStepNanos)    { "{0:N2}" -f ($row.BaselineSteadyStepNanos / 1000.0) }        else { "N/A" }
  $stepEnv      = if ($null -ne $row.EnvelopeStepNanos)          { "{0:N2}" -f ($row.EnvelopeStepNanos / 1000.0) }              else { "N/A" }
  $baseEnv      = if ($null -ne $row.BaselineEnvelopeStepNanos)  { "{0:N2}" -f ($row.BaselineEnvelopeStepNanos / 1000.0) }      else { "N/A" }
  $mps          = if ($null -ne $row.MethodsPerStep)             { "$($row.MethodsPerStep)" }                                    else { "N/A" }
  $ohFull       = if ($null -ne $row.OverheadNsFullRun)          { "{0:N1}" -f $row.OverheadNsFullRun }                         else { "N/A" }
  $ohSteady     = if ($null -ne $row.OverheadNsSteadyState)      { "{0:N1}" -f $row.OverheadNsSteadyState }                     else { "N/A" }
  $ohEnvelope   = if ($null -ne $row.OverheadNsEnvelope)         { "{0:N1}" -f $row.OverheadNsEnvelope }                        else { "N/A" }
  $markdown += "`n| $($row.Variant) | $($row.Platform) | $ss | $stepMean | $baseMean | $stepSteady | $baseSteady | $stepEnv | $baseEnv | $mps | $ohFull | $ohSteady | $ohEnvelope |"
}

# --- Per-step curve table: median µs at selected step indices --------------
# Picks indices that span the cold→warm transition. Edit the array if you
# want different resolution.
$curveStepIndices = @(0, 1, 2, 5, 10, 25, 50, 75, 100, 125, ($StepCount - 1))
$curveStepIndices = @($curveStepIndices | Where-Object { $_ -lt $StepCount } | Sort-Object -Unique)
$curveHeaderCells = $curveStepIndices | ForEach-Object { "s$_" }
$curveDivider = ($curveStepIndices | ForEach-Object { "---:" }) -join ' | '

$markdown += @"


## Per-step median curve (µs)

Sampled step indices across $RunCount runs. otel-* sawtooth = BSP flushes. Full data in ``per_step_medians.csv`` / ``results.json::Results[*].PerRunStepNanos``.

| Variant | Platform | $($curveHeaderCells -join ' | ') |
|---|---|$curveDivider|
"@

foreach ($res in $allResults) {
  $vp = Get-VariantAndPlatform -ExeName $res.Executable
  $medians = $res.PerStepMedianNanos
  if ($null -eq $medians -or $medians.Count -eq 0) { continue }
  $cells = @()
  foreach ($idx in $curveStepIndices) {
    if ($idx -lt $medians.Count -and $null -ne $medians[$idx]) {
      # Display as µs with 2 decimals so sub-µs values (Native baseline ~0.4)
      # remain visible. Large values like 12000 µs still print fine.
      $cells += "{0:N2}" -f ([double]$medians[$idx] / 1000.0)
    } else {
      $cells += "—"
    }
  }
  $markdown += "`n| $($vp.Variant) | $($vp.Platform) | $($cells -join ' | ') |"
}

$markdown += @"

> Curve shape: JVM C1≈step 1-2, C2≈step 55. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction.
"@

$markdown | Out-File $mdFile -Encoding utf8

Write-Host "Measurements and stats saved successfully to folder: `n -> $resultsDir"
Write-Host "  results.json          (raw + statistics)"
Write-Host "  results.md            (summary tables + per-step curve)"
Write-Host "  per_step_medians.csv  (long-form per-step medians for plotting)"
Write-Host "  traces/               (k-perf trace per platform, one preserved per variant)"
Write-Host "Benchmark evaluation finished."
Pop-Location
