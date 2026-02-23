import argparse
import csv
import itertools
import json
import platform
import shutil
import subprocess
import sys
import time
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path
from pickle import GLOBAL
from typing import Dict, List, Optional, Sequence, Tuple

# Ensure we can import sibling benchmarking utilities
CURRENT_DIR = Path(__file__).resolve().parent
ROOT_DIR = CURRENT_DIR.parents[1]
BENCHMARKING_DIR = ROOT_DIR / "benchmarking"

if str(BENCHMARKING_DIR) not in sys.path:
    sys.path.insert(0, str(BENCHMARKING_DIR))

from machine_info import get_machine_info
from build import (
    KPerfConfig,
    clean_game_of_life_commonmain_kperf_variant,
    clean_game_of_life_commonmain_reference,
    clean_game_of_life_dedicatedmain_kperf_variant,
    clean_game_of_life_dedicatedmain_reference,
    clean_instrumentation_overhead_analyzer_plugin,
    clean_kir_helperkit,
    clean_kperf_plugin,
    build_game_of_life_commonmain_reference,
    build_game_of_life_dedicatedmain_reference,
    build_game_of_life_kperf_variant,
    build_instrumentation_overhead_analyzer_plugin,
    build_kir_helperkit,
    build_kperf_plugin,
)
from utils import get_benchmark_statistics

PROJECT_VERSION = "0.1.0"
TRACE_PREFIX = "trace"
SYMBOL_PREFIX = "symbol"
GRAPH_VISUALIZER = ROOT_DIR / "analyzers" / "call_graph_visualizer" / "graph-visualizer.py"


@dataclass(frozen=True)
class Executable:
    name: str
    path: Path
    kind: str  # jar | node | native
    config: Optional[KPerfConfig]


@dataclass
class Args:
    repetition_count: int
    clean_build: bool
    step_count: int
    reference: bool
    common: bool
    dedicated: bool
    jvm: bool
    js: bool
    native: bool
    flush_early: List[bool]
    instrument_property_accessors: List[bool]
    test_kir: List[bool]


def _parse_args() -> Args:
    parser = argparse.ArgumentParser(description="Python equivalent of run.ps1 for k-perf Game of Life benchmarks")
    parser.add_argument("--repetition-count", type=int, default=50)
    parser.add_argument("--clean-build", action="store_true", default=False)
    parser.add_argument("--step-count", type=int, default=500)
    parser.add_argument("--reference", action="store_true", default=True)
    parser.add_argument("--no-reference", dest="reference", action="store_false")
    parser.add_argument("--common", action="store_true", default=True)
    parser.add_argument("--dedicated", action="store_true", default=False)
    parser.add_argument("--jvm", action="store_true", default=True)
    parser.add_argument("--no-jvm", dest="jvm", action="store_false")
    parser.add_argument("--js", action="store_true", default=True)
    parser.add_argument("--no-js", dest="js", action="store_false")
    parser.add_argument("--native", action="store_true", default=True)
    parser.add_argument("--no-native", dest="native", action="store_false")
    parser.add_argument("--flush-early", nargs="*", type=lambda v: v.lower() == "true", default=[False])
    parser.add_argument("--instrument-property-accessors", nargs="*", type=lambda v: v.lower() == "true", default=[False])
    parser.add_argument("--test-kir", nargs="*", type=lambda v: v.lower() == "true", default=[False])

    ns = parser.parse_args()
    return Args(
        repetition_count=ns.repetition_count,
        clean_build=ns.clean_build,
        step_count=ns.step_count,
        reference=ns.reference,
        common=ns.common,
        dedicated=ns.dedicated,
        jvm=ns.jvm,
        js=ns.js,
        native=ns.native,
        flush_early=ns.flush_early,
        instrument_property_accessors=ns.instrument_property_accessors,
        test_kir=ns.test_kir,
    )


def _native_target_and_ext() -> Tuple[list[str], str]:
    system = platform.system()
    if system == "Windows":
        return ["mingwX64"], ".exe"
    if system == "Darwin":
        return ["macosArm64", "macosX64"], ".kexe"
    return ["linuxX64"], ".kexe"


