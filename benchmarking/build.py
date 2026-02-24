import argparse
import platform
import subprocess
import time
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, List, Optional

from utils import find_first_gradle_task

ROOT_DIR = Path(__file__).resolve().parent.parent


@dataclass
class KPerfConfig:
    flush_early: bool
    instrument_property_accessors: bool
    test_kir: bool

    def suffix(self) -> str:
        return (
            f"flushEarly-{'true' if self.flush_early else 'false'}-"
            f"propAccessors-{'true' if self.instrument_property_accessors else 'false'}-"
            f"testKIR-{'true' if self.test_kir else 'false'}"
        )


@dataclass
class IoaConfig:
    kind: str

    def suffix(self) -> str:
        return f"kind-{self.kind.lower()}"


def _gradle_wrapper(cwd: Path) -> str:
    if platform.system() == "Windows":
        candidate = cwd / "gradlew.bat"
        return str(candidate if candidate.exists() else "gradlew.bat")
    candidate = cwd / "gradlew"
    return str(candidate if candidate.exists() else "./gradlew")


def _run(cmd: List[str], cwd: Path) -> tuple[int, str, str]:
    try:
        completed = subprocess.run(cmd, cwd=cwd, capture_output=True, text=True, encoding="utf-8")
        return completed.returncode, completed.stdout, completed.stderr
    except FileNotFoundError:
        return 127, "", ""
    except Exception as exc:  # pragma: no cover - defensive
        return 1, "", str(exc)


def _print_banner(title: str, task: Optional[str] = None):
    print("")
    print("==========================================")
    print(title)
    if task:
        print(f"Task: {task}")
    print("==========================================")


def _invoke_gradle_task_if_present_timed(
    task: Optional[str],
    title: str,
    cwd: Path,
    gradle_args: Optional[List[str]] = None,
) -> Optional[float]:
    if not task:
        print(f"Skipping {title} (task not found)")
        return None

    gradle_args = gradle_args or []
    _print_banner(title, task)

    start = time.perf_counter()
    rc, out, err = _run([_gradle_wrapper(cwd), *gradle_args, task], cwd=cwd)
    duration_ms = (time.perf_counter() - start) * 1000

    if out:
        print(out.strip())
    if err:
        print(err.strip())
    if rc != 0:
        raise RuntimeError(f"{title} failed with exit code {rc}")

    print(f"{title} completed successfully in {duration_ms:.2f} ms.")
    return duration_ms


def _invoke_kmp_build_with_timings(
    title: str,
    path: Path,
    gradle_args: Optional[List[str]] = None,
) -> Dict[str, float]:
    gradle_args = gradle_args or []
    _print_banner(f"{title} (Kotlin Multiplatform)", str(path))

    rc, task_out, task_err = _run([_gradle_wrapper(path), "-q", "tasks", "--all"], cwd=path)
    if task_out:
        # help diagnose missing tasks without overwhelming output
        print(task_out.strip())
    if task_err:
        print(task_err.strip())
    if rc != 0:
        raise RuntimeError(f"{title} task discovery failed with exit code {rc}")

    jvm_task = find_first_gradle_task(task_out, ["jvmJar", "compileKotlinJvm"])
    js_task = find_first_gradle_task(
        task_out,
        [
            "jsProductionExecutableCompileSync",
            "jsProductionExecutableCompile",
            "jsNodeProductionExecutableCompileSync",
            "jsNodeProductionExecutableCompile",
            "jsBrowserProductionWebpack",
            "compileKotlinJs",
        ],
    )
    windows_task = find_first_gradle_task(task_out, ["linkReleaseExecutableMingwX64", "linkDebugExecutableMingwX64"])
    linux_task = find_first_gradle_task(task_out, ["linkReleaseExecutableLinuxX64", "linkDebugExecutableLinuxX64"])
    mac_task = find_first_gradle_task(
        task_out,
        [
            "linkReleaseExecutableMacosArm64",
            "linkDebugExecutableMacosArm64",
            "linkReleaseExecutableMacosX64",
            "linkDebugExecutableMacosX64",
        ],
    )

    timings: Dict[str, float] = {}
    jvm_duration = _invoke_gradle_task_if_present_timed(jvm_task, f"{title} - JVM build", path, gradle_args)
    if jvm_duration is not None:
        timings["jvm"] = jvm_duration

    js_duration = _invoke_gradle_task_if_present_timed(js_task, f"{title} - JS build", path, gradle_args)
    if js_duration is not None:
        timings["js"] = js_duration

    windows_duration = _invoke_gradle_task_if_present_timed(
        windows_task, f"{title} - Windows build", path, gradle_args
    )
    if windows_duration is not None:
        timings["windows"] = windows_duration

    linux_duration = _invoke_gradle_task_if_present_timed(linux_task, f"{title} - Linux build", path, gradle_args)
    if linux_duration is not None:
        timings["linux"] = linux_duration

    mac_duration = _invoke_gradle_task_if_present_timed(mac_task, f"{title} - Mac build", path, gradle_args)
    if mac_duration is not None:
        timings["mac"] = mac_duration

    print(f"{title} completed successfully.")
    return timings


