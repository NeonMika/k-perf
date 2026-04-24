"""IOA measurements box-plot grid: one modified box plot per kind × target cell showing all run times."""

import argparse
import json
import re
import sys
from pathlib import Path

import matplotlib
matplotlib.rcParams["svg.fonttype"] = "none"  # embed text as real SVG text, not paths
import matplotlib.pyplot as plt
import numpy as np

# ── Well-known targets ────────────────────────────────────────────────────────
TARGET_PRIORITY = ["jar", "node"]

def target_label(target: str) -> str:
    labels = {
        "jar":         "JVM",
        "node":        "JS (Node)",
        "linux-exe":   "Native (Linux)",
        "windows-exe": "Native (Windows)",
        "macos-exe":   "Native (macOS)",
    }
    if target in labels:
        return labels[target]
    if target.endswith("-exe"):
        os_part = target[:-4].replace("-", " ").title()
        return f"Native ({os_part})"
    return target.title()


PLAIN_KIND = "plain (reference)"

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
    return KIND_LABELS.get(raw.lower(), _auto_title(raw))


def _auto_title(s: str) -> str:
    spaced = re.sub(r"([a-z])([A-Z])", r"\1 \2", s)
    return " ".join(w.capitalize() for w in re.split(r"[\s_\-]+", spaced))


def os_to_platform(os_string: str) -> str:
    os_lower = os_string.lower()
    if "windows" in os_lower:
        return "Windows"
    if "mac" in os_lower or "darwin" in os_lower:
        return "macOS"
    return "Linux"


# ── Target extraction ─────────────────────────────────────────────────────────

def _all_known_targets(executables: list[str]) -> list[str]:
    seen: set[str] = set()
    for exe in executables:
        t = _extract_target(exe)
        if t:
            seen.add(t)
    result = [t for t in TARGET_PRIORITY if t in seen]
    exe_targets = sorted(t for t in seen if t not in TARGET_PRIORITY)
    return result + exe_targets


def _extract_target(exe: str) -> str | None:
    for t in ["jar", "node"]:
        if exe.endswith(f"-{t}"):
            return t
    m = re.search(r"-([a-z]+-exe)$", exe)
    if m:
        return m.group(1)
    return None


def parse_executable(exe: str, known_targets: list[str]) -> tuple[str, str] | None:
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


# ── Data loading ──────────────────────────────────────────────────────────────

def load_results_raw(
    folder: Path,
) -> tuple[dict[tuple[str, str], list[float]], dict, list[str]]:
    """Load per-executable JSON files and return raw run times (in ms)."""
    exe_files = sorted(f for f in folder.glob("*.json") if not f.name.startswith("_"))
    if not exe_files:
        sys.exit(f"ERROR: no executable JSON files found in {folder}")

    executables = [f.stem for f in exe_files]
    known_targets = _all_known_targets(executables)

    raw: dict[tuple[str, str], list[float]] = {}
    metadata: dict = {}
    first_done = False

    for f in exe_files:
        with f.open() as fp:
            data = json.load(fp)

        parsed = parse_executable(f.stem, known_targets)
        if parsed is None:
            continue
        raw_kind, target = parsed

        times_us: list[float] = data.get("times", [])
        if not times_us:
            continue

        display_kind = PLAIN_KIND if raw_kind == PLAIN_KIND else pretty_kind(raw_kind)
        raw[(display_kind, target)] = [t / 1000.0 for t in times_us]  # µs → ms

        if not first_done:
            machine = data.get("machineInfo", {})
            params = data.get("parameters", {})
            metadata = {
                "os":   machine.get("OS", ""),
                "reps": data.get("repetitions", params.get("RepetitionCount", "?")),
                "steps": params.get("StepCount", "?"),
            }
            first_done = True

    return raw, metadata, known_targets


# ── Grid construction ─────────────────────────────────────────────────────────

def build_grid(
    raw: dict[tuple[str, str], list[float]],
    known_targets: list[str],
) -> tuple[list[str], list[str]]:
    """Return (ordered_kinds, ordered_targets) for the plot grid."""
    all_kinds = {k for k, _ in raw}
    kinds = [PLAIN_KIND] + sorted(k for k in all_kinds if k != PLAIN_KIND)
    targets = [t for t in known_targets if any((k, t) in raw for k in all_kinds)]
    return kinds, targets


def _group(target: str) -> str:
    if target == "jar":
        return "JVM"
    if target == "node":
        return "JavaScript"
    return "Native"


# ── Plotting ──────────────────────────────────────────────────────────────────

