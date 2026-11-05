# Benchmark Results (2026_08_23_14_45_03)

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
| baseline JVM | 10 | 10.90 | 11.00 | 12.35 | 12.27 | 1.82 |
| baseline JS (Node) | 10 | 14.78 | 14.35 | 101.09 | 103.26 | 15.14 |
| baseline Native (Linux) | 10 | 1.18 | 1.18 | 10.57 | 10.66 | 0.31 |
| otel JVM | 10 | 2,939.72 | 2,896.23 | 22,558.75 | 21,083.66 | 13,572.37 |
| otel-proto JVM | 10 | 1,663.84 | 1,673.89 | 14,387.94 | 14,039.17 | 2,627.84 |
| otel-proto-sampler JVM | 10 | 1,593.47 | 1,583.74 | 13,555.46 | 13,449.96 | 3,115.98 |
| otel-proto-timesource JVM | 10 | 1,606.91 | 1,605.80 | 13,562.91 | 13,444.99 | 3,127.10 |
| otel-proto-anchored JVM | 10 | 1,640.40 | 1,633.27 | 14,073.88 | 13,896.87 | 2,905.37 |
| otel-proto-fastbatch JVM | 10 | 1,193.63 | 1,171.18 | 8,907.49 | 8,983.59 | 5,071.95 |
| otel-proto-combined JVM | 10 | 1,208.31 | 1,183.46 | 8,115.93 | 8,065.13 | 6,820.18 |
| otel JS (Node) | 10 | 13,142.37 | 13,171.35 | 131,334.60 | 129,090.57 | 12,017.76 |
| otel-proto JS (Node) | 10 | 13,064.54 | 13,068.83 | 130,144.90 | 129,209.73 | 6,608.21 |
| otel-proto-sampler JS (Node) | 10 | 12,754.92 | 12,755.90 | 126,825.95 | 126,532.46 | 4,198.44 |
| otel-proto-timesource JS (Node) | 10 | 14,570.95 | 14,632.40 | 145,861.38 | 143,823.77 | 12,884.52 |
| otel-proto-anchored JS (Node) | 10 | 12,781.06 | 12,762.85 | 127,200.12 | 126,341.88 | 7,765.82 |
| otel-proto-fastbatch JS (Node) | 10 | 8,693.03 | 8,682.05 | 86,349.14 | 85,840.24 | 4,107.25 |
| otel-proto-combined JS (Node) | 10 | 8,451.30 | 8,457.87 | 84,138.97 | 83,594.34 | 4,385.44 |
| otel Native (Linux) | 10 | 10,090.64 | 10,084.63 | 98,925.45 | 99,762.29 | 12,372.88 |
| otel-proto Native (Linux) | 10 | 8,455.33 | 8,436.65 | 85,380.08 | 85,010.95 | 7,626.75 |
| otel-proto-sampler Native (Linux) | 10 | 8,423.60 | 8,460.67 | 84,421.38 | 84,370.80 | 7,430.06 |
| otel-proto-timesource Native (Linux) | 10 | 8,460.52 | 8,419.91 | 85,103.88 | 84,775.49 | 7,237.41 |
| otel-proto-anchored Native (Linux) | 10 | 8,425.90 | 8,448.52 | 84,050.68 | 84,055.14 | 7,232.68 |
| otel-proto-fastbatch Native (Linux) | 10 | 4,443.84 | 4,175.56 | 41,211.22 | 41,201.20 | 11,186.18 |
| otel-proto-combined Native (Linux) | 10 | 4,154.11 | 4,068.18 | 39,732.98 | 39,609.38 | 8,891.25 |

## Per-method timings

The step times from above, converted into the cost of one traced function call:

- **Per-method (ns) = step / methods**: the mean step time divided by the 21892 function calls in a step, i.e. the average total cost of one function call, including the workload's own computation.
- **Overhead/method (ns) = Δ vs baseline**: the same value minus the baseline's value on the same platform, i.e. what tracing itself adds to every single function call. This column is the main result of the benchmark.

Baseline rows only show the pure workload cost per call (no instrumentation).

