# Benchmark Results (2026_08_24_17_06_38)

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
| baseline JVM | 10 | 11.05 | 10.91 | 12.56 | 12.41 | 1.97 |
| baseline JS (Node) | 10 | 14.93 | 14.11 | 100.85 | 102.49 | 12.79 |
| baseline Native (Linux) | 10 | 1.38 | 1.40 | 11.98 | 12.73 | 3.65 |
| otel JVM | 10 | 2,862.44 | 2,832.52 | 22,866.58 | 20,704.36 | 13,648.21 |
| otel-proto JVM | 10 | 1,654.37 | 1,645.55 | 14,050.70 | 13,959.26 | 3,407.19 |
| otel-proto-sampler JVM | 10 | 1,598.51 | 1,592.18 | 13,555.25 | 13,488.59 | 3,187.10 |
| otel-proto-timesource JVM | 10 | 1,630.87 | 1,630.34 | 13,920.04 | 13,833.17 | 3,289.38 |
| otel-proto-anchored JVM | 10 | 1,608.29 | 1,601.45 | 13,828.38 | 13,666.79 | 2,742.93 |
| otel-proto-fastbatch JVM | 10 | 1,234.23 | 1,206.12 | 8,895.60 | 8,771.74 | 6,546.88 |
| otel-proto-combined JVM | 10 | 1,175.71 | 1,182.80 | 7,871.37 | 7,704.07 | 7,412.57 |
| otel JS (Node) | 10 | 13,130.13 | 13,103.19 | 130,903.59 | 128,804.13 | 11,946.98 |
| otel-proto JS (Node) | 10 | 12,966.49 | 12,957.18 | 129,127.22 | 128,591.81 | 5,270.98 |
| otel-proto-sampler JS (Node) | 10 | 12,757.79 | 12,749.98 | 126,556.85 | 126,379.79 | 5,954.78 |
| otel-proto-timesource JS (Node) | 10 | 14,604.06 | 14,596.01 | 145,505.94 | 143,498.79 | 13,121.08 |
| otel-proto-anchored JS (Node) | 10 | 12,762.97 | 12,750.13 | 127,148.74 | 126,320.28 | 7,141.68 |
| otel-proto-fastbatch JS (Node) | 10 | 8,677.49 | 8,672.64 | 86,370.49 | 85,905.47 | 4,682.13 |
| otel-proto-combined JS (Node) | 10 | 8,437.68 | 8,451.66 | 84,030.50 | 83,522.19 | 4,324.28 |
| otel Native (Linux) | 10 | 10,345.71 | 10,330.12 | 101,859.45 | 102,031.40 | 12,203.25 |
| otel-proto Native (Linux) | 10 | 8,436.37 | 8,459.28 | 85,101.88 | 85,209.32 | 7,101.52 |
| otel-proto-sampler Native (Linux) | 10 | 8,405.45 | 8,414.66 | 84,301.76 | 84,234.25 | 7,977.80 |
| otel-proto-timesource Native (Linux) | 10 | 8,328.77 | 8,286.59 | 83,435.99 | 83,222.98 | 6,863.17 |
| otel-proto-anchored Native (Linux) | 10 | 8,554.92 | 8,485.46 | 86,128.50 | 86,136.08 | 7,264.21 |
| otel-proto-fastbatch Native (Linux) | 10 | 4,325.37 | 4,114.99 | 40,356.52 | 40,396.99 | 11,313.47 |
| otel-proto-combined Native (Linux) | 10 | 4,516.04 | 4,274.24 | 42,206.38 | 42,288.12 | 10,663.00 |

## Per-method timings

The step times from above, converted into the cost of one traced function call:

- **Per-method (ns) = step / methods**: the mean step time divided by the 21892 function calls in a step, i.e. the average total cost of one function call, including the workload's own computation.
- **Overhead/method (ns) = Δ vs baseline**: the same value minus the baseline's value on the same platform, i.e. what tracing itself adds to every single function call. This column is the main result of the benchmark.

Baseline rows only show the pure workload cost per call (no instrumentation).

