# Benchmark Results (2026_05_26_18_58_37)

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
| baseline JVM | 20 | 29.29 | 28.92 | 59.18 | 5.90 | 628.17 |
| k-perf JVM | 20 | 67.38 | 61.87 | 356.36 | 164.55 | 1,152.62 |
| otel JVM | 20 | 425.38 | 410.62 | 2,673.81 | 1,309.45 | 4,927.46 |
| otel-proto JVM | 20 | 154.22 | 146.97 | 901.25 | 427.60 | 1,545.32 |
| otel-proto-timesource JVM | 20 | 150.21 | 141.21 | 865.16 | 405.05 | 1,632.93 |
| baseline JS (Node) | 20 | 16.26 | 15.70 | 36.42 | 10.90 | 127.17 |
| k-perf JS (Node) | 20 | 106.96 | 106.25 | 643.59 | 489.25 | 655.66 |
| otel JS (Node) | 20 | 627.48 | 608.84 | 4,095.70 | 1,678.85 | 4,386.92 |
| otel-proto JS (Node) | 20 | 198.39 | 189.36 | 1,242.46 | 804.20 | 1,502.09 |
| otel-proto-timesource JS (Node) | 20 | 187.18 | 177.40 | 1,162.12 | 742.60 | 1,879.10 |
| baseline Native (Win) | 20 | 1.49 | 1.46 | 3.16 | 1.20 | 21.72 |
| k-perf Native (Win) | 20 | 32.93 | 32.00 | 212.21 | 183.30 | 106.44 |
| otel Native (Win) | 20 | 1,071.24 | 1,060.98 | 7,121.37 | 1,291.20 | 8,821.95 |
| otel-proto Native (Win) | 20 | 106.62 | 106.51 | 699.38 | 484.60 | 422.07 |
| otel-proto-timesource Native (Win) | 20 | 107.29 | 105.28 | 704.17 | 480.25 | 455.29 |

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
| k-perf | JVM | 26 | 356.36 | 59.18 | 142.00 | 5.90 | 93.22 | 2.78 | 179 | 1,660.2 | 760.3 | 505.3 |
| otel | JVM | 38 | 2,673.81 | 59.18 | 1,098.82 | 5.90 | 422.66 | 2.78 | 179 | 14,606.9 | 6,105.7 | 2,345.7 |
| otel-proto | JVM | 19 | 901.25 | 59.18 | 406.72 | 5.90 | 321.50 | 2.78 | 179 | 4,704.3 | 2,239.2 | 1,780.6 |
| otel-proto-timesource | JVM | 20 | 865.16 | 59.18 | 385.24 | 5.90 | 312.70 | 2.78 | 179 | 4,502.7 | 2,119.2 | 1,731.4 |
| k-perf | JS | 3 | 643.59 | 36.42 | 521.94 | 17.82 | 368.90 | 6.30 | 179 | 3,392.0 | 2,816.3 | 2,025.7 |
| otel | JS | 3 | 4,095.70 | 36.42 | 3,545.92 | 17.82 | 908.17 | 6.30 | 179 | 22,677.5 | 19,710.0 | 5,038.4 |
| otel-proto | JS | 11 | 1,242.46 | 36.42 | 821.95 | 17.82 | 663.92 | 6.30 | 179 | 6,737.6 | 4,492.3 | 3,673.9 |
| otel-proto-timesource | JS | 12 | 1,162.12 | 36.42 | 758.57 | 17.82 | 583.86 | 6.30 | 179 | 6,288.8 | 4,138.3 | 3,226.6 |
| k-perf | Native | 0 | 212.21 | 3.16 | 187.36 | 1.22 | 156.05 | 0.84 | 179 | 1,167.9 | 1,039.9 | 867.1 |
| otel | Native | 0 | 7,121.37 | 3.16 | 6,636.51 | 1.22 | 618.25 | 0.84 | 179 | 39,766.5 | 37,068.6 | 3,449.2 |
| otel-proto | Native | 0 | 699.38 | 3.16 | 621.43 | 1.22 | 475.94 | 0.84 | 179 | 3,889.5 | 3,464.8 | 2,654.2 |
| otel-proto-timesource | Native | 0 | 704.17 | 3.16 | 610.24 | 1.22 | 470.82 | 0.84 | 179 | 3,916.3 | 3,402.3 | 2,625.6 |

