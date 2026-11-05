# Benchmark Results (2026_06_02_21_38_00)

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
| baseline JVM | 10 | 30.03 | 29.08 | 32.71 | 32.30 | 11.25 |
| k-perf JVM | 10 | 359.36 | 359.22 | 2,738.91 | 2,749.75 | 393.36 |
| otel JVM | 10 | 13,955.97 | 14,165.12 | 124,537.64 | 123,788.15 | 36,037.21 |
| otel-proto JVM | 10 | 1,330.97 | 1,343.26 | 10,075.53 | 9,869.25 | 1,965.64 |
| otel-proto-timesource JVM | 10 | 1,376.68 | 1,372.43 | 10,558.83 | 10,300.85 | 2,155.82 |
| otel-proto-anchored JVM | 10 | 1,269.36 | 1,278.18 | 9,682.60 | 9,483.70 | 1,415.82 |
| baseline JS (Node) | 10 | 45.72 | 44.26 | 288.69 | 284.30 | 97.03 |
| k-perf JS (Node) | 10 | 6,142.05 | 5,945.15 | 57,018.15 | 56,812.60 | 15,844.95 |
| otel JS (Node) | 10 | 34,432.23 | 32,072.10 | 316,225.26 | 313,757.90 | 74,400.00 |
| otel-proto JS (Node) | 10 | 7,092.52 | 6,960.76 | 67,225.94 | 67,135.80 | 9,187.76 |
| otel-proto-timesource JS (Node) | 10 | 9,261.38 | 9,235.59 | 90,615.17 | 90,504.10 | 5,772.99 |
| otel-proto-anchored JS (Node) | 10 | 6,275.94 | 6,274.40 | 61,176.34 | 61,134.60 | 3,107.04 |
| baseline Native (Win) | 10 | 2.89 | 2.83 | 19.95 | 19.90 | 4.57 |
| k-perf Native (Win) | 10 | 2,050.45 | 2,044.87 | 20,180.02 | 20,156.70 | 1,672.24 |
| otel Native (Win) | 10 | 57,544.80 | 59,727.46 | 555,281.21 | 573,924.95 | 147,970.30 |
| otel-proto Native (Win) | 10 | 7,070.58 | 7,069.32 | 69,824.16 | 69,837.15 | 3,861.63 |
| otel-proto-timesource Native (Win) | 10 | 6,341.02 | 6,343.30 | 62,572.26 | 62,526.90 | 5,147.08 |
| otel-proto-anchored Native (Win) | 10 | 7,031.35 | 7,021.88 | 69,500.23 | 69,416.80 | 3,759.19 |

## Per-method timings

| Variant | Platform | Mean step (µs) | Methods/step | Per-method (ns) = step / methods | Overhead/method (ns) = Δ vs baseline |
|---|---|---:|---:|---:|---:|
| k-perf | JVM | 2,738.91 | 21893 | 125.1 | 123.6 |
| otel | JVM | 124,537.64 | 21893 | 5,688.5 | 5,687.0 |
| otel-proto | JVM | 10,075.53 | 21893 | 460.2 | 458.7 |
| otel-proto-timesource | JVM | 10,558.83 | 21893 | 482.3 | 480.8 |
| otel-proto-anchored | JVM | 9,682.60 | 21893 | 442.3 | 440.8 |
| k-perf | JS | 57,018.15 | 21893 | 2,604.4 | 2,591.2 |
| otel | JS | 316,225.26 | 21893 | 14,444.1 | 14,430.9 |
| otel-proto | JS | 67,225.94 | 21893 | 3,070.7 | 3,057.5 |
| otel-proto-timesource | JS | 90,615.17 | 21893 | 4,139.0 | 4,125.8 |
| otel-proto-anchored | JS | 61,176.34 | 21893 | 2,794.3 | 2,781.1 |
| k-perf | Native | 20,180.02 | 21893 | 921.8 | 920.8 |
| otel | Native | 555,281.21 | 21893 | 25,363.4 | 25,362.5 |
| otel-proto | Native | 69,824.16 | 21893 | 3,189.3 | 3,188.4 |
| otel-proto-timesource | Native | 62,572.26 | 21893 | 2,858.1 | 2,857.2 |
| otel-proto-anchored | Native | 69,500.23 | 21893 | 3,174.5 | 3,173.6 |

