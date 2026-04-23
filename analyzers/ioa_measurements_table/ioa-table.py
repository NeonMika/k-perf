"""IOA measurements table: mean ± stddev per kind × target, color-coded by overhead ratio."""

import argparse
import json
import sys
from pathlib import Path

import matplotlib.colors as mcolors
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns

# ── Thresholds for red/yellow/green ──────────────────────────────────────────
# Ratios are relative to the plain (uninstrumented) reference run per target.
GREEN_THRESHOLD = 1.1   # ≤ 10 % overhead → green
YELLOW_THRESHOLD = 2.0  # ≤ 100 % overhead → yellow; above → red

# ── Targets and their display names ──────────────────────────────────────────
TARGET_ORDER = ["jar", "node", "linux-exe"]
TARGET_LABELS = {"jar": "JVM", "node": "JS (Node)", "linux-exe": "Native (Linux)"}

PLAIN_KIND = "plain (reference)"


def fmt_micros(mean: float, std: float) -> str:
    """Format mean ± stddev in human-readable µs."""
    def _fmt(v: float) -> str:
        if v >= 1_000_000:
            return f"{v / 1_000_000:.2f}M"
        if v >= 1_000:
            return f"{v / 1_000:.1f}k"
        return f"{v:.0f}"
    return f"{_fmt(mean)} µs\n±{_fmt(std)}"


def parse_executable(exe: str) -> tuple[str, str] | None:
    """Return (kind, target) or None if unrecognised."""
    if exe.startswith("commonmain-plain-"):
        suffix = exe[len("commonmain-plain-"):]
        return PLAIN_KIND, suffix
    if exe.startswith("commonmain-ioa-kind-"):
        rest = exe[len("commonmain-ioa-kind-"):]
        for target in ["linux-exe", "node", "jar"]:
            if rest.endswith(f"-{target}"):
                kind = rest[: -(len(target) + 1)]
                return kind, target
    return None


def load_results(folder: Path) -> pd.DataFrame:
    results_file = folder / "_results.json"
    if not results_file.exists():
        sys.exit(f"ERROR: {results_file} not found")
    with results_file.open() as f:
        data = json.load(f)
    records = []
    for entry in data:
        parsed = parse_executable(entry["executable"])
        if parsed is None:
            continue
        kind, target = parsed
        records.append(
            {
                "kind": kind,
                "target": target,
                "mean": entry["mean"],
                "stddev": entry["stddev"],
            }
        )
    return pd.DataFrame(records)


def build_table(df: pd.DataFrame) -> tuple[pd.DataFrame, pd.DataFrame, pd.DataFrame]:
    """Return (mean_pivot, stddev_pivot, ratio_pivot) all indexed kind × target."""
    mean_piv = df.pivot(index="kind", columns="target", values="mean")
    std_piv = df.pivot(index="kind", columns="target", values="stddev")

    plain_ref = mean_piv.loc[PLAIN_KIND] if PLAIN_KIND in mean_piv.index else pd.Series(dtype=float)
    ratio_piv = mean_piv.div(plain_ref, axis="columns")

    # Sort: plain first, then alphabetical
    kinds = [PLAIN_KIND] + sorted(k for k in mean_piv.index if k != PLAIN_KIND)
    targets = [t for t in TARGET_ORDER if t in mean_piv.columns]

    return mean_piv.loc[kinds, targets], std_piv.loc[kinds, targets], ratio_piv.loc[kinds, targets]


def make_colormap() -> tuple[mcolors.ListedColormap, mcolors.BoundaryNorm]:
    colors = ["#4CAF50", "#FFC107", "#F44336"]  # green, amber, red
    boundaries = [0.0, GREEN_THRESHOLD, YELLOW_THRESHOLD, 1e9]
    cmap = mcolors.ListedColormap(colors, name="ryg_overhead")
    norm = mcolors.BoundaryNorm(boundaries, ncolors=cmap.N)
    return cmap, norm


def plot_table(
    mean_piv: pd.DataFrame,
    std_piv: pd.DataFrame,
    ratio_piv: pd.DataFrame,
    title: str,
    output_path: Path,
) -> None:
    n_rows, n_cols = mean_piv.shape
    cell_w, cell_h = 3.2, 0.7
    fig_w = max(10, n_cols * cell_w + 3)
    fig_h = max(6, n_rows * cell_h + 2)

    fig, ax = plt.subplots(figsize=(fig_w, fig_h))

    cmap, norm = make_colormap()

    # Build annotation matrix
    annot = np.empty(mean_piv.shape, dtype=object)
    for i in range(n_rows):
        for j in range(n_cols):
            m = mean_piv.iloc[i, j]
            s = std_piv.iloc[i, j]
            annot[i, j] = fmt_micros(m, s) if not np.isnan(m) else "–"

    sns.heatmap(
        ratio_piv.values.astype(float),
        ax=ax,
        annot=annot,
        fmt="",
        cmap=cmap,
        norm=norm,
        linewidths=0.5,
        linecolor="white",
        xticklabels=[TARGET_LABELS.get(t, t) for t in mean_piv.columns],
        yticklabels=mean_piv.index.tolist(),
        annot_kws={"size": 8, "va": "center"},
        cbar=False,
    )

    # ── Colorbar legend ───────────────────────────────────────────────────────
    cbar_ax = fig.add_axes([0.92, 0.15, 0.02, 0.7])
    sm = plt.cm.ScalarMappable(cmap=cmap, norm=norm)
    sm.set_array([])
    cbar = fig.colorbar(sm, cax=cbar_ax)
    cbar.set_ticks([GREEN_THRESHOLD / 2, (GREEN_THRESHOLD + YELLOW_THRESHOLD) / 2, YELLOW_THRESHOLD + 0.5])
    cbar.set_ticklabels(
        [f"≤{(GREEN_THRESHOLD - 1) * 100:.0f}% overhead", f"≤{(YELLOW_THRESHOLD - 1) * 100:.0f}% overhead", f">{(YELLOW_THRESHOLD - 1) * 100:.0f}% overhead"]
    )
    cbar.ax.tick_params(labelsize=8)

    ax.set_title(title, fontsize=13, fontweight="bold", pad=14)
    ax.set_xlabel("Target", fontsize=10, labelpad=8)
    ax.set_ylabel("IOA Kind", fontsize=10, labelpad=8)
    ax.tick_params(axis="x", labelsize=9, rotation=0)
    ax.tick_params(axis="y", labelsize=8, rotation=0)

    plt.tight_layout(rect=[0, 0, 0.91, 1])
    fig.savefig(output_path, dpi=150, bbox_inches="tight")
    print(f"Saved: {output_path}")
    plt.close(fig)


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Generate a mean±stddev overhead table for an IOA benchmark run."
    )
    parser.add_argument(
        "folder",
        nargs="?",
        default=".",
        help="Path to the measurement folder containing _results.json (default: current dir)",
    )
    parser.add_argument(
        "--output",
        "-o",
        default=None,
        help="Output PNG path (default: <folder>/ioa_table.png)",
    )
    args = parser.parse_args()

    folder = Path(args.folder).resolve()
    output = Path(args.output) if args.output else folder / "ioa_table.png"

    df = load_results(folder)
    mean_piv, std_piv, ratio_piv = build_table(df)

    title = f"IOA Overhead · {folder.name}"
    plot_table(mean_piv, std_piv, ratio_piv, title, output)


if __name__ == "__main__":
    main()
