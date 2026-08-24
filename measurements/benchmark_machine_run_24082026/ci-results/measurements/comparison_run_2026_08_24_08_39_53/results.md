# Benchmark Results (2026_08_24_08_39_53)

This document contains the results of one full execution of the k-perf/OTel comparison benchmark. The benchmark measures how much runtime overhead different tracing implementations add to a Kotlin Multiplatform program, on three platforms: JVM, JavaScript (Node.js), and a native binary.

Terminology used throughout this document:

- A **variant** is one tracing implementation. `baseline` is the identical program with no tracing at all.
- A **step** is one call of the workload function (`fibonacci(20)`). The duration of every step is measured individually.
- A **run** is one complete program execution containing 100 steps. Every variant is executed 10 times, each time in a fresh process, so results do not depend on a single lucky or unlucky execution.
- The first 20 steps of every run are **warmup** and excluded from all timing statistics.

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

The workload is deterministic, so the exact number of traced function calls in one step is known in advance. The tables below divide step times by this value to get per-function-call times.

| Platform | methods_per_step (formula) |
|---|---:|
| JVM | 21892 |
| JS | 21892 |
| Native | 21892 |

## Execution Summary

Raw timing statistics for every (variant, platform) combination. Column meanings:

- **Iterations**: how many of the 10 runs produced a valid measurement. A value below 10 means some runs failed. The raw output of failed runs is stored in the `failures/` folder.
- **Total mean / Total median (ms)**: average and middle value of the whole-process duration. For OTel variants this includes waiting at the end of the process until all remaining tracing data has been exported. Because of that, do not compare totals across variants. Use the step columns for comparisons instead.
- **Mean step (µs)**: the headline timing number, computed in two stages. First, for every step index, take the median of that step's duration across the 10 runs. Then average those medians over the measured region (step 20 to 99). The first 20 steps of every run are excluded as warmup.
- **Step median / Step stddev (µs)**: the middle value and the spread of all measured (non-warmup) step durations. A large stddev means step times fluctuated strongly from step to step or run to run.

| Executable | Iterations | Total mean (ms) | Total median (ms) | Mean step (µs) | Step median (µs) | Step stddev (µs) |
|------------|-----------:|----------------:|------------------:|---------------:|-----------------:|-----------------:|
| baseline JVM | 10 | 11.13 | 11.00 | 12.59 | 12.48 | 2.92 |
| baseline JS (Node) | 10 | 15.60 | 14.54 | 100.85 | 102.40 | 116.14 |
| baseline Native (Linux) | 10 | 1.33 | 1.16 | 10.43 | 10.44 | 3.91 |
| otel JVM | 10 | 3,035.44 | 3,033.30 | 23,909.08 | 22,583.54 | 15,165.59 |
| otel-proto JVM | 10 | 1,639.39 | 1,635.24 | 14,048.27 | 14,002.80 | 2,679.88 |
| otel-proto-sampler JVM | 10 | 1,546.73 | 1,532.03 | 13,361.33 | 13,065.99 | 2,789.84 |
| otel-proto-timesource JVM | 10 | 1,636.09 | 1,642.57 | 13,885.35 | 13,776.03 | 3,268.57 |
| otel-proto-anchored JVM | 10 | 1,659.21 | 1,661.49 | 14,148.28 | 14,139.26 | 2,892.64 |
| otel-proto-fastbatch JVM | 10 | 1,291.86 | 1,299.01 | 9,254.90 | 9,144.63 | 6,173.11 |
| otel-proto-combined JVM | 10 | 1,177.61 | 1,170.14 | 8,251.01 | 8,172.59 | 6,597.73 |
| otel JS (Node) | 10 | 13,107.91 | 13,132.11 | 130,975.72 | 128,666.41 | 11,881.47 |
| otel-proto JS (Node) | 10 | 12,961.01 | 12,952.08 | 129,007.84 | 128,445.45 | 5,147.94 |
| otel-proto-sampler JS (Node) | 10 | 12,709.91 | 12,728.60 | 126,445.77 | 126,047.22 | 4,145.02 |
| otel-proto-timesource JS (Node) | 10 | 14,615.61 | 14,618.20 | 145,673.73 | 143,636.29 | 12,827.92 |
| otel-proto-anchored JS (Node) | 10 | 12,726.14 | 12,716.46 | 126,883.62 | 125,847.98 | 7,105.28 |
| otel-proto-fastbatch JS (Node) | 10 | 8,745.81 | 8,728.78 | 86,819.15 | 86,419.20 | 5,275.65 |
| otel-proto-combined JS (Node) | 10 | 8,458.35 | 8,446.03 | 84,158.15 | 83,654.72 | 4,536.74 |
| otel Native (Linux) | 10 | 10,364.17 | 10,383.26 | 101,737.47 | 101,648.64 | 12,486.54 |
| otel-proto Native (Linux) | 10 | 8,475.58 | 8,475.71 | 85,010.51 | 85,057.24 | 7,886.24 |
| otel-proto-sampler Native (Linux) | 10 | 8,397.38 | 8,409.74 | 83,788.08 | 83,629.54 | 7,317.06 |
| otel-proto-timesource Native (Linux) | 10 | 8,246.59 | 8,242.49 | 83,060.76 | 83,224.78 | 7,927.26 |
| otel-proto-anchored Native (Linux) | 10 | 8,469.09 | 8,441.59 | 85,061.27 | 84,991.48 | 7,703.23 |
| otel-proto-fastbatch Native (Linux) | 10 | 4,256.12 | 4,044.89 | 40,126.89 | 39,944.29 | 9,586.97 |
| otel-proto-combined Native (Linux) | 10 | 4,270.49 | 4,047.55 | 39,214.51 | 39,255.34 | 11,125.16 |

