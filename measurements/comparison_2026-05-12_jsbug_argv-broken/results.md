# Benchmark Results (2026_05_12_17_23_25)

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
| JS | 1 |
| Native | 179 |

## Execution Summary

Total time = wall-clock of the whole process (warmup + StepCount steps + plugin teardown).
Step time = mean across all flat per-step samples (RunCount Ã— StepCount samples).

| Executable | Iterations | Total mean (ms) | Total median (ms) | Step mean (Âµs) | Step median (Âµs) | Step stddev (Âµs) |
|------------|-----------:|----------------:|------------------:|---------------:|-----------------:|-----------------:|
| baseline JVM | 20 | 28.38 | 27.65 | 60.16 | 5.00 | 642.59 |
| k-perf JVM | 20 | 170.84 | 168.23 | 1,064.12 | 830.00 | 1,263.22 |
| otel JVM | 20 | 350.91 | 346.74 | 2,217.22 | 1,129.00 | 4,388.31 |
| otel-proto JVM | 20 | 421.34 | 408.05 | 2,686.02 | 880.50 | 5,546.54 |
| otel-proto-timesource JVM | 20 | 437.05 | 427.74 | 2,790.79 | 892.50 | 5,660.03 |
| baseline JS (Node) | 20 | 5.37 | 5.34 | 1,014.50 | 986.50 | 78.79 |
| k-perf JS (Node) | 20 | 16.37 | 16.29 | 12,233.75 | 12,222.50 | 634.72 |
| otel JS (Node) | 20 | 15.58 | 14.92 | 11,584.45 | 10,982.00 | 1,340.15 |
| otel-proto JS (Node) | 20 | 15.41 | 15.01 | 11,347.05 | 11,007.00 | 816.84 |
| otel-proto-timesource JS (Node) | 20 | 16.08 | 15.83 | 12,063.05 | 11,856.00 | 787.73 |
| baseline Native (Win) | 20 | 1.43 | 1.44 | 2.28 | 0.00 | 24.57 |
| k-perf Native (Win) | 20 | 153.34 | 149.46 | 1,013.37 | 926.00 | 426.64 |
| otel Native (Win) | 20 | 702.69 | 695.72 | 4,670.03 | 693.00 | 5,572.51 |
| otel-proto Native (Win) | 20 | 603.24 | 600.94 | 4,010.38 | 579.00 | 4,754.12 |
| otel-proto-timesource Native (Win) | 20 | 592.95 | 587.11 | 3,942.51 | 559.50 | 4,734.06 |

## Overhead per instrumented method

overhead_ns_per_method = (step_mean_Âµs_instrumented âˆ’ step_mean_Âµs_baseline) Ã— 1000 / methods_per_step

methods_per_step is derived from the preserved k-perf trace under 	races/
(trace lines / 2 / StepCount). For the otel-* variants this is a lower bound
since those plugins also instrument the epeat { } lambda body itself
(~+1 method per step, ~0.5% systematic underestimate).

| Variant | Platform | Step mean (Âµs) | Baseline step (Âµs) | Methods/step | Overhead (ns/method) |
|---|---|---:|---:|---:|---:|
| k-perf | JVM | 1,064.12 | 60.16 | 179 | 5,608.7 |
| otel | JVM | 2,217.22 | 60.16 | 179 | 12,050.6 |
| otel-proto | JVM | 2,686.02 | 60.16 | 179 | 14,669.6 |
| otel-proto-timesource | JVM | 2,790.79 | 60.16 | 179 | 15,254.9 |
| k-perf | JS | 12,233.75 | 1,014.50 | 1 | 11,219,250.0 |
| otel | JS | 11,584.45 | 1,014.50 | 1 | 10,569,950.0 |
| otel-proto | JS | 11,347.05 | 1,014.50 | 1 | 10,332,550.0 |
| otel-proto-timesource | JS | 12,063.05 | 1,014.50 | 1 | 11,048,550.0 |
| k-perf | Native | 1,013.37 | 2.28 | 179 | 5,648.5 |
| otel | Native | 4,670.03 | 2.28 | 179 | 26,076.8 |
| otel-proto | Native | 4,010.38 | 2.28 | 179 | 22,391.6 |
| otel-proto-timesource | Native | 3,942.51 | 2.28 | 179 | 22,012.4 |

## Per-step times (JIT warmup curves)

Full per-step samples are in esults.json under Results[*].PerRunStepMicros
(shape: RunCount Ã— StepCount). To plot the JIT warmup curve, take the per-step
median across runs for a given (variant, platform).

Notes on interpretation:
- **JVM** HotSpot tiered compilation has two thresholds (~200 calls for C1,
  ~10k for C2). With ~180 user methods per step, expect two inflection
  points: one near step 1â€“2 and another near step 55.
- **JS** V8 uses Ignition â†’ Sparkplug â†’ Maglev â†’ Turbofan tiers; different
  curve shape than JVM.
- **Kotlin/Native** is AOT â€” expect flat from step 1.
- **otel-* variants** have monotonic upward drift superimposed on the JIT
  curve from the dcxp BatchSpanProcessor.removeSpanDataFromBatch O(nÂ²) bug
  (GEMINI.md Finding #1, 2026-05-04). For "steady-state" otel numbers, ignore
  steps before the C2 knee and average over the tail.
