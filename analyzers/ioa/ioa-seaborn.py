"""IOA results loader and plots.

Usage (as a library)::

    import ioa

    df = ioa.load("path/to/measurement_folder")

    ioa.kind_label("filelazyflush")   # -> "File (lazy flush)"
    ioa.target_label("jar")           # -> "JVM"

Usage (as a script)::

    python ioa.py <folder> <graph>

Arguments:
    folder   Path to a measurement folder containing _results.json.
    graph    Name of the graph to plot. Available: overhead_bar

Example::

    python ioa.py ../../measurements/2026_04_09_12_30_55_game-of-life-kmp-commonmain-ioa overhead_bar

Returned DataFrame columns
--------------------------
Parsed from the executable name:
  kind          str   IOA kind name (e.g. "tryfinally", "filelazyflush") or
                      "plain" for the uninstrumented reference.
  target        str   Runtime target: "jar", "node", or "native".
  is_reference  bool  True when this is the plain/uninstrumented baseline.

Measurements (all timing values in microseconds):
  mean_us       float
  median_us     float
  stddev_us     float
  min_us        float
  max_us        float
  ci95_lower_us float
  ci95_upper_us float
  build_time_ms float

Run parameters:
  repetitions   int
  steps         int

Environment (one value per run, repeated for every row):
  os            str
  os_arch       str
  cpu           str
  cpu_cores     int
  git_commit    str
  git_branch    str
  timestamp     str   CollectionTimestamp from the JSON
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path

import pandas as pd
import seaborn as sns


# ---------------------------------------------------------------------------
# Display name helpers
# ---------------------------------------------------------------------------

_KIND_LABELS: dict[str, str] = {
    "plain":                                    "Plain (reference)",
    "ioa":                                      "IOA",
    "none":                                     "None",
    "tryfinally":                               "Try / Finally",
    "timeclock":                                "Time · Clock",
    "timemonotonicfunction":                    "Time · Monotonic Function",
    "timemonotonicfunctioninwholemilliseconds": "Time · Monotonic Function (ms)",
    "timemonotonicfunctioninwholemicroseconds": "Time · Monotonic Function (µs)",
    "timemonotonicfunctioninwholenanoseconds":  "Time · Monotonic Function (ns)",
    "timemonotonicglobal":                      "Time · Monotonic Global",
    "timemonotonicglobalinwholemilliseconds":   "Time · Monotonic Global (ms)",
    "timemonotonicglobalinwholemicroseconds":   "Time · Monotonic Global (µs)",
    "timemonotonicglobalinwholenanoseconds":    "Time · Monotonic Global (ns)",
    "timemonotonicglobalreducedobjects":        "Time · Monotonic Global (reduced objects)",
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
    "macos-exe":   "Native (macOS)",
    "exe":         "Native",
}


def kind_label(kind: str) -> str:
    """Return a human-readable display name for an IOA kind token."""
    label = _KIND_LABELS.get(kind.lower())
    if label:
        return label
    # Fallback: split run-together lowercase/camelCase into Title Case words
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

    Supported patterns (all hyphen-separated):
        commonmain-plain-{target}
        commonmain-ioa-kind-{kind}-{target}

    Targets are: jar, node, or *-exe (e.g. linux-exe, windows-exe).
    Returns None for unrecognised names.
    """
    # Target is always: jar, node, or <os>-exe
    _TARGET_RE = r"(jar|node|[a-z]+-exe)$"

    m = re.fullmatch(r"commonmain-plain-" + _TARGET_RE, name)
    if m:
        return {"kind": "plain", "target": m.group(1), "is_reference": True}

    m = re.fullmatch(r"commonmain-ioa-kind-(.+)-" + _TARGET_RE, name)
    if m:
        return {"kind": m.group(1), "target": m.group(2), "is_reference": False}

    return None

# ---------------------------------------------------------------------------
# Loading
# ---------------------------------------------------------------------------

