# Captures CPU profiles for all 4 variants x 3 platforms (12 total) and emits
# one SUMMARY.md per profile next to the captured file.
#
# Tools used: JFR (built into JDK), `node --cpu-prof` (built into Node), samply
# + xperf (Windows ADK Performance Toolkit) for Native.
#
# Usage:
#   .\profiles\run-all-profiles.ps1                           # all 12, fresh capture
#   .\profiles\run-all-profiles.ps1 -Variants k-perf          # one variant only
#   .\profiles\run-all-profiles.ps1 -Platforms jvm,js         # two platforms only
#   .\profiles\run-all-profiles.ps1 -SkipCapture              # only re-render markdown from existing profiles
#   .\profiles\run-all-profiles.ps1 -TopN 50                  # bigger top-N tables

param(
  [string[]]$Variants  = @('k-perf','otel','otel-proto','otel-proto-timesource','otel-proto-anchored'),
  [string[]]$Platforms = @('jvm','js','native'),
  [int]$TopN           = 30,
  [switch]$SkipCapture,                       # render markdown from already-captured profiles
  [int]$WarmupRuns     = 1,                   # warmup runs before each profiled capture
  [int]$StepCount      = 500                  # workload() invocations per profiled run. Default 500
                                              # to give JFR (~20ms sampling) + cpuprof + PerfView
                                              # enough samples; single-step runs (~hundreds of ms)
                                              # produce empty JFR recordings.
)

$ErrorActionPreference = 'Stop'

# Resolve project root (parent of profiles/)
$ProjectRoot = Split-Path $PSScriptRoot -Parent
Set-Location $ProjectRoot

# Add tool dirs to PATH idempotently.
$toolDirs = @(
  'C:\Program Files\nodejs\',
  "$env:USERPROFILE\bin"                # PerfView.exe (Microsoft-signed, allowed by Smart App Control)
)
foreach ($d in $toolDirs) {
  if (-not (Test-Path $d)) { Write-Host "[warn] tool dir missing: $d" -ForegroundColor Yellow }
  elseif ($env:PATH -notlike "*$d*") { $env:PATH = "$d;$env:PATH" }
}

# Auto-download PerfView.exe if missing. PerfView is the recorder we use for native
# CPU sampling because samply.exe (its predecessor in this script) is unsigned and
# gets blocked by Smart App Control on Windows 11. PerfView is signed by Microsoft
# and fetched directly from microsoft/perfview's GitHub releases.
$PerfViewPath = "$env:USERPROFILE\bin\PerfView.exe"
if (-not (Test-Path $PerfViewPath)) {
  Write-Host "PerfView.exe not found; downloading from GitHub releases..." -ForegroundColor Yellow
  if (-not (Test-Path "$env:USERPROFILE\bin")) { New-Item -ItemType Directory -Path "$env:USERPROFILE\bin" -Force | Out-Null }
  try {
    $rel = Invoke-RestMethod -Uri 'https://api.github.com/repos/microsoft/perfview/releases/latest' -UseBasicParsing
    $asset = $rel.assets | Where-Object { $_.name -ieq 'PerfView.exe' } | Select-Object -First 1
    if (-not $asset) { throw "no PerfView.exe asset in latest release ($($rel.tag_name))" }
    Invoke-WebRequest -Uri $asset.browser_download_url -OutFile $PerfViewPath -UseBasicParsing
    Write-Host "Downloaded PerfView $($rel.tag_name) ($([Math]::Round((Get-Item $PerfViewPath).Length/1MB,1)) MB)" -ForegroundColor Green
  } catch {
    Write-Host "[warn] PerfView download failed: $($_.Exception.Message)" -ForegroundColor Yellow
    Write-Host "       Native captures will fail; install PerfView.exe manually at $PerfViewPath"
  }
}

# Detect elevation. Native CPU sampling needs admin (kernel ETW providers).
# We don't auto-elevate the whole script; instead we batch all native captures
# into one elevated cmd subprocess later (one UAC prompt total for the run).
$IsElevated = (New-Object Security.Principal.WindowsPrincipal([Security.Principal.WindowsIdentity]::GetCurrent())).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

