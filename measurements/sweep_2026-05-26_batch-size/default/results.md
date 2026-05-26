# Benchmark Results (2026_05_26_16_34_32)

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
| Native | N/A |

## Execution Summary

Total time = wall-clock of the whole process (warmup + StepCount steps + plugin teardown).
Step time = mean across all flat per-step samples (RunCount × StepCount samples).

| Executable | Iterations | Total mean (ms) | Total median (ms) | Step mean (µs) | Step median (µs) | Step stddev (µs) |
|------------|-----------:|----------------:|------------------:|---------------:|-----------------:|-----------------:|
| baseline JVM | 20 | 26.40 | 25.88 | 53.18 | 5.80 | 559.10 |
| k-perf JVM | 20 | 58.79 | 57.79 | 317.13 | 160.55 | 968.21 |
| otel JVM | 20 | 329.87 | 318.24 | 2,076.99 | 1,022.20 | 4,163.09 |
| otel-proto JVM | 20 | 133.80 | 132.32 | 788.35 | 415.75 | 1,346.10 |
| otel-proto-timesource JVM | 20 | 125.21 | 125.70 | 739.55 | 387.35 | 1,167.30 |
| baseline JS (Node) | 20 | 13.59 | 13.41 | 31.39 | 9.20 | 98.90 |
| k-perf JS (Node) | 20 | 90.28 | 89.92 | 545.13 | 435.30 | 506.00 |
| otel JS (Node) | 20 | 531.98 | 521.33 | 3,479.93 | 1,279.35 | 3,834.60 |
| otel-proto JS (Node) | 20 | 178.63 | 173.11 | 1,122.89 | 778.95 | 1,294.39 |
| otel-proto-timesource JS (Node) | 20 | 204.41 | 197.55 | 1,303.57 | 957.80 | 1,286.26 |

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
| k-perf | JVM | 29 | 317.13 | 53.18 | 144.33 | 5.65 | 98.25 | 2.27 | 179 | 1,474.6 | 774.7 | 536.2 |
| otel | JVM | 38 | 2,076.99 | 53.18 | 874.57 | 5.65 | 358.36 | 2.27 | 179 | 11,306.2 | 4,854.3 | 1,989.3 |
| otel-proto | JVM | 22 | 788.35 | 53.18 | 398.91 | 5.65 | 286.80 | 2.27 | 179 | 4,107.1 | 2,197.0 | 1,589.5 |
| otel-proto-timesource | JVM | 26 | 739.55 | 53.18 | 367.60 | 5.65 | 287.35 | 2.27 | 179 | 3,834.5 | 2,022.1 | 1,592.6 |
| k-perf | JS | 8 | 545.13 | 31.39 | 449.55 | 17.05 | 310.75 | 6.00 | 179 | 2,870.0 | 2,416.2 | 1,702.5 |
| otel | JS | 3 | 3,479.93 | 31.39 | 3,123.24 | 17.05 | 822.99 | 6.00 | 179 | 19,265.5 | 17,353.0 | 4,564.2 |
| otel-proto | JS | 11 | 1,122.89 | 31.39 | 814.17 | 17.05 | 672.93 | 6.00 | 179 | 6,097.7 | 4,453.2 | 3,725.9 |
| otel-proto-timesource | JS | 7 | 1,303.57 | 31.39 | 1,016.07 | 17.05 | 813.74 | 6.00 | 179 | 7,107.1 | 5,581.1 | 4,512.5 |

## Per-step median curve (µs)

Per-step median across all 20 runs, sampled at selected step indices.
Shows the JIT warm-up shape: cold steps on the left, steady-state on the
right. Full data (every step) is in per_step_medians.csv and in
esults.json under Results[*].PerStepMedians.

Sawtooth pattern in otel-* rows is BatchSpanProcessor flushes intersecting
the dcxp persistent-list O(n²) bug (GEMINI.md Finding #1, 2026-05-04).

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 6,874.65 | 21.75 | 15.35 | 10.65 | 5.90 | 6.70 | 5.70 | 5.35 | 5.20 | 5.10 | 2.15 |
| k-perf | JVM | 11,482.65 | 1,298.05 | 1,229.80 | 1,175.65 | 543.35 | 306.05 | 168.15 | 132.90 | 185.20 | 89.90 | 99.50 |
| otel | JVM | 11,483.20 | 6,043.95 | 34,852.35 | 15,052.25 | 1,941.05 | 4,874.80 | 607.80 | 400.60 | 1,405.50 | 1,411.00 | 453.80 |
| otel-proto | JVM | 12,040.85 | 6,107.60 | 4,876.90 | 3,032.05 | 5,120.25 | 690.90 | 382.60 | 296.00 | 408.45 | 369.65 | 347.95 |
| otel-proto-timesource | JVM | 9,025.50 | 6,194.50 | 4,870.55 | 3,000.90 | 5,382.15 | 666.60 | 365.45 | 316.35 | 503.65 | 321.15 | 318.85 |
| baseline | JS | 954.95 | 110.95 | 80.55 | 67.85 | 35.75 | 35.05 | 8.35 | 7.20 | 6.30 | 6.30 | 5.40 |
| k-perf | JS | 5,460.75 | 1,882.90 | 1,614.65 | 2,763.95 | 1,079.05 | 573.35 | 428.50 | 430.35 | 389.70 | 337.45 | 442.95 |
| otel | JS | 10,812.50 | 7,160.30 | 30,131.95 | 10,400.85 | 1,089.95 | 7,108.40 | 933.80 | 907.05 | 6,797.05 | 7,331.00 | 815.10 |
| otel-proto | JS | 11,398.15 | 7,819.00 | 7,382.00 | 2,646.50 | 2,365.35 | 771.20 | 711.60 | 1,333.55 | 709.20 | 908.30 | 573.05 |
| otel-proto-timesource | JS | 12,366.40 | 8,114.60 | 6,591.90 | 3,599.85 | 1,367.85 | 1,045.20 | 870.05 | 1,248.20 | 839.20 | 918.60 | 805.10 |

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
