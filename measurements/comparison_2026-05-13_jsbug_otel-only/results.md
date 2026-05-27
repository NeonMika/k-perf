# Benchmark Results (2026_05_13_14_30_33)

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
| baseline JVM | 20 | 27.80 | 27.73 | 57.85 | 5.80 | 603.66 |
| k-perf JVM | 20 | 174.66 | 176.50 | 1,096.84 | 839.95 | 1,795.47 |
| otel JVM | 20 | 360.22 | 356.45 | 2,275.51 | 1,238.30 | 4,582.99 |
| otel-proto JVM | 20 | 449.20 | 444.29 | 2,865.74 | 976.05 | 5,713.36 |
| otel-proto-timesource JVM | 20 | 432.04 | 428.81 | 2,763.91 | 982.85 | 5,596.72 |
| baseline JS (Node) | 20 | 14.36 | 14.29 | 32.78 | 10.20 | 99.38 |
| k-perf JS (Node) | 20 | 352.01 | 349.21 | 2,293.38 | 1,965.65 | 1,358.36 |
| otel JS (Node) | 20 | 14.57 | 14.38 | 10,693.51 | 10,573.85 | 463.63 |
| otel-proto JS (Node) | 20 | 16.03 | 14.67 | 11,912.02 | 10,774.60 | 4,369.75 |
| otel-proto-timesource JS (Node) | 20 | 17.45 | 16.56 | 13,033.08 | 12,428.25 | 1,914.71 |
| baseline Native (Win) | 20 | 1.24 | 1.13 | 2.77 | 0.80 | 20.52 |
| k-perf Native (Win) | 20 | 167.76 | 167.66 | 1,109.96 | 1,045.55 | 345.24 |
| otel Native (Win) | 20 | 745.16 | 731.61 | 4,954.50 | 856.30 | 5,813.53 |
| otel-proto Native (Win) | 20 | 625.25 | 622.18 | 4,156.88 | 622.90 | 4,905.51 |
| otel-proto-timesource Native (Win) | 20 | 653.73 | 616.08 | 4,346.70 | 760.90 | 5,289.95 |

## Overhead per instrumented method

`overhead_ns/method = (step_ns_instrumented − step_ns_baseline) / methods_per_step`

- **Full**: mean over all steps. Cold-JVM-biased.
- **Steady**: mean from `SS-start` onward (first step ≤ 2× tail-median). Quote this.

methods/step from preserved k-perf trace (`traces/`); otel-* underestimate by ~0.5 % (skipped `repeat { }` lambda body).

| Variant | Platform | SS-start | Step mean (µs) | Baseline mean (µs) | Steady (µs) | Baseline steady (µs) | Methods/step | Overhead full (ns) | Overhead steady (ns) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|
| k-perf | JVM | 11 | 1,096.84 | 57.85 | 820.80 | 5.81 | 179 | 5,804.4 | 4,553.0 |
| otel | JVM | 52 | 2,275.51 | 57.85 | 820.45 | 5.81 | 179 | 12,389.1 | 4,551.1 |
| otel-proto | JVM | 38 | 2,865.74 | 57.85 | 1,576.55 | 5.81 | 179 | 15,686.5 | 8,775.1 |
| otel-proto-timesource | JVM | 41 | 2,763.91 | 57.85 | 1,524.21 | 5.81 | 179 | 15,117.6 | 8,482.7 |
| k-perf | JS | 3 | 2,293.38 | 32.78 | 2,131.75 | 16.66 | 179 | 12,629.0 | 11,816.1 |
| otel | JS | 0 | 10,693.51 | 32.78 | 10,573.85 | 16.66 | 179 | 59,557.2 | 58,978.7 |
| otel-proto | JS | 0 | 11,912.02 | 32.78 | 10,774.60 | 16.66 | 179 | 66,364.5 | 60,100.2 |
| otel-proto-timesource | JS | 0 | 13,033.08 | 32.78 | 12,428.25 | 16.66 | 179 | 72,627.4 | 69,338.5 |
| k-perf | Native | 1 | 1,109.96 | 2.77 | 1,059.23 | 0.81 | 179 | 6,185.4 | 5,913.0 |
| otel | Native | 0 | 4,954.50 | 2.77 | 4,749.73 | 0.81 | 179 | 27,663.3 | 26,530.3 |
| otel-proto | Native | 0 | 4,156.88 | 2.77 | 4,056.68 | 0.81 | 179 | 23,207.3 | 22,658.5 |
| otel-proto-timesource | Native | 0 | 4,346.70 | 2.77 | 4,066.81 | 0.81 | 179 | 24,267.8 | 22,715.1 |

