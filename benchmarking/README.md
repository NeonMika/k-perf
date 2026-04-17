# Benchmarking Suite for Game of Life KMP

This folder contains benchmarking scripts for evaluating the performance characteristics of the Game of Life Kotlin Multiplatform (KMP) implementation across different architectural variants and compiler plugins.

## Overview

The benchmarking suite measures the execution time of Game of Life simulations under two different instrumentation scenarios:

1. **K-Perf Benchmark** (`game-of-life-kmp-k-perf/`)
   - Evaluates the performance impact of the K-Perf compiler plugin
   - Tests all combinations of `enabled`, `flushEarly`, `instrumentPropertyAccessors`, `testKIR`, and `methods` flags
   - Tests both CommonMain and DedicatedMain architectural variants
   - Produces measurements across JVM (JAR), JavaScript (Node.js), and Native targets

2. **Instrumentation-Overhead-Analyzer Benchmark** (`game-of-life-kmp-commonmain-ioa/`)
   - Measures the overhead introduced by the IOA plugin per `IoaKind`
   - Available `IoaKind` values are read dynamically from `IoaKind.kt`
   - Compares instrumented (per-kind) vs non-instrumented versions
   - Focuses on CommonMain architecture across JVM, JS, and Native targets

## Goals

The benchmarks aim to:

- Quantify the performance impact of compiler plugins on code execution
- Compare optimization strategies (e.g., flush-early behavior in K-Perf)
- Evaluate architectural decisions (CommonMain vs DedicatedMain)
- Measure overhead introduced by instrumentation per `IoaKind`
- Provide statistical analysis (mean, median, standard deviation, confidence intervals)

## Shared Infrastructure

All Python modules live in this (`benchmarking/`) directory:

| Module | Purpose |
|---|---|
| `benchmark_types.py` | Enums and dataclasses (`KPerfConfig`, `IoaConfig`, `BenchmarkExecutable`, …) |
| `gradle_utils.py` | Gradle task discovery, timed task execution, KMP build orchestration |
| `statistics_utils.py` | Statistical analysis, machine-info collection, CSV/JSON export |
| `build.py` | Build functions for all projects; `invoke_get_executables` / `invoke_get_ioa_executables` |
| `run.py` | `invoke_benchmark_suite()` — the core measurement loop |

## Artifact Layout

Both suites use the same `dist/` → `bin/<suffix>/` pattern:

- The Gradle build of each KMP example copies built artifacts (JARs, JS bundles, native binaries) into `dist/` via `doLast` copy blocks.
- The Python build script immediately copies `dist/` into `bin/<suffix>/` after each variant build, so multiple configurations coexist without overwriting each other.
- The benchmark runner picks executables from `bin/<suffix>/` by their standard Gradle-generated names (no variant suffix in the filename itself).

Reference (uninstrumented) executables are read directly from the `game-of-life-kmp-commonmain/dist/` directory since there is only one configuration.

## Results

Benchmark results are stored in the `../measurements/` folder with subdirectories named by timestamp and benchmark type:

```
measurements/
├── 2026_02_03_01_40_27_k-perf_local_3reps_20steps.../
│   ├── commonmain-plain-jar.json
│   ├── commonmain-k-perf-enabled-true-flushEarly-false-...-jar.json
│   └── _results.csv / _results.json
└── 2026_02_03_01_40_27_ioa_local_3reps_20steps.../
    ├── commonmain-plain-jar.json
    ├── commonmain-ioa-kind-none-jar.json
    ├── commonmain-ioa-kind-stringbuilderappend-jar.json
    └── _results.csv / _results.json
```

Each JSON file contains:

- **executable**: Name of the benchmarked executable/variant
- **repetitions**: Number of times the simulation was run
- **times**: Array of execution times in microseconds for each repetition
- **statistics**: Computed statistics including:
  - `mean`: Average execution time
  - `median`: Middle value of execution times
  - `stddev`: Standard deviation
  - `min`/`max`: Minimum and maximum times
  - `ci95`: 95% confidence interval using t-distribution

## Running the Benchmarks

### Quick Start

#### K-Perf Benchmark

```bash
cd benchmarking/game-of-life-kmp-k-perf
python benchmark.py
```

