"""IOA measurements table: mean ± stddev per kind × target, color-coded by total runtime (ms)."""

import argparse
import json
import re
import sys
from pathlib import Path

import matplotlib
matplotlib.rcParams["svg.fonttype"] = "none"  # embed text as real SVG text, not paths
import matplotlib.colors as mcolors
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns

# ── CLI defaults ──────────────────────────────────────────────────────────────
DEFAULT_GREEN_MS = 50.0   # ≤ N ms → green
DEFAULT_RED_FACTOR = 3.0  # > green × factor → red; between → yellow

# ── Well-known targets ────────────────────────────────────────────────────────
# Fixed targets come first in this order; any unknown target is appended after.
TARGET_PRIORITY = ["jar", "node"]

def target_label(target: str) -> str:
    """Human-readable column header for a target string."""
    labels = {
        "jar":         "JVM",
        "node":        "JS (Node)",
        "linux-exe":   "Native (Linux)",
        "windows-exe": "Native (Windows)",
        "macos-exe":   "Native (macOS)",
    }
    if target in labels:
        return labels[target]
    # generic fallback for any exe variant
    if target.endswith("-exe"):
        os_part = target[:-4].replace("-", " ").title()
        return f"Native ({os_part})"
    return target.title()


PLAIN_KIND = "plain (reference)"

