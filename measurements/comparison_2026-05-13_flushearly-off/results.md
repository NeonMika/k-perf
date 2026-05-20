# Benchmark Results (2026_05_13_15_57_23)

## Parameters
- **Warmup Iterations:** 0
- **Run Iterations:** 20
- **Step Count (workload calls per process):** 150
- **Clean Build:** True
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
| Native | 179 |

## Execution Summary

Total time = wall-clock of the whole process (warmup + StepCount steps + plugin teardown).
Step time = mean across all flat per-step samples (RunCount × StepCount samples).

| Executable | Iterations | Total mean (ms) | Total median (ms) | Step mean (µs) | Step median (µs) | Step stddev (µs) |
|------------|-----------:|----------------:|------------------:|---------------:|-----------------:|-----------------:|
| baseline JVM | 20 | 27.59 | 27.51 | 57.09 | 5.90 | 593.83 |
| k-perf JVM | 20 | 63.06 | 62.62 | 348.90 | 187.00 | 1,080.61 |
| otel JVM | 20 | 360.81 | 356.00 | 2,289.46 | 1,247.55 | 4,591.37 |
| otel-proto JVM | 20 | 455.94 | 443.70 | 2,908.95 | 1,000.00 | 5,791.39 |
| otel-proto-timesource JVM | 20 | 448.30 | 445.89 | 2,868.35 | 1,039.80 | 5,787.78 |
| baseline JS (Node) | 20 | 14.69 | 14.27 | 31.46 | 11.20 | 95.80 |
| k-perf JS (Node) | 20 | 107.10 | 102.08 | 652.83 | 516.15 | 540.77 |
| otel JS (Node) | 20 | 547.62 | 540.14 | 3,583.88 | 1,384.75 | 3,715.42 |
| otel-proto JS (Node) | 20 | 622.50 | 612.78 | 4,083.66 | 1,282.05 | 4,715.77 |
| otel-proto-timesource JS (Node) | 20 | 661.16 | 656.37 | 4,348.31 | 1,602.40 | 4,610.47 |
| baseline Native (Win) | 20 | 1.22 | 1.11 | 2.68 | 0.80 | 18.84 |
| k-perf Native (Win) | 20 | 29.13 | 28.03 | 188.29 | 163.90 | 68.42 |
| otel Native (Win) | 20 | 767.54 | 738.13 | 5,102.94 | 865.80 | 6,071.85 |
| otel-proto Native (Win) | 20 | 629.46 | 620.29 | 4,186.20 | 627.75 | 4,964.20 |
| otel-proto-timesource Native (Win) | 20 | 625.55 | 613.40 | 4,159.93 | 649.95 | 4,986.00 |

## Overhead per instrumented method

Two overhead numbers per (variant, platform):
- **Full-run** uses the arithmetic mean over *all* per-step samples
  (RunCount × StepCount). Heavily biased by the first ~5–10 cold steps on
  JVM where step 0 alone can be 1000× the steady-state cost — so this
  overestimates the warm-state overhead.
- **Steady-state** uses the auto-detected stable region (SS-start): the
  first step index whose per-step median (across runs) is within 2× the
  median of the latter-50% tail. Recommended quotation when reporting
  "cost of one instrumented method call at warm steady state".

overhead_ns_per_method = (step_ns_instrumented − step_ns_baseline) / methods_per_step
(per-step timings are collected at nanosecond resolution via Duration.inWholeNanoseconds
and the QPC/hrtime backing clocks; display columns below show µs/ms for readability.)

methods_per_step is derived from the preserved k-perf trace under 	races/
(trace lines / 2 / StepCount). For the otel-* variants this is a lower bound
(those plugins also instrument the epeat { } lambda body, ~+1 method per
step, ~0.5% underestimate).

