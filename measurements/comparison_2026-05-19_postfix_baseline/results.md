# Benchmark Results (2026_05_19_16_54_48)

## Parameters
- **Warmup Iterations:** 0
- **Run Iterations:** 20
- **Step Count (workload calls per process):** 150
- **Clean Build:** True
- **Run timeout (s):** 750

## System Information
- **OS:** Microsoft Windows 11 Pro 10.0.26200 64-bit
- **CPU:** Intel(R) Core(TM) i7-9750H CPU @ 2.60GHz (6 Cores / 12 Logical Processors)
- **RAM:** 31.74 GB
- **Java Version:** 21.0.10 ("21.0.10")
- **Node Version:** v24.15.0

## Hardware Overview Details
- **Device:** LENOVO - 20QN000DGE
- **Git Branch:** LB_otel_analysis

## Methods per step (k-perf trace, lines/2/StepCount)

| Platform | methods_per_step |
|---|---:|
| JVM | 179 |
| JS | 179 |
| Native | 179 |

## Execution Summary

Total = wall-clock per process. Step = mean across RunCount × StepCount samples.

| Executable | Iterations | Total mean (ms) | Total median (ms) | Step mean (µs) | Step median (µs) | Step stddev (µs) |
|------------|-----------:|----------------:|------------------:|---------------:|-----------------:|-----------------:|
| baseline JVM | 20 | 25.84 | 25.77 | 53.27 | 5.80 | 561.77 |
| k-perf JVM | 20 | 55.47 | 54.88 | 299.71 | 148.90 | 964.72 |
| otel JVM | 20 | 326.50 | 324.14 | 2,056.77 | 1,046.55 | 4,097.82 |
| otel-proto JVM | 20 | 135.62 | 132.98 | 800.32 | 428.15 | 1,295.74 |
| otel-proto-timesource JVM | 20 | 130.18 | 126.04 | 763.20 | 382.45 | 1,281.21 |
| baseline JS (Node) | 20 | 15.06 | 15.17 | 32.54 | 11.50 | 95.54 |
| k-perf JS (Node) | 20 | 95.36 | 95.67 | 579.27 | 459.25 | 509.71 |
| otel JS (Node) | 20 | 539.77 | 531.35 | 3,530.63 | 1,368.75 | 3,782.93 |
| otel-proto JS (Node) | 20 | 165.65 | 165.23 | 1,042.68 | 748.85 | 1,250.87 |
| otel-proto-timesource JS (Node) | 20 | 201.42 | 195.60 | 1,283.78 | 957.10 | 1,267.32 |
| baseline Native (Win) | 20 | 1.36 | 1.40 | 2.85 | 1.30 | 18.73 |
| k-perf Native (Win) | 20 | 31.47 | 30.47 | 202.62 | 172.35 | 73.21 |
| otel Native (Win) | 20 | 757.85 | 741.12 | 5,035.83 | 863.15 | 6,014.34 |
| otel-proto Native (Win) | 20 | 101.44 | 99.97 | 664.69 | 474.05 | 398.49 |
| otel-proto-timesource Native (Win) | 20 | 94.83 | 90.06 | 620.04 | 418.65 | 393.50 |

## Overhead per instrumented method

`overhead_ns/method = (step_ns_instrumented − step_ns_baseline) / methods_per_step`

- **Full**: mean over all steps. Cold-JVM-biased.
- **Steady**: mean from `SS-start` onward (first step ≤ 2× tail-median). Quote this.

methods/step from preserved k-perf trace (`traces/`); otel-* underestimate by ~0.5 % (skipped `repeat { }` lambda body).