#### Instrumentation-Overhead-Analyzer Benchmark

```bash
cd benchmarking/game-of-life-kmp-commonmain-ioa
python benchmark.py
```

Both run with default parameters: 3 repetitions, 20 simulation steps, clean build enabled, all executables and platforms.

### Parameters

#### K-Perf Benchmark Parameters (`benchmark.py`)

**`--repetition-count N` (default: 3)**
- Number of times each executable is benchmarked.

**`--clean-build true|false` (default: true)**
- Rebuild all dependencies before benchmarking. Set to `false` to reuse existing binaries.

**`--step-count N` (default: 20)**
- Number of Game of Life simulation steps per benchmark run.

**`--common`, `--dedicated` (default: true / false)**
- Select which game type variants to include.

**`--jvm`, `--js`, `--native` (default: true)**
- Select target platforms.

**`--reference true|false` (default: true)**
- Include uninstrumented reference executables.

**`--enabled`, `--flush-early`, `--instrument-property-accessors`, `--test-kir`**
- Comma-separated boolean lists. The Cartesian product of all combinations is built and benchmarked.
- Example: `--flush-early true,false` benchmarks both flushEarly variants.

**`--methods REGEX[,REGEX...]` (default: `.*`)**
- Comma-separated regex patterns for k-perf method filtering.

**`--ci-label LABEL` (default: `local`)**
- Label embedded in result directory names and JSON payloads for CI identification.

Run `python benchmark.py --help` for full documentation.

#### IOA Benchmark Parameters (`benchmark.py`)

**`--repetition-count N` (default: 3)**
- Number of times each executable is benchmarked.

**`--clean-build true|false` (default: true)**
- Rebuild all dependencies before benchmarking.

**`--step-count N` (default: 20)**
- Number of Game of Life simulation steps per benchmark run.

**`--reference true|false` (default: true)**
- Include the uninstrumented reference (plain commonmain) executable.

**`--ioa true|false` (default: true)**
- Include IOA-instrumented executables.

**`--jvm`, `--js`, `--native` (default: true)**
- Select target platforms.

**`--ioa-kinds KIND[,KIND...]` (default: all available)**
- Comma-separated `IoaKind` enum values to benchmark. Available kinds are parsed at runtime from `IoaKind.kt`. Example: `--ioa-kinds None,StringBuilderAppend`.

**`--ci-label LABEL` (default: `local`)**
- Label embedded in result directory names.

Run `python benchmark.py --help` for full documentation (also shows all available `IoaKind` values).

### Advanced Usage

#### Combining Multiple Parameters (K-Perf)

```bash
# Run only CommonMain flushEarlyTrue variants with 100 repetitions and 50 simulation steps
cd benchmarking/game-of-life-kmp-k-perf
python benchmark.py --repetition-count 100 --step-count 50 --flush-early true --dedicated false

# Compare both flushEarly variants in one invocation
python benchmark.py --flush-early true,false --repetition-count 100 --step-count 25

# Skip rebuild on a subsequent run
python benchmark.py --flush-early true,false --repetition-count 100 --step-count 25 --clean-build false
```

#### IOA with Specific Kinds

```bash
cd benchmarking/game-of-life-kmp-commonmain-ioa

# Benchmark only the None and StringBuilderAppend kinds, JVM only
python benchmark.py --ioa-kinds None,StringBuilderAppend --jvm true --js false --native false

# Skip rebuild on a second run
python benchmark.py --clean-build false --repetition-count 50
```

#### Quick Smoke Test (Both Suites)

```bash
cd benchmarking/game-of-life-kmp-k-perf
python benchmark.py --repetition-count 3 --step-count 5 --js false --native false

cd ../game-of-life-kmp-commonmain-ioa
python benchmark.py --repetition-count 3 --step-count 5 --js false --native false \
    --ioa-kinds None
```

## Output Format

The benchmarks produce timestamped result directories in the measurements folder.

### K-Perf directory name pattern
```
{timestamp}_k-perf_{ci-label}_{N}reps_{N}steps_ref{t|f}_cmn{t|f}_ded{t|f}_{platforms}_en{...}_fe{...}_pa{...}_tkir{...}_m-{methods}
```

