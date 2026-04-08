# ⚡ k-perf

> **Automatic performance tracing for Kotlin Multiplatform — zero boilerplate, pure compiler magic.**

k-perf is a **Kotlin backend compiler plugin** that instruments your functions at the IR (Intermediate Representation) level to generate detailed execution traces. It works transparently across **JVM, JavaScript, and Native** targets without any changes to your source code.

This is a research project by the [SSW group at JKU Linz](https://ssw.jku.at/), originating from the **SSP 2024** (Symposium on Software Performance) paper.

---

## 🚦 CI Status

### Build & Tests

> Tests (JUnit 5 via kctfork) run inside the build workflows — there is no separate test workflow.

[![Build all on Windows](https://github.com/NeonMika/k-perf/actions/workflows/build-all-on-windows.yml/badge.svg)](https://github.com/NeonMika/k-perf/actions/workflows/build-all-on-windows.yml)
[![Build all on Ubuntu](https://github.com/NeonMika/k-perf/actions/workflows/build-all-on-ubuntu.yml/badge.svg)](https://github.com/NeonMika/k-perf/actions/workflows/build-all-on-ubuntu.yml)

[![Build all on Windows (separated)](https://github.com/NeonMika/k-perf/actions/workflows/build-all-on-windows-separated.yml/badge.svg)](https://github.com/NeonMika/k-perf/actions/workflows/build-all-on-windows-separated.yml)
[![Build all on Ubuntu (separated)](https://github.com/NeonMika/k-perf/actions/workflows/build-all-on-ubuntu-separated.yml/badge.svg)](https://github.com/NeonMika/k-perf/actions/workflows/build-all-on-ubuntu-separated.yml)

[![Build all on Windows (highly separated)](https://github.com/NeonMika/k-perf/actions/workflows/build-all-on-windows-highly-separated.yml/badge.svg)](https://github.com/NeonMika/k-perf/actions/workflows/build-all-on-windows-highly-separated.yml)
[![Build all on Ubuntu (highly separated)](https://github.com/NeonMika/k-perf/actions/workflows/build-all-on-ubuntu-highly-separated.yml/badge.svg)](https://github.com/NeonMika/k-perf/actions/workflows/build-all-on-ubuntu-highly-separated.yml)

### Benchmarks

> Benchmark workflows are triggered manually (`workflow_dispatch`) — badges show "no status" until a run is triggered.

[![Benchmark (Windows, Large)](https://github.com/NeonMika/k-perf/actions/workflows/benchmark-windows-large.yml/badge.svg)](https://github.com/NeonMika/k-perf/actions/workflows/benchmark-windows-large.yml)
[![Benchmark (Windows, Small)](https://github.com/NeonMika/k-perf/actions/workflows/benchmark-windows-small.yml/badge.svg)](https://github.com/NeonMika/k-perf/actions/workflows/benchmark-windows-small.yml)

[![Benchmark (Linux, Large)](https://github.com/NeonMika/k-perf/actions/workflows/benchmark-linux-large.yml/badge.svg)](https://github.com/NeonMika/k-perf/actions/workflows/benchmark-linux-large.yml)
[![Benchmark (Linux, Small)](https://github.com/NeonMika/k-perf/actions/workflows/benchmark-linux-small.yml/badge.svg)](https://github.com/NeonMika/k-perf/actions/workflows/benchmark-linux-small.yml)

### Publish

[![Maven Central k-perf](https://img.shields.io/maven-central/v/io.github.neonmika/k-perf.svg?label=k-perf)](https://central.sonatype.com/artifact/io.github.neonmika/k-perf)
[![Maven Central k-perf-plugin](https://img.shields.io/maven-central/v/io.github.neonmika.k-perf-plugin/io.github.neonmika.k-perf-plugin.gradle.plugin.svg?label=k-perf-plugin)](https://central.sonatype.com/namespace/io.github.neonmika.k-perf-plugin)
[![Maven Central KIRHelperKit](https://img.shields.io/maven-central/v/io.github.neonmika/KIRHelperKit.svg?label=KIRHelperKit)](https://central.sonatype.com/artifact/io.github.neonmika/KIRHelperKit)

---

## 📋 Table of Contents

- [CI Status](#-ci-status)
- [Add k-perf to Your Project](#-add-k-perf-to-your-project)
- [How It Works](#-how-it-works)
- [Repository Structure](#-repository-structure)
- [Quick Start](#-quick-start)
- [Plugins](#-plugins)
  - [k-perf](#k-perf-plugin)
  - [Instrumentation Overhead Analyzer](#instrumentation-overhead-analyzer-plugin)
- [KIRHelperKit](#-kirhelperkit)
- [KMP Examples](#-kmp-examples)
- [Analyzers](#-analyzers)
- [Benchmarking](#-benchmarking)
- [License](#-license)

---

## 🔌 Add k-perf to Your Project

> Available on [Maven Central](https://central.sonatype.com/artifact/io.github.neonmika/k-perf) — no local build needed.

**1. Configure your plugin repositories** (`settings.gradle.kts`):

```kotlin
pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}
```

**2. Apply the plugin and configure it** (`build.gradle.kts`):

```kotlin
plugins {
  kotlin("multiplatform") version "2.3.0"
  id("io.github.neonmika.k-perf-plugin") version "0.2.1"
}

kperf {
  enabled = true      // toggle instrumentation on/off
  flushEarly = false  // buffer until exit (faster) or flush on every write (safer)
  methods = ".*"      // regex filter on fully-qualified function names
}
```

That's all — rebuild your project and k-perf will instrument every function matching the `methods` filter. Trace and symbols files are written to the working directory on exit.

> 💡 See the full [configuration reference](#configuration-options) and [output file formats](#-how-it-works) below.

---

## 🔍 How It Works

k-perf hooks into the Kotlin compiler's IR backend. At compile time, it wraps every eligible function body in a `try/finally` block and injects timing calls — no runtime agent, no bytecode manipulation after the fact.

**At runtime**, each instrumented function:

1. 🟢 **On entry** — records the current time and writes `>;{methodId}` to the trace file
2. 🔴 **On exit** — measures elapsed microseconds and writes `<;{elapsedµs}` to the trace file
3. 🏁 **On `main` exit** — flushes both output files and prints their paths to stdout

**Output files** (written to the working directory):

| File | Format | Description |
|---|---|---|
| `trace_<platform>_<random>.txt` | Line-by-line | Call enter/exit events with timing |
| `symbols_<platform>_<random>.txt` | JSON | Maps method IDs to fully qualified names |

```
# trace file example
>;0        ← enter method 0 (main)
>;1        ← enter method 1 (GameOfLife.step)
<;42       ← exit method 1, elapsed 42 µs
<;105      ← exit method 0, elapsed 105 µs
```

```json
// symbols file example
{ "0": "main", "1": "game.gol.GameOfLife.step" }
```

File I/O uses **[`kotlinx-io`](https://github.com/Kotlin/kotlinx-io)** for cross-platform compatibility — the same API works on JVM, JS, and Native.

---

## 📁 Repository Structure

```
k-perf/
├── 🧰 KIRHelperKit/                          # IR utility library (published separately)
├── 🔌 plugins/
│   ├── k-perf/                               # Main performance tracing plugin
│   └── instrumentation-overhead-analyzer/    # Plugin overhead measurement tool
├── 🎮 kmp-examples/                          # 5 Game of Life KMP demo projects
│   ├── game-of-life-kmp-commonmain/          # Baseline (no instrumentation)
│   ├── game-of-life-kmp-commonmain-k-perf/   # k-perf instrumented (CommonMain)
│   ├── game-of-life-kmp-commonmain-ioa/      # IOA instrumented
│   ├── game-of-life-kmp-dedicatedmain/       # Baseline (per-platform main)
│   └── game-of-life-kmp-dedicatedmain-k-perf/# k-perf instrumented (DedicatedMain)
├── 📊 analyzers/
│   └── call_graph_visualizer/                # Python script → DOT/Graphviz call graphs
├── 📈 benchmarking/                          # PowerShell benchmark runners + shared utils
│   ├── game-of-life-kmp-k-perf/             # k-perf overhead benchmark suite
│   └── game-of-life-kmp-commonmain-ioa/     # IOA overhead benchmark suite
├── 📂 measurements/                          # Stored benchmark results (JSON + CSV)
│   └── 2024_11_SSP/                         # SSP 2024 paper data
├── buildAll.ps1                              # 🪟 Windows full build script
└── buildAll.sh                               # 🐧 Linux/macOS full build script
```

> **⚠️ Note:** This is **not** a single-root Gradle project. Each subproject (`KIRHelperKit`, `plugins/k-perf`, etc.) has its own `gradlew`. They must be built **in order** because each layer publishes to `mavenLocal` for the next to consume.

---

## 🚀 Quick Start

### Prerequisites

- JDK 21
- Gradle (via the included `gradlew` wrappers — no separate install needed)
- For JS targets: Node.js
- For Native targets: platform toolchain (mingwX64 on Windows, clang on Linux/macOS)

### Build Everything

```powershell
# Windows
.\buildAll.ps1

# Linux/macOS
./buildAll.sh

# Skip clean for faster iteration
.\buildAll.ps1 -CleanBuild $false
```

The build runs in this mandatory order (each step publishes to `mavenLocal`):

```
1. KIRHelperKit               → publishToMavenLocal
2. instrumentation-overhead-analyzer → publishToMavenLocal
3. k-perf plugin              → publishToMavenLocal
4. kmp-examples (all 5)       → build only
```

### Running Tests

```powershell
# Run all plugin tests
cd plugins\k-perf
.\gradlew test

# Run a specific test class
.\gradlew test --tests "at.jku.ssw.compilerplugin.KPerfCompilerPluginTest"
```

Tests use **[kctfork](https://github.com/ZacSweers/kotlin-compile-testing)** — they compile Kotlin source strings in-process and assert the instrumented output.

---

## 🔌 Plugins

### k-perf Plugin

**Plugin ID:** `io.github.neonmika.k-perf-plugin` | **Artifact:** `io.github.neonmika:k-perf:0.2.1`

#### Applying the Plugin

```kotlin
// settings.gradle.kts
pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal() // needed if using a locally published build
  }
}
```

```kotlin
// build.gradle.kts
plugins {
  kotlin("multiplatform") version "2.3.0"
  id("io.github.neonmika.k-perf-plugin") version "0.2.1"
}

kperf {
  enabled = true                    // toggle instrumentation on/off
  flushEarly = false                // flush after every write (safer) vs. buffer until end (faster)
  instrumentPropertyAccessors = false // also trace property getters/setters
  methods = ".*"                    // comma-separated regexes to filter which functions to instrument
}
```

> 💡 The plugin **automatically adds `kotlinx-io-core`** as a `commonMain` dependency — you don't need to declare it yourself.

#### Configuration Options

| Option | Type | Default | Description |
|---|---|---|---|
| `enabled` | `Boolean` | `true` | Enable/disable all instrumentation |
| `flushEarly` | `Boolean` | `false` | Flush trace file after every entry/exit (slower but safer) |
| `instrumentPropertyAccessors` | `Boolean` | `false` | Trace property getters/setters (note: JVM backend may optimize these away) |
| `methods` | `String` | `".*"` | Comma-separated regexes matched against fully-qualified function names |
| `testKIR` | `Boolean` | `false` | Use `KIRHelperKitTestingExtension` instead of the default implementation (for KIRHelperKit development/testing) |

#### Functions NOT Instrumented

k-perf automatically skips:

- The synthetic helpers it injects (`_enter_method`, `_exit_method`, `_exit_main`)
- Functions with no body
- Lambda adapters and anonymous functions
- Constructors (`<init>`)
- Property accessors when `instrumentPropertyAccessors = false`
- Functions whose FQN doesn't match the `methods` regex filter

---

### Instrumentation Overhead Analyzer Plugin

**Plugin ID:** `io.github.neonmika.instrumentation-overhead-analyzer` | **Artifact:** `io.github.neonmika:instrumentation-overhead-analyzer:0.2.1`

This plugin complements k-perf by injecting **controlled, synthetic overhead** into functions at compile time. The goal is to precisely measure how much overhead different instrumentation strategies add — giving you a quantified cost baseline to compare against.

```kotlin
// build.gradle.kts
plugins {
  id("io.github.neonmika.instrumentation-overhead-analyzer") version "0.2.1"
}

instrumentationOverheadAnalyzer {
  enabled = true
}
```

> ⚠️ **Work in progress:** The IOA plugin is currently a **stub**. It walks IR functions and identifies instrumentation candidates, but the synthetic overhead injection is **not yet implemented**. The plugin compiles and can be applied, but it does not modify the program under analysis.

> 🔬 This plugin is currently in **prototype/research phase** — it identifies instrumentation candidates but the synthetic overhead injection is still being developed.

---

## 🧰 KIRHelperKit

**Artifact:** `io.github.neonmika:KIRHelperKit:0.2.1`

KIRHelperKit is a standalone utility library that makes **Kotlin IR compiler plugin development** significantly easier. It abstracts the boilerplate of navigating and constructing IR elements.

### What It Provides

| Utility | Description |
|---|---|
| `findClass(signature)` | Locate an `IrClassSymbol` by fully-qualified name |
| `findFunction(signature)` | Find an `IrSimpleFunctionSymbol` globally or within a class |
| `findProperty(name)` | Find an `IrPropertySymbol` by name |
| `enableCallDSL` / `callExpression` | Fluent DSL for constructing IR call expressions |
| `IrStringBuilder` | Generate `StringBuilder` IR operations (`.append()`, `.insert()`, `.delete()`) |
| `IrFileIOHandler` | Cross-platform file I/O operations within Kotlin IR |

### Using KIRHelperKit in Your Plugin

```kotlin
// build.gradle.kts of your compiler plugin project
dependencies {
  implementation("io.github.neonmika:KIRHelperKit:0.2.1")
}
```

Make sure `mavenLocal()` is listed in your `settings.gradle.kts` repositories if using a local build:

```kotlin
pluginManagement {
  repositories { gradlePluginPortal(); mavenLocal() }
}
dependencyResolutionManagement {
  repositories { mavenCentral(); mavenLocal() }
}
```

---

## 🎮 KMP Examples

Five Kotlin Multiplatform projects demonstrate k-perf in action using **Conway's Game of Life**. They all target JVM, JavaScript (Node.js), and Native (mingwX64, linuxX64, macosX64).

| Project | Plugin | Architecture |
|---|---|---|
| `game-of-life-kmp-commonmain` | ❌ none | Single shared `main()` |
| `game-of-life-kmp-commonmain-k-perf` | ✅ k-perf | Single shared `main()` |
| `game-of-life-kmp-commonmain-ioa` | 🚧 IOA *(work in progress)* | Single shared `main()` |
| `game-of-life-kmp-dedicatedmain` | ❌ none | Per-platform `main()` |
| `game-of-life-kmp-dedicatedmain-k-perf` | ✅ k-perf | Per-platform `main()` |

### CommonMain vs DedicatedMain

- **CommonMain** — one `fun main(args: Array<String>)` in `commonMain/`, compiled identically for all targets.
- **DedicatedMain** — each platform has its own main (`jvmMain/`, `jsMain/`, `nativeMain/`), enabling platform-specific logic.

### Running an Example

```powershell
cd kmp-examples\game-of-life-kmp-commonmain-k-perf

# JVM
.\gradlew jvmRun --args="500"

# JavaScript (Node.js)
.\gradlew jsNodeRun --args="500"

# Native (Windows)
.\gradlew runReleaseExecutableMingwX64
```

The app prints `### Elapsed time: {microseconds}` to stdout and produces a trace + symbols file.

---

## 📊 Analyzers

### 🕸️ Call Graph Visualizer

Located at `analyzers/call_graph_visualizer/`, this Python script reads a k-perf trace file and generates a **DOT/Graphviz call graph**.

```bash
python graph-visualizer.py <trace_file> <symbols_file>
```

A Jupyter Notebook version (`graph-visualizer.ipynb`) is also available for interactive exploration.

---

## 📈 Benchmarking

The `benchmarking/` folder contains two PowerShell benchmark suites. Both use shared infrastructure in `benchmarking/utils.ps1` (statistics) and `benchmarking/build.ps1` (Gradle helpers).

Results land in `measurements/<timestamp>_<suite-name>/` as JSON files with full statistics: mean, median, stddev, min, max, and 95% confidence intervals (t-distribution).

### Suite 1 — k-perf Overhead

Measures the overhead of the k-perf plugin across platforms and configurations.

```powershell
cd benchmarking\game-of-life-kmp-k-perf

# Default run (50 repetitions, 10 steps, all platforms)
.\run.ps1

# JVM only, skip rebuild, 100 reps
.\run.ps1 -Filters @("jar") -RepetitionCount 100 -CleanBuild $false

# Compare flushEarly strategies on CommonMain JVM
.\run.ps1 -Filters @("common", "flushEarlyTrue", "jar")
.\run.ps1 -Filters @("common", "flushEarlyFalse", "jar") -CleanBuild $false
```

**Available filters** (AND logic — all tags must match):

| Category | Tags |
|---|---|
| Architecture | `common`, `dedicated` |
| Strategy | `flushEarlyTrue`, `flushEarlyFalse` |
| Platform | `jar` (JVM), `js` (Node.js), `native` (Windows .exe) |

### Suite 2 — Instrumentation Overhead Analyzer

> ⚠️ **Work in progress:** The IOA plugin is currently a stub — it does not yet inject synthetic overhead. This suite benchmarks the plugin's compile-time and runtime cost as a baseline, but results do not yet reflect real instrumentation overhead.

Measures the raw overhead introduced by the IOA plugin.

```powershell
cd benchmarking\game-of-life-kmp-commonmain-ioa

# Default run (50 repetitions, 10 steps, all platforms)
.\run.ps1

# JVM only, 100 reps
.\run.ps1 -Filters @("jar") -RepetitionCount 100
```

### Common Parameters

| Parameter | Default | Description |
|---|---|---|
| `-RepetitionCount` | `50` | Runs per executable |
| `-StepCount` | `10` | Game of Life steps per run (higher = more stable measurements) |
| `-CleanBuild` | `$true` | Rebuild from scratch before running |
| `-Filters` | *(all)* | Array of tags; only executables matching all tags are benchmarked |

### Example Result File

```json
{
  "executable": "commonmain-plain-jar",
  "repetitions": 50,
  "times": [68942, 65363, 68848],
  "statistics": {
    "mean": 70254.5,
    "median": 70654.0,
    "stddev": 1845.3,
    "min": 65363,
    "max": 74446,
    "ci95": { "lower": 69584.2, "upper": 70924.8 }
  }
}
```

> 📌 The `measurements/2024_11_SSP/` folder preserves the original data from the SSP 2024 paper.

---

## 📜 License

The projects in this repository are licensed under the [GNU Lesser General Public License v3.0 (LGPL-3.0)](https://www.gnu.org/licenses/lgpl-3.0.html).

✅ You are free to **use k-perf in your own projects** (open-source or proprietary) without any licensing requirements on your code.  
⚠️ Any **modifications to k-perf itself** must be released under the same LGPL-3.0 license.
