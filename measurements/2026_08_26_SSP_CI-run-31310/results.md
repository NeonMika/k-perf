# Benchmark Results (2026_08_26_17_43_58, CI run 31310)

This document contains the results of one full execution of the k-perf/OTel comparison benchmark. The benchmark measures how much runtime overhead different tracing implementations add to a Kotlin Multiplatform program, on three platforms: JVM, JavaScript (Node.js), and a native binary.

Terminology used throughout this document:

- A **variant** is one tracing implementation. `baseline` is the identical program with no tracing at all.
- A **step** is one call of the workload function (`fibonacci(20)`). The duration of every step is measured individually.
- A **run** is one complete program execution containing 100 steps. Every variant is executed 10 times, each time in a fresh process, so results do not depend on a single lucky or unlucky execution.
- The first 20 steps of every run are **warmup**. This is a configurable cutoff (`-WarmupCount` defaults to 20), not a fixed constant.
- Headline step statistics first exclude warmup steps, then discard the lowest 1% and highest 1% of the pooled durations, rounding the number removed from each tail up. `steps.csv` and the median-curve analysis retain every step timing.

## Parameters
- **Warmup steps/run (excluded from headline stats):** 20
- **Run Iterations:** 10
- **Step Count (workload calls per process):** 100
- **Measured steps per run:** 80
- **Outliers discarded from each tail of headline step statistics:** 1% (rounded up, capped to preserve at least one observation)
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
- **Mean step / Step median / Step stddev (µs)**: the arithmetic mean, middle value, and sample standard deviation of the same trimmed population. For each row, the first 20 steps of every successful run are excluded, then the pooled durations are sorted and the lowest and highest 1% are discarded (the count per tail is rounded up, but capped so at least one observation remains). With all 10 runs successful, 800 non-warmup observations become 784 analyzed observations. Exact counts are stored as `StepNonWarmupCount`, `StepOutliersPerTail`, and `StepAnalyzedCount` in `results.json`. A large stddev means retained step times fluctuated strongly from step to step or run to run.

| Executable | Iterations | Total mean (ms) | Total median (ms) | Mean step (µs) | Step median (µs) | Step stddev (µs) |
|------------|-----------:|----------------:|------------------:|---------------:|-----------------:|-----------------:|
| baseline JVM | 10 | 10.59 | 10.50 | 12.84 | 12.07 | 2.13 |
| baseline JS (Node) | 10 | 15.09 | 14.92 | 105.67 | 104.71 | 13.06 |
| baseline Native (Linux) | 10 | 1.21 | 1.17 | 10.66 | 10.47 | 0.72 |
| otel JVM | 10 | 3,004.66 | 2,974.98 | 25,878.92 | 21,146.66 | 12,953.26 |
| otel-proto JVM | 10 | 1,592.80 | 1,583.68 | 14,255.40 | 13,369.75 | 2,510.56 |
| otel-proto-sampler JVM | 10 | 1,542.55 | 1,545.21 | 13,836.11 | 13,030.16 | 2,479.56 |
| otel-proto-timesource JVM | 10 | 1,624.13 | 1,613.43 | 14,596.02 | 13,872.83 | 2,668.61 |
| otel-proto-anchored JVM | 10 | 1,607.02 | 1,616.45 | 14,357.25 | 13,499.47 | 2,560.46 |
| otel-proto-fastbatch JVM | 10 | 1,237.73 | 1,244.41 | 10,190.66 | 9,045.25 | 4,706.55 |
| otel-proto-combined JVM | 10 | 1,194.85 | 1,200.34 | 9,584.58 | 7,938.76 | 5,956.27 |
| otel JS (Node) | 10 | 13,144.92 | 13,127.70 | 130,461.12 | 129,006.35 | 8,550.55 |
| otel-proto JS (Node) | 10 | 12,985.88 | 12,973.95 | 129,156.73 | 128,839.33 | 3,960.02 |
| otel-proto-sampler JS (Node) | 10 | 12,766.58 | 12,753.91 | 126,861.48 | 126,700.24 | 2,698.83 |
| otel-proto-timesource JS (Node) | 10 | 14,644.97 | 14,645.80 | 145,322.91 | 143,898.36 | 8,641.59 |
| otel-proto-anchored JS (Node) | 10 | 12,793.20 | 12,802.27 | 127,078.42 | 126,566.99 | 4,226.29 |
| otel-proto-fastbatch JS (Node) | 10 | 8,659.84 | 8,654.61 | 85,999.05 | 85,649.25 | 2,995.00 |
| otel-proto-combined JS (Node) | 10 | 8,489.10 | 8,485.65 | 84,349.99 | 83,967.28 | 3,045.89 |
| otel Native (Linux) | 10 | 10,344.61 | 10,321.41 | 103,387.85 | 101,621.51 | 10,714.66 |
| otel-proto Native (Linux) | 10 | 8,412.31 | 8,372.61 | 85,385.76 | 84,449.28 | 5,760.90 |
| otel-proto-sampler Native (Linux) | 10 | 8,304.16 | 8,288.13 | 84,258.18 | 83,228.23 | 5,436.61 |
| otel-proto-timesource Native (Linux) | 10 | 8,425.97 | 8,372.37 | 85,693.05 | 84,430.98 | 6,012.01 |
| otel-proto-anchored Native (Linux) | 10 | 8,406.42 | 8,359.17 | 85,198.49 | 83,756.00 | 6,069.96 |
| otel-proto-fastbatch Native (Linux) | 10 | 4,172.11 | 4,118.00 | 41,153.66 | 40,471.69 | 5,852.07 |
| otel-proto-combined Native (Linux) | 10 | 3,928.88 | 3,908.41 | 39,044.84 | 38,080.95 | 6,476.90 |

