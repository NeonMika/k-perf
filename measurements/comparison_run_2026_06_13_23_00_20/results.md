# Benchmark Results (2026_06_13_23_00_20)

## Parameters
- **Warmup steps/run (discarded from stats):** 20
- **Run Iterations:** 10
- **Step Count (workload calls per process):** 100
- **Measured steps per run:** 80
- **Clean Build:** True
- **Run timeout (s):** 500
- **Variants:** baseline, k-perf, otel, otel-proto, otel-proto-sampler, otel-proto-timesource, otel-proto-anchored, otel-proto-fastbatch

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
| baseline JVM | 10 | 28.84 | 28.02 | 33.81 | 31.50 | 23.21 |
| k-perf JVM | 10 | 326.03 | 326.27 | 2,552.53 | 2,610.70 | 415.09 |
| baseline JS (Node) | 10 | 36.87 | 36.49 | 239.81 | 239.25 | 63.47 |
| k-perf JS (Node) | 10 | 4,081.44 | 4,079.69 | 39,736.57 | 39,642.70 | 1,570.39 |
| baseline Native (Win) | 10 | 2.85 | 2.71 | 19.91 | 19.90 | 5.68 |
| k-perf Native (Win) | 10 | 1,931.67 | 1,932.52 | 19,156.67 | 19,144.45 | 629.01 |
| otel JVM | 10 | 9,861.87 | 9,998.07 | 94,661.30 | 90,030.65 | 28,778.37 |
| otel-proto JVM | 10 | 7,504.66 | 7,504.23 | 66,115.79 | 66,222.70 | 5,654.59 |
| otel-proto-sampler JVM | 10 | 7,101.21 | 7,052.52 | 62,058.63 | 62,118.60 | 6,120.29 |
| otel-proto-timesource JVM | 10 | 7,578.46 | 7,536.59 | 67,054.13 | 66,965.90 | 5,947.32 |
| otel-proto-anchored JVM | 10 | 7,569.33 | 7,506.99 | 66,771.11 | 67,157.45 | 6,188.70 |
| otel-proto-fastbatch JVM | 10 | 5,135.12 | 4,977.80 | 35,496.18 | 34,946.10 | 42,459.98 |
| otel JS (Node) | 10 | 32,679.06 | 32,662.30 | 323,040.67 | 317,320.70 | 50,067.63 |
| otel-proto JS (Node) | 10 | 32,178.20 | 32,251.52 | 319,607.65 | 313,185.95 | 52,191.27 |
| otel-proto-sampler JS (Node) | 10 | 31,749.99 | 31,936.45 | 315,998.74 | 309,758.65 | 45,505.27 |
| otel-proto-timesource JS (Node) | 10 | 35,015.84 | 35,004.47 | 347,040.86 | 341,224.55 | 50,521.81 |
| otel-proto-anchored JS (Node) | 10 | 32,016.62 | 32,074.82 | 317,367.87 | 311,211.35 | 47,526.39 |
| otel-proto-fastbatch JS (Node) | 10 | 19,992.09 | 19,906.23 | 196,361.27 | 195,361.10 | 20,643.71 |
| otel Native (Win) | 10 | 61,216.15 | 60,969.04 | 609,485.99 | 612,589.85 | 34,325.21 |
| otel-proto Native (Win) | 10 | 52,039.49 | 51,910.60 | 524,215.81 | 527,689.30 | 25,036.37 |
| otel-proto-sampler Native (Win) | 10 | 51,824.31 | 51,715.15 | 521,471.30 | 525,216.05 | 29,834.55 |
| otel-proto-timesource Native (Win) | 10 | 51,696.62 | 51,293.89 | 520,042.40 | 522,639.60 | 26,716.85 |
| otel-proto-anchored Native (Win) | 10 | 52,441.70 | 52,290.61 | 527,981.78 | 531,442.55 | 28,663.69 |
| otel-proto-fastbatch Native (Win) | 10 | 17,338.31 | 16,994.48 | 171,293.07 | 173,920.90 | 22,150.97 |

## Per-method timings

