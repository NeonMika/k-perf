#!/usr/bin/env python3
"""Builds all supported KMP targets for the project in the current working directory.

Called per-project by build-all-on-*-separated.yml for discrete CI step visibility.
Task discovery logic mirrors buildAll.py — keep both in sync when changing target lists.

This script replaces both Build-KmpExample.ps1 (Windows) and build-kmp-example.sh (Ubuntu).
"""

from __future__ import annotations

import sys
from pathlib import Path

_SCRIPTS_DIR = Path(__file__).resolve().parent
_REPO_ROOT = _SCRIPTS_DIR.parent.parent
sys.path.insert(0, str(_REPO_ROOT / "benchmarking"))

from gradle_utils import invoke_kmp_build  # noqa: E402

if __name__ == "__main__":
    invoke_kmp_build(
        f"Building {Path.cwd().name}",
        Path.cwd(),
        clean_build=True,
    )
