"""k-perf results loader and table-number reporter.

Usage::

    python kperf_numbers.py [<folder> ...] [options]

Arguments:
    folder   Optional measurement folders containing per-executable JSON files.
             If omitted, the latest k-perf folder in ../../measurements is used.

Options:
    --transformation NAME   Number transformation (default: runtime).
                            Available: runtime, function_runtime, function_overhead
    --steps-until N         Only consider the first N steps of each run (mutually
                            exclusive with --steps-skip).
    --steps-skip N          Skip the first N steps of each run (mutually exclusive
                            with --steps-until).
    --ignore-variants V ... Exclude listed variants (space-separated and/or comma chunks).
    --only-variant V ...    Include only listed variants (space-separated and/or comma chunks).
    --output FORMAT         Output format: csv or json (default: csv).

Output::

    {first_folder}/numbers-{transformation}-{steps|full}.{csv|json}
"""

from __future__ import annotations

import argparse
import csv
import json
import re
import sys
from pathlib import Path
from typing import Iterable


def _parse_variant_target(name: str) -> dict | None:
    """Parse executable name into {variant, target, is_reference}."""
    target_re = r"(jar|node|native|[a-z]+-exe|exe)$"

    plain_match = re.fullmatch(r"commonmain-plain-" + target_re, name)
    if plain_match:
        return {
            "variant": "plain",
            "target": plain_match.group(1),
            "is_reference": True,
        }

    kperf_match = re.fullmatch(r"commonmain-k-perf-(.+)-" + target_re, name)
    if kperf_match:
        return {
            "variant": kperf_match.group(1),
            "target": kperf_match.group(2),
            "is_reference": False,
        }

    return None


def _target_group(target: str) -> str:
    if target == "jar":
        return "jvm"
    if target == "node":
        return "js"
    return "native"


def _native_target_specificity(target: str) -> int:
    if target in {"linux-exe", "win-exe", "windows-exe", "mac-exe", "macos-exe"}:
        return 2
    if target in {"native", "exe"}:
        return 1
    return 0


def _detect_measurement_os(folder: Path, entries: list[dict]) -> str:
    targets = {entry["target"] for entry in entries}
    if {"win-exe", "windows-exe"} & targets:
        return "Windows"
    if "linux-exe" in targets:
        return "Linux"
    if {"mac-exe", "macos-exe"} & targets:
        return "macOS"

    name = folder.name.lower()
    if "windows" in name or re.search(r"\bwin\b", name):
        return "Windows"
    if "linux" in name:
        return "Linux"
    if "macos" in name or re.search(r"\bmac\b", name):
        return "macOS"
    return "Unknown"


def load_entries(folder: Path) -> list[dict]:
    entries: list[dict] = []
    for json_file in sorted(folder.glob("commonmain-*.json")):
        parsed = _parse_variant_target(json_file.stem)
        if parsed is None:
            continue

        with json_file.open(encoding="utf-8") as file:
            data = json.load(file)

        stats = data.get("statistics", {})
        step_times_all_runs: list[list[float]] = data.get("stepTimes", [])
        entries.append({
            **parsed,
            "overall_mean_us": stats.get("mean", 0.0),
            "step_times_all_runs": step_times_all_runs,
            "step_count": len(step_times_all_runs[0]) if step_times_all_runs else 0,
        })

    return entries


def load_entries_for_measurements(folders: list[Path]) -> tuple[list[dict], list[dict]]:
    measurements: list[dict] = []
    all_entries: list[dict] = []

    for idx, folder in enumerate(folders):
        folder_entries = load_entries(folder)
        measurement_id = f"m{idx}"
        measurements.append({
            "id": measurement_id,
            "os_label": _detect_measurement_os(folder, folder_entries),
            "folder": folder,
        })

        selected: dict[tuple[str, str], dict] = {}
        for entry in folder_entries:
            group = _target_group(entry["target"])
            key = (entry["variant"], group)
            candidate = {
                **entry,
                "measurement_id": measurement_id,
                "target_group": group,
            }

            existing = selected.get(key)
            if existing is None:
                selected[key] = candidate
                continue

            if group == "native" and _native_target_specificity(entry["target"]) > _native_target_specificity(existing["target"]):
                selected[key] = candidate

        all_entries.extend(selected.values())

    return measurements, all_entries