def _build_kperf_configs(args: Args) -> List[KPerfConfig]:
    combos = list(itertools.product(args.flush_early, args.instrument_property_accessors, args.test_kir))
    return [KPerfConfig(f, p, t) for f, p, t in combos]


def _print_header(title: str):
    print("".ljust(1))
    print("==========================================")
    print(title)
    print("==========================================")


def _clean_if_needed(args: Args):
    if not args.clean_build:
        _print_header("# Skipping clean phase (clean-build = false)...")
        return

    _print_header("# Cleaning k-perf benchmark dependencies...")
    clean_kir_helperkit()
    clean_kperf_plugin()
    clean_instrumentation_overhead_analyzer_plugin()
    clean_game_of_life_commonmain_reference()
    clean_game_of_life_dedicatedmain_reference()
    clean_game_of_life_commonmain_kperf_variant()
    clean_game_of_life_dedicatedmain_kperf_variant()


def _build_all(args: Args, configs: List[KPerfConfig]) -> Dict[str, float]:
    build_times: Dict[str, float] = {}

    _print_header("# Building k-perf benchmark dependencies...")
    build_times.update(build_kir_helperkit())
    build_times.update(build_kperf_plugin())
    build_times.update(build_instrumentation_overhead_analyzer_plugin())
    build_times.update(build_game_of_life_commonmain_reference())
    build_times.update(build_game_of_life_dedicatedmain_reference())

    for cfg in configs:
        build_times.update(build_game_of_life_kperf_variant("common", cfg))
        build_times.update(build_game_of_life_kperf_variant("dedicated", cfg))

    _print_header("# Build phase completed successfully!")
    return build_times


def _project_names(game_type: str) -> Tuple[str, str]:
    if game_type == "common":
        return "game-of-life-kmp-commonmain", "game-of-life-kmp-commonmain-k-perf"
    return "game-of-life-kmp-dedicatedmain", "game-of-life-kmp-dedicatedmain-k-perf"


def _collect_executables_and_verify(
    args: Args,
    configs: List[KPerfConfig],
) -> List[Executable]:
    missing = []
    _print_header("## Collecting and Validating Executables...")

    native_targets, native_ext = _native_target_and_ext()
    executables: List[Executable] = []

    def _verify(exe: Executable, fail_soft: bool = False) -> bool:
        if not exe.path.exists():
            if not fail_soft:
                print(f"ERROR: NOT FOUND {exe.name} at {exe.path}")
                missing.append(exe)
            return False
        else:
            print(f"OK: {exe.name} at {exe.path}")
            executables.append(exe)
            return True

    selected_game_types = []
    if args.common:
        selected_game_types.append("common")
    if args.dedicated:
        selected_game_types.append("dedicated")

    for game_type in selected_game_types:
        plain_name, kperf_name = _project_names(game_type)
        plain_root = ROOT_DIR / "kmp-examples" / plain_name / "build"
        kperf_root = ROOT_DIR / "kmp-examples" / kperf_name / "build"
        tag = "commonmain" if game_type == "common" else "dedicatedmain"

        if args.reference and args.jvm:
            _verify(Executable(
                name=f"{tag}-plain-jar",
                path=plain_root / "lib" / f"{plain_name}-jvm-{PROJECT_VERSION}.jar",
                kind="jar",
                config=None,
            ))

        if args.reference and args.js:
            _verify(Executable(
                name=f"{tag}-plain-node",
                path=plain_root / "js" / "packages" / plain_name / "kotlin" / f"{plain_name}.js",
                kind="node",
                config=None,
            ))

        if args.reference and args.native:
            found = False
            for native_target in native_targets:
                exe = Executable(
                    name=f"{tag}-plain-native",
                    path=plain_root / "bin" / native_target / "releaseExecutable" / f"{plain_name}{native_ext}",
                    kind="native",
                    config=None,
                )

                if _verify(exe, fail_soft=True):
                    found = True
                    break

            if not found:
                print(f"ERROR: NOT FOUND {tag}-plain-native at any of {native_targets}")
                missing.append(exe)

        for cfg in configs:
            suffix = cfg.suffix()
            if args.jvm:
                _verify(Executable(
                    name=f"{tag}-k-perf-{suffix}-jar",
                    path=kperf_root / "lib" / f"{kperf_name}-jvm-{PROJECT_VERSION}-{suffix}.jar",
                    kind="jar",
                    config=cfg,
                ))

            if args.js:
                _verify(
                    Executable(
                        name=f"{tag}-k-perf-{suffix}-node",
                        path=kperf_root / "js" / "packages" / f"{kperf_name}-{suffix}" / "kotlin" / f"{kperf_name}-{suffix}.js",
                        kind="node",
                        config=cfg,
                    )
                )

            if args.native:
                found = False
                for native_target in native_targets:
                    exe = Executable(
                        name=f"{tag}-k-perf-{suffix}-native",
                        path=kperf_root / "bin" / native_target / "releaseExecutable" / f"{kperf_name}-{suffix}{native_ext}",
                        kind="native",
                        config=cfg,
                    )

                    if _verify(exe, fail_soft=True):
                        found = True
                        break

                if not found:
                    print(f"ERROR: NOT FOUND {tag}-k-perf-{suffix}-native at any of {native_targets}")
                    missing.append(exe)

    if missing:
        print(f"ERROR: The following executables were not found:")
        for miss in missing:
            print(f" - {miss.name}")
            print(f"   at {miss.path}")

        raise FileNotFoundError(f"Missing executables.")
    else:
        print()
        print(" All executables found! Proceeding with the benchmark...")
        print("==========================================")

    return executables


