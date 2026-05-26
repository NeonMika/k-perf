# Benchmark Results (2026_05_23_22_27_29)

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
| baseline JVM | 20 | 30.12 | 29.20 | 59.96 | 6.10 | 626.05 |
| k-perf JVM | 20 | 58.11 | 57.88 | 310.24 | 148.50 | 999.41 |
| otel JVM | 20 | 354.25 | 345.34 | 2,215.11 | 1,126.70 | 4,199.10 |
| otel-proto JVM | 20 | 133.24 | 131.83 | 785.20 | 410.30 | 1,313.53 |
| otel-proto-timesource JVM | 20 | 133.55 | 127.32 | 789.70 | 398.25 | 1,300.34 |
| baseline JS (Node) | 20 | 14.18 | 13.59 | 32.31 | 9.70 | 111.04 |
| k-perf JS (Node) | 20 | 90.04 | 89.61 | 544.53 | 430.85 | 500.62 |
| otel JS (Node) | 20 | 527.54 | 523.87 | 3,451.22 | 1,340.75 | 3,623.99 |
| otel-proto JS (Node) | 20 | 168.06 | 166.74 | 1,058.34 | 761.05 | 1,235.02 |
| otel-proto-timesource JS (Node) | 20 | 199.81 | 194.64 | 1,274.29 | 956.95 | 1,338.98 |
| baseline Native (Win) | 20 | 1.16 | 1.01 | 2.60 | 0.80 | 19.49 |
| k-perf Native (Win) | 20 | 28.59 | 27.97 | 184.58 | 163.20 | 45.70 |
| otel Native (Win) | 20 | 820.96 | 799.28 | 5,459.43 | 1,007.70 | 6,670.12 |
| otel-proto Native (Win) | 20 | 93.86 | 93.34 | 616.17 | 488.70 | 280.66 |
| otel-proto-timesource Native (Win) | 20 | 84.94 | 84.87 | 556.49 | 434.55 | 272.37 |

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
| k-perf | JVM | 26 | 310.24 | 59.96 | 132.24 | 6.00 | 87.40 | 3.12 | 179 | 1,398.2 | 705.2 | 470.9 |
| otel | JVM | 36 | 2,215.11 | 59.96 | 998.15 | 6.00 | 390.23 | 3.12 | 179 | 12,040.0 | 5,542.8 | 2,162.7 |
| otel-proto | JVM | 22 | 785.20 | 59.96 | 401.34 | 6.00 | 296.57 | 3.12 | 179 | 4,051.6 | 2,208.6 | 1,639.4 |
| otel-proto-timesource | JVM | 26 | 789.70 | 59.96 | 377.37 | 6.00 | 310.55 | 3.12 | 179 | 4,076.8 | 2,074.7 | 1,717.5 |
| k-perf | JS | 6 | 544.53 | 32.31 | 454.07 | 17.24 | 313.39 | 5.90 | 179 | 2,861.5 | 2,440.4 | 1,717.8 |
| otel | JS | 3 | 3,451.22 | 32.31 | 3,126.32 | 17.24 | 872.09 | 5.90 | 179 | 19,100.0 | 17,369.2 | 4,839.1 |
| otel-proto | JS | 11 | 1,058.34 | 32.31 | 772.26 | 17.24 | 642.25 | 5.90 | 179 | 5,732.0 | 4,218.0 | 3,555.0 |
| otel-proto-timesource | JS | 10 | 1,274.29 | 32.31 | 984.89 | 17.24 | 785.20 | 5.90 | 179 | 6,938.4 | 5,405.9 | 4,353.6 |
| k-perf | Native | 0 | 184.58 | 2.60 | 176.80 | 0.80 | 153.70 | 0.70 | 179 | 1,016.6 | 983.2 | 854.7 |
| otel | Native | 0 | 5,459.43 | 2.60 | 5,098.32 | 0.80 | 579.08 | 0.70 | 179 | 30,485.1 | 28,477.8 | 3,231.2 |
| otel-proto | Native | 0 | 616.17 | 2.60 | 599.60 | 0.80 | 471.13 | 0.70 | 179 | 3,427.8 | 3,345.2 | 2,628.1 |
| otel-proto-timesource | Native | 0 | 556.49 | 2.60 | 544.89 | 0.80 | 412.37 | 0.70 | 179 | 3,094.3 | 3,039.6 | 2,299.8 |

