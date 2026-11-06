# Benchmark Results (2026_06_04_22_24_35)

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
| baseline JVM | 10 | 29.54 | 28.69 | 34.21 | 32.30 | 13.71 |
| k-perf JVM | 10 | 356.39 | 353.07 | 2,818.66 | 2,889.35 | 388.51 |
| baseline JS (Node) | 10 | 41.71 | 41.28 | 262.49 | 258.45 | 92.20 |
| k-perf JS (Node) | 10 | 4,143.11 | 4,080.04 | 39,767.72 | 39,736.35 | 2,214.89 |
| baseline Native (Win) | 10 | 3.05 | 2.92 | 19.94 | 19.90 | 7.53 |
| k-perf Native (Win) | 10 | 1,961.09 | 1,951.04 | 19,385.59 | 19,366.50 | 1,011.06 |
| otel JVM | 10 | 12,054.22 | 12,206.55 | 102,558.92 | 99,709.65 | 23,048.33 |
| otel-proto JVM | 10 | 10,832.20 | 10,783.05 | 95,138.58 | 95,415.00 | 13,960.99 |
| otel-proto-timesource JVM | 10 | 11,757.34 | 11,697.31 | 108,746.20 | 109,960.80 | 16,308.80 |
| otel-proto-anchored JVM | 10 | 11,637.91 | 11,649.42 | 103,780.81 | 103,254.45 | 15,524.55 |
| otel JS (Node) | 10 | 31,059.47 | 31,011.39 | 302,153.01 | 301,458.90 | 46,159.56 |
| otel-proto JS (Node) | 10 | 35,705.56 | 35,832.79 | 353,462.64 | 350,769.85 | 24,413.25 |
| otel-proto-timesource JS (Node) | 10 | 38,506.42 | 38,537.07 | 379,938.90 | 378,193.25 | 24,220.05 |
| otel-proto-anchored JS (Node) | 10 | 35,267.78 | 35,186.86 | 347,253.54 | 346,083.20 | 24,338.10 |
| otel Native (Win) | 10 | 73,424.92 | 73,833.67 | 683,197.91 | 684,895.20 | 161,296.28 |
| otel-proto Native (Win) | 10 | 71,284.25 | 71,633.03 | 631,663.78 | 631,968.25 | 212,509.51 |
| otel-proto-timesource Native (Win) | 10 | 97,150.18 | 97,769.71 | 992,925.15 | 773,217.95 | 431,043.92 |
| otel-proto-anchored Native (Win) | 10 | 80,980.49 | 78,082.82 | 698,416.06 | 642,168.20 | 318,719.80 |

## Per-method timings

| Variant | Platform | Mean step (µs) | Methods/step | Per-method (ns) = step / methods | Overhead/method (ns) = Δ vs baseline |
|---|---|---:|---:|---:|---:|
| k-perf | JVM | 2,818.66 | 21893 | 128.7 | 127.2 |
| k-perf | JS | 39,767.72 | 21893 | 1,816.5 | 1,804.5 |
| k-perf | Native | 19,385.59 | 21893 | 885.5 | 884.6 |
| otel | JVM | 102,558.92 | 21893 | 4,684.6 | 4,683.0 |
| otel-proto | JVM | 95,138.58 | 21893 | 4,345.6 | 4,344.1 |
| otel-proto-timesource | JVM | 108,746.20 | 21893 | 4,967.2 | 4,965.6 |
| otel-proto-anchored | JVM | 103,780.81 | 21893 | 4,740.4 | 4,738.8 |
| otel | JS | 302,153.01 | 21893 | 13,801.4 | 13,789.4 |
| otel-proto | JS | 353,462.64 | 21893 | 16,145.0 | 16,133.0 |
| otel-proto-timesource | JS | 379,938.90 | 21893 | 17,354.4 | 17,342.4 |
| otel-proto-anchored | JS | 347,253.54 | 21893 | 15,861.4 | 15,849.4 |
| otel | Native | 683,197.91 | 21893 | 31,206.2 | 31,205.3 |
| otel-proto | Native | 631,663.78 | 21893 | 28,852.3 | 28,851.4 |
| otel-proto-timesource | Native | 992,925.15 | 21893 | 45,353.5 | 45,352.6 |
| otel-proto-anchored | Native | 698,416.06 | 21893 | 31,901.3 | 31,900.4 |

## Delivery verification

Expected = `methods/step × StepCount × RunCount` = 21893 × 100 × 10 = 21893000.
Compares (a) what the BSP/exporter actually sent vs (b) what Jaeger received vs (c) what Jaeger then dropped at its collector queue. Anything < 100 % is interesting.

