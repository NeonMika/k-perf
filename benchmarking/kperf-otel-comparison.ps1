param(
  [bool]$CleanBuild = $true,
  # WarmupCount: number of *step indices* discarded from the start of each
  # measured run. Replaces the old 2× tail-median steady-state detector
  # (which was opaque and circular). With StepCount=100 and WarmupCount=20,
  # 80 measured steps remain per run.
  [int]$WarmupCount = 20,
  [int]$RunCount = 10,
  [int]$StepCount = 100,
  [string[]]$Variants = @('baseline','k-perf','otel','otel-proto','otel-proto-sampler','otel-proto-timesource','otel-proto-anchored','otel-proto-fastbatch')
)

$ErrorActionPreference = "Stop"

$KnownVariants = @('baseline','k-perf','otel','otel-proto','otel-proto-sampler','otel-proto-timesource','otel-proto-anchored','otel-proto-fastbatch')
foreach ($v in $Variants) {
  if ($KnownVariants -notcontains $v) {
    throw "Unknown variant '$v'. Allowed: $($KnownVariants -join ', ')"
  }
}
if ($Variants -notcontains 'baseline') {
  Write-Host "WARNING: 'baseline' is not selected - per-platform Overhead/method columns will be empty." -ForegroundColor Yellow
}
$AnyProtoVariant = @($Variants | Where-Object { $_ -like 'otel-proto*' }).Count -gt 0

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
    [bool]$SkipClean = $false,
    [switch]$RefreshDeps
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
    $gradleArgs = @()
    if ($RefreshDeps) { $gradleArgs += '--refresh-dependencies' }
    $gradleArgs += $Tasks
    if ($CleanBuild -and -not $SkipClean) {
      & .\gradlew clean @gradleArgs
    }
    else {
      & .\gradlew @gradleArgs
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

if ($Variants -contains 'k-perf') {
  Invoke-GradleBuild -Title "KIRHelperKit" -Path ".\KIRHelperKit" -Tasks @("publishToMavenLocal")
  Invoke-GradleBuild -Title "k-perf plugin" -Path ".\plugins\k-perf" -Tasks @("publishToMavenLocal")
}

if ($Variants -contains 'otel') {
  Invoke-GradleBuild -Title "OTel OTLP Exporter" -Path ".\otlp-exporter" -Tasks @("publishToMavenLocal")
  Invoke-GradleBuild -Title "OTel Plugin Util" -Path ".\plugins\otel-plugin\util" -Tasks @("publishToMavenLocal")
  Invoke-GradleBuild -Title "OTel Plugin" -Path ".\plugins\otel-plugin\plugin" -Tasks @("publishToMavenLocal")
}

if ($AnyProtoVariant) {
  Invoke-GradleBuild -Title "OTel OTLP Exporter (proto)" -Path ".\otlp-exporter-proto" -Tasks @("publishToMavenLocal")
  Invoke-GradleBuild -Title "OTel Plugin Util (proto)" -Path ".\plugins\otel-plugin-proto\util" -Tasks @("publishToMavenLocal")
}
if ($Variants -contains 'otel-proto') {
  Invoke-GradleBuild -Title "OTel Plugin (proto)" -Path ".\plugins\otel-plugin-proto\plugin" -Tasks @("publishToMavenLocal")
}
if ($Variants -contains 'otel-proto-timesource') {
  Invoke-GradleBuild -Title "OTel Plugin (proto-timesource)" -Path ".\plugins\otel-plugin-proto-timesource\plugin" -Tasks @("publishToMavenLocal")
}
if ($Variants -contains 'otel-proto-anchored') {
  Invoke-GradleBuild -Title "OTel Plugin (proto-anchored)" -Path ".\plugins\otel-plugin-proto-anchored\plugin" -Tasks @("publishToMavenLocal")
}
if ($Variants -contains 'otel-proto-sampler') {
  Invoke-GradleBuild -Title "OTel Plugin (proto-sampler)" -Path ".\plugins\otel-plugin-proto-sampler\plugin" -Tasks @("publishToMavenLocal")
}
if ($Variants -contains 'otel-proto-fastbatch') {
  Invoke-GradleBuild -Title "OTel Plugin (proto-fastbatch)" -Path ".\plugins\otel-plugin-proto-fastbatch\plugin" -Tasks @("publishToMavenLocal")
}

Write-Host "=========================================="
Write-Host "Compiling Comparison Projects"
Write-Host "=========================================="

$comparisonBuilds = @(
  @{ Variant = 'baseline';              Title = "Comparison Project (baseline)";              Path = ".\kmp-examples\comparison-baseline";              RefreshDeps = $false },
  @{ Variant = 'k-perf';                Title = "Comparison Project (k-perf)";                Path = ".\kmp-examples\comparison-k-perf";                RefreshDeps = $false },
  @{ Variant = 'otel';                  Title = "Comparison Project (otel)";                  Path = ".\kmp-examples\comparison-otel";                  RefreshDeps = $true },
  @{ Variant = 'otel-proto';            Title = "Comparison Project (otel-proto)";            Path = ".\kmp-examples\comparison-otel-proto";            RefreshDeps = $true },
  @{ Variant = 'otel-proto-sampler';    Title = "Comparison Project (otel-proto-sampler)";    Path = ".\kmp-examples\comparison-otel-proto-sampler";    RefreshDeps = $true },
  @{ Variant = 'otel-proto-timesource'; Title = "Comparison Project (otel-proto-timesource)"; Path = ".\kmp-examples\comparison-otel-proto-timesource"; RefreshDeps = $true },
  @{ Variant = 'otel-proto-anchored';   Title = "Comparison Project (otel-proto-anchored)";   Path = ".\kmp-examples\comparison-otel-proto-anchored";   RefreshDeps = $true },
  @{ Variant = 'otel-proto-fastbatch';  Title = "Comparison Project (otel-proto-fastbatch)";  Path = ".\kmp-examples\comparison-otel-proto-fastbatch";  RefreshDeps = $true }
)
$comparisonTasks = @("jvmJar", "kotlinNpmInstall", "jsProductionExecutableCompileSync", "linkReleaseExecutableMingwX64")
foreach ($b in $comparisonBuilds) {
  if ($Variants -notcontains $b.Variant) { continue }
  if ($b.RefreshDeps) {
    Invoke-GradleBuild -Title $b.Title -Path $b.Path -Tasks $comparisonTasks -SkipClean $true -RefreshDeps
  } else {
    Invoke-GradleBuild -Title $b.Title -Path $b.Path -Tasks $comparisonTasks -SkipClean $true
  }
}


# Path resolving for execution. StepCount is appended at run time as the
# positional argv[0] each Main.kt reads.
$baselineJvm    = "java -jar .\kmp-examples\comparison-baseline\build\lib\comparison-baseline-jvm-0.1.0.jar"
$baselineJs     = "node --max-old-space-size=16384 .\kmp-examples\comparison-baseline\build\js\packages\comparison-baseline\kotlin\comparison-baseline.js"
$baselineNative = ".\kmp-examples\comparison-baseline\build\bin\mingwX64\releaseExecutable\comparison-baseline.exe"

$kperfJvm    = "java -jar .\kmp-examples\comparison-k-perf\build\lib\comparison-k-perf-jvm-0.1.0-flushEarly-false.jar"
$kperfJs     = "node --max-old-space-size=16384 .\kmp-examples\comparison-k-perf\build\js\packages\comparison-k-perf-flushEarly-false\kotlin\comparison-k-perf-flushEarly-false.js"
$kperfNative = ".\kmp-examples\comparison-k-perf\build\bin\mingwX64\releaseExecutable\comparison-k-perf-flushEarly-false.exe"

$otelJvm    = "java -jar .\kmp-examples\comparison-otel\build\lib\comparison-otel-jvm-1.0.0.jar"
$otelJs     = "node --max-old-space-size=16384 .\kmp-examples\comparison-otel\build\js\packages\comparison-otel\kotlin\comparison-otel.js"
$otelNative = ".\kmp-examples\comparison-otel\build\bin\mingwX64\releaseExecutable\main.exe"

$otelProtoJvm    = "java -jar .\kmp-examples\comparison-otel-proto\build\lib\comparison-otel-proto-jvm-1.0.0.jar"
$otelProtoJs     = "node --max-old-space-size=16384 .\kmp-examples\comparison-otel-proto\build\js\packages\comparison-otel-proto\kotlin\comparison-otel-proto.js"
$otelProtoNative = ".\kmp-examples\comparison-otel-proto\build\bin\mingwX64\releaseExecutable\main.exe"

$otelProtoTsJvm    = "java -jar .\kmp-examples\comparison-otel-proto-timesource\build\lib\comparison-otel-proto-timesource-jvm-1.0.0.jar"
$otelProtoTsJs     = "node --max-old-space-size=16384 .\kmp-examples\comparison-otel-proto-timesource\build\js\packages\comparison-otel-proto-timesource\kotlin\comparison-otel-proto-timesource.js"
$otelProtoTsNative = ".\kmp-examples\comparison-otel-proto-timesource\build\bin\mingwX64\releaseExecutable\main.exe"

$otelProtoAnchoredJvm    = "java -jar .\kmp-examples\comparison-otel-proto-anchored\build\lib\comparison-otel-proto-anchored-jvm-1.0.0.jar"
$otelProtoAnchoredJs     = "node --max-old-space-size=16384 .\kmp-examples\comparison-otel-proto-anchored\build\js\packages\comparison-otel-proto-anchored\kotlin\comparison-otel-proto-anchored.js"
$otelProtoAnchoredNative = ".\kmp-examples\comparison-otel-proto-anchored\build\bin\mingwX64\releaseExecutable\main.exe"

$otelProtoSamplerJvm    = "java -jar .\kmp-examples\comparison-otel-proto-sampler\build\lib\comparison-otel-proto-sampler-jvm-1.0.0.jar"
$otelProtoSamplerJs     = "node --max-old-space-size=16384 .\kmp-examples\comparison-otel-proto-sampler\build\js\packages\comparison-otel-proto-sampler\kotlin\comparison-otel-proto-sampler.js"
$otelProtoSamplerNative = ".\kmp-examples\comparison-otel-proto-sampler\build\bin\mingwX64\releaseExecutable\main.exe"

$otelProtoFastbatchJvm    = "java -jar .\kmp-examples\comparison-otel-proto-fastbatch\build\lib\comparison-otel-proto-fastbatch-jvm-1.0.0.jar"
$otelProtoFastbatchJs     = "node --max-old-space-size=16384 .\kmp-examples\comparison-otel-proto-fastbatch\build\js\packages\comparison-otel-proto-fastbatch\kotlin\comparison-otel-proto-fastbatch.js"
$otelProtoFastbatchNative = ".\kmp-examples\comparison-otel-proto-fastbatch\build\bin\mingwX64\releaseExecutable\main.exe"

$executables = @(
  @{ Name = "baseline JVM";                       Command = $baselineJvm },
  @{ Name = "k-perf JVM";                         Command = $kperfJvm },
  @{ Name = "otel JVM";                           Command = $otelJvm },
  @{ Name = "otel-proto JVM";                     Command = $otelProtoJvm },
  @{ Name = "otel-proto-sampler JVM";             Command = $otelProtoSamplerJvm },
  @{ Name = "otel-proto-timesource JVM";          Command = $otelProtoTsJvm },
  @{ Name = "otel-proto-anchored JVM";            Command = $otelProtoAnchoredJvm },
  @{ Name = "otel-proto-fastbatch JVM";           Command = $otelProtoFastbatchJvm },
  @{ Name = "baseline JS (Node)";                 Command = $baselineJs },
  @{ Name = "k-perf JS (Node)";                   Command = $kperfJs },
  @{ Name = "otel JS (Node)";                     Command = $otelJs },
  @{ Name = "otel-proto JS (Node)";               Command = $otelProtoJs },
  @{ Name = "otel-proto-sampler JS (Node)";       Command = $otelProtoSamplerJs },
  @{ Name = "otel-proto-timesource JS (Node)";    Command = $otelProtoTsJs },
  @{ Name = "otel-proto-anchored JS (Node)";      Command = $otelProtoAnchoredJs },
  @{ Name = "otel-proto-fastbatch JS (Node)";     Command = $otelProtoFastbatchJs },
  @{ Name = "baseline Native (Win)";              Command = $baselineNative },
  @{ Name = "k-perf Native (Win)";                Command = $kperfNative },
  @{ Name = "otel Native (Win)";                  Command = $otelNative },
  @{ Name = "otel-proto Native (Win)";            Command = $otelProtoNative },
  @{ Name = "otel-proto-sampler Native (Win)";    Command = $otelProtoSamplerNative },
  @{ Name = "otel-proto-timesource Native (Win)"; Command = $otelProtoTsNative },
  @{ Name = "otel-proto-anchored Native (Win)";   Command = $otelProtoAnchoredNative },
  @{ Name = "otel-proto-fastbatch Native (Win)";  Command = $otelProtoFastbatchNative }
)
$executables = @($executables | Where-Object {
  $Variants -contains (Get-VariantAndPlatform -ExeName $_.Name).Variant
})

Write-Host "=========================================="
Write-Host "Running Measurements"
Write-Host "Warmup steps/run: $WarmupCount | Runs: $RunCount | StepCount: $StepCount | Run timeout: ${RunTimeoutSeconds}s"
Write-Host "Variants: $($Variants -join ', ')"
Write-Host "=========================================="

$allResults = @()

# Compute the results directory up-front so we can drop per-run debug dumps
# under it the moment a run fails.
$timestamp = Get-Date -Format "yyyy_MM_dd_HH_mm_ss"
$resultsDir = ".\measurements\comparison_run_$timestamp"
$failuresDir = Join-Path $resultsDir "failures"
$tracesDir   = Join-Path $resultsDir "traces"

# methods_per_step keyed by platform name ("JVM"/"JS"/"Native"). Primary
# source is the static formula below (workload is deterministic + all
# variants now exclude property accessors uniformly, so a closed-form
# computation is exact). The k-perf trace capture remains as a runtime
# sanity check — if it diverges from the formula by >1% a warning is
# emitted further down.
#
# Workload (in each comparison-*/src/commonMain/kotlin/Main.kt):
#   workload() = fibonacci(20) + bubbleSort(15-element array)
#   per workload() invocation = per step:
#       fib_call_count(20) + 1 (bubbleSort) + 1 (workload itself)
#       = 21891 + 2 = 21893
#
# fib_call_count(n) = 2*F(n+1) - 1 where F is the Fibonacci sequence.
$WorkloadFibDepth = 20    # hard-coded in each Main.kt's workload()

function Get-FibCallCount {
  param([int]$N)
  if ($N -le 1) { return 1L }
  # iterative Fibonacci so this helper itself is O(N) rather than recursive
  $a = 1L; $b = 1L
  for ($i = 2; $i -le ($N + 1); $i++) { $tmp = $a + $b; $a = $b; $b = $tmp }
  return 2L * $a - 1L
}

$staticMethodsPerStep = (Get-FibCallCount -N $WorkloadFibDepth) + 2L
$methodsPerStep = @{ 'JVM' = $staticMethodsPerStep; 'JS' = $staticMethodsPerStep; 'Native' = $staticMethodsPerStep }
Write-Host ("methods/step (from formula: fibDepth={0}): {1:N0}" -f $WorkloadFibDepth, $staticMethodsPerStep) -ForegroundColor Cyan

# k-perf trace-based methods/step, captured during the first k-perf run for
# each platform. Used purely as a sanity check vs $staticMethodsPerStep.
$methodsPerStepFromTrace = @{}
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


Invoke-Docker -DockerArgs @('rm', '-f', 'otel-collector', 'jaeger', 'envoy')

function Start-Jaeger {
  Write-Host "--- Booting Jaeger + Envoy gRPC-Web proxy (in-memory storage capped at 400 traces) ---" -ForegroundColor Cyan

  Invoke-Docker -DockerArgs @('rm', '-f', 'jaeger', 'envoy')

  Invoke-Docker -DockerArgs @('network', 'create', 'otel-net')

  Invoke-Docker -DockerArgs @(
    'run', '-d', '--name', 'jaeger',
    '--network', 'otel-net',
    '--memory=12g',
    '-e', 'COLLECTOR_OTLP_ENABLED=true',
    '-e', 'COLLECTOR_QUEUE_SIZE=5000000',
    '-e', 'COLLECTOR_NUM_WORKERS=100',
    '-e', 'MEMORY_MAX_TRACES=100',
    '-p', '16686:16686', '-p', '14269:14269',
    'jaegertracing/all-in-one:1.65.0'
  )
  $jaegerRunExit = $LASTEXITCODE
  if ($jaegerRunExit -ne 0) {
    Write-Host "docker run (jaeger) failed. Last container logs (if any):" -ForegroundColor Yellow
    Invoke-Docker -DockerArgs @('logs', '--tail', '40', 'jaeger') -CaptureOutput | ForEach-Object { Write-Host "  $_" }
    Write-Host "All jaeger-named containers (running or stopped):" -ForegroundColor Yellow
    Invoke-Docker -DockerArgs @('ps', '-a', '--filter', 'name=jaeger') -CaptureOutput | ForEach-Object { Write-Host "  $_" }
    throw "Failed to start jaeger container (docker run exited $jaegerRunExit). Check that ports 16686/14269 are free, Docker Desktop is healthy, and no zombie jaeger container exists."
  }

  $deadline = (Get-Date).AddSeconds(30)
  $ready = $false
  while ((Get-Date) -lt $deadline) {
    $probe = Test-NetConnection -ComputerName '127.0.0.1' -Port 14269 -WarningAction SilentlyContinue -InformationLevel Quiet
    if ($probe) { $ready = $true; break }
    Start-Sleep -Milliseconds 500
  }
  if (-not $ready) {
    Write-Host "jaeger logs:" -ForegroundColor Yellow
    Invoke-Docker -DockerArgs @('logs', '--tail', '40', 'jaeger') -CaptureOutput | ForEach-Object { Write-Host $_ }
    throw "jaeger did not start listening on :14269 within 30s."
  }

  $envoyConfig = Join-Path $ScriptRoot 'envoy-grpc-web.yaml'
  if (-not (Test-Path $envoyConfig)) {
    throw "Envoy config not found at $envoyConfig — it should live next to kperf-otel-comparison.ps1."
  }
  Invoke-Docker -DockerArgs @(
    'run', '-d', '--name', 'envoy',
    '--network', 'otel-net',
    '--memory=2g',
    '-p', '4317:4317', '-p', '4318:4318',
    '-v', "${envoyConfig}:/etc/envoy/envoy.yaml:ro",
    'envoyproxy/envoy:v1.31.0'
  )
  $envoyRunExit = $LASTEXITCODE
  if ($envoyRunExit -ne 0) {
    Write-Host "docker run (envoy) failed. Last container logs (if any):" -ForegroundColor Yellow
    Invoke-Docker -DockerArgs @('logs', '--tail', '40', 'envoy') -CaptureOutput | ForEach-Object { Write-Host "  $_" }
    throw "Failed to start envoy container (docker run exited $envoyRunExit). Check that ports 4317/4318 are free."
  }

  $deadline = (Get-Date).AddSeconds(30)
  $ready = $false
  while ((Get-Date) -lt $deadline) {
    $probe4317 = Test-NetConnection -ComputerName '127.0.0.1' -Port 4317 -WarningAction SilentlyContinue -InformationLevel Quiet
    $probe4318 = Test-NetConnection -ComputerName '127.0.0.1' -Port 4318 -WarningAction SilentlyContinue -InformationLevel Quiet
    if ($probe4317 -and $probe4318) { $ready = $true; break }
    Start-Sleep -Milliseconds 500
  }
  if (-not $ready) {
    Write-Host "envoy logs:" -ForegroundColor Yellow
    Invoke-Docker -DockerArgs @('logs', '--tail', '40', 'envoy') -CaptureOutput | ForEach-Object { Write-Host $_ }
    throw "envoy did not start listening on :4317 and :4318 within 30s."
  }
  Write-Host "Backend ready: Envoy gRPC[-Web] on :4317 -> jaeger:4317 | Envoy OTLP/HTTP on :4318 -> jaeger:4318 | UI http://localhost:16686 | metrics :14269." -ForegroundColor Cyan
}

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

  $expSpans = $null; $expBatches = $null; $expFailures = $null; $expFailedSpans = $null; $firstError = $null
  $m = [regex]::Match($OutputStr, '(?m)^### exported_spans:\s*(\d+)\s*$')
  if ($m.Success) { $expSpans = [long]$m.Groups[1].Value }
  $m = [regex]::Match($OutputStr, '(?m)^### export_batches:\s*(\d+)\s*$')
  if ($m.Success) { $expBatches = [long]$m.Groups[1].Value }
  $m = [regex]::Match($OutputStr, '(?m)^### export_failures:\s*(\d+)\s*$')
  if ($m.Success) { $expFailures = [long]$m.Groups[1].Value }
  $m = [regex]::Match($OutputStr, '(?m)^### export_failed_spans:\s*(\d+)\s*$')
  if ($m.Success) { $expFailedSpans = [long]$m.Groups[1].Value }
  $m = [regex]::Match($OutputStr, '(?m)^### first_export_error:\s*(.+)$')
  if ($m.Success) { $firstError = $m.Groups[1].Value.Trim() }

  return @{
    TotalNanos       = $totalNanos
    StepNanos        = $stepNanos
    ExportedSpans    = $expSpans
    ExportBatches    = $expBatches
    ExportFailures   = $expFailures
    ExportFailedSpans = $expFailedSpans
    FirstExportError = $firstError
  }
}

