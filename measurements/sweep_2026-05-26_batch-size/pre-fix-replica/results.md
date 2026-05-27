# Benchmark Results (2026_05_26_17_00_05)

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
| baseline JVM | 20 | 29.57 | 28.18 | 60.47 | 6.40 | 635.87 |
| k-perf JVM | 20 | 60.49 | 58.62 | 325.21 | 162.00 | 1,036.79 |
| otel JVM | 20 | 377.99 | 353.09 | 2,383.43 | 1,161.25 | 4,622.01 |
| otel-proto JVM | 20 | 418.70 | 388.97 | 2,662.82 | 845.75 | 5,713.03 |
| otel-proto-timesource JVM | 20 | 376.89 | 364.18 | 2,395.54 | 704.45 | 5,375.27 |
| baseline JS (Node) | 20 | 15.82 | 15.15 | 34.69 | 12.15 | 108.59 |
| k-perf JS (Node) | 20 | 101.07 | 99.36 | 611.97 | 472.40 | 574.71 |
| otel JS (Node) | 20 | 574.11 | 547.14 | 3,754.53 | 1,449.65 | 4,159.11 |
| otel-proto JS (Node) | 20 | 624.08 | 613.44 | 4,091.87 | 1,251.55 | 4,929.37 |
| otel-proto-timesource JS (Node) | 20 | 645.90 | 637.19 | 4,245.64 | 1,543.65 | 4,674.30 |

## Overhead per instrumented method

`overhead_ns/method = (step_ns_instrumented − step_ns_baseline) / methods_per_step`

- **Full**: mean over all steps. Cold-JVM-biased.
- **Steady**: mean from `SS-start` onward (first step ≤ 2× tail-median). Quote this.
- **Envelope (P10)**: 10th percentile of steady-region per-step medians. Sawtooth-aware lower bound.

methods/step from preserved k-perf trace (`traces/`); otel-* underestimate by ~0.5 % (skipped `repeat { }` lambda body).

| Variant | Platform | SS-start | Step mean (µs) | Baseline mean (µs) | Steady (µs) | Baseline steady (µs) | Envelope P10 (µs) | Baseline envelope (µs) | Methods/step | Overhead full (ns) | Overhead steady (ns) | Overhead envelope (ns) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| k-perf | JVM | 32 | 325.21 | 60.47 | 136.46 | 6.46 | 90.94 | 2.90 | 179 | 1,479.0 | 726.3 | 491.8 |
| otel | JVM | 38 | 2,383.43 | 60.47 | 973.66 | 6.46 | 378.17 | 2.90 | 179 | 12,977.4 | 5,403.3 | 2,096.5 |
| otel-proto | JVM | 29 | 2,662.82 | 60.47 | 1,425.61 | 6.46 | 335.35 | 2.90 | 179 | 14,538.2 | 7,928.2 | 1,857.3 |
| otel-proto-timesource | JVM | 26 | 2,395.54 | 60.47 | 1,359.96 | 6.46 | 324.25 | 2.90 | 179 | 13,045.1 | 7,561.4 | 1,795.3 |
| k-perf | JS | 6 | 611.97 | 34.69 | 506.91 | 18.96 | 349.60 | 7.10 | 179 | 3,225.0 | 2,726.0 | 1,913.4 |
| otel | JS | 3 | 3,754.53 | 34.69 | 3,309.08 | 18.96 | 862.49 | 7.10 | 179 | 20,781.2 | 18,380.5 | 4,778.7 |
| otel-proto | JS | 4 | 4,091.87 | 34.69 | 3,667.64 | 18.96 | 836.75 | 7.10 | 179 | 22,665.8 | 20,383.7 | 4,634.9 |
| otel-proto-timesource | JS | 6 | 4,245.64 | 34.69 | 3,764.51 | 18.96 | 1,021.57 | 7.10 | 179 | 23,524.8 | 20,924.8 | 5,667.5 |

## Per-step median curve (µs)

Sampled step indices across all runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,706.15 | 24.65 | 16.90 | 20.55 | 6.60 | 7.55 | 6.40 | 6.20 | 6.25 | 5.80 | 2.40 |
| k-perf | JVM | 12,445.50 | 1,468.35 | 1,321.10 | 1,220.15 | 574.65 | 311.85 | 149.60 | 109.05 | 136.85 | 107.50 | 93.45 |
| otel | JVM | 12,803.85 | 6,771.75 | 36,302.65 | 13,806.85 | 2,300.90 | 6,394.85 | 859.30 | 373.50 | 1,679.25 | 1,655.45 | 372.90 |
| otel-proto | JVM | 13,594.50 | 6,580.65 | 57,785.00 | 18,231.10 | 1,522.10 | 5,822.90 | 482.30 | 390.80 | 2,300.20 | 2,301.95 | 329.75 |
| otel-proto-timesource | JVM | 9,973.15 | 6,495.90 | 55,040.35 | 17,827.25 | 1,639.65 | 5,127.60 | 435.00 | 340.95 | 2,367.85 | 2,285.20 | 296.15 |
| baseline | JS | 1,024.85 | 151.65 | 82.55 | 67.95 | 50.05 | 39.30 | 9.70 | 9.30 | 6.80 | 7.25 | 6.40 |
| k-perf | JS | 5,992.60 | 1,960.50 | 1,764.35 | 3,236.25 | 1,189.60 | 695.55 | 503.90 | 346.70 | 358.85 | 426.20 | 412.85 |
| otel | JS | 11,739.75 | 7,637.35 | 30,691.95 | 10,869.45 | 1,175.65 | 7,381.45 | 991.00 | 1,037.75 | 7,059.90 | 7,563.85 | 841.35 |
| otel-proto | JS | 11,368.75 | 7,778.10 | 41,521.10 | 13,144.25 | 2,011.70 | 9,579.35 | 893.00 | 2,314.45 | 7,102.90 | 7,984.30 | 1,034.15 |
| otel-proto-timesource | JS | 12,083.45 | 7,836.90 | 39,053.85 | 13,448.10 | 1,422.65 | 8,101.70 | 3,079.55 | 1,101.15 | 7,346.85 | 6,497.75 | 1,360.85 |

## Per-step times (JIT warmup curves)

> Curve shape: JVM C1≈step 1-2, C2≈step 55. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction.
