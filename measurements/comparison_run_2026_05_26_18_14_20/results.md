# Benchmark Results (2026_05_26_18_14_20)

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
| baseline JVM | 20 | 27.40 | 27.22 | 55.67 | 5.90 | 591.33 |
| k-perf JVM | 20 | 64.27 | 61.51 | 340.88 | 165.20 | 1,177.39 |
| otel JVM | 20 | 381.64 | 381.84 | 2,401.83 | 1,156.70 | 4,598.34 |
| otel-proto JVM | 20 | 140.90 | 140.36 | 828.62 | 450.40 | 1,368.15 |
| otel-proto-timesource JVM | 20 | 131.14 | 128.70 | 769.44 | 385.25 | 1,396.48 |
| baseline JS (Node) | 20 | 14.59 | 14.20 | 33.34 | 10.00 | 111.95 |
| k-perf JS (Node) | 20 | 105.50 | 98.93 | 637.39 | 485.90 | 609.69 |
| otel JS (Node) | 20 | 573.78 | 565.15 | 3,745.62 | 1,413.95 | 4,081.05 |
| otel-proto JS (Node) | 20 | 176.53 | 171.17 | 1,105.16 | 744.70 | 1,341.75 |
| otel-proto-timesource JS (Node) | 20 | 167.97 | 161.06 | 1,047.23 | 710.95 | 1,297.97 |
| baseline Native (Win) | 20 | 1.40 | 1.44 | 2.97 | 1.30 | 19.47 |
| k-perf Native (Win) | 20 | 29.59 | 29.55 | 190.55 | 165.35 | 59.35 |
| otel Native (Win) | 20 | 967.94 | 918.78 | 6,435.39 | 1,082.65 | 8,003.24 |
| otel-proto Native (Win) | 20 | 100.10 | 99.05 | 656.55 | 478.00 | 376.50 |
| otel-proto-timesource Native (Win) | 20 | 109.37 | 102.09 | 716.19 | 504.10 | 462.44 |

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
| k-perf | JVM | 29 | 340.88 | 55.67 | 139.41 | 5.79 | 88.75 | 2.40 | 179 | 1,593.3 | 746.5 | 482.4 |
| otel | JVM | 36 | 2,401.83 | 55.67 | 1,029.26 | 5.79 | 400.37 | 2.40 | 179 | 13,107.1 | 5,717.7 | 2,223.3 |
| otel-proto | JVM | 19 | 828.62 | 55.67 | 430.38 | 5.79 | 317.05 | 2.40 | 179 | 4,318.2 | 2,372.0 | 1,757.8 |
| otel-proto-timesource | JVM | 19 | 769.44 | 55.67 | 369.47 | 5.79 | 289.75 | 2.40 | 179 | 3,987.5 | 2,031.8 | 1,605.3 |
| k-perf | JS | 6 | 637.39 | 33.34 | 493.75 | 17.14 | 345.51 | 6.40 | 179 | 3,374.6 | 2,662.6 | 1,894.4 |
| otel | JS | 3 | 3,745.62 | 33.34 | 3,315.21 | 17.14 | 901.18 | 6.40 | 179 | 20,739.0 | 18,425.0 | 4,998.8 |
| otel-proto | JS | 11 | 1,105.16 | 33.34 | 766.48 | 17.14 | 637.17 | 6.40 | 179 | 5,987.8 | 4,186.3 | 3,523.9 |
| otel-proto-timesource | JS | 12 | 1,047.23 | 33.34 | 716.87 | 17.14 | 521.31 | 6.40 | 179 | 5,664.2 | 3,909.1 | 2,876.6 |
| k-perf | Native | 0 | 190.55 | 2.97 | 177.68 | 1.28 | 154.20 | 0.79 | 179 | 1,048.0 | 985.5 | 857.0 |
| otel | Native | 0 | 6,435.39 | 2.97 | 6,001.31 | 1.28 | 582.84 | 0.79 | 179 | 35,935.3 | 33,519.7 | 3,251.7 |
| otel-proto | Native | 0 | 656.55 | 2.97 | 607.77 | 1.28 | 470.17 | 0.79 | 179 | 3,651.3 | 3,388.2 | 2,622.2 |
| otel-proto-timesource | Native | 0 | 716.19 | 2.97 | 624.10 | 1.28 | 467.71 | 0.79 | 179 | 3,984.4 | 3,479.4 | 2,608.5 |

