"""k-perf results loader and table-number reporter.

Usage::

    python kperf_numbers.py [<folder> ...] [options]

Arguments:
    folder   Optional measurement folders containing per-executable JSON files.
             If omitted, the latest k-perf folder in ../../measurements is used.

Options:
    --ignore-variants V ... Exclude listed variants (space-separated and/or comma chunks).
    --only-variant V ...    Include only listed variants (space-separated and/or comma chunks).
    --output FORMAT         Output format: csv or json (default: csv).

Output::

    {first_folder}/numbers.{csv|json}

Every value (plain and every k-perf variant) is computed exactly like the
plain/reference row of ``ioa.py`` (see ``ioa.baseline_mean_ci95``): the mean
per-function-call time in ns, after dropping ``ioa.WARMUP_STEPS`` warm-up
steps, with its one-sample 95% confidence-interval half-width.
"""

from __future__ import annotations

import argparse
import csv
import json
import re
import sys
from pathlib import Path

from ioa import (
    _detect_measurement_os,
    _native_target_specificity,
    _parse_kind_list as _parse_list,
    _target_group,
    baseline_mean_ci95,
)

TARGET_GROUPS = ("jvm", "js", "native")


def _parse_variant_target(name: str) -> dict | None:
    """Parse executable name into {variant, target}."""
    target_re = r"(jar|node|native|[a-z]+-exe|exe)$"

    m = re.fullmatch(r"commonmain-plain-" + target_re, name)
    if m:
        return {"variant": "plain", "target": m.group(1)}

    m = re.fullmatch(r"commonmain-k-perf-(.+)-" + target_re, name)
    if m:
        return {"variant": m.group(1), "target": m.group(2)}

    return None


def load_entries(folder: Path) -> list[dict]:
    entries: list[dict] = []
    for json_file in sorted(folder.glob("commonmain-*.json")):
        parsed = _parse_variant_target(json_file.stem)
        if parsed is None:
            continue
        with json_file.open(encoding="utf-8") as file:
            data = json.load(file)
        entries.append({**parsed, "step_times_all_runs": data.get("stepTimes", [])})
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
        })

        # Collapse per-folder duplicates into JVM/JS/Native groups, preferring
        # OS-specific native targets.
        selected: dict[tuple[str, str], dict] = {}
        for entry in folder_entries:
            group = _target_group(entry["target"])
            key = (entry["variant"], group)
            existing = selected.get(key)
            if existing is None or (
                group == "native"
                and _native_target_specificity(entry["target"]) > _native_target_specificity(existing["target"])
            ):
                selected[key] = {**entry, "measurement_id": measurement_id, "target_group": group}

        all_entries.extend(selected.values())

    return measurements, all_entries


def _find_latest_kperf_folder(measurements_root: Path) -> Path:
    candidates = [p for p in measurements_root.iterdir() if p.is_dir() and "_k-perf_" in p.name]
    if not candidates:
        raise FileNotFoundError(f"No k-perf measurement folders found in {measurements_root}")
    return max(candidates, key=lambda p: p.name)


def _column_keys(measurements: list[dict]) -> list[str]:
    return [f"{m['os_label']}_{group}" for m in measurements for group in TARGET_GROUPS]


def build_number_rows(
    measurements: list[dict],
    entries: list[dict],
    ignore_variants: set[str],
    only_variants: set[str],
) -> list[dict]:
    entry_map = {(e["measurement_id"], e["variant"], e["target_group"]): e for e in entries}

    def include_variant(variant: str) -> bool:
        normalized = variant.lower()
        if only_variants:
            return normalized in only_variants
        return normalized not in ignore_variants

    all_variants = {e["variant"] for e in entries if include_variant(e["variant"])}
    ordered_variants = ["plain"] if "plain" in all_variants else []
    ordered_variants += sorted(v for v in all_variants if v != "plain")

    rows: list[dict] = []
    for variant in ordered_variants:
        row: dict[str, float | str] = {"variant": variant}
        for measurement in measurements:
            for group in TARGET_GROUPS:
                key = f"{measurement['os_label']}_{group}"
                entry = entry_map.get((measurement["id"], variant, group))
                if entry is None:
                    row[key] = row[f"{key}_ci95"] = ""
                    continue
                mean_us, ci95_us = baseline_mean_ci95(entry)
                row[key] = mean_us * 1000.0  # ns
                row[f"{key}_ci95"] = ci95_us * 1000.0  # ns
        rows.append(row)

    return rows


def write_csv(path: Path, rows: list[dict], measurements: list[dict]) -> None:
    fieldnames = ["variant"]
    for key in _column_keys(measurements):
        fieldnames += [key, f"{key}_ci95"]

    with path.open("w", encoding="utf-8", newline="") as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerow({"variant": "unit:ns"})
        writer.writerows(rows)


def write_json(path: Path, rows: list[dict]) -> None:
    path.write_text(json.dumps({"unit": "ns", "rows": rows}, indent=2), encoding="utf-8")


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

    if args.folders:
        folders = [Path(folder) for folder in args.folders]
    else:
        measurements_root = Path(__file__).resolve().parents[2] / "measurements"
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

    rows = build_number_rows(
        measurements,
        entries,
        _parse_list(args.ignore_variants),
        _parse_list(args.only_variant),
    )

    out_path = folders[0] / f"numbers.{args.output}"
    if args.output == "csv":
        write_csv(out_path, rows, measurements)
    else:
        write_json(out_path, rows)

    print(f"Saved: {out_path}")


if __name__ == "__main__":
    main()