| Variant | Platform | Expected | Exported (BSP→wire) | Jaeger received | Jaeger dropped (Δ) | Delivered (%) |
|---|---|---:|---:|---:|---:|---:|
| otel | JVM | 21,893,000 | 0 | 9,360,296 | 5,352,692 | 18.31 |
| otel-proto | JVM | 21,893,000 | 21,893,010 | 21,893,010 | 17,045,113 | 22.14 |
| otel-proto-timesource | JVM | 21,893,000 | 21,893,010 | 21,893,010 | 17,841,592 | 18.51 |
| otel-proto-anchored | JVM | 21,893,000 | 21,893,010 | 21,893,010 | 17,726,181 | 19.03 |
| otel | JS | 21,893,000 | 0 | 31,253,306 | 0 | 142.75 |
| otel-proto | JS | 21,893,000 | 19,703,709 | 21,893,010 | 0 | 100.00 |
| otel-proto-timesource | JS | 21,893,000 | 21,893,010 | 21,893,010 | 0 | 100.00 |
| otel-proto-anchored | JS | 21,893,000 | 21,893,010 | 21,893,010 | 0 | 100.00 |
| otel | Native | 21,893,000 | 0 | 53,146,316 | 0 | 242.75 |
| otel-proto | Native | 21,893,000 | 21,893,010 | 43,786,020 | 0 | 200.00 |
| otel-proto-timesource | Native | 21,893,000 | 21,893,010 | 43,786,020 | 0 | 200.00 |
| otel-proto-anchored | Native | 21,893,000 | 21,893,010 | 43,786,020 | 0 | 200.00 |
> Note: Jaeger metric labels share a service across all platforms, so the **Jaeger received** and **dropped** columns are reported per *variant*, not per (variant, platform). The Exported column IS per (variant, platform) — it comes from each binary's stdout.

## Per-step median curve (µs)

Sampled step indices across 10 runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s20 | s25 | s30 | s60 | s99 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,700.40 | 49.25 | 34.70 | 32.10 | 32.60 | 37.75 | 33.65 | 34.15 | 32.40 | 38.70 |
| k-perf | JVM | 40,277.20 | 8,535.15 | 6,922.50 | 3,432.65 | 2,609.60 | 2,867.35 | 2,957.15 | 2,254.90 | 3,053.50 | 3,101.10 |
| baseline | JS | 3,065.00 | 695.05 | 521.65 | 318.45 | 296.90 | 268.70 | 209.10 | 253.15 | 248.75 | 248.00 |
| k-perf | JS | 70,782.80 | 43,940.65 | 43,120.45 | 42,506.05 | 41,761.35 | 41,624.00 | 39,526.55 | 39,495.30 | 39,317.70 | 39,357.95 |
| baseline | Native | 258.95 | 20.60 | 20.10 | 19.95 | 20.00 | 19.90 | 19.95 | 19.95 | 20.00 | 19.95 |
| k-perf | Native | 21,227.80 | 20,516.75 | 20,453.50 | 19,802.15 | 19,227.45 | 20,153.50 | 19,176.00 | 19,052.25 | 19,238.20 | 19,086.30 |
| otel | JVM | 394,813.35 | 269,849.55 | 167,193.75 | 109,761.50 | 110,018.95 | 141,306.60 | 118,206.65 | 142,969.55 | 94,109.25 | 93,376.70 |
| otel-proto | JVM | 484,127.55 | 309,307.25 | 194,305.15 | 118,895.30 | 110,918.35 | 100,137.20 | 98,341.00 | 92,959.75 | 94,831.30 | 100,688.35 |
| otel-proto-timesource | JVM | 521,888.45 | 331,833.50 | 205,271.90 | 127,650.50 | 116,338.80 | 119,762.90 | 108,189.80 | 110,190.60 | 113,319.75 | 95,916.00 |
| otel-proto-anchored | JVM | 596,292.20 | 305,711.15 | 215,333.30 | 134,088.75 | 122,091.00 | 111,886.30 | 106,929.90 | 119,356.00 | 109,194.15 | 94,006.85 |
| otel | JS | 427,317.65 | 329,748.40 | 309,965.40 | 306,066.95 | 301,775.55 | 297,569.05 | 301,752.90 | 300,607.65 | 301,682.65 | 298,996.45 |
| otel-proto | JS | 509,507.60 | 378,909.30 | 357,920.35 | 350,328.40 | 350,381.60 | 343,576.20 | 347,392.95 | 349,551.65 | 355,184.40 | 348,498.95 |
| otel-proto-timesource | JS | 545,581.85 | 412,435.35 | 386,275.45 | 378,583.10 | 377,462.85 | 369,592.25 | 375,856.10 | 377,546.35 | 379,034.30 | 376,629.20 |
| otel-proto-anchored | JS | 503,494.85 | 380,948.65 | 351,923.95 | 347,375.00 | 346,416.40 | 338,543.55 | 346,034.60 | 345,794.75 | 344,673.05 | 347,444.80 |
| otel | Native | 636,717.30 | 642,175.80 | 666,987.10 | 657,676.95 | 671,344.90 | 646,208.30 | 662,222.70 | 659,161.25 | 681,918.25 | 690,653.15 |
| otel-proto | Native | 557,770.65 | 599,911.35 | 606,278.65 | 604,850.55 | 584,844.40 | 585,283.20 | 611,810.25 | 600,845.10 | 642,090.90 | 651,776.05 |
| otel-proto-timesource | Native | 562,953.00 | 569,863.45 | 557,852.25 | 591,148.05 | 1,262,091.65 | 692,923.90 | 1,297,104.25 | 615,928.65 | 626,516.50 | 672,019.10 |
| otel-proto-anchored | Native | 561,245.20 | 576,925.20 | 553,472.90 | 598,627.50 | 1,029,759.40 | 609,403.85 | 796,265.95 | 627,044.25 | 632,812.70 | 627,466.95 |
> Curve shape: JVM C1≈step 1-2, C2 hits later. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction. Step indices < 20 are discarded from the per-method statistics above.
