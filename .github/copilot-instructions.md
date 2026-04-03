# Copilot Instructions for k-perf

## What This Project Is

**k-perf** is a Kotlin compiler plugin that instruments functions at the IR (Intermediate Representation) level to generate execution traces for performance analysis. It targets Kotlin Multiplatform (JVM, JavaScript, Native). This is a research project from JKU Linz (SSW group), originating from the SSP 2024 paper.

## Build System & Commands

All subprojects use **Gradle with Kotlin DSL** (`build.gradle.kts`). There is no root-level Gradle project — each subproject has its own `gradlew`.

### Build order (mandatory — each layer publishes to `mavenLocal` for the next to consume)

```
KIRHelperKit → plugins/instrumentation-overhead-analyzer → plugins/k-perf → kmp-examples/*
```

**Full build (all platforms):**
```powershell
# Windows
.\buildAll.ps1

# Linux/macOS
./buildAll.sh

# Skip clean for faster iteration
.\buildAll.ps1 -CleanBuild $false
```

**Per-subproject build + publish:**
```powershell
cd KIRHelperKit
.\gradlew build publishToMavenLocal

cd plugins\k-perf
.\gradlew build publishToMavenLocal
```

### Running tests

Tests live only in the plugin projects (JUnit 5 via `useJUnitPlatform()`).

```powershell
# Run all tests in a plugin
cd plugins\k-perf
.\gradlew test

# Run a single test class
.\gradlew test --tests "at.jku.ssw.compilerplugin.KPerfCompilerPluginTest"

# Run a single test method (use the exact display name for backtick-named tests)
.\gradlew test --tests "at.jku.ssw.compilerplugin.KPerfCompilerPluginTest.SSP (Symposium for Software Performance) simple example"

# Same pattern for IOA plugin
cd plugins\instrumentation-overhead-analyzer
.\gradlew test --tests "at.jku.ssw.compilerplugin.InstrumentationOverheadAnalyzerCompilerPluginTest"
```

Tests use **kctfork** (`dev.zacsweers.kctfork:core:0.12.1`) for in-process Kotlin compilation — they compile Kotlin source strings, run them in the classloader, and assert the result. There are no tests in `KIRHelperKit` (it is exercised transitively through plugin tests).

### Benchmarking

Full documentation is in [`benchmarking/README.md`](../benchmarking/README.md). There are two benchmark suites, each with its own `run.ps1`. They share [`benchmarking/utils.ps1`](../benchmarking/utils.ps1) (statistics: mean, median, stddev, CI95 via t-distribution) and [`benchmarking/build.ps1`](../benchmarking/build.ps1) (Gradle build helpers), both dot-sourced at the top of each runner.

**Suite 1 — `benchmarking/game-of-life-kmp-k-perf/run.ps1`**

Measures the overhead of the k-perf plugin across architectures, targets, and plugin configurations.

```powershell
cd benchmarking\game-of-life-kmp-k-perf

# Default run (50 reps, 500 steps, CommonMain only, JVM+JS+Native, flushEarly=false)
.\run.ps1

# Run only JVM, both architectures
.\run.ps1 -JS $false -Native $false

# Test multiple flushEarly values in one run (Cartesian product)
.\run.ps1 -FlushEarly @($false, $true) -Common $true -Dedicated $true -JVM $true -JS $false -Native $false

# Skip rebuild when re-running with different params
.\run.ps1 -CleanBuild $false -RepetitionCount 10 -StepCount 20
```

Key parameters (defaults differ from README — use actual script values):

| Parameter | Default | Description |
|---|---|---|
| `-RepetitionCount` | `50` | Runs per executable |
| `-StepCount` | `500` | Game of Life steps per run |
| `-CleanBuild` | `$false` | Rebuild dependencies before run |
| `-Common` / `-Dedicated` | `$true` / `$false` | Include CommonMain / DedicatedMain architecture |
| `-JVM` / `-JS` / `-Native` | `$true` / `$true` / `$true` | Target platform toggles |
| `-FlushEarly` | `@($false)` | Array — one build+measure pass per value |
| `-InstrumentPropertyAccessors` | `@($false)` | Array — Cartesian product with FlushEarly |
| `-TestKIR` | `@($false)` | Array — Cartesian product with above |
| `-Reference` | `$true` | Include uninstrumented baseline |

**Suite 2 — `benchmarking/game-of-life-kmp-commonmain-ioa/run.ps1`**