## Per-step median curve (µs)

Per-step median across all 20 runs, sampled at selected step indices.
Shows the JIT warm-up shape: cold steps on the left, steady-state on the
right. Full data (every step) is in per_step_medians.csv and in
esults.json under Results[*].PerStepMedians.

Sawtooth pattern in otel-* rows is BatchSpanProcessor flushes intersecting
the dcxp persistent-list O(n²) bug (GEMINI.md Finding #1, 2026-05-04).

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,456.40 | 23.90 | 15.65 | 11.00 | 6.05 | 6.90 | 5.85 | 5.55 | 5.45 | 5.30 | 2.20 |
| k-perf | JVM | 13,548.00 | 1,376.15 | 1,320.40 | 1,226.50 | 545.85 | 253.70 | 192.70 | 135.65 | 187.70 | 93.65 | 94.55 |
| otel | JVM | 13,456.40 | 6,543.75 | 37,692.60 | 16,563.80 | 2,154.70 | 6,456.05 | 746.40 | 568.95 | 1,717.80 | 1,714.85 | 479.45 |
| otel-proto | JVM | 13,861.45 | 6,765.80 | 5,211.10 | 3,348.95 | 5,137.20 | 539.50 | 342.60 | 317.45 | 1,089.25 | 420.25 | 358.25 |
| otel-proto-timesource | JVM | 13,893.60 | 6,531.55 | 5,952.85 | 3,151.10 | 5,055.50 | 527.50 | 348.65 | 314.55 | 686.65 | 350.90 | 364.80 |
| baseline | JS | 1,018.40 | 154.15 | 85.95 | 72.00 | 42.75 | 36.60 | 10.55 | 8.05 | 6.50 | 6.40 | 6.10 |
| k-perf | JS | 6,383.90 | 1,870.70 | 1,667.85 | 3,102.00 | 1,153.70 | 614.10 | 490.95 | 346.45 | 434.30 | 384.40 | 497.80 |
| otel | JS | 12,823.20 | 7,880.95 | 33,623.50 | 12,242.60 | 1,140.10 | 7,961.85 | 949.35 | 984.00 | 7,978.00 | 8,675.55 | 873.90 |
| otel-proto | JS | 12,214.15 | 7,856.90 | 8,932.05 | 2,478.90 | 2,435.85 | 762.30 | 705.55 | 1,356.50 | 713.75 | 919.25 | 603.20 |
| otel-proto-timesource | JS | 11,284.95 | 6,668.25 | 10,215.75 | 2,107.20 | 1,343.60 | 742.15 | 693.10 | 820.15 | 666.60 | 733.80 | 532.40 |
| baseline | Native | 255.15 | 1.35 | 0.90 | 0.70 | 0.70 | 1.10 | 1.15 | 1.10 | 1.50 | 1.45 | 1.30 |
| k-perf | Native | 204.90 | 161.10 | 155.80 | 255.20 | 155.50 | 155.65 | 190.15 | 223.95 | 214.40 | 166.75 | 165.85 |
| otel | Native | 656.45 | 558.90 | 12,987.80 | 13,229.30 | 560.35 | 17,500.25 | 662.60 | 686.40 | 17,013.00 | 18,570.75 | 587.75 |
| otel-proto | Native | 632.45 | 491.65 | 684.75 | 613.85 | 824.35 | 476.55 | 1,378.00 | 479.80 | 477.70 | 476.15 | 476.15 |
| otel-proto-timesource | Native | 625.70 | 495.15 | 658.75 | 602.60 | 738.15 | 473.45 | 1,387.55 | 475.70 | 476.90 | 475.90 | 472.60 |

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