def _parse_list(values: Iterable[str] | None) -> set[str]:
    if values is None:
        return set()

    parsed: set[str] = set()
    for value in values:
        for token in value.split(","):
            token = token.strip().lower()
            if token:
                parsed.add(token)
    return parsed


def compute_value(entry: dict, steps_until: int | None, steps_skip: int | None) -> tuple[float, int, bool]:
    raw_step_count: int = entry["step_count"]
    if steps_until is None and steps_skip is None:
        return entry["overall_mean_us"], raw_step_count, False

    step_times_all_runs: list[list[float]] = entry["step_times_all_runs"]
    if not step_times_all_runs:
        return 0.0, 0, True

    if steps_until is not None:
        filtered_runs = [run[:steps_until] for run in step_times_all_runs]
        effective_steps = min(steps_until, raw_step_count)
    else:
        filtered_runs = [run[steps_skip:] for run in step_times_all_runs]
        effective_steps = max(0, raw_step_count - steps_skip)

    all_step_times = [time for run in filtered_runs for time in run]
    if not all_step_times:
        return 0.0, effective_steps, True

    return sum(all_step_times) / len(all_step_times), effective_steps, True


def _func_call_count(is_step_avg: bool, step_count: int) -> int:
    calls = 4001 * step_count
    if not is_step_avg:
        calls += 14
    return calls


def _func_time_us(value_us: float, is_step_avg: bool, step_count: int) -> float:
    calls = _func_call_count(is_step_avg, step_count)
    return value_us / calls if calls > 0 else 0.0


def _transform_runtime(_baseline_us: float, value_us: float, _is_step_avg: bool, _step_count: int, _is_baseline: bool) -> float:
    return value_us / 1000.0  # ms


def _transform_function_runtime(
    _baseline_us: float,
    value_us: float,
    is_step_avg: bool,
    step_count: int,
    _is_baseline: bool,
) -> float:
    return _func_time_us(value_us, is_step_avg, step_count) * 1000.0  # ns


def _transform_function_overhead(
    baseline_us: float,
    value_us: float,
    is_step_avg: bool,
    step_count: int,
    is_baseline: bool,
) -> float:
    if is_baseline:
        return _func_time_us(value_us, is_step_avg, step_count) * 1000.0  # ns

    baseline_ns = _func_time_us(baseline_us, is_step_avg, step_count) * 1000.0
    value_ns = _func_time_us(value_us, is_step_avg, step_count) * 1000.0
    return value_ns - baseline_ns


TRANSFORMATIONS = {
    "runtime": _transform_runtime,
    "function_runtime": _transform_function_runtime,
    "function_overhead": _transform_function_overhead,
}

TRANSFORMATION_UNITS = {
    "runtime": "ms",
    "function_runtime": "ns",
    "function_overhead": "ns",
}


def _find_latest_kperf_folder(measurements_root: Path) -> Path:
    candidates = [
        path for path in measurements_root.iterdir()
        if path.is_dir() and "_k-perf_" in path.name
    ]
    if not candidates:
        raise FileNotFoundError(f"No k-perf measurement folders found in {measurements_root}")
    return max(candidates, key=lambda path: path.name)


def build_number_rows(
    measurements: list[dict],
    entries: list[dict],
    steps_until: int | None,
    steps_skip: int | None,
    transformation: str,
    ignore_variants: set[str],
    only_variants: set[str],
) -> tuple[list[dict], str]:
    entry_map: dict[tuple[str, str, str], dict] = {
        (entry["measurement_id"], entry["variant"], entry["target_group"]): entry
        for entry in entries
    }

    def include_variant(variant: str) -> bool:
        normalized = variant.lower()
        if only_variants:
            return normalized in only_variants
        if ignore_variants:
            return normalized not in ignore_variants
        return True

    all_variants = {entry["variant"] for entry in entries if include_variant(entry["variant"])}
    ordered_variants = ["plain"] if "plain" in all_variants else []
    ordered_variants.extend(sorted(variant for variant in all_variants if variant != "plain"))

    transform = TRANSFORMATIONS[transformation]
    target_groups = ("jvm", "js", "native")
    output_rows: list[dict] = []

    for variant in ordered_variants:
        row: dict[str, float | str] = {"variant": variant}
        for measurement in measurements:
            for group in target_groups:
                value_key = f"{measurement['os_label']}_{group}"
                value_entry = entry_map.get((measurement["id"], variant, group))
                baseline_entry = entry_map.get((measurement["id"], "plain", group))
                if value_entry is None:
                    row[value_key] = ""
                    continue

                value_us, step_count, is_step_avg = compute_value(value_entry, steps_until, steps_skip)
                baseline_us = value_us
                if baseline_entry is not None:
                    baseline_us, _, _ = compute_value(baseline_entry, steps_until, steps_skip)

                is_baseline = variant == "plain"
                row[value_key] = transform(baseline_us, value_us, is_step_avg, step_count, is_baseline)

        output_rows.append(row)

    return output_rows, TRANSFORMATION_UNITS[transformation]


