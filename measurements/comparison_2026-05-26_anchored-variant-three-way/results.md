# Benchmark Results (2026_05_26_20_50_50)

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
| baseline JVM | 20 | 27.40 | 27.01 | 57.43 | 5.90 | 600.37 |
| k-perf JVM | 20 | 61.30 | 61.38 | 332.22 | 177.15 | 973.11 |
| otel JVM | 20 | 370.41 | 357.51 | 2,312.30 | 1,147.50 | 4,299.64 |
| otel-proto JVM | 20 | 161.56 | 139.15 | 944.41 | 444.30 | 1,665.12 |
| otel-proto-timesource JVM | 20 | 136.15 | 133.66 | 801.91 | 392.60 | 1,319.95 |
| otel-proto-anchored JVM | 20 | 142.88 | 137.77 | 836.74 | 438.75 | 1,465.73 |
| baseline JS (Node) | 20 | 15.72 | 15.65 | 35.10 | 11.60 | 121.65 |
| k-perf JS (Node) | 20 | 108.35 | 106.43 | 654.89 | 497.40 | 637.77 |
| otel JS (Node) | 20 | 599.81 | 586.39 | 3,915.97 | 1,561.90 | 4,208.54 |
| otel-proto JS (Node) | 20 | 182.77 | 178.14 | 1,142.55 | 763.20 | 1,481.89 |
| otel-proto-timesource JS (Node) | 20 | 214.72 | 208.88 | 1,364.73 | 996.70 | 1,365.31 |
| otel-proto-anchored JS (Node) | 20 | 170.58 | 168.52 | 1,064.04 | 725.35 | 1,322.35 |
| baseline Native (Win) | 20 | 1.36 | 1.41 | 2.96 | 0.90 | 21.09 |
| k-perf Native (Win) | 20 | 31.76 | 31.60 | 204.47 | 171.50 | 88.47 |
| otel Native (Win) | 20 | 1,025.33 | 987.11 | 6,815.99 | 1,121.80 | 8,520.43 |
| otel-proto Native (Win) | 20 | 103.18 | 101.67 | 677.85 | 491.85 | 389.36 |
| otel-proto-timesource Native (Win) | 20 | 89.94 | 88.54 | 588.98 | 416.90 | 364.72 |
| otel-proto-anchored Native (Win) | 20 | 104.64 | 100.34 | 687.66 | 478.60 | 426.02 |

## Overhead per instrumented method

`overhead_ns/method = (step_ns_instrumented − step_ns_baseline) / methods_per_step`

- **Full**: mean over all steps. Cold-JVM-biased.
- **Steady**: mean from `SS-start` onward (first step ≤ 2× tail-median). Quote this.
- **Envelope (P10)**: 10th percentile of steady-region per-step medians. Sawtooth-aware lower bound.

methods/step from preserved k-perf trace (`traces/`); otel-* underestimate by ~0.5 % (skipped `repeat { }` lambda body).

| Variant | Platform | SS-start | Step mean (µs) | Baseline mean (µs) | Steady (µs) | Baseline steady (µs) | Envelope P10 (µs) | Baseline envelope (µs) | Methods/step | Overhead full (ns) | Overhead steady (ns) | Overhead envelope (ns) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| k-perf | JVM | 26 | 332.22 | 57.43 | 153.30 | 5.76 | 102.93 | 3.69 | 179 | 1,535.2 | 824.2 | 554.4 |
| otel | JVM | 38 | 2,312.30 | 57.43 | 999.95 | 5.76 | 409.33 | 3.69 | 179 | 12,597.1 | 5,554.1 | 2,266.1 |
| otel-proto | JVM | 19 | 944.41 | 57.43 | 424.85 | 5.76 | 330.40 | 3.69 | 179 | 4,955.2 | 2,341.3 | 1,825.2 |
| otel-proto-timesource | JVM | 20 | 801.91 | 57.43 | 381.58 | 5.76 | 323.33 | 3.69 | 179 | 4,159.1 | 2,099.5 | 1,785.7 |
| otel-proto-anchored | JVM | 18 | 836.74 | 57.43 | 416.74 | 5.76 | 344.70 | 3.69 | 179 | 4,353.7 | 2,295.9 | 1,905.1 |
| k-perf | JS | 6 | 654.89 | 35.10 | 523.31 | 17.30 | 373.27 | 6.40 | 179 | 3,462.5 | 2,826.9 | 2,049.6 |
| otel | JS | 3 | 3,915.97 | 35.10 | 3,442.03 | 17.30 | 914.31 | 6.40 | 179 | 21,680.8 | 19,132.6 | 5,072.1 |
| otel-proto | JS | 11 | 1,142.55 | 35.10 | 780.14 | 17.30 | 644.60 | 6.40 | 179 | 6,186.9 | 4,261.7 | 3,565.4 |
| otel-proto-timesource | JS | 7 | 1,364.73 | 35.10 | 1,032.52 | 17.30 | 840.39 | 6.40 | 179 | 7,428.1 | 5,671.6 | 4,659.2 |
| otel-proto-anchored | JS | 12 | 1,064.04 | 35.10 | 734.46 | 17.30 | 553.83 | 6.40 | 179 | 5,748.3 | 4,006.5 | 3,058.2 |
| k-perf | Native | 0 | 204.47 | 2.96 | 180.84 | 1.01 | 155.04 | 0.70 | 179 | 1,125.8 | 1,004.6 | 862.2 |
| otel | Native | 0 | 6,815.99 | 2.96 | 6,368.63 | 1.01 | 604.98 | 0.70 | 179 | 38,061.6 | 35,573.3 | 3,375.9 |
| otel-proto | Native | 0 | 677.85 | 2.96 | 623.81 | 1.01 | 482.89 | 0.70 | 179 | 3,770.3 | 3,479.3 | 2,693.8 |
| otel-proto-timesource | Native | 0 | 588.98 | 2.96 | 547.02 | 1.01 | 409.47 | 0.70 | 179 | 3,273.8 | 3,050.3 | 2,283.6 |
| otel-proto-anchored | Native | 0 | 687.66 | 2.96 | 611.28 | 1.01 | 469.19 | 0.70 | 179 | 3,825.2 | 3,409.3 | 2,617.2 |

