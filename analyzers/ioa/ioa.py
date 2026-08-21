"""IOA results loader and LaTeX table generator.

Usage::

    python ioa.py <folder> [<folder> ...] [--only-kind KIND ...]

Arguments:
    folder   One or more measurement folders containing per-executable JSON files.
             Each folder contributes its own JVM, JS, and Native outputs, grouped
             as one measurement (column group) in the output table.
    --only-kind KIND ...   Include only listed kinds in output rows. Accepts
                            space-separated kinds and/or comma-separated chunks.

Output::

    {first_folder}/latex-overhead.tex

Example::

    python ioa.py ../../measurements/2026_04_23_13_54_53_...
    python ioa.py ../../measurements/2026_04_23_13_54_53_... --only-kind addtolist,timeclock

Per-step JSON files loaded (inside the folder):
    commonmain-ioa-kind-{kind}-{target}.json
    commonmain-plain-{target}.json

Each such JSON has a ``stepTimes`` array: one entry per run, each entry an
array of per-step times (in microseconds).

Statistics (fixed, not configurable):
    The first WARMUP_STEPS steps of every run are dropped as warm-up. Let R be
    the number of (post-warm-up) runs, S the number of post-warm-up steps per
    run, and F = FUNCTION_CALLS_PER_STEP the fixed number of traced function
    calls per step. For an operation X:

        For each run r:  X_r = (1 / (S*F)) * sum_{s=1}^{S} T_r,s^(X)
        mu_X    = (1/R) * sum_r X_r
        sigma_X = sqrt( (1/(R-1)) * sum_r (X_r - mu_X)^2 )

    The plain/reference row reports (mu_B, sigma_B) directly. Each operation's
    overhead row reports:

        mu_O    = mu_I - mu_B
        sigma_O = sqrt(sigma_I^2 + sigma_B^2)
"""

from __future__ import annotations

import argparse
import json
import math
import re
import statistics
import sys
from pathlib import Path
from typing import Iterable


# ---------------------------------------------------------------------------
# Fixed statistics parameters
# ---------------------------------------------------------------------------

WARMUP_STEPS = 150
FUNCTION_CALLS_PER_STEP = 4001

YELLOW_THRESHOLD_US = 0.0075
RED_THRESHOLD_US = 0.03


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


def kind_label(kind: str) -> str:
    """Return a human-readable display name for an IOA kind token."""
    label = _KIND_LABELS.get(kind.lower())
    if label:
        return label
    spaced = re.sub(r"([a-z])([A-Z])", r"\1 \2", kind)
    return " ".join(w.capitalize() for w in re.split(r"[\s_\-]+", spaced))