Measures raw instrumentation overhead introduced by the IOA plugin.

```powershell
cd benchmarking\game-of-life-kmp-commonmain-ioa

# Default run (50 reps, 10 steps, reference + IOA, JVM+JS+Native)
.\run.ps1

# JVM only, skip rebuild
.\run.ps1 -JS $false -Native $false -CleanBuild $false

# Increase fidelity
.\run.ps1 -RepetitionCount 100 -StepCount 50 -JVM $true -JS $false -Native $false
```

Parameters: `-RepetitionCount` (50), `-StepCount` (10), `-CleanBuild` (`$true`), `-Reference` (`$true`), `-IOA` (`$true`), `-JVM`/`-JS`/`-Native` (all `$true`).

Results land in `measurements/<timestamp>_<suite-name>/` with the following structure per run:

| File | Description |
|---|---|
| `<executable-name>.json` | Per-executable full result: `parameters`, `machineInfo`, `buildTimeMs`, `executable`, `repetitions`, `times[]` (µs from app stdout), `statistics` |
| `_results.csv` | Summary row per executable — sorts first alphabetically due to `_` prefix |
| `_results.json` | Same summary data as CSV but in JSON format |
| `<executable-name>_<iteration>.png` | Call graph PNG generated by `graph-visualizer.py` for every k-perf iteration (only k-perf suite) |

The elapsed times in `times[]` are **application-reported** (the `### Elapsed time: ` line printed by `play()` via `TimeSource.Monotonic`), not external wall-clock. The script parses stdout to extract them.

The `measurements/2024_11_SSP/` folder contains the older format from the SSP paper: plain `.txt` files with one microsecond value per line and no statistics computed.

---

## Architecture

### Three-layer dependency chain

```
KIRHelperKit  (io.github.neonmika:KIRHelperKit:0.2.0)
    └── consumed by ──▶  plugins/k-perf  (io.github.neonmika.k-perf-plugin)
                         plugins/instrumentation-overhead-analyzer
                              └── applied to ──▶  kmp-examples/*
```

### What the k-perf plugin does (IR level)

`KPerfExtension : IrGenerationExtension` walks every `IrFunction` in the module using `IrElementTransformerVoidWithContext`. For each eligible function it wraps the body in a try/finally, injecting:

- **On entry:** calls `_enter_method(methodId: Int): TimeMark` — writes `>;{id}\n` to the trace sink and returns `TimeSource.Monotonic.markNow()`
- **On exit:** calls `_exit_method(startTime: TimeMark)` — calls `startTime.elapsedNow().inWholeMicroseconds`, writes `<;{micros}\n`
- **On main exit only:** calls `_exit_main(startTime: TimeMark)` — flushes both files, writes the symbols file, prints filenames to stdout

These three synthetic functions (`_enter_method`, `_exit_method`, `_exit_main`) are added as top-level declarations to the **first file** of the module. The static fields (`_stringBuilder`, `_bufferedTraceFileSink`, `_bufferedSymbolsFileSink`, etc.) are also injected into the first file.

File I/O uses **`kotlinx-io`** for cross-platform compatibility (same API on JVM, JS, Native).

### Output file formats

**Trace file** (`./trace_<platform>_<random>.txt`):
```
>;0          # enter method id=0
>;1          # enter method id=1
<;42         # exit method id=1, elapsed=42µs  (id implicit: stack-based)
<;105        # exit method id=0, elapsed=105µs
```

**Symbols file** (`./symbols_<platform>_<random>.txt`) — JSON object:
```json
{ "0": "main", "1": "game.gol.GameOfLife.step", ... }
```

The plugin prints both filenames to stdout at the end of `main`, which is how benchmarking scripts locate the trace output.

---

## KMP Examples

Five Kotlin Multiplatform projects under `kmp-examples/`, all targeting JVM + JS (Node.js) + mingwX64 + linuxX64 + macosX64. They implement Conway's Game of Life and serve as the subjects under test for the plugins. The `play(steps)` function in `commonMain/kotlin/game/gol/GameOfLife.kt` is shared across all five; only the `main()` location and applied plugins differ.

