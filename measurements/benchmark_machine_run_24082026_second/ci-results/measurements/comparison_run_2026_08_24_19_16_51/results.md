# Benchmark Results (2026_08_24_19_16_51)

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
| baseline JVM | 10 | 10.96 | 10.98 | 12.48 | 12.32 | 2.25 |
| baseline JS (Node) | 10 | 14.55 | 14.59 | 102.20 | 104.58 | 20.53 |
| baseline Native (Linux) | 10 | 1.19 | 1.17 | 10.40 | 10.43 | 1.73 |
| otel JVM | 10 | 2,878.34 | 2,861.89 | 22,672.13 | 20,527.54 | 13,144.67 |
| otel-proto JVM | 10 | 1,613.44 | 1,603.76 | 13,709.47 | 13,520.14 | 2,844.99 |
| otel-proto-sampler JVM | 10 | 1,571.58 | 1,566.44 | 13,538.42 | 13,421.36 | 2,897.54 |
| otel-proto-timesource JVM | 10 | 1,662.59 | 1,687.15 | 14,205.91 | 14,095.91 | 2,921.08 |
| otel-proto-anchored JVM | 10 | 1,615.02 | 1,613.14 | 13,546.25 | 13,456.67 | 3,103.59 |
| otel-proto-fastbatch JVM | 10 | 1,208.55 | 1,208.15 | 8,779.52 | 8,737.95 | 6,352.80 |
| otel-proto-combined JVM | 10 | 1,162.84 | 1,169.49 | 7,710.82 | 7,644.66 | 7,748.32 |
| otel JS (Node) | 10 | 13,142.72 | 13,144.23 | 130,979.70 | 128,960.68 | 12,442.22 |
| otel-proto JS (Node) | 10 | 12,978.95 | 12,953.06 | 129,132.89 | 128,648.85 | 5,451.47 |
| otel-proto-sampler JS (Node) | 10 | 12,669.00 | 12,685.26 | 126,080.86 | 125,780.46 | 4,265.75 |
| otel-proto-timesource JS (Node) | 10 | 14,644.90 | 14,625.84 | 146,060.16 | 144,114.97 | 12,940.34 |
| otel-proto-anchored JS (Node) | 10 | 12,773.23 | 12,761.90 | 127,182.63 | 126,363.40 | 6,623.18 |
| otel-proto-fastbatch JS (Node) | 10 | 8,696.05 | 8,691.93 | 86,519.25 | 86,057.09 | 4,602.80 |
| otel-proto-combined JS (Node) | 10 | 8,446.06 | 8,461.79 | 84,078.81 | 83,646.22 | 4,242.03 |
| otel Native (Linux) | 10 | 10,264.98 | 10,268.13 | 100,521.74 | 100,379.76 | 12,326.19 |
| otel-proto Native (Linux) | 10 | 8,410.86 | 8,383.76 | 84,368.59 | 84,295.95 | 7,297.60 |
| otel-proto-sampler Native (Linux) | 10 | 8,319.74 | 8,290.70 | 83,459.80 | 83,453.88 | 7,333.55 |
| otel-proto-timesource Native (Linux) | 10 | 8,336.59 | 8,315.07 | 83,532.22 | 83,502.12 | 7,291.16 |
| otel-proto-anchored Native (Linux) | 10 | 8,471.57 | 8,493.53 | 84,843.56 | 84,699.08 | 7,693.88 |
| otel-proto-fastbatch Native (Linux) | 10 | 4,536.05 | 4,337.49 | 43,272.01 | 43,409.48 | 9,531.06 |
| otel-proto-combined Native (Linux) | 10 | 4,219.49 | 4,003.61 | 39,158.92 | 39,322.79 | 11,008.52 |

## Per-method timings

The step times from above, converted into the cost of one traced function call:

- **Per-method (ns) = step / methods**: the mean step time divided by the 21892 function calls in a step, i.e. the average total cost of one function call, including the workload's own computation.
- **Overhead/method (ns) = Δ vs baseline**: the same value minus the baseline's value on the same platform, i.e. what tracing itself adds to every single function call. This column is the main result of the benchmark.

Baseline rows only show the pure workload cost per call (no instrumentation).

