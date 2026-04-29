# Benchmark Results (2026_03_23_21_17_55)

## Parameters
- **Warmup Iterations:** 3
- **Run Iterations:** 10
- **Clean Build:** False

## System Information
- **OS:** Microsoft Windows 11 Home 10.0.26200 64-Bit
- **CPU:** AMD Ryzen 7 5800X 8-Core Processor (8 Cores / 16 Logical Processors)
- **RAM:** 31.93 GB
- **Java Version:** Not available (Not available)
- **Node Version:** v18.14.2

## Hardware Overview Details
- **Device:** Gigabyte Technology Co., Ltd. - B550 AORUS ELITE V2
- **Git Branch:** LB_otel_analysis

## Execution Summary

| Executable | Iterations | Mean (ms) | Median (ms) | Min (ms) | Max (ms) | StdDev |
|------------|------------|-----------|-------------|----------|----------|--------|
| k-perf JVM | 10 | 588,00 | 626,50 | 521 | 646 | 57,29 |
| otel JVM | 10 | 789,30 | 785,50 | 742 | 839 | 27,04 |
| k-perf JS (Node) | 10 | 1 429,70 | 1 429,00 | 1 342 | 1 512 | 46,39 |
| otel JS (Node) | 10 | 2 238,10 | 2 239,00 | 2 222 | 2 262 | 11,81 |