def _clean_project(title: str, path: Path):
    _print_banner(title)
    rc, out, err = _run([_gradle_wrapper(path), "clean"], cwd=path)
    if out:
        print(out.strip())
    if err:
        print(err.strip())
    if rc != 0:
        raise RuntimeError(f"{title} clean failed with exit code {rc}")


def _build_and_publish(title: str, path: Path, label: str) -> Dict[str, float]:
    _print_banner(title)
    start = time.perf_counter()
    rc, out, err = _run([_gradle_wrapper(path), "build", "publishToMavenLocal"], cwd=path)
    duration_ms = (time.perf_counter() - start) * 1000
    if out:
        print(out.strip())
    if err:
        print(err.strip())
    if rc != 0:
        raise RuntimeError(f"{title} failed with exit code {rc}")

    print(f"{title} completed successfully in {duration_ms:.2f} ms.")
    return {label: duration_ms}


# Clean helpers

def clean_kir_helperkit():
    return _clean_project("## Cleaning KIRHelperKit", ROOT_DIR / "KIRHelperKit")


def clean_kperf_plugin():
    return _clean_project("## Cleaning k-perf (Kotlin compiler plugin)", ROOT_DIR / "plugins" / "k-perf")


def clean_instrumentation_overhead_analyzer_plugin():
    return _clean_project(
        "## Cleaning instrumentation-overhead-analyzer (Kotlin compiler plugin)",
        ROOT_DIR / "plugins" / "instrumentation-overhead-analyzer",
    )


def clean_game_of_life_commonmain_reference():
    return _clean_project(
        "## Cleaning game-of-life-kmp-commonmain reference application",
        ROOT_DIR / "kmp-examples" / "game-of-life-kmp-commonmain",
    )


def clean_game_of_life_commonmain_ioa():
    return _clean_project(
        "## Cleaning game-of-life-kmp-commonmain-ioa application",
        ROOT_DIR / "kmp-examples" / "game-of-life-kmp-commonmain-ioa",
    )


def clean_game_of_life_dedicatedmain_reference():
    return _clean_project(
        "## Cleaning game-of-life-kmp-dedicatedmain reference application",
        ROOT_DIR / "kmp-examples" / "game-of-life-kmp-dedicatedmain",
    )


def clean_game_of_life_commonmain_kperf_variant():
    return _clean_project(
        "## Cleaning game-of-life-kmp-commonmain-k-perf",
        ROOT_DIR / "kmp-examples" / "game-of-life-kmp-commonmain-k-perf",
    )


def clean_game_of_life_dedicatedmain_kperf_variant():
    return _clean_project(
        "## Cleaning game-of-life-kmp-dedicatedmain-k-perf",
        ROOT_DIR / "kmp-examples" / "game-of-life-kmp-dedicatedmain-k-perf",
    )


# Build helpers

def build_kir_helperkit() -> Dict[str, float]:
    return _build_and_publish("## Building KIRHelperKit", ROOT_DIR / "KIRHelperKit", "KirHelperKit")


