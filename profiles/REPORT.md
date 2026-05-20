# Profiling Findings — k-perf vs OTel Variants

12 CPU profiles captured: 4 variants (`k-perf`, `otel`, `otel-proto`, `otel-proto-timesource`) × 3 platforms (JVM, JS Node, Native mingwX64). Tools: JFR (JVM), `node --cpu-prof` (JS), PerfView (Native). Analyzers under this dir; raw profiles under `{js,jvm,native}-<variant>/`.

---

## 1. Headline findings

### 1.1 dcxp `BatchSpanProcessor.removeSpanDataFromBatch` is O(n²)

`BatchSpanProcessor` removes the just-exported batch from the queue with `queue.removeAll(exportedBatch)`, where `exportedBatch` is a `List`. `removeAll` delegates to `removeAll { exportedBatch.contains(it) }` — each `contains()` is O(n). With `maxExportBatchSize=512` and ~243 k spans / ~475 export cycles, that's ~125 M comparisons.

| Platform | Self-time on persistent-list ops | % of run |
|---|---:|---:|
| JS otel-proto | 1035 ms | **10.7%** |
| JS otel-proto-timesource | 1212 ms | **10.1%** |
| JVM otel-proto (in-stack) | 440 ms / 1130 ms sampled | **38%** |
| JVM otel-proto-timesource | 380 ms / 1370 ms sampled | **28%** |
| Native | not symbolicated, but same call chain |

Fix: `queue.removeAll(exportedBatch.toSet())` upstream in dcxp's port.

### 1.2 Clock cost is universally <1%

Regex search for `now|markNow|elapsed|nanoTime|hrtime|currentTimeMillis|Instant` across all 12 profiles:

| Variant | JS | JVM |
|---|---:|---:|
| k-perf | 50 ms (1.7%) | 0 ms |
| otel | 58 ms (0.3%) | 0 ms |
| otel-proto | 48 ms (0.5%) | 30 ms (0.7%) |
| otel-proto-timesource | 67 ms (0.6%) | 20 ms (1.5%) |

Native: <1% (only system DLL clock calls visible). The TimeSource variant matches `otel-proto` within rounding on every platform.

### 1.3 JSON serialization is ~25% of JVM `otel`

JFR profile of JVM otel (4.1 s sampled):

| Self ms | %    | Function |
|--------:|-----:|----------|
| 650     | 16%  | `sun.nio.cs.UTF_8$Encoder.encodeBufferLoop` |
| 350     |  9%  | `JsonToStringWriter.writeQuoted` |
| 260     |  6%  | `Intrinsics.throwParameterIsNullNPE` |
| 180     |  4%  | `StreamingJsonEncoder.beginStructure` |

Total inclusive `CharsetEncoder.encode` = 710 ms / 4100 ms = ~25%. The proto variants entirely sidestep this. The JSON→Proto switch saved CPU, not just bandwidth.

### 1.4 JS Long polyfill is JS-only, ~7-8% of CPU

`Long.subtract|divide|multiply|bitwiseAnd|lessThan|equalsLong`:

| Variant | JS self ms | % of run |
|---|---:|---:|
| k-perf | 80 ms | 2.7% |
| otel | 1279 ms | 5.7% (JSON dominates) |
| otel-proto | 760 ms | **7.8%** |
| otel-proto-timesource | 905 ms | **7.5%** |
| JVM/Native | 0 ms | 0% (real 64-bit ints) |

Span IDs and nanosecond timestamps are `Long`; Kotlin compiles `Long` to a 2-Int polyfill class on JS where every arithmetic op is a function call.

### 1.5 `Span.<init>` is ~13% of JVM `otel-proto`

Per-call construction of `io.opentelemetry.proto.trace.v1.Span` allocates lists for repeated fields and interns strings. Span pooling could halve this.
---

## 2. Native per-module attribution (compensates for missing symbols)

Time spent inside each loaded binary:

| Module | k-perf (1.5 s) | otel (8.2 s) | otel-proto (7.0 s) | otel-proto-timesource (7.1 s) |
|---|---:|---:|---:|---:|
| **App exe** | **20%** | **74%** | **85%** | **84%** |
| `ntoskrnl.exe` | 40% | 2% | 1% | 1% |
| `FLTMGR.SYS` | 13% | 0% | 0% | 0% |
| `Ntfs.sys` | 5% | 0% | 0% | 0% |
| `WdFilter.sys` | 3% | 0% | 0% | 0% |
| `ntdll.dll` | 10% | 16% | 7% | 7% |
| `kernel32.dll` | 2% | 8% | 7% | 7% |

