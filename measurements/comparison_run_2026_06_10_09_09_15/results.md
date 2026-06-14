# Benchmark Results (2026_06_10_09_09_15)

## Parameters
- **Warmup steps/run (discarded from stats):** 20
- **Run Iterations:** 10
- **Step Count (workload calls per process):** 100
- **Measured steps per run:** 80
- **Clean Build:** True
- **Run timeout (s):** 500

## System Information
- **OS:** Microsoft Windows 11 Pro 10.0.26200 64-bit
- **CPU:** Intel(R) Core(TM) i7-9750H CPU @ 2.60GHz (6 Cores / 12 Logical Processors)
- **RAM:** 31.74 GB
- **Java Version:** 21.0.10 ("21.0.10")
- **Node Version:** v24.15.0

## Hardware Overview Details
- **Device:** LENOVO - 20QN000DGE
- **Git Branch:** LB_otel_analysis

## Methods per step

Closed-form: `fib_call_count(fibDepth) + 2` with `fibDepth=20` (i.e. `2 * Fibonacci(fibDepth+1) - 1` recursive calls plus 1 for `bubbleSort` plus 1 for `workload` itself). The k-perf trace column is empirical (`lines / 2 / StepCount`) and serves as a sanity check.

| Platform | methods_per_step (formula, used) | methods_per_step (k-perf trace, check) |
|---|---:|---:|
| JVM | 21893 | 21893 |
| JS | 21893 | 21893 |
| Native | 21893 | 21893 |

## Execution Summary

`Mean step (µs)` = mean of per-step medians from step index 20 to 99 across 10 measured runs (first 20 step indices of each run discarded as warmup).

| Executable | Iterations | Total mean (ms) | Total median (ms) | Mean step (µs) | Step median (µs) | Step stddev (µs) |
|------------|-----------:|----------------:|------------------:|---------------:|-----------------:|-----------------:|
| baseline JVM | 10 | 27.83 | 27.81 | 35.80 | 31.05 | 12.85 |
| k-perf JVM | 10 | 341.22 | 341.77 | 2,730.00 | 2,794.80 | 356.29 |
| baseline JS (Node) | 10 | 42.46 | 38.16 | 247.24 | 247.35 | 140.85 |
| k-perf JS (Node) | 10 | 4,460.71 | 4,397.14 | 42,326.86 | 42,168.15 | 8,299.34 |
| baseline Native (Win) | 10 | 3.12 | 2.96 | 19.95 | 20.00 | 6.54 |
| k-perf Native (Win) | 10 | 2,225.24 | 2,247.19 | 21,925.55 | 21,918.95 | 2,271.79 |
| otel JVM | 10 | 12,387.89 | 12,329.02 | 108,228.74 | 105,460.15 | 25,686.18 |
| otel-proto JVM | 10 | 12,333.97 | 12,351.52 | 109,440.23 | 109,334.15 | 13,467.05 |
| otel-proto-timesource JVM | 10 | 12,958.59 | 12,965.05 | 118,577.81 | 119,443.05 | 14,887.81 |
| otel-proto-anchored JVM | 10 | 14,588.62 | 14,929.48 | 134,768.27 | 134,997.00 | 18,692.91 |
| otel JS (Node) | 10 | 32,876.57 | 31,411.21 | 316,174.09 | 310,944.75 | 61,548.56 |
| otel-proto JS (Node) | 10 | 36,327.65 | 35,889.89 | 355,403.01 | 353,084.35 | 32,256.63 |
| otel-proto-timesource JS (Node) | 10 | 38,525.10 | 38,481.61 | 381,215.69 | 379,316.90 | 23,505.48 |
| otel-proto-anchored JS (Node) | 10 | 35,066.53 | 35,042.03 | 347,296.92 | 344,923.50 | 25,574.59 |
| otel Native (Win) | 10 | 75,454.14 | 76,243.43 | 700,280.81 | 700,798.65 | 169,596.25 |
| otel-proto Native (Win) | 10 | 74,928.89 | 74,056.88 | 659,647.14 | 657,112.75 | 232,440.20 |
| otel-proto-timesource Native (Win) | 10 | 102,387.13 | 101,264.83 | 1,025,596.91 | 810,504.00 | 449,346.50 |
| otel-proto-anchored Native (Win) | 10 | 84,338.32 | 77,304.96 | 748,611.38 | 661,437.65 | 341,767.87 |