| Variant | Platform | Mean step (µs) | Methods/step | Per-method (ns) = step / methods | Overhead/method (ns) = Δ vs baseline |
|---|---|---:|---:|---:|---:|
| baseline | JVM | 12.48 | 21892 | 0.6 | N/A |
| baseline | JS | 102.20 | 21892 | 4.7 | N/A |
| baseline | Native | 10.40 | 21892 | 0.5 | N/A |
| otel | JVM | 22,672.13 | 21892 | 1,035.6 | 1,035.1 |
| otel-proto | JVM | 13,709.47 | 21892 | 626.2 | 625.7 |
| otel-proto-sampler | JVM | 13,538.42 | 21892 | 618.4 | 617.8 |
| otel-proto-timesource | JVM | 14,205.91 | 21892 | 648.9 | 648.3 |
| otel-proto-anchored | JVM | 13,546.25 | 21892 | 618.8 | 618.2 |
| otel-proto-fastbatch | JVM | 8,779.52 | 21892 | 401.0 | 400.5 |
| otel-proto-combined | JVM | 7,710.82 | 21892 | 352.2 | 351.7 |
| otel | JS | 130,979.70 | 21892 | 5,983.0 | 5,978.3 |
| otel-proto | JS | 129,132.89 | 21892 | 5,898.6 | 5,894.0 |
| otel-proto-sampler | JS | 126,080.86 | 21892 | 5,759.2 | 5,754.6 |
| otel-proto-timesource | JS | 146,060.16 | 21892 | 6,671.9 | 6,667.2 |
| otel-proto-anchored | JS | 127,182.63 | 21892 | 5,809.5 | 5,804.9 |
| otel-proto-fastbatch | JS | 86,519.25 | 21892 | 3,952.1 | 3,947.4 |
| otel-proto-combined | JS | 84,078.81 | 21892 | 3,840.6 | 3,835.9 |
| otel | Native | 100,521.74 | 21892 | 4,591.7 | 4,591.2 |
| otel-proto | Native | 84,368.59 | 21892 | 3,853.9 | 3,853.4 |
| otel-proto-sampler | Native | 83,459.80 | 21892 | 3,812.3 | 3,811.9 |
| otel-proto-timesource | Native | 83,532.22 | 21892 | 3,815.7 | 3,815.2 |
| otel-proto-anchored | Native | 84,843.56 | 21892 | 3,875.6 | 3,875.1 |
| otel-proto-fastbatch | Native | 43,272.01 | 21892 | 1,976.6 | 1,976.1 |
| otel-proto-combined | Native | 39,158.92 | 21892 | 1,788.7 | 1,788.3 |

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
| otel | JS | 21,892,000 | 21,892,010 | 313,856 | 21,892,010 | 0 | 100.00 | OK (false-fail: 313,856) |
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
| otel-proto-fastbatch | Native | 21,892,000 | 21,892,010 | 512 | 21,892,010 | 0 | 100.00 | OK (false-fail: 512) |
| otel-proto-combined | Native | 21,892,000 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |

First swallowed export error per row (if any):
- **otel JS** first export error: `IllegalStateException: Content-Length mismatch: expected 21 bytes, but received 0 bytes`
- **otel-proto-fastbatch Native** first export error: `ClosedSendChannelException: Channel was closed`

## Per-step median curve (µs)