## Per-step median curve (µs)

Per-step median across all 20 runs, sampled at selected step indices.
Shows the JIT warm-up shape: cold steps on the left, steady-state on the
right. Full data (every step) is in per_step_medians.csv and in
esults.json under Results[*].PerStepMedians.

Sawtooth pattern in otel-* rows is BatchSpanProcessor flushes intersecting
the dcxp persistent-list O(n²) bug (GEMINI.md Finding #1, 2026-05-04).

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,642.10 | 24.70 | 16.45 | 19.25 | 6.40 | 7.55 | 6.05 | 5.65 | 6.10 | 5.40 | 2.30 |
| k-perf | JVM | 11,926.70 | 1,338.65 | 1,331.15 | 1,162.75 | 546.85 | 252.15 | 213.15 | 96.90 | 191.45 | 111.75 | 87.25 |
| otel | JVM | 11,702.35 | 6,175.55 | 35,646.50 | 14,178.60 | 1,917.60 | 5,943.95 | 542.60 | 408.45 | 1,679.15 | 1,669.50 | 501.25 |
| otel-proto | JVM | 11,858.85 | 6,088.55 | 4,807.10 | 3,067.80 | 5,260.95 | 670.00 | 396.55 | 292.85 | 412.00 | 413.25 | 344.35 |
| otel-proto-timesource | JVM | 9,258.65 | 6,297.95 | 4,970.00 | 3,014.45 | 5,137.90 | 703.65 | 380.00 | 338.40 | 427.90 | 339.15 | 329.20 |
| baseline | JS | 979.85 | 116.10 | 81.65 | 69.00 | 48.50 | 53.40 | 8.40 | 7.20 | 6.30 | 6.35 | 5.30 |
| k-perf | JS | 5,456.75 | 1,827.25 | 1,576.95 | 2,653.30 | 1,058.00 | 562.70 | 417.75 | 325.95 | 392.25 | 364.65 | 405.80 |
| otel | JS | 10,665.10 | 7,010.90 | 28,971.15 | 10,411.45 | 1,153.35 | 7,146.05 | 904.85 | 916.30 | 6,917.25 | 7,463.20 | 829.50 |
| otel-proto | JS | 10,862.10 | 7,568.10 | 7,532.90 | 2,591.35 | 2,460.95 | 817.85 | 707.10 | 1,122.55 | 651.20 | 814.90 | 552.20 |
| otel-proto-timesource | JS | 11,609.00 | 7,897.00 | 6,337.90 | 3,372.40 | 1,362.30 | 1,103.10 | 983.10 | 1,191.75 | 845.15 | 887.00 | 747.05 |
| baseline | Native | 224.45 | 1.30 | 0.90 | 0.70 | 0.70 | 0.90 | 0.80 | 0.80 | 0.80 | 0.80 | 0.80 |
| k-perf | Native | 197.15 | 162.95 | 155.80 | 243.60 | 155.25 | 154.00 | 183.80 | 209.50 | 193.35 | 154.30 | 154.20 |
| otel | Native | 624.80 | 529.15 | 10,350.85 | 9,944.85 | 559.95 | 14,080.40 | 667.55 | 601.65 | 12,735.20 | 13,634.70 | 780.20 |
| otel-proto | Native | 595.85 | 517.85 | 669.70 | 625.25 | 759.15 | 483.65 | 1,275.40 | 486.60 | 485.25 | 469.10 | 468.10 |
| otel-proto-timesource | Native | 545.65 | 501.20 | 610.65 | 571.70 | 656.25 | 433.20 | 432.00 | 434.95 | 414.85 | 422.45 | 415.90 |

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
