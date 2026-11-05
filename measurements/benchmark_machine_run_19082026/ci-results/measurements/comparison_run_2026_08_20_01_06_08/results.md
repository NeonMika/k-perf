# Benchmark Results (2026_08_20_01_06_08)

## Parameters
- **Warmup steps/run (discarded from stats):** 20
- **Run Iterations:** 10
- **Step Count (workload calls per process):** 100
- **Measured steps per run:** 80
- **Clean Build:** True
- **Run timeout (s):** 500
- **Variants:** baseline, otel, otel-proto, otel-proto-sampler, otel-proto-timesource, otel-proto-anchored, otel-proto-fastbatch, otel-proto-combined

## System Information
- **OS:** Debian GNU/Linux 13 (trixie) x86_64
- **CPU:** AMD Ryzen 9 9950X 16-Core Processor (16 Cores / 32 Logical Processors)
- **RAM:** 60.44 GB
- **Java Version:** 21.0.5 ("21.0.5")
- **Node Version:** v24.18.0

## Hardware Overview Details
- **Device:** Gigabyte Technology Co., Ltd. - B850 AORUS ELITE WIFI7
- **Git Branch:** HEAD

## Methods per step

Closed-form: `fib_call_count(fibDepth) + 2` with `fibDepth=20` (i.e. `2 * Fibonacci(fibDepth+1) - 1` recursive calls plus 1 for `bubbleSort` plus 1 for `workload` itself).

| Platform | methods_per_step (formula) |
|---|---:|
| JVM | 21893 |
| JS | 21893 |
| Native | 21893 |

## Execution Summary

`Mean step (µs)` = mean of per-step medians from step index 20 to 99 across 10 measured runs (first 20 step indices of each run discarded as warmup).

| Executable | Iterations | Total mean (ms) | Total median (ms) | Mean step (µs) | Step median (µs) | Step stddev (µs) |
|------------|-----------:|----------------:|------------------:|---------------:|-----------------:|-----------------:|
| baseline JVM | 10 | 11.06 | 11.05 | 14.35 | 14.27 | 1.37 |
| baseline JS (Node) | 10 | 15.15 | 14.76 | 102.11 | 104.77 | 14.42 |
| baseline Native (Linux) | 10 | 1.40 | 1.31 | 10.77 | 10.77 | 3.37 |
| otel JVM | 10 | 2,966.74 | 2,949.52 | 23,138.26 | 21,330.84 | 14,497.97 |
| otel-proto JVM | 10 | 1,631.58 | 1,624.52 | 13,927.13 | 13,838.43 | 2,887.92 |
| otel-proto-sampler JVM | 10 | 1,542.56 | 1,532.48 | 13,089.13 | 13,084.82 | 2,892.04 |
| otel-proto-timesource JVM | 10 | 1,625.11 | 1,623.44 | 13,579.67 | 13,444.14 | 3,785.64 |
| otel-proto-anchored JVM | 10 | 1,618.99 | 1,621.23 | 13,937.02 | 13,741.34 | 2,912.24 |
| otel-proto-fastbatch JVM | 10 | 1,091.47 | 991.61 | 6,556.11 | 6,292.56 | 6,890.13 |
| otel-proto-combined JVM | 10 | 1,002.07 | 879.15 | 5,923.84 | 5,390.96 | 7,248.57 |
| otel JS (Node) | 10 | 13,160.04 | 13,162.69 | 130,665.53 | 128,965.45 | 12,949.24 |
| otel-proto JS (Node) | 10 | 13,043.44 | 13,037.59 | 129,649.32 | 129,199.22 | 5,505.35 |
| otel-proto-sampler JS (Node) | 10 | 12,740.10 | 12,752.16 | 126,705.36 | 126,376.67 | 4,922.88 |
| otel-proto-timesource JS (Node) | 10 | 14,510.22 | 14,606.39 | 145,760.34 | 143,496.15 | 13,299.44 |
| otel-proto-anchored JS (Node) | 10 | 12,745.18 | 12,766.71 | 126,915.98 | 125,954.22 | 7,665.71 |
| otel-proto-fastbatch JS (Node) | 10 | 8,708.89 | 8,716.43 | 86,617.10 | 86,079.13 | 4,298.34 |
| otel-proto-combined JS (Node) | 10 | 8,421.99 | 8,420.78 | 83,829.95 | 83,323.65 | 4,336.90 |
| otel Native (Linux) | 10 | 10,241.83 | 10,224.99 | 100,880.12 | 101,197.50 | 12,229.76 |
| otel-proto Native (Linux) | 10 | 8,522.99 | 8,494.05 | 85,328.88 | 85,536.56 | 7,535.06 |
| otel-proto-sampler Native (Linux) | 10 | 8,404.06 | 8,456.04 | 84,338.07 | 84,045.08 | 7,009.71 |
| otel-proto-timesource Native (Linux) | 10 | 8,428.47 | 8,433.75 | 84,462.42 | 84,293.78 | 7,185.57 |
| otel-proto-anchored Native (Linux) | 10 | 8,494.20 | 8,492.68 | 85,319.86 | 84,888.26 | 7,161.22 |
| otel-proto-fastbatch Native (Linux) | 10 | 4,524.51 | 4,257.36 | 42,281.71 | 42,231.75 | 10,782.31 |
| otel-proto-combined Native (Linux) | 10 | 4,316.37 | 4,051.68 | 40,031.77 | 40,000.73 | 10,862.88 |