| Variant | Platform | Mean step (µs) | Methods/step | Per-method (ns) = step / methods | Overhead/method (ns) = Δ vs baseline |
|---|---|---:|---:|---:|---:|
| k-perf | JVM | 2,552.53 | 21893 | 116.6 | 115.0 |
| k-perf | JS | 39,736.57 | 21893 | 1,815.0 | 1,804.1 |
| k-perf | Native | 19,156.67 | 21893 | 875.0 | 874.1 |
| otel | JVM | 94,661.30 | 21893 | 4,323.8 | 4,322.3 |
| otel-proto | JVM | 66,115.79 | 21893 | 3,020.0 | 3,018.4 |
| otel-proto-sampler | JVM | 62,058.63 | 21893 | 2,834.6 | 2,833.1 |
| otel-proto-timesource | JVM | 67,054.13 | 21893 | 3,062.8 | 3,061.3 |
| otel-proto-anchored | JVM | 66,771.11 | 21893 | 3,049.9 | 3,048.3 |
| otel-proto-fastbatch | JVM | 35,496.18 | 21893 | 1,621.3 | 1,619.8 |
| otel | JS | 323,040.67 | 21893 | 14,755.4 | 14,744.5 |
| otel-proto | JS | 319,607.65 | 21893 | 14,598.6 | 14,587.7 |
| otel-proto-sampler | JS | 315,998.74 | 21893 | 14,433.8 | 14,422.8 |
| otel-proto-timesource | JS | 347,040.86 | 21893 | 15,851.7 | 15,840.7 |
| otel-proto-anchored | JS | 317,367.87 | 21893 | 14,496.3 | 14,485.4 |
| otel-proto-fastbatch | JS | 196,361.27 | 21893 | 8,969.1 | 8,958.2 |
| otel | Native | 609,485.99 | 21893 | 27,839.3 | 27,838.4 |
| otel-proto | Native | 524,215.81 | 21893 | 23,944.4 | 23,943.5 |
| otel-proto-sampler | Native | 521,471.30 | 21893 | 23,819.1 | 23,818.2 |
| otel-proto-timesource | Native | 520,042.40 | 21893 | 23,753.8 | 23,752.9 |
| otel-proto-anchored | Native | 527,981.78 | 21893 | 24,116.5 | 24,115.6 |
| otel-proto-fastbatch | Native | 171,293.07 | 21893 | 7,824.1 | 7,823.2 |

## Delivery verification

Expected = `methods/step × StepCount × RunCount` = 21893 × 100 × 10 = 21893000. Jaeger columns are per-variant deltas. **Delivered % = Stored (saved_ok) / Expected** (ground truth). Status: OK · LOSS · DUP · INVALID (Jaeger died — row untrustworthy) · OK (false-fail: N) (delivered, client threw on response read). ~ = client-side fallback when Jaeger metrics missing.

| Variant | Platform | Expected | Exported (attempted) | Failed (client) | Wire recv (Δ) | Stored saved_ok (Δ) | Dropped (Δ) | Delivered (%) | Dup | Status |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|:--|
| otel | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-sampler | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-timesource | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-anchored | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-fastbatch | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel | JS | 21,893,000 | 21,893,010 | 125,952 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK (false-fail: 125,952) |
| otel-proto | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-sampler | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-timesource | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-anchored | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-fastbatch | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel | Native | 21,893,000 | 21,893,010 | 197,120 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK (false-fail: 197,120) |
| otel-proto | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-sampler | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-timesource | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-anchored | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-fastbatch | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |

First swallowed export error per row (if any):
- **otel JS** first export error: `IllegalStateException: Content-Length mismatch: expected 21 bytes, but received 0 bytes`
- **otel Native** first export error: `IllegalStateException: Unable to query response headers length: The operation completed successfully. Error 0 (0x80070000)`

## Per-step median curve (µs)

