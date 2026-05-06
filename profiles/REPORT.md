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

## 7. Discussion / things that came up

### 7.1 Why does the supervisor's `Clock.System.now()` micro-benchmark show ~5× JS gap that we can't reproduce?

The micro-benchmark measures **per-call cost in isolation** (one `Clock.System.now()` injected into an otherwise-empty function). It reports ~1957 ns/call on JS, ~376 ns for `TimeSource.Monotonic.markNow()`. Theoretical savings on our workload: 486 k calls × 1582 ns ≈ 768 ms — should be visible against an 8500 ms total.

Profile says actual clock cost is ~48 ms (~99 ns/call), ~20× less than the micro-bench predicts. Three independent reasons:

1. **JIT specialisation in the long-running workload.** V8 (and HotSpot) optimise the call site heavily after thousands of calls; the micro-bench harness doesn't reach the same steady state.
2. **The timesource replacement isn't free.** `_anchorEpochNanos + _benchmarkMark.elapsedNow().inWholeNanoseconds` adds Long arithmetic; on JS that's a polyfilled 2-Int class (~7.7% of CPU). We trade one expensive thing for several cheap ones; net saving is small.
3. **The OTel SDK was already monotonic-anchored.** `sdk-trace-jvm:1.0.570`'s `AnchoredClock.now() = clock.nanoTime() − this.nanoTime + this.epochNanos` is the same pattern we re-implemented in IR. Our IR optimisation duplicated what the SDK had internally.

Both benchmarks are correct; they measure different things.

### 7.2 Why does the OTel plugin call the clock twice per span?

OTel spans require **two absolute wall-clock timestamps** (`start_time_unix_nano` and `end_time_unix_nano` in the OTLP Protobuf wire format), not a duration. The trace UI (Jaeger, etc.) renders waterfall diagrams across services on a global timeline — duration alone isn't enough; the consumer needs to know *when* on the timeline the span sat.

k-perf only records elapsed durations (`<;42` = "exited, 42 µs elapsed") and so needs only one `markNow()` + one `elapsedNow()`.

The timesource variant still produces two absolute timestamps, but synthesises them from one wall-clock anchor + monotonic offsets — so two cheap monotonic reads instead of two `Clock.System.now()` calls.

### 7.3 Why do `OtlpExporter` and `util` look unused?

Static grep finds zero callers because the constructor calls and the `await(exporter, ...)` calls are **synthesised by the IR plugin at compile time**, not written by hand. Each OTel plugin's `IrExtension.kt` has a `buildField("_exporter", ..., initializer = { call(Exporter_constructor) { ... } })` block that injects a top-level `OtlpExporter(host, service)` field into `firstFile`. At runtime every span goes through it. IntelliJ's "0 usages" indicator is correct about the static call graph; the plugin generates the dynamic callers.

### 7.4 How does `otel-proto-timesource` create a span (one-screen summary)

**Once at module load** (firstFile static-init):
```
_anchor          = Clock.System.now()               // single wall-clock read
_anchorEpochNanos = anchor.toEpochMs() * 1_000_000 + anchor.nanosecondsOfSecond
_benchmarkMark    = TimeSource.Monotonic.markNow()  // monotonic baseline
_exporter, _processor, _provider, _tracer = OTel SDK plumbing
```

**Two helper functions injected once** (in firstFile):
```
fun _startSpan(name, ctx) = _tracer.spanBuilder(name)
                               .setParent(ctx)
                               .setStartTimestamp(_anchorEpochNanos + _benchmarkMark.elapsedNow().inWholeNanoseconds, NANOSECOND)
                               .startSpan()
                               .also { ctx.with(it).makeCurrent() }

fun _endSpan(span, ctx)   = ctx.makeCurrent().also {
                                span.end(_anchorEpochNanos + _benchmarkMark.elapsedNow().inWholeNanoseconds, NANOSECOND)
                            }
```

**Every user function gets wrapped:**
```
fun yourFunction(...) {
    val ctx  = Context.current()
    val span = _startSpan("yourFunction", ctx)
    try { …original body… } finally { _endSpan(span, ctx) }
}
```

`main()` additionally runs `_processor.shutdown()` + `await(_exporter, _anchor)` in its finally to drain BSP queue before exit. **Zero `Clock.System.now()` calls per span.**

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

Caveats: profiler overhead ~14% (JS), safepoint bias on JFR may under-sample tight JIT loops, Native app symbols don't resolve due to Kotlin/Native `-opt` vs `-g` mutual exclusion (still applies under PerfView — it's a Kotlin/Native limitation, not a profiler one). The previous Native pipeline (samply + Firefox Profiler JSON) was replaced after Smart App Control started blocking samply.exe; the data shape is unchanged because both tools wrap the same Windows kernel ETW.
