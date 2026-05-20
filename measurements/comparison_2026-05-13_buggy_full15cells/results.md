# Benchmark Results (2026_05_13_15_17_23)

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
| baseline JVM | 20 | 27.69 | 27.45 | 58.12 | 5.85 | 610.35 |
| k-perf JVM | 20 | 179.34 | 171.99 | 1,122.16 | 863.35 | 1,473.04 |
| otel JVM | 20 | 390.84 | 380.94 | 2,482.46 | 1,301.10 | 4,897.52 |
| otel-proto JVM | 20 | 461.18 | 458.30 | 2,950.94 | 1,008.20 | 5,856.44 |
| otel-proto-timesource JVM | 20 | 462.18 | 450.99 | 2,958.76 | 1,008.20 | 5,935.00 |
| baseline JS (Node) | 20 | 14.39 | 14.22 | 31.51 | 10.30 | 97.25 |
| k-perf JS (Node) | 20 | 347.67 | 345.68 | 2,263.67 | 1,989.60 | 989.72 |
| otel JS (Node) | 20 | 563.63 | 552.51 | 3,689.37 | 1,423.30 | 3,820.78 |
| otel-proto JS (Node) | 20 | 633.53 | 625.76 | 4,157.39 | 1,314.20 | 4,766.61 |
| otel-proto-timesource JS (Node) | 20 | 683.19 | 674.04 | 4,493.22 | 1,643.10 | 4,824.02 |
| baseline Native (Win) | 20 | 1.29 | 1.32 | 2.88 | 0.90 | 20.50 |
| k-perf Native (Win) | 20 | 162.12 | 164.12 | 1,072.44 | 1,036.20 | 177.56 |
| otel Native (Win) | 20 | 771.07 | 752.15 | 5,126.97 | 863.35 | 6,073.89 |
| otel-proto Native (Win) | 20 | 668.51 | 650.94 | 4,445.38 | 702.25 | 5,341.42 |
| otel-proto-timesource Native (Win) | 20 | 623.21 | 620.54 | 4,144.68 | 601.50 | 4,944.85 |

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
| k-perf | JVM | 9 | 1,122.16 | 58.12 | 862.99 | 5.68 | 179 | 5,944.4 | 4,789.4 |
| otel | JVM | 52 | 2,482.46 | 58.12 | 922.58 | 5.68 | 179 | 13,543.8 | 5,122.3 |
| otel-proto | JVM | 36 | 2,950.94 | 58.12 | 1,680.82 | 5.68 | 179 | 16,161.0 | 9,358.3 |
| otel-proto-timesource | JVM | 38 | 2,958.76 | 58.12 | 1,610.16 | 5.68 | 179 | 16,204.7 | 8,963.5 |
| k-perf | JS | 3 | 2,263.67 | 31.51 | 2,133.15 | 17.15 | 179 | 12,470.2 | 11,821.2 |
| otel | JS | 3 | 3,689.37 | 31.51 | 3,363.47 | 17.15 | 179 | 20,435.0 | 18,694.5 |
| otel-proto | JS | 4 | 4,157.39 | 31.51 | 3,801.09 | 17.15 | 179 | 23,049.6 | 21,139.3 |
| otel-proto-timesource | JS | 3 | 4,493.22 | 31.51 | 4,086.25 | 17.15 | 179 | 24,925.8 | 22,732.4 |
| k-perf | Native | 0 | 1,072.44 | 2.88 | 1,037.46 | 0.97 | 179 | 5,975.2 | 5,790.5 |
| otel | Native | 0 | 5,126.97 | 2.88 | 4,892.05 | 0.97 | 179 | 28,626.2 | 27,324.5 |
| otel-proto | Native | 0 | 4,445.38 | 2.88 | 4,246.85 | 0.97 | 179 | 24,818.5 | 23,720.0 |
| otel-proto-timesource | Native | 0 | 4,144.68 | 2.88 | 4,015.29 | 0.97 | 179 | 23,138.5 | 22,426.4 |