| Variant | Platform | Mean step (µs) | Methods/step | Per-method (ns) = step / methods | Overhead/method (ns) = Δ vs baseline |
|---|---|---:|---:|---:|---:|
| baseline | JVM | 12.35 | 21892 | 0.6 | N/A |
| baseline | JS | 101.09 | 21892 | 4.6 | N/A |
| baseline | Native | 10.57 | 21892 | 0.5 | N/A |
| otel | JVM | 22,558.75 | 21892 | 1,030.5 | 1,029.9 |
| otel-proto | JVM | 14,387.94 | 21892 | 657.2 | 656.7 |
| otel-proto-sampler | JVM | 13,555.46 | 21892 | 619.2 | 618.6 |
| otel-proto-timesource | JVM | 13,562.91 | 21892 | 619.5 | 619.0 |
| otel-proto-anchored | JVM | 14,073.88 | 21892 | 642.9 | 642.3 |
| otel-proto-fastbatch | JVM | 8,907.49 | 21892 | 406.9 | 406.3 |
| otel-proto-combined | JVM | 8,115.93 | 21892 | 370.7 | 370.2 |
| otel | JS | 131,334.60 | 21892 | 5,999.2 | 5,994.6 |
| otel-proto | JS | 130,144.90 | 21892 | 5,944.9 | 5,940.2 |
| otel-proto-sampler | JS | 126,825.95 | 21892 | 5,793.3 | 5,788.6 |
| otel-proto-timesource | JS | 145,861.38 | 21892 | 6,662.8 | 6,658.2 |
| otel-proto-anchored | JS | 127,200.12 | 21892 | 5,810.3 | 5,805.7 |
| otel-proto-fastbatch | JS | 86,349.14 | 21892 | 3,944.3 | 3,939.7 |
| otel-proto-combined | JS | 84,138.97 | 21892 | 3,843.4 | 3,838.7 |
| otel | Native | 98,925.45 | 21892 | 4,518.8 | 4,518.3 |
| otel-proto | Native | 85,380.08 | 21892 | 3,900.1 | 3,899.6 |
| otel-proto-sampler | Native | 84,421.38 | 21892 | 3,856.3 | 3,855.8 |
| otel-proto-timesource | Native | 85,103.88 | 21892 | 3,887.4 | 3,887.0 |
| otel-proto-anchored | Native | 84,050.68 | 21892 | 3,839.3 | 3,838.9 |
| otel-proto-fastbatch | Native | 41,211.22 | 21892 | 1,882.5 | 1,882.0 |
| otel-proto-combined | Native | 39,732.98 | 21892 | 1,815.0 | 1,814.5 |

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
| otel-proto-fastbatch | JVM | 21,892,000 | 21,892,010 | 0 | 14,200,675 | 7,691,335 | 64.87 | LOSS |
| otel-proto-combined | JVM | 21,892,000 | 21,892,010 | 0 | 14,412,189 | 7,479,821 | 65.83 | LOSS |
| otel | JS | 21,892,000 | 21,892,010 | 325,632 | 21,892,010 | 0 | 100.00 | OK (false-fail: 325,632) |
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
| baseline | JVM | 2,451.91 | 20.84 | 12.65 | 12.37 | 12.56 | 12.33 | 12.49 | 12.36 | 12.23 | 11.92 |
| baseline | JS | 1,176.44 | 348.92 | 224.53 | 89.32 | 105.49 | 105.41 | 85.32 | 101.25 | 101.35 | 101.21 |
| baseline | Native | 65.36 | 10.70 | 10.60 | 10.58 | 10.60 | 10.57 | 10.57 | 10.56 | 10.56 | 10.56 |
| otel | JVM | 171,776.15 | 62,927.03 | 50,334.59 | 31,673.96 | 37,795.94 | 39,441.21 | 42,933.05 | 46,111.37 | 19,349.35 | 14,973.05 |
| otel-proto | JVM | 103,758.36 | 32,861.07 | 25,410.58 | 16,885.30 | 14,750.58 | 13,792.93 | 14,612.46 | 15,028.66 | 15,273.23 | 13,734.77 |
| otel-proto-sampler | JVM | 103,169.42 | 36,598.48 | 22,312.61 | 15,456.86 | 15,384.83 | 13,088.14 | 14,362.51 | 15,147.06 | 13,405.61 | 13,392.01 |
| otel-proto-timesource | JVM | 98,360.17 | 31,666.47 | 23,517.82 | 15,645.38 | 14,495.98 | 13,742.78 | 14,017.56 | 14,284.24 | 14,081.43 | 12,699.76 |
| otel-proto-anchored | JVM | 103,833.00 | 34,951.97 | 22,923.71 | 16,604.67 | 14,620.04 | 14,701.78 | 15,759.57 | 13,934.84 | 14,454.85 | 14,104.84 |
| otel-proto-fastbatch | JVM | 107,962.69 | 52,614.31 | 19,356.91 | 12,169.64 | 10,478.58 | 9,235.18 | 10,293.99 | 9,781.18 | 8,862.87 | 8,316.71 |
| otel-proto-combined | JVM | 108,908.99 | 52,480.39 | 16,545.55 | 15,867.73 | 8,844.51 | 7,939.66 | 8,351.58 | 8,755.97 | 8,087.61 | 6,881.01 |
| otel | JS | 185,995.71 | 143,391.85 | 130,991.38 | 127,478.87 | 127,236.04 | 127,359.26 | 126,278.57 | 128,469.31 | 130,485.51 | 147,433.46 |
| otel-proto | JS | 185,433.97 | 143,913.60 | 130,670.32 | 133,424.96 | 128,541.44 | 126,306.54 | 127,757.73 | 127,913.71 | 129,553.63 | 177,448.26 |
| otel-proto-sampler | JS | 185,882.41 | 141,032.29 | 127,025.76 | 125,810.53 | 125,272.15 | 123,948.59 | 127,019.82 | 126,254.66 | 126,794.17 | 125,813.13 |
| otel-proto-timesource | JS | 197,284.54 | 158,440.61 | 144,666.27 | 142,726.27 | 142,425.23 | 140,343.52 | 141,244.75 | 142,612.58 | 144,765.08 | 144,397.09 |
| otel-proto-anchored | JS | 186,307.85 | 141,926.18 | 127,365.54 | 125,918.32 | 125,126.14 | 123,315.39 | 125,546.29 | 124,987.39 | 126,807.55 | 166,714.93 |
| otel-proto-fastbatch | JS | 125,879.01 | 99,493.92 | 88,383.68 | 86,381.05 | 84,526.69 | 83,287.29 | 108,944.32 | 83,580.80 | 85,824.97 | 85,694.63 |
| otel-proto-combined | JS | 122,315.64 | 97,169.03 | 82,539.36 | 82,930.43 | 81,802.13 | 80,743.19 | 104,560.54 | 80,959.59 | 83,414.27 | 85,812.86 |
| otel | Native | 98,228.15 | 98,512.13 | 100,984.89 | 99,894.09 | 97,088.69 | 93,865.73 | 90,773.94 | 90,328.86 | 98,722.55 | 102,246.46 |
| otel-proto | Native | 74,518.98 | 73,503.37 | 73,876.08 | 73,944.60 | 78,022.70 | 80,457.61 | 80,650.57 | 82,851.69 | 85,399.87 | 84,799.43 |
| otel-proto-sampler | Native | 74,353.50 | 74,883.83 | 72,509.65 | 75,765.81 | 77,745.89 | 80,762.50 | 83,792.17 | 85,621.14 | 81,883.79 | 84,651.15 |
| otel-proto-timesource | Native | 73,251.69 | 72,680.32 | 72,457.18 | 75,512.05 | 74,489.08 | 79,217.91 | 82,162.80 | 83,396.51 | 85,991.99 | 83,871.21 |
| otel-proto-anchored | Native | 73,922.74 | 77,875.82 | 77,048.92 | 78,630.66 | 80,617.56 | 77,690.53 | 82,124.38 | 83,384.22 | 85,261.20 | 86,804.76 |
| otel-proto-fastbatch | Native | 41,043.88 | 42,649.41 | 42,909.55 | 40,297.01 | 40,831.64 | 41,798.73 | 40,019.22 | 41,012.72 | 40,071.36 | 40,898.33 |
| otel-proto-combined | Native | 39,506.92 | 37,469.24 | 36,959.58 | 39,280.95 | 37,209.23 | 39,238.65 | 36,793.67 | 38,793.08 | 39,251.43 | 43,198.49 |