# --- variant -> per-platform paths --------------------------------------------------------------
$variantSpec = @{
  'k-perf' = @{
    Display = 'k-perf'
    Jar     = 'kmp-examples\comparison-k-perf\build\lib\comparison-k-perf-jvm-0.1.0-flushEarly-false.jar'
    Js      = 'kmp-examples\comparison-k-perf\build\js\packages\comparison-k-perf-flushEarly-false\kotlin\comparison-k-perf-flushEarly-false.js'
    Exe     = 'kmp-examples\comparison-k-perf\build\bin\mingwX64\releaseExecutable\comparison-k-perf-flushEarly-false.exe'
    JsDir   = 'js-kperf'                                                      # legacy dir name (no hyphen)
    NeedsCollector = $false
  }
  'otel' = @{
    Display = 'otel (JSON/HTTP)'
    Jar     = 'kmp-examples\comparison-otel\build\lib\comparison-otel-jvm-1.0.0.jar'
    Js      = 'kmp-examples\comparison-otel\build\js\packages\comparison-otel\kotlin\comparison-otel.js'
    Exe     = 'kmp-examples\comparison-otel\build\bin\mingwX64\releaseExecutable\main.exe'
    JsDir   = 'js-otel'
    NeedsCollector = $true
  }
  'otel-proto' = @{
    Display = 'otel-proto (Protobuf/gRPC)'
    Jar     = 'kmp-examples\comparison-otel-proto\build\lib\comparison-otel-proto-jvm-1.0.0.jar'
    Js      = 'kmp-examples\comparison-otel-proto\build\js\packages\comparison-otel-proto\kotlin\comparison-otel-proto.js'
    Exe     = 'kmp-examples\comparison-otel-proto\build\bin\mingwX64\releaseExecutable\main.exe'
    JsDir   = 'js-otel-proto'
    NeedsCollector = $true
  }
  'otel-proto-timesource' = @{
    Display = 'otel-proto-timesource (Protobuf/gRPC + monotonic clock)'
    Jar     = 'kmp-examples\comparison-otel-proto-timesource\build\lib\comparison-otel-proto-timesource-jvm-1.0.0.jar'
    Js      = 'kmp-examples\comparison-otel-proto-timesource\build\js\packages\comparison-otel-proto-timesource\kotlin\comparison-otel-proto-timesource.js'
    Exe     = 'kmp-examples\comparison-otel-proto-timesource\build\bin\mingwX64\releaseExecutable\main.exe'
    JsDir   = 'js-otel-proto-timesource'
    NeedsCollector = $true
  }
  'otel-proto-anchored' = @{
    Display = 'otel-proto-anchored (Protobuf/gRPC + SDK AnchoredClock)'
    Jar     = 'kmp-examples\comparison-otel-proto-anchored\build\lib\comparison-otel-proto-anchored-jvm-1.0.0.jar'
    Js      = 'kmp-examples\comparison-otel-proto-anchored\build\js\packages\comparison-otel-proto-anchored\kotlin\comparison-otel-proto-anchored.js'
    Exe     = 'kmp-examples\comparison-otel-proto-anchored\build\bin\mingwX64\releaseExecutable\main.exe'
    JsDir   = 'js-otel-proto-anchored'
    NeedsCollector = $true
  }
}

# Suspect-search regexes used in every SUMMARY.md so cross-profile comparison is mechanical.
$suspectSearches = @(
  @{ Title = 'Clock / time-reading frames';     Regex = 'now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant' },
  @{ Title = 'Persistent-list / O(n^2) lookups'; Regex = 'AbstractPersistentList|AbstractList\.indexOf|recyclableRemoveAll|protoOf\.c2|protoOf\.e2' },
  @{ Title = 'Long-polyfill arithmetic (JS only)'; Regex = '^subtract$|^divide$|^multiply$|^bitwiseAnd$|^lessThan$|^equalsLong$' },
  @{ Title = 'OTel SDK Span construction';      Regex = 'Span\.<init>|SdkSpanBuilder|RecordEventsReadableSpan|BatchSpanProcessor' }
)

# --- helpers ------------------------------------------------------------------------------------