## Per-method timings

The step times from above, converted into the cost of one traced function call:

- **Per-method (ns) = step / methods**: the mean step time divided by the 21892 function calls in a step, i.e. the average total cost of one function call, including the workload's own computation.
- **Overhead/method (ns) = Δ vs baseline**: the same value minus the baseline's value on the same platform, i.e. what tracing itself adds to every single function call. This column is the main result of the benchmark.

Baseline rows only show the pure workload cost per call (no instrumentation).

| Variant | Platform | Mean step (µs) | Methods/step | Per-method (ns) = step / methods | Overhead/method (ns) = Δ vs baseline |
|---|---|---:|---:|---:|---:|
| baseline | JVM | 12.59 | 21892 | 0.6 | N/A |
| baseline | JS | 100.85 | 21892 | 4.6 | N/A |
| baseline | Native | 10.43 | 21892 | 0.5 | N/A |
| otel | JVM | 23,909.08 | 21892 | 1,092.1 | 1,091.6 |
| otel-proto | JVM | 14,048.27 | 21892 | 641.7 | 641.1 |
| otel-proto-sampler | JVM | 13,361.33 | 21892 | 610.3 | 609.8 |
| otel-proto-timesource | JVM | 13,885.35 | 21892 | 634.3 | 633.7 |
| otel-proto-anchored | JVM | 14,148.28 | 21892 | 646.3 | 645.7 |
| otel-proto-fastbatch | JVM | 9,254.90 | 21892 | 422.8 | 422.2 |
| otel-proto-combined | JVM | 8,251.01 | 21892 | 376.9 | 376.3 |
| otel | JS | 130,975.72 | 21892 | 5,982.8 | 5,978.2 |
| otel-proto | JS | 129,007.84 | 21892 | 5,892.9 | 5,888.3 |
| otel-proto-sampler | JS | 126,445.77 | 21892 | 5,775.9 | 5,771.3 |
| otel-proto-timesource | JS | 145,673.73 | 21892 | 6,654.2 | 6,649.6 |
| otel-proto-anchored | JS | 126,883.62 | 21892 | 5,795.9 | 5,791.3 |
| otel-proto-fastbatch | JS | 86,819.15 | 21892 | 3,965.8 | 3,961.2 |
| otel-proto-combined | JS | 84,158.15 | 21892 | 3,844.2 | 3,839.6 |
| otel | Native | 101,737.47 | 21892 | 4,647.2 | 4,646.8 |
| otel-proto | Native | 85,010.51 | 21892 | 3,883.2 | 3,882.7 |
| otel-proto-sampler | Native | 83,788.08 | 21892 | 3,827.3 | 3,826.9 |
| otel-proto-timesource | Native | 83,060.76 | 21892 | 3,794.1 | 3,793.6 |
| otel-proto-anchored | Native | 85,061.27 | 21892 | 3,885.5 | 3,885.0 |
| otel-proto-fastbatch | Native | 40,126.89 | 21892 | 1,832.9 | 1,832.5 |
| otel-proto-combined | Native | 39,214.51 | 21892 | 1,791.3 | 1,790.8 |

