# Copilot Instructions for k-perf

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

Tests live only in the plugin projects (JUnit 5); there are no tests in `KIRHelperKit` (it is exercised transitively). Tests use **kctfork** (`dev.zacsweers.kctfork:core`) — they compile Kotlin source strings in-process and assert the instrumented output.

### Benchmarking

See [`benchmarking/README.md`](../benchmarking/README.md) for full documentation on the two benchmark suites, parameters, and output format.

> ⚠️ **Keep benchmarking scripts in sync**: Any change to kperf plugin settings (names/defaults), KMP example project structure, or project version **must** be reflected in:
> - `benchmarking/build.ps1` (build-time key names, gradle args passed to KPerfConfig)
> - `benchmarking/game-of-life-kmp-k-perf/run.ps1` (JAR version strings, suffix logic)
> - `benchmarking/game-of-life-kmp-commonmain-ioa/run.ps1` (JAR version strings) — note: the IOA plugin itself is **work in progress**; this benchmark measures a mostly no-op plugin for now
>
> GitHub Actions benchmark workflows (all `workflow_dispatch`-only) are in `.github/workflows/benchmark-*.yml`. They run both suites and commit results to `measurements/` with a `[skip ci]` message to avoid triggering build workflows.

---

## Architecture

### Three-layer dependency chain

```
KIRHelperKit  (io.github.neonmika:KIRHelperKit:0.2.1)
    └── consumed by ──▶  plugins/k-perf  (io.github.neonmika.k-perf-plugin)
                         plugins/instrumentation-overhead-analyzer
                              └── applied to ──▶  kmp-examples/*
```

> ⚠️ **`plugins/instrumentation-overhead-analyzer` is currently work in progress.** The plugin builds, is applied to `game-of-life-kmp-commonmain-ioa`, and walks IR functions — but the synthetic overhead injection is **not yet implemented**. Treat it as a stub/prototype.

See [`README.md`](../README.md) for a full overview of the repository structure.

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

### Output artifact naming

The k-perf variants use the active plugin configuration to tag their output artifacts. The `archiveClassifier` for JARs and `baseName` suffix for native binaries follow the pattern:

```
flushEarly-<true|false>-propAccessors-<true|false>-testKIR-<true|false>
```

For example, a JAR built with `kperfFlushEarly=true` is named `game-of-life-kmp-commonmain-k-perf-flushEarly-true-propAccessors-false-testKIR-false.jar`. The JS module name uses the same suffix via `outputModuleName`. Built JARs and their runtime dependencies are copied to `build/lib/` by the Gradle build.

### Running a KMP example

**Option A — Gradle run tasks** (Gradle overhead, but always rebuilds):

| Task | Platform |
|---|---|
| `jvmRun` | JVM (uses `mainRun { mainClass.set(...) }` in build.gradle.kts) |
| `jsNodeProductionRun` | JS (Node.js) |
| `runReleaseExecutableMingwX64` | Native (Windows) |

```powershell
cd kmp-examples\game-of-life-kmp-commonmain-k-perf
.\gradlew jvmRun --info --stacktrace -PkperfEnabled=true -PkperfFlushEarly=false -PkperfMethods=.*
```

**Option B — Direct invocation** (no Gradle overhead; requires prior build):

```powershell
# JVM — <steps> is positional arg (default: 500)
java -jar build\lib\<project>-jvm-<version>.jar <steps>
# e.g.:
java -jar build\lib\game-of-life-kmp-commonmain-jvm-0.2.1.jar 500
java -jar build\lib\game-of-life-kmp-commonmain-k-perf-jvm-0.2.1-flushEarly-false-propAccessors-false-testKIR-false.jar 500

# JS (Node.js)
node build\js\packages\<project>\kotlin\<project>.js <steps>
# k-perf variant uses suffix in package and module name:
node build\js\packages\<project>-<suffix>\kotlin\<project>-<suffix>.js <steps>

# Native (Windows)
build\bin\mingwX64\releaseExecutable\<project>.exe <steps>
# k-perf variant:
build\bin\mingwX64\releaseExecutable\<project>-<suffix>.exe <steps>
```

**Option C — IDE run configurations**: Each KMP example has `.run/` directory with pre-configured IntelliJ/Android Studio run configs for all three platforms. k-perf examples include the full `-Pkperf*` parameter set with defaults suitable for development (`-PkperfTestKIR=true`).

> **JVM `mainClass`**: CommonMain projects use `CommonGameOfLifeApplicationKt`; DedicatedMain projects use `JVMGameOfLifeApplicationKt`.

## KIRHelperKit

Utility library for writing Kotlin IR compiler plugins. Key packages:
- `at.jku.ssw.kir.find` — locate symbols: `findClass`, `findFunction`, `findProperty`, extension methods on `IrPluginContext`, `IrClassSymbol`, `IrPropertySymbol`
- `at.jku.ssw.kir.call` — DSL for building IR function call expressions
- `at.jku.ssw.kir.general` — `IrStringBuilder`, `IrFileIOHandler` helpers

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

> ⚠️ **Defaults are defined in three separate places.** When changing a default value, update all three:
> 1. `plugins/k-perf/src/main/kotlin/at/jku/ssw/gradle/KPerfGradlePlugin.kt` — extension class property defaults (`var flushEarly: Boolean = false`, etc.)
> 2. `plugins/k-perf/src/main/kotlin/at/jku/ssw/compilerplugin/KPerfComponentRegistrar.kt` — compiler-side fallbacks (`configuration[KEY] ?: false`, etc.)
> 3. `kmp-examples/game-of-life-kmp-*-k-perf/build.gradle.kts` — Gradle property fallbacks (`.getOrElse(false)`, etc.)

### Group, version, and artifact coordinates

All subprojects use:
- `group = "io.github.neonmika"`
- `version = "<current major version>.<current minor version>.<current patch version>"` (e.g., `0.2.1`)

When updating versions, always update all versions in all subprojects to keep them in sync.
Thus, make sure to update all version defintions, as well as all plugin declarations as well as dependency declarations in all build scripts.
Also make sure to update the version in the README.md.

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

Both plugins write a `./DEBUG.txt` file **at compile time** (not runtime) using a custom function `printText(...)`. This file is deleted at the start of each compilation run and written to the working directory where `gradlew` runs. It is in `.gitignore`.