function Get-JaegerMetricsText {
  for ($attempt = 1; $attempt -le 3; $attempt++) {
    try {
      return (Invoke-WebRequest -Uri 'http://localhost:14269/metrics' -UseBasicParsing -TimeoutSec 20 -ErrorAction Stop).Content
    } catch {
      if ($attempt -lt 3) { Start-Sleep -Seconds 2 }
    }
  }
  return $null
}

function Get-JaegerDeliveryCounters {
  param([string]$ServiceName)
  $text = Get-JaegerMetricsText
  if ($null -eq $text) { return $null }
  $svc = [regex]::Escape($ServiceName)

  $received = 0L
  $rxRecv = '(?m)^jaeger_collector_spans_received_total\{[^}]*svc="' + $svc + '"[^}]*\}\s+(\S+)\s*$'
  foreach ($m in [regex]::Matches($text, $rxRecv)) { $received += [long][double]$m.Groups[1].Value }

  $saved = 0L
  $rxSaved = '(?m)^jaeger_collector_spans_saved_by_svc_total\{(?=[^}]*result="ok")(?=[^}]*svc="' + $svc + '")[^}]*\}\s+(\S+)\s*$'
  foreach ($m in [regex]::Matches($text, $rxSaved)) { $saved += [long][double]$m.Groups[1].Value }

  $dropped = 0L
  $rxDrop = '(?m)^jaeger_collector_spans_dropped_total\{[^}]*\}\s+(\S+)\s*$'
  foreach ($m in [regex]::Matches($text, $rxDrop)) { $dropped += [long][double]$m.Groups[1].Value }

  return @{ Received = $received; Saved = $saved; Dropped = $dropped }
}