def load(path: str | Path) -> pd.DataFrame:
    """Load a single measurement run and return a DataFrame.

    *path* may be a folder containing ``_results.json`` or the file itself.
    """
    path = Path(path)
    results_file = path / "_results.json" if path.is_dir() else path
    if not results_file.exists():
        raise FileNotFoundError(f"Results file not found: {results_file}")

    with results_file.open(encoding="utf-8") as f:
        data: list[dict] = json.load(f)

    if not data:
        return pd.DataFrame()

    first = data[0]
    env = {
        "os":         first.get("OS", ""),
        "os_arch":    first.get("OSArchitecture", ""),
        "cpu":        first.get("CPU", ""),
        "cpu_cores":  first.get("CPUCores"),
        "git_commit": first.get("GitCommitHash", ""),
        "git_branch": first.get("GitBranch", ""),
        "timestamp":  first.get("CollectionTimestamp", ""),
    }

    records = []
    for entry in data:
        parsed = _parse_executable(entry.get("executable", ""))
        if parsed is None:
            continue
        records.append({
            **parsed,
            "mean_us":       entry.get("mean"),
            "median_us":     entry.get("median"),
            "stddev_us":     entry.get("stddev"),
            "min_us":        entry.get("min"),
            "max_us":        entry.get("max"),
            "ci95_lower_us": entry.get("ci95_lower"),
            "ci95_upper_us": entry.get("ci95_upper"),
            "build_time_ms": entry.get("buildTimeMs"),
            "repetitions":   entry.get("RepetitionCount"),
            "steps":         entry.get("StepCount"),
            **env,
        })

    return pd.DataFrame(records)


# ---------------------------------------------------------------------------
# Plots
# ---------------------------------------------------------------------------

def plot_overhead_bar(df: pd.DataFrame) -> None:
    """Bar chart: mean runtime (ms) grouped by IOA kind, split by target.

    X axis — target (display label)
    Y axis — mean runtime in ms
    Hue   — IOA kind (display label)
    """
    plot_df = df.copy()
    plot_df["target_label"] = plot_df["target"].map(target_label)
    plot_df["kind_label"]   = plot_df["kind"].map(kind_label)
    plot_df["mean_ms"]      = plot_df["mean_us"] / 1000.0

    ax = sns.barplot(
        data=plot_df,
        x="target_label",
        y="mean_ms",
        hue="kind_label",
        errorbar=None,
    )
    ax.set(
        xlabel="Target",
        ylabel="Mean runtime (ms)",
        title="IOA instrumentation overhead by kind and target",
    )
    sns.move_legend(ax, "upper left", bbox_to_anchor=(1, 1), title="Kind")


