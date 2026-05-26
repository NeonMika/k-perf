# Benchmark Results (2026_05_26_16_39_08)

## Parameters
- **Warmup Iterations:** 0
- **Run Iterations:** 20
- **Step Count (workload calls per process):** 150
- **Clean Build:** False
- **Run timeout (s):** 750

## System Information
- **OS:** Microsoft Windows 11 Pro 10.0.26200 64-bit
- **CPU:** Intel(R) Core(TM) i7-9750H CPU @ 2.60GHz (6 Cores / 12 Logical Processors)
- **RAM:** 31.74 GB
- **Java Version:** 21.0.10 ("21.0.10")
- **Node Version:** v24.15.0

## Hardware Overview Details
- **Device:** LENOVO - 20QN000DGE
- **Git Branch:** LB_otel_analysis

## Methods per step (k-perf trace, lines/2/StepCount)

| Platform | methods_per_step |
|---|---:|
| JVM | 179 |
| JS | 179 |
| Native | N/A |

## Execution Summary

Total time = wall-clock of the whole process (warmup + StepCount steps + plugin teardown).
Step time = mean across all flat per-step samples (RunCount × StepCount samples).

| Executable | Iterations | Total mean (ms) | Total median (ms) | Step mean (µs) | Step median (µs) | Step stddev (µs) |
|------------|-----------:|----------------:|------------------:|---------------:|-----------------:|-----------------:|
| baseline JVM | 20 | 28.28 | 27.86 | 57.47 | 6.10 | 612.06 |
| k-perf JVM | 20 | 65.57 | 59.62 | 350.60 | 175.40 | 1,088.46 |
| otel JVM | 20 | 380.01 | 377.93 | 2,388.82 | 1,162.10 | 4,589.33 |
| otel-proto JVM | 20 | 624.84 | 612.57 | 3,968.24 | 2,732.90 | 4,562.22 |
| otel-proto-timesource JVM | 20 | 621.21 | 612.84 | 3,948.01 | 2,641.85 | 4,477.64 |
| baseline JS (Node) | 20 | 15.86 | 14.46 | 35.14 | 11.80 | 118.52 |
| k-perf JS (Node) | 20 | 105.78 | 100.44 | 641.86 | 489.55 | 609.91 |
| otel JS (Node) | 20 | 563.25 | 554.94 | 3,683.22 | 1,390.35 | 3,958.98 |
| otel-proto JS (Node) | 20 | 520.69 | 505.92 | 3,396.78 | 2,801.50 | 2,352.20 |
| otel-proto-timesource JS (Node) | 20 | 556.76 | 550.33 | 3,639.93 | 2,943.55 | 2,452.72 |

## Overhead per instrumented method

Three overhead numbers per (variant, platform):
- **Full-run** uses the arithmetic mean over *all* per-step samples
  (RunCount × StepCount). Heavily biased by the first ~5–10 cold steps on
  JVM where step 0 alone can be 1000× the steady-state cost — so this
  overestimates the warm-state overhead.
- **Steady-state** uses the auto-detected stable region (SS-start): the
  first step index whose per-step median (across runs) is within 2× the
  median of the latter-50% tail. Recommended quotation when reporting
  "cost of one instrumented method call at warm steady state".
- **Envelope (P10)** is the 10th-percentile per-step median over the
  steady region. For otel-* rows the steady region contains a sawtooth
  of quiet steps interleaved with batch-flush spikes; the arithmetic
  mean blends both into a single inflated number that hides the
  per-method instrumentation cost behind amortized flush work. The P10
  envelope picks the quietest samples and approximates "what one
  instrumented method costs when no flush is occurring". Useful as a
  lower bound; compare against Overhead steady (ns) to read off the
  flush-amortization share.

overhead_ns_per_method = (step_ns_instrumented − step_ns_baseline) / methods_per_step
(per-step timings are collected at nanosecond resolution via Duration.inWholeNanoseconds
and the QPC/hrtime backing clocks; display columns below show µs/ms for readability.)

methods_per_step is derived from the preserved k-perf trace under 	races/
(trace lines / 2 / StepCount). For the otel-* variants this is a lower bound
(those plugins also instrument the epeat { } lambda body, ~+1 method per
step, ~0.5% underestimate).

