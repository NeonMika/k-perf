# Benchmark Results (2026_04_29_20_42_09)

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
| k-perf JVM | 20 | 633,65 | 638,00 | 533 | 646 | 23,96 |
| otel JVM | 20 | 3 821,00 | 3 803,50 | 3 669 | 4 179 | 104,02 |
| otel-proto JVM | 20 | 1 261,65 | 1 254,50 | 1 184 | 1 403 | 46,00 |
| otel-proto-timesource JVM | 20 | 1 267,20 | 1 269,00 | 1 216 | 1 350 | 33,62 |
| k-perf JS (Node) | 20 | 1 466,05 | 1 462,00 | 1 344 | 1 552 | 57,80 |
| otel JS (Node) | 20 | 14 334,45 | 14 274,50 | 13 531 | 15 049 | 434,93 |
| otel-proto JS (Node) | 20 | 8 481,55 | 8 086,50 | 7 739 | 10 022 | 758,85 |
| otel-proto-timesource JS (Node) | 20 | 8 478,10 | 8 148,00 | 7 945 | 9 990 | 676,88 |
| k-perf Native (Win) | 20 | 825,85 | 840,00 | 730 | 857 | 39,38 |
| otel Native (Win) | 20 | 3 801,25 | 3 824,50 | 3 573 | 4 022 | 156,66 |
| otel-proto Native (Win) | 20 | 3 051,90 | 3 043,00 | 2 985 | 3 215 | 54,39 |
| otel-proto-timesource Native (Win) | 20 | 3 051,85 | 3 009,00 | 2 898 | 3 415 | 121,72 |
