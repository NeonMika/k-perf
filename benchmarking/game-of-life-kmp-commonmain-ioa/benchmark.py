#!/usr/bin/env python3
"""IOA (Instrumentation Overhead Analyzer) benchmark entry point.

Usage (from this directory):
    python benchmark.py [options]

Run python benchmark.py --help for full parameter documentation.

Available IoaKinds are read dynamically from:
    plugins/instrumentation-overhead-analyzer/src/main/kotlin/at/jku/ssw/shared/InstrumentationOverheadAnalyzerKind.kt
"""

from __future__ import annotations

import argparse
import sys
from datetime import datetime
from pathlib import Path

# Allow importing from the parent benchmarking/ directory
_SCRIPT_DIR = Path(__file__).resolve().parent
_BENCHMARKING_DIR = _SCRIPT_DIR.parent
_REPO_ROOT = _BENCHMARKING_DIR.parent
sys.path.insert(0, str(_BENCHMARKING_DIR))

from benchmark_types import IoaConfig
from build import (
    ARTIFACT_VERSION,
    build_game_of_life_commonmain_ioa_variant,
    build_game_of_life_commonmain_reference,
    build_instrumentation_overhead_analyzer_plugin,
    build_kir_helper_kit,
    invoke_get_ioa_executables,
)
from gradle_utils import invoke_gradle_clean
from run import invoke_benchmark_suite
from statistics_utils import get_machine_info, merge_dict


# ---------------------------------------------------------------------------
# Parse available IoaKind values from the Kotlin enum at import time
# ---------------------------------------------------------------------------

_IOA_KIND_KT = (
    _REPO_ROOT
    / "plugins"
    / "instrumentation-overhead-analyzer"
    / "src"
    / "main"
    / "kotlin"
    / "at"
    / "jku"
    / "ssw"
    / "shared"
    / "InstrumentationOverheadAnalyzerKind.kt"
)

try:
    _kt_content = _IOA_KIND_KT.read_text(encoding="utf-8")
    _start_marker = "enum class InstrumentationOverheadAnalyzerKind {"
    _start = _kt_content.find(_start_marker)
    if _start == -1:
        raise ValueError("Could not find 'enum class InstrumentationOverheadAnalyzerKind {' in InstrumentationOverheadAnalyzerKind.kt")
    _start += len(_start_marker)
    _end = _kt_content.find("}", _start)
    if _end == -1:
        raise ValueError("Could not find closing '}' of InstrumentationOverheadAnalyzerKind enum in InstrumentationOverheadAnalyzerKind.kt")
    _enum_body = _kt_content[_start:_end].strip()
    ALL_IOA_KINDS: list[str] = [
        line.strip().rstrip(",")
        for line in _enum_body.splitlines()
        if line.strip() and not line.strip().startswith("//")
    ]
except FileNotFoundError:
    print(
        f"WARNING: InstrumentationOverheadAnalyzerKind.kt not found at {_IOA_KIND_KT}. "
        "IoaKind filtering will be unavailable.",
        file=sys.stderr,
    )
    ALL_IOA_KINDS = []


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------