## OTel span delivery verification

Tracing data (spans, one per traced function call) is exported asynchronously in the background. A benchmark could therefore look fast simply because tracing data was silently thrown away instead of being processed. This table proves that did not happen: for every OTel variant, spans are counted at each step of the export pipeline (when they leave the plugin, when they arrive over the network, and when the Jaeger backend stores them), and all counts must match the number of spans the program generated. Unlike the timing statistics, span counts cover all 100 steps of every run: the warmup cutoff applies only to timings, because warmup steps still create and export spans.

Column meanings:

- **Expected**: the number of spans the program generates. It is `methods/step × StepCount × RunCount` = 21892 × 100 × 10 = 21892000 spans per row.
- **Exported**: spans the plugin handed to the network.
- **Failed (client)**: spans whose export ended in an error on the program side.
- **Stored (Jaeger)**: spans the Jaeger backend saved. This is the ground truth for delivery.
- **Dropped (Jaeger)**: spans that reached Jaeger but were thrown away because the backend was overloaded.
- **Delivered (%)**: Stored divided by Expected. 100.00 means every single span arrived.
- **Status**: the verdict for the row. OK means everything arrived. LOSS means spans went missing somewhere. DUP means Jaeger received spans more than once. INVALID means the Jaeger backend crashed during this variant, so the whole row is untrustworthy. OK (false-fail: N) means everything arrived, but the plugin wrongly counted N spans as failed because it could not read the server's confirmation.

More detailed counters (e.g. the raw network-level receive counts) are stored in `results.json`.

| Variant | Platform | Expected | Exported | Failed (client) | Stored (Jaeger) | Dropped (Jaeger) | Delivered (%) | Status |
|---|---|---:|---:|---:|---:|---:|---:|:--|
| otel | JVM | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto | JVM | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-sampler | JVM | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-timesource | JVM | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-anchored | JVM | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-fastbatch | JVM | 21,892,000 | 21,892,010 | 0 | 14,869,554 | 7,022,456 | 67.92 | LOSS |
| otel-proto-combined | JVM | 21,892,000 | 21,892,010 | 0 | 13,966,218 | 7,925,792 | 63.80 | LOSS |
| otel | JS | 21,892,000 | 21,892,010 | 314,880 | 21,892,010 | 0 | 100.00 | OK (false-fail: 314,880) |
| otel-proto | JS | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-sampler | JS | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-timesource | JS | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-anchored | JS | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-fastbatch | JS | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-combined | JS | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel | Native | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto | Native | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-sampler | Native | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-timesource | Native | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-anchored | Native | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-fastbatch | Native | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-combined | Native | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |

First swallowed export error per row (if any):
- **otel JS** first export error: `IllegalStateException: Content-Length mismatch: expected 21 bytes, but received 0 bytes`

## Per-step median curve (µs)