## Per-method timings

The step times from above, converted into the cost of one traced function call:

- **Per-method (ns) = step / methods**: the mean step time divided by the 21892 function calls in a step, i.e. the average total cost of one function call, including the workload's own computation.
- **Overhead/method (ns) = Δ vs baseline**: the same value minus the baseline's value on the same platform, i.e. what tracing itself adds to every single function call. This column is the main result of the benchmark.

Baseline rows only show the pure workload cost per call (no instrumentation).

| Variant | Platform | Mean step (µs) | Methods/step | Per-method (ns) = step / methods | Overhead/method (ns) = Δ vs baseline |
|---|---|---:|---:|---:|---:|
| baseline | JVM | 12.84 | 21892 | 0.6 | N/A |
| baseline | JS | 105.67 | 21892 | 4.8 | N/A |
| baseline | Native | 10.66 | 21892 | 0.5 | N/A |
| otel | JVM | 25,878.92 | 21892 | 1,182.1 | 1,181.5 |
| otel-proto | JVM | 14,255.40 | 21892 | 651.2 | 650.6 |
| otel-proto-sampler | JVM | 13,836.11 | 21892 | 632.0 | 631.4 |
| otel-proto-timesource | JVM | 14,596.02 | 21892 | 666.7 | 666.1 |
| otel-proto-anchored | JVM | 14,357.25 | 21892 | 655.8 | 655.2 |
| otel-proto-fastbatch | JVM | 10,190.66 | 21892 | 465.5 | 464.9 |
| otel-proto-combined | JVM | 9,584.58 | 21892 | 437.8 | 437.2 |
| otel | JS | 130,461.12 | 21892 | 5,959.3 | 5,954.5 |
| otel-proto | JS | 129,156.73 | 21892 | 5,899.7 | 5,894.9 |
| otel-proto-sampler | JS | 126,861.48 | 21892 | 5,794.9 | 5,790.1 |
| otel-proto-timesource | JS | 145,322.91 | 21892 | 6,638.2 | 6,633.3 |
| otel-proto-anchored | JS | 127,078.42 | 21892 | 5,804.8 | 5,800.0 |
| otel-proto-fastbatch | JS | 85,999.05 | 21892 | 3,928.3 | 3,923.5 |
| otel-proto-combined | JS | 84,349.99 | 21892 | 3,853.0 | 3,848.2 |
| otel | Native | 103,387.85 | 21892 | 4,722.6 | 4,722.1 |
| otel-proto | Native | 85,385.76 | 21892 | 3,900.3 | 3,899.8 |
| otel-proto-sampler | Native | 84,258.18 | 21892 | 3,848.8 | 3,848.3 |
| otel-proto-timesource | Native | 85,693.05 | 21892 | 3,914.4 | 3,913.9 |
| otel-proto-anchored | Native | 85,198.49 | 21892 | 3,891.8 | 3,891.3 |
| otel-proto-fastbatch | Native | 41,153.66 | 21892 | 1,879.8 | 1,879.4 |
| otel-proto-combined | Native | 39,044.84 | 21892 | 1,783.5 | 1,783.0 |

## OTel span delivery verification