### IOA directory name pattern
```
{timestamp}_ioa_{ci-label}_{N}reps_{N}steps_ref{t|f}_ioa{t|f}_{platforms}_kinds-{kinds}
```

### Per-executable JSON structure

```json
{
  "parameters": { "RepetitionCount": 3, "CleanBuild": true, "StepCount": 20, ... },
  "machineInfo": { ... },
  "buildTimeMs": 12345.6,
  "executable": "commonmain-plain-jar",
  "repetitions": 3,
  "timeUnit": "microseconds",
  "times": [68942, 65363, 68848],
  "stepTimes": [[...], [...], [...]],
  "statistics": {
    "count": 3,
    "mean": 67717.7,
    "median": 68848.0,
    "stddev": 1879.3,
    "min": 65363,
    "max": 68942,
    "ci95": { "lower": 63046.7, "upper": 72388.6 }
  },
  "status": "ok"
}
```

## Troubleshooting

**No successful measurements / "Elapsed time not found"**
- Ensure the executable was built successfully and the path is correct.
- Try `--clean-build true` to force a fresh build.

**Build failures**
- Ensure JDK, Gradle, Node.js, and Kotlin compiler tools are installed.
- Run with `--clean-build true` to ensure a clean build state.

**Missing executables after build**
- Some target platforms may not build on your system (e.g., native executables only build on supported platforms).
- Use `--jvm false`, `--js false`, or `--native false` to skip unavailable platforms.


This folder contains benchmarking scripts for evaluating the performance characteristics of the Game of Life Kotlin Multiplatform (KMP) implementation across different architectural variants and compiler plugins.

## Overview

The benchmarking suite measures the execution time of Game of Life simulations under two different instrumentation scenarios:

1. **K-Perf Benchmark** (`game-of-life-kmp-k-perf/`)
   - Evaluates the performance impact of the K-Perf compiler plugin
   - Compares `flushEarlyTrue` vs `flushEarlyFalse` compilation strategies
   - Tests both CommonMain and DedicatedMain architectural variants
   - Produces measurements across JVM (JAR), JavaScript (Node.js), and Native (Windows executable) targets

2. **Instrumentation-Overhead-Analyzer Benchmark** (`game-of-life-kmp-commonmain-ioa/`)
   - Measures the overhead introduced by instrumentation via the Instrumentation-Overhead-Analyzer plugin
   - Compares instrumented vs non-instrumented versions
   - Focuses on CommonMain architecture
   - Produces measurements across the same target platforms (JVM, JavaScript, Native)

## Goals

The benchmarks aim to:

- Quantify the performance impact of compiler plugins on code execution
- Compare optimization strategies (e.g., flush-early behavior in K-Perf)
- Evaluate architectural decisions (CommonMain vs DedicatedMain)
- Measure overhead introduced by instrumentation
- Provide statistical analysis (mean, median, standard deviation, confidence intervals)

## Results

Benchmark results are stored in the `../measurements/` folder with subdirectories named by timestamp and benchmark type:

```
measurements/
├── 2026_02_03_01_40_27_game-of-life-kmp-k-perf/
│   ├── commonmain-plain-jar.json
│   ├── commonmain-k-perf-flushEarlyTrue-jar.json
│   ├── commonmain-k-perf-flushEarlyFalse-jar.json
│   ├── ... (additional executable results)
└── 2026_02_03_01_40_27_game-of-life-kmp-commonmain-ioa/
    ├── commonmain_plain_jar.json
    ├── commonmain_ioa_jar.json
    └── ... (additional executable results)
```

Each JSON file contains:

- **executable**: Name of the benchmarked executable/variant
- **repetitions**: Number of times the simulation was run
- **times**: Array of execution times in milliseconds for each repetition
- **statistics**: Computed statistics including:
  - `mean`: Average execution time
  - `median`: Middle value of execution times
  - `stddev`: Standard deviation
  - `min`/`max`: Minimum and maximum times
  - `ci95`: 95% confidence interval using t-distribution

## Running the Benchmarks

### Quick Start

#### K-Perf Benchmark

```bash
cd benchmarking/game-of-life-kmp-k-perf
python3 benchmark.py
```

