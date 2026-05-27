# Benchmark Results (2026_05_26_16_47_23)

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

Total = wall-clock per process. Step = mean across RunCount × StepCount samples.

| Executable | Iterations | Total mean (ms) | Total median (ms) | Step mean (µs) | Step median (µs) | Step stddev (µs) |
|------------|-----------:|----------------:|------------------:|---------------:|-----------------:|-----------------:|
| baseline JVM | 20 | 30.38 | 28.44 | 61.52 | 6.50 | 640.60 |
| k-perf JVM | 20 | 63.72 | 58.73 | 341.06 | 168.15 | 1,116.90 |
| otel JVM | 20 | 395.81 | 357.77 | 2,492.04 | 1,183.50 | 4,831.94 |
| otel-proto JVM | 20 | 612.05 | 581.22 | 3,950.64 | 534.90 | 12,315.24 |
| otel-proto-timesource JVM | 20 | 603.89 | 589.35 | 3,907.47 | 539.30 | 12,311.53 |
| baseline JS (Node) | 20 | 15.46 | 15.13 | 34.60 | 12.05 | 108.82 |
| k-perf JS (Node) | 20 | 108.57 | 101.66 | 659.55 | 497.55 | 598.05 |
| otel JS (Node) | 20 | 572.22 | 546.79 | 3,743.53 | 1,423.35 | 4,053.01 |
| otel-proto JS (Node) | 20 | 1,239.95 | 1,208.69 | 8,197.88 | 1,087.20 | 22,134.17 |
| otel-proto-timesource JS (Node) | 20 | 1,256.23 | 1,245.91 | 8,311.08 | 1,333.55 | 21,555.85 |

## Overhead per instrumented method

`overhead_ns/method = (step_ns_instrumented − step_ns_baseline) / methods_per_step`

- **Full**: mean over all steps. Cold-JVM-biased.
- **Steady**: mean from `SS-start` onward (first step ≤ 2× tail-median). Quote this.
- **Envelope (P10)**: 10th percentile of steady-region per-step medians. Sawtooth-aware lower bound.

methods/step from preserved k-perf trace (`traces/`); otel-* underestimate by ~0.5 % (skipped `repeat { }` lambda body).

| Variant | Platform | SS-start | Step mean (µs) | Baseline mean (µs) | Steady (µs) | Baseline steady (µs) | Envelope P10 (µs) | Baseline envelope (µs) | Methods/step | Overhead full (ns) | Overhead steady (ns) | Overhead envelope (ns) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| k-perf | JVM | 28 | 341.06 | 61.52 | 148.10 | 6.69 | 97.76 | 4.53 | 179 | 1,561.6 | 790.0 | 520.9 |
| otel | JVM | 43 | 2,492.04 | 61.52 | 1,005.74 | 6.69 | 408.69 | 4.53 | 179 | 13,578.3 | 5,581.2 | 2,257.9 |
| otel-proto | JVM | 13 | 3,950.64 | 61.52 | 2,819.67 | 6.69 | 240.42 | 4.53 | 179 | 21,726.9 | 15,715.0 | 1,317.9 |
| otel-proto-timesource | JVM | 12 | 3,907.47 | 61.52 | 2,799.00 | 6.69 | 253.40 | 4.53 | 179 | 21,485.7 | 15,599.5 | 1,390.3 |
| k-perf | JS | 6 | 659.55 | 34.60 | 528.05 | 18.56 | 374.95 | 7.30 | 179 | 3,491.3 | 2,846.3 | 2,053.9 |
| otel | JS | 3 | 3,743.53 | 34.60 | 3,340.62 | 18.56 | 884.07 | 7.30 | 179 | 20,720.2 | 18,559.0 | 4,898.2 |
| otel-proto | JS | 8 | 8,197.88 | 34.60 | 8,094.70 | 18.56 | 829.70 | 7.30 | 179 | 45,604.9 | 45,118.1 | 4,594.4 |
| otel-proto-timesource | JS | 7 | 8,311.08 | 34.60 | 8,226.54 | 18.56 | 991.27 | 7.30 | 179 | 46,237.3 | 45,854.6 | 5,497.1 |

## Per-step median curve (µs)

Sampled step indices across all runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,569.05 | 24.20 | 16.50 | 18.20 | 6.45 | 9.35 | 6.60 | 5.75 | 6.15 | 6.20 | 2.45 |
| k-perf | JVM | 12,614.75 | 1,362.45 | 1,314.00 | 1,148.05 | 525.95 | 342.45 | 194.60 | 124.70 | 176.70 | 97.70 | 96.65 |
| otel | JVM | 12,656.40 | 6,576.40 | 36,963.35 | 14,327.85 | 2,125.30 | 6,521.20 | 775.70 | 481.75 | 1,804.75 | 1,729.60 | 453.05 |
| otel-proto | JVM | 13,418.40 | 6,485.40 | 5,319.95 | 3,237.00 | 5,245.55 | 600.95 | 389.35 | 377.75 | 1,314.30 | 26,914.25 | 221.70 |
| otel-proto-timesource | JVM | 10,324.80 | 6,707.70 | 5,366.55 | 3,334.65 | 5,252.50 | 587.95 | 409.10 | 308.35 | 1,509.75 | 24,653.65 | 196.80 |
| baseline | JS | 1,004.10 | 134.40 | 78.35 | 60.45 | 44.95 | 36.95 | 9.05 | 8.40 | 7.05 | 8.45 | 6.40 |
| k-perf | JS | 6,146.65 | 2,061.65 | 1,790.85 | 2,928.00 | 1,221.00 | 678.10 | 525.45 | 369.05 | 406.65 | 394.00 | 440.50 |
| otel | JS | 11,254.00 | 7,223.75 | 30,449.20 | 10,983.35 | 1,150.80 | 7,454.70 | 965.40 | 968.85 | 7,618.75 | 7,737.90 | 806.55 |
| otel-proto | JS | 11,307.70 | 7,975.75 | 7,641.20 | 2,777.20 | 2,383.00 | 1,110.50 | 1,093.30 | 929.15 | 826.60 | 72,933.25 | 798.40 |
| otel-proto-timesource | JS | 12,091.25 | 8,197.60 | 6,438.50 | 3,351.45 | 1,402.65 | 1,287.30 | 1,282.05 | 1,066.55 | 1,013.20 | 80,866.60 | 975.05 |

## Per-step times (JIT warmup curves)

> Curve shape: JVM C1≈step 1-2, C2≈step 55. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction.