## Per-step median curve (µs)

Sampled step indices across all runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,380.65 | 20.95 | 15.15 | 10.85 | 5.90 | 6.85 | 5.80 | 5.45 | 5.25 | 5.15 | 2.30 |
| k-perf | JVM | 21,336.20 | 3,846.05 | 3,323.25 | 2,374.95 | 1,444.25 | 964.35 | 859.65 | 801.95 | 761.30 | 684.80 | 681.15 |
| otel | JVM | 12,346.20 | 6,187.30 | 39,171.55 | 15,685.70 | 2,247.55 | 4,160.35 | 1,086.70 | 337.10 | 1,421.05 | 1,580.75 | 482.50 |
| otel-proto | JVM | 13,477.70 | 6,282.10 | 56,326.05 | 19,889.45 | 1,990.95 | 6,280.95 | 662.75 | 353.00 | 2,677.55 | 2,495.75 | 303.15 |
| otel-proto-timesource | JVM | 10,220.25 | 6,048.45 | 56,589.45 | 18,760.50 | 2,085.15 | 6,502.70 | 442.75 | 424.25 | 2,522.20 | 2,398.50 | 327.15 |
| baseline | JS | 984.00 | 120.35 | 95.60 | 57.65 | 47.30 | 39.40 | 8.60 | 8.40 | 6.40 | 6.80 | 5.40 |
| k-perf | JS | 15,676.30 | 5,349.90 | 4,894.85 | 3,360.95 | 2,919.00 | 2,518.00 | 2,263.85 | 1,838.20 | 1,811.15 | 1,783.60 | 1,849.00 |
| otel | JS | 10,573.85 | — | — | — | — | — | — | — | — | — | — |
| otel-proto | JS | 10,774.60 | — | — | — | — | — | — | — | — | — | — |
| otel-proto-timesource | JS | 12,428.25 | — | — | — | — | — | — | — | — | — | — |
| baseline | Native | 241.00 | 1.30 | 0.80 | 0.70 | 0.80 | 0.85 | 0.80 | 0.80 | 0.90 | 0.85 | 0.80 |
| k-perf | Native | 2,582.50 | 1,333.40 | 1,470.90 | 1,395.60 | 1,017.70 | 977.05 | 1,130.50 | 960.40 | 1,121.80 | 996.10 | 1,006.55 |
| otel | Native | 596.05 | 516.35 | 10,293.20 | 10,034.45 | 560.95 | 11,757.85 | 690.90 | 734.10 | 12,536.95 | 12,612.85 | 614.40 |
| otel-proto | Native | 592.85 | 540.00 | 10,873.75 | 11,814.05 | 744.85 | 10,722.05 | 505.95 | 516.90 | 11,103.15 | 10,464.65 | 507.45 |
| otel-proto-timesource | Native | 536.90 | 529.15 | 10,973.70 | 12,292.00 | 687.45 | 11,389.65 | 463.35 | 512.20 | 10,974.05 | 10,406.70 | 463.60 |

## Per-step times (JIT warmup curves)

> Curve shape: JVM C1≈step 1-2, C2≈step 55. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction.
