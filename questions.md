# Progress Summary
- **Benchmarking Script Refactoring:** Consolidated, parameterized, and stabilized the comparison script (`benchmarking/kperf-otel-comparison.ps1`).
- **JS Target Execution Fixed:** Resolved Node.js/Yarn dependency resolution issues for the OpenTelemetry JS target (`@js-joda/core`).
- **Scientific OS Isolation:** Implemented dynamic Jaeger Docker container toggling to entirely prevent "noisy neighbor" CPU/RAM interference during `k-perf` baseline runs.
- **Asynchronous Telemetry Capture:** Enforced strict regex parsing to capture the exact OTel SDK asynchronous `Flush finished` hooks, guaranteeing complete network serialization overhead is included in the benchmarks.
- **Automated Reporting:** Added dynamic Markdown (`results.md`) and JSON generation, organized by timestamped runs.

---

# Questions for Supervisor

## 1. Docker Container Handling Strategy
**Context:** To ensure a mathematically fair baseline for the `k-perf` compiler plugin (which writes to file I/O), our benchmark script explicitly stops the Jaeger Docker container during `k-perf` runs to free up idle OS cache/CPU resources. The script then securely boots Jaeger back up specifically when the OpenTelemetry plugin is tested, strictly forcing real network TCP transfers.
**Question:** Does this isolated container toggling strategy align with your expectations for a scientifically plausible configuration?

## 2. Native Target Compatibility (Windows `mingwX64` vs Linux)
**Context:** We want to compare the plugins completely natively. However, the current Kotlin Multiplatform port of the OpenTelemetry SDK (`dcxp/opentelemetry-kotlin`) and its underlying HTTP Ktor exporters do not natively compile or run on Windows Native (`mingwX64`) due to deep C++ SDK limitations and missing standard library sysroots. Because of this, the `otel-plugin` is currently restricted to JVM, JS, and `linuxX64`. 
**Question:** How should we proceed with benchmarking the Native implementation? Should we transition to a strictly Linux/WSL execution environment for Native comparisons, or should we temporarily drop Windows Native from the comparative scope?

## 3. Implementing the Protobuf Exporter
**Context:** The initial benchmarks have overwhelmingly proven our hypothesis for **Performance Issue #2**. The current HTTP JSON exporter on the Node.js target takes >10 seconds due to massive serialization/network overhead, compared to `k-perf`'s 1.5 seconds.
**Question:** Should our immediate next technical step be investigating and swapping out the JSON formatting for a Multiplatform-compatible Protobuf serialization layer to bridge this gap?
