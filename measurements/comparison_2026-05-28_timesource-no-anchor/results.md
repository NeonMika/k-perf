# Benchmark Results (2026_05_28_13_56_27)

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
| baseline JVM | 20 | 26.99 | 26.90 | 54.68 | 5.90 | 556.00 |
| k-perf JVM | 20 | 58.25 | 58.71 | 313.59 | 162.50 | 940.34 |
| otel JVM | 20 | 335.17 | 336.96 | 2,114.42 | 994.55 | 4,148.81 |
| otel-proto JVM | 20 | 131.54 | 132.41 | 775.61 | 429.75 | 1,288.16 |
| otel-proto-timesource JVM | 20 | 134.37 | 127.63 | 793.74 | 465.20 | 1,175.62 |
| otel-proto-anchored JVM | 20 | 131.90 | 125.97 | 772.49 | 401.70 | 1,378.56 |
| baseline JS (Node) | 20 | 13.29 | 13.14 | 30.11 | 9.00 | 97.45 |
| k-perf JS (Node) | 20 | 92.39 | 91.37 | 558.10 | 437.90 | 520.83 |
| otel JS (Node) | 20 | 530.36 | 527.76 | 3,469.66 | 1,337.00 | 3,690.77 |
| otel-proto JS (Node) | 20 | 169.56 | 166.90 | 1,067.78 | 768.70 | 1,222.02 |
| otel-proto-timesource JS (Node) | 20 | 195.37 | 193.54 | 1,248.32 | 955.20 | 1,218.67 |
| otel-proto-anchored JS (Node) | 20 | 161.30 | 157.30 | 1,011.25 | 723.70 | 1,206.43 |
| baseline Native (Win) | 20 | 1.22 | 1.07 | 2.74 | 0.80 | 19.64 |
| k-perf Native (Win) | 20 | 29.66 | 29.47 | 191.46 | 163.10 | 69.44 |
| otel Native (Win) | 20 | 722.24 | 714.33 | 4,801.02 | 848.35 | 5,709.74 |
| otel-proto Native (Win) | 20 | 95.50 | 93.14 | 627.00 | 482.55 | 314.76 |
| otel-proto-timesource Native (Win) | 20 | 93.44 | 93.79 | 610.53 | 431.25 | 336.71 |
| otel-proto-anchored Native (Win) | 20 | 101.37 | 100.93 | 665.57 | 483.30 | 348.29 |

## Overhead per instrumented method

`overhead_ns/method = (step_ns_instrumented − step_ns_baseline) / methods_per_step`

- **Full**: mean over all steps. Cold-JVM-biased.
- **Steady**: mean from `SS-start` onward (first step ≤ 2× tail-median). Quote this.
- **Envelope (P10)**: 10th percentile of steady-region per-step medians. Sawtooth-aware lower bound.

methods/step from preserved k-perf trace (`traces/`); otel-* underestimate by ~0.5 % (skipped `repeat { }` lambda body).

| Variant | Platform | SS-start | Step mean (µs) | Baseline mean (µs) | Steady (µs) | Baseline steady (µs) | Envelope P10 (µs) | Baseline envelope (µs) | Methods/step | Overhead full (ns) | Overhead steady (ns) | Overhead envelope (ns) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| k-perf | JVM | 29 | 313.59 | 54.68 | 144.34 | 5.78 | 97.80 | 3.02 | 179 | 1,446.4 | 774.1 | 529.5 |
| otel | JVM | 36 | 2,114.42 | 54.68 | 946.34 | 5.78 | 348.84 | 3.02 | 179 | 11,507.0 | 5,254.5 | 1,932.0 |
| otel-proto | JVM | 19 | 775.61 | 54.68 | 415.49 | 5.78 | 334.05 | 3.02 | 179 | 4,027.5 | 2,288.9 | 1,849.4 |
| otel-proto-timesource | JVM | 19 | 793.74 | 54.68 | 426.11 | 5.78 | 341.75 | 3.02 | 179 | 4,128.9 | 2,348.2 | 1,892.4 |
| otel-proto-anchored | JVM | 22 | 772.49 | 54.68 | 373.83 | 5.78 | 313.13 | 3.02 | 179 | 4,010.2 | 2,056.2 | 1,732.5 |
| k-perf | JS | 6 | 558.10 | 30.11 | 458.41 | 17.20 | 315.79 | 6.05 | 179 | 2,949.6 | 2,464.9 | 1,730.4 |
| otel | JS | 3 | 3,469.66 | 30.11 | 3,146.11 | 17.20 | 863.58 | 6.05 | 179 | 19,215.3 | 17,479.9 | 4,790.7 |
| otel-proto | JS | 11 | 1,067.78 | 30.11 | 780.64 | 17.20 | 645.86 | 6.05 | 179 | 5,797.1 | 4,265.0 | 3,574.4 |
| otel-proto-timesource | JS | 7 | 1,248.32 | 30.11 | 1,001.59 | 17.20 | 769.36 | 6.05 | 179 | 6,805.6 | 5,499.3 | 4,264.3 |
| otel-proto-anchored | JS | 12 | 1,011.25 | 30.11 | 724.02 | 17.20 | 526.91 | 6.05 | 179 | 5,481.2 | 3,948.7 | 2,909.8 |
| k-perf | Native | 0 | 191.46 | 2.74 | 177.36 | 0.83 | 153.95 | 0.70 | 179 | 1,054.3 | 986.2 | 856.1 |
| otel | Native | 0 | 4,801.02 | 2.74 | 4,539.34 | 0.83 | 504.26 | 0.70 | 179 | 26,806.0 | 25,354.8 | 2,813.2 |
| otel-proto | Native | 0 | 627.00 | 2.74 | 594.37 | 0.83 | 470.85 | 0.70 | 179 | 3,487.5 | 3,315.8 | 2,626.5 |
| otel-proto-timesource | Native | 0 | 610.53 | 2.74 | 555.24 | 0.83 | 409.98 | 0.70 | 179 | 3,395.5 | 3,097.3 | 2,286.5 |
| otel-proto-anchored | Native | 0 | 665.57 | 2.74 | 617.31 | 0.83 | 468.81 | 0.70 | 179 | 3,702.9 | 3,444.0 | 2,615.1 |