Tracing data (spans, one per traced function call) is exported asynchronously in the background. A benchmark could therefore look fast simply because tracing data was silently thrown away instead of being processed. This table proves that did not happen: for every OTel variant, spans are counted at each step of the export pipeline (when they leave the plugin, when they arrive over the network, and when the Jaeger backend stores them), and all counts must match the number of spans the program generated. Unlike the headline timing statistics, span counts cover all 100 steps of every run: neither the warmup cutoff nor the 1% tail trimming applies to span delivery, because every step still creates and exports spans.

Column meanings:

- **Expected**: the number of spans the program generates. Each process creates `methods/step × StepCount` workload spans plus one span for the instrumented `main` function. With 10 run(s), that is `(methods/step × StepCount + 1) × RunCount` = (21892 × 100 + 1) × 10 = 21892010 spans per row.
- **Exported**: spans the plugin handed to the network.
- **Failed (client)**: spans whose export ended in an error on the program side.
- **Stored (Jaeger)**: spans the Jaeger backend saved. This is the ground truth for delivery.
- **Dropped (Jaeger)**: spans that reached Jaeger but were thrown away because the backend was overloaded.
- **Delivered (%)**: Stored divided by Expected. 100.00 means every single span arrived.
- **Status**: the verdict for the row. OK means everything arrived. LOSS means spans went missing somewhere. DUP means Jaeger received spans more than once. INVALID means the Jaeger backend crashed during this variant, so the whole row is untrustworthy. OK (false-fail: N) means everything arrived, but the plugin wrongly counted N spans as failed because it could not read the server's confirmation.

More detailed counters (e.g. the raw network-level receive counts) are stored in `results.json`.

| Variant | Platform | Expected | Exported | Failed (client) | Stored (Jaeger) | Dropped (Jaeger) | Delivered (%) | Status |
|---|---|---:|---:|---:|---:|---:|---:|:--|
| otel | JVM | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto | JVM | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-sampler | JVM | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-timesource | JVM | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-anchored | JVM | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-fastbatch | JVM | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-combined | JVM | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel | JS | 21,892,010 | 21,892,010 | 330,240 | 21,892,010 | 0 | 100.00 | OK (false-fail: 330,240) |
| otel-proto | JS | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-sampler | JS | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-timesource | JS | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-anchored | JS | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-fastbatch | JS | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-combined | JS | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel | Native | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto | Native | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-sampler | Native | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-timesource | Native | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-anchored | Native | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-fastbatch | Native | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |
| otel-proto-combined | Native | 21,892,010 | 21,892,010 | 0 | 21,892,010 | 0 | 100.00 | OK |

First swallowed export error per row (if any):
- **otel JS** first export error: `IllegalStateException: Content-Length mismatch: expected 21 bytes, but received 0 bytes`

## Per-step median curve (µs)

