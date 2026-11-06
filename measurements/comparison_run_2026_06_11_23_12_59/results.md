# Benchmark Results (2026_06_11_23_12_59)

## Parameters
- **Warmup steps/run (discarded from stats):** 20
- **Run Iterations:** 10
- **Step Count (workload calls per process):** 100
- **Measured steps per run:** 80
- **Clean Build:** True
- **Run timeout (s):** 500
- **Variants:** baseline, k-perf, otel, otel-proto, otel-proto-sampler, otel-proto-timesource, otel-proto-anchored

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
| baseline JVM | 10 | 26.78 | 26.51 | 31.01 | 30.70 | 8.30 |
| k-perf JVM | 10 | 327.49 | 323.29 | 2,537.83 | 2,580.95 | 492.25 |
| baseline JS (Node) | 10 | 35.73 | 35.42 | 233.11 | 230.10 | 54.22 |
| k-perf JS (Node) | 10 | 4,100.86 | 4,041.32 | 39,296.60 | 39,212.05 | 3,950.58 |
| baseline Native (Win) | 10 | 2.75 | 2.71 | 19.43 | 19.40 | 4.40 |
| k-perf Native (Win) | 10 | 1,907.31 | 1,901.83 | 18,771.14 | 18,795.60 | 1,131.44 |
| otel JVM | 10 | 10,141.05 | 10,201.06 | 97,635.27 | 91,615.25 | 32,243.90 |
| otel-proto JVM | 10 | 7,864.67 | 7,919.97 | 69,606.18 | 69,612.90 | 5,759.84 |
| otel-proto-sampler JVM | 10 | 7,472.06 | 7,405.41 | 65,530.28 | 65,588.50 | 5,458.04 |
| otel-proto-timesource JVM | 10 | 7,898.98 | 7,895.98 | 70,366.44 | 70,198.10 | 6,242.82 |
| otel-proto-anchored JVM | 10 | 7,971.41 | 7,968.71 | 70,543.12 | 70,685.95 | 5,605.42 |
| otel JS (Node) | 10 | 33,804.42 | 33,065.87 | 327,625.61 | 319,298.60 | 66,526.89 |
| otel-proto JS (Node) | 10 | 32,886.60 | 32,877.57 | 323,720.99 | 315,604.70 | 66,156.04 |
| otel-proto-sampler JS (Node) | 10 | 32,320.12 | 32,405.43 | 318,992.42 | 311,021.95 | 57,942.78 |
| otel-proto-timesource JS (Node) | 10 | 35,798.54 | 35,643.90 | 351,186.31 | 342,529.05 | 66,529.34 |
| otel-proto-anchored JS (Node) | 10 | 32,188.06 | 32,067.44 | 317,115.44 | 308,658.85 | 64,198.94 |
| otel Native (Win) | 10 | 75,222.30 | 75,161.50 | 759,244.40 | 760,363.25 | 27,710.03 |
| otel-proto Native (Win) | 10 | 58,182.31 | 57,235.00 | 579,830.06 | 584,065.10 | 44,026.58 |
| otel-proto-sampler Native (Win) | 10 | 58,726.79 | 57,867.83 | 586,310.37 | 587,410.20 | 39,762.53 |
| otel-proto-timesource Native (Win) | 10 | 56,790.68 | 56,743.85 | 573,025.83 | 574,349.25 | 31,771.94 |
| otel-proto-anchored Native (Win) | 10 | 57,962.69 | 57,970.72 | 583,912.15 | 586,668.65 | 33,190.93 |

## Per-method timings