How the duration of a step changes over the lifetime of a process. The column `s0` is the first step of a run, `s1` the second, and so on. Each cell shows the median of that step's duration across the 10 runs. For example, `s0` is the median duration of the very first step, taken over all 10 runs. Reading a row from left to right therefore shows one representative process over time. Only selected step indices are shown here. The full curves are in `per_step_medians.csv` and `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s20 | s25 | s30 | s60 | s99 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 2,482.37 | 21.06 | 12.92 | 12.46 | 12.84 | 12.44 | 12.53 | 12.61 | 12.30 | 12.01 |
| baseline | JS | 1,182.54 | 347.45 | 216.72 | 87.62 | 106.91 | 105.83 | 84.87 | 103.51 | 104.86 | 102.34 |
| baseline | Native | 68.41 | 10.59 | 10.49 | 10.45 | 10.48 | 10.43 | 10.44 | 10.45 | 10.36 | 10.35 |
| otel | JVM | 156,617.56 | 61,009.09 | 57,158.83 | 28,666.36 | 38,936.80 | 43,963.32 | 51,452.43 | 31,589.01 | 18,543.52 | 14,477.26 |
| otel-proto | JVM | 101,148.93 | 33,482.76 | 22,359.22 | 16,629.71 | 14,885.38 | 13,815.98 | 15,486.59 | 14,491.23 | 13,630.74 | 13,731.89 |
| otel-proto-sampler | JVM | 102,111.00 | 33,786.75 | 22,354.57 | 15,617.68 | 13,662.24 | 12,608.79 | 13,439.08 | 13,002.80 | 13,539.90 | 11,931.14 |
| otel-proto-timesource | JVM | 97,468.24 | 38,984.00 | 24,785.99 | 16,557.50 | 14,998.17 | 15,560.61 | 17,400.37 | 14,845.00 | 14,634.64 | 13,290.18 |
| otel-proto-anchored | JVM | 104,719.68 | 35,604.66 | 22,455.91 | 16,115.34 | 14,523.59 | 14,721.91 | 13,303.13 | 14,699.10 | 14,748.64 | 12,505.80 |
| otel-proto-fastbatch | JVM | 110,226.11 | 45,826.85 | 17,113.80 | 12,775.05 | 9,996.33 | 9,157.65 | 10,392.04 | 10,056.88 | 8,316.69 | 7,744.50 |
| otel-proto-combined | JVM | 109,498.09 | 48,379.84 | 17,118.72 | 12,130.55 | 8,852.54 | 8,359.23 | 7,691.15 | 7,487.91 | 7,526.86 | 7,104.14 |
| otel | JS | 184,741.89 | 142,790.16 | 130,391.64 | 127,611.72 | 126,330.46 | 125,899.40 | 125,909.63 | 128,205.99 | 130,064.83 | 150,887.37 |
| otel-proto | JS | 183,702.16 | 143,094.50 | 129,956.57 | 139,680.67 | 126,188.37 | 124,640.79 | 127,870.36 | 127,689.18 | 129,329.70 | 145,227.11 |
| otel-proto-sampler | JS | 180,056.63 | 139,353.19 | 125,493.50 | 125,396.92 | 123,866.51 | 122,752.08 | 124,831.99 | 125,195.40 | 125,950.18 | 125,692.08 |
| otel-proto-timesource | JS | 196,766.47 | 158,843.96 | 144,347.69 | 142,855.86 | 141,683.11 | 140,134.18 | 140,769.44 | 142,526.58 | 145,378.84 | 144,839.49 |
| otel-proto-anchored | JS | 180,718.01 | 140,614.36 | 127,018.21 | 125,418.60 | 124,727.23 | 123,108.35 | 125,144.23 | 125,318.05 | 126,716.16 | 173,226.08 |
| otel-proto-fastbatch | JS | 123,810.06 | 98,161.07 | 88,306.70 | 85,228.88 | 83,655.59 | 82,819.90 | 108,215.74 | 83,572.46 | 85,606.06 | 86,993.60 |
| otel-proto-combined | JS | 120,286.97 | 96,129.47 | 81,986.42 | 82,666.47 | 81,318.21 | 81,051.18 | 103,419.77 | 80,997.17 | 83,628.87 | 85,218.24 |
| otel | Native | 100,195.12 | 101,766.20 | 108,444.89 | 104,345.75 | 99,888.05 | 95,113.78 | 97,641.75 | 100,538.22 | 103,640.50 | 106,247.22 |
| otel-proto | Native | 72,468.74 | 73,525.04 | 72,402.28 | 75,585.02 | 77,916.27 | 77,952.79 | 83,986.59 | 82,201.06 | 85,390.55 | 83,574.33 |
| otel-proto-sampler | Native | 69,154.78 | 70,778.16 | 71,442.81 | 73,792.09 | 79,417.65 | 77,634.03 | 81,391.16 | 82,047.44 | 84,565.74 | 83,450.38 |
| otel-proto-timesource | Native | 71,952.77 | 73,060.07 | 74,093.01 | 75,515.14 | 76,839.21 | 80,133.55 | 80,662.03 | 82,685.06 | 82,487.44 | 86,304.37 |
| otel-proto-anchored | Native | 72,477.87 | 72,774.97 | 75,410.76 | 76,769.79 | 79,054.97 | 81,277.81 | 83,935.76 | 85,348.97 | 83,775.75 | 86,414.08 |
| otel-proto-fastbatch | Native | 40,816.55 | 40,033.19 | 39,939.21 | 38,460.70 | 41,240.16 | 40,192.31 | 41,860.41 | 43,325.76 | 43,581.60 | 46,353.52 |
| otel-proto-combined | Native | 40,519.22 | 36,472.49 | 37,668.31 | 39,350.90 | 39,434.39 | 39,641.53 | 38,355.88 | 39,981.74 | 41,167.49 | 38,947.38 |
