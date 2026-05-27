# Benchmark Results (2026_05_26_22_06_35)

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

Total = wall-clock per process. Step = mean across RunCount × StepCount samples.

| Executable | Iterations | Total mean (ms) | Total median (ms) | Step mean (µs) | Step median (µs) | Step stddev (µs) |
|------------|-----------:|----------------:|------------------:|---------------:|-----------------:|-----------------:|
| baseline JVM | 20 | 32.69 | 31.42 | 67.46 | 6.90 | 706.70 |
| k-perf JVM | 20 | 73.31 | 70.66 | 388.20 | 196.95 | 1,212.01 |
| otel JVM | 20 | 407.26 | 381.58 | 2,554.77 | 1,232.70 | 4,726.40 |
| otel-proto JVM | 20 | 185.87 | 165.08 | 1,090.82 | 609.05 | 2,106.78 |
| otel-proto-timesource JVM | 20 | 145.23 | 134.75 | 851.89 | 432.90 | 1,451.40 |
| otel-proto-anchored JVM | 20 | 137.60 | 133.27 | 806.27 | 415.20 | 1,459.34 |
| baseline JS (Node) | 20 | 16.64 | 16.74 | 35.28 | 13.40 | 108.31 |
| k-perf JS (Node) | 20 | 120.57 | 113.30 | 730.45 | 567.60 | 680.86 |
| otel JS (Node) | 20 | 596.09 | 580.56 | 3,893.73 | 1,515.45 | 4,282.91 |
| otel-proto JS (Node) | 20 | 188.52 | 186.23 | 1,182.66 | 818.15 | 1,393.66 |
| otel-proto-timesource JS (Node) | 20 | 235.56 | 228.55 | 1,499.32 | 1,120.30 | 1,383.90 |
| otel-proto-anchored JS (Node) | 20 | 180.52 | 176.35 | 1,126.86 | 785.35 | 1,349.37 |
| baseline Native (Win) | 20 | 1.48 | 1.52 | 3.26 | 1.20 | 23.40 |
| k-perf Native (Win) | 20 | 35.84 | 31.71 | 230.69 | 194.10 | 105.84 |
| otel Native (Win) | 20 | 928.39 | 900.05 | 6,172.56 | 1,134.65 | 7,657.94 |
| otel-proto Native (Win) | 20 | 111.47 | 103.69 | 732.07 | 537.00 | 446.90 |
| otel-proto-timesource Native (Win) | 20 | 94.71 | 90.19 | 619.05 | 454.70 | 378.09 |
| otel-proto-anchored Native (Win) | 20 | 107.44 | 100.07 | 706.11 | 515.40 | 419.21 |

## Overhead per instrumented method

`overhead_ns/method = (step_ns_instrumented − step_ns_baseline) / methods_per_step`

- **Full**: mean over all steps. Cold-JVM-biased.
- **Steady**: mean from `SS-start` onward (first step ≤ 2× tail-median). Quote this.
- **Envelope (P10)**: 10th percentile of steady-region per-step medians. Sawtooth-aware lower bound.

methods/step from preserved k-perf trace (`traces/`); otel-* underestimate by ~0.5 % (skipped `repeat { }` lambda body).

| Variant | Platform | SS-start | Step mean (µs) | Baseline mean (µs) | Steady (µs) | Baseline steady (µs) | Envelope P10 (µs) | Baseline envelope (µs) | Methods/step | Overhead full (ns) | Overhead steady (ns) | Overhead envelope (ns) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| k-perf | JVM | 29 | 388.20 | 67.46 | 181.86 | 7.12 | 119.80 | 2.68 | 179 | 1,791.8 | 976.2 | 654.3 |
| otel | JVM | 35 | 2,554.77 | 67.46 | 1,112.78 | 7.12 | 427.22 | 2.68 | 179 | 13,895.6 | 6,176.9 | 2,371.8 |
| otel-proto | JVM | 17 | 1,090.82 | 67.46 | 564.37 | 7.12 | 430.27 | 2.68 | 179 | 5,717.1 | 3,113.2 | 2,388.8 |
| otel-proto-timesource | JVM | 20 | 851.89 | 67.46 | 405.96 | 7.12 | 330.23 | 2.68 | 179 | 4,382.3 | 2,228.2 | 1,829.9 |
| otel-proto-anchored | JVM | 18 | 806.27 | 67.46 | 392.93 | 7.12 | 309.87 | 2.68 | 179 | 4,127.4 | 2,155.4 | 1,716.1 |
| k-perf | JS | 6 | 730.45 | 35.28 | 581.12 | 18.62 | 401.06 | 8.42 | 179 | 3,883.7 | 3,142.5 | 2,193.5 |
| otel | JS | 3 | 3,893.73 | 35.28 | 3,483.37 | 18.62 | 888.13 | 8.42 | 179 | 21,555.6 | 19,356.1 | 4,914.6 |
| otel-proto | JS | 11 | 1,182.66 | 35.28 | 848.05 | 18.62 | 694.82 | 8.42 | 179 | 6,410.0 | 4,633.7 | 3,834.7 |
| otel-proto-timesource | JS | 7 | 1,499.32 | 35.28 | 1,176.27 | 18.62 | 926.10 | 8.42 | 179 | 8,179.0 | 6,467.3 | 5,126.7 |
| otel-proto-anchored | JS | 12 | 1,126.86 | 35.28 | 806.03 | 18.62 | 594.17 | 8.42 | 179 | 6,098.2 | 4,398.9 | 3,272.4 |
| k-perf | Native | 0 | 230.69 | 3.26 | 197.53 | 1.22 | 165.94 | 1.05 | 179 | 1,270.6 | 1,096.7 | 921.1 |
| otel | Native | 0 | 6,172.56 | 3.26 | 5,691.48 | 1.22 | 608.85 | 1.05 | 179 | 34,465.4 | 31,789.2 | 3,395.5 |
| otel-proto | Native | 0 | 732.07 | 3.26 | 653.28 | 1.22 | 493.89 | 1.05 | 179 | 4,071.6 | 3,642.8 | 2,753.3 |
| otel-proto-timesource | Native | 0 | 619.05 | 3.26 | 564.83 | 1.22 | 418.53 | 1.05 | 179 | 3,440.2 | 3,148.7 | 2,332.3 |
| otel-proto-anchored | Native | 0 | 706.11 | 3.26 | 638.29 | 1.22 | 477.95 | 1.05 | 179 | 3,926.6 | 3,559.1 | 2,664.2 |