## Per-method timings

| Variant | Platform | Mean step (µs) | Methods/step | Per-method (ns) = step / methods | Overhead/method (ns) = Δ vs baseline |
|---|---|---:|---:|---:|---:|
| k-perf | JVM | 2,730.00 | 21893 | 124.7 | 123.1 |
| k-perf | JS | 42,326.86 | 21893 | 1,933.4 | 1,922.1 |
| k-perf | Native | 21,925.55 | 21893 | 1,001.5 | 1,000.6 |
| otel | JVM | 108,228.74 | 21893 | 4,943.5 | 4,941.9 |
| otel-proto | JVM | 109,440.23 | 21893 | 4,998.9 | 4,997.2 |
| otel-proto-timesource | JVM | 118,577.81 | 21893 | 5,416.2 | 5,414.6 |
| otel-proto-anchored | JVM | 134,768.27 | 21893 | 6,155.8 | 6,154.1 |
| otel | JS | 316,174.09 | 21893 | 14,441.8 | 14,430.5 |
| otel-proto | JS | 355,403.01 | 21893 | 16,233.6 | 16,222.3 |
| otel-proto-timesource | JS | 381,215.69 | 21893 | 17,412.7 | 17,401.4 |
| otel-proto-anchored | JS | 347,296.92 | 21893 | 15,863.4 | 15,852.1 |
| otel | Native | 700,280.81 | 21893 | 31,986.5 | 31,985.6 |
| otel-proto | Native | 659,647.14 | 21893 | 30,130.5 | 30,129.6 |
| otel-proto-timesource | Native | 1,025,596.91 | 21893 | 46,845.9 | 46,845.0 |
| otel-proto-anchored | Native | 748,611.38 | 21893 | 34,194.1 | 34,193.2 |

## Delivery verification

Expected = `methods/step × StepCount × RunCount` = 21893 × 100 × 10 = 21893000.
Compares (a) what the BSP/exporter actually sent vs (b) what Jaeger received vs (c) what Jaeger then dropped at its collector queue. Anything < 100 % is interesting.

| Variant | Platform | Expected | Exported (BSP→wire) | Jaeger received | Jaeger dropped (Δ) | Delivered (%) |
|---|---|---:|---:|---:|---:|---:|
| otel | JVM | 21,893,000 | 0 | 6,599,625 | 3,157,513 | 15.72 |
| otel-proto | JVM | 21,893,000 | 21,893,010 | 21,893,010 | 17,227,476 | 21.31 |
| otel-proto-timesource | JVM | 21,893,000 | 21,893,010 | 21,893,010 | 18,116,264 | 17.25 |
| otel-proto-anchored | JVM | 21,893,000 | 21,893,010 | 21,893,010 | 18,687,907 | 14.64 |
| otel | JS | 21,893,000 | 0 | 28,492,635 | 0 | 130.14 |
| otel-proto | JS | 21,893,000 | 19,703,709 | 21,893,010 | 0 | 100.00 |
| otel-proto-timesource | JS | 21,893,000 | 19,703,709 | 21,893,010 | 0 | 100.00 |
| otel-proto-anchored | JS | 21,893,000 | 19,703,709 | 21,893,010 | 0 | 100.00 |
| otel | Native | 21,893,000 | 0 | 50,385,645 | 0 | 230.15 |
| otel-proto | Native | 21,893,000 | 21,893,010 | 43,786,020 | 0 | 200.00 |
| otel-proto-timesource | Native | 21,893,000 | 21,893,010 | 43,786,020 | 0 | 200.00 |
| otel-proto-anchored | Native | 21,893,000 | 21,893,010 | 43,786,020 | 0 | 200.00 |
> Note: Jaeger metric labels share a service across all platforms, so the **Jaeger received** and **dropped** columns are reported per *variant*, not per (variant, platform). The Exported column IS per (variant, platform) — it comes from each binary's stdout.

## Per-step median curve (µs)

