# Benchmark Results (2026_05_26_14_56_43)

## Parameters
- **Warmup Iterations:** 0
- **Run Iterations:** 1
- **Step Count (workload calls per process):** 20
- **Clean Build:** True
- **Run timeout (s):** 100

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
| baseline JVM | 1 | 21.78 | 21.78 | 364.50 | 9.20 | 1,579.36 |
| k-perf JVM | 1 | 27.79 | 27.79 | 1,117.81 | 535.95 | 2,319.43 |
| otel JVM | 1 | 129.58 | 129.58 | 6,025.30 | 2,537.95 | 7,808.56 |
| otel-proto JVM | 1 | 433.04 | 433.04 | 21,025.51 | 17,301.75 | 15,334.21 |
| otel-proto-timesource JVM | 1 | 61.28 | 61.28 | 2,676.86 | 1,889.95 | 2,159.71 |
| baseline JS (Node) | 1 | 7.45 | 7.45 | 101.63 | 35.00 | 207.60 |
| k-perf JS (Node) | 1 | 31.95 | 31.95 | 1,285.48 | 744.95 | 1,661.82 |
| otel JS (Node) | 1 | 114.80 | 114.80 | 5,470.29 | 1,857.80 | 6,635.17 |
| otel-proto JS (Node) | 1 | 144.28 | 144.28 | 6,902.67 | 4,180.60 | 6,793.81 |
| otel-proto-timesource JS (Node) | 1 | 63.49 | 63.49 | 2,933.05 | 2,107.05 | 2,629.77 |
| baseline Native (Win) | 1 | 0.37 | 0.37 | 12.09 | 0.70 | 50.91 |
| k-perf Native (Win) | 1 | 4.08 | 4.08 | 197.57 | 186.35 | 38.74 |
| otel Native (Win) | 1 | 93.75 | 93.75 | 4,672.54 | 876.40 | 6,153.40 |
| otel-proto-timesource Native (Win) | 1 | 13.14 | 13.14 | 649.77 | 619.75 | 102.12 |

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
| k-perf | JVM | 4 | 1,117.81 | 364.50 | 514.83 | 10.23 | 307.45 | 6.36 | 179 | 4,208.4 | 2,819.0 | 1,682.1 |
| otel | JVM | 4 | 6,025.30 | 364.50 | 3,971.06 | 10.23 | 1,149.55 | 6.36 | 179 | 31,624.6 | 22,127.6 | 6,386.5 |
| otel-proto | JVM | 1 | 21,025.51 | 364.50 | 17,869.44 | 10.23 | 10,770.66 | 6.36 | 179 | 115,424.6 | 99,772.1 | 60,135.7 |
| otel-proto-timesource | JVM | 8 | 2,676.86 | 364.50 | 1,548.70 | 10.23 | 866.42 | 6.36 | 179 | 12,918.2 | 8,594.8 | 4,804.8 |
| k-perf | JS | 3 | 1,285.48 | 101.63 | 819.68 | 27.74 | 479.42 | 10.12 | 179 | 6,613.7 | 4,424.2 | 2,621.8 |
| otel | JS | 3 | 5,470.29 | 101.63 | 3,481.41 | 27.74 | 1,092.44 | 10.12 | 179 | 29,992.5 | 19,294.2 | 6,046.5 |
| otel-proto | JS | 4 | 6,902.67 | 101.63 | 4,086.49 | 27.74 | 2,742.55 | 10.12 | 179 | 37,994.7 | 22,674.6 | 15,265.0 |
| otel-proto-timesource | JS | 4 | 2,933.05 | 101.63 | 1,865.50 | 27.74 | 1,014.35 | 10.12 | 179 | 15,818.0 | 10,266.8 | 5,610.2 |
| k-perf | Native | 0 | 197.57 | 12.09 | 197.57 | 0.71 | 147.48 | 0.60 | 179 | 1,036.2 | 1,099.8 | 820.6 |
| otel | Native | 0 | 4,672.54 | 12.09 | 4,672.54 | 0.71 | 527.69 | 0.60 | 179 | 26,036.0 | 26,099.6 | 2,944.6 |
| otel-proto-timesource | Native | 0 | 649.77 | 12.09 | 649.77 | 0.71 | 545.26 | 0.60 | 179 | 3,562.4 | 3,626.0 | 3,042.8 |

## Per-step median curve (µs)

Per-step median across all 1 runs, sampled at selected step indices.
Shows the JIT warm-up shape: cold steps on the left, steady-state on the
right. Full data (every step) is in per_step_medians.csv and in
esults.json under Results[*].PerStepMedians.

Sawtooth pattern in otel-* rows is BatchSpanProcessor flushes intersecting
the dcxp persistent-list O(n²) bug (GEMINI.md Finding #1, 2026-05-04).

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s19 |
|---|---|---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,074.40 | 24.90 | 16.80 | 24.40 | 8.40 | 7.30 |
| k-perf | JVM | 10,880.70 | 1,211.60 | 1,190.10 | 1,355.80 | 557.30 | 295.30 |
| otel | JVM | 11,574.80 | 8,235.10 | 33,830.80 | 15,085.60 | 1,767.40 | 1,450.80 |
| otel-proto | JVM | 80,990.80 | 25,539.70 | 21,349.10 | 19,764.50 | 15,065.00 | 10,418.50 |
| otel-proto-timesource | JVM | 8,993.30 | 5,835.70 | 4,889.30 | 2,862.80 | 5,232.20 | 891.80 |
| baseline | JS | 958.60 | 128.00 | 181.20 | 90.50 | 59.70 | 14.90 |
| k-perf | JS | 7,888.70 | 2,114.00 | 1,772.40 | 2,560.40 | 1,011.00 | 549.80 |
| otel | JS | 14,231.30 | 7,685.60 | 28,305.00 | 10,218.10 | 1,093.60 | 2,093.70 |
| otel-proto | JS | 29,235.00 | 16,897.20 | 17,198.80 | 5,201.80 | 2,738.20 | 2,612.40 |
| otel-proto-timesource | JS | 11,037.70 | 7,813.20 | 6,764.70 | 3,388.80 | 1,322.00 | 952.00 |
| baseline | Native | 228.40 | 1.30 | 0.80 | 0.60 | 0.70 | 0.70 |
| k-perf | Native | 187.60 | 177.20 | 233.10 | 264.60 | 146.80 | 214.00 |
| otel | Native | 579.40 | 654.80 | 10,240.10 | 13,388.70 | 692.60 | 506.90 |
| otel-proto-timesource | Native | 498.30 | 613.40 | 643.40 | 575.70 | 892.10 | 528.70 |

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