#### Instrumentation-Overhead-Analyzer Benchmark

```powershell
cd benchmarking/game-of-life-kmp-commonmain-ioa
.\run.ps1
```

The k-perf benchmark uses Python (`benchmark.py`); the IOA benchmark still uses PowerShell (`run.ps1`).

Both will run with default parameters (3 repetitions, 20 simulation steps per execution, no clean build, all executables tested).

### Parameters

#### K-Perf Benchmark Parameters (`benchmark.py`)

**`--repetition-count N` (default: 3)**

- Number of times each executable is benchmarked
- Higher values provide more statistical confidence but take longer
- Example: `--repetition-count 100`

**`--clean-build true|false` (default: false)**

- Controls whether dependencies and applications are rebuilt
- Set to `false` to skip building and use existing binaries
- Example: `--clean-build true`

**`--step-count N` (default: 20)**

- Number of Game of Life simulation steps to execute per benchmark run
- Example: `--step-count 50`

**`--common`, `--dedicated`, `--jvm`, `--js`, `--native` (all default: true/false as noted in `--help`)**

- Select which game types and target platforms to benchmark

**`--enabled`, `--flush-early`, `--instrument-property-accessors`, `--test-kir`**

- Comma-separated boolean lists forming the Cartesian product of k-perf configurations
- Example: `--flush-early true,false` benchmarks both flushEarly variants

**`--methods REGEX[,REGEX...]` (default: `.*`)**

- Comma-separated regex patterns passed to the k-perf plugin for method filtering

**`--ci-label LABEL` (default: `local`)**

- Label embedded in result directory names and JSON payloads for CI identification

Run `python3 benchmark.py --help` for full parameter documentation.

#### IOA Benchmark Parameters (`run.ps1`)

The IOA benchmark retains its PowerShell interface. See `game-of-life-kmp-commonmain-ioa/run.ps1` for its `-RepetitionCount`, `-CleanBuild`, `-StepCount`, `-Reference`, `-IOA`, `-JVM`, `-JS`, `-Native`, and `-CILabel` parameters.

### Advanced Usage

#### Combining Multiple Parameters (K-Perf)

```bash
# Run only CommonMain flushEarlyTrue variants with 100 repetitions and 50 simulation steps
cd benchmarking/game-of-life-kmp-k-perf
python3 benchmark.py --repetition-count 100 --step-count 50 --flush-early true --dedicated false

# Run IOA benchmark with faster iteration (25 repetitions, 5 steps) on JAR targets only, skip rebuild
cd benchmarking/game-of-life-kmp-commonmain-ioa
.\run.ps1 -RepetitionCount 25 -StepCount 5 -CleanBuild $false -JS $false -Native $false
```

#### Sequential Benchmark Runs

```bash
cd benchmarking/game-of-life-kmp-k-perf

# Run K-Perf with both flushEarly variants in one invocation
python3 benchmark.py --flush-early true,false --repetition-count 100 --step-count 25

# Skip rebuild on subsequent runs
python3 benchmark.py --flush-early true,false --repetition-count 100 --step-count 25 --clean-build false
```

#### Quick Testing Before Full Benchmark

```bash
# Test with minimal iterations to verify everything works
python3 benchmark.py --repetition-count 3 --step-count 5
```

## How Filters Work

The filtering system uses an **AND** logic: an executable is selected if **all its tags match at least one filter value**.

Each executable has a set of tags. For example:

- `commonmain-plain-jar` has tags: `["common", "jar"]`
- `commonmain-k-perf-flushEarlyTrue-jar` has tags: `["common", "flushEarlyTrue", "jar"]`
- `dedicatedmain-k-perf-flushEarlyFalse-exe` has tags: `["dedicated", "flushEarlyFalse", "native"]`

When you specify filters like `-Filters @("common", "jar")`, the script checks:

- **Tag "common"**: Is "common" present in the executable's tags? If not, skip this executable.
- **Tag "jar"**: Is "jar" present in the executable's tags? If not, skip this executable.

Only executables having both tags are selected.

### Filter Examples with K-Perf Benchmark

