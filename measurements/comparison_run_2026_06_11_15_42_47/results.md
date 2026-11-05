# Benchmark Results (2026_06_11_15_42_47)

## Parameters
- **Warmup steps/run (discarded from stats):** 20
- **Run Iterations:** 3
- **Step Count (workload calls per process):** 50
- **Measured steps per run:** 30
- **Clean Build:** True
- **Run timeout (s):** 250
- **Variants:** baseline, k-perf, otel, otel-proto, otel-proto-sampler

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

`Mean step (µs)` = mean of per-step medians from step index 20 to 49 across 3 measured runs (first 20 step indices of each run discarded as warmup).

| Executable | Iterations | Total mean (ms) | Total median (ms) | Mean step (µs) | Step median (µs) | Step stddev (µs) |
|------------|-----------:|----------------:|------------------:|---------------:|-----------------:|-----------------:|
| baseline JVM | 3 | 25.27 | 25.93 | 33.59 | 31.60 | 18.89 |
| k-perf JVM | 3 | 193.95 | 200.09 | 2,471.64 | 2,371.40 | 331.74 |
| baseline JS (Node) | 3 | 23.56 | 24.07 | 256.31 | 240.00 | 65.93 |
| k-perf JS (Node) | 3 | 2,084.16 | 2,086.17 | 40,005.18 | 39,672.85 | 1,311.69 |
| baseline Native (Win) | 3 | 1.68 | 1.79 | 20.97 | 20.10 | 5.56 |
| k-perf Native (Win) | 3 | 981.48 | 1,005.18 | 19,194.07 | 19,184.00 | 693.74 |
| otel JVM | 3 | 5,190.37 | 5,814.03 | 104,296.66 | 99,877.55 | 36,332.55 |
| otel-proto JVM | 3 | 4,494.59 | 4,584.94 | 74,031.79 | 74,407.95 | 5,327.02 |
| otel-proto-sampler JVM | 3 | 4,191.27 | 4,323.60 | 68,533.02 | 68,105.50 | 7,342.09 |
| otel JS (Node) | 3 | 16,260.35 | 16,468.08 | 318,408.81 | 316,815.40 | 16,589.49 |
| otel-proto JS (Node) | 3 | 16,073.62 | 16,258.01 | 314,387.11 | 312,950.05 | 16,651.36 |
| otel-proto-sampler JS (Node) | 3 | 16,020.07 | 16,406.92 | 314,386.91 | 311,737.70 | 15,451.82 |
| otel Native (Win) | 3 | 30,517.32 | 31,851.61 | 604,416.54 | 605,535.60 | 34,481.71 |
| otel-proto Native (Win) | 3 | 25,365.27 | 26,043.07 | 512,382.99 | 513,600.05 | 27,511.45 |
| otel-proto-sampler Native (Win) | 3 | 24,863.66 | 25,645.18 | 502,452.30 | 506,181.80 | 26,119.05 |

## Per-method timings

| Variant | Platform | Mean step (µs) | Methods/step | Per-method (ns) = step / methods | Overhead/method (ns) = Δ vs baseline |
|---|---|---:|---:|---:|---:|
| k-perf | JVM | 2,471.64 | 21893 | 112.9 | 111.4 |
| k-perf | JS | 40,005.18 | 21893 | 1,827.3 | 1,815.6 |
| k-perf | Native | 19,194.07 | 21893 | 876.7 | 875.8 |
| otel | JVM | 104,296.66 | 21893 | 4,763.9 | 4,762.4 |
| otel-proto | JVM | 74,031.79 | 21893 | 3,381.5 | 3,380.0 |
| otel-proto-sampler | JVM | 68,533.02 | 21893 | 3,130.4 | 3,128.8 |
| otel | JS | 318,408.81 | 21893 | 14,543.9 | 14,532.2 |
| otel-proto | JS | 314,387.11 | 21893 | 14,360.2 | 14,348.5 |
| otel-proto-sampler | JS | 314,386.91 | 21893 | 14,360.2 | 14,348.4 |
| otel | Native | 604,416.54 | 21893 | 27,607.8 | 27,606.8 |
| otel-proto | Native | 512,382.99 | 21893 | 23,404.0 | 23,403.0 |
| otel-proto-sampler | Native | 502,452.30 | 21893 | 22,950.4 | 22,949.4 |

## Delivery verification

Expected = `methods/step × StepCount × RunCount` = 21893 × 50 × 3 = 3283950. Jaeger columns are per-variant deltas. **Delivered % = Stored (saved_ok) / Expected** (ground truth). Status: OK · LOSS · DUP · INVALID (Jaeger died — row untrustworthy) · OK (false-fail: N) (delivered, client threw on response read). ~ = client-side fallback when Jaeger metrics missing.