Sampled step indices across 10 runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s20 | s25 | s30 | s60 | s99 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,098.80 | 46.10 | 31.75 | 32.50 | 30.85 | 37.80 | 31.35 | 30.70 | 30.95 | 30.80 |
| k-perf | JVM | 39,089.55 | 7,664.70 | 5,736.70 | 3,471.95 | 2,462.85 | 2,760.15 | 2,719.60 | 2,217.35 | 2,802.25 | 2,924.55 |
| baseline | JS | 3,045.05 | 669.20 | 584.80 | 260.95 | 266.95 | 254.95 | 218.15 | 246.90 | 243.90 | 241.50 |
| k-perf | JS | 76,459.20 | 46,504.10 | 44,938.05 | 43,952.70 | 45,613.50 | 43,866.50 | 41,672.80 | 45,185.60 | 41,512.35 | 41,469.85 |
| baseline | Native | 307.60 | 20.65 | 20.25 | 20.15 | 21.25 | 20.00 | 19.95 | 19.95 | 20.05 | 19.90 |
| k-perf | Native | 22,904.00 | 22,217.30 | 22,800.05 | 22,157.60 | 21,073.15 | 23,490.00 | 22,066.55 | 21,278.95 | 21,821.45 | 21,817.85 |
| otel | JVM | 448,257.40 | 268,845.60 | 169,301.05 | 128,617.75 | 122,315.20 | 126,824.15 | 136,816.90 | 111,872.10 | 113,006.00 | 106,471.25 |
| otel-proto | JVM | 560,887.30 | 325,975.60 | 221,814.80 | 143,838.10 | 128,189.20 | 118,104.10 | 116,180.20 | 107,749.25 | 109,022.30 | 99,365.00 |
| otel-proto-timesource | JVM | 672,804.75 | 351,660.50 | 198,985.50 | 149,095.10 | 140,759.30 | 125,401.20 | 129,155.80 | 121,639.15 | 114,877.25 | 116,736.50 |
| otel-proto-anchored | JVM | 637,368.80 | 355,950.60 | 225,168.65 | 156,122.95 | 161,535.30 | 142,795.90 | 143,861.25 | 134,402.80 | 136,836.10 | 120,547.50 |
| otel | JS | 450,746.00 | 344,464.30 | 325,527.90 | 321,759.40 | 322,612.05 | 298,597.60 | 306,035.75 | 306,856.65 | 314,657.45 | 316,903.05 |
| otel-proto | JS | 510,948.85 | 380,052.10 | 357,980.65 | 352,461.70 | 351,095.90 | 343,356.70 | 350,484.40 | 350,466.30 | 352,486.70 | 350,029.80 |
| otel-proto-timesource | JS | 535,731.30 | 407,168.70 | 388,038.55 | 379,885.45 | 376,839.50 | 369,512.55 | 376,449.90 | 376,746.15 | 377,770.25 | 376,152.25 |
| otel-proto-anchored | JS | 501,192.70 | 371,797.25 | 351,959.55 | 346,565.70 | 345,060.90 | 337,785.25 | 344,666.50 | 343,460.35 | 346,124.05 | 344,243.05 |
| otel | Native | 654,470.40 | 664,138.40 | 677,815.15 | 664,366.80 | 678,763.45 | 662,219.55 | 695,150.55 | 692,603.20 | 703,245.90 | 699,908.15 |
| otel-proto | Native | 603,214.05 | 596,610.75 | 623,804.75 | 648,086.15 | 617,701.40 | 605,241.25 | 656,272.85 | 621,801.05 | 713,458.75 | 696,883.90 |
| otel-proto-timesource | Native | 589,529.90 | 609,557.05 | 586,603.20 | 1,104,916.20 | 1,197,843.70 | 1,179,817.85 | 747,470.75 | 629,524.05 | 670,634.85 | 669,025.75 |
| otel-proto-anchored | Native | 569,893.10 | 582,061.70 | 577,981.10 | 610,974.55 | 950,609.05 | 667,253.75 | 912,059.75 | 623,934.25 | 633,219.50 | 642,300.20 |
> Curve shape: JVM C1≈step 1-2, C2 hits later. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction. Step indices < 20 are discarded from the per-method statistics above.