def overview_table(df: pd.DataFrame) -> None:
    """Heatmap table: kinds as rows, targets as columns.

    Each cell shows  mean ± stddev  in ms.
    Color encodes overhead ratio vs the plain reference per target
    (green ≤ 1.1× → yellow ≤ 2.0× → red).
    """
    import numpy as np
    import matplotlib.pyplot as plt

    # Canonical kind order matching IoaKind.kt, plain reference first
    KIND_ORDER = [
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

    # Canonical target order: JVM, JS, Native
    TARGET_ORDER = ["jar", "node"]  # exe variants appended after

    plot_df = df.copy()
    plot_df["kind_label"]   = plot_df["kind"].map(kind_label)
    plot_df["target_label"] = plot_df["target"].map(target_label)
    plot_df["mean_ms"]      = plot_df["mean_us"]   / 1000.0
    plot_df["std_ms"]       = plot_df["stddev_us"] / 1000.0

    mean_piv = plot_df.pivot(index="kind_label", columns="target_label", values="mean_ms")
    std_piv  = plot_df.pivot(index="kind_label", columns="target_label", values="std_ms")

    # Sort rows by KIND_ORDER (unknown kinds appended at end)
    present_kinds = set(plot_df["kind"].unique())
    ordered_kinds = [k for k in KIND_ORDER if k in present_kinds]
    ordered_kinds += sorted(k for k in present_kinds if k not in KIND_ORDER)
    row_labels = [kind_label(k) for k in ordered_kinds if kind_label(k) in mean_piv.index]
    mean_piv = mean_piv.loc[row_labels]
    std_piv  = std_piv.loc[row_labels]

    # Sort columns: jar → node → exe variants
    present_targets = set(plot_df["target"].unique())
    ordered_targets = [t for t in TARGET_ORDER if t in present_targets]
    ordered_targets += sorted(t for t in present_targets if t not in TARGET_ORDER)
    col_labels = [target_label(t) for t in ordered_targets if target_label(t) in mean_piv.columns]
    mean_piv = mean_piv[col_labels]
    std_piv  = std_piv[col_labels]

    # Overhead ratio vs the plain reference row (per column)
    plain_label = kind_label("plain")
    if plain_label in mean_piv.index:
        ratio_piv = mean_piv.div(mean_piv.loc[plain_label], axis="columns")
    else:
        ratio_piv = mean_piv / mean_piv.min()

    def _fmt(mean: float, std: float) -> str:
        if mean >= 1000:
            return f"{mean/1000:.2f}s\n±{std/1000:.2f}s"
        return f"{mean:.1f} ms\n±{std:.1f} ms"

    annot = np.empty(mean_piv.shape, dtype=object)
    for i in range(mean_piv.shape[0]):
        for j in range(mean_piv.shape[1]):
            m, s = mean_piv.iloc[i, j], std_piv.iloc[i, j]
            annot[i, j] = _fmt(m, s) if not (np.isnan(m) or np.isnan(s)) else "–"

    # Per-column thresholds from the 33rd / 67th percentile of non-plain ratios.
    # Each cell maps to 0 (green), 1 (yellow) or 2 (red); plain row → NaN (white).
    plain_label = kind_label("plain")
    non_plain = ratio_piv.drop(index=plain_label, errors="ignore")
    t_low  = non_plain.quantile(0.33, axis=0)
    t_high = non_plain.quantile(0.67, axis=0)

    color_piv = ratio_piv.copy().astype(float)
    for col in ratio_piv.columns:
        for row in ratio_piv.index:
            if row == plain_label:
                color_piv.loc[row, col] = np.nan
                continue
            v = ratio_piv.loc[row, col]
            if   v <= t_low[col]:  color_piv.loc[row, col] = 0.0   # green
            elif v <= t_high[col]: color_piv.loc[row, col] = 0.5   # yellow
            else:                  color_piv.loc[row, col] = 1.0   # red

    import matplotlib
    import matplotlib.colors as mcolors
    cmap = mcolors.ListedColormap(["#4CAF50", "#FFC107", "#F44336"])
    cmap.set_bad("white")
    norm = mcolors.BoundaryNorm([-0.1, 0.25, 0.75, 1.1], ncolors=3)

    n_rows, n_cols = mean_piv.shape
    fig, ax = plt.subplots(figsize=(n_cols * 2.5, n_rows * 0.9))

    sns.heatmap(
        color_piv,
        ax=ax,
        annot=annot,
        fmt="",
        cmap=cmap,
        norm=norm,
        linewidths=0.5,
        linecolor="white",
        annot_kws={"size": 8, "va": "center"},
        cbar=False,
    )

    for text in ax.texts:
        text.set_color("black")

    # Move column labels to the top
    ax.xaxis.tick_top()
    ax.xaxis.set_label_position("top")
    ax.set_xlabel("Target", labelpad=8)
    ax.set_ylabel("Instrumentation kind", labelpad=8)
    ax.set_title("IOA instrumentation overhead vs plain reference\n(per-column thresholds: green=lower third · yellow=middle · red=upper third)", pad=16)


# ---------------------------------------------------------------------------
# Graph registry
# ---------------------------------------------------------------------------

GRAPHS: dict[str, callable] = {
    "overhead_bar":   plot_overhead_bar,
    "overview_table": overview_table,
}


# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------

def main() -> None:
    import matplotlib.pyplot as plt

    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("folder", help="Measurement folder containing _results.json")
    parser.add_argument("graph", choices=list(GRAPHS), help="Graph to plot")
    args = parser.parse_args()

    folder = Path(args.folder)
    df = load(folder)
    if df.empty:
        print("No data found in", folder, file=sys.stderr)
        sys.exit(1)

    GRAPHS[args.graph](df)
    plt.tight_layout()

    output = folder / f"{args.graph}.svg"
    plt.savefig(output, format="svg")
    print(f"Saved: {output}")


if __name__ == "__main__":
    main()