| Filters | Selected Executables | Count |
|---------|----------------------|-------|
| `@("common", "jar")` | All commonmain JARs (plain, flushEarlyTrue, flushEarlyFalse) | 3 |
| `@("dedicated", "native")` | All dedicatedmain native executables (plain, flushEarlyTrue, flushEarlyFalse) | 3 |
| `@("flushEarlyTrue")` | All flushEarlyTrue variants across all architectures and platforms (jar, js, native for both commonmain and dedicatedmain) | 6 |
| `@("common", "flushEarlyTrue", "jar")` | Only commonmain-k-perf-flushEarlyTrue-jar | 1 |
| `@("jar")` | All JAR targets regardless of architecture or optimization (plain + flushEarlyTrue + flushEarlyFalse for both commonmain and dedicatedmain) | 6 |
| All (default) | All 18 executables | 18 |

## Output Format

The benchmarks produce timestamped result directories in the measurements folder. For example:

```
measurements/2026_02_03_01_40_27_game-of-life-kmp-k-perf/
```

The timestamp format is: `YYYY_MM_DD_HH_mm_ss_benchmark-name`

Each directory contains one JSON file per executable that was benchmarked, named after the executable (e.g., `commonmain-plain-jar.json`).

### Example Result File Structure

```json
{
    "executable": "commonmain-plain-jar",
    "repetitions": 50,
    "times": [68942, 65363, 68848, ...],
    "statistics": {
        "mean": 70254.5,
        "median": 70654.0,
        "stddev": 1845.3,
        "min": 65363,
        "max": 74446,
        "ci95": {
            "lower": 69584.2,
            "upper": 70924.8
        }
    }
}
```

## Shared Infrastructure

Both benchmarks use shared components located in the parent `benchmarking/` folder.

### Python modules (used by `benchmark.py`)

- **`benchmark_types.py`**: Enums and dataclasses (`KPerfConfig`, `BenchmarkExecutable`, `BenchmarkStatistics`, etc.)
- **`gradle_utils.py`**: Gradle task discovery, timed task execution, KMP build orchestration
- **`statistics_utils.py`**: Statistical analysis (mean, median, stddev, CI95), machine info collection, CSV/JSON export
- **`build.py`**: Granular build functions for KIRHelperKit, k-perf plugin, and all Game of Life variants
- **`run.py`**: `invoke_benchmark_suite()` — the core measurement loop

### PowerShell modules (used by `run.ps1` in the IOA benchmark)

- **`types.ps1`**, **`gradle_utils.ps1`**, **`statistics_utils.ps1`**, **`build.ps1`** — the original PowerShell equivalents of the Python modules above, retained for the IOA benchmark.

## Troubleshooting

**No successful measurements / "Elapsed time not found"**

- Ensure the executable was built successfully and the path is correct
- Try `--clean-build true` to force a fresh build

**Build failures**

- Ensure you have JDK, Gradle, Node.js, and Kotlin compiler tools installed
- Check that the paths in the executable definitions match your build output structure
- Try running with `--clean-build true` to ensure a clean build state

**Missing executables after build**

- Some target platforms may not build on your system (e.g., native executables only build on supported platforms)
- Use `--jvm false`, `--js false`, or `--native false` to skip unavailable platforms
- Check the build output for compilation errors

## Example Workflows

### Benchmark a Single Configuration

```bash
cd benchmarking/game-of-life-kmp-k-perf
python3 benchmark.py --common true --dedicated false --jvm true --js false --native false \
    --flush-early true --repetition-count 100
```

### Compare Two flushEarly Strategies

```bash
cd benchmarking/game-of-life-kmp-k-perf
# Benchmark both flushEarly=true and flushEarly=false in one run
python3 benchmark.py --flush-early true,false --repetition-count 50
```

### Measure Instrumentation Overhead

```powershell
cd benchmarking/game-of-life-kmp-commonmain-ioa
.\run.ps1 -JVM $true -JS $false -Native $false -RepetitionCount 100 -StepCount 50
# Compare the commonmain-plain-jar.json vs commonmain-ioa-jar.json results
```

### Quick Smoke Test

```bash
cd benchmarking/game-of-life-kmp-k-perf
python3 benchmark.py --repetition-count 3 --step-count 5 --js false --native false
```