Sampled step indices across 10 runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s20 | s25 | s30 | s60 | s99 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,329.50 | 48.00 | 31.95 | 32.50 | 31.85 | 31.35 | 32.50 | 47.30 | 32.00 | 31.55 |
| k-perf | JVM | 40,906.65 | 8,782.10 | 5,901.45 | 2,950.00 | 2,422.25 | 2,678.95 | 2,692.05 | 2,221.00 | 2,610.25 | 2,694.40 |
| baseline | JS | 2,883.65 | 617.35 | 418.10 | 290.05 | 276.75 | 256.35 | 218.05 | 235.10 | 234.55 | 229.60 |
| k-perf | JS | 71,424.20 | 43,670.60 | 43,057.20 | 41,844.00 | 41,987.15 | 42,023.80 | 39,996.50 | 39,944.10 | 39,631.55 | 39,282.95 |
| baseline | Native | 243.95 | 20.50 | 20.10 | 20.00 | 20.00 | 20.05 | 19.95 | 19.90 | 19.95 | 19.90 |
| k-perf | Native | 21,100.85 | 20,614.40 | 20,362.80 | 19,933.35 | 19,132.60 | 19,596.50 | 19,299.35 | 19,231.15 | 18,993.60 | 18,937.00 |
| otel | JVM | 254,565.35 | 191,564.30 | 140,603.70 | 65,586.15 | 71,557.50 | 150,725.00 | 105,200.85 | 103,363.00 | 84,922.90 | 77,172.30 |
| otel-proto | JVM | 271,921.30 | 113,276.50 | 88,993.05 | 117,996.95 | 90,967.90 | 68,267.65 | 69,331.80 | 69,213.35 | 67,710.85 | 63,491.35 |
| otel-proto-sampler | JVM | 243,558.25 | 141,278.45 | 85,137.05 | 132,411.50 | 91,757.30 | 66,093.70 | 66,454.30 | 63,043.35 | 61,331.90 | 57,429.75 |
| otel-proto-timesource | JVM | 250,331.75 | 142,002.75 | 84,941.55 | 119,421.70 | 93,463.20 | 69,397.55 | 70,782.50 | 64,456.85 | 70,280.90 | 65,992.05 |
| otel-proto-anchored | JVM | 242,146.40 | 140,718.15 | 87,436.40 | 122,955.45 | 91,366.95 | 70,019.55 | 71,259.50 | 67,307.00 | 65,675.95 | 59,964.55 |
| otel-proto-fastbatch | JVM | 274,308.75 | 174,212.75 | 101,539.45 | 88,401.00 | 66,325.90 | 55,614.50 | 35,402.85 | 37,426.70 | 30,868.25 | 35,195.80 |
| otel | JS | 470,021.90 | 351,628.60 | 328,062.15 | 318,471.80 | 321,997.20 | 319,129.05 | 317,092.80 | 317,661.45 | 317,649.35 | 311,145.65 |
| otel-proto | JS | 458,081.75 | 346,370.80 | 321,102.85 | 318,894.90 | 316,091.40 | 307,251.70 | 314,729.10 | 315,373.90 | 311,980.50 | 310,728.65 |
| otel-proto-sampler | JS | 448,918.10 | 344,046.35 | 314,472.25 | 313,489.15 | 314,103.20 | 306,014.15 | 310,407.50 | 311,368.40 | 311,768.80 | 311,593.00 |
| otel-proto-timesource | JS | 481,934.65 | 376,256.60 | 349,265.25 | 343,959.20 | 342,490.05 | 336,112.65 | 340,591.85 | 339,937.85 | 339,876.05 | 336,759.25 |
| otel-proto-anchored | JS | 447,542.05 | 338,632.80 | 319,167.15 | 314,277.40 | 311,027.05 | 304,626.70 | 310,830.00 | 313,029.30 | 311,628.50 | 308,629.75 |
| otel-proto-fastbatch | JS | 310,583.30 | 220,156.20 | 203,041.90 | 198,423.30 | 197,294.55 | 194,352.90 | 218,856.20 | 194,156.35 | 198,757.95 | 195,728.60 |
| otel | Native | 570,624.50 | 569,374.00 | 575,523.10 | 582,673.45 | 585,337.65 | 573,117.65 | 594,917.65 | 590,632.45 | 615,333.05 | 623,229.05 |
| otel-proto | Native | 464,320.40 | 470,299.35 | 472,240.50 | 471,154.30 | 478,309.80 | 487,075.30 | 506,784.95 | 509,072.60 | 529,195.90 | 533,414.40 |
| otel-proto-sampler | Native | 460,012.40 | 469,780.95 | 465,662.50 | 468,444.90 | 476,206.60 | 483,985.00 | 500,873.80 | 504,609.45 | 528,253.80 | 527,830.75 |
| otel-proto-timesource | Native | 467,283.25 | 463,425.80 | 467,427.95 | 469,639.75 | 473,469.75 | 479,769.25 | 498,703.55 | 505,661.85 | 525,536.45 | 518,637.35 |
| otel-proto-anchored | Native | 468,021.85 | 475,623.20 | 472,184.60 | 470,049.85 | 482,574.30 | 495,171.30 | 504,737.00 | 512,218.80 | 533,681.20 | 533,433.00 |
| otel-proto-fastbatch | Native | 151,293.55 | 145,359.95 | 144,479.95 | 146,898.05 | 149,848.70 | 160,891.00 | 155,161.80 | 157,748.25 | 172,358.60 | 184,716.65 |
> Curve shape: JVM C1≈step 1-2, C2 hits later. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction. Step indices < 20 are discarded from the per-method statistics above.
