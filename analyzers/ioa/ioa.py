"""IOA results loader and LaTeX table generator.

Usage::

    python ioa.py <folder> [<folder> ...] [options]

Arguments:
    folder   One or more measurement folders containing per-executable JSON files.
             Every specified folder contributes JVM, JS, and Native outputs.

Options:
    --transformation NAME   Transformation to apply to cell values (default: runtime).
                            Available: runtime, function_runtime, function_overhead
    --steps-until N         Only consider the first N steps of each run (mutually
                            exclusive with --steps-skip).
    --steps-skip N          Skip the first N steps of each run (mutually exclusive
                            with --steps-until).
    --ignore-kinds KIND ... Exclude listed kinds from output rows.
    --only-kind KIND ...    Include only listed kinds in output rows.
    --function-overhead-yellow-threshold-us N
                            Yellow threshold for function_overhead colouring (default: 0.005 µs = 5 ns).
    --function-overhead-red-threshold-us N
                            Red threshold for function_overhead colouring (default: 0.015 µs = 15 ns).
    --ignore-for-paper      Apply paper ignore-kind preset.

Output::

    {first_folder}/latex-{transformation}-{steps|full}.tex

Example::

    python ioa.py ../../measurements/2026_04_23_13_54_53_... --transformation function_runtime
    python ioa.py ../../measurements/2026_04_23_13_54_53_... --steps-skip 1 --transformation function_overhead

Per-step JSON files loaded (inside the folder):
    commonmain-ioa-kind-{kind}-{target}.json
    commonmain-plain-{target}.json

Each such JSON has a ``stepTimes`` array: one entry per run, each entry is an
array of per-step times (in microseconds).
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path
from typing import Callable, Iterable


# ---------------------------------------------------------------------------
# Display name helpers
# ---------------------------------------------------------------------------

_KIND_LABELS: dict[str, str] = {
    "plain":                                    "Plain (reference)",
    "ioa":                                      "IOA",
    "none":                                     "None",
    "tryfinally":                               "Try Finally",
    "timeclock":                                "Time -- Clock",
    "timemonotonicfunction":                    "Time -- Monotonic Function",
    "timemonotonicfunctioninwholemilliseconds": "Time -- Monotonic Function (ms)",
    "timemonotonicfunctioninwholemicroseconds": "Time -- Monotonic Function ($\\mu$s)",
    "timemonotonicfunctioninwholenanoseconds":  "Time -- Monotonic Function (ns)",
    "timemonotonicglobal":                      "Time -- Monotonic Global",
    "timemonotonicglobalinwholemilliseconds":   "Time -- Monotonic Global (ms)",
    "timemonotonicglobalinwholemicroseconds":   "Time -- Monotonic Global ($\\mu$s)",
    "timemonotonicglobalinwholenanoseconds":    "Time -- Monotonic Global (ns)",
    "timemonotonicglobalreducedobjects":        "Time -- Monotonic Global (reduced obj.)",
    "incrementintcounter":                      "Increment Int Counter",
    "incrementatomicintcounter":                "Increment Atomic Int Counter",
    "randomvalue":                              "Random Value",
    "standardout":                              "Standard Out",
    "appendtostringbuilder":                    "Append to String Builder",
    "fileeagerflush":                           "File (Eager Flush)",
    "filelazyflush":                            "File (Lazy Flush)",
    "addtolist":                                "Add to List",
    "addduplicatestoset":                       "Add Duplicates to Set",
    "adduniquetoset":                           "Add Unique to Set",
    "poctryfinallyincrementint":                "Proof of Concept",
}

_TARGET_LABELS: dict[str, str] = {
    "jar":         "JVM",
    "node":        "JS (Node)",
    "native":      "Native",
    "linux-exe":   "Native (Linux)",
    "windows-exe": "Native (Windows)",
    "win-exe":     "Native (Windows)",
    "macos-exe":   "Native (macOS)",
    "mac-exe":     "Native (macOS)",
    "exe":         "Native",
}

# Canonical kind order matching IoaKind.kt, plain reference first
KIND_ORDER: list[str] = [
    "plain",
    "none", "tryfinally",
    "timeclock",
    "timemonotonicfunction",
    "timemonotonicfunctioninwholemilliseconds",
    "timemonotonicfunctioninwholemicroseconds",
    "timemonotonicfunctioninwholenanoseconds",
    "timemonotonicglobal",
    "timemonotonicglobalinwholemilliseconds",
    "timemonotonicglobalinwholemicroseconds",
    "timemonotonicglobalinwholenanoseconds",
    "timemonotonicglobalreducedobjects",
    "incrementintcounter", "incrementatomicintcounter",
    "randomvalue",
    "standardout", "appendtostringbuilder",
    "fileeagerflush", "filelazyflush",
    "addtolist", "addduplicatestoset", "adduniquetoset",
]

def kind_label(kind: str) -> str:
    """Return a human-readable display name for an IOA kind token."""
    label = _KIND_LABELS.get(kind.lower())
    if label:
        return label
    spaced = re.sub(r"([a-z])([A-Z])", r"\1 \2", kind)
    return " ".join(w.capitalize() for w in re.split(r"[\s_\-]+", spaced))


def target_label(target: str) -> str:
    """Return a human-readable display name for a target token."""
    if target in _TARGET_LABELS:
        return _TARGET_LABELS[target]
    if target.endswith("-exe"):
        os_part = target[:-4].replace("-", " ").title()
        return f"Native ({os_part})"
    return target.title()


# ---------------------------------------------------------------------------
# Executable name parsing
# ---------------------------------------------------------------------------

def _parse_executable(name: str) -> dict | None:
    """Return {kind, target, is_reference} parsed from an executable name.

    Supported patterns:
        commonmain-plain-{target}
        commonmain-ioa-kind-{kind}-{target}

    Targets: jar, node, native/exe, or <os>-exe (e.g. linux-exe, win-exe, mac-exe).
    Returns None for unrecognised names.
    """
    _TARGET_RE = r"(jar|node|native|[a-z]+-exe|exe)$"

    m = re.fullmatch(r"commonmain-plain-" + _TARGET_RE, name)
    if m:
        return {"kind": "plain", "target": m.group(1), "is_reference": True}

    m = re.fullmatch(r"commonmain-ioa-kind-(.+?)-" + _TARGET_RE, name)
    if m:
        return {"kind": m.group(1), "target": m.group(2), "is_reference": False}

    return None


# ---------------------------------------------------------------------------
# Data loading
# ---------------------------------------------------------------------------

def load_entries(folder: Path) -> list[dict]:
    """Load all per-executable JSON files from a measurement folder.

    Each returned entry dict contains:
        kind, target, is_reference        — from the filename
        overall_mean_us, overall_stddev_us — from statistics block
        step_times_all_runs               — list[list[float]], µs per step per run
        step_count                        — steps per run (from first run)
    """
    entries: list[dict] = []
    for json_file in sorted(folder.glob("commonmain-*.json")):
        parsed = _parse_executable(json_file.stem)
        if parsed is None:
            continue
        with json_file.open(encoding="utf-8") as f:
            data = json.load(f)

        stats = data.get("statistics", {})
        step_times_all_runs: list[list[float]] = data.get("stepTimes", [])

        entries.append({
            **parsed,
            "overall_mean_us":   stats.get("mean",   0.0),
            "overall_stddev_us": stats.get("stddev", 0.0),
            "step_times_all_runs": step_times_all_runs,
            "step_count": len(step_times_all_runs[0]) if step_times_all_runs else 0,
        })

    return entries


def _target_group(target: str) -> str:
    """Map concrete executable target names to JVM/JS/Native column groups."""
    if target == "jar":
        return "jvm"
    if target == "node":
        return "js"
    return "native"


def _native_target_specificity(target: str) -> int:
    """Prefer OS-specific native targets over generic native/exe targets."""
    if target in {"linux-exe", "win-exe", "windows-exe", "mac-exe", "macos-exe"}:
        return 2
    if target in {"native", "exe"}:
        return 1
    return 0


def _detect_measurement_os(folder: Path, entries: list[dict]) -> str:
    """Infer an OS label for a measurement folder."""
    targets = {e["target"] for e in entries}
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


def load_entries_for_measurements(folders: list[Path]) -> tuple[list[dict], list[dict]]:
    """Load entries grouped by measurement folder.

    Returns:
        measurements: [{id, os_label, folder}]
        entries:      [{..., measurement_id, target_group}]
    """
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

        # Collapse per-folder duplicates into JVM/JS/Native groups.
        selected: dict[tuple[str, str], dict] = {}
        for entry in folder_entries:
            group = _target_group(entry["target"])
            key = (entry["kind"], group)
            candidate = {
                **entry,
                "measurement_id": measurement_id,
                "target_group": group,
            }

            existing = selected.get(key)
            if existing is None:
                selected[key] = candidate
                continue

            # For native duplicates, prefer OS-specific entries.
            if group == "native" and _native_target_specificity(entry["target"]) > _native_target_specificity(existing["target"]):
                selected[key] = candidate

        all_entries.extend(selected.values())

    return measurements, all_entries


def compute_value(
    entry: dict,
    steps_until: int | None,
    steps_skip: int | None,
) -> tuple[float, int, bool]:
    """Compute (value_µs, effective_step_count, is_step_avg) for an entry.

    If neither filter is set, returns the overall mean and is_step_avg=False.
    Otherwise computes the mean of per-step times after applying the filter,
    and returns is_step_avg=True.
    """
    raw_step_count: int = entry["step_count"]

    if steps_until is None and steps_skip is None:
        return entry["overall_mean_us"], raw_step_count, False

    step_times_all_runs: list[list[float]] = entry["step_times_all_runs"]
    if not step_times_all_runs:
        return 0.0, 0, True

    if steps_until is not None:
        filtered_runs = [run[:steps_until] for run in step_times_all_runs]
        effective_steps = min(steps_until, raw_step_count)
    else:  # steps_skip
        filtered_runs = [run[steps_skip:] for run in step_times_all_runs]
        effective_steps = max(0, raw_step_count - steps_skip)

    all_step_times = [t for run in filtered_runs for t in run]
    if not all_step_times:
        return 0.0, effective_steps, True

    mean_val = sum(all_step_times) / len(all_step_times)
    return mean_val, effective_steps, True


# ---------------------------------------------------------------------------
# Transformations
# ---------------------------------------------------------------------------

TransformFn = Callable[[float, float, bool, int, bool], tuple[str, str | None]]
"""Signature: (baseline_us, value_us, is_step_avg, step_count, is_baseline) -> (latex_text, color_or_none)