## Per-method timings

| Variant | Platform | Mean step (µs) | Methods/step | Per-method (ns) = step / methods | Overhead/method (ns) = Δ vs baseline |
|---|---|---:|---:|---:|---:|
| otel | JVM | 23,138.26 | 21893 | 1,056.9 | 1,056.2 |
| otel-proto | JVM | 13,927.13 | 21893 | 636.1 | 635.5 |
| otel-proto-sampler | JVM | 13,089.13 | 21893 | 597.9 | 597.2 |
| otel-proto-timesource | JVM | 13,579.67 | 21893 | 620.3 | 619.6 |
| otel-proto-anchored | JVM | 13,937.02 | 21893 | 636.6 | 635.9 |
| otel-proto-fastbatch | JVM | 6,556.11 | 21893 | 299.5 | 298.8 |
| otel-proto-combined | JVM | 5,923.84 | 21893 | 270.6 | 269.9 |
| otel | JS | 130,665.53 | 21893 | 5,968.4 | 5,963.7 |
| otel-proto | JS | 129,649.32 | 21893 | 5,922.0 | 5,917.3 |
| otel-proto-sampler | JS | 126,705.36 | 21893 | 5,787.5 | 5,782.8 |
| otel-proto-timesource | JS | 145,760.34 | 21893 | 6,657.9 | 6,653.2 |
| otel-proto-anchored | JS | 126,915.98 | 21893 | 5,797.1 | 5,792.4 |
| otel-proto-fastbatch | JS | 86,617.10 | 21893 | 3,956.4 | 3,951.7 |
| otel-proto-combined | JS | 83,829.95 | 21893 | 3,829.1 | 3,824.4 |
| otel | Native | 100,880.12 | 21893 | 4,607.9 | 4,607.4 |
| otel-proto | Native | 85,328.88 | 21893 | 3,897.5 | 3,897.0 |
| otel-proto-sampler | Native | 84,338.07 | 21893 | 3,852.3 | 3,851.8 |
| otel-proto-timesource | Native | 84,462.42 | 21893 | 3,858.0 | 3,857.5 |
| otel-proto-anchored | Native | 85,319.86 | 21893 | 3,897.1 | 3,896.6 |
| otel-proto-fastbatch | Native | 42,281.71 | 21893 | 1,931.3 | 1,930.8 |
| otel-proto-combined | Native | 40,031.77 | 21893 | 1,828.5 | 1,828.0 |