function Get-ServiceNameForVariant {
  param([string]$Variant)
  if ($Variant -eq 'baseline' -or $Variant -eq 'k-perf') { return $null }
  return "comparison-$Variant"
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
      $mpsFromTrace = [Math]::Floor($lineCount / 2 / $StepCount)
      $methodsPerStepFromTrace[$vp.Platform] = $mpsFromTrace
      $formulaVal = $methodsPerStep[$vp.Platform]
      $deltaPct = if ($formulaVal -gt 0) { 100.0 * [Math]::Abs($mpsFromTrace - $formulaVal) / $formulaVal } else { 0.0 }
      $colour = if ($deltaPct -gt 1.0) { 'Yellow' } else { 'Cyan' }
      Write-Host ("  Preserved trace $safeName.txt ($lineCount lines) -> methods/step from trace = {0:N0}; formula = {1:N0}; delta = {2:N2}%" -f $mpsFromTrace, $formulaVal, $deltaPct) -ForegroundColor $colour
      if ($deltaPct -gt 1.0) {
        Write-Host ("  WARN: k-perf trace methods/step ({0}) diverges from formula ({1}) by {2:N2}% on $($vp.Platform). Investigate before trusting the overhead numbers." -f $mpsFromTrace, $formulaVal, $deltaPct) -ForegroundColor Yellow
      }
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

$nonOtelExecutables = $executables | Where-Object { $_.Name -notmatch 'otel' }
$otelExecutables    = $executables | Where-Object { $_.Name -match 'otel' }

Write-Host ""
Write-Host "=========================================="
Write-Host "PHASE 1 of 2: non-OTel variants ($($nonOtelExecutables.Count) rows). Jaeger NOT running."
Write-Host "=========================================="

$otelPhaseBannerShown = $false
foreach ($exe in (@($nonOtelExecutables) + @($otelExecutables))) {
  Write-Host ""

  if ($exe.Name -match 'otel') {
    if (-not $otelPhaseBannerShown) {
      Write-Host ""
      Write-Host "=========================================="
      Write-Host "PHASE 2 of 2: OTel variants ($($otelExecutables.Count) rows). Restarting Jaeger per variant."
      Write-Host "=========================================="
      $otelPhaseBannerShown = $true
    }
    Start-Jaeger
  }

  Write-Host "--- Benchmarking: $($exe.Name) ---"
  $invocation = "$($exe.Command) $StepCount"

  # No discarded warmup runs — $WarmupCount is now applied per-run as a
  # step-index cutoff (the first $WarmupCount step indices of every measured
  # run are excluded from per-step statistics in Get-PerStepMedians/Mean
  # below).

  $vpForJaeger = Get-VariantAndPlatform -ExeName $exe.Name
  $svcForJaeger = Get-ServiceNameForVariant -Variant $vpForJaeger.Variant
  $jaegerCountersAtStart = $null
  if ($exe.Name -match 'otel' -and $null -ne $svcForJaeger) {
    $jaegerCountersAtStart = Get-JaegerDeliveryCounters -ServiceName $svcForJaeger
  }

  # Actual measurements
  $totalNanosList = @()
  $perRunStepNanos = @()
  $exportedSpansList = @()
  $exportBatchesList = @()
  $exportFailuresList = @()
  $exportFailedSpansList = @()
  $firstExportError = $null
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
      $extra = ''
      if ($null -ne $parsed.ExportedSpans) {
        $extra = "  exported={0:N0} batches={1:N0}" -f $parsed.ExportedSpans, $parsed.ExportBatches
        if ($null -ne $parsed.ExportFailures -and $parsed.ExportFailures -gt 0) {
          $extra += "  FAILED_BATCHES={0:N0}" -f $parsed.ExportFailures
          if ($null -ne $parsed.ExportFailedSpans) {
            $extra += " (~{0:N0} spans)" -f $parsed.ExportFailedSpans
          }
        }
      }
      Write-Host ("  Run {0}: total {1:N3} ms ({2} steps){3}" -f ($i+1), $totalMs, $parsed.StepNanos.Count, $extra) -ForegroundColor Green
      if ($null -ne $parsed.FirstExportError -and $null -eq $firstExportError) {
        $firstExportError = $parsed.FirstExportError
        Write-Host "  FIRST EXPORT ERROR: $firstExportError" -ForegroundColor Yellow
      }
      $totalNanosList += [double]$parsed.TotalNanos
      $perRunStepNanos += , @($parsed.StepNanos)
      if ($null -ne $parsed.ExportedSpans)  { $exportedSpansList  += [long]$parsed.ExportedSpans }
      if ($null -ne $parsed.ExportBatches)  { $exportBatchesList  += [long]$parsed.ExportBatches }
      if ($null -ne $parsed.ExportFailures) { $exportFailuresList += [long]$parsed.ExportFailures }
      if ($null -ne $parsed.ExportFailedSpans) { $exportFailedSpansList += [long]$parsed.ExportFailedSpans }
    }
    else {
      Write-Host "  Run $($i+1): Failed to parse time" -ForegroundColor Red
      Save-FailureOutput -Phase "run" -ExeName $exe.Name -Iteration ($i + 1) -RawOutput $outputStr
    }
  }

  $jaegerSpansReceivedDelta = $null
  $jaegerSpansSavedDelta = $null
  $jaegerSpansDroppedDelta = $null
  $jaegerBackendDied = $false
  if ($exe.Name -match 'otel' -and $null -ne $svcForJaeger) {
    Start-Sleep -Seconds 5
    $jaegerState = (Invoke-Docker -DockerArgs @('inspect', 'jaeger', '--format', '{{.State.OOMKilled}};{{.State.ExitCode}};{{.State.Status}}') -CaptureOutput) -join ''
    if ($jaegerState -match 'true' -or $jaegerState -match ';137;' -or $jaegerState -match ';exited') {
      $jaegerBackendDied = $true
      Write-Host "  BACKEND DIED during this variant (jaeger state: $jaegerState) — delivery counters and timings are INVALID." -ForegroundColor Red
    }
    $countersAtEnd = Get-JaegerDeliveryCounters -ServiceName $svcForJaeger
    if ($null -eq $countersAtEnd) {
      $jaegerBackendDied = $true
      Write-Host "  Jaeger /metrics unreachable at end-of-variant snapshot — marking row INVALID." -ForegroundColor Red
    }
    if ($null -ne $countersAtEnd -and $null -ne $jaegerCountersAtStart) {
      $jaegerSpansReceivedDelta = $countersAtEnd.Received - $jaegerCountersAtStart.Received
      $jaegerSpansSavedDelta    = $countersAtEnd.Saved    - $jaegerCountersAtStart.Saved
      $jaegerSpansDroppedDelta  = $countersAtEnd.Dropped  - $jaegerCountersAtStart.Dropped
    }
  }

  # Flatten per-step times across all runs, EXCLUDING the first $WarmupCount
  # step indices in each run (warmup steps per-run).
  $flatStepNanos = @()
  foreach ($runSteps in $perRunStepNanos) {
    for ($s = $WarmupCount; $s -lt $runSteps.Count; $s++) {
      $flatStepNanos += [double]$runSteps[$s]
    }
  }

  $totalStats = Get-BenchmarkStatistics -Values $totalNanosList
  $stepStats  = Get-BenchmarkStatistics -Values $flatStepNanos

  $exportedSpansSum  = ($exportedSpansList  | Measure-Object -Sum).Sum
  $exportBatchesSum  = ($exportBatchesList  | Measure-Object -Sum).Sum
  $exportFailuresSum = ($exportFailuresList | Measure-Object -Sum).Sum
  $exportFailedSpansSum = if ($exportFailedSpansList.Count -gt 0) { ($exportFailedSpansList | Measure-Object -Sum).Sum } else { $null }

  $allResults += [ordered]@{
    Executable                = $exe.Name
    Count                     = $totalStats.count
    TotalMeanNanos            = $totalStats.mean
    TotalMedianNanos          = $totalStats.median
    TotalStdDevNanos          = $totalStats.stddev
    TotalMinNanos             = $totalStats.min
    TotalMaxNanos             = $totalStats.max
    StepMeanNanos             = $stepStats.mean
    StepMedianNanos           = $stepStats.median
    StepStdDevNanos           = $stepStats.stddev
    StepMinNanos              = $stepStats.min
    StepMaxNanos              = $stepStats.max
    TotalsNanos               = $totalNanosList
    PerRunStepNanos           = $perRunStepNanos
    ExportedSpansSum          = $exportedSpansSum
    ExportBatchesSum          = $exportBatchesSum
    ExportFailuresSum         = $exportFailuresSum
    ExportFailedSpansSum      = $exportFailedSpansSum
    FirstExportError          = $firstExportError
    JaegerSpansReceivedDelta  = $jaegerSpansReceivedDelta
    JaegerSpansSavedDelta     = $jaegerSpansSavedDelta
    JaegerSpansDroppedDelta   = $jaegerSpansDroppedDelta
    JaegerBackendDied         = $jaegerBackendDied
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

# --- Per-step median curve ------------------------------------------------
# For each (variant, platform), compute the per-step median across runs
# (one median per step index, shape: StepCount). Then average the medians
# from index $WarmupCount to end as the headline "Step mean" — replaces
# the old opaque "first step ≤ 2× tail-median" steady-state detector.

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

# Build a per-(variant,platform) summary: per-step medians + mean step time
# computed over steps $WarmupCount..end. All values in ns; convert at
# display time.
foreach ($res in $allResults) {
  $medians = Get-PerStepMedians -PerRunStepValues $res.PerRunStepNanos
  $res['PerStepMedianNanos'] = $medians
  # Override the previously-flat StepMeanNanos (which already excludes
  # warmup step indices, see the flat slice above) with the
  # WarmupCount..end mean of the per-step median curve. Aligns with what
  # the Per-step median CSV emits as the "measured" region.
  $res['StepMeanNanos'] = Get-MeanFromIndex -Values $medians -Start $WarmupCount
}

# --- Per-method calculation -----------------------------------------------
# Two columns per row (both in ns/method):
#   1) PerMethodTotal = StepMeanNanos / methodsPerStep
#      Reproduces the naïve "average step time over total method calls"
#      whiteboard math. Includes the workload's own cost.
#   2) OverheadPerMethod = (StepMeanNanos − baseline.StepMeanNanos) / methodsPerStep
#      Delta vs the uninstrumented baseline on the same platform. This
#      isolates "what the plugin added".

$baselineStepByPlatform = @{}
foreach ($res in $allResults) {
  $vp = Get-VariantAndPlatform -ExeName $res.Executable
  if ($vp.Variant -eq 'baseline') {
    if ($null -ne $res.StepMeanNanos) { $baselineStepByPlatform[$vp.Platform] = $res.StepMeanNanos }
  }
}

$overheadRows = @()
foreach ($res in $allResults) {
  $vp = Get-VariantAndPlatform -ExeName $res.Executable
  if ($vp.Variant -eq 'baseline') { continue }

  $mps         = $methodsPerStep[$vp.Platform]
  $stepMean    = $res.StepMeanNanos
  $baselineRef = $baselineStepByPlatform[$vp.Platform]

  $perMethodTotal = $null
  if ($null -ne $stepMean -and $null -ne $mps -and $mps -gt 0) {
    $perMethodTotal = $stepMean / $mps
  }
  $overheadPerMethod = $null
  if ($null -ne $baselineRef -and $null -ne $stepMean -and $null -ne $mps -and $mps -gt 0) {
    $overheadPerMethod = ($stepMean - $baselineRef) / $mps
  }

  $overheadRows += [ordered]@{
    Variant              = $vp.Variant
    Platform             = $vp.Platform
    StepMeanNanos        = $stepMean
    BaselineStepNanos    = $baselineRef
    MethodsPerStep       = $mps
    PerMethodTotalNs     = $perMethodTotal
    OverheadPerMethodNs  = $overheadPerMethod
  }
}

# --- Emit per-step median CSV ---------------------------------------------
# One row per (variant, platform, step). Use this in Excel/Python to plot
# the per-step curve. Values in nanoseconds (divide by 1000 for µs).
# `is_warmup` flags the first WarmupCount step indices for clarity.
$csvPath = Join-Path $resultsDir "per_step_medians.csv"
$csvLines = @("variant,platform,step,median_ns,is_warmup")
foreach ($res in $allResults) {
  $vp = Get-VariantAndPlatform -ExeName $res.Executable
  $medians = $res.PerStepMedianNanos
  if ($null -eq $medians -or $medians.Count -eq 0) { continue }
  for ($i = 0; $i -lt $medians.Count; $i++) {
    $m = if ($null -ne $medians[$i]) { "{0:F0}" -f [double]$medians[$i] } else { "" }
    $isWarmup = if ($i -lt $WarmupCount) { 1 } else { 0 }
    $csvLines += "$($vp.Variant),$($vp.Platform),$i,$m,$isWarmup"
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
- **Warmup steps/run (discarded from stats):** $WarmupCount
- **Run Iterations:** $RunCount
- **Step Count (workload calls per process):** $StepCount
- **Measured steps per run:** $($StepCount - $WarmupCount)
- **Clean Build:** $CleanBuild
- **Run timeout (s):** $RunTimeoutSeconds
- **Variants:** $($Variants -join ', ')

## System Information
- **OS:** $($machineInfo.OS) $($machineInfo.OSArchitecture)
- **CPU:** $($machineInfo.CPU) ($($machineInfo.CPUCores) Cores / $($machineInfo.CPULogicalProcessors) Logical Processors)
- **RAM:** $($machineInfo.TotalRAMGB) GB
- **Java Version:** $($machineInfo.JavaVersion) ($($machineInfo.JavaDistribution))
- **Node Version:** $($machineInfo.NodeVersion)

## Hardware Overview Details
- **Device:** $($machineInfo.DeviceManufacturer) - $($machineInfo.DeviceModel)
- **Git Branch:** $($machineInfo.GitBranch)

## Methods per step

Closed-form: ``fib_call_count(fibDepth) + 2`` with ``fibDepth=$WorkloadFibDepth`` (i.e. ``2 * Fibonacci(fibDepth+1) - 1`` recursive calls plus 1 for ``bubbleSort`` plus 1 for ``workload`` itself). The k-perf trace column is empirical (``lines / 2 / StepCount``) and serves as a sanity check.

| Platform | methods_per_step (formula, used) | methods_per_step (k-perf trace, check) |
|---|---:|---:|
"@

foreach ($plat in @('JVM','JS','Native')) {
  $formulaVal = if ($methodsPerStep.ContainsKey($plat)) { "$($methodsPerStep[$plat])" } else { 'N/A' }
  $traceVal   = if ($methodsPerStepFromTrace.ContainsKey($plat)) { "$($methodsPerStepFromTrace[$plat])" } else { 'N/A' }
  $markdown += "`n| $plat | $formulaVal | $traceVal |"
}

$markdown += @"


## Execution Summary

``Mean step (µs)`` = mean of per-step medians from step index $WarmupCount to $($StepCount - 1) across $RunCount measured runs (first $WarmupCount step indices of each run discarded as warmup).

| Executable | Iterations | Total mean (ms) | Total median (ms) | Mean step (µs) | Step median (µs) | Step stddev (µs) |
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


## Per-method timings

| Variant | Platform | Mean step (µs) | Methods/step | Per-method (ns) = step / methods | Overhead/method (ns) = Δ vs baseline |
|---|---|---:|---:|---:|---:|
"@

foreach ($row in $overheadRows) {
  $stepMean       = if ($null -ne $row.StepMeanNanos)       { "{0:N2}" -f ($row.StepMeanNanos / 1000.0) }   else { "N/A" }
  $mps            = if ($null -ne $row.MethodsPerStep)      { "$($row.MethodsPerStep)" }                    else { "N/A" }
  $perMethodTotal = if ($null -ne $row.PerMethodTotalNs)    { "{0:N1}" -f $row.PerMethodTotalNs }           else { "N/A" }
  $overhead       = if ($null -ne $row.OverheadPerMethodNs) { "{0:N1}" -f $row.OverheadPerMethodNs }        else { "N/A" }
  $markdown += "`n| $($row.Variant) | $($row.Platform) | $stepMean | $mps | $perMethodTotal | $overhead |"
}

$expectedTotal = $methodsPerStep['JVM']  # all platforms share the formula value
$markdown += @"



Expected = ``methods/step × StepCount × RunCount`` = $expectedTotal × $StepCount × $RunCount = $($expectedTotal * $StepCount * $RunCount). Jaeger columns are per-variant deltas. **Delivered % = Stored (saved_ok) / Expected** (ground truth). Status: OK · LOSS · DUP · INVALID (Jaeger died — row untrustworthy) · OK (false-fail: N) (delivered, client threw on response read). `~` = client-side fallback when Jaeger metrics missing.

| Variant | Platform | Expected | Exported (attempted) | Failed (client) | Wire recv (Δ) | Stored saved_ok (Δ) | Dropped (Δ) | Delivered (%) | Dup | Status |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|:--|
"@

$exportErrorNotes = @()
foreach ($res in $allResults) {
  $vp = Get-VariantAndPlatform -ExeName $res.Executable
  if ($vp.Variant -eq 'baseline' -or $vp.Variant -eq 'k-perf') { continue }
  $expected = [long]$expectedTotal * [long]$StepCount * [long]$res.Count
  $expFmt = "{0:N0}" -f $expected

  $exportedNum    = $res.ExportedSpansSum
  $failedSpansNum = $res.ExportFailedSpansSum
  $recvNum        = $res.JaegerSpansReceivedDelta
  $savedNum       = $res.JaegerSpansSavedDelta
  $dropNum        = $res.JaegerSpansDroppedDelta
  $died           = [bool]$res.JaegerBackendDied

  $exportedFmt = if ($null -ne $exportedNum)    { "{0:N0}" -f $exportedNum }    else { 'N/A' }
  $failedFmt   = if ($null -ne $failedSpansNum) { "{0:N0}" -f $failedSpansNum } else { 'N/A' }
  $recvFmt     = if ($null -ne $recvNum)        { "{0:N0}" -f $recvNum }        else { 'N/A' }
  $savedFmt    = if ($null -ne $savedNum)       { "{0:N0}" -f $savedNum }       else { 'N/A' }
  $dropFmt     = if ($null -ne $dropNum)        { "{0:N0}" -f $dropNum }        else { 'N/A' }

  $deliveredPct = $null; $pctMark = ''
  if ($null -ne $savedNum -and $expected -gt 0) {
    $deliveredPct = 100.0 * $savedNum / $expected
  } elseif ($null -ne $exportedNum -and $expected -gt 0) {
    $failedForCalc = if ($null -ne $failedSpansNum) { [double]$failedSpansNum } else { 0.0 }
    $clientDelivered = [double]$exportedNum - $failedForCalc
    $deliveredPct = 100.0 * $clientDelivered / $expected
    $pctMark = '~'
  }
  $deliveredFmt = if ($null -ne $deliveredPct) { ("{0:N2}" -f $deliveredPct) + $pctMark } else { 'N/A' }

  $dupFmt = '—'
  if ($null -ne $recvNum -and $null -ne $exportedNum -and $exportedNum -gt 0 -and $recvNum -gt ($exportedNum * 1.01)) {
    $dupFmt = "{0:N2}x" -f ($recvNum / [double]$exportedNum)
  }

  $status = '—'
  if ($died) {
    $status = 'INVALID (backend died)'
  } elseif ($dupFmt -ne '—') {
    $status = 'DUP'
  } elseif ($null -ne $deliveredPct -and $deliveredPct -lt 99.0) {
    $status = 'LOSS'
  } elseif ($null -ne $savedNum -and $expected -gt 0 -and $savedNum -ge ($expected * 0.995) -and $null -ne $failedSpansNum -and $failedSpansNum -gt 0) {
    $status = "OK (false-fail: {0:N0})" -f $failedSpansNum
  } elseif ($null -ne $deliveredPct) {
    $status = 'OK'
  }

  $markdown += "`n| $($vp.Variant) | $($vp.Platform) | $expFmt | $exportedFmt | $failedFmt | $recvFmt | $savedFmt | $dropFmt | $deliveredFmt | $dupFmt | $status |"
  if ($null -ne $res.FirstExportError) {
    $exportErrorNotes += "- **$($vp.Variant) $($vp.Platform)** first export error: ``$($res.FirstExportError)``"
  }
}

if ($exportErrorNotes.Count -gt 0) {
  $markdown += "`n`nFirst swallowed export error per row (if any):`n" + ($exportErrorNotes -join "`n")
}

# --- Per-step curve table: median µs at selected step indices --------------
# Picks indices that span the warmup region (≤ WarmupCount) and the
# measured region (≥ WarmupCount). Edit the array if you want different
# resolution.
$curveStepIndices = @(
  0, 1, 2, 5, 10,
  $WarmupCount,
  ($WarmupCount + 5),
  ($WarmupCount + 10),
  ([Math]::Floor(($WarmupCount + $StepCount) / 2)),
  ($StepCount - 1)
)
$curveStepIndices = @($curveStepIndices | Where-Object { $_ -ge 0 -and $_ -lt $StepCount } | Sort-Object -Unique)
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

> Curve shape: JVM C1≈step 1-2, C2 hits later. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction. Step indices < $WarmupCount are discarded from the per-method statistics above.
"@

$markdown | Out-File $mdFile -Encoding utf8

Write-Host "Measurements and stats saved successfully to folder: `n -> $resultsDir"
Write-Host "  results.json          (raw + statistics)"
Write-Host "  results.md            (summary tables + per-step curve)"
Write-Host "  per_step_medians.csv  (long-form per-step medians for plotting)"
Write-Host "  traces/               (k-perf trace per platform, one preserved per variant)"
Write-Host "Benchmark evaluation finished."

Write-Host ""
Write-Host "=========================================="
Write-Host "Jaeger is still running at http://localhost:16686" -ForegroundColor Green
Write-Host "  - Every OTel variant produced StepCount x RunCount = $($StepCount * $RunCount) traces" -ForegroundColor Green
Write-Host "    (one per step thanks to Main.kt's per-step Context.root() reset)" -ForegroundColor Green
Write-Host "  - Each trace ~= 'methods/step' spans, fully renderable in the UI" -ForegroundColor Green
Write-Host "  - Storage is in-memory, capped at the most recent 400 traces" -ForegroundColor Green
Write-Host "  - Verify zero drops at: http://localhost:14269/metrics" -ForegroundColor Green
Write-Host "    (jaeger_collector_spans_dropped_total should be 0)" -ForegroundColor Green
Write-Host "  - gRPC traffic flows through the Envoy gRPC-Web proxy on :4317" -ForegroundColor Green
Write-Host "    (required for the Kotlin/JS gRPC-Web client to reach Jaeger)" -ForegroundColor Green
Write-Host "  - Shut everything down with: docker stop jaeger envoy" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green

Pop-Location
