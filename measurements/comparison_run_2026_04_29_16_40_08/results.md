# Benchmark Results (2026_04_29_16_40_08)

## Parameters
- **Warmup Iterations:** 5
- **Run Iterations:** 20
- **Clean Build:** True

## System Information
- **OS:** Microsoft Windows 11 Home 10.0.26200 64-Bit
- **CPU:** AMD Ryzen 7 5800X 8-Core Processor (8 Cores / 16 Logical Processors)
- **RAM:** 31.93 GB
- **Java Version:** 22.0.1 ("22.0.1")
- **Node Version:** v18.14.2

## Hardware Overview Details
- **Device:** Gigabyte Technology Co., Ltd. - B550 AORUS ELITE V2
- **Git Branch:** LB_otel_analysis

## Execution Summary

| Executable | Iterations | Mean (ms) | Median (ms) | Min (ms) | Max (ms) | StdDev |
|------------|------------|-----------|-------------|----------|----------|--------|
| k-perf JVM | 20 | 636,40 | 635,50 | 538 | 730 | 31,44 |
| otel JVM | 20 | 3 679,65 | 3 735,00 | 1 855 | 4 460 | 464,10 |
| otel-proto JVM | 20 | 1 238,55 | 1 237,50 | 1 165 | 1 332 | 43,24 |
| k-perf JS (Node) | 20 | 1 418,60 | 1 429,50 | 1 299 | 1 584 | 79,57 |
| otel JS (Node) | 20 | 13 755,70 | 13 765,50 | 13 370 | 14 166 | 170,18 |
| otel-proto JS (Node) | 20 | 9 079,45 | 9 075,00 | 8 960 | 9 332 | 86,89 |
| k-perf Native (Win) | 20 | 809,00 | 819,00 | 712 | 918 | 45,74 |
| otel Native (Win) | 20 | 3 584,80 | 3 568,00 | 3 507 | 3 817 | 68,87 |
| otel-proto Native (Win) | 20 | 3 001,30 | 2 997,00 | 2 934 | 3 095 | 44,74 |
