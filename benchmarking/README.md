# Benchmarking Suite for Game of Life KMP

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

```powershell
cd benchmarking/game-of-life-kmp-k-perf
.\run.ps1
```

#### Instrumentation-Overhead-Analyzer Benchmark

```powershell
cd benchmarking/game-of-life-kmp-commonmain-ioa
.\run.ps1
```

Both will run with default parameters (50 repetitions, 10 simulation steps per execution, clean build enabled, all executables tested).

### Parameters

#### Common Parameters (Both Benchmarks)

**`-RepetitionCount` (default: 50)**

- Number of times each executable is benchmarked
- Higher values provide more statistical confidence but take longer
- Example: `-RepetitionCount 100` runs each executable 100 times

**`-CleanBuild` (default: $true)**

- Controls whether dependencies and applications are rebuilt
- Set to `$false` to skip building and use existing binaries
- Useful when running multiple benchmark variations sequentially
- Example: `-CleanBuild $false` skips the build phase

**`-StepCount` (default: 10)**

- Number of Game of Life simulation steps to execute per benchmark run
- This is the parameter passed to the Game of Life application as a command-line argument
- Lower values = shorter execution times (good for testing); higher values = more stable measurements
- Example: `-StepCount 20` simulates 20 steps per execution

**`-Filters` (default: benchmark-specific)**

- Selects which executables to benchmark based on tags
- Only executables matching ALL specified filter criteria are run
- See detailed filter options below

### Parameter Validation

The scripts validate that only expected parameters are provided. If an unexpected parameter is passed, the script will abort with an error message listing the valid parameters. This helps catch typos or outdated parameter names (e.g., using `-BenchmarkSteps` instead of `-StepCount`).

#### K-Perf Benchmark Filters

Available filter tags:

- **Architecture**: `common` (CommonMain), `dedicated` (DedicatedMain)
- **Optimization Strategy**: `flushEarlyTrue`, `flushEarlyFalse`
- **Target Platform**: `jar` (JVM), `js` (JavaScript/Node.js), `native` (Windows executable)

Default filters: all tags (runs 18 executables)

Example filter combinations:

```powershell
# Only run CommonMain JARs (3 executables: plain, flushEarlyTrue, flushEarlyFalse)
.\run.ps1 -Filters @("common", "jar")

# Only run DedicatedMain with flushEarlyFalse (3 executables: jar, js, native)
.\run.ps1 -Filters @("dedicated", "flushEarlyFalse")

# Only run native executables (6 executables: 3 commonmain, 3 dedicatedmain variants)
.\run.ps1 -Filters @("native")

# Run CommonMain flushEarlyTrue on all platforms (3 executables: jar, js, native)
.\run.ps1 -Filters @("common", "flushEarlyTrue")
```

#### Instrumentation-Overhead-Analyzer Benchmark Filters

Available filter tags:

- **Architecture**: `common` (CommonMain only)
- **Target Platform**: `jar` (JVM), `js` (JavaScript/Node.js), `native` (Windows executable)

Default filters: `common`, `jar`, `js`, `native` (runs all 6 executables by default; note: no IOA-instrumented native executable is currently available)

Example filter combinations:

```powershell
# Only run plain variants without instrumentation overlay (3 executables)
.\run.ps1 -Filters @("common")

# Only run JVM (JAR) targets for instrumentation overhead measurement (2 executables: plain, ioa)
.\run.ps1 -Filters @("jar")

# Only run JavaScript targets (2 executables: plain, ioa)
.\run.ps1 -Filters @("js")
```

### Advanced Usage

#### Combining Multiple Parameters

```powershell
# Run only CommonMain flushEarlyTrue variants with 100 repetitions and 50 simulation steps
cd benchmarking/game-of-life-kmp-k-perf
.\run.ps1 -RepetitionCount 100 -StepCount 50 -Filters @("common", "flushEarlyTrue")

# Run IOA benchmark with faster iteration (25 repetitions, 5 steps) on JAR targets only, skip rebuild
cd benchmarking/game-of-life-kmp-commonmain-ioa
.\run.ps1 -RepetitionCount 25 -StepCount 5 -CleanBuild $false -Filters @("jar")
```

#### Sequential Benchmark Runs

To compare different configurations without rebuilding each time:

```powershell
cd benchmarking/game-of-life-kmp-k-perf

# Run K-Perf with flushEarlyTrue (builds once)
.\run.ps1 -Filters @("common", "flushEarlyTrue") -RepetitionCount 100 -StepCount 25

# Run K-Perf with flushEarlyFalse (uses existing build)
.\run.ps1 -Filters @("common", "flushEarlyFalse") -RepetitionCount 100 -StepCount 25 -CleanBuild $false

# Run K-Perf with DedicatedMain (uses existing build)
.\run.ps1 -Filters @("dedicated", "jar") -RepetitionCount 100 -StepCount 25 -CleanBuild $false
```

#### Quick Testing Before Full Benchmark

```powershell
# Test with minimal iterations to verify everything works
.\run.ps1 -RepetitionCount 3 -StepCount 5
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

Both benchmarks use shared components located in the parent `benchmarking/` folder:

- **`utils.ps1`**: Common utility functions for:
  - Statistical analysis (mean, median, standard deviation, confidence intervals)
  - Gradle task discovery and execution
  - Kotlin Multiplatform compilation automation

- **`build.ps1`**: Granular build functions for:
  - Building KIRHelperKit
  - Building compiler plugins (K-Perf, Instrumentation-Overhead-Analyzer)
  - Compiling Game of Life variants (CommonMain, DedicatedMain, with/without instrumentation)

These are imported by both benchmark scripts via dot-sourcing:

```powershell
. "$PSScriptRoot\utils.ps1"
. "$PSScriptRoot\build.ps1"
```

## Troubleshooting

**"ERROR: No executables match the provided filters!"**

- You specified a filter combination that doesn't match any executables
- Check the available filter options displayed in the error message
- Use the default filter or review the examples above

**Build failures**

- Ensure you have JDK, Gradle, Node.js, and Kotlin compiler tools installed
- Check that the paths in the executable definitions match your build output structure
- Try running with `CleanBuild = $true` to ensure a clean build state

**Missing executables after build**

- Some target platforms may not build on your system (e.g., native executables only build on supported platforms)
- Use filters to skip unavailable platforms
- Check the build output for compilation errors

## Example Workflows

### Benchmark a Single Configuration

```powershell
cd benchmarking/game-of-life-kmp-k-perf
.\run.ps1 -Filters @("common", "flushEarlyTrue", "jar") -RepetitionCount 100
```

### Compare Two Optimization Strategies

```powershell
cd benchmarking/game-of-life-kmp-k-perf
# Run with flushEarlyTrue
.\run.ps1 -Filters @("flushEarlyTrue") -RepetitionCount 50
# Run with flushEarlyFalse
.\run.ps1 -Filters @("flushEarlyFalse") -RepetitionCount 50 -CleanBuild $false
```

### Measure Instrumentation Overhead

```powershell
cd benchmarking/game-of-life-kmp-commonmain-ioa
.\run.ps1 -Filters @("jar") -RepetitionCount 100 -StepCount 50
# Compare the plain_jar.json vs ioa_jar.json results
```

### Quick Smoke Test

```powershell
cd benchmarking/game-of-life-kmp-k-perf
.\run.ps1 -RepetitionCount 3 -StepCount 5 -Filters @("common", "jar")
```