## Per-step median curve (µs)

Per-step median across all 20 runs, sampled at selected step indices.
Shows the JIT warm-up shape: cold steps on the left, steady-state on the
right. Full data (every step) is in per_step_medians.csv and in
esults.json under Results[*].PerStepMedians.

Sawtooth pattern in otel-* rows is BatchSpanProcessor flushes intersecting
the dcxp persistent-list O(n²) bug (GEMINI.md Finding #1, 2026-05-04).

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,286.25 | 21.75 | 15.35 | 10.75 | 5.90 | 6.75 | 5.85 | 5.50 | 5.35 | 5.00 | 2.20 |
| k-perf | JVM | 17,791.30 | 3,982.60 | 3,401.95 | 2,397.25 | 1,450.60 | 1,009.55 | 895.05 | 811.45 | 769.05 | 774.35 | 735.45 |
| otel | JVM | 12,739.75 | 6,300.45 | 40,164.05 | 15,624.20 | 2,307.80 | 4,683.25 | 1,075.25 | 442.20 | 1,588.80 | 1,624.05 | 493.75 |
| otel-proto | JVM | 13,545.95 | 6,113.95 | 57,391.55 | 21,401.05 | 2,164.45 | 6,570.25 | 671.40 | 413.70 | 2,776.45 | 2,437.50 | 314.70 |
| otel-proto-timesource | JVM | 10,283.55 | 6,156.10 | 57,393.40 | 19,825.50 | 2,224.90 | 6,677.85 | 624.40 | 408.50 | 2,657.70 | 2,595.55 | 322.25 |
| baseline | JS | 976.60 | 126.65 | 72.30 | 63.70 | 52.25 | 56.65 | 8.60 | 7.40 | 6.45 | 6.85 | 5.30 |
| k-perf | JS | 12,350.40 | 5,323.75 | 4,789.55 | 3,024.65 | 3,049.25 | 2,405.85 | 2,252.80 | 1,883.60 | 1,845.75 | 1,918.15 | 1,837.25 |
| otel | JS | 10,873.05 | 7,341.55 | 29,098.15 | 10,726.50 | 1,204.15 | 7,695.00 | 966.70 | 994.55 | 7,446.80 | 7,897.10 | 874.60 |
| otel-proto | JS | 10,743.25 | 7,251.35 | 39,368.85 | 13,214.65 | 2,080.80 | 9,817.20 | 919.95 | 1,009.80 | 7,671.90 | 9,662.50 | 871.90 |
| otel-proto-timesource | JS | 11,643.55 | 8,171.95 | 37,621.40 | 13,521.80 | 1,547.90 | 8,672.60 | 3,334.50 | 1,195.05 | 8,085.20 | 7,263.70 | 1,534.40 |
| baseline | Native | 240.80 | 1.40 | 0.90 | 0.70 | 0.70 | 1.45 | 0.90 | 1.10 | 0.85 | 0.90 | 1.40 |
| k-perf | Native | 2,002.20 | 1,265.20 | 1,298.75 | 1,425.75 | 1,013.20 | 934.95 | 1,103.55 | 937.05 | 1,091.00 | 1,010.85 | 1,087.20 |
| otel | Native | 597.30 | 520.40 | 10,272.90 | 10,392.30 | 570.00 | 12,381.25 | 605.30 | 671.65 | 13,336.55 | 12,766.75 | 635.55 |
| otel-proto | Native | 595.90 | 525.10 | 10,857.35 | 11,700.60 | 741.60 | 11,232.95 | 571.90 | 549.60 | 11,181.80 | 10,920.20 | 542.15 |
| otel-proto-timesource | Native | 532.50 | 542.10 | 10,678.90 | 12,215.70 | 698.90 | 10,636.00 | 467.60 | 463.65 | 10,887.60 | 10,058.20 | 456.65 |

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