# PS 5.1 + Stop-EAP wraps any stderr line from a native command as a NativeCommandError; relax
# locally and use $LASTEXITCODE to detect real failures. Same trick the comparison script uses.
function Invoke-Native {
  param([scriptblock]$Block)
  $prev = $ErrorActionPreference
  $ErrorActionPreference = 'Continue'
  try { & $Block } finally { $ErrorActionPreference = $prev }
}

# Looks at workload stdout for the marker line our examples print. Returns null if not found.
function Parse-WorkloadMs {
  param([string]$Output)
  $m = [regex]::Match($Output, 'Flush finished - (\d+) ms elapsed')
  if ($m.Success) { return [int]$m.Groups[1].Value }
  $m = [regex]::Match($Output, 'Execution finished - (\d+) ms elapsed')
  if ($m.Success) { return [int]$m.Groups[1].Value }
  return $null
}

# k-perf writes trace_*.txt + symbols_*.txt to cwd at the end of every run; clean up so the
# project root doesn't accumulate clutter between captures.
function Remove-KperfTraceLeftovers {
  Get-ChildItem -LiteralPath $ProjectRoot -Filter 'trace_*.txt'     -ErrorAction SilentlyContinue | Remove-Item -Force -ErrorAction SilentlyContinue
  Get-ChildItem -LiteralPath $ProjectRoot -Filter 'symbols_*.txt'   -ErrorAction SilentlyContinue | Remove-Item -Force -ErrorAction SilentlyContinue
  Get-ChildItem -LiteralPath $ProjectRoot -Filter 'trace_Native*'   -ErrorAction SilentlyContinue | Remove-Item -Force -ErrorAction SilentlyContinue
  Get-ChildItem -LiteralPath $ProjectRoot -Filter 'symbols_Native*' -ErrorAction SilentlyContinue | Remove-Item -Force -ErrorAction SilentlyContinue
}

# Boot whichever otel-collector container is currently parked on :4317/:4318 (we don't recreate
# it here -- that's the comparison runner's job. We just confirm something is listening).
function Ensure-CollectorReachable {
  $probe = Test-NetConnection -ComputerName '127.0.0.1' -Port 4317 -WarningAction SilentlyContinue -InformationLevel Quiet
  if (-not $probe) {
    Write-Host '[warn] :4317 is not reachable. OTel variants will hang on gRPC export.' -ForegroundColor Yellow
    Write-Host '       Boot the collector (e.g. via benchmarking\kperf-otel-comparison.ps1, which also creates the container) before running this script.'
    throw 'OTel collector not reachable on :4317'
  }
}

# Run an analyzer and return its stdout as a single string; abort the whole run if analyzer fails
# (a broken analyzer would silently make every SUMMARY.md misleading).
function Run-Analyzer {
  param([string]$Script, [string]$Profile, [int]$Top)
  $out = Invoke-Native { & node $Script $Profile $Top 2>&1 }
  if ($LASTEXITCODE -ne 0) { throw "analyzer $Script failed (exit $LASTEXITCODE) on $Profile" }
  return ($out -join "`n")
}

function Run-FrameSearch {
  param([string]$Script, [string]$Profile, [string]$Regex, [int]$TopCallers = 5)
  $out = Invoke-Native { & node $Script $Profile $Regex $TopCallers 2>&1 }
  if ($LASTEXITCODE -ne 0) { return "[search failed: exit $LASTEXITCODE]" }
  return ($out -join "`n")
}

# --- per-platform capture functions -------------------------------------------------------------

function Capture-Jvm {
  param([hashtable]$Spec, [string]$ProfilePath)
  if (-not (Test-Path $Spec.Jar)) { throw "jar missing: $($Spec.Jar) -- build the variant first" }
  for ($i = 0; $i -lt $WarmupRuns; $i++) { $null = Invoke-Native { & java -jar $Spec.Jar $StepCount 2>&1 } }
  $sw = [Diagnostics.Stopwatch]::StartNew()
  $out = Invoke-Native { & java "-XX:StartFlightRecording=settings=profile,filename=$ProfilePath" -jar $Spec.Jar $StepCount 2>&1 }
  $sw.Stop()
  return @{ Wall = $sw.ElapsedMilliseconds; Workload = (Parse-WorkloadMs ($out -join "`n")); Stdout = $out }
}