| Variant | Platform | Mean step (µs) | Methods/step | Per-method (ns) = step / methods | Overhead/method (ns) = Δ vs baseline |
|---|---|---:|---:|---:|---:|
| baseline | JVM | 12.56 | 21892 | 0.6 | N/A |
| baseline | JS | 100.85 | 21892 | 4.6 | N/A |
| baseline | Native | 11.98 | 21892 | 0.5 | N/A |
| otel | JVM | 22,866.58 | 21892 | 1,044.5 | 1,043.9 |
| otel-proto | JVM | 14,050.70 | 21892 | 641.8 | 641.2 |
| otel-proto-sampler | JVM | 13,555.25 | 21892 | 619.2 | 618.6 |
| otel-proto-timesource | JVM | 13,920.04 | 21892 | 635.9 | 635.3 |
| otel-proto-anchored | JVM | 13,828.38 | 21892 | 631.7 | 631.1 |
| otel-proto-fastbatch | JVM | 8,895.60 | 21892 | 406.3 | 405.8 |
| otel-proto-combined | JVM | 7,871.37 | 21892 | 359.6 | 359.0 |
| otel | JS | 130,903.59 | 21892 | 5,979.5 | 5,974.9 |
| otel-proto | JS | 129,127.22 | 21892 | 5,898.4 | 5,893.8 |
| otel-proto-sampler | JS | 126,556.85 | 21892 | 5,781.0 | 5,776.4 |
| otel-proto-timesource | JS | 145,505.94 | 21892 | 6,646.5 | 6,641.9 |
| otel-proto-anchored | JS | 127,148.74 | 21892 | 5,808.0 | 5,803.4 |
| otel-proto-fastbatch | JS | 86,370.49 | 21892 | 3,945.3 | 3,940.7 |
| otel-proto-combined | JS | 84,030.50 | 21892 | 3,838.4 | 3,833.8 |
| otel | Native | 101,859.45 | 21892 | 4,652.8 | 4,652.3 |
| otel-proto | Native | 85,101.88 | 21892 | 3,887.4 | 3,886.8 |
| otel-proto-sampler | Native | 84,301.76 | 21892 | 3,850.8 | 3,850.3 |
| otel-proto-timesource | Native | 83,435.99 | 21892 | 3,811.3 | 3,810.7 |
| otel-proto-anchored | Native | 86,128.50 | 21892 | 3,934.2 | 3,933.7 |
| otel-proto-fastbatch | Native | 40,356.52 | 21892 | 1,843.4 | 1,842.9 |
| otel-proto-combined | Native | 42,206.38 | 21892 | 1,927.9 | 1,927.4 |

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
| otel-proto-fastbatch | JVM | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-combined | JVM | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel | JS | 21,892,000 | 21,892,010 | 285,184 | 21,892,010 | 0 | 100.00 | OK (false-fail: 285,184) |
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
| baseline | JVM | 2,494.26 | 20.91 | 12.88 | 12.61 | 12.91 | 12.50 | 12.52 | 12.56 | 12.46 | 12.21 |
| baseline | JS | 1,152.16 | 337.99 | 217.68 | 89.31 | 104.98 | 105.07 | 84.82 | 100.78 | 101.72 | 103.23 |
| baseline | Native | 62.19 | 10.66 | 10.53 | 10.48 | 10.53 | 11.65 | 12.83 | 12.81 | 12.77 | 10.39 |
| otel | JVM | 152,126.78 | 65,581.68 | 51,342.00 | 21,638.51 | 41,037.10 | 51,974.26 | 34,523.99 | 32,889.46 | 16,333.49 | 15,591.54 |
| otel-proto | JVM | 103,616.10 | 30,490.93 | 23,337.31 | 17,747.73 | 15,277.09 | 14,893.38 | 15,663.53 | 14,562.21 | 15,805.76 | 13,990.70 |
| otel-proto-sampler | JVM | 103,483.92 | 35,278.68 | 22,981.22 | 16,246.97 | 14,768.01 | 12,916.52 | 14,411.47 | 14,169.85 | 12,977.29 | 13,398.61 |
| otel-proto-timesource | JVM | 96,879.68 | 36,122.40 | 24,442.00 | 16,779.34 | 13,954.28 | 13,505.62 | 15,123.01 | 14,642.14 | 15,054.97 | 13,341.55 |
| otel-proto-anchored | JVM | 104,654.04 | 29,237.33 | 22,392.30 | 16,233.68 | 14,275.48 | 13,633.40 | 14,669.64 | 15,404.95 | 14,288.48 | 12,644.89 |
| otel-proto-fastbatch | JVM | 107,454.25 | 52,331.03 | 18,929.36 | 14,089.84 | 9,984.92 | 8,912.55 | 9,206.12 | 8,876.33 | 9,891.03 | 7,693.19 |
| otel-proto-combined | JVM | 104,821.91 | 48,192.91 | 17,416.29 | 12,491.21 | 9,308.98 | 7,754.68 | 8,363.32 | 7,731.47 | 7,455.67 | 6,708.70 |
| otel | JS | 183,475.57 | 141,757.34 | 130,921.40 | 127,705.85 | 126,548.75 | 126,579.53 | 126,084.77 | 127,442.90 | 130,142.46 | 164,089.29 |
| otel-proto | JS | 182,091.82 | 141,692.79 | 129,158.17 | 135,320.79 | 127,134.96 | 125,488.30 | 128,284.37 | 126,959.78 | 128,959.85 | 146,113.99 |
| otel-proto-sampler | JS | 183,814.57 | 142,128.64 | 126,786.47 | 130,734.33 | 124,779.02 | 123,066.65 | 126,607.51 | 125,357.47 | 126,574.00 | 126,853.96 |
| otel-proto-timesource | JS | 196,369.26 | 157,470.04 | 144,299.24 | 142,177.42 | 142,341.45 | 140,030.77 | 140,872.49 | 142,667.33 | 144,657.93 | 143,722.53 |
| otel-proto-anchored | JS | 180,673.67 | 139,976.73 | 127,624.77 | 125,316.17 | 124,611.79 | 122,428.68 | 125,180.00 | 125,410.49 | 127,073.29 | 175,175.15 |
| otel-proto-fastbatch | JS | 123,041.24 | 97,871.51 | 87,465.84 | 85,672.11 | 84,028.59 | 83,372.93 | 108,877.34 | 83,315.88 | 85,695.49 | 86,914.24 |
| otel-proto-combined | JS | 119,381.88 | 95,811.08 | 81,966.11 | 83,069.17 | 81,695.04 | 80,687.04 | 102,771.43 | 81,075.53 | 83,610.33 | 84,874.41 |
| otel | Native | 100,489.25 | 102,603.48 | 107,094.62 | 102,128.42 | 100,773.20 | 91,075.05 | 96,634.12 | 94,699.58 | 98,601.99 | 105,339.62 |
| otel-proto | Native | 72,087.37 | 71,510.59 | 72,955.62 | 74,449.64 | 77,337.27 | 78,146.35 | 83,044.02 | 84,065.86 | 86,349.24 | 86,266.64 |
| otel-proto-sampler | Native | 69,913.59 | 70,804.00 | 71,856.41 | 77,166.13 | 74,601.36 | 78,247.67 | 82,454.72 | 81,648.66 | 83,785.46 | 89,892.82 |
| otel-proto-timesource | Native | 72,488.75 | 72,451.42 | 74,938.36 | 78,000.85 | 76,835.03 | 78,885.24 | 79,606.40 | 82,493.12 | 84,625.08 | 84,948.41 |
| otel-proto-anchored | Native | 73,603.66 | 73,761.73 | 74,784.89 | 78,864.65 | 77,192.52 | 78,632.58 | 82,353.90 | 83,579.07 | 87,707.96 | 87,312.07 |
| otel-proto-fastbatch | Native | 39,305.64 | 38,388.17 | 39,004.32 | 41,086.03 | 40,269.24 | 36,864.70 | 38,433.75 | 39,678.61 | 40,649.25 | 44,150.75 |
| otel-proto-combined | Native | 39,797.52 | 38,504.23 | 39,035.47 | 41,724.12 | 41,488.37 | 40,942.34 | 40,602.84 | 40,046.76 | 41,935.01 | 44,532.09 |