How the duration of a step changes over the lifetime of a process. The column `s0` is the first step of a run, `s1` the second, and so on. Each cell shows the median of that step's duration across the 10 runs. For example, `s0` is the median duration of the very first step, taken over all 10 runs. Reading a row from left to right therefore shows one representative process over time. Warmup steps and the values excluded by the 1% tail trimming remain in this analysis because the curve is specifically used to inspect warmup behavior. Only selected step indices are shown here. Every raw timing is stored in `steps.csv`; the full per-run arrays and derived median curves are also in `results.json::Results[*]`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s20 | s25 | s30 | s60 | s99 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 2,396.38 | 20.24 | 12.46 | 12.19 | 12.40 | 12.09 | 12.08 | 12.16 | 12.06 | 11.79 |
| baseline | JS | 1,201.46 | 348.28 | 228.53 | 91.03 | 107.73 | 107.62 | 90.13 | 106.01 | 104.40 | 103.98 |
| baseline | Native | 71.35 | 10.61 | 10.55 | 10.51 | 10.55 | 10.48 | 10.47 | 10.48 | 10.48 | 10.43 |
| otel | JVM | 162,554.57 | 56,003.80 | 44,911.64 | 39,820.01 | 35,456.62 | 67,159.94 | 33,117.38 | 28,245.60 | 20,902.67 | 18,558.92 |
| otel-proto | JVM | 103,324.55 | 31,514.08 | 20,459.59 | 16,336.66 | 13,789.04 | 13,206.69 | 13,784.20 | 15,336.89 | 13,239.97 | 12,476.55 |
| otel-proto-sampler | JVM | 105,000.77 | 34,023.84 | 23,132.73 | 15,520.38 | 14,075.68 | 13,022.40 | 13,016.83 | 12,277.18 | 13,685.56 | 12,494.93 |
| otel-proto-timesource | JVM | 95,444.16 | 42,483.00 | 22,897.76 | 15,835.93 | 14,694.13 | 14,813.49 | 15,700.03 | 13,657.54 | 14,930.96 | 14,414.57 |
| otel-proto-anchored | JVM | 102,804.20 | 33,200.95 | 22,048.08 | 16,309.49 | 14,020.27 | 13,965.20 | 13,432.58 | 13,631.23 | 13,350.48 | 12,299.75 |
| otel-proto-fastbatch | JVM | 106,816.92 | 43,782.54 | 18,309.90 | 11,799.68 | 11,441.39 | 9,028.45 | 9,727.77 | 9,185.10 | 8,366.12 | 8,303.87 |
| otel-proto-combined | JVM | 107,086.78 | 41,616.10 | 19,338.31 | 13,101.70 | 9,444.22 | 9,839.68 | 7,207.66 | 8,215.65 | 9,186.10 | 7,669.78 |
| otel | JS | 184,321.20 | 143,100.45 | 130,994.28 | 127,336.91 | 126,822.98 | 126,808.29 | 126,334.57 | 128,258.35 | 130,246.78 | 165,039.79 |
| otel-proto | JS | 180,825.33 | 141,270.33 | 128,464.61 | 126,187.72 | 127,199.85 | 126,502.87 | 127,134.80 | 128,144.25 | 130,841.26 | 164,412.08 |
| otel-proto-sampler | JS | 179,519.48 | 140,282.11 | 125,054.49 | 126,434.02 | 124,601.80 | 125,044.18 | 125,008.70 | 125,878.94 | 127,231.98 | 126,112.39 |
| otel-proto-timesource | JS | 200,085.26 | 158,704.02 | 145,274.04 | 143,232.20 | 142,198.17 | 140,516.33 | 141,502.43 | 142,454.06 | 144,473.58 | 144,295.96 |
| otel-proto-anchored | JS | 181,598.98 | 140,242.59 | 127,155.31 | 125,565.69 | 124,987.43 | 123,212.68 | 125,844.40 | 125,915.78 | 127,317.88 | 159,285.57 |
| otel-proto-fastbatch | JS | 123,933.98 | 97,030.19 | 88,145.46 | 84,928.92 | 83,717.05 | 82,911.95 | 105,801.95 | 83,002.03 | 85,261.82 | 88,129.19 |
| otel-proto-combined | JS | 118,879.80 | 94,655.49 | 84,187.32 | 83,457.21 | 81,884.54 | 80,895.04 | 105,813.79 | 81,445.05 | 83,764.12 | 85,733.00 |
| otel | Native | 99,085.47 | 105,509.56 | 104,613.05 | 107,512.10 | 102,235.14 | 95,279.80 | 99,995.05 | 96,493.13 | 98,783.03 | 104,899.37 |
| otel-proto | Native | 72,346.52 | 72,959.97 | 76,243.71 | 73,928.39 | 76,995.48 | 78,697.48 | 81,328.94 | 87,163.34 | 85,598.54 | 84,587.49 |
| otel-proto-sampler | Native | 72,165.29 | 72,960.02 | 74,610.16 | 73,893.15 | 76,040.94 | 78,623.60 | 79,405.36 | 80,662.53 | 82,663.28 | 86,808.40 |
| otel-proto-timesource | Native | 72,520.73 | 72,944.93 | 74,350.51 | 73,008.80 | 78,313.93 | 77,713.61 | 84,086.09 | 81,592.43 | 83,073.20 | 85,098.75 |
| otel-proto-anchored | Native | 72,520.13 | 73,688.07 | 73,873.64 | 77,862.05 | 78,732.98 | 77,686.26 | 82,413.93 | 83,591.98 | 83,542.64 | 83,494.90 |
| otel-proto-fastbatch | Native | 41,206.44 | 40,173.53 | 41,749.77 | 41,316.33 | 41,631.55 | 41,989.55 | 40,287.60 | 40,562.26 | 38,225.74 | 40,890.62 |
| otel-proto-combined | Native | 38,246.34 | 38,148.91 | 35,796.50 | 38,570.32 | 37,334.69 | 37,921.10 | 38,695.84 | 36,620.07 | 38,521.16 | 38,275.07 |