function Capture-Js {
  param([hashtable]$Spec, [string]$ProfileDir, [string]$ProfileName)
  if (-not (Test-Path $Spec.Js)) { throw "js bundle missing: $($Spec.Js) -- build the variant first" }
  if (-not (Test-Path $ProfileDir)) { New-Item -ItemType Directory -Path $ProfileDir -Force | Out-Null }
  for ($i = 0; $i -lt $WarmupRuns; $i++) { $null = Invoke-Native { & node $Spec.Js $StepCount 2>&1 } }
  $sw = [Diagnostics.Stopwatch]::StartNew()
  # Absolute paths -- Node mangles relative paths under --cpu-prof (see profiles/REPORT.md section 8).
  $absDir = (Resolve-Path $ProfileDir).Path
  $out = Invoke-Native { & node '--cpu-prof' "--cpu-prof-dir=$absDir" "--cpu-prof-name=$ProfileName" $Spec.Js $StepCount 2>&1 }
  $sw.Stop()
  return @{ Wall = $sw.ElapsedMilliseconds; Workload = (Parse-WorkloadMs ($out -join "`n")); Stdout = $out }
}

function Capture-Native {
  param([hashtable]$Spec, [string]$EtlPath)
  # Two-step PerfView capture:
  #   1. Record kernel CPU samples while the workload runs (ADMIN REQUIRED).
  #   2. Export the resulting ETL into a per-process XML stacks zip (no admin).
  # Step 1 is elevated externally via Capture-NativeBatch when the parent shell
  # is non-elevated (one UAC prompt for all 4 native variants); when this
  # function runs from an already-elevated shell we record inline.
  if (-not (Test-Path $Spec.Exe)) { throw "exe missing: $($Spec.Exe) -- build the variant first" }
  if (-not (Test-Path $PerfViewPath)) { throw "PerfView.exe missing at $PerfViewPath" }
  Remove-KperfTraceLeftovers

  $capLog = $EtlPath -replace '\.etl\.zip$', '.perfview.log'
  $expLog = $EtlPath -replace '\.etl\.zip$', '.export.log'

  if (-not (Test-Path $EtlPath)) {
    # Inline capture path (elevated parent shell).
    if (-not $IsElevated) {
      throw "no .etl.zip at $EtlPath and parent shell not elevated -- run Capture-NativeBatch first or relaunch as admin"
    }
    for ($i = 0; $i -lt $WarmupRuns; $i++) { $null = Invoke-Native { & $Spec.Exe $StepCount 2>&1 } }
    Remove-KperfTraceLeftovers
    $sw = [Diagnostics.Stopwatch]::StartNew()
    Invoke-Native { & $PerfViewPath /AcceptEULA /NoGui "/LogFile=$capLog" "/DataFile=$EtlPath" /BufferSizeMB=256 /CircularMB=500 run "$($Spec.Exe) $StepCount" 2>&1 | Out-Null }
    $sw.Stop()
    $wall = $sw.ElapsedMilliseconds
  } else {
    # Capture was already done by Capture-NativeBatch in the elevated subprocess.
    $wall = 0
  }

  # Step 2: export ETL -> XML stacks, filtered to the workload's process name.
  # PerfView writes <input-base>.perfView.xml.zip next to the input ETL.
  $procName = [System.IO.Path]::GetFileNameWithoutExtension($Spec.Exe)
  Invoke-Native { & $PerfViewPath /AcceptEULA /NoGui "/LogFile=$expLog" UserCommand SaveCPUStacks "$EtlPath" "$procName" 2>&1 | Out-Null }
  $xmlPath = $EtlPath -replace '\.etl\.zip$', '.perfView.xml.zip'
  if (-not (Test-Path $xmlPath)) {
    throw "PerfView export did not produce $xmlPath (see $expLog)"
  }

  # Try to recover workload time from the PerfView capture log (records the run's
  # stdout). Marker line is the workload's "Execution finished - N ms" / "Flush finished".
  $workload = $null
  if (Test-Path $capLog) {
    $logTxt = Get-Content $capLog -Raw -ErrorAction SilentlyContinue
    if ($logTxt) { $workload = Parse-WorkloadMs $logTxt }
  }

  Remove-KperfTraceLeftovers
  return @{ Wall = $wall; Workload = $workload; Stdout = @() }
}