def _clean_trace_and_symbol_files() -> List[str]:
    deleted: List[str] = []
    for prefix in (TRACE_PREFIX, SYMBOL_PREFIX):
        for file in CURRENT_DIR.glob(f"{prefix}*.txt"):
            try:
                file.unlink()
                deleted.append(file.name)
            except OSError:
                pass
    return deleted


def _process_traces(executable_name: str, iteration: int, measurement_dir: Path):
    for trace_file in CURRENT_DIR.glob(f"{TRACE_PREFIX}*.txt"):
        result = subprocess.run(
            [sys.executable, str(GRAPH_VISUALIZER), str(trace_file)],
            cwd=CURRENT_DIR,
            capture_output=True,
            text=True,
        )
        if result.returncode == 0:
            png_files = sorted(
                CURRENT_DIR.glob("*.png"), key=lambda p: p.stat().st_mtime, reverse=True
            )
            if png_files:
                png_file = png_files[0]
                new_name = f"{executable_name}_{iteration}.png"
                new_path = CURRENT_DIR / new_name
                png_file.rename(new_path)
                shutil.copy2(new_path, measurement_dir / new_name)
                new_path.unlink(missing_ok=True)
        trace_file.unlink(missing_ok=True)

    for symbol_file in CURRENT_DIR.glob(f"{SYMBOL_PREFIX}*.txt"):
        pass
        symbol_file.unlink(missing_ok=True)