def main() -> None:
    parser = argparse.ArgumentParser(
        description="IOA benchmark suite for Game of Life KMP.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )

    parser.add_argument(
        "--repetition-count", type=int, default=3, metavar="N",
        help="Number of timed iterations per executable (default: 3).",
    )
    parser.add_argument(
        "--clean-build", type=lambda v: v.lower() in ("true", "1", "yes"),
        default=True, metavar="BOOL",
        help="Rebuild all dependencies before benchmarking (default: true).",
    )
    parser.add_argument(
        "--step-count", type=int, default=20, metavar="N",
        help="Number of Game of Life simulation steps per run (default: 20).",
    )
    parser.add_argument(
        "--reference", type=lambda v: v.lower() in ("true", "1", "yes"),
        default=True, metavar="BOOL",
        help="Include uninstrumented reference executables (default: true).",
    )
    parser.add_argument(
        "--ioa", type=lambda v: v.lower() in ("true", "1", "yes"),
        default=True, metavar="BOOL",
        help="Include IOA-instrumented executables (default: true).",
    )
    parser.add_argument(
        "--jvm", type=lambda v: v.lower() in ("true", "1", "yes"),
        default=True, metavar="BOOL",
        help="Include JVM (JAR) targets (default: true).",
    )
    parser.add_argument(
        "--js", type=lambda v: v.lower() in ("true", "1", "yes"),
        default=True, metavar="BOOL",
        help="Include JS (Node.js) targets (default: true).",
    )
    parser.add_argument(
        "--native", type=lambda v: v.lower() in ("true", "1", "yes"),
        default=True, metavar="BOOL",
        help="Include native executable targets (default: true).",
    )
    parser.add_argument(
        "--ioa-kinds", type=lambda v: [k.strip() for k in v.split(",")],
        default=ALL_IOA_KINDS, metavar="KIND[,KIND...]",
        help=(
            "Comma-separated IoaKind values to benchmark. "
            f"Available: {', '.join(ALL_IOA_KINDS)}. "
            "Defaults to all available kinds."
        ),
    )
    parser.add_argument(
        "--ci-label", type=str, default="local", metavar="LABEL",
        help="Label embedded in results for CI identification (default: local).",
    )

    args = parser.parse_args()

    # Validate
    if not (args.reference or args.ioa):
        print("ERROR: At least one of --reference or --ioa must be true.")
        sys.exit(1)
    if not (args.jvm or args.js or args.native):
        print("ERROR: At least one of --jvm, --js, or --native must be true.")
        sys.exit(1)
    if args.ioa and not args.ioa_kinds:
        print("ERROR: --ioa-kinds must contain at least one value when --ioa is true.")
        sys.exit(1)

    if ALL_IOA_KINDS:
        unknown = [k for k in args.ioa_kinds if k not in ALL_IOA_KINDS]
        if unknown:
            print(f"ERROR: Unknown IoaKind(s): {', '.join(unknown)}")
            print(f"Available: {', '.join(ALL_IOA_KINDS)}")
            sys.exit(1)

    ioa_configs = [IoaConfig(kind=k) for k in args.ioa_kinds]

    # Collect system info
    print("==========================================")
    print("# Collecting System Information...")
    print("==========================================")

    machine_info = get_machine_info(
        _REPO_ROOT / "kmp-examples" / "game-of-life-kmp-commonmain"
    )

    print("System Information collected:")
    for key, val in machine_info.items():
        print(f"  {key} : {val}")
    print()

    print()
    print("==========================================")
    print("# IOA Configurations to Build")
    print("==========================================")
    for cfg in ioa_configs:
        print(f"- {cfg.suffix()}")

    # --- Clean phase ---
    if args.clean_build:
        print()
        print("==========================================")
        print("# Cleaning IOA benchmark dependencies...")
        print("==========================================")
        invoke_gradle_clean(_REPO_ROOT / "KIRHelperKit", "KIRHelperKit")
        invoke_gradle_clean(
            _REPO_ROOT / "plugins" / "instrumentation-overhead-analyzer",
            "instrumentation-overhead-analyzer",
        )
        if args.reference:
            invoke_gradle_clean(
                _REPO_ROOT / "kmp-examples" / "game-of-life-kmp-commonmain",
                "game-of-life-kmp-commonmain",
            )
        if args.ioa:
            invoke_gradle_clean(
                _REPO_ROOT / "kmp-examples" / "game-of-life-kmp-commonmain-ioa",
                "game-of-life-kmp-commonmain-ioa",
            )
        print()
    else:
        print()
        print("==========================================")
        print("# Skipping clean phase (--clean-build false)...")
        print("==========================================")
        print()

    # --- Build phase ---
    build_times: dict[str, float] = {}
    print()
    print("==========================================")
    print("# Building IOA benchmark dependencies...")
    print("==========================================")

    build_times = merge_dict(build_times, build_kir_helper_kit())
    build_times = merge_dict(build_times, build_instrumentation_overhead_analyzer_plugin())

    if args.reference:
        build_times = merge_dict(build_times, build_game_of_life_commonmain_reference())

    if args.ioa:
        for cfg in ioa_configs:
            build_times = merge_dict(
                build_times, build_game_of_life_commonmain_ioa_variant(cfg)
            )

    print()
    print("==========================================")
    print("# Build phase completed successfully!")
    print("==========================================")

    # Collect executables
    executables = invoke_get_ioa_executables(
        ioa_configs=ioa_configs,
        reference=args.reference,
        ioa=args.ioa,
        jvm=args.jvm,
        js=args.js,
        native=args.native,
        artifact_version=ARTIFACT_VERSION,
    )

    if not executables:
        print("ERROR: No executables match the provided parameters.")
        sys.exit(1)

    print()
    print(f"Selected {len(executables)} executables for benchmarking:")
    for exec_ in executables:
        print(f"  - {exec_.name}")
    print()

    # Build measurement directory name
    platform_parts: list[str] = []
    if args.jvm:
        platform_parts.append("jvm")
    if args.js:
        platform_parts.append("js")
    if args.native:
        platform_parts.append("nat")
    platform_label = "-".join(platform_parts)

    measurement_timestamp = datetime.now().strftime("%Y_%m_%d_%H_%M_%S")
    ref_flag = "t" if args.reference else "f"
    ioa_flag = "t" if args.ioa else "f"
    kinds_label = "+".join(args.ioa_kinds)

    measurement_dir_name = (
        f"{measurement_timestamp}_ioa_{args.ci_label}_{args.repetition_count}reps_{args.step_count}steps"
        f"_ref{ref_flag}_ioa{ioa_flag}_{platform_label}"
        f"_kinds-{kinds_label}"
    )
    measurement_dir = _REPO_ROOT / "measurements" / measurement_dir_name

    if measurement_dir.exists():
        print(f"ERROR: Measurement directory already exists: {measurement_dir}")
        print("Please try again in a moment to get a different timestamp.")
        sys.exit(1)

    measurement_dir.mkdir(parents=True, exist_ok=False)

    # Suite-level parameters embedded in every result JSON
    suite_parameters = {
        "CILabel": args.ci_label,
        "RepetitionCount": args.repetition_count,
        "CleanBuild": args.clean_build,
        "StepCount": args.step_count,
        "Reference": args.reference,
        "IOA": args.ioa,
        "JVM": args.jvm,
        "JS": args.js,
        "Native": args.native,
        "IoaKinds": args.ioa_kinds,
    }

    invoke_benchmark_suite(
        executables=executables,
        repetition_count=args.repetition_count,
        step_count=args.step_count,
        measurement_dir=measurement_dir,
        machine_info=machine_info,
        build_times=build_times,
        parameters=suite_parameters,
        clean_build=args.clean_build,
    )

    sys.exit(0)


if __name__ == "__main__":
    main()