# Batch all 4 native PerfView captures into a single elevated cmd subprocess.
# This is how we keep UAC prompts down to one per script run when the user
# launches the script from a non-elevated shell.
function Capture-NativeBatch {
  param([array]$Jobs)   # [{ Name, Exe, EtlPath, CapLog }]
  if ($Jobs.Count -eq 0) { return }
  if ($IsElevated) { return }   # not needed; Capture-Native will record inline
  if (-not (Test-Path $PerfViewPath)) { throw "PerfView.exe missing at $PerfViewPath" }

  # Build a .cmd that runs each capture, with `>>$result` so the user can read
  # the elevated subprocess's progress after it finishes.
  $batPath = "$env:TEMP\pv-batch-$([guid]::NewGuid().ToString('N')).cmd"
  $resultPath = "$env:TEMP\pv-batch-$([guid]::NewGuid().ToString('N')).out"
  $lines = @('@echo off')
  $lines += "echo === PerfView native capture batch === > `"$resultPath`""
  foreach ($j in $Jobs) {
    # Pre-create the parent dir (cmd.exe can't expand vars there).
    $dir = Split-Path $j.EtlPath -Parent
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    # Warmup outside the capture so JIT/loader caches are warm.
    for ($i = 0; $i -lt $WarmupRuns; $i++) { $null = Invoke-Native { & $j.Exe $StepCount 2>&1 } }
    Remove-KperfTraceLeftovers
    $lines += "echo. >> `"$resultPath`""
    $lines += "echo === capturing $($j.Name) === >> `"$resultPath`""
    $lines += "`"$PerfViewPath`" /AcceptEULA /NoGui ""/LogFile=$($j.CapLog)"" ""/DataFile=$($j.EtlPath)"" /BufferSizeMB=256 /CircularMB=500 run ""$($j.Exe) $StepCount"" >> `"$resultPath`" 2>&1"
    $lines += "echo capture exit=%ERRORLEVEL% >> `"$resultPath`""
  }
  $lines += "echo. >> `"$resultPath`""
  $lines += "echo === BATCH DONE === >> `"$resultPath`""
  ($lines -join "`r`n") | Out-File -FilePath $batPath -Encoding ascii

  Write-Host "" -ForegroundColor Yellow
  Write-Host "Native captures need admin (kernel ETW). Triggering ONE UAC prompt for all $($Jobs.Count) captures..." -ForegroundColor Yellow
  Write-Host "Look for the UAC dialog -- the captures take ~5-30 s each." -ForegroundColor Yellow
  $proc = Start-Process cmd.exe -ArgumentList "/c","`"$batPath`"" -Verb RunAs -Wait -PassThru
  Write-Host "Elevated batch finished (exit=$($proc.ExitCode))." -ForegroundColor Yellow
  Remove-Item $batPath -ErrorAction SilentlyContinue
  if (Test-Path $resultPath) {
    Write-Host "--- elevated batch tail ---" -ForegroundColor DarkGray
    Get-Content $resultPath -ErrorAction SilentlyContinue | Select-Object -Last 12 | ForEach-Object { Write-Host "  $_" -ForegroundColor DarkGray }
    Remove-Item $resultPath -ErrorAction SilentlyContinue
  }
  Remove-KperfTraceLeftovers
}

# --- markdown writer ----------------------------------------------------------------------------

