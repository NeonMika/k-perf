"""Runtime execution and benchmark suite orchestration.

For each executable:
  1. Runs repetition_count timed iterations, parsing "### Elapsed time: <µs>" from stdout
     for the total time and "!!! Elapsed time <n>: <µs>" for per-step times.
  2. Calls post_iteration_action (if provided) after each timed iteration, passing
     (executable, iteration_number, measurement_dir) as arguments.
  3. Computes statistics (mean, median, stddev, CI95) over collected total times.
  4. Writes a per-executable JSON file to measurement_dir/<name>.json.
  5. Accumulates a CSV record for the summary files.
After all executables: writes _results.csv and _results.json to measurement_dir.
Prints [X/N] progress with an ETA estimate after each executable completes.

Output format (per-executable JSON):
  {
    "parameters":  { ... }             # caller-supplied parameters dict
    "machineInfo": { ... }             # get_machine_info() result
    "buildTimeMs": <ms>|null
    "executable":  "<name>"
    "repetitions": <N>
    "timeUnit":    "microseconds"
    "times":       [<µs>, ...]         # total elapsed time per repetition
    "stepTimes":   [[<µs>, ...], ...]  # per-step times; outer=repetitions, inner=steps
    "statistics":  { count, mean, median, stddev, min, max, ci95 }
    "status":      "ok" | "failed"     # "failed" when no successful measurements
  }
"""

from __future__ import annotations

import json
import re
import subprocess
import sys
import time
from pathlib import Path
from typing import Any, Callable, Optional

from benchmark_types import BenchmarkExecutable, ExecutableType
from statistics_utils import (
    build_benchmark_csv_record,
    export_benchmark_results_to_csv,
    export_benchmark_results_to_json,
    get_benchmark_statistics,
)


def _json_serialise(obj: Any) -> Any:
    if hasattr(obj, "__dict__"):
        return obj.__dict__
    return str(obj)