## Per-step median curve (µs)

Sampled step indices across 10 runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s20 | s25 | s30 | s60 | s99 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,604.40 | 51.35 | 32.60 | 31.50 | 31.60 | 31.80 | 32.70 | 34.55 | 32.30 | 32.70 |
| k-perf | JVM | 44,160.05 | 8,501.40 | 6,811.80 | 3,785.35 | 2,744.30 | 3,004.05 | 2,998.55 | 2,355.00 | 2,775.80 | 2,801.30 |
| otel | JVM | 403,825.75 | 263,742.15 | 168,808.25 | 113,563.10 | 124,650.30 | 179,603.10 | 124,977.60 | 143,636.95 | 124,929.85 | 108,919.15 |
| otel-proto | JVM | 125,445.70 | 57,482.15 | 35,271.10 | 16,703.95 | 13,868.65 | 9,525.80 | 9,553.80 | 10,213.20 | 10,152.90 | 9,711.70 |
| otel-proto-timesource | JVM | 113,531.85 | 51,254.95 | 31,563.90 | 17,927.75 | 13,989.50 | 12,197.25 | 10,052.55 | 10,321.70 | 10,070.25 | 10,394.20 |
| otel-proto-anchored | JVM | 103,939.45 | 43,607.65 | 27,982.10 | 28,047.90 | 13,048.45 | 9,916.10 | 9,530.70 | 9,664.40 | 9,208.60 | 9,499.35 |
| baseline | JS | 3,201.25 | 693.15 | 580.65 | 428.80 | 321.05 | 296.55 | 230.25 | 285.10 | 440.25 | 270.35 |
| k-perf | JS | 108,051.60 | 62,022.85 | 63,861.80 | 56,772.35 | 53,806.15 | 52,311.25 | 51,494.10 | 52,202.70 | 66,797.25 | 58,494.85 |
| otel | JS | 469,763.10 | 337,735.85 | 317,277.20 | 321,699.80 | 324,477.05 | 308,932.70 | 312,358.20 | 315,385.75 | 311,714.35 | 308,366.40 |
| otel-proto | JS | 134,830.00 | 73,387.45 | 69,852.65 | 77,983.30 | 68,691.70 | 68,359.10 | 68,973.85 | 68,676.75 | 67,618.45 | 67,162.35 |
| otel-proto-timesource | JS | 156,298.45 | 94,631.70 | 92,655.90 | 92,544.45 | 91,350.95 | 90,885.85 | 90,898.90 | 89,937.40 | 89,499.00 | 90,743.30 |
| otel-proto-anchored | JS | 122,971.90 | 64,589.05 | 62,532.15 | 62,677.10 | 61,516.90 | 61,653.90 | 61,945.35 | 60,969.60 | 61,505.55 | 60,199.40 |
| baseline | Native | 255.20 | 20.60 | 20.10 | 20.00 | 20.10 | 19.95 | 20.00 | 19.95 | 20.00 | 19.95 |
| k-perf | Native | 21,728.15 | 21,222.40 | 21,175.60 | 20,492.25 | 19,754.10 | 20,559.30 | 20,009.45 | 19,995.45 | 19,714.05 | 19,994.95 |
| otel | Native | 540,041.10 | 543,757.05 | 541,831.30 | 563,755.70 | 565,957.60 | 537,839.30 | 592,135.80 | 566,972.70 | 626,635.95 | 603,208.05 |
| otel-proto | Native | 74,922.95 | 70,885.55 | 70,730.35 | 70,312.65 | 70,729.25 | 70,627.30 | 69,473.45 | 70,009.95 | 69,604.05 | 69,470.20 |
| otel-proto-timesource | Native | 66,599.75 | 63,639.75 | 63,404.65 | 62,721.85 | 62,156.60 | 62,358.30 | 63,239.40 | 62,667.45 | 61,663.25 | 62,713.85 |
| otel-proto-anchored | Native | 73,986.40 | 69,941.90 | 70,088.05 | 69,598.90 | 69,530.75 | 70,513.45 | 69,452.65 | 70,005.45 | 68,608.15 | 70,164.60 |
> Curve shape: JVM C1≈step 1-2, C2 hits later. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction. Step indices < 20 are discarded from the per-method statistics above.