def write_csv(path: Path, rows: list[dict], measurements: list[dict], unit: str) -> None:
    target_groups = ("jvm", "js", "native")
    fieldnames = ["variant"]
    for measurement in measurements:
        fieldnames.extend(f"{measurement['os_label']}_{group}" for group in target_groups)

    with path.open("w", encoding="utf-8", newline="") as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerow({"variant": f"unit:{unit}"})
        for row in rows:
            writer.writerow(row)


def write_json(path: Path, rows: list[dict], unit: str) -> None:
    payload = {
        "unit": unit,
        "rows": rows,
    }
    path.write_text(json.dumps(payload, indent=2), encoding="utf-8")


def main() -> None:
    parser = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument(
        "folders",
        nargs="*",
        help="Optional k-perf measurement folders. If omitted, latest k-perf folder is used.",
    )
    parser.add_argument(
        "--transformation",
        choices=list(TRANSFORMATIONS),
        default="runtime",
        help="Number transformation (default: runtime)",
    )

    steps_group = parser.add_mutually_exclusive_group()
    steps_group.add_argument(
        "--steps-until",
        metavar="N",
        type=int,
        help="Only consider the first N steps of each run",
    )
    steps_group.add_argument(
        "--steps-skip",
        metavar="N",
        type=int,
        help="Skip the first N steps of each run",
    )

    variant_filter_group = parser.add_mutually_exclusive_group()
    variant_filter_group.add_argument(
        "--ignore-variants",
        nargs="+",
        metavar="VARIANT",
        help="Variants to exclude (space-separated and/or comma-separated chunks).",
    )
    variant_filter_group.add_argument(
        "--only-variant",
        nargs="+",
        metavar="VARIANT",
        help="Variants to include (space-separated and/or comma-separated chunks).",
    )

    parser.add_argument(
        "--output",
        choices=["csv", "json"],
        default="csv",
        help="Output format (default: csv)",
    )

    args = parser.parse_args()

    repo_root = Path(__file__).resolve().parents[2]
    measurements_root = repo_root / "measurements"

    if args.folders:
        folders = [Path(folder) for folder in args.folders]
    else:
        folders = [_find_latest_kperf_folder(measurements_root)]

    for folder in folders:
        if not folder.is_dir():
            print(f"Error: {folder} is not a directory", file=sys.stderr)
            sys.exit(1)

    measurements, entries = load_entries_for_measurements(folders)
    if not entries:
        joined = ", ".join(str(folder) for folder in folders)
        print(f"No recognised JSON files found in: {joined}", file=sys.stderr)
        sys.exit(1)

    ignore_variants = _parse_list(args.ignore_variants)
    only_variants = _parse_list(args.only_variant)

    rows, unit = build_number_rows(
        measurements,
        entries,
        args.steps_until,
        args.steps_skip,
        args.transformation,
        ignore_variants,
        only_variants,
    )

    steps_suffix = "full" if args.steps_until is None and args.steps_skip is None else "steps"
    out_extension = "csv" if args.output == "csv" else "json"
    out_path = folders[0] / f"numbers-{args.transformation}-{steps_suffix}.{out_extension}"

    if args.output == "csv":
        write_csv(out_path, rows, measurements, unit)
    else:
        write_json(out_path, rows, unit)

    print(f"Saved: {out_path}")


if __name__ == "__main__":
    main()