| Variant | Platform | Mean step (µs) | Methods/step | Per-method (ns) = step / methods | Overhead/method (ns) = Δ vs baseline |
|---|---|---:|---:|---:|---:|
| k-perf | JVM | 2,537.83 | 21893 | 115.9 | 114.5 |
| k-perf | JS | 39,296.60 | 21893 | 1,794.9 | 1,784.3 |
| k-perf | Native | 18,771.14 | 21893 | 857.4 | 856.5 |
| otel | JVM | 97,635.27 | 21893 | 4,459.7 | 4,458.2 |
| otel-proto | JVM | 69,606.18 | 21893 | 3,179.4 | 3,178.0 |
| otel-proto-sampler | JVM | 65,530.28 | 21893 | 2,993.2 | 2,991.8 |
| otel-proto-timesource | JVM | 70,366.44 | 21893 | 3,214.1 | 3,212.7 |
| otel-proto-anchored | JVM | 70,543.12 | 21893 | 3,222.2 | 3,220.8 |
| otel | JS | 327,625.61 | 21893 | 14,964.9 | 14,954.2 |
| otel-proto | JS | 323,720.99 | 21893 | 14,786.5 | 14,775.9 |
| otel-proto-sampler | JS | 318,992.42 | 21893 | 14,570.5 | 14,559.9 |
| otel-proto-timesource | JS | 351,186.31 | 21893 | 16,041.0 | 16,030.4 |
| otel-proto-anchored | JS | 317,115.44 | 21893 | 14,484.8 | 14,474.1 |
| otel | Native | 759,244.40 | 21893 | 34,679.8 | 34,678.9 |
| otel-proto | Native | 579,830.06 | 21893 | 26,484.7 | 26,483.8 |
| otel-proto-sampler | Native | 586,310.37 | 21893 | 26,780.7 | 26,779.8 |
| otel-proto-timesource | Native | 573,025.83 | 21893 | 26,173.9 | 26,173.0 |
| otel-proto-anchored | Native | 583,912.15 | 21893 | 26,671.2 | 26,670.3 |

## Delivery verification

Expected = `methods/step × StepCount × RunCount` = 21893 × 100 × 10 = 21893000. Jaeger columns are per-variant deltas. **Delivered % = Stored (saved_ok) / Expected** (ground truth). Status: OK · LOSS · DUP · INVALID (Jaeger died — row untrustworthy) · OK (false-fail: N) (delivered, client threw on response read). ~ = client-side fallback when Jaeger metrics missing.

| Variant | Platform | Expected | Exported (attempted) | Failed (client) | Wire recv (Δ) | Stored saved_ok (Δ) | Dropped (Δ) | Delivered (%) | Dup | Status |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|:--|
| otel | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-sampler | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-timesource | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-anchored | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel | JS | 21,893,000 | 17,514,408 | 132,096 | 18,108,328 | 18,108,328 | 0 | 82.71 | 1.03x | DUP |
| otel-proto | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-sampler | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-timesource | JS | 21,893,000 | 19,703,709 | 0 | 19,703,709 | 19,703,709 | 0 | 90.00 | — | LOSS |
| otel-proto-anchored | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel | Native | 21,893,000 | 21,893,010 | 185,344 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK (false-fail: 185,344) |
| otel-proto | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-sampler | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-timesource | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-anchored | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |

First swallowed export error per row (if any):
- **otel JS** first export error: `IllegalStateException: Content-Length mismatch: expected 21 bytes, but received 0 bytes`
- **otel Native** first export error: `IllegalStateException: Unable to query response headers length: The operation completed successfully. Error 0 (0x80070000)`

## Per-step median curve (µs)