def invoke_benchmark_suite(
    executables: list[BenchmarkExecutable],
    repetition_count: int,
    step_count: int,
    measurement_dir: Path,
    machine_info: dict[str, Any],
    build_times: Optional[dict[str, float]] = None,
    parameters: Optional[dict] = None,
    clean_build: bool = False,
    warmup_count: int = 0,
    post_iteration_action: Optional[Callable] = None,
) -> None:
    if build_times is None:
        build_times = {}
    if parameters is None:
        parameters = {}

    # Validate that all executables exist before starting any measurements
    print()
    print("==========================================")
    print("## Validating Executables...")
    print("==========================================")

    missing: list[BenchmarkExecutable] = []
    for executable in executables:
        file_path = Path(executable.path)
        if file_path.exists():
            print(f"OK: Found: {executable.name} at {file_path}")
        else:
            print(f"ERROR: NOT FOUND: {executable.name} at {file_path}")
            missing.append(executable)

    if missing:
        print()
        print("ERROR: The following executables were not found:")
        for m in missing:
            print(f"  - {m.name}")
            print(f"    expected at: {m.path}")
        print()
        print("Cannot proceed with benchmarking. Please check the build output above.")
        sys.exit(1)

    print()
    print("All executables found! Proceeding with benchmarks...")
    print("==========================================")

    csv_records: list[dict] = []
    total_executables = len(executables)
    completed_executables = 0
    benchmark_start_time = time.monotonic()

    for executable in executables:
        file_path = executable.path
        file_type = executable.type

        print()
        print("--------------------------------------------------------")

        # Warmup iterations (output discarded)
        if warmup_count > 0:
            print(f"[WARMUP] Running {warmup_count} warmup iteration(s) for {executable.name}...")
            for w in range(1, warmup_count + 1):
                try:
                    cmd = _build_command(file_type, file_path, step_count)
                    subprocess.run(cmd, capture_output=True)
                except Exception as e:
                    print(f"[WARMUP] Iteration {w} failed: {e}")
            print("[WARMUP] Done.")

        # Timed iterations
        elapsed_times: list[float] = []
        step_times_all_reps: list[list[float]] = []

        for i in range(1, repetition_count + 1):
            print()
            print(f"Running iteration {i} of {repetition_count} for {executable.name}:")

            output_text: Optional[str] = None
            execution_success = False

            try:
                cmd = _build_command(file_type, file_path, step_count)
                result = subprocess.run(cmd, capture_output=True, text=True)
                output_text = result.stdout
                execution_success = result.returncode == 0
            except Exception as e:
                print(f"ERROR: Failed to execute {executable.name}: {e}")
                execution_success = False

            if not execution_success:
                print(f"ERROR: Execution failed for iteration {i}")
                continue

            elapsed_line = re.search(r"^### Elapsed time:\s*(.+)$", output_text or "", re.MULTILINE)
            if elapsed_line:
                elapsed_us = float(elapsed_line.group(1).strip())
                print(f"- Ran {elapsed_us / 1000:.3f} ms")
                elapsed_times.append(elapsed_us)

                step_times_this_rep: list[float] = []
                for step_match in re.finditer(
                    r"^!!! Elapsed time \d+:\s*(.+)$", output_text or "", re.MULTILINE
                ):
                    step_times_this_rep.append(float(step_match.group(1).strip()))
                step_times_all_reps.append(step_times_this_rep)
            else:
                print(f"- Elapsed time not found in iteration {i}")

            if post_iteration_action is not None:
                post_iteration_action(executable, i, measurement_dir)

        # Warn if no measurements collected
        if not elapsed_times:
            print()
            print("========================================================")
            print(f"[WARN] No successful measurements for {executable.name}")
            print("========================================================")

        # Compute statistics and write per-executable JSON
        output_file_path = measurement_dir / f"{executable.name}.json"
        stats = get_benchmark_statistics(elapsed_times)
        relevant_build_time = build_times.get(executable.name)
        status = "failed" if not elapsed_times else "ok"

        payload = {
            "parameters": parameters,
            "machineInfo": machine_info,
            "buildTimeMs": relevant_build_time,
            "executable": executable.name,
            "repetitions": repetition_count,
            "timeUnit": "microseconds",
            "times": elapsed_times,
            "stepTimes": step_times_all_reps,
            "statistics": stats,
            "status": status,
        }

        with open(output_file_path, "w", encoding="utf-8") as f:
            json.dump(payload, f, indent=4, default=_json_serialise)

        print(f"Results saved to {output_file_path}")

        csv_record = build_benchmark_csv_record(
            executable_name=executable.name,
            statistics=stats,
            machine_info=machine_info,
            repetition_count=repetition_count,
            clean_build=clean_build,
            step_count=step_count,
            build_time=relevant_build_time,
            additional_parameters=parameters,
        )
        csv_records.append(csv_record)

        # Progress and ETA
        completed_executables += 1
        elapsed_sec = time.monotonic() - benchmark_start_time
        avg_per_exec = elapsed_sec / completed_executables
        remaining_sec = (total_executables - completed_executables) * avg_per_exec
        print()
        print(
            f"[{completed_executables}/{total_executables}] Completed: {executable.name}"
            f"  (estimated ~{remaining_sec:.0f}s remaining)"
        )

    # Write summary files
    csv_file_path = measurement_dir / "_results.csv"
    json_file_path = measurement_dir / "_results.json"

    export_benchmark_results_to_csv(csv_records, csv_file_path)
    export_benchmark_results_to_json(csv_records, json_file_path)

    print()
    print("==========================================")
    print("# All benchmarks complete.")
    print(f"  Summary: {csv_file_path}")
    print(f"  Summary: {json_file_path}")
    print("==========================================")


def _build_command(file_type: ExecutableType, file_path: str, step_count: int) -> list[str]:
    if file_type == ExecutableType.Jar:
        return ["java", "-jar", file_path, str(step_count)]
    elif file_type == ExecutableType.Node:
        return ["node", file_path, str(step_count)]
    else:
        return [file_path, str(step_count)]