## Per-step median curve (µs)

Sampled step indices across 20 runs. otel-* sawtooth = BSP flushes. Full data in `per_step_medians.csv` / `results.json::Results[*].PerRunStepNanos`.

| Variant | Platform | s0 | s1 | s2 | s5 | s10 | s25 | s50 | s75 | s100 | s125 | s149 |
|---|---|---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---:|
| baseline | JVM | 6,816.20 | 21.15 | 15.55 | 12.35 | 5.95 | 7.55 | 5.90 | 5.40 | 5.40 | 5.25 | 2.35 |
| k-perf | JVM | 11,373.90 | 1,230.70 | 1,253.80 | 1,169.05 | 568.90 | 278.10 | 168.60 | 135.25 | 137.25 | 123.40 | 121.25 |
| otel | JVM | 11,435.70 | 6,043.55 | 33,921.00 | 15,032.00 | 1,868.35 | 5,157.55 | 656.65 | 455.55 | 1,409.75 | 1,517.85 | 323.50 |
| otel-proto | JVM | 11,796.10 | 6,155.50 | 4,846.75 | 3,021.50 | 4,994.70 | 739.95 | 403.35 | 344.35 | 549.15 | 375.45 | 341.30 |
| otel-proto-timesource | JVM | 9,070.00 | 6,149.45 | 4,945.90 | 3,046.15 | 5,082.75 | 700.80 | 382.50 | 386.10 | 696.65 | 327.35 | 402.60 |
| otel-proto-anchored | JVM | 12,551.80 | 6,176.40 | 4,689.25 | 2,974.00 | 1,910.95 | 529.10 | 482.05 | 279.20 | 439.90 | 313.80 | 308.80 |
| baseline | JS | 977.80 | 112.60 | 68.50 | 72.30 | 34.50 | 36.45 | 8.40 | 7.40 | 6.30 | 6.10 | 7.85 |
| k-perf | JS | 5,579.90 | 1,883.15 | 1,578.75 | 2,709.10 | 1,053.50 | 553.10 | 428.65 | 322.95 | 393.50 | 374.10 | 440.95 |
| otel | JS | 10,701.75 | 7,146.20 | 30,393.55 | 10,395.90 | 1,105.90 | 7,193.85 | 936.60 | 935.70 | 6,902.80 | 7,475.30 | 843.55 |
| otel-proto | JS | 10,621.75 | 7,371.45 | 7,567.80 | 2,525.45 | 2,361.60 | 791.55 | 699.20 | 1,165.60 | 664.50 | 843.95 | 559.70 |
| otel-proto-timesource | JS | 11,575.55 | 8,153.15 | 6,319.25 | 3,095.00 | 1,396.85 | 995.10 | 956.20 | 932.15 | 946.05 | 917.95 | 739.10 |
| otel-proto-anchored | JS | 10,680.20 | 6,506.45 | 8,057.15 | 1,999.55 | 1,368.85 | 773.60 | 662.85 | 839.05 | 629.70 | 678.75 | 507.25 |
| baseline | Native | 233.10 | 1.30 | 0.90 | 0.70 | 0.70 | 1.00 | 0.95 | 0.85 | 0.80 | 0.95 | 0.80 |
| k-perf | Native | 199.05 | 201.50 | 155.85 | 246.80 | 155.05 | 154.15 | 181.50 | 211.10 | 191.45 | 154.20 | 154.05 |
| otel | Native | 602.50 | 520.35 | 10,194.80 | 9,823.50 | 513.15 | 11,775.80 | 515.80 | 519.80 | 11,595.70 | 12,346.65 | 609.60 |
| otel-proto | Native | 601.45 | 508.55 | 637.40 | 610.35 | 718.25 | 480.70 | 1,259.10 | 477.25 | 477.85 | 469.70 | 469.85 |
| otel-proto-timesource | Native | 556.40 | 500.10 | 593.40 | 656.80 | 660.40 | 429.75 | 411.50 | 470.60 | 411.55 | 419.90 | 414.50 |
| otel-proto-anchored | Native | 603.35 | 533.20 | 669.90 | 652.95 | 788.90 | 469.10 | 1,471.50 | 474.40 | 478.50 | 469.45 | 480.45 |
> Curve shape: JVM C1≈step 1-2, C2≈step 55. JS V8 tiered. Native AOT (flat). otel-* drift + sawtooth = dcxp BSP/persistent-list interaction.