def _flatten_machine_info(info: Dict[str, object]) -> Dict[str, object]:
    flat: Dict[str, object] = {
        "CollectionTimestamp": info.get("timestamp"),
    }

    git = info.get("git", {})
    flat["GitCommitHash"] = git.get("hash")
    flat["GitBranch"] = git.get("branch")

    os_info = info.get("os", {})
    flat["OS"] = os_info.get("name")
    flat["OSArchitecture"] = os_info.get("architecture")
    flat["OSRelease"] = os_info.get("release")

    device = info.get("device", {})
    flat["DeviceManufacturer"] = device.get("manufacturer")
    flat["DeviceModel"] = device.get("model")

    virt = info.get("virtualization", {})
    flat["IsVirtualMachine"] = virt.get("vm")
    flat["HyperVEnabled"] = virt.get("hypervisor")

    cpu = info.get("cpu", {})
    flat["CPU"] = cpu.get("name")
    flat["CPUCores"] = cpu.get("physical_cores")
    flat["CPULogicalProcessors"] = cpu.get("logical_cores")
    flat["CPUMaxClockSpeedMHz"] = cpu.get("max_clock_mhz")

    mem = info.get("memory", {})
    total_mb = mem.get("total")
    free_mb = mem.get("free")
    flat["TotalRAMGB"] = round(total_mb / 1024, 2) if total_mb else None
    flat["AvailableRAMGB"] = round(free_mb / 1024, 2) if free_mb else None

    disk = info.get("disk", {})
    total_mb = disk.get("total")
    free_mb = disk.get("free")
    flat["DiskSizeGB"] = round(total_mb / 1000, 2) if total_mb else None
    flat["SystemDriveFreeSpaceGB"] = round(free_mb / 1000, 2) if free_mb else None

    system = info.get("system", {})
    uptime_seconds = system.get("uptime", {}).get("total_seconds") if system else None
    flat["SystemUptimeHours"] = round(uptime_seconds / 3600, 2) if uptime_seconds else None
    flat["TimeZone"] = system.get("timezone") if system else None
    flat["Username"] = system.get("user") if system else None

    versions = info.get("versions", {})
    java = versions.get("java", {}) if isinstance(versions.get("java"), dict) else {}
    flat["JavaVersion"] = java.get("version") if java else None
    flat["JavaDistribution"] = java.get("distribution") if java else None
    flat["NodeVersion"] = versions.get("node")
    flat["PythonVersion"] = versions.get("python")
    flat["GradleVersion"] = versions.get("gradle")
    flat["KotlinVersion"] = versions.get("kotlin")

    flat["RunningProcessCount"] = info.get("prcesses") or info.get("processes")

    return flat


def _timestamped_measurement_dir() -> Path:
    ts = datetime.now().strftime("%Y_%m_%d_%H_%M_%S")
    name = f"{ts}_game-of-life-kmp-k-perf"
    return ROOT_DIR / "measurements" / name


def _run_executable(exe: Executable, step_count: int) -> Tuple[bool, str]:
    if exe.kind == "jar":
        cmd = ["java", "-jar", str(exe.path), str(step_count)]
    elif exe.kind == "node":
        cmd = ["node", str(exe.path), str(step_count)]
    else:
        cmd = [str(exe.path), str(step_count)]

    result = subprocess.run(cmd, cwd=CURRENT_DIR, capture_output=True, text=True)
    output = "\n".join(filter(None, [result.stdout, result.stderr])).strip()
    return result.returncode == 0, output


def _extract_elapsed(output: str) -> Optional[float]:
    for line in output.splitlines():
        if line.startswith("### Elapsed time:"):
            value = line.split(":", 1)[1].strip()
            try:
                return float(value)
            except ValueError:
                return None
    return None


def _write_results(
    measurement_dir: Path,
    exe: Executable,
    elapsed_times: List[float],
    stats: Dict[str, object],
    build_times: Dict[str, float],
    args: Args,
    machine_info: Dict[str, object],
):
    output_file = measurement_dir / f"{exe.name}.json"
    payload = {
        "parameters": {
            "RepetitionCount": args.repetition_count,
            "CleanBuild": args.clean_build,
            "StepCount": args.step_count,
            "Reference": args.reference,
            "Common": args.common,
            "Dedicated": args.dedicated,
            "JVM": args.jvm,
            "JS": args.js,
            "Native": args.native,
            "FlushEarly": args.flush_early,
            "InstrumentPropertyAccessors": args.instrument_property_accessors,
            "TestKIR": args.test_kir,
        },
        "machineInfo": machine_info,
        "buildTimeMs": build_times.get(exe.name),
        "executable": exe.name,
        "repetitions": args.repetition_count,
        "times": elapsed_times,
        "statistics": stats,
    }
    output_file.write_text(json.dumps(payload, indent=2), encoding="utf-8")

    ci = stats.get("ci95") or {}
    return {
        "mean": stats.get("mean"),
        "median": stats.get("median"),
        "stddev": stats.get("stddev"),
        "min": stats.get("min"),
        "max": stats.get("max"),
        "ci95_lower": ci.get("lower"),
        "ci95_upper": ci.get("upper"),
        "executable": exe.name,
        "buildTimeMs": build_times.get(exe.name),
        "RepetitionCount": args.repetition_count,
        "CleanBuild": args.clean_build,
        "StepCount": args.step_count,
        "Reference": args.reference,
        "Common": args.common,
        "Dedicated": args.dedicated,
        "JVM": args.jvm,
        "JS": args.js,
        "Native": args.native,
        "FlushEarly": args.flush_early,
        "InstrumentPropertyAccessors": args.instrument_property_accessors,
        "TestKIR": args.test_kir,
    }