| Project | Plugin | Architecture |
|---|---|---|
| `game-of-life-kmp-commonmain` | none | CommonMain |
| `game-of-life-kmp-commonmain-k-perf` | `io.github.neonmika.k-perf-plugin` | CommonMain |
| `game-of-life-kmp-commonmain-ioa` | `io.github.neonmika.instrumentation-overhead-analyzer` | CommonMain |
| `game-of-life-kmp-dedicatedmain` | none | DedicatedMain |
| `game-of-life-kmp-dedicatedmain-k-perf` | `io.github.neonmika.k-perf-plugin` | DedicatedMain |

### CommonMain vs DedicatedMain

- **CommonMain**: a single `fun main(args: Array<String>)` lives in `commonMain/kotlin/CommonGameOfLifeApplication.kt`, compiled identically for all targets. The JVM manifest's `Main-Class` is `CommonGameOfLifeApplicationKt`.
- **DedicatedMain**: each platform source set has its own `main()` — `jvmMain/kotlin/JVMGameOfLifeApplication.kt`, `jsMain/kotlin/JSGameOfLifeApplication.kt`, `nativeMain/kotlin/NativeGameOfLifeApplication.kt`. The JVM manifest's `Main-Class` is `JVMGameOfLifeApplicationKt`. The dedicated JVM main additionally creates a `test.txt` sentinel file; other platforms are currently identical to the CommonMain version.

### Output artifact naming

The k-perf variants use the active plugin configuration to tag their output artifacts. The `archiveClassifier` for JARs and `baseName` suffix for native binaries follow the pattern:

```
flushEarly-<true|false>-propAccessors-<true|false>-testKIR-<true|false>
```

For example, a JAR built with `kperfFlushEarly=true` is named `game-of-life-kmp-commonmain-k-perf-flushEarly-true-propAccessors-false-testKIR-false.jar`. The JS module name uses the same suffix via `outputModuleName`. Built JARs and their runtime dependencies are copied to `build/lib/` by the Gradle build.

### Plugin configuration in build.gradle.kts

**k-perf** — configured via the `kperf { }` extension; Gradle properties override defaults:
```kotlin
kperf {
  enabled = true
  flushEarly = providers.gradleProperty("kperfFlushEarly").map { it.toBoolean() }.getOrElse(false)
  instrumentPropertyAccessors = providers.gradleProperty("kperfInstrumentPropertyAccessors").map { it.toBoolean() }.getOrElse(false)
  testKIR = providers.gradleProperty("kperfTestKIR").map { it.toBoolean() }.getOrElse(false)
}
```

**IOA** — simpler, single flag:
```kotlin
instrumentationOverheadAnalyzer {
  enabled = true
}
```

### `kotlinx-io-core` dependency

Do **not** add `kotlinx-io-core` to the `commonMain` dependencies of a KMP example that uses the k-perf plugin — the plugin injects it automatically at compile time. The plain and IOA examples do not need it at all.

### Running a KMP example manually

```powershell
cd kmp-examples\game-of-life-kmp-commonmain-k-perf

# JVM
.\gradlew jvmRun --args="10"

# JS (Node.js)
.\gradlew jsNodeRun --args="10"

# Native (current host platform)
.\gradlew runDebugExecutableMingwX64   # Windows
```

### KIRHelperKit

Utility library for writing Kotlin IR compiler plugins. Key packages:
- `at.jku.ssw.kir.find` — locate symbols: `findClass`, `findFunction`, `findProperty`, extension methods on `IrPluginContext`, `IrClassSymbol`, `IrPropertySymbol`
- `at.jku.ssw.kir.call` — DSL for building IR function call expressions
- `at.jku.ssw.kir.general` — `IrStringBuilder`, `IrFileIOHandler` helpers

---

## Analyzers

Two analysis tools live under `analyzers/`. New analyzers should be added here as additional subdirectories.

### `analyzers/call_graph_visualizer/`

**`graph-visualizer.py`** — Python script that parses a trace+symbols pair and produces a Graphviz call graph.

```bash
# Minimal usage (symbols file is auto-located by replacing "trace" with "symbols" in the filename)
python analyzers/call_graph_visualizer/graph-visualizer.py trace_JVM_12345.txt

# Explicit paths + custom output
python analyzers/call_graph_visualizer/graph-visualizer.py trace_JVM_12345.txt \
  --symbols symbols_JVM_12345.txt \
  --output my_graph.png
```