Sampled step indices across 10 runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s20 | s25 | s30 | s60 | s99 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 6,881.55 | 46.85 | 31.45 | 30.80 | 30.75 | 30.90 | 32.60 | 30.90 | 31.30 | 31.20 |
| k-perf | JVM | 37,640.35 | 9,699.45 | 5,766.20 | 2,939.40 | 2,406.70 | 2,679.25 | 2,707.90 | 2,209.75 | 2,630.30 | 2,532.75 |
| baseline | JS | 2,986.40 | 600.80 | 419.80 | 244.25 | 265.40 | 240.80 | 202.20 | 231.65 | 223.20 | 225.45 |
| k-perf | JS | 70,818.05 | 43,351.35 | 42,200.80 | 41,763.45 | 41,296.50 | 41,092.50 | 38,824.35 | 39,076.40 | 38,596.40 | 39,508.00 |
| baseline | Native | 243.40 | 20.00 | 19.60 | 19.40 | 19.45 | 19.40 | 19.40 | 19.40 | 19.50 | 19.40 |
| k-perf | Native | 20,987.60 | 20,112.35 | 20,346.15 | 19,565.00 | 18,693.15 | 18,969.75 | 18,586.60 | 18,485.35 | 18,634.25 | 18,490.45 |
| otel | JVM | 253,225.60 | 195,112.40 | 158,253.45 | 60,275.65 | 55,686.65 | 246,691.10 | 124,943.50 | 122,489.35 | 87,835.65 | 83,354.85 |
| otel-proto | JVM | 247,140.25 | 127,374.15 | 85,522.10 | 126,828.80 | 98,584.30 | 68,490.90 | 70,872.25 | 72,949.70 | 69,683.75 | 64,380.65 |
| otel-proto-sampler | JVM | 249,790.60 | 141,070.70 | 84,775.90 | 140,341.25 | 91,653.35 | 68,884.20 | 71,698.40 | 66,106.50 | 66,796.65 | 60,018.50 |
| otel-proto-timesource | JVM | 247,165.00 | 140,415.05 | 90,547.65 | 119,114.00 | 99,228.90 | 67,837.65 | 75,760.60 | 73,729.35 | 73,274.35 | 64,935.10 |
| otel-proto-anchored | JVM | 242,513.80 | 143,395.30 | 97,169.95 | 118,805.55 | 98,690.50 | 70,064.30 | 72,968.40 | 71,500.95 | 70,248.60 | 63,488.05 |
| otel | JS | 458,774.00 | 354,757.30 | 325,653.70 | 327,957.40 | 317,783.60 | 315,750.15 | 313,170.40 | 319,924.50 | 319,319.95 | 310,242.10 |
| otel-proto | JS | 449,728.55 | 343,070.05 | 317,117.40 | 313,813.30 | 316,012.05 | 309,482.65 | 326,042.25 | 317,139.00 | 315,492.55 | 313,756.20 |
| otel-proto-sampler | JS | 446,905.80 | 341,606.75 | 310,231.70 | 310,186.70 | 311,862.75 | 306,286.30 | 315,598.85 | 310,143.10 | 313,341.75 | 306,532.95 |
| otel-proto-timesource | JS | 478,702.55 | 366,470.10 | 341,890.85 | 339,528.30 | 360,691.85 | 335,013.80 | 339,754.90 | 344,656.00 | 341,155.90 | 340,932.10 |
| otel-proto-anchored | JS | 429,754.80 | 329,952.60 | 309,370.45 | 307,478.15 | 306,714.40 | 304,897.25 | 310,265.20 | 305,621.85 | 310,658.50 | 305,436.30 |
| otel | Native | 559,524.65 | 570,495.35 | 563,600.20 | 722,112.95 | 739,205.45 | 733,477.85 | 751,941.50 | 756,495.00 | 756,419.70 | 760,510.95 |
| otel-proto | Native | 457,363.05 | 458,269.80 | 460,504.85 | 465,268.25 | 553,906.60 | 538,560.35 | 553,226.95 | 573,660.20 | 585,221.85 | 579,896.90 |
| otel-proto-sampler | Native | 462,100.50 | 457,486.80 | 459,326.55 | 461,970.15 | 538,437.65 | 554,537.90 | 568,418.50 | 570,479.65 | 590,905.45 | 591,247.55 |
| otel-proto-timesource | Native | 454,632.55 | 458,390.65 | 457,975.10 | 464,090.45 | 525,836.85 | 537,619.75 | 556,891.95 | 559,834.95 | 578,446.10 | 578,521.75 |
| otel-proto-anchored | Native | 466,580.55 | 463,618.35 | 464,602.70 | 466,555.95 | 535,272.80 | 538,739.45 | 553,085.60 | 573,365.20 | 593,011.10 | 590,263.15 |
> Curve shape: JVM C1≈step 1-2, C2 hits later. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction. Step indices < 20 are discarded from the per-method statistics above.
