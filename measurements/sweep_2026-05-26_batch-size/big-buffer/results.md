# Benchmark Results (2026_05_26_16_53_09)

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
| baseline JVM | 20 | 28.28 | 27.43 | 57.85 | 6.20 | 609.87 |
| k-perf JVM | 20 | 64.82 | 60.23 | 345.12 | 175.85 | 1,079.27 |
| otel JVM | 20 | 388.53 | 366.09 | 2,436.26 | 1,191.80 | 4,719.89 |
| otel-proto JVM | 20 | 1,057.71 | 1,053.23 | 6,949.99 | 342.30 | 44,421.37 |
| otel-proto-timesource JVM | 20 | 1,030.77 | 1,021.97 | 6,775.22 | 334.45 | 43,590.16 |
| baseline JS (Node) | 20 | 14.64 | 14.56 | 32.90 | 10.90 | 102.63 |
| k-perf JS (Node) | 20 | 114.04 | 105.59 | 689.12 | 521.50 | 679.54 |
| otel JS (Node) | 20 | 562.16 | 553.71 | 3,677.72 | 1,415.35 | 3,926.05 |
| otel-proto JS (Node) | 20 | 2,861.79 | 2,843.64 | 19,011.17 | 1,090.00 | 122,919.22 |
| otel-proto-timesource JS (Node) | 20 | 2,769.88 | 2,748.15 | 18,406.35 | 1,311.60 | 117,197.51 |

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
| k-perf | JVM | 28 | 345.12 | 57.85 | 151.12 | 6.01 | 104.43 | 2.63 | 179 | 1,604.9 | 810.7 | 568.7 |
| otel | JVM | 38 | 2,436.26 | 57.85 | 983.10 | 6.01 | 388.55 | 2.63 | 179 | 13,287.2 | 5,458.6 | 2,156.0 |
| otel-proto | JVM | 92 | 6,949.99 | 57.85 | 4,307.41 | 6.01 | 85.53 | 2.63 | 179 | 38,503.6 | 24,030.1 | 463.1 |
| otel-proto-timesource | JVM | 92 | 6,775.22 | 57.85 | 4,169.94 | 6.01 | 89.35 | 2.63 | 179 | 37,527.2 | 23,262.2 | 484.5 |
| k-perf | JS | 6 | 689.12 | 32.90 | 550.34 | 18.00 | 385.57 | 6.88 | 179 | 3,666.0 | 2,974.0 | 2,115.6 |
| otel | JS | 3 | 3,677.72 | 32.90 | 3,291.09 | 18.00 | 854.87 | 6.88 | 179 | 20,362.1 | 18,285.4 | 4,737.4 |
| otel-proto | JS | 9 | 19,011.17 | 32.90 | 19,709.07 | 18.00 | 849.80 | 6.88 | 179 | 106,023.8 | 110,006.0 | 4,709.1 |
| otel-proto-timesource | JS | 7 | 18,406.35 | 32.90 | 18,729.05 | 18.00 | 1,004.71 | 6.88 | 179 | 102,645.0 | 104,531.0 | 5,574.5 |

## Per-step median curve (µs)

Per-step median across all 20 runs, sampled at selected step indices.
Shows the JIT warm-up shape: cold steps on the left, steady-state on the
right. Full data (every step) is in per_step_medians.csv and in
esults.json under Results[*].PerStepMedians.

Sawtooth pattern in otel-* rows is BatchSpanProcessor flushes intersecting
the dcxp persistent-list O(n²) bug (GEMINI.md Finding #1, 2026-05-04).

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,335.80 | 24.00 | 15.05 | 12.80 | 6.35 | 7.40 | 6.15 | 6.05 | 5.50 | 5.55 | 2.25 |
| k-perf | JVM | 12,399.10 | 1,386.95 | 1,341.40 | 1,205.40 | 597.05 | 293.00 | 207.70 | 113.50 | 159.95 | 141.05 | 121.55 |
| otel | JVM | 12,972.60 | 6,676.75 | 37,802.10 | 14,671.75 | 2,172.75 | 5,944.55 | 740.55 | 433.60 | 1,448.50 | 1,774.75 | 485.55 |
| otel-proto | JVM | 13,342.65 | 6,725.10 | 5,180.85 | 3,143.95 | 5,004.30 | 945.65 | 435.45 | 288.00 | 94.75 | 88.25 | 72.00 |
| otel-proto-timesource | JVM | 10,233.75 | 6,847.90 | 5,251.80 | 3,263.95 | 5,195.05 | 810.90 | 398.40 | 329.15 | 107.60 | 109.65 | 78.75 |
| baseline | JS | 1,010.45 | 130.55 | 74.45 | 60.75 | 47.95 | 45.65 | 9.10 | 8.05 | 6.60 | 7.25 | 6.60 |
| k-perf | JS | 5,807.10 | 1,970.15 | 1,816.75 | 3,258.15 | 1,192.45 | 681.65 | 522.05 | 407.15 | 383.15 | 407.40 | 470.90 |
| otel | JS | 11,491.00 | 7,052.05 | 30,339.10 | 10,847.70 | 1,128.90 | 7,445.90 | 994.90 | 1,017.00 | 7,007.40 | 7,520.80 | 802.85 |
| otel-proto | JS | 11,196.50 | 8,455.00 | 7,062.95 | 2,497.40 | 2,362.75 | 1,153.55 | 999.55 | 1,217.45 | 856.10 | 897.45 | 863.75 |
| otel-proto-timesource | JS | 12,037.25 | 8,067.60 | 6,548.60 | 3,787.70 | 1,464.55 | 2,250.70 | 1,300.75 | 1,098.55 | 1,040.85 | 1,454.10 | 970.90 |

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