baseline_us  : mean µs of the plain/reference run under the same conditions
value_us     : mean µs of the measured kind under the same conditions
is_step_avg  : True when --steps-until or --steps-skip was used (per-step mean)
               False for overall run measurement
step_count   : number of steps contributing to the value
               (for overall: total steps in first run)
is_baseline  : True when this row is the plain/reference baseline itself
color_or_none: xcolor name (e.g. "yellow!50", "red!30") or None for no highlight
"""

DEFAULT_FUNCTION_OVERHEAD_YELLOW_THRESHOLD_US = 0.005
DEFAULT_FUNCTION_OVERHEAD_RED_THRESHOLD_US = 0.015

IGNORE_KINDS_FOR_PAPER = {
    "timemonotonicfunctioninwholemicroseconds",
    "timemonotonicfunctioninwholenanoseconds",
    "timemonotonicglobalinwholemilliseconds",
    "timemonotonicglobalinwholemicroseconds",
    "timemonotonicglobalinwholenanoseconds",
    "timemonotonicglobalreducedobjects",
    "poctryfinallyincrementint",
}

def _fmt_us(us: float, target: str | None = None) -> str:
    """Format a µs value as a compact string suitable for LaTeX cells.

    Without an explicit target unit, this chooses the largest fitting unit
    (s/ms/µs/ns), so 1000 ns is rendered as 1 µs.
    """
    ms = us / 1000.0
    s = ms / 1000.0
    ns = us * 1000.0

    if (s >= 1 and target is None) or target == "s":
        return f"{s:.2f}\\,s"

    if (ms >= 1 and target is None) or target == "ms":
        return f"{ms:.2f}\\,ms"

    if (us >= 1 and target is None) or target == "µs":
        return f"{us:.2f}\\,$\\mu$s"

    if (ns >= 1 and target is None) or target == "ns":
        return f"{ns:.2f}\\,ns"

    return f"{us:.2f}\\,µs"


def _func_call_count(is_step_avg: bool, step_count: int) -> int:
    """Number of traced function calls for the given measurement mode.

    For step-filtered mode (`--steps-skip`/`--steps-until`), the input value is
    already a per-step mean, so use only 4001 calls.
    For full-run mode, use total traced calls: 4001 * step_count + 14.
    """
    if is_step_avg:
        return 4001

    return 4001 * step_count + 14


def _func_time_us(value_us: float, is_step_avg: bool, step_count: int) -> float:
    """Return the per-function-call time in µs."""
    n = _func_call_count(is_step_avg, step_count)
    return value_us / n if n > 0 else 0.0


def _transform_runtime(
    baseline: float, value: float, is_step_avg: bool, step_count: int, is_baseline: bool
) -> tuple[str, str | None]:
    """Show the raw runtime value; no cell colouring."""
    return _fmt_us(value, "ms"), None


def _transform_function_runtime(
    baseline: float, value: float, is_step_avg: bool, step_count: int, is_baseline: bool
) -> tuple[str, str | None]:
    """Show per-function-call time (runtime / number of traced calls); no colouring."""
    ft = _func_time_us(value, is_step_avg, step_count)
    return _fmt_us(ft), None


def _transform_function_overhead(
    baseline: float,
    value: float,
    is_step_avg: bool,
    step_count: int,
    is_baseline: bool,
    *,
    yellow_threshold_us: float = DEFAULT_FUNCTION_OVERHEAD_YELLOW_THRESHOLD_US,
    red_threshold_us: float = DEFAULT_FUNCTION_OVERHEAD_RED_THRESHOLD_US,
) -> tuple[str, str | None]:
    """Show per-function overhead vs plain baseline.

    For the baseline row itself the plain function runtime is shown (no colour).
    Coloured yellow/red based on configurable thresholds. Values are formatted
    with automatic unit selection (ns/µs/ms/s).
    """
    if is_baseline:
        ft = _func_time_us(value, is_step_avg, step_count)
        return _fmt_us(ft), None

    baseline_ft = _func_time_us(baseline, is_step_avg, step_count)
    value_ft    = _func_time_us(value,    is_step_avg, step_count)
    overhead    = value_ft - baseline_ft

    sign = "+" if overhead >= 0 else ""
    text = sign + _fmt_us(abs(overhead))

    if overhead >= red_threshold_us:
        color = "red!30"
    elif overhead >= yellow_threshold_us:
        color: str | None = "yellow!30"
    else:
        color = None

    return text, color


TRANSFORMATIONS: dict[str, TransformFn] = {
    "runtime":            _transform_runtime,
    "function_runtime":   _transform_function_runtime,
    "function_overhead":  _transform_function_overhead,
}


# ---------------------------------------------------------------------------
# LaTeX escaping
# ---------------------------------------------------------------------------

def _latex_escape(text: str) -> str:
    """Escape LaTeX special characters in plain text (not in pre-built LaTeX)."""
    # Process backslash first to avoid double-escaping
    text = text.replace("\\", "\\textbackslash{}")
    for ch, repl in (
        ("&",  "\\&"),
        ("%",  "\\%"),
        ("$",  "\\$"),
        ("#",  "\\#"),
        ("_",  "\\_"),
        ("{",  "\\{"),
        ("}",  "\\}"),
        ("~",  "\\textasciitilde{}"),
        ("^",  "\\^{}"),
    ):
        text = text.replace(ch, repl)
    return text


def _parse_kind_list(values: Iterable[str] | None) -> set[str]:
    """Parse kind tokens from repeated args and/or comma-separated chunks."""
    if values is None:
        return set()
    parsed: set[str] = set()
    for value in values:
        for token in value.split(","):
            token = token.strip().lower()
            if token:
                parsed.add(token)
    return parsed


STATIC_OPERATION_LABELS: dict[str, str] = {
    "timemonotonicfunction": r"\begin{tabular}[c]{@{}l@{}}Time -- Monotonic\\Function\end{tabular}",
    "timemonotonicfunctioninwholemilliseconds": r"\begin{tabular}[c]{@{}l@{}}Time -- Monotonic\\Function (ms)\end{tabular}",
    "timemonotonicfunctioninwholemicroseconds": r"\begin{tabular}[c]{@{}l@{}}Time -- Monotonic\\Function ($\mu$s)\end{tabular}",
    "timemonotonicfunctioninwholenanoseconds": r"\begin{tabular}[c]{@{}l@{}}Time -- Monotonic\\Function (ns)\end{tabular}",
    "timemonotonicglobal": r"\begin{tabular}[c]{@{}l@{}}Time -- Monotonic\\Global\end{tabular}",
    "timemonotonicglobalinwholemilliseconds": r"\begin{tabular}[c]{@{}l@{}}Time -- Monotonic\\Global (ms)\end{tabular}",
    "timemonotonicglobalinwholemicroseconds": r"\begin{tabular}[c]{@{}l@{}}Time -- Monotonic\\Global ($\mu$s)\end{tabular}",
    "timemonotonicglobalinwholenanoseconds": r"\begin{tabular}[c]{@{}l@{}}Time -- Monotonic\\Global (ns)\end{tabular}",
    "timemonotonicglobalreducedobjects": r"\begin{tabular}[c]{@{}l@{}}Time -- Monotonic\\Global (reduced obj.)\end{tabular}",
    "incrementatomicintcounter": r"\begin{tabular}[c]{@{}l@{}}Increment Atomic\\Int Counter\end{tabular}",
    "appendtostringbuilder": r"\begin{tabular}[c]{@{}l@{}}Append to\\String Builder\end{tabular}",
}


def _operation_label_cell(kind: str) -> str:
    """Return statically configured operation labels with fixed line breaks."""
    kind_norm = kind.lower()
    if kind_norm in STATIC_OPERATION_LABELS:
        return STATIC_OPERATION_LABELS[kind_norm]
    return kind_label(kind)


# ---------------------------------------------------------------------------
# LaTeX table generation
# ---------------------------------------------------------------------------

def generate_latex_table(
    measurements: list[dict],
    entries: list[dict],
    steps_until: int | None,
    steps_skip: int | None,
    transform: TransformFn,
    ignore_kinds: set[str],
    only_kinds: set[str],
) -> str:
    """Return a LaTeX ``tabular`` snippet for the overhead table.

    Rows  = IOA kinds (in canonical order, plain reference first)
    Cols  = for each measurement: JVM, JS, Native

    Requires in the enclosing document::

        \\usepackage{booktabs}
        \\usepackage[table]{xcolor}
    """
    # Build (measurement, kind, target_group) -> entry lookup
    entry_map: dict[tuple[str, str, str], dict] = {
        (e["measurement_id"], e["kind"], e["target_group"]): e for e in entries
    }

    def include_kind(kind: str) -> bool:
        kind_norm = kind.lower()
        if only_kinds:
            return kind_norm in only_kinds
        if ignore_kinds:
            return kind_norm not in ignore_kinds
        return True

    target_groups = ["jvm", "js", "native"]
    target_group_labels = {
        "jvm": "JVM",
        "js": "JS",
        "native": "Native",
    }

    # Ordered kinds (excluding plain — plain gets its own header row)
    all_kinds_set = {
        e["kind"] for e in entries if not e["is_reference"] and include_kind(e["kind"])
    }
    ordered_kinds = [k for k in KIND_ORDER if k in all_kinds_set]
    ordered_kinds += sorted(k for k in all_kinds_set if k not in KIND_ORDER)
    show_plain = include_kind("plain")

    col_spec = "l" + "r" * (len(measurements) * len(target_groups))

    lines: list[str] = []
    lines.append(r"% Requires: \usepackage{booktabs}, \usepackage[table]{xcolor}, \usepackage{array}, \usepackage{multirow}")
    lines.append(r"\begin{tabular}{" + col_spec + r"}")
    lines.append(r"\toprule")

    # Header row 1: OS per measurement (spanning JVM/JS/Native)
    header_os = [r"\multirow{2}{*}{\textbf{\shortstack[l]{Instrumentation\\Operation}}}"] + [
        r"\multicolumn{3}{c}{\textbf{" + _latex_escape(m["os_label"]) + r"}}"
        for m in measurements
    ]
    lines.append(" & ".join(header_os) + r" \\")

    # Header row 2: centered target names per measurement
    header_targets = [""]
    for _ in measurements:
        header_targets.extend(
            r"\multicolumn{1}{c}{\textbf{" + target_group_labels[group] + r"}}"
            for group in target_groups
        )
    lines.append(" & ".join(header_targets) + r" \\")

    for measurement_idx in range(len(measurements)):
        start = 2 + measurement_idx * len(target_groups)
        end = start + len(target_groups) - 1
        lines.append(rf"\cmidrule(lr){{{start}-{end}}}")
    lines.append(r"\midrule")

    # Plain reference row — passed through the transform with is_baseline=True
    if show_plain:
        plain_cells = [r"\textit{Plain (reference)}"]
        for measurement in measurements:
            for group in target_groups:
                plain_entry = entry_map.get((measurement["id"], "plain", group))
                if plain_entry:
                    val, sc, is_step = compute_value(plain_entry, steps_until, steps_skip)
                    text, color = transform(val, val, is_step, sc, True)
                    cell = (r"\cellcolor{" + color + r"}" + text) if color else text
                    plain_cells.append(cell)
                else:
                    plain_cells.append("--")
        lines.append(" & ".join(plain_cells) + r" \\")
        if ordered_kinds:
            lines.append(r"\midrule")

    # One row per kind
    for kind in ordered_kinds:
        row_cells = [_operation_label_cell(kind)]
        for measurement in measurements:
            for group in target_groups:
                kind_entry = entry_map.get((measurement["id"], kind, group))
                plain_entry = entry_map.get((measurement["id"], "plain", group))

                if kind_entry is None:
                    row_cells.append("--")
                    continue

                kind_val, step_count, is_step = compute_value(kind_entry, steps_until, steps_skip)
                baseline_val = kind_val  # fallback: no colouring
                if plain_entry is not None:
                    baseline_val, _, _ = compute_value(plain_entry, steps_until, steps_skip)

                text, color = transform(baseline_val, kind_val, is_step, step_count, False)

                if color:
                    cell = r"\cellcolor{" + color + r"}" + text
                else:
                    cell = text
                row_cells.append(cell)

        lines.append(" & ".join(row_cells) + r" \\")

    lines.append(r"\bottomrule")
    lines.append(r"\end{tabular}")
    return "\n".join(lines)


# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------

def main() -> None:
    parser = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument(
        "folders",
        nargs="+",
        help=(
            "Measurement folders containing per-executable JSON files. "
            "Each folder contributes its own JVM, JS, and Native outputs."
        ),
    )
    parser.add_argument(
        "--transformation",
        choices=list(TRANSFORMATIONS),
        default="runtime",
        help="Cell value transformation (default: runtime)",
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
    kind_filter_group = parser.add_mutually_exclusive_group()
    kind_filter_group.add_argument(
        "--ignore-kinds",
        nargs="+",
        metavar="KIND",
        help=(
            "Kinds to exclude from output rows. "
            "Accepts space-separated kinds and/or comma-separated chunks."
        ),
    )
    kind_filter_group.add_argument(
        "--only-kind",
        nargs="+",
        metavar="KIND",
        help=(
            "Kinds to include in output rows. "
            "Accepts space-separated kinds and/or comma-separated chunks."
        ),
    )
    parser.add_argument(
        "--ignore-for-paper",
        action="store_true",
        help="Apply preset ignore list for paper tables.",
    )
    parser.add_argument(
        "--function-overhead-yellow-threshold-us",
        type=float,
        default=None,
        metavar="N",
        help=(
            "Yellow threshold (in µs) for --transformation function_overhead "
            f"(default: {DEFAULT_FUNCTION_OVERHEAD_YELLOW_THRESHOLD_US})."
        ),
    )
    parser.add_argument(
        "--function-overhead-red-threshold-us",
        type=float,
        default=None,
        metavar="N",
        help=(
            "Red threshold (in µs) for --transformation function_overhead "
            f"(default: {DEFAULT_FUNCTION_OVERHEAD_RED_THRESHOLD_US})."
        ),
    )
    args = parser.parse_args()

    yellow_threshold_us = DEFAULT_FUNCTION_OVERHEAD_YELLOW_THRESHOLD_US
    red_threshold_us = DEFAULT_FUNCTION_OVERHEAD_RED_THRESHOLD_US
    if args.function_overhead_yellow_threshold_us is not None:
        yellow_threshold_us = args.function_overhead_yellow_threshold_us
    if args.function_overhead_red_threshold_us is not None:
        red_threshold_us = args.function_overhead_red_threshold_us

    if yellow_threshold_us < 0 or red_threshold_us < 0:
        print("Error: function_overhead thresholds must be >= 0", file=sys.stderr)
        sys.exit(1)
    if red_threshold_us < yellow_threshold_us:
        print("Error: red threshold must be >= yellow threshold", file=sys.stderr)
        sys.exit(1)

    folders = [Path(folder_arg) for folder_arg in args.folders]
    for folder in folders:
        if not folder.is_dir():
            print(f"Error: {folder} is not a directory", file=sys.stderr)
            sys.exit(1)

    measurements, entries = load_entries_for_measurements(folders)
    if not entries:
        joined = ", ".join(str(folder) for folder in folders)
        print(f"No recognised JSON files found in: {joined}", file=sys.stderr)
        sys.exit(1)

    transform_fn = TRANSFORMATIONS[args.transformation]
    if args.transformation == "function_overhead":
        def transform_fn(
            baseline: float,
            value: float,
            is_step_avg: bool,
            step_count: int,
            is_baseline: bool,
        ) -> tuple[str, str | None]:
            return _transform_function_overhead(
                baseline,
                value,
                is_step_avg,
                step_count,
                is_baseline,
                yellow_threshold_us=yellow_threshold_us,
                red_threshold_us=red_threshold_us,
            )
    ignore_kinds = _parse_kind_list(args.ignore_kinds)
    if args.ignore_for_paper:
        ignore_kinds.update(IGNORE_KINDS_FOR_PAPER)
    only_kinds = _parse_kind_list(args.only_kind)
    latex = generate_latex_table(
        measurements,
        entries,
        args.steps_until,
        args.steps_skip,
        transform_fn,
        ignore_kinds,
        only_kinds,
    )

    steps_suffix = "full"
    if args.steps_until is not None:
        steps_suffix = f"steps"
    elif args.steps_skip is not None:
        steps_suffix = f"steps"

    out_path = folders[0] / f"latex-{args.transformation}-{steps_suffix}.tex"
    out_path.write_text(latex, encoding="utf-8")
    print(f"Saved: {out_path}")


if __name__ == "__main__":
    main()