def _write_summary(measurement_dir: Path, records: List[Dict[str, object]]):
    csv_path = measurement_dir / "_results.csv"
    json_path = measurement_dir / "_results.json"

    fieldnames = list(records[0].keys()) if records else []
    with csv_path.open("w", encoding="utf-8", newline="") as fh:
        writer = csv.DictWriter(fh, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(records)

    json_path.write_text(json.dumps(records, indent=2), encoding="utf-8")


def main():
    args = _parse_args()

    if not (args.common or args.dedicated):
        raise ValueError("At least one of --common or --dedicated must be true")
    if not (args.jvm or args.js or args.native):
        raise ValueError("At least one of --jvm, --js, or --native must be true")
    if not args.flush_early:
        raise ValueError("--flush-early must contain at least one value")
    if not args.instrument_property_accessors:
        raise ValueError("--instrument-property-accessors must contain at least one value")
    if not args.test_kir:
        raise ValueError("--test-kir must contain at least one value")

    _print_header("# Collecting System Information...")
    machine_info = get_machine_info(str(ROOT_DIR / "kmp-examples" / "game-of-life-kmp-commonmain"))
    machine_info_flat = _flatten_machine_info(machine_info)
    print("System Information collected:")
    for k, v in machine_info_flat.items():
        print(f"  {k}: {v}")

    configs = _build_kperf_configs(args)
    _print_header("# K-perf Configurations to Build")
    for cfg in configs:
        print(f"- {cfg.suffix()}")

    _clean_if_needed(args)
    build_times = _build_all(args, configs)

    executables = _collect_executables_and_verify(args, configs)
    if not executables:
        raise RuntimeError("No executables match the provided parameters")

    print("")
    print(f"Selected {len(executables)} executables for benchmarking:")
    for exe in executables:
        print(f"  - {exe.name}")

    measurement_dir = _timestamped_measurement_dir()
    if measurement_dir.exists():
        raise FileExistsError(f"Measurement directory already exists: {measurement_dir}")
    measurement_dir.mkdir(parents=True, exist_ok=False)

    deleted = _clean_trace_and_symbol_files()
    if deleted:
        print(f"Deleted: {', '.join(deleted)}")
    else:
        print("No existing trace/symbol files found.")

    csv_records: List[Dict[str, object]] = []

    for exe in executables:
        print("-" * 56)
        print(f"Starting benchmark for: {exe.name} ({exe.path})")
        print("-" * 56)

        elapsed_times: List[float] = []

        for i in range(1, args.repetition_count + 1):
            print("")
            print(f"Running iteration {i} of {args.repetition_count} for {exe.name}:")
            success, output = _run_executable(exe, args.step_count)
            if not success:
                print(f"ERROR: Execution failed for iteration {i}")
                continue

            elapsed = _extract_elapsed(output)
            if elapsed is not None:
                print(f"- Ran {elapsed / 1000:.3f} ms")
                elapsed_times.append(elapsed)
            else:
                print(f"- Elapsed time not found in iteration {i}")

            _process_traces(exe.name, i, measurement_dir)

        stats = get_benchmark_statistics(elapsed_times)
        record = _write_results(
            measurement_dir,
            exe,
            elapsed_times,
            stats,
            build_times,
            args,
            machine_info,
        )
        csv_records.append({**record, **machine_info_flat})

    _write_summary(measurement_dir, csv_records)
    print(f"Summary results saved to {measurement_dir / '_results.csv'} and {measurement_dir / '_results.json'}")
    print("All benchmarks are complete.")


if __name__ == "__main__":
    main()