- Prints the DOT source to **stdout** unconditionally.
- Renders a **PNG** alongside it when the `graphviz` Python package is installed (`pip install graphviz`). Without it, only DOT output is produced.
- Nodes are labelled `shortMethodName (~Xms -- Y%)` and heat-coloured in red proportional to their share of total runtime.
- Edge labels show direct call count plus indirect (transitive) call count.
- The benchmark runner (`benchmarking/game-of-life-kmp-k-perf/run.ps1`) calls this script automatically after every instrumented run and saves the PNGs to the measurement directory.

**`graph-visualizer.ipynb`** — Jupyter notebook version of the same logic, useful for interactive exploration.

### `analyzers/measurements_plotter/`

**`index.html`** — Self-contained browser-based tool using Plotly.js for visualising `_results.json` or individual `*.json` benchmark result files. Open directly in a browser — no server required. Currently a work in progress; the directory may be empty until populated.

### Adding a new analyzer

- Create a new subdirectory under `analyzers/`.
- Analyzers are standalone tools (Python, HTML/JS, notebooks) — they have no Gradle build and are not part of `buildAll.ps1`.
- Consume the standard output files from `measurements/`: per-executable JSON and/or `_results.csv` / `_results.json`.

---

## Key Conventions

### Functions excluded from instrumentation

`KPerfExtension` skips a function if any of these are true:
- Name is `_enter_method`, `_exit_method`, or `_exit_main` (the injected helpers themselves)
- Body is null
- Origin is `ADAPTER_FOR_CALLABLE_REFERENCE`
- FQ name contains `<init>` or `<anonymous>`
- Origin is `DEFAULT_PROPERTY_ACCESSOR` (unless `instrumentPropertyAccessors = true`)
- FQ name is non-null and does not match any of the `methods` regexes (full-string `Regex.matches`)

Follow the same exclusion logic when adding new instrumentation in either plugin.

### Plugin configuration (k-perf)

Configured via the `kperf { }` block in the consumer's `build.gradle.kts`, backed by Gradle properties:

| Property | Default | Effect |
|---|---|---|
| `enabled` | `true` | Toggle instrumentation on/off |
| `flushEarly` | `false` | Flush trace sink after every write (safe but slower) |
| `instrumentPropertyAccessors` | `false` | Also instrument property getters/setters |
| `testKIR` | `false` | Enable `KIRHelperKitTestingExtension` for IR debugging |
| `methods` | `".*"` | Comma-separated regexes matched against a function's FQN; only matching functions are instrumented (e.g., `"a\.b\..*;c\.d\.MyClass\.fun123"`) |

Gradle properties (`-PkperfFlushEarly=true`, `-PkperfMethods=a\.b\..*`) override defaults in the KMP example projects. `enabled` is also configurable via `-PkperfEnabled=false`.

### Group, version, and artifact coordinates

All subprojects use:
- `group = "io.github.neonmika"`
- `version = "0.2.0"`
- `mavenLocal()` must appear in both `pluginManagement.repositories` and `dependencyResolutionManagement.repositories` in consumers' `settings.gradle.kts`

### `STRINGBUILDER_MODE` flag

`KPerfExtension` has a hardcoded `val STRINGBUILDER_MODE = false`. When `true`, the plugin buffers all trace output in a `StringBuilder` and writes it all at once at the end. This mode is kept for experimentation — do not remove it, but do not enable it by default.

### Static fields are injected into `moduleFragment.files[0]`

All synthetic IR fields and functions added by the plugin are attached to the **first file** in the module. This is by convention in both plugins. When adding new static state to a plugin, follow this pattern.

### Adding a new KMP example

1. Create the project under `kmp-examples/` following the existing structure
2. Add it to `buildAll.ps1` via `Invoke-KmpBuild` and to `buildAll.sh` via `run_kmp_build`
3. If it needs benchmarking, add executable entries to `benchmarking/*/run.ps1` and `utils.ps1`

### Adding a new compiler plugin

1. Mirror the structure of `plugins/k-perf` (gradle plugin + compiler plugin registrar + extension + command line processor)
2. Add to `buildAll.ps1`/`buildAll.sh` with `build publishToMavenLocal` before any KMP examples that consume it
3. Tests go in `src/test/kotlin/`, use kctfork's `KotlinCompilation` to compile `SourceFile.kotlin(...)` strings in-process

### Debug output during compilation

Both plugins write a `./DEBUG.txt` file **at compile time** (not runtime) using `File("./DEBUG.txt").appendText(...)`. This file is deleted at the start of each compilation run and written to the working directory where `gradlew` runs. It is in `.gitignore`.