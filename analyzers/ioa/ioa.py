"""IOA results loader and LaTeX table generator.

Usage::

    python ioa.py <folder> [options]

Arguments:
    folder   Path to a measurement folder containing per-executable JSON files.

Options:
    --transformation NAME   Transformation to apply to cell values (default: runtime).
                            Available: runtime, function_runtime, function_overhead
    --steps-until N         Only consider the first N steps of each run (mutually
                            exclusive with --steps-skip).
    --steps-skip N          Skip the first N steps of each run (mutually exclusive
                            with --steps-until).

Output::

    {folder}/latex-{transformation}-{steps|full}.tex

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
from typing import Callable


# ---------------------------------------------------------------------------
# Display name helpers
# ---------------------------------------------------------------------------

_KIND_LABELS: dict[str, str] = {
    "plain":                                    "Plain (reference)",
    "ioa":                                      "IOA",
    "none":                                     "None",
    "tryfinally":                               "Try / Finally",
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
    "appendtostringbuilder":                    "Append To StringBuilder",
    "fileeagerflush":                           "File (eager flush)",
    "filelazyflush":                            "File (lazy flush)",
    "addtolist":                                "Add To List",
    "addduplicatestoset":                       "Add Duplicates To Set",
    "adduniquetoset":                           "Add Unique To Set",
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

# Canonical target order: JVM → JS → Native variants
TARGET_ORDER: list[str] = ["jar", "node", "linux-exe", "win-exe", "windows-exe", "mac-exe", "macos-exe", "exe", "native"]


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

    Targets: jar, node, or <os>-exe (e.g. linux-exe, win-exe, mac-exe).
    Returns None for unrecognised names.
    """
    _TARGET_RE = r"(jar|node|[a-z]+-exe)$"

    m = re.fullmatch(r"commonmain-plain-" + _TARGET_RE, name)
    if m:
        return {"kind": "plain", "target": m.group(1), "is_reference": True}

    m = re.fullmatch(r"commonmain-ioa-kind-(.+)-" + _TARGET_RE, name)
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


def _fmt_us(us: float, target: str | None = None) -> str:
    """Format a µs value as a compact string suitable for LaTeX cells."""
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

    Per step: 4001 calls (one step of the game-of-life trace).
    Overall:  4001 * step_count + 14 extra top-level calls.
    """
    calls = 4001 * step_count
    if not is_step_avg:
        calls += 14

    return calls


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
    return _fmt_us(ft, "ns"), None


def _transform_function_overhead(
    baseline: float, value: float, is_step_avg: bool, step_count: int, is_baseline: bool
) -> tuple[str, str | None]:
    """Show per-function overhead vs plain baseline.

    For the baseline row itself the plain function runtime is shown (no colour).
    Coloured yellow if overhead >= 2 µs, red if >= 10 µs.
    """
    if is_baseline:
        ft = _func_time_us(value, is_step_avg, step_count)
        return _fmt_us(ft, "ns"), None

    baseline_ft = _func_time_us(baseline, is_step_avg, step_count)
    value_ft    = _func_time_us(value,    is_step_avg, step_count)
    overhead    = value_ft - baseline_ft

    sign = "+" if overhead >= 0 else ""
    text = sign + _fmt_us(overhead, "ns")

    if overhead >= 0.01:
        color = "red!30"
    elif overhead >= 0.002:
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


# ---------------------------------------------------------------------------
# LaTeX table generation
# ---------------------------------------------------------------------------

def _target_sort_key(target: str) -> int:
    try:
        return TARGET_ORDER.index(target)
    except ValueError:
        return len(TARGET_ORDER)


def generate_latex_table(
    entries: list[dict],
    steps_until: int | None,
    steps_skip: int | None,
    transform: TransformFn,
) -> str:
    """Return a LaTeX ``tabular`` snippet for the overhead table.

    Rows  = IOA kinds (in canonical order, plain reference first)
    Cols  = targets (JVM → JS → Native variants)

    Requires in the enclosing document::

        \\usepackage{booktabs}
        \\usepackage[table]{xcolor}
    """
    # Build (kind, target) -> entry lookup
    entry_map: dict[tuple[str, str], dict] = {
        (e["kind"], e["target"]): e for e in entries
    }

    # Ordered targets
    all_targets = sorted({e["target"] for e in entries}, key=_target_sort_key)

    # Ordered kinds (excluding plain — plain gets its own header row)
    all_kinds_set = {e["kind"] for e in entries if not e["is_reference"]}
    ordered_kinds = [k for k in KIND_ORDER if k in all_kinds_set]
    ordered_kinds += sorted(k for k in all_kinds_set if k not in KIND_ORDER)

    # Fixed-width centered columns for target data; all equal width so headers align.
    col_spec = "l" + "r" * len(all_targets)

    lines: list[str] = []
    lines.append(r"% Requires: \usepackage{booktabs}, \usepackage[table]{xcolor}, \usepackage{array}")
    lines.append(r"\begin{tabular}{" + col_spec + r"}")
    lines.append(r"\toprule")

    # Column header — target labels are centered by the column type
    header_parts = ["\\textbf{Kind}"] + [
        "\\textbf{" + _latex_escape(target_label(t)) + "}" for t in all_targets
    ]
    lines.append(" & ".join(header_parts) + r" \\")
    lines.append(r"\midrule")

    # Plain reference row — passed through the transform with is_baseline=True
    plain_cells = [r"\textit{Plain (reference)}"]
    for t in all_targets:
        plain_entry = entry_map.get(("plain", t))
        if plain_entry:
            val, sc, is_step = compute_value(plain_entry, steps_until, steps_skip)
            text, color = transform(val, val, is_step, sc, True)
            cell = (r"\cellcolor{" + color + r"}" + text) if color else text
            plain_cells.append(cell)
        else:
            plain_cells.append("--")
    lines.append(" & ".join(plain_cells) + r" \\")
    lines.append(r"\midrule")

    # One row per kind
    for kind in ordered_kinds:
        # kind_label() returns strings from a controlled dict that already
        # contain valid LaTeX markup (e.g. $\mu$), so no escaping here.
        row_cells = [kind_label(kind)]
        for t in all_targets:
            kind_entry  = entry_map.get((kind, t))
            plain_entry = entry_map.get(("plain", t))

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
    parser.add_argument("folder", help="Measurement folder containing per-executable JSON files")
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

    args = parser.parse_args()

    folder = Path(args.folder)
    if not folder.is_dir():
        print(f"Error: {folder} is not a directory", file=sys.stderr)
        sys.exit(1)

    entries = load_entries(folder)
    if not entries:
        print(f"No recognised JSON files found in {folder}", file=sys.stderr)
        sys.exit(1)

    transform_fn = TRANSFORMATIONS[args.transformation]
    latex = generate_latex_table(entries, args.steps_until, args.steps_skip, transform_fn)

    steps_suffix = "full"
    if args.steps_until is not None:
        steps_suffix = f"steps"
    elif args.steps_skip is not None:
        steps_suffix = f"steps"

    out_path = folder / f"latex-{args.transformation}-{steps_suffix}.tex"
    out_path.write_text(latex, encoding="utf-8")
    print(f"Saved: {out_path}")


if __name__ == "__main__":
    main()