| Variant | Platform | SS-start | Step mean (µs) | Baseline mean (µs) | Steady (µs) | Baseline steady (µs) | Methods/step | Overhead full (ns) | Overhead steady (ns) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|
| k-perf | JVM | 32 | 299.71 | 53.27 | 125.18 | 5.58 | 179 | 1,376.8 | 668.1 |
| otel | JVM | 38 | 2,056.77 | 53.27 | 845.58 | 5.58 | 179 | 11,192.7 | 4,692.8 |
| otel-proto | JVM | 22 | 800.32 | 53.27 | 415.96 | 5.58 | 179 | 4,173.5 | 2,292.6 |
| otel-proto-timesource | JVM | 23 | 763.20 | 53.27 | 368.14 | 5.58 | 179 | 3,966.1 | 2,025.5 |
| k-perf | JS | 6 | 579.27 | 32.54 | 487.80 | 17.11 | 179 | 3,054.4 | 2,629.6 |
| otel | JS | 3 | 3,530.63 | 32.54 | 3,153.86 | 17.11 | 179 | 19,542.4 | 17,523.7 |
| otel-proto | JS | 11 | 1,042.68 | 32.54 | 771.38 | 17.11 | 179 | 5,643.3 | 4,213.8 |
| otel-proto-timesource | JS | 7 | 1,283.78 | 32.54 | 994.72 | 17.11 | 179 | 6,990.2 | 5,461.5 |
| k-perf | Native | 0 | 202.62 | 2.85 | 184.97 | 1.24 | 179 | 1,116.0 | 1,026.4 |
| otel | Native | 0 | 5,035.83 | 2.85 | 4,734.55 | 1.24 | 179 | 28,117.2 | 26,443.1 |
| otel-proto | Native | 0 | 664.69 | 2.85 | 609.14 | 1.24 | 179 | 3,697.4 | 3,396.1 |
| otel-proto-timesource | Native | 0 | 620.04 | 2.85 | 549.57 | 1.24 | 179 | 3,447.9 | 3,063.3 |

## Per-step median curve (µs)

Sampled step indices across all runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 6,864.10 | 21.55 | 15.25 | 10.55 | 6.05 | 7.05 | 5.80 | 5.40 | 5.20 | 5.35 | 2.10 |
| k-perf | JVM | 11,636.10 | 1,323.20 | 1,261.35 | 1,077.05 | 544.85 | 283.35 | 199.35 | 104.70 | 166.35 | 78.55 | 85.05 |
| otel | JVM | 11,388.80 | 5,987.35 | 35,322.30 | 14,732.10 | 1,852.00 | 5,460.00 | 529.25 | 328.60 | 1,398.95 | 1,524.00 | 335.00 |
| otel-proto | JVM | 11,861.55 | 6,028.75 | 4,812.15 | 3,015.60 | 5,287.20 | 769.00 | 384.65 | 310.65 | 434.55 | 414.35 | 328.65 |
| otel-proto-timesource | JVM | 9,644.45 | 6,277.60 | 4,977.25 | 3,116.85 | 5,256.00 | 714.45 | 423.25 | 277.60 | 395.45 | 329.90 | 310.60 |
| baseline | JS | 987.55 | 141.00 | 95.15 | 74.50 | 50.10 | 63.20 | 8.45 | 7.75 | 6.55 | 6.25 | 9.75 |
| k-perf | JS | 5,500.90 | 1,746.25 | 1,692.85 | 2,753.05 | 1,128.15 | 556.65 | 479.60 | 358.00 | 404.35 | 418.60 | 451.10 |
| otel | JS | 10,796.60 | 7,148.65 | 29,875.80 | 10,425.95 | 1,122.70 | 7,122.05 | 922.10 | 938.35 | 6,930.65 | 7,514.70 | 834.35 |
| otel-proto | JS | 10,576.65 | 7,102.60 | 7,262.60 | 2,519.50 | 2,366.35 | 766.85 | 698.40 | 1,107.05 | 655.35 | 847.05 | 546.30 |
| otel-proto-timesource | JS | 11,300.30 | 7,749.30 | 6,279.15 | 3,164.85 | 1,358.00 | 987.80 | 898.05 | 1,143.80 | 840.15 | 912.70 | 739.90 |
| baseline | Native | 226.15 | 1.35 | 0.85 | 0.70 | 0.70 | 0.80 | 1.50 | 1.45 | 1.65 | 1.45 | 1.35 |
| k-perf | Native | 210.10 | 159.40 | 157.25 | 268.35 | 167.10 | 155.30 | 207.70 | 213.10 | 200.85 | 167.00 | 167.05 |
| otel | Native | 613.95 | 522.10 | 10,422.30 | 11,178.95 | 519.10 | 13,008.50 | 590.75 | 596.65 | 12,109.75 | 12,046.35 | 601.00 |
| otel-proto | Native | 596.55 | 473.85 | 614.70 | 583.80 | 713.15 | 463.10 | 1,471.10 | 472.25 | 468.10 | 479.80 | 463.65 |
| otel-proto-timesource | Native | 537.45 | 458.40 | 552.55 | 527.75 | 589.85 | 414.25 | 415.30 | 1,172.20 | 417.30 | 415.75 | 416.60 |

## Per-step times (JIT warmup curves)

> Curve shape: JVM C1≈step 1-2, C2≈step 55. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction.