def _operation_label_cell(kind: str) -> str:
    """Return statically configured operation labels with fixed line breaks."""
    kind_norm = kind.lower()
    if kind_norm in STATIC_OPERATION_LABELS:
        return STATIC_OPERATION_LABELS[kind_norm]
    return kind_label(kind)


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

    Each returned entry dict contains kind, target, is_reference (from the
    filename) and step_times_all_runs — list[list[float]], µs per step per run.
    """
    entries: list[dict] = []
    for json_file in sorted(folder.glob("commonmain-*.json")):
        parsed = _parse_executable(json_file.stem)
        if parsed is None:
            continue
        with json_file.open(encoding="utf-8") as f:
            data = json.load(f)
        entries.append({**parsed, "step_times_all_runs": data.get("stepTimes", [])})

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


# ---------------------------------------------------------------------------
# Statistics
# ---------------------------------------------------------------------------

def per_run_function_time_us(entry: dict) -> list[float]:
    """Per-run mean per-function-call time (µs), after dropping WARMUP_STEPS.

    One sample per run: X_r = (1 / (S*F)) * sum_{s=1}^{S} T_r,s, where S is
    that run's post-warm-up step count and F = FUNCTION_CALLS_PER_STEP.
    """
    return [
        statistics.mean(run[WARMUP_STEPS:]) / FUNCTION_CALLS_PER_STEP
        for run in entry["step_times_all_runs"]
        if len(run) > WARMUP_STEPS
    ]


def mean_stddev(values: list[float]) -> tuple[float, float]:
    """(mu, sigma): sample mean and sample stddev (ddof=1); sigma=0 for <2 values."""
    if not values:
        return 0.0, 0.0
    mean = statistics.mean(values)
    stddev = statistics.stdev(values) if len(values) > 1 else 0.0
    return mean, stddev


def overhead_mean_stddev(kind_entry: dict, baseline_entry: dict) -> tuple[float, float]:
    """(mu_O, sigma_O) per-function-call overhead of kind_entry vs baseline_entry.

    mu_O = mu_I - mu_B; sigma_O = sqrt(sigma_I^2 + sigma_B^2), combining each
    side's own run-to-run variance under independence.
    """
    mu_i, sigma_i = mean_stddev(per_run_function_time_us(kind_entry))
    mu_b, sigma_b = mean_stddev(per_run_function_time_us(baseline_entry))
    return mu_i - mu_b, math.sqrt(sigma_i**2 + sigma_b**2)


# ---------------------------------------------------------------------------
# Formatting
# ---------------------------------------------------------------------------

def _decimals_for_magnitude(magnitude: float) -> int:
    """Decimal places for a non-negative ns value, by its integer-digit count.

    3+ integer digits -> 0 decimals, 2 -> 1 decimal, else (0 or 1) -> 2 decimals.
    """
    digits_before_decimal = 1 if magnitude < 1 else math.floor(math.log10(magnitude)) + 1
    if digits_before_decimal >= 3:
        return 0
    if digits_before_decimal == 2:
        return 1
    return 2


def _fmt_ns(value_ns: float) -> str:
    """Format a ns value (no unit suffix — every value is ns): '-' prefix for
    negatives only (never '+' for positives), with decimal places fixed by
    _decimals_for_magnitude.

    E.g. 123.4 -> "123", 12.34 -> "12.3", 1.234 -> "1.23", -1.234 -> "-1.23".
    """
    sign = "-" if value_ns < 0 else ""
    magnitude = abs(value_ns)

    decimals = _decimals_for_magnitude(magnitude)
    rounded = round(magnitude, decimals)

    # Rounding can carry into a new digit-count bracket (e.g. 9.996 -> 10.00),
    # which would leave the decimal count for the bracket it just left.
    new_decimals = _decimals_for_magnitude(rounded)
    if new_decimals != decimals:
        rounded = round(rounded, new_decimals)
        decimals = new_decimals

    if rounded == 0:
        sign = ""  # avoid a misleading "-0.00" for negatives that round to zero

    return f"{sign}{rounded:.{decimals}f}"


def _overhead_color(overhead_us: float) -> str | None:
    """Yellow/red cell colour for a function-overhead value, based on fixed thresholds."""
    if overhead_us >= RED_THRESHOLD_US:
        return "red!30"
    if overhead_us >= YELLOW_THRESHOLD_US:
        return "yellow!30"
    return None


def _baseline_cell(mu_b_us: float, sigma_b_us: float) -> str:
    """'123 $\\pm$ 4.50' (ns, unitless) cell text for the plain/reference row (no colour)."""
    return f"{_fmt_ns(mu_b_us * 1000)} $\\pm$ {_fmt_ns(sigma_b_us * 1000)}"


def _overhead_cell(mu_o_us: float, sigma_o_us: float) -> tuple[str, str | None]:
    """'123 $\\pm$ 4.50' (ns, unitless) cell text and its yellow/red threshold colour."""
    text = f"{_fmt_ns(mu_o_us * 1000)} $\\pm$ {_fmt_ns(sigma_o_us * 1000)}"
    return text, _overhead_color(mu_o_us)


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


# ---------------------------------------------------------------------------
# LaTeX table generation
# ---------------------------------------------------------------------------

def generate_latex_table(
    measurements: list[dict],
    entries: list[dict],
    only_kinds: set[str],
) -> str:
    """Return a LaTeX ``tabular`` snippet for the overhead table.

    Rows  = IOA kinds (in canonical order, plain reference first)
    Cols  = for each measurement: JVM, JS, Native

    Requires in the enclosing document::

        \\usepackage{booktabs}
        \\usepackage[table]{xcolor}
    """
    entry_map: dict[tuple[str, str, str], dict] = {
        (e["measurement_id"], e["kind"], e["target_group"]): e for e in entries
    }

    def include_kind(kind: str) -> bool:
        return not only_kinds or kind.lower() in only_kinds

    target_groups = ["jvm", "js", "native"]
    target_group_labels = {"jvm": "JVM", "js": "JS", "native": "Native"}

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

    # Plain reference row — mu_B ± sigma_B, no colouring
    if show_plain:
        plain_cells = [r"\textit{Plain (reference)}"]
        for measurement in measurements:
            for group in target_groups:
                plain_entry = entry_map.get((measurement["id"], "plain", group))
                if plain_entry is None:
                    plain_cells.append("--")
                    continue
                mu_b, sigma_b = mean_stddev(per_run_function_time_us(plain_entry))
                plain_cells.append(_baseline_cell(mu_b, sigma_b))
        lines.append(" & ".join(plain_cells) + r" \\")
        if ordered_kinds:
            lines.append(r"\midrule")

    # One row per kind — mu_O ± sigma_O vs the plain baseline, threshold-coloured
    for kind in ordered_kinds:
        row_cells = [_operation_label_cell(kind)]
        for measurement in measurements:
            for group in target_groups:
                kind_entry = entry_map.get((measurement["id"], kind, group))
                plain_entry = entry_map.get((measurement["id"], "plain", group))

                if kind_entry is None or plain_entry is None:
                    row_cells.append("--")
                    continue

                mu_o, sigma_o = overhead_mean_stddev(kind_entry, plain_entry)
                text, color = _overhead_cell(mu_o, sigma_o)
                row_cells.append(r"\cellcolor{" + color + r"}" + text if color else text)

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
        "--only-kind",
        nargs="+",
        metavar="KIND",
        help=(
            "Kinds to include in output rows. "
            "Accepts space-separated kinds and/or comma-separated chunks."
        ),
    )
    args = parser.parse_args()

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

    only_kinds = _parse_kind_list(args.only_kind)
    latex = generate_latex_table(measurements, entries, only_kinds)

    out_path = folders[0] / "latex-overhead.tex"
    out_path.write_text(latex, encoding="utf-8")
    print(f"Saved: {out_path}")


if __name__ == "__main__":
    main()