## Per-step median curve (µs)

Per-step median across all 20 runs, sampled at selected step indices.
Shows the JIT warm-up shape: cold steps on the left, steady-state on the
right. Full data (every step) is in per_step_medians.csv and in
esults.json under Results[*].PerStepMedians.

Sawtooth pattern in otel-* rows is BatchSpanProcessor flushes intersecting
the dcxp persistent-list O(n²) bug (GEMINI.md Finding #1, 2026-05-04).

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,148.60 | 23.55 | 15.85 | 10.85 | 6.00 | 6.85 | 5.90 | 5.50 | 5.40 | 5.10 | 2.20 |
| k-perf | JVM | 13,174.55 | 1,363.45 | 1,310.55 | 1,242.20 | 554.65 | 237.55 | 209.45 | 128.65 | 147.40 | 99.05 | 85.75 |
| otel | JVM | 12,270.75 | 6,421.50 | 36,518.95 | 14,818.20 | 2,212.20 | 5,632.40 | 512.55 | 452.50 | 1,706.00 | 1,957.15 | 407.55 |
| otel-proto | JVM | 12,755.60 | 6,130.10 | 4,932.10 | 3,151.65 | 5,389.60 | 673.55 | 388.55 | 434.25 | 1,517.15 | 416.90 | 378.75 |
| otel-proto-timesource | JVM | 12,871.95 | 6,341.15 | 5,057.95 | 3,286.65 | 4,758.00 | 548.35 | 340.85 | 285.20 | 560.80 | 341.95 | 334.80 |
| baseline | JS | 983.40 | 135.60 | 76.10 | 68.20 | 40.90 | 34.05 | 8.50 | 8.00 | 6.55 | 7.85 | 5.25 |
| k-perf | JS | 6,130.40 | 1,940.00 | 1,855.25 | 2,974.85 | 1,123.55 | 638.25 | 460.10 | 331.40 | 446.60 | 359.75 | 489.45 |
| otel | JS | 11,739.50 | 8,053.65 | 32,301.95 | 11,170.05 | 1,134.85 | 7,597.50 | 939.35 | 946.15 | 7,164.40 | 7,959.95 | 833.65 |
| otel-proto | JS | 11,239.30 | 7,939.55 | 7,395.55 | 2,825.25 | 2,426.35 | 755.70 | 686.15 | 1,127.90 | 647.55 | 855.75 | 556.55 |
| otel-proto-timesource | JS | 11,335.75 | 7,076.35 | 8,348.40 | 2,153.40 | 1,438.55 | 761.25 | 635.35 | 1,020.65 | 616.40 | 642.85 | 512.20 |
| baseline | Native | 228.50 | 1.40 | 0.90 | 0.70 | 0.70 | 1.00 | 1.35 | 1.45 | 1.35 | 1.30 | 1.35 |
| k-perf | Native | 202.00 | 162.50 | 155.45 | 250.70 | 155.30 | 154.45 | 199.70 | 210.40 | 191.90 | 155.10 | 154.35 |
| otel | Native | 604.75 | 521.60 | 11,365.70 | 11,583.85 | 599.40 | 15,794.70 | 705.55 | 807.85 | 15,873.35 | 15,588.15 | 903.45 |
| otel-proto | Native | 616.10 | 487.50 | 657.00 | 603.40 | 764.60 | 469.85 | 1,445.90 | 477.20 | 478.55 | 473.10 | 472.35 |
| otel-proto-timesource | Native | 610.75 | 483.10 | 642.80 | 603.25 | 770.50 | 472.10 | 1,506.85 | 469.55 | 493.70 | 473.90 | 479.65 |

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