def build_kperf_plugin() -> Dict[str, float]:
    return _build_and_publish("## Building k-perf (Kotlin compiler plugin)", ROOT_DIR / "plugins" / "k-perf", "KPerfPlugin")


def build_instrumentation_overhead_analyzer_plugin() -> Dict[str, float]:
    return _build_and_publish(
        "## Building instrumentation-overhead-analyzer (Kotlin compiler plugin)",
        ROOT_DIR / "plugins" / "instrumentation-overhead-analyzer",
        "InstrumentationOverheadAnalyzerPlugin",
    )


def build_game_of_life_commonmain_reference() -> Dict[str, float]:
    timings = _invoke_kmp_build_with_timings(
        "## Building game-of-life-kmp-commonmain reference application (without plugin)",
        ROOT_DIR / "kmp-examples" / "game-of-life-kmp-commonmain",
    )
    build_times: Dict[str, float] = {}
    if "jvm" in timings:
        build_times["commonmain-plain-jar"] = timings["jvm"]
        build_times["commonmain_plain_jar"] = timings["jvm"]
    if "js" in timings:
        build_times["commonmain-plain-node"] = timings["js"]
        build_times["commonmain_plain_node"] = timings["js"]
    if "windows" in timings:
        build_times["commonmain-plain-exe"] = timings["windows"]
        build_times["commonmain_plain_exe"] = timings["windows"]
    print("game-of-life-kmp-commonmain reference application build completed successfully.")
    return build_times


def build_game_of_life_dedicatedmain_reference() -> Dict[str, float]:
    timings = _invoke_kmp_build_with_timings(
        "## Building game-of-life-kmp-dedicatedmain reference application (without plugin)",
        ROOT_DIR / "kmp-examples" / "game-of-life-kmp-dedicatedmain",
    )
    build_times: Dict[str, float] = {}
    if "jvm" in timings:
        build_times["dedicatedmain-plain-jar"] = timings["jvm"]
    if "js" in timings:
        build_times["dedicatedmain-plain-node"] = timings["js"]
    if "windows" in timings:
        build_times["dedicatedmain-plain-exe"] = timings["windows"]
    print("game-of-life-kmp-dedicatedmain reference application build completed successfully.")
    return build_times


def build_game_of_life_commonmain_ioa() -> Dict[str, float]:
    timings = _invoke_kmp_build_with_timings(
        "## Building game-of-life-kmp-commonmain-ioa application",
        ROOT_DIR / "kmp-examples" / "game-of-life-kmp-commonmain-ioa",
    )
    build_times: Dict[str, float] = {}
    if "jvm" in timings:
        build_times["commonmain_ioa_jar"] = timings["jvm"]
    if "js" in timings:
        build_times["commonmain_ioa_node"] = timings["js"]
    if "windows" in timings:
        build_times["commonmain_ioa_exe"] = timings["windows"]
    print("game-of-life-kmp-commonmain-ioa build completed successfully.")
    return build_times


def build_game_of_life_commonmain_ioa_variant(config: IoaConfig) -> Dict[str, float]:
    project_name = "game-of-life-kmp-commonmain-ioa"
    project_path = ROOT_DIR / "kmp-examples" / project_name
    suffix = config.suffix()
    gradle_args = [f"-PioaKind={config.kind}"]

    print("")
    print(f"## Building {project_name} with suffix: {suffix}...")
    title = f"{project_name} ({suffix})"
    timings = _invoke_kmp_build_with_timings(title, project_path, gradle_args)

    build_times: Dict[str, float] = {}

    if "jvm" in timings:
        build_times[f"commonmain_ioa_{suffix}-jar"] = timings["jvm"]
    if "js" in timings:
        build_times[f"commonmain_ioa_{suffix}-node"] = timings["js"]
    if "windows" in timings:
        build_times[f"commonmain_ioa_{suffix}-exe"] = timings["windows"]
    print(f"{project_name} build with {suffix} completed successfully.")
    return build_times