# ── Kind name prettification ──────────────────────────────────────────────────
# Maps the lowercased kind token to a human-readable label.
KIND_LABELS: dict[str, str] = {
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


def pretty_kind(raw: str) -> str:
    """Convert a raw kind token to a display name."""
    return KIND_LABELS.get(raw.lower(), _auto_title(raw))


def _auto_title(s: str) -> str:
    """Fallback: split a run-together lowercase string into Title Case words."""
    # Insert a space before each uppercase run boundary via regex on camelCase,
    # then title-case each word.
    spaced = re.sub(r"([a-z])([A-Z])", r"\1 \2", s)
    return " ".join(w.capitalize() for w in re.split(r"[\s_\-]+", spaced))


def os_to_platform(os_string: str) -> str:
    """Derive a short platform label from an OS string."""
    os_lower = os_string.lower()
    if "windows" in os_lower:
        return "Windows"
    if "mac" in os_lower or "darwin" in os_lower:
        return "macOS"
    return "Linux"


# ── Data loading ──────────────────────────────────────────────────────────────

def _all_known_targets(executables: list[str]) -> list[str]:
    """Return all unique target suffixes found in executable names, ordered."""
    seen: set[str] = set()
    for exe in executables:
        t = _extract_target(exe)
        if t:
            seen.add(t)
    # Fixed order: jar, node, then any exe variants alphabetically
    result = [t for t in TARGET_PRIORITY if t in seen]
    exe_targets = sorted(t for t in seen if t not in TARGET_PRIORITY)
    return result + exe_targets


def _extract_target(exe: str) -> str | None:
    """Return the target suffix of an executable name, or None."""
    # Try fixed non-exe targets first
    for t in ["jar", "node"]:
        if exe.endswith(f"-{t}"):
            return t
    # Any *-exe suffix (linux-exe, windows-exe, macos-exe, …)
    m = re.search(r"-([a-z]+-exe)$", exe)
    if m:
        return m.group(1)
    return None


def parse_executable(exe: str, known_targets: list[str]) -> tuple[str, str] | None:
    """Return (raw_kind, target) or None."""
    target = _extract_target(exe)
    if target is None:
        return None

    if exe.startswith("commonmain-plain-"):
        return PLAIN_KIND, target

    if exe.startswith("commonmain-ioa-kind-"):
        rest = exe[len("commonmain-ioa-kind-"):]
        raw_kind = rest[: -(len(target) + 1)]
        return raw_kind, target

    return None


def load_results(folder: Path) -> tuple[pd.DataFrame, dict]:
    results_file = folder / "_results.json"
    if not results_file.exists():
        sys.exit(f"ERROR: {results_file} not found")
    with results_file.open() as f:
        data = json.load(f)

    executables = [e["executable"] for e in data]
    known_targets = _all_known_targets(executables)

    # Extract metadata from first entry
    first = data[0]
    metadata = {
        "os":     first.get("OS", ""),
        "reps":   first.get("RepetitionCount", "?"),
        "steps":  first.get("StepCount", "?"),
    }

    records = []
    for entry in data:
        parsed = parse_executable(entry["executable"], known_targets)
        if parsed is None:
            continue
        raw_kind, target = parsed
        display_kind = PLAIN_KIND if raw_kind == PLAIN_KIND else pretty_kind(raw_kind)
        # Convert µs → ms
        records.append({
            "kind":     display_kind,
            "raw_kind": raw_kind,
            "target":   target,
            "mean_ms":  entry["mean"] / 1000.0,
            "std_ms":   entry["stddev"] / 1000.0,
        })

    return pd.DataFrame(records), metadata, known_targets


# ── Table construction ────────────────────────────────────────────────────────

def build_table(
    df: pd.DataFrame, known_targets: list[str]
) -> tuple[pd.DataFrame, pd.DataFrame]:
    """Return (mean_ms_pivot, std_ms_pivot) indexed by display kind × target."""
    mean_piv = df.pivot(index="kind", columns="target", values="mean_ms")
    std_piv  = df.pivot(index="kind", columns="target", values="std_ms")

    plain_display = PLAIN_KIND
    kinds = [plain_display] + sorted(k for k in mean_piv.index if k != plain_display)
    targets = [t for t in known_targets if t in mean_piv.columns]

    return mean_piv.loc[kinds, targets], std_piv.loc[kinds, targets]


# ── Plotting ──────────────────────────────────────────────────────────────────

def fmt_ms(mean: float, std: float) -> str:
    """Format mean ± stddev in ms."""
    def _f(v: float) -> str:
        if v >= 1000:
            return f"{v / 1000:.2f}s"
        if v >= 10:
            return f"{v:.1f} ms"
        return f"{v:.2f} ms"
    return f"{_f(mean)}\n±{_f(std)}"


def make_colormap(
    green_ms: float, red_factor: float
) -> tuple[mcolors.ListedColormap, mcolors.BoundaryNorm]:
    red_ms = green_ms * red_factor
    colors = ["#4CAF50", "#FFC107", "#F44336"]  # green, amber, red
    boundaries = [0.0, green_ms, red_ms, 1e12]
    cmap = mcolors.ListedColormap(colors, name="ryg_runtime")
    norm = mcolors.BoundaryNorm(boundaries, ncolors=cmap.N)
    return cmap, norm, red_ms


def plot_table(
    mean_piv: pd.DataFrame,
    std_piv: pd.DataFrame,
    known_targets: list[str],
    title: str,
    green_ms: float,
    red_factor: float,
    output_path: Path,
) -> None:
    n_rows, n_cols = mean_piv.shape
    cell_w, cell_h = 3.4, 0.72
    fig_w = max(10, n_cols * cell_w + 3.5)
    fig_h = max(6, n_rows * cell_h + 2.5)

    fig, ax = plt.subplots(figsize=(fig_w, fig_h))
    cmap, norm, red_ms = make_colormap(green_ms, red_factor)

    # Annotation matrix
    annot = np.empty(mean_piv.shape, dtype=object)
    for i in range(n_rows):
        for j in range(n_cols):
            m = mean_piv.iloc[i, j]
            s = std_piv.iloc[i, j]
            annot[i, j] = fmt_ms(m, s) if not np.isnan(m) else "–"

    col_labels = [target_label(t) for t in mean_piv.columns]

    sns.heatmap(
        mean_piv.values.astype(float),
        ax=ax,
        annot=annot,
        fmt="",
        cmap=cmap,
        norm=norm,
        linewidths=0.5,
        linecolor="white",
        xticklabels=col_labels,
        yticklabels=mean_piv.index.tolist(),
        annot_kws={"size": 8, "va": "center"},
        cbar=False,
    )

    # ── Colorbar legend ───────────────────────────────────────────────────────
    cbar_ax = fig.add_axes([0.92, 0.15, 0.018, 0.7])
    sm = plt.cm.ScalarMappable(cmap=cmap, norm=norm)
    sm.set_array([])
    cbar = fig.colorbar(sm, cax=cbar_ax)
    cbar.set_ticks([green_ms / 2, (green_ms + red_ms) / 2, red_ms * 1.2])
    cbar.set_ticklabels([
        f"≤ {green_ms:.0f} ms",
        f"≤ {red_ms:.0f} ms",
        f"> {red_ms:.0f} ms",
    ])
    cbar.ax.tick_params(labelsize=8)

    # ── Column group headers ──────────────────────────────────────────────────
    # Add a secondary x-axis row above the column labels as category headers.
    # Group: "JVM" / "JS" / "Native" derived from the target type.
    def _group(target: str) -> str:
        if target == "jar":
            return "JVM"
        if target == "node":
            return "JavaScript"
        return "Native"

    groups: list[tuple[str, int, int]] = []  # (label, start_col, end_col)
    for col_idx, t in enumerate(mean_piv.columns):
        g = _group(t)
        if groups and groups[-1][0] == g:
            groups[-1] = (g, groups[-1][1], col_idx + 1)
        else:
            groups.append((g, col_idx, col_idx + 1))

    ax2 = ax.twiny()
    ax2.set_xlim(ax.get_xlim())
    ax2.set_xticks([(s + e) / 2 for _, s, e in groups])
    ax2.set_xticklabels([g for g, _, _ in groups], fontsize=10, fontweight="bold")
    ax2.tick_params(length=0)
    ax2.spines["top"].set_visible(False)

    # Draw separator lines between groups on the heatmap
    for _, g_start, g_end in groups:
        if g_start > 0:
            ax.axvline(x=g_start, color="black", linewidth=1.5, linestyle="--", alpha=0.5)

    ax.set_title(title, fontsize=13, fontweight="bold", pad=28)
    ax.set_xlabel("")
    ax.set_ylabel("IOA Kind", fontsize=10, labelpad=8)
    ax.tick_params(axis="x", labelsize=9, rotation=0)
    ax.tick_params(axis="y", labelsize=8, rotation=0)

    plt.tight_layout(rect=[0, 0, 0.91, 1])
    fig.savefig(output_path, format="svg", bbox_inches="tight")
    print(f"Saved: {output_path}")
    plt.close(fig)


# ── Entry point ───────────────────────────────────────────────────────────────

def main() -> None:
    parser = argparse.ArgumentParser(
        description="Generate a mean±stddev runtime table (SVG) for an IOA benchmark run."
    )
    parser.add_argument(
        "folder",
        nargs="?",
        default=".",
        help="Path to the measurement folder containing _results.json (default: current dir)",
    )
    parser.add_argument(
        "--output", "-o",
        default=None,
        help="Output SVG path (default: <folder>/ioa_table.svg)",
    )
    parser.add_argument(
        "--green",
        type=float,
        default=DEFAULT_GREEN_MS,
        metavar="MS",
        help=f"Upper bound in ms for green cells (default: {DEFAULT_GREEN_MS})",
    )
    parser.add_argument(
        "--red-factor",
        type=float,
        default=DEFAULT_RED_FACTOR,
        metavar="FACTOR",
        dest="red_factor",
        help=f"Multiplier applied to --green to get the red threshold (default: {DEFAULT_RED_FACTOR}x → red above green×factor)",
    )
    args = parser.parse_args()

    folder = Path(args.folder).resolve()
    output = Path(args.output) if args.output else folder / "ioa_table.svg"

    df, meta, known_targets = load_results(folder)
    mean_piv, std_piv = build_table(df, known_targets)

    platform = os_to_platform(meta["os"])
    title = f"IOA Overhead · {meta['reps']} Repetitions, {meta['steps']} Steps · {platform}"
    plot_table(mean_piv, std_piv, known_targets, title, args.green, args.red_factor, output)


if __name__ == "__main__":
    main()
