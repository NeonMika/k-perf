---
name: familiarize
description: >
  Gain deep context on a file or task before starting work in the k-perf repo.
  Invoked with "/familiarize @SomeFile" or "/familiarize @SomeFile <task description>".
  Reads the target file(s), understands their role in the architecture, traces
  related code and tests, inspects git history, and surfaces open TODOs and
  issues — then delivers a structured briefing so work can start immediately.
---

# familiarize skill

## Overview

Before making changes it is important to understand what a file does, how it
fits into the broader codebase, what has changed recently, and what known
issues exist around it. This skill automates that research and produces a
structured briefing.

**Input forms:**

| Form | Example |
|---|---|
| File only | `/familiarize @Backend.ts` |
| File + task | `/familiarize @mapping.ts refactor arrow drawing` |
| Multiple files | `/familiarize @TraceState.d.ts @DebuggerProtocol.d.ts` |
| Task only | `/familiarize add array bounds highlighting to array view` |

---

## Step 1 — Parse the input

Extract from the user's message:

- **Target files** – every `@Name` token (e.g. `@mapping.ts`, `@Backend.kt`).
  Strip the `@` prefix; the name may or may not include a path.
- **Task description** – any text that is not a `@Name` token (may be empty).

Store both for use in later steps. If no `@Name` tokens are present, the task
description alone is the focus — proceed to Step 3 using the task description
to guide what to explore.

---

## Step 2 — Locate the file(s)

For each extracted file name, find its full path in the repository:

```powershell
# Fast glob-based search from repo root
Get-ChildItem -Recurse -File -Path "C:\Repos\k-perf" `
  -Filter "<FileName>" | Select-Object -ExpandProperty FullName
```

Alternatively use the **glob** tool with pattern `**/<FileName>`.

If the file name is ambiguous (multiple matches), list all candidates and pick
the most plausible one given the task description. Note the sub-project it
belongs to:

| Sub-project path | Role |
|---|---|
| `KIRHelperKit/` | Shared IR utility library consumed by both plugins |
| `plugins/k-perf/` | Main performance-tracing compiler plugin |
| `plugins/instrumentation-overhead-analyzer/` | Overhead-measurement companion plugin |
| `kmp-examples/<variant>/` | Sample KMP apps that exercise the plugin |
| `benchmarking/<suite>/` | PowerShell benchmark runners (not Gradle) |
| `analyzers/<tool>/` | Standalone post-processing tools (Python/HTML) |

---

## Step 3 — Read the target file(s)

Use the **view** tool to read each located file in full.

While reading, note:
- **Purpose** – what does this file/class/module do?
- **Key exports / public API** – functions, classes, types exported or exposed
- **Dependencies imported** – what does it depend on?
- **Non-obvious patterns** – anything that will need explaining when making changes

---

## Step 4 — Understand the architectural context

Based on the sub-project and file type, add the relevant architectural frame:

| Sub-project | Key architecture notes |
|---|---|
| `KIRHelperKit` | Shared IR helpers (e.g. `IrBuilderExtension`); JVM 1.8; no tests; **must be published first** (`./gradlew publishToMavenLocal`) before either plugin compiles |
| `plugins/k-perf` | `KPerfExtension : IrGenerationExtension` is the entry point; uses `IrElementTransformerVoidWithContext` to wrap eligible functions with `_enter_method`/`_exit_method` calls; synthetic fields/functions are attached to `moduleFragment.files[0]`; tests use kctfork for in-process compilation |
| `plugins/instrumentation-overhead-analyzer` | `InstrumentationOverheadAnalyzerExtension`; same structural pattern as k-perf plugin; measures the overhead of instrumentation itself |
| `kmp-examples/<variant>` | KMP Gradle project; consumes k-perf from mavenLocal; two architectures: `CommonMain` (single `fun main()` in `commonMain/`) and `DedicatedMain` (per-platform `main()` in each source set); `kotlinx-io-core` is injected by the plugin — do not add it to dependencies manually |
| `benchmarking/<suite>` | PowerShell scripts (`run.ps1`); not Gradle; parse `### Elapsed time:` from stdout; per-run output: per-executable `.json`, `_results.csv`, `_results.json`, call graph `.png` (k-perf suite only) |
| `analyzers/<tool>` | Standalone tools; no Gradle build; `call_graph_visualizer/graph-visualizer.py` reads `trace_*.txt` + `symbols_*.txt`; `measurements_plotter/index.html` reads `_results.json` |


---

## Step 5 — Explore related code

Depending on file type, gather additional context in parallel:

### Callers / usages
Search for the file's exported symbols being used elsewhere:

Or use the **grep** tool with appropriate glob patterns.

### Sibling files
List files in the same directory — related components, helpers, or fixtures
often live next to the file:

```powershell
Get-ChildItem -Path "<ParentDirectory>"
```

Or use the **view** tool on the parent directory.

## Step 6 — Inspect git history

Retrieve the last ~10 commits that touched the target file(s):

```powershell
git -C "C:\Repos\k-perf" --no-pager log --oneline -10 -- "<RelativeFilePath>"
```

For the most recent commit, show its diff:

```powershell
git -C "C:\Repos\k-perf" --no-pager show <SHA> -- "<RelativeFilePath>"
```

Note any patterns: was this file recently refactored, frequently changed, or
stable? Any commit messages that hint at known fragility?

---

## Step 7 — Surface TODOs and open issues

### In-code TODOs
Search the file (and immediate neighbours) for `TODO`, `FIXME`, `HACK`, `XXX`:

```powershell
Select-String -Path "<FilePath>" -Pattern "TODO|FIXME|HACK|XXX"
```

## Step 8 — Deliver the briefing

Produce a structured summary. Keep it dense but scannable — the goal is to
give an engineer everything they need to start confidently.

```
## Familiarization: <FileName(s)>  [<Sub-project>]

### Task
<Task description, or "general familiarization" if none provided>

### What this file does
<2–5 sentences describing purpose, key exports, main logic>

### Architecture fit
<How it connects to the rest of the system — callers, dependencies, pipeline stage>

### Related files
<List of directly relevant files found in Steps 3–5, with one-line notes>

### Tests
<Test files found; or "No direct tests found — covered by <integration test / none>">

### Recent git activity  (<N> commits)
<Bullet list of the last few commits: SHA (short), date, message>
<Flag if any commit introduced a known bug or TODO>

### Open TODOs / known issues
<Bullet list of in-code TODOs and relevant CHANGELOG/TODO.md items>

### Watch-outs for the task
<Any gotchas, fragile areas, or architectural constraints directly relevant to
the stated task — e.g. "array instrumentation IDs must stay stable across
recompiles", "graphviz port strings are assembled in mapping.ts, not the component">
```

If no task description was given, omit the "Watch-outs" section.

---

## Tips

- **Run Steps 3–7 in parallel** where possible — reading the file, checking
  git history, and searching for TODOs are independent.
- If the file is very large (> 500 lines), use `view` with a `view_range` to
  read the most relevant sections first, then expand as needed.
- If the `@Name` resolves to a directory rather than a file,
  treat all files in that directory as targets and summarise the directory as a
  whole module.
- Always note the sub-project — it determines which build/test commands apply
  and which architectural constraints are relevant (`KIRHelperKit`, `plugins/*`,
  `kmp-examples/*`, `benchmarking/*`, or `analyzers/*`).