def build_game_of_life_kperf_variant(game_type: str, config: KPerfConfig) -> Dict[str, float]:
    project_name = (
        "game-of-life-kmp-commonmain-k-perf" if game_type == "common" else "game-of-life-kmp-dedicatedmain-k-perf"
    )
    project_path = ROOT_DIR / "kmp-examples" / project_name
    suffix = config.suffix()
    gradle_args = [
        f"-PkperfFlushEarly={str(config.flush_early).lower()}",
        f"-PkperfInstrumentPropertyAccessors={str(config.instrument_property_accessors).lower()}",
        f"-PkperfTestKIR={str(config.test_kir).lower()}",
    ]

    print("")
    print(f"## Building {project_name} with suffix: {suffix}...")
    title = f"{project_name} ({suffix})"
    timings = _invoke_kmp_build_with_timings(title, project_path, gradle_args)

    build_times: Dict[str, float] = {}
    game_type_string = "commonmain" if game_type == "common" else "dedicatedmain"

    if "jvm" in timings:
        build_times[f"{game_type_string}-k-perf-{suffix}-jar"] = timings["jvm"]
    if "js" in timings:
        build_times[f"{game_type_string}-k-perf-{suffix}-node"] = timings["js"]
    if "windows" in timings:
        build_times[f"{game_type_string}-k-perf-{suffix}-exe"] = timings["windows"]

    print(f"{project_name} build with {suffix} completed successfully.")
    return build_times


_CLEAN_TARGETS = {
    "kirhelperkit": clean_kir_helperkit,
    "kperf-plugin": clean_kperf_plugin,
    "instrumentation-overhead-analyzer": clean_instrumentation_overhead_analyzer_plugin,
    "gol-commonmain": clean_game_of_life_commonmain_reference,
    "gol-commonmain-ioa": clean_game_of_life_commonmain_ioa,
    "gol-dedicatedmain": clean_game_of_life_dedicatedmain_reference,
    "gol-commonmain-k-perf": clean_game_of_life_commonmain_kperf_variant,
    "gol-dedicatedmain-k-perf": clean_game_of_life_dedicatedmain_kperf_variant,
}


_BUILD_TARGETS = {
    "kirhelperkit": build_kir_helperkit,
    "kperf-plugin": build_kperf_plugin,
    "instrumentation-overhead-analyzer": build_instrumentation_overhead_analyzer_plugin,
    "gol-commonmain-reference": build_game_of_life_commonmain_reference,
    "gol-dedicatedmain-reference": build_game_of_life_dedicatedmain_reference,
    "gol-commonmain-ioa": build_game_of_life_commonmain_ioa,
}


def _parse_args():
    parser = argparse.ArgumentParser(description="Python equivalent of benchmarking/build.ps1")
    subparsers = parser.add_subparsers(dest="command")

    clean_parser = subparsers.add_parser("clean", help="Clean a target")
    clean_parser.add_argument("target", choices=sorted(_CLEAN_TARGETS.keys()))

    build_parser = subparsers.add_parser("build", help="Build a target")
    build_parser.add_argument("target", choices=sorted(_BUILD_TARGETS.keys()))

    kperf_parser = subparsers.add_parser("build-kperf", help="Build a k-perf Game of Life variant")
    kperf_parser.add_argument("game_type", choices=["common", "dedicated"], help="Which variant to build")
    kperf_parser.add_argument("--flush-early", action="store_true", default=False)
    kperf_parser.add_argument("--instrument-property-accessors", action="store_true", default=False)
    kperf_parser.add_argument("--test-kir", action="store_true", default=False)

    return parser.parse_args()


def main():
    args = _parse_args()
    if args.command == "clean":
        _CLEAN_TARGETS[args.target]()
    elif args.command == "build":
        result = _BUILD_TARGETS[args.target]()
        print(result)
    elif args.command == "build-kperf":
        cfg = KPerfConfig(
            flush_early=args.flush_early,
            instrument_property_accessors=args.instrument_property_accessors,
            test_kir=args.test_kir,
        )
        result = build_game_of_life_kperf_variant(args.game_type, cfg)
        print(result)
    else:
        # If no args, print help
        _parse_args()


if __name__ == "__main__":
    main()