function Write-Summary {
  param(
    [string]$OutPath,
    [string]$VariantName,
    [string]$VariantDisplay,
    [string]$Platform,
    [string]$ProfilePath,
    [hashtable]$CaptureResult,
    [string]$AnalyzerOutput,
    [array]$SearchResults,
    [bool]$ReusedStale = $false
  )
  $relProfile = (Resolve-Path -Relative $ProfilePath)
  $rel = (Resolve-Path -Relative (Split-Path $ProfilePath -Parent))
  $now = Get-Date -Format 'yyyy-MM-dd HH:mm:ss'
  $profileWritten = (Get-Item $ProfilePath).LastWriteTime.ToString('yyyy-MM-dd HH:mm:ss')
  $wall = if ($null -ne $CaptureResult) { "$($CaptureResult.Wall) ms (incl. profiler overhead)" } else { 'n/a (capture skipped or failed)' }
  $workload = if ($null -ne $CaptureResult -and $null -ne $CaptureResult.Workload) { "$($CaptureResult.Workload) ms" } else { 'unknown (no marker line in stdout)' }

  $sb = [System.Text.StringBuilder]::new()
  [void]$sb.AppendLine("# Profile -- $VariantDisplay ($Platform)")
  [void]$sb.AppendLine('')
  if ($ReusedStale) {
    [void]$sb.AppendLine('> WARNING: capture failed in this run; analyzing the previously captured profile instead.')
    [void]$sb.AppendLine("> Profile file was last written at **$profileWritten** -- not from the most recent ``run-all-profiles.ps1`` invocation.")
    [void]$sb.AppendLine('> Common cause on Windows 11: Smart App Control blocks unsigned `samply.exe`.')
    [void]$sb.AppendLine('')
  }
  [void]$sb.AppendLine("**Variant:** ``$VariantName``  ")
  [void]$sb.AppendLine("**Platform:** $Platform  ")
  [void]$sb.AppendLine("**SUMMARY rendered:** $now  ")
  [void]$sb.AppendLine("**Profile file last captured:** $profileWritten  ")
  [void]$sb.AppendLine("**Profile file:** [$([System.IO.Path]::GetFileName($ProfilePath))]($([System.IO.Path]::GetFileName($ProfilePath)))  ")
  [void]$sb.AppendLine("**Wall time (capture run):** $wall  ")
  [void]$sb.AppendLine("**Workload-reported time:** $workload  ")
  [void]$sb.AppendLine('')
  [void]$sb.AppendLine('---')
  [void]$sb.AppendLine('')
  [void]$sb.AppendLine("## Top $TopN frames")
  [void]$sb.AppendLine('')
  [void]$sb.AppendLine('```')
  [void]$sb.AppendLine($AnalyzerOutput.Trim())
  [void]$sb.AppendLine('```')
  [void]$sb.AppendLine('')
  [void]$sb.AppendLine('## Targeted suspect searches')
  [void]$sb.AppendLine('')
  foreach ($s in $SearchResults) {
    [void]$sb.AppendLine("### $($s.Title)")
    [void]$sb.AppendLine('')
    [void]$sb.AppendLine("Regex: ``$($s.Regex)``")
    [void]$sb.AppendLine('')
    [void]$sb.AppendLine('```')
    [void]$sb.AppendLine($s.Output.Trim())
    [void]$sb.AppendLine('```')
    [void]$sb.AppendLine('')
  }
  [void]$sb.AppendLine('---')
  [void]$sb.AppendLine('')
  [void]$sb.AppendLine('## How to view interactively')
  [void]$sb.AppendLine('')
  switch ($Platform) {
    'jvm'    { [void]$sb.AppendLine('Open ``' + [System.IO.Path]::GetFileName($ProfilePath) + '`` in **JDK Mission Control** (https://jdk.java.net/jmc/) or IntelliJ IDEA Ultimate.') }
    'js'     { [void]$sb.AppendLine('In Chrome/Edge: open DevTools -> Performance -> click the upload icon -> load ``' + [System.IO.Path]::GetFileName($ProfilePath) + '``. Or drag the file onto https://profiler.firefox.com.') }
    'native' { [void]$sb.AppendLine('Drag ``' + [System.IO.Path]::GetFileName($ProfilePath) + '`` onto https://profiler.firefox.com (or run ``samply load <file>``). App-code frames appear as ``0x<hex>`` because Kotlin/Native release builds drop ``-g`` on the ``-opt`` conflict; system DLL symbols resolve via Microsoft''s public symbol server.') }
  }
  Set-Content -Path $OutPath -Value $sb.ToString() -Encoding utf8
}

# --- main loop ----------------------------------------------------------------------------------

$wantOtel = $false
foreach ($v in $Variants) { if ($variantSpec[$v].NeedsCollector) { $wantOtel = $true; break } }
if ($wantOtel -and -not $SkipCapture) { Ensure-CollectorReachable }