How the duration of a step changes over the lifetime of a process. The column `s0` is the first step of a run, `s1` the second, and so on. Each cell shows the median of that step's duration across the 10 runs. For example, `s0` is the median duration of the very first step, taken over all 10 runs. Reading a row from left to right therefore shows one representative process over time. Only selected step indices are shown here. The full curves are in `per_step_medians.csv` and `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s20 | s25 | s30 | s60 | s99 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 2,490.59 | 21.30 | 13.12 | 12.73 | 12.93 | 12.60 | 12.54 | 12.69 | 12.45 | 11.95 |
| baseline | JS | 1,189.06 | 346.23 | 234.76 | 91.34 | 105.13 | 106.47 | 84.78 | 101.21 | 100.77 | 100.61 |
| baseline | Native | 64.39 | 10.55 | 10.45 | 10.41 | 10.48 | 10.44 | 10.44 | 10.42 | 10.44 | 10.44 |
| otel | JVM | 163,965.36 | 64,880.93 | 41,535.90 | 41,738.29 | 32,905.15 | 36,828.79 | 38,557.65 | 29,562.16 | 17,902.77 | 16,606.59 |
| otel-proto | JVM | 105,409.82 | 34,131.61 | 24,038.38 | 18,106.93 | 13,779.77 | 15,720.76 | 14,463.40 | 14,889.06 | 15,322.84 | 14,593.59 |
| otel-proto-sampler | JVM | 104,379.26 | 34,024.11 | 20,945.72 | 14,855.12 | 15,131.50 | 12,760.53 | 14,276.69 | 14,943.84 | 13,720.15 | 14,452.06 |
| otel-proto-timesource | JVM | 100,152.34 | 35,342.16 | 22,913.81 | 15,465.24 | 14,750.50 | 13,632.03 | 16,309.57 | 14,419.52 | 14,261.21 | 13,764.58 |
| otel-proto-anchored | JVM | 104,462.49 | 30,680.45 | 25,373.28 | 17,304.46 | 14,848.49 | 13,508.40 | 14,745.60 | 14,027.41 | 14,491.05 | 13,638.70 |
| otel-proto-fastbatch | JVM | 112,730.56 | 57,207.84 | 15,362.89 | 13,530.74 | 11,252.96 | 9,915.82 | 9,840.36 | 10,169.34 | 8,899.68 | 8,451.15 |
| otel-proto-combined | JVM | 109,438.42 | 46,005.55 | 18,355.78 | 11,541.14 | 9,508.23 | 8,192.10 | 7,993.12 | 7,909.61 | 8,002.23 | 7,263.37 |
| otel | JS | 187,577.49 | 142,540.10 | 131,774.17 | 127,230.68 | 126,292.89 | 126,854.04 | 126,088.14 | 127,484.26 | 130,036.12 | 165,262.45 |
| otel-proto | JS | 185,095.04 | 142,890.94 | 129,471.70 | 135,253.19 | 127,708.81 | 126,116.26 | 128,484.98 | 127,606.37 | 129,050.53 | 142,326.73 |
| otel-proto-sampler | JS | 183,276.88 | 141,331.77 | 125,629.48 | 126,679.13 | 124,510.92 | 122,971.74 | 124,532.25 | 125,967.45 | 126,605.73 | 125,944.43 |
| otel-proto-timesource | JS | 196,953.43 | 157,822.26 | 144,312.88 | 142,718.27 | 142,096.80 | 140,018.24 | 140,817.44 | 142,144.30 | 145,142.50 | 145,066.06 |
| otel-proto-anchored | JS | 177,789.80 | 140,810.61 | 127,115.32 | 125,147.57 | 124,905.40 | 122,802.34 | 125,344.95 | 125,283.06 | 126,150.62 | 173,824.73 |
| otel-proto-fastbatch | JS | 125,712.94 | 99,419.10 | 88,257.63 | 94,372.45 | 84,723.47 | 83,387.48 | 108,461.43 | 83,708.24 | 85,990.38 | 87,153.30 |
| otel-proto-combined | JS | 120,268.88 | 96,490.39 | 82,002.32 | 82,960.24 | 81,839.06 | 80,866.70 | 104,382.00 | 81,118.49 | 83,521.45 | 85,592.67 |
| otel | Native | 100,990.62 | 103,211.35 | 103,276.27 | 102,467.88 | 97,457.53 | 92,059.76 | 98,791.68 | 98,699.33 | 102,869.18 | 103,299.53 |
| otel-proto | Native | 74,137.40 | 72,465.65 | 72,501.15 | 74,841.02 | 77,401.15 | 79,245.07 | 79,910.26 | 86,364.14 | 85,467.27 | 83,439.04 |
| otel-proto-sampler | Native | 74,144.01 | 74,829.92 | 76,595.58 | 79,110.49 | 81,154.92 | 81,240.84 | 84,843.76 | 87,147.39 | 82,126.21 | 88,392.04 |
| otel-proto-timesource | Native | 71,064.70 | 71,243.08 | 70,564.99 | 70,552.50 | 73,479.25 | 73,646.44 | 80,243.32 | 80,663.60 | 81,455.77 | 84,249.69 |
| otel-proto-anchored | Native | 71,665.93 | 73,750.45 | 76,070.20 | 77,627.52 | 77,519.32 | 78,468.69 | 80,364.12 | 83,913.60 | 86,435.04 | 85,440.85 |
| otel-proto-fastbatch | Native | 42,425.42 | 40,223.35 | 40,053.09 | 39,384.17 | 41,155.25 | 39,489.23 | 39,730.10 | 40,530.92 | 41,920.04 | 41,765.80 |
| otel-proto-combined | Native | 39,279.95 | 39,445.96 | 40,367.83 | 40,669.72 | 41,173.29 | 38,241.11 | 37,577.83 | 40,473.23 | 38,403.88 | 37,929.61 |