Expected = `methods/step × StepCount × RunCount` = 21893 × 100 × 10 = 21893000. Jaeger columns are per-variant deltas. **Delivered % = Stored (saved_ok) / Expected** (ground truth). Status: OK · LOSS · DUP · INVALID (Jaeger died — row untrustworthy) · OK (false-fail: N) (delivered, client threw on response read). ~ = client-side fallback when Jaeger metrics missing.

| Variant | Platform | Expected | Exported (attempted) | Failed (client) | Wire recv (Δ) | Stored saved_ok (Δ) | Dropped (Δ) | Delivered (%) | Dup | Status |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|:--|
| otel | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-sampler | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-timesource | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-anchored | JVM | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-fastbatch | JVM | 21,893,000 | 21,893,010 | 13,827,028 | N/A | N/A | N/A | 36.84~ | — | INVALID (backend died) |
| otel-proto-combined | JVM | 21,893,000 | 21,893,010 | 13,674,975 | N/A | N/A | N/A | 37.54~ | — | INVALID (backend died) |
| otel | JS | 21,893,000 | 21,893,010 | 304,640 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK (false-fail: 304,640) |
| otel-proto | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-sampler | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-timesource | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-anchored | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-fastbatch | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-combined | JS | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-sampler | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-timesource | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-anchored | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-fastbatch | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |
| otel-proto-combined | Native | 21,893,000 | 21,893,010 | 0 | 21,893,010 | 21,893,010 | 0 | 100.00 | — | OK |

First swallowed export error per row (if any):
- **otel-proto-fastbatch JVM** first export error: `StatusException: Status(code=UNAVAILABLE, statusMessage=upstream connect error or disconnect/reset before headers. reset reason: connection termination)`
- **otel-proto-combined JVM** first export error: `StatusException: Status(code=UNAVAILABLE, statusMessage=upstream connect error or disconnect/reset before headers. reset reason: connection termination)`
- **otel JS** first export error: `IllegalStateException: Content-Length mismatch: expected 21 bytes, but received 0 bytes`

## Per-step median curve (µs)