# Native captures need admin (kernel ETW). If native is in scope and we're not
# elevated, batch all 4 PerfView captures into one elevated cmd subprocess
# (one UAC prompt total). Each per-variant Capture-Native call later just
# re-uses the already-captured ETL for the (non-admin) export step.
if ('native' -in $Platforms -and -not $SkipCapture) {
  if ($IsElevated) {
    Write-Host "Running elevated -- native captures will record inline." -ForegroundColor Green
  } else {
    $nativeJobs = @()
    foreach ($vName in $Variants) {
      if (-not $variantSpec.ContainsKey($vName)) { continue }
      $spec = $variantSpec[$vName]
      if (-not (Test-Path $spec.Exe)) {
        Write-Host "[warn] $vName native exe missing: $($spec.Exe) -- skipping native capture for it" -ForegroundColor Yellow
        continue
      }
      $dir = "profiles\native-$vName"
      $absDir = Join-Path $PWD $dir
      $nativeJobs += [pscustomobject]@{
        Name    = $vName
        Exe     = (Resolve-Path $spec.Exe).Path
        EtlPath = "$absDir\$vName.etl.zip"
        CapLog  = "$absDir\$vName.perfview.log"
      }
    }
    Capture-NativeBatch -Jobs $nativeJobs
  }
}

$startedAt = Get-Date
$summaries = @()

foreach ($vName in $Variants) {
  if (-not $variantSpec.ContainsKey($vName)) {
    Write-Host "[warn] unknown variant '$vName' -- skipping" -ForegroundColor Yellow
    continue
  }
  $spec = $variantSpec[$vName]

  foreach ($p in $Platforms) {
    Write-Host ('=' * 70) -ForegroundColor Cyan
    Write-Host "Variant: $vName  |  Platform: $p" -ForegroundColor Cyan
    Write-Host ('=' * 70) -ForegroundColor Cyan

    # Resolve dir + filename + analyzer per platform.
    switch ($p) {
      'jvm' {
        $dir       = "profiles\jvm-$vName"
        $profile   = "$dir\$vName.jfr"
        $analyzer  = 'profiles\jvm_analyze.js'
        $searcher  = 'profiles\jvm_find_frame.js'
      }
      'js' {
        $dir       = "profiles\$($spec.JsDir)"
        $jsName    = if ($vName -eq 'k-perf') { 'kperf' } else { $vName }
        $profile   = "$dir\$jsName.cpuprofile"
        $analyzer  = 'profiles\analyze.js'
        $searcher  = 'profiles\find_frame.js'
      }
      'native' {
        # `$profile` is the file the *analyzer* reads (the XML stacks zip).
        # `$captureFile` is the raw ETL the *recorder* produces. Capture-Native
        # consumes the ETL and writes the XML next to it (PerfView's naming).
        $dir         = "profiles\native-$vName"
        $captureFile = "$dir\$vName.etl.zip"
        $profile     = "$dir\$vName.perfView.xml.zip"
        $analyzer    = 'profiles\native_analyze.js'
        $searcher    = 'profiles\native_find_frame.js'
      }
      default { Write-Host "[warn] unknown platform '$p' -- skipping" -ForegroundColor Yellow; continue }
    }
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }

    # Capture (or reuse). On capture failure, fall back to reusing any prior
    # profile rather than leaving a hole in the run -- this is the right
    # behaviour for e.g. Windows Smart App Control blocking samply.exe (every
    # native capture fails, but the prior .json.gz from an earlier session is
    # still informative). The SUMMARY.md captures the staleness explicitly.
    $capture = $null
    $reusedStale = $false
    if (-not $SkipCapture) {
      try {
        switch ($p) {
          'jvm'    { $capture = Capture-Jvm    $spec $profile }
          'js'     { $capture = Capture-Js     $spec $dir ([System.IO.Path]::GetFileName($profile)) }
          'native' { $capture = Capture-Native $spec $captureFile }
        }
        Remove-KperfTraceLeftovers
        Write-Host ("  captured  wall={0} ms  workload={1} ms  -> {2}" -f $capture.Wall, $capture.Workload, $profile) -ForegroundColor Green
      } catch {
        Write-Host "  [error] capture failed: $($_.Exception.Message)" -ForegroundColor Red
        if (Test-Path $profile) {
          $age = (Get-Date) - (Get-Item $profile).LastWriteTime
          Write-Host ("  fall-back: reusing existing profile from {0:N1} h ago: {1}" -f $age.TotalHours, $profile) -ForegroundColor Yellow
          $reusedStale = $true
        } else {
          Write-Host "  [skip] no prior profile available either; nothing to summarise" -ForegroundColor Yellow
          continue
        }
      }
    } else {
      if (-not (Test-Path $profile)) { Write-Host "  [skip] no existing profile at $profile" -ForegroundColor Yellow; continue }
      Write-Host "  reusing existing $profile (-SkipCapture)" -ForegroundColor DarkGray
    }

    # Run analyzer + suspect searches. If the analyzer dies (e.g. an empty
    # recording or a malformed profile), don't kill the whole batch -- record
    # the failure in the SUMMARY.md and move on.
    try {
      $top  = Run-Analyzer $analyzer $profile $TopN
    } catch {
      Write-Host "  [warn] analyzer failed: $($_.Exception.Message)" -ForegroundColor Yellow
      $top = "[analyzer failed]`n$($_.Exception.Message)"
    }
    $searches = foreach ($s in $suspectSearches) {
      [pscustomobject]@{
        Title  = $s.Title
        Regex  = $s.Regex
        Output = (Run-FrameSearch $searcher $profile $s.Regex)
      }
    }

    # Write per-profile SUMMARY.md
    $summaryPath = Join-Path $dir 'SUMMARY.md'
    Write-Summary -OutPath $summaryPath `
                  -VariantName $vName -VariantDisplay $spec.Display -Platform $p `
                  -ProfilePath $profile -CaptureResult $capture `
                  -AnalyzerOutput $top -SearchResults $searches `
                  -ReusedStale $reusedStale
    Write-Host "  summary -> $summaryPath" -ForegroundColor Green

    $summaries += [pscustomobject]@{
      Variant  = $vName
      Platform = $p
      Profile  = $profile
      Summary  = $summaryPath
      Wall     = if ($capture) { $capture.Wall } else { $null }
      Workload = if ($capture) { $capture.Workload } else { $null }
    }
  }
}

