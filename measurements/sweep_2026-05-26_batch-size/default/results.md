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

Total = wall-clock per process. Step = mean across RunCount × StepCount samples.

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

`overhead_ns/method = (step_ns_instrumented − step_ns_baseline) / methods_per_step`

- **Full**: mean over all steps. Cold-JVM-biased.
- **Steady**: mean from `SS-start` onward (first step ≤ 2× tail-median). Quote this.
- **Envelope (P10)**: 10th percentile of steady-region per-step medians. Sawtooth-aware lower bound.

methods/step from preserved k-perf trace (`traces/`); otel-* underestimate by ~0.5 % (skipped `repeat { }` lambda body).

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

Sampled step indices across all runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

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

> Curve shape: JVM C1≈step 1-2, C2≈step 55. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction.
