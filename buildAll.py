#!/usr/bin/env python3
"""Full build script for the k-perf repository.

Replaces buildAll.sh (Linux/macOS) and buildAll.ps1 (Windows).
Build order: KIRHelperKit → plugins → KMP examples (each layer publishes to mavenLocal).

Usage:
    python buildAll.py [--no-clean]

⚠️ Duplication notice: The build order, project list, and KMP task priority lists here
are mirrored in several places that must be kept in sync:
  - .github/workflows/build-all-on-*-separated.yml    (per-project CI steps)
  - .github/workflows/build-all-on-*-highly-separated.yml  (per-target CI steps)
  - .github/scripts/build-kmp-example.py  (KMP target-discovery helper for separated workflows)
Keep all four in sync when adding/removing projects, changing the build order, or updating
the JS/JVM/native task priority lists.
"""

from __future__ import annotations

import argparse
import subprocess
import sys
from pathlib import Path

_REPO_ROOT = Path(__file__).resolve().parent
sys.path.insert(0, str(_REPO_ROOT / "benchmarking"))

from gradle_utils import GRADLEW_CMD, invoke_kmp_build  # noqa: E402


def _run_gradle(title: str, path: Path, tasks: list[str], clean_build: bool) -> None:
    print()
    print("==========================================")
    print(title)
    print(f"Path: {path}")
    print(f"Tasks: {' '.join(tasks)}")
    print("==========================================")

    cmd = ([GRADLEW_CMD, "clean"] if clean_build else [GRADLEW_CMD]) + tasks
    result = subprocess.run(cmd, cwd=path)
    if result.returncode != 0:
        print(f"ERROR: {title} failed with exit code {result.returncode}")
        sys.exit(result.returncode)

    print(f"{title} completed successfully.")


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Full build for the k-perf repository.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument(
        "--no-clean",
        action="store_true",
        help="Skip the clean phase to reuse existing build outputs.",
    )
    args = parser.parse_args()
    clean_build = not args.no_clean

    print("==========================================")
    print(f"Starting full build (CleanBuild = {clean_build})")
    print("==========================================")

    # KIRHelperKit — publish to mavenLocal so plugins can consume it
    _run_gradle(
        "Building KIRHelperKit",
        _REPO_ROOT / "KIRHelperKit",
        ["build", "publishToMavenLocal"],
        clean_build,
    )

    # Plugins — publish to mavenLocal so KMP examples can consume them
    _run_gradle(
        "Building instrumentation-overhead-analyzer plugin",
        _REPO_ROOT / "plugins" / "instrumentation-overhead-analyzer",
        ["build", "publishToMavenLocal"],
        clean_build,
    )
    _run_gradle(
        "Building k-perf plugin",
        _REPO_ROOT / "plugins" / "k-perf",
        ["build", "publishToMavenLocal"],
        clean_build,
    )

    # KMP examples
    kmp_projects = [
        "game-of-life-kmp-commonmain",
        "game-of-life-kmp-commonmain-ioa",
        "game-of-life-kmp-commonmain-k-perf",
        "game-of-life-kmp-dedicatedmain",
        "game-of-life-kmp-dedicatedmain-k-perf",
    ]
    for project in kmp_projects:
        invoke_kmp_build(
            f"Building {project} example",
            _REPO_ROOT / "kmp-examples" / project,
            clean_build=clean_build,
        )

    print()
    print("==========================================")
    print("All builds completed successfully.")
    print("==========================================")


if __name__ == "__main__":
    main()