def plot_boxgrid(
    raw: dict[tuple[str, str], list[float]],
    kinds: list[str],
    targets: list[str],
    title: str,
    output_path: Path,
) -> None:
    n_rows = len(kinds)
    n_cols = len(targets)

    cell_w, cell_h = 2.2, 1.6
    fig_w = n_cols * cell_w + 3.0   # extra left margin for row labels
    fig_h = n_rows * cell_h + 1.2   # extra top margin for column headers

    # Global y-axis limits: same range for every cell so boxes are comparable
    all_times = [t for times in raw.values() for t in times]
    y_min = min(all_times)
    y_max = max(all_times)
    y_pad = (y_max - y_min) * 0.05
    y_lo = max(0.0, y_min - y_pad)
    y_hi = y_max + y_pad

    # sharey=True: every subplot shares the same y-axis range
    fig, axes = plt.subplots(
        n_rows, n_cols,
        figsize=(fig_w, fig_h),
        squeeze=False,
        sharey=True,
    )

    for row_idx, kind in enumerate(kinds):
        for col_idx, target in enumerate(targets):
            ax = axes[row_idx, col_idx]
            times = raw.get((kind, target))

            # Hide x-tick on every cell
            ax.set_xticks([])

            if not times:
                ax.text(0.5, 0.5, "–", ha="center", va="center",
                        transform=ax.transAxes, fontsize=10, color="#777777")
                for spine in ax.spines.values():
                    spine.set_linewidth(0.4)
                    spine.set_color("#AAAAAA")
                continue

            # Modified box plot: Tukey 1.5×IQR whiskers, outliers shown individually
            ax.boxplot(
                times,
                vert=True,
                patch_artist=True,
                showfliers=True,
                widths=0.5,
                boxprops=dict(facecolor="white", linewidth=0.8),
                whiskerprops=dict(linewidth=0.8, linestyle="--", color="#555555"),
                capprops=dict(linewidth=0.8, color="#555555"),
                medianprops=dict(color="black", linewidth=1.5),
                flierprops=dict(
                    marker="o",
                    markersize=3,
                    markerfacecolor="#888888",
                    markeredgecolor="#444444",
                    markeredgewidth=0.4,
                    alpha=0.7,
                    linestyle="none",
                ),
            )

            ax.set_ylim(y_lo, y_hi)

            # Y-axis ticks and labels only on the leftmost column
            if col_idx == 0:
                ax.tick_params(axis="y", labelsize=6, pad=2)
                ax.yaxis.set_major_formatter(plt.FuncFormatter(lambda v, _: _fmt_ms(v)))
            else:
                ax.tick_params(axis="y", left=False, labelleft=False)

            for spine in ax.spines.values():
                spine.set_linewidth(0.4)
                spine.set_color("#AAAAAA")

    # ── Row labels (kind names) as y-axis labels on the leftmost column ───────
    for row_idx, kind in enumerate(kinds):
        axes[row_idx, 0].set_ylabel(
            kind,
            fontsize=7,
            rotation=0,
            ha="right",
            va="center",
            labelpad=6,
            fontweight="bold" if kind == PLAIN_KIND else "normal",
        )

    # ── Column target labels ──────────────────────────────────────────────────
    for col_idx, target in enumerate(targets):
        axes[0, col_idx].set_title(target_label(target), fontsize=8, pad=4)

    # ── Tight layout first so that get_position() returns final coordinates ───
    plt.tight_layout(rect=[0, 0, 1, 0.96])   # leave room at top for suptitle

    # ── Column group headers placed AFTER tight_layout ────────────────────────
    groups: list[tuple[str, int, int]] = []
    for col_idx, t in enumerate(targets):
        g = _group(t)
        if groups and groups[-1][0] == g:
            groups[-1] = (g, groups[-1][1], col_idx + 1)
        else:
            groups.append((g, col_idx, col_idx + 1))

    for g_label, g_start, g_end in groups:
        left  = axes[0, g_start].get_position().x0
        right = axes[0, g_end - 1].get_position().x1
        top   = axes[0, 0].get_position().y1
        fig.text(
            (left + right) / 2.0, top + 0.015,
            g_label,
            ha="center", va="bottom",
            fontsize=9, fontweight="bold",
            transform=fig.transFigure,
        )

    # Dashed vertical separators between groups
    for _, g_start, _ in groups:
        if g_start > 0:
            for row_idx in range(n_rows):
                axes[row_idx, g_start].spines["left"].set(
                    linewidth=1.5, linestyle="--", color="black", alpha=0.5
                )

    fig.suptitle(title, fontsize=12, fontweight="bold")
    fig.savefig(output_path, format="svg", bbox_inches="tight")
    print(f"Saved: {output_path}")
    plt.close(fig)


def _fmt_ms(v: float) -> str:
    """Format a millisecond value compactly."""
    if v >= 1000:
        return f"{v / 1000:.2f}s"
    if v >= 10:
        return f"{v:.0f}ms"
    if v >= 1:
        return f"{v:.1f}ms"
    return f"{v * 1000:.0f}µs"


# ── Entry point ───────────────────────────────────────────────────────────────

def main() -> None:
    parser = argparse.ArgumentParser(
        description=(
            "Generate a box-plot grid (SVG) for an IOA benchmark run. "
            "Each cell shows a modified box plot (Tukey 1.5×IQR, outliers visible) "
            "of all repetition times for one IOA kind × execution target combination. "
            "All cells share the same y-axis for direct comparison."
        ),
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument(
        "folder",
        nargs="?",
        default=".",
        help="Path to the measurement folder containing per-executable JSON files",
    )
    parser.add_argument(
        "--output", "-o",
        default=None,
        help="Output SVG path (default: <folder>/ioa_boxplot.svg)",
    )
    args = parser.parse_args()

    folder = Path(args.folder).resolve()
    output = Path(args.output) if args.output else folder / "ioa_boxplot.svg"

    raw, meta, known_targets = load_results_raw(folder)
    kinds, targets = build_grid(raw, known_targets)

    platform = os_to_platform(meta.get("os", ""))
    title = (
        f"IOA Overhead · {meta.get('reps', '?')} Repetitions, "
        f"{meta.get('steps', '?')} Steps · {platform}"
    )

    plot_boxgrid(raw, kinds, targets, title, output)


if __name__ == "__main__":
    main()