Sampled step indices across 10 runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s20 | s25 | s30 | s60 | s99 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 2,432.66 | 23.09 | 14.76 | 14.44 | 14.34 | 14.37 | 14.68 | 14.42 | 14.50 | 13.86 |
| baseline | JS | 1,225.29 | 360.47 | 240.59 | 92.35 | 112.48 | 105.35 | 86.36 | 105.25 | 103.57 | 101.61 |
| baseline | Native | 98.39 | 18.30 | 17.80 | 17.46 | 10.84 | 10.79 | 10.79 | 10.80 | 10.77 | 10.76 |
| otel | JVM | 164,566.64 | 54,719.07 | 51,267.83 | 44,501.54 | 29,503.04 | 39,258.35 | 42,269.07 | 25,036.33 | 20,127.75 | 17,301.79 |
| otel-proto | JVM | 106,487.08 | 30,771.47 | 24,407.81 | 16,146.30 | 14,298.18 | 13,190.68 | 13,776.97 | 14,560.52 | 13,673.16 | 13,254.55 |
| otel-proto-sampler | JVM | 105,015.10 | 35,000.40 | 19,649.10 | 14,979.37 | 13,929.68 | 12,889.56 | 13,955.36 | 13,910.89 | 12,917.52 | 12,894.71 |
| otel-proto-timesource | JVM | 98,045.66 | 37,143.09 | 24,597.08 | 16,000.18 | 13,499.64 | 13,194.30 | 16,595.38 | 13,444.14 | 13,360.42 | 13,342.99 |
| otel-proto-anchored | JVM | 102,392.47 | 30,398.28 | 21,832.27 | 16,543.94 | 14,116.77 | 14,392.09 | 14,896.23 | 14,094.47 | 14,543.81 | 13,077.09 |
| otel-proto-fastbatch | JVM | 108,436.16 | 41,238.02 | 21,387.21 | 13,088.26 | 7,937.14 | 6,666.79 | 5,955.97 | 7,308.76 | 5,668.31 | 7,596.46 |
| otel-proto-combined | JVM | 110,428.16 | 43,330.47 | 18,448.53 | 8,984.62 | 6,400.20 | 6,128.58 | 5,322.03 | 7,472.33 | 4,686.52 | 4,950.33 |
| otel | JS | 185,054.73 | 142,899.26 | 132,095.20 | 128,104.12 | 127,380.47 | 127,275.88 | 127,137.40 | 127,778.66 | 129,961.35 | 131,549.48 |
| otel-proto | JS | 187,195.79 | 142,721.92 | 130,301.61 | 139,856.20 | 127,340.12 | 126,699.69 | 142,321.95 | 128,437.69 | 129,335.26 | 140,543.31 |
| otel-proto-sampler | JS | 185,306.29 | 140,299.81 | 126,100.15 | 128,727.48 | 124,933.80 | 123,574.85 | 127,241.01 | 125,238.06 | 126,600.31 | 127,951.40 |
| otel-proto-timesource | JS | 198,171.31 | 157,702.12 | 144,210.17 | 142,307.67 | 142,311.52 | 139,881.31 | 141,329.67 | 142,355.42 | 145,129.71 | 142,674.66 |
| otel-proto-anchored | JS | 185,390.51 | 140,820.23 | 127,575.82 | 126,162.99 | 125,079.80 | 122,707.37 | 125,222.48 | 125,055.77 | 126,110.47 | 173,451.51 |
| otel-proto-fastbatch | JS | 124,775.53 | 98,954.47 | 89,610.81 | 85,869.76 | 84,133.18 | 83,475.74 | 108,141.03 | 83,676.26 | 86,156.34 | 88,030.20 |
| otel-proto-combined | JS | 118,976.76 | 96,112.13 | 81,773.62 | 82,868.79 | 82,561.43 | 81,168.26 | 102,220.20 | 81,376.74 | 83,531.31 | 84,348.73 |
| otel | Native | 97,441.76 | 98,764.33 | 102,895.98 | 98,892.04 | 101,558.11 | 92,938.40 | 91,700.45 | 95,850.11 | 103,320.20 | 101,848.14 |
| otel-proto | Native | 72,985.97 | 74,332.82 | 74,984.99 | 75,906.73 | 79,530.58 | 79,799.66 | 80,745.44 | 83,169.20 | 87,963.59 | 85,359.97 |
| otel-proto-sampler | Native | 73,418.00 | 74,891.27 | 73,241.22 | 76,192.75 | 76,513.44 | 82,106.73 | 83,215.87 | 82,821.03 | 85,482.96 | 87,347.69 |
| otel-proto-timesource | Native | 76,313.55 | 74,796.00 | 76,099.34 | 76,926.81 | 75,962.39 | 79,008.27 | 82,223.29 | 84,612.34 | 84,639.00 | 89,643.49 |
| otel-proto-anchored | Native | 75,233.81 | 73,491.74 | 75,623.14 | 78,201.53 | 78,794.51 | 78,888.19 | 84,904.05 | 82,612.89 | 89,895.49 | 84,878.09 |
| otel-proto-fastbatch | Native | 40,765.53 | 40,310.67 | 38,213.77 | 40,282.56 | 42,270.03 | 39,535.67 | 41,611.59 | 40,656.16 | 43,036.34 | 43,602.06 |
| otel-proto-combined | Native | 38,175.86 | 36,983.39 | 36,273.90 | 40,245.18 | 39,085.29 | 39,556.93 | 37,183.96 | 39,150.05 | 40,401.08 | 40,626.09 |
> Curve shape: JVM C1≈step 1-2, C2 hits later. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction. Step indices < 20 are discarded from the per-method statistics above.