# --- index file ---------------------------------------------------------------------------------
$indexPath = 'profiles\INDEX.md'
$idx = [System.Text.StringBuilder]::new()
[void]$idx.AppendLine('# Profile Run Index')
[void]$idx.AppendLine('')
[void]$idx.AppendLine("Generated $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') by ``profiles/run-all-profiles.ps1``.")
[void]$idx.AppendLine('')
[void]$idx.AppendLine('| Variant | Platform | Workload | Capture wall | Profile | Summary |')
[void]$idx.AppendLine('|---|---|---:|---:|---|---|')
foreach ($s in $summaries) {
  $wl   = if ($null -ne $s.Workload) { "$($s.Workload) ms" } else { '--' }
  $wall = if ($null -ne $s.Wall)     { "$($s.Wall) ms"     } else { '--' }
  $relProfile = (Resolve-Path -Relative $s.Profile) -replace '\\','/'
  $relSummary = (Resolve-Path -Relative $s.Summary) -replace '\\','/'
  # Strip both `./profiles/` (PS Resolve-Path style) and `profiles/` (raw) prefix
  # so links resolve from INDEX.md, which sits inside profiles/.
  $relProfile = $relProfile -replace '^\./profiles/','' -replace '^profiles/',''
  $relSummary = $relSummary -replace '^\./profiles/','' -replace '^profiles/',''
  [void]$idx.AppendLine("| $($s.Variant) | $($s.Platform) | $wl | $wall | [$([System.IO.Path]::GetFileName($s.Profile))]($relProfile) | [SUMMARY.md]($relSummary) |")
}
[void]$idx.AppendLine('')
[void]$idx.AppendLine('See [REPORT.md](REPORT.md) for the synthesised cross-platform analysis.')
Set-Content $indexPath -Value $idx.ToString() -Encoding utf8

$elapsed = (Get-Date) - $startedAt
Write-Host ''
Write-Host ('=' * 70) -ForegroundColor Cyan
Write-Host ("Done. {0} profiles processed in {1:N1} s." -f $summaries.Count, $elapsed.TotalSeconds) -ForegroundColor Cyan
Write-Host "Index: $indexPath" -ForegroundColor Cyan
Write-Host ('=' * 70) -ForegroundColor Cyan