## Per-step median curve (µs)

Sampled step indices across all runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,138.80 | 22.35 | 15.30 | 17.60 | 6.10 | 7.25 | 6.00 | 5.40 | 5.45 | 5.10 | 2.55 |
| k-perf | JVM | 11,651.20 | 1,398.05 | 1,303.25 | 1,159.40 | 553.45 | 296.35 | 168.30 | 117.60 | 175.05 | 107.60 | 100.30 |
| otel | JVM | 11,461.70 | 6,115.60 | 34,544.55 | 14,528.90 | 1,995.95 | 6,055.35 | 749.20 | 443.30 | 1,626.25 | 1,671.45 | 396.20 |
| otel-proto | JVM | 13,371.25 | 6,509.30 | 5,087.70 | 3,291.45 | 5,511.60 | 618.10 | 371.85 | 305.85 | 1,270.00 | 407.30 | 347.10 |
| otel-proto-timesource | JVM | 10,091.75 | 6,812.55 | 5,190.40 | 3,081.30 | 5,199.95 | 544.20 | 336.45 | 292.60 | 414.60 | 364.85 | 359.20 |
| otel-proto-anchored | JVM | 13,299.80 | 6,761.55 | 4,890.60 | 2,903.20 | 1,799.35 | 522.35 | 372.70 | 348.75 | 761.20 | 357.00 | 346.65 |
| baseline | JS | 1,003.20 | 183.55 | 110.65 | 67.35 | 44.80 | 41.05 | 8.85 | 8.45 | 6.60 | 9.50 | 6.10 |
| k-perf | JS | 5,766.55 | 2,194.90 | 1,749.05 | 2,612.80 | 1,195.00 | 682.05 | 465.30 | 321.90 | 411.65 | 452.45 | 462.60 |
| otel | JS | 11,820.55 | 7,590.70 | 32,509.20 | 11,463.75 | 1,184.05 | 7,765.85 | 947.90 | 962.00 | 7,927.70 | 7,926.75 | 859.45 |
| otel-proto | JS | 11,967.25 | 8,527.40 | 8,110.60 | 2,832.05 | 2,396.65 | 747.80 | 708.50 | 1,208.25 | 675.20 | 923.95 | 549.10 |
| otel-proto-timesource | JS | 12,914.00 | 8,806.35 | 6,732.60 | 3,675.65 | 1,422.65 | 1,010.00 | 946.65 | 1,257.70 | 886.35 | 951.35 | 827.45 |
| otel-proto-anchored | JS | 11,828.40 | 6,917.65 | 8,958.10 | 2,085.50 | 1,349.95 | 772.35 | 665.25 | 797.90 | 651.50 | 643.10 | 505.40 |
| baseline | Native | 243.85 | 1.40 | 0.90 | 0.70 | 0.70 | 1.20 | 1.05 | 1.00 | 1.45 | 1.00 | 1.25 |
| k-perf | Native | 200.05 | 162.05 | 155.45 | 244.05 | 155.40 | 154.45 | 211.15 | 215.65 | 197.10 | 156.25 | 155.60 |
| otel | Native | 619.05 | 548.65 | 12,023.30 | 12,094.55 | 598.80 | 17,375.70 | 606.10 | 779.65 | 16,746.05 | 16,727.10 | 677.70 |
| otel-proto | Native | 605.50 | 493.50 | 634.50 | 602.10 | 723.10 | 479.00 | 1,448.60 | 490.85 | 491.70 | 484.25 | 481.75 |
| otel-proto-timesource | Native | 531.55 | 460.30 | 562.85 | 529.90 | 597.35 | 412.10 | 411.05 | 1,393.20 | 413.05 | 419.95 | 415.60 |
| otel-proto-anchored | Native | 596.25 | 474.20 | 630.55 | 594.35 | 711.50 | 462.45 | 1,360.95 | 475.70 | 478.80 | 470.60 | 469.85 |

## Per-step times (JIT warmup curves)

> Curve shape: JVM C1≈step 1-2, C2≈step 55. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction.