| Variant | Platform | Expected | Exported (attempted) | Failed (client) | Wire recv (Δ) | Stored saved_ok (Δ) | Dropped (Δ) | Delivered (%) | Dup | Status |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|:--|
| otel | JVM | 3,283,950 | 3,283,953 | 0 | 3,283,953 | 3,283,953 | 0 | 100.00 | — | OK |
| otel-proto | JVM | 3,283,950 | 3,283,953 | 0 | 3,283,953 | 3,283,953 | 0 | 100.00 | — | OK |
| otel-proto-sampler | JVM | 3,283,950 | 3,283,953 | 0 | 3,283,953 | 3,283,953 | 0 | 100.00 | — | OK |
| otel | JS | 3,283,950 | 3,283,953 | 33,280 | 3,283,953 | 3,283,953 | 0 | 100.00 | — | OK (false-fail: 33,280) |
| otel-proto | JS | 3,283,950 | 3,283,953 | 0 | 3,283,953 | 3,283,953 | 0 | 100.00 | — | OK |
| otel-proto-sampler | JS | 3,283,950 | 3,283,953 | 0 | 3,283,953 | 3,283,953 | 0 | 100.00 | — | OK |
| otel | Native | 3,283,950 | 3,283,953 | 22,016 | 3,283,953 | 3,283,953 | 0 | 100.00 | — | OK (false-fail: 22,016) |
| otel-proto | Native | 3,283,950 | 3,283,953 | 0 | 3,283,953 | 3,283,953 | 0 | 100.00 | — | OK |
| otel-proto-sampler | Native | 3,283,950 | 3,283,953 | 0 | 3,283,953 | 3,283,953 | 0 | 100.00 | — | OK |

First swallowed export error per row (if any):
- **otel JS** first export error: `IllegalStateException: Content-Length mismatch: expected 21 bytes, but received 0 bytes`
- **otel Native** first export error: `IllegalStateException: Unable to query response headers length: The operation completed successfully. Error 0 (0x80070000)`

## Per-step median curve (µs)

Sampled step indices across 3 runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s20 | s25 | s30 | s35 | s49 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 7,542.20 | 48.30 | 32.00 | 31.30 | 31.40 | 31.50 | 32.00 | 31.10 | 31.20 | 31.10 |
| k-perf | JVM | 40,911.00 | 8,105.90 | 5,621.70 | 3,233.90 | 2,457.00 | 2,726.40 | 2,787.90 | 2,222.50 | 2,221.90 | 2,737.30 |
| baseline | JS | 2,814.40 | 615.30 | 467.50 | 327.60 | 261.70 | 253.90 | 200.20 | 225.40 | 278.80 | 268.00 |
| k-perf | JS | 71,838.40 | 43,882.80 | 42,399.20 | 42,027.30 | 41,223.50 | 42,375.80 | 40,102.50 | 39,361.20 | 39,347.20 | 39,061.20 |
| baseline | Native | 244.10 | 20.10 | 19.70 | 19.50 | 20.10 | 20.00 | 20.00 | 24.20 | 19.90 | 23.90 |
| k-perf | Native | 21,665.40 | 20,784.70 | 20,446.80 | 19,969.20 | 18,892.40 | 19,380.00 | 18,913.40 | 18,703.30 | 19,662.50 | 18,550.10 |
| otel | JVM | 264,238.60 | 175,760.70 | 126,212.00 | 92,343.70 | 43,603.20 | 171,271.50 | 126,884.60 | 101,354.20 | 92,452.30 | 79,215.90 |
| otel-proto | JVM | 247,766.30 | 149,676.80 | 94,578.60 | 128,828.10 | 99,134.30 | 72,086.80 | 75,646.40 | 76,602.00 | 78,656.20 | 73,092.50 |
| otel-proto-sampler | JVM | 259,136.20 | 108,397.70 | 72,794.10 | 126,747.10 | 88,199.00 | 71,624.40 | 68,624.20 | 64,833.30 | 66,299.70 | 64,086.00 |
| otel | JS | 440,450.50 | 342,814.10 | 341,724.10 | 319,945.90 | 315,246.80 | 319,992.90 | 315,491.60 | 319,035.20 | 313,746.10 | 311,163.50 |
| otel-proto | JS | 447,230.20 | 339,837.20 | 318,008.10 | 315,544.00 | 316,179.20 | 307,145.20 | 312,183.40 | 317,658.90 | 312,580.50 | 308,438.10 |
| otel-proto-sampler | JS | 434,157.90 | 342,434.80 | 316,226.90 | 334,592.60 | 313,294.80 | 317,363.10 | 307,906.60 | 312,168.90 | 317,357.90 | 307,916.00 |
| otel | Native | 602,899.00 | 627,771.70 | 595,362.10 | 573,763.20 | 589,960.60 | 572,983.70 | 581,925.80 | 597,799.30 | 609,509.50 | 618,117.00 |
| otel-proto | Native | 475,069.80 | 473,607.50 | 504,268.10 | 470,960.90 | 473,748.70 | 480,969.40 | 493,810.70 | 507,114.40 | 509,300.70 | 512,785.50 |
| otel-proto-sampler | Native | 474,290.10 | 477,815.40 | 483,263.60 | 460,376.30 | 463,051.00 | 470,667.70 | 505,061.20 | 495,532.30 | 516,348.00 | 519,870.50 |
> Curve shape: JVM C1≈step 1-2, C2 hits later. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction. Step indices < 20 are discarded from the per-method statistics above.