## Per-step median curve (µs)

Sampled step indices across all runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 8,348.40 | 25.80 | 16.15 | 19.30 | 6.85 | 8.40 | 7.50 | 6.45 | 6.25 | 6.00 | 2.40 |
| k-perf | JVM | 13,757.20 | 1,550.10 | 1,608.55 | 1,317.55 | 663.50 | 323.00 | 208.90 | 148.60 | 203.15 | 123.55 | 112.60 |
| otel | JVM | 13,361.65 | 7,052.35 | 36,845.30 | 14,391.40 | 2,486.00 | 7,275.15 | 865.15 | 508.85 | 1,853.70 | 1,880.10 | 446.25 |
| otel-proto | JVM | 14,933.95 | 7,656.20 | 6,044.80 | 3,654.60 | 6,192.65 | 900.70 | 471.05 | 493.40 | 702.65 | 475.45 | 409.35 |
| otel-proto-timesource | JVM | 10,814.35 | 7,105.55 | 6,021.20 | 3,710.95 | 5,417.25 | 572.90 | 416.55 | 327.20 | 476.55 | 396.95 | 356.05 |
| otel-proto-anchored | JVM | 13,468.70 | 7,078.75 | 5,255.65 | 3,480.45 | 4,564.65 | 509.50 | 319.25 | 319.80 | 407.80 | 345.55 | 383.00 |
| baseline | JS | 1,069.60 | 143.80 | 85.20 | 70.15 | 43.60 | 51.70 | 9.50 | 8.45 | 9.30 | 11.95 | 8.40 |
| k-perf | JS | 6,504.20 | 2,422.20 | 2,206.40 | 3,361.25 | 1,317.15 | 720.40 | 616.50 | 393.65 | 422.50 | 497.10 | 544.95 |
| otel | JS | 12,117.95 | 8,133.85 | 33,113.35 | 12,024.70 | 1,230.50 | 8,100.85 | 999.55 | 1,033.50 | 7,354.20 | 7,886.10 | 880.00 |
| otel-proto | JS | 12,082.05 | 7,989.90 | 8,070.75 | 2,867.85 | 2,478.70 | 784.95 | 698.35 | 1,350.50 | 759.65 | 945.75 | 591.45 |
| otel-proto-timesource | JS | 12,744.25 | 8,782.80 | 7,197.45 | 4,056.75 | 1,599.50 | 1,230.35 | 1,096.55 | 1,322.55 | 962.10 | 1,155.50 | 837.85 |
| otel-proto-anchored | JS | 11,785.20 | 7,511.70 | 8,926.25 | 2,273.60 | 1,558.00 | 787.55 | 669.95 | 1,028.15 | 707.40 | 719.55 | 527.00 |
| baseline | Native | 268.50 | 1.55 | 1.00 | 0.80 | 0.80 | 1.30 | 1.10 | 1.35 | 1.40 | 1.40 | 1.20 |
| k-perf | Native | 222.55 | 164.55 | 168.00 | 270.60 | 163.80 | 166.35 | 212.50 | 228.55 | 213.20 | 176.75 | 176.90 |
| otel | Native | 637.25 | 585.65 | 11,906.05 | 11,963.25 | 601.50 | 15,393.40 | 756.15 | 659.35 | 15,791.60 | 13,655.60 | 711.40 |
| otel-proto | Native | 619.10 | 493.60 | 638.25 | 606.35 | 730.15 | 535.70 | 1,592.75 | 503.95 | 515.65 | 529.50 | 516.90 |
| otel-proto-timesource | Native | 543.85 | 489.10 | 560.55 | 564.40 | 623.45 | 422.10 | 420.10 | 497.75 | 423.90 | 437.05 | 444.70 |
| otel-proto-anchored | Native | 635.15 | 525.75 | 638.70 | 628.75 | 759.30 | 481.40 | 1,538.25 | 489.75 | 481.20 | 489.00 | 483.50 |

## Per-step times (JIT warmup curves)

> Curve shape: JVM C1≈step 1-2, C2≈step 55. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction.