| Variant | Platform | SS-start | Step mean (µs) | Baseline mean (µs) | Steady (µs) | Baseline steady (µs) | Methods/step | Overhead full (ns) | Overhead steady (ns) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|
| k-perf | JVM | 40 | 348.90 | 57.09 | 141.71 | 5.68 | 179 | 1,630.2 | 760.0 |
| otel | JVM | 52 | 2,289.46 | 57.09 | 850.02 | 5.68 | 179 | 12,471.3 | 4,717.0 |
| otel-proto | JVM | 41 | 2,908.95 | 57.09 | 1,585.37 | 5.68 | 179 | 15,932.1 | 8,825.1 |
| otel-proto-timesource | JVM | 41 | 2,868.35 | 57.09 | 1,546.43 | 5.68 | 179 | 15,705.3 | 8,607.5 |
| k-perf | JS | 6 | 652.83 | 31.46 | 536.04 | 17.20 | 179 | 3,471.4 | 2,898.5 |
| otel | JS | 3 | 3,583.88 | 31.46 | 3,270.25 | 17.20 | 179 | 19,845.9 | 18,173.5 |
| otel-proto | JS | 4 | 4,083.66 | 31.46 | 3,758.01 | 17.20 | 179 | 22,638.0 | 20,898.4 |
| otel-proto-timesource | JS | 6 | 4,348.31 | 31.46 | 3,949.67 | 17.20 | 179 | 24,116.5 | 21,969.1 |
| k-perf | Native | 0 | 188.29 | 2.68 | 177.66 | 0.84 | 179 | 1,037.0 | 987.8 |
| otel | Native | 0 | 5,102.94 | 2.68 | 4,786.96 | 0.84 | 179 | 28,493.1 | 26,738.1 |
| otel-proto | Native | 0 | 4,186.20 | 2.68 | 4,056.03 | 0.84 | 179 | 23,371.6 | 22,654.7 |
| otel-proto-timesource | Native | 0 | 4,159.93 | 2.68 | 3,998.93 | 0.84 | 179 | 23,224.9 | 22,335.7 |

## Per-step median curve (µs)

Per-step median across all 20 runs, sampled at selected step indices.
Shows the JIT warm-up shape: cold steps on the left, steady-state on the
right. Full data (every step) is in per_step_medians.csv and in
esults.json under Results[*].PerStepMedians.

Sawtooth pattern in otel-* rows is BatchSpanProcessor flushes intersecting
the dcxp persistent-list O(n²) bug (GEMINI.md Finding #1, 2026-05-04).

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,169.75 | 21.00 | 15.10 | 10.90 | 6.00 | 7.85 | 5.90 | 5.50 | 5.40 | 5.00 | 2.20 |
| k-perf | JVM | 12,954.30 | 1,313.20 | 1,236.40 | 1,111.55 | 648.50 | 354.25 | 183.75 | 121.00 | 185.05 | 91.80 | 80.25 |
| otel | JVM | 11,747.00 | 6,359.00 | 39,703.35 | 15,344.10 | 2,208.35 | 4,370.75 | 1,209.45 | 338.20 | 1,435.40 | 1,543.40 | 488.85 |
| otel-proto | JVM | 13,440.35 | 6,135.35 | 56,295.15 | 20,081.60 | 2,189.30 | 6,342.20 | 684.90 | 380.45 | 2,534.70 | 2,394.20 | 339.90 |
| otel-proto-timesource | JVM | 10,500.20 | 6,234.75 | 56,537.10 | 20,552.10 | 2,079.05 | 6,222.05 | 477.75 | 437.60 | 2,691.55 | 2,332.35 | 307.70 |
| baseline | JS | 981.65 | 117.90 | 72.10 | 58.80 | 37.80 | 44.80 | 8.75 | 8.25 | 6.40 | 6.25 | 6.10 |
| k-perf | JS | 5,663.85 | 1,823.95 | 1,766.60 | 2,809.55 | 1,137.75 | 712.65 | 487.85 | 358.90 | 449.50 | 431.90 | 518.05 |
| otel | JS | 10,802.25 | 7,181.00 | 28,795.90 | 10,733.10 | 1,146.45 | 7,413.15 | 952.90 | 968.90 | 7,341.35 | 7,730.50 | 851.65 |
| otel-proto | JS | 10,607.05 | 7,364.40 | 39,160.40 | 12,732.65 | 2,033.50 | 9,593.30 | 907.35 | 1,070.75 | 7,485.15 | 9,367.25 | 994.75 |
| otel-proto-timesource | JS | 11,391.50 | 7,905.35 | 37,155.60 | 13,140.65 | 1,520.20 | 8,389.90 | 3,083.05 | 1,152.20 | 7,833.95 | 6,907.85 | 1,508.45 |
| baseline | Native | 226.60 | 1.30 | 0.80 | 0.70 | 0.70 | 1.30 | 0.85 | 0.80 | 0.85 | 0.80 | 0.80 |
| k-perf | Native | 196.90 | 163.95 | 161.55 | 245.85 | 154.15 | 153.10 | 183.60 | 214.95 | 192.90 | 154.40 | 157.10 |
| otel | Native | 602.15 | 527.75 | 10,181.30 | 9,928.55 | 561.30 | 12,637.90 | 892.10 | 601.80 | 12,667.35 | 12,682.55 | 600.95 |
| otel-proto | Native | 599.20 | 516.70 | 10,874.25 | 11,374.25 | 718.35 | 10,739.75 | 509.45 | 511.05 | 11,056.65 | 10,561.05 | 514.10 |
| otel-proto-timesource | Native | 546.90 | 523.10 | 10,893.65 | 11,746.25 | 730.00 | 10,808.85 | 441.80 | 455.90 | 11,002.50 | 9,983.60 | 449.80 |

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