| Variant | Platform | SS-start | Step mean (µs) | Baseline mean (µs) | Steady (µs) | Baseline steady (µs) | Envelope P10 (µs) | Baseline envelope (µs) | Methods/step | Overhead full (ns) | Overhead steady (ns) | Overhead envelope (ns) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| k-perf | JVM | 23 | 350.60 | 57.47 | 158.15 | 5.93 | 97.64 | 2.57 | 179 | 1,637.6 | 850.3 | 531.1 |
| otel | JVM | 35 | 2,388.82 | 57.47 | 1,071.08 | 5.93 | 435.20 | 2.57 | 179 | 13,024.3 | 5,950.5 | 2,417.0 |
| otel-proto | JVM | 14 | 3,968.24 | 57.47 | 2,743.09 | 5.93 | 1,820.20 | 2.57 | 179 | 21,847.9 | 15,291.4 | 10,154.4 |
| otel-proto-timesource | JVM | 23 | 3,948.01 | 57.47 | 2,465.73 | 5.93 | 1,780.18 | 2.57 | 179 | 21,734.8 | 13,741.9 | 9,930.8 |
| k-perf | JS | 6 | 641.86 | 35.14 | 520.18 | 18.53 | 375.22 | 7.22 | 179 | 3,389.5 | 2,802.5 | 2,055.9 |
| otel | JS | 3 | 3,683.22 | 35.14 | 3,345.18 | 18.53 | 895.52 | 7.22 | 179 | 20,380.3 | 18,584.6 | 4,962.6 |
| otel-proto | JS | 4 | 3,396.78 | 35.14 | 2,950.69 | 18.53 | 2,245.68 | 7.22 | 179 | 18,780.1 | 16,380.8 | 12,505.4 |
| otel-proto-timesource | JS | 4 | 3,639.93 | 35.14 | 3,167.77 | 18.53 | 2,366.03 | 7.22 | 179 | 20,138.5 | 17,593.5 | 13,177.7 |

## Per-step median curve (µs)

Per-step median across all 20 runs, sampled at selected step indices.
Shows the JIT warm-up shape: cold steps on the left, steady-state on the
right. Full data (every step) is in per_step_medians.csv and in
esults.json under Results[*].PerStepMedians.

Sawtooth pattern in otel-* rows is BatchSpanProcessor flushes intersecting
the dcxp persistent-list O(n²) bug (GEMINI.md Finding #1, 2026-05-04).

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,365.00 | 24.45 | 15.90 | 19.45 | 6.50 | 7.35 | 6.25 | 5.65 | 5.75 | 5.45 | 2.45 |
| k-perf | JVM | 12,349.10 | 1,402.00 | 1,384.40 | 1,300.00 | 670.45 | 258.35 | 218.15 | 160.85 | 165.60 | 113.75 | 92.45 |
| otel | JVM | 12,581.80 | 6,578.70 | 36,199.10 | 14,448.45 | 2,176.95 | 5,974.50 | 734.80 | 460.05 | 1,670.45 | 1,870.45 | 437.95 |
| otel-proto | JVM | 49,074.60 | 14,266.00 | 11,000.35 | 8,849.45 | 10,116.80 | 4,864.20 | 2,091.10 | 2,045.80 | 2,679.00 | 3,427.55 | 2,668.35 |
| otel-proto-timesource | JVM | 46,286.35 | 15,135.10 | 11,587.90 | 8,860.70 | 9,984.75 | 5,280.95 | 2,031.10 | 2,275.95 | 2,228.15 | 2,618.80 | 2,356.70 |
| baseline | JS | 1,064.30 | 136.00 | 74.35 | 72.05 | 43.90 | 36.50 | 8.80 | 8.05 | 7.10 | 10.05 | 6.45 |
| k-perf | JS | 5,794.65 | 2,003.55 | 1,794.80 | 3,242.25 | 1,147.35 | 652.25 | 486.40 | 362.20 | 392.30 | 378.50 | 460.70 |
| otel | JS | 11,276.25 | 7,494.50 | 31,357.05 | 10,989.60 | 1,181.95 | 7,422.45 | 950.80 | 1,007.65 | 7,354.75 | 7,706.85 | 808.75 |
| otel-proto | JS | 23,525.45 | 14,283.40 | 12,398.00 | 7,064.65 | 2,930.75 | 3,252.15 | 3,609.45 | 2,444.55 | 2,282.20 | 2,264.10 | 2,925.50 |
| otel-proto-timesource | JS | 24,920.25 | 14,697.90 | 12,135.35 | 6,428.10 | 4,481.55 | 3,677.50 | 2,698.00 | 2,616.85 | 2,519.05 | 2,474.40 | 3,122.95 |

## Per-step times (JIT warmup curves)

Full per-step medians (one value per step index, across all runs) are in
per_step_medians.csv for plotting (units: nanoseconds). The raw
per-(run × step) ns samples are in esults.json under
Results[*].PerRunStepNanos.

Notes on interpretation:
- **JVM** HotSpot tiered compilation has two thresholds (~200 calls for C1,
  ~10k for C2). With ~180 user methods per step, expect two inflection
  points: one near step 1–2 and another near step 55. In practice the
  observed curve often shows a single steep drop in the first 5–10 steps
  followed by a slow tail — the second knee can be invisible when the hot
  path is trivial.
- **JS** V8 uses Ignition → Sparkplug → Maglev → Turbofan tiers; different
  curve shape than JVM.
- **Kotlin/Native** is AOT — expect flat from step 1.
- **otel-* variants** have monotonic upward drift + periodic spikes
  superimposed on the JIT curve from the dcxp
  BatchSpanProcessor.removeSpanDataFromBatch O(n²) bug. Steady-state
  overhead averages over both quiet and spike samples.