- k-perf Native is dominated by **kernel-side file I/O** (~70% in kernel/filter drivers) — that's `flushEarly=true` doing a sync write per span boundary.
- All OTel variants are user-mode-bound (~85% in app code).
- `otel` shows more `ntdll.dll` time than the proto variants — consistent with JSON allocation churn.

---

## 3. Top JS self-time tables

**otel-proto** (9.7 s wall):

| Self ms | Function |
|--------:|----------|
| 819 | `AbstractList.indexOf` (the O(n²)) |
| 676 | `toTypedArray` (proto serialization) |
| 484 | Ktor HTTP/2 coroutine state machine |
| 422 | GC |
| 385 | `equals` |
| 378 | `arrayCopy` |
| 214 | `AbstractPersistentList.contains` |
| 199 | `Long.subtract` |

**otel** (22 s wall, profiler overhead heavier):

| Self ms | Function |
|--------:|----------|
| 5320 | `toTypedArray` |
| 3753 | Ktor HTTP/2 coroutine |
| 1151 | `recyclableRemoveAll` (the O(n²)) |
| 994 | `encodeUtf8` |
| 753 | `charCodeAt` |
| 663 | GC |

**k-perf** (3.0 s wall):

| Self ms | Function |
|--------:|----------|
| 1507 | native `writeBuffer` (sync flush per span) |
| 307 | `Segment_init` (kotlinx-io buffer plumbing) |
| 171 | GC |
| 40  | `hrtime` (1.3% — even on the leanest pipeline, clock isn't a lever) |

---

## 4. Cross-platform synthesis

| Cost bucket | JS otel-proto | JVM otel-proto | Native otel-proto |
|---|---:|---:|---:|
| Persistent-list O(n²) | 10.7% | 38% in-stack | unresolved |
| Long arithmetic | 7.8% | 0% | 0% |
| Protobuf serialization (`toTypedArray` + encoding) | 7.0% | ~5% | unresolved |
| `Span.<init>` allocation | 3% | 13% | unresolved |
| GC | 4.3% | (in JIT) | n/a |
| HTTP/2 client | 5% | small | small |
| Clock | 0.5% | <1% | <1% |

**Generalises across platforms:** the persistent-list bug, clock irrelevance, timesource ≈ otel-proto.
**Platform-specific:** Long polyfill (JS), JSON cost on JVM otel, k-perf's I/O profile shape.

---

## 5. Kickoff items final status

| Item | Status |
|---|---|
| #5 Clock | Closed. <1% on every platform. Negative result. |
| #6 TimeSource.Monotonic | Closed. Implemented as `otel-plugin-proto-timesource`; ~0% wall-time delta because clock cost is dominated by other per-span work and the SDK's `AnchoredClock` already does monotonic-anchored timing internally. |
| #7 JSON → Protobuf | Closed positive. Profile shows JSON encoding is ~25% of JVM `otel` CPU; the proto variant entirely sidesteps it. |

---

## 6. Open candidates surfaced by profiling

1. **Patch dcxp `removeSpanDataFromBatch`** to convert the batch to a `Set`. Predicted: ~10% on JS, ~25-30% reduction in stack-time on JVM.
2. **Span pooling** for `Span.<init>` (~13% JVM otel-proto).
3. **JS-specific:** replace `Long` with `BigInt`/`Number` (53-bit) in span/trace ID and timestamp paths.
4. **Drop `Intrinsics.throwParameterIsNullNPE`** on JVM otel hot path (~6%).
5. **Structural:** bypass `SdkSpanBuilder`/`RecordEventsReadableSpan` on the hot path; emit a thin in-memory record from IR, inflate to OTel `Span` only at export time.

---

## 8. Files

| File | Purpose |
|------|---------|
| [analyze.js](analyze.js), [find_frame.js](find_frame.js) | V8 cpuprofile analyzers (JS) |
| [jvm_analyze.js](jvm_analyze.js), [jvm_find_frame.js](jvm_find_frame.js) | JFR analyzers (JVM) |
| [native_analyze.js](native_analyze.js), [native_find_frame.js](native_find_frame.js) | PerfView XML-stacks analyzers + per-module attribution (Native) |
| [run-all-profiles.ps1](run-all-profiles.ps1) | Captures all 12 profiles in one go and writes a `SUMMARY.md` next to each. Auto-downloads PerfView; for Native triggers one UAC prompt per run. |
| `js-{k-perf,otel,otel-proto,otel-proto-timesource}/*.cpuprofile` | 4 JS profiles — viewable in Chrome DevTools → Performance → Load |
| `jvm-{k-perf,otel,otel-proto,otel-proto-timesource}/*.jfr` | 4 JVM profiles — viewable in JDK Mission Control |
| `native-{k-perf,otel,otel-proto,otel-proto-timesource}/*.etl.zip` (raw) + `*.perfView.xml.zip` (parsed) | 4 Native profiles — open `.etl.zip` in PerfView GUI for flame charts |

Capture commands:
```powershell
# JVM
java -XX:StartFlightRecording=settings=profile,filename=<out>.jfr -jar <jar>

# JS
node --cpu-prof --cpu-prof-dir=<dir> --cpu-prof-name=<name>.cpuprofile <bundle.js>

# Native (requires elevated PowerShell or self-elevating subprocess)
# Step 1: capture (admin required for kernel ETW)
PerfView.exe /AcceptEULA /NoGui /LogFile=<out>.perfview.log /DataFile=<out>.etl.zip /BufferSizeMB=256 /CircularMB=500 run <exe>
# Step 2: export to XML stacks (no admin), filtered to the workload's process name
PerfView.exe /AcceptEULA /NoGui /LogFile=<out>.export.log UserCommand SaveCPUStacks <out>.etl.zip <process-name-without-extension>
```

Caveats: profiler overhead ~14% (JS), safepoint bias on JFR may under-sample tight JIT loops.

---

## 13. Post-fix re-profile (2026-05-20)

After the `setMaxExportBatchSize` typo fix landed in `otel-plugin-proto` and
`otel-plugin-proto-timesource` (commit `69e411d`, defaults now
`queueSize=2048, batchSize=Int.MAX_VALUE`), the picture changes materially.
The JSON `otel` baseline is unchanged on purpose — it still uses the
unpatched plugin.

Capture: `profiles/run-all-profiles.ps1 -StepCount 3000`. StepCount=3000 was
chosen because the post-fix otel-proto JVM workload now runs at ~0.2 ms per
step (was ~2 ms pre-fix) and JFR's 10 ms sampling produced fewer than 10
samples at the previous StepCount=1 default. Even at 3000 steps the
otel-proto JVM profile is sample-poor (5 samples) and the timesource
sibling (34 samples, same code path) is the more reliable read on JVM.

Native data in this section is partially stale: the .etl.zip recapture
needs UAC approval which wasn't granted on the StepCount=3000 re-run, so
the perfView.xml.zip artefacts are from the StepCount=1 sweep and contain
similar limitations. Re-run with admin elevation to refresh.

### 13.1 dcxp persistent-list / `removeSpanDataFromBatch` n² — essentially gone

Pre-fix (2026-05-04) `removeSpanDataFromBatch`-family frames
(`AbstractPersistentList.contains` / `recyclableRemoveAll` /
`TrieIterator.fillPathIfNeeded`) on JVM otel-proto: **~38% in-stack
time**. Post-fix (2026-05-20) on JVM otel-proto-timesource:
**1 sample / 34 ≈ 3%**.

Mechanism: with the bounded queue (2048), the persistent-list never grows
beyond that bound during a process. The n² cost cap moves from
~25M comparisons per export (unbounded × 512 batches) to at most
~4M (2048²) — and in practice well below because the queue is often
nearly empty post-drain.

**Implication: Phase 5 (dcxp fork to convert `removeAll(list)` →
`removeAll(set)`) is no longer on the critical path.** The fix would still
be correct upstream, but the queue cap has already extracted ~90% of the
available win. Skip Phase 5; spend that day on Phase 8 instead.

### 13.2 Trace-ID base16 validation is the new dominant frame

Pre-fix this was hidden under the n² persistent-list churn. Post-fix it
emerges as the top self-time frame on both JVM and JS.

**JVM otel-proto-timesource (34 samples)**: `java.lang.String.charAt` —
190 ms self, **19 / 34 samples (56%)**, called inclusively from
`OtelEncodingUtils.isValidBase16String` → `TraceId.isValid` →
`SpanContext.isValid`. The OTel SDK validates every trace ID's
hex-character set on every span creation; with ~180 spans per workload
step, that's ~540 000 base16 validations per StepCount=3000 run.

**JS otel-proto (1467 samples)**: `get_isValid` (api-all.js:102) —
67 ms self + dependent `charCodeAt` (75 ms) + `String` walking — together
~25 % of inclusive CPU on the same code path.

Fix would live in dcxp's `OtelEncodingUtils.isValidBase16String` (or its
caller in `SpanContext.isValid`). Easy upstream patch: skip validation for
SDK-generated trace IDs (they're always valid by construction). Worth a
PR. **Strong Phase 8 candidate.**

### 13.3 JS Long polyfill cost — relative share grew

Pre-fix: 7.8 % of JS otel-proto CPU on Long arithmetic (`add`, `multiply`,
`lessThan`, `subtract`, `shiftRight`, `bitwiseAnd`, `equalsLong`).
Post-fix: same regex matches ~19 % of the (much smaller) pie:

| Frame | self ms | hits |
|---|---:|---:|
| `add` | 104.8 | 65 |
| `multiply` | 69.6 | 43 |
| `subtract` | 46.8 | 28 |
| `lessThan` | 50.7 | 31 |
| `shiftRight` | 29.7 | 19 |
| `bitwiseAnd` | 28.9 | 18 |
| `equalsLong` | 25.6 | 16 |
| `millis` (@js-joda) | 156.2 | 96 |

The polyfill cost didn't shrink in absolute terms; the pie did. Translating
the JS span-ID / timestamp path from Kotlin `Long` to JS `Number` (53-bit
precision, fine for these magnitudes) would harvest ~15–20 % on JS. **A
Phase 8 candidate** (alongside the trace-ID validation fix).

### 13.4 OTel SDK span construction & context propagation — small but persistent

Steady ~10 % of JVM otel-proto-timesource CPU in:

- `SdkSpanBuilder.startSpan` (30 ms / 3 samples)
- `MainKt._startSpan` (30 ms self, 80 ms total — the IR-injected wrapper)
- `Context.getOrElse` / `ArrayBasedContext` / `Span.fromContext` (10 ms total)
- `Span.<init>` from the OTLP protobuf bindings (10 ms)
- `kmpgrpc CodedOutputStreamImpl.writeBytes` (10 ms — gRPC encoding)

No single dominant frame; consistent with the "bypass the SDK on the hot
path" idea (Path B / direct protobuf encoder, GEMINI Hypothesis 6). But
the predicted 10–12 % win is small now that the bigger fish (n², trace-ID
validation) is identified.

### 13.5 `Intrinsics.throwParameterIsNullNPE` — disappeared from top frames

Pre-fix (2026-05-04) this was 260 ms self / 6 % of JVM otel CPU. Post-fix
it appears 0 times in the top 30 of otel-proto-timesource and otel-proto.
Only `Intrinsics.sanitizeStackTrace` (10 ms / 1 sample / ~3 %) and
`Intrinsics.areEqual` (10 ms / 1 sample) show up — different functions,
not the same per-parameter null check.

**Implication: the recommended Phase 8 cheap-win (`-Xno-param-assertions`)
would touch the OTel JSON variant (still on the older code path) but not
the proto variants we're trying to make faster.** Lower priority than
2026-05-04 thinking suggested.

### 13.6 Updated Phase 8 recommendation

Based on the new top-N tables:

| Old plan candidate | Old expectation | Post-fix profile says |
|---|---|---|
| `-Xno-param-assertions` (intrinsics) | ~6 % JVM | No longer in top 30 on the variants we care about |
| Direct protobuf encoder (Path B) | ~10–12 % JVM | Plausible but ~10 % is now the *whole* sub-frame budget |
| JS Long → Number | ~7–8 % JS | Now ~19 % of a smaller JS pie — bigger relative win |
| **NEW: skip trace-ID validation for SDK-gen'd IDs** | n/a | **~56 % JVM, ~25 % JS** |

Switch Phase 8's target. New recommended pick:
**patch dcxp's `OtelEncodingUtils.isValidBase16String` to short-circuit
for SDK-generated IDs.** Either via a `@JvmField val skipValidation = true`
constructor option on `SpanContext`, or via a separate
`SdkSpanContext(trustedHexIds: true)` factory path that bypasses the
character-set scan. Lives in the same dcxp fork we'd set up for Phase 5
(now skipped), so the infrastructure cost is similar — but the payoff is
~5× larger. JS gets the same dcxp patch as a side benefit.

Fallback if the dcxp fork is too invasive: JS Long → Number (Phase 8
fallback B) is now the second-best lever — bigger than originally
expected.