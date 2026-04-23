"""Build functions for benchmarking applications."""

from __future__ import annotations

import shutil
import sys
from pathlib import Path
from typing import Optional

from benchmark_types import GameType, IoaConfig, KPerfConfig, get_game_type_string
from gradle_utils import (
    GRADLEW_CMD,
    invoke_gradle_clean,
    invoke_kmp_build_with_timings,
)

_BENCHMARKING_DIR = Path(__file__).resolve().parent
_REPO_ROOT = _BENCHMARKING_DIR.parent

ARTIFACT_VERSION = "0.2.1"

# Compute the platform label and native extension once at import time.
if sys.platform.startswith("win"):
    NATIVE_PLATFORM_LABEL = "win"
    NATIVE_EXT = ".exe"
elif sys.platform == "darwin":
    NATIVE_PLATFORM_LABEL = "mac"
    NATIVE_EXT = ".kexe"
else:
    NATIVE_PLATFORM_LABEL = "linux"
    NATIVE_EXT = ".kexe"


def build_kir_helper_kit() -> dict[str, float]:
    import subprocess
    import time

    print()
    print("==========================================")
    print("## Building KIRHelperKit")
    print("==========================================")

    path = _REPO_ROOT / "KIRHelperKit"
    start = time.monotonic()
    result = subprocess.run([GRADLEW_CMD, "build", "publishToMavenLocal"], cwd=path)
    duration_ms = (time.monotonic() - start) * 1000

    if result.returncode != 0:
        print("ERROR: KIRHelperKit build failed!")
        sys.exit(1)

    print(f"KIRHelperKit build completed successfully in {round(duration_ms, 2)} ms.")
    return {"KirHelperKit": duration_ms}


def build_k_perf_plugin() -> dict[str, float]:
    import subprocess
    import time

    print()
    print("==========================================")
    print("## Building k-perf (Kotlin compiler plugin)")
    print("==========================================")

    path = _REPO_ROOT / "plugins" / "k-perf"
    start = time.monotonic()
    result = subprocess.run([GRADLEW_CMD, "build", "publishToMavenLocal"], cwd=path)
    duration_ms = (time.monotonic() - start) * 1000

    if result.returncode != 0:
        print("ERROR: k-perf build failed!")
        sys.exit(1)

    print(f"k-perf build completed successfully in {round(duration_ms, 2)} ms.")
    return {"KPerfPlugin": duration_ms}


def build_instrumentation_overhead_analyzer_plugin() -> dict[str, float]:
    import subprocess
    import time

    print()
    print("==========================================")
    print("## Building instrumentation-overhead-analyzer (Kotlin compiler plugin)")
    print("==========================================")

    path = _REPO_ROOT / "plugins" / "instrumentation-overhead-analyzer"
    start = time.monotonic()
    result = subprocess.run([GRADLEW_CMD, "build", "publishToMavenLocal"], cwd=path)
    duration_ms = (time.monotonic() - start) * 1000

    if result.returncode != 0:
        print("ERROR: instrumentation-overhead-analyzer build failed!")
        sys.exit(1)

    print(
        f"instrumentation-overhead-analyzer build completed successfully in {round(duration_ms, 2)} ms."
    )
    return {"InstrumentationOverheadAnalyzerPlugin": duration_ms}


def build_game_of_life_commonmain_reference() -> dict[str, float]:
    print()
    print("==========================================")
    print("## Building game-of-life-kmp-commonmain reference application (without plugin)")
    print("==========================================")

    path = _REPO_ROOT / "kmp-examples" / "game-of-life-kmp-commonmain"
    timings = invoke_kmp_build_with_timings(
        "game-of-life-kmp-commonmain reference application", path
    )

    build_times: dict[str, float] = {}
    if "jvm" in timings:
        build_times["commonmain-plain-jar"] = timings["jvm"]
    if "js" in timings:
        build_times["commonmain-plain-node"] = timings["js"]
    if "windows" in timings:
        build_times["commonmain-plain-win-exe"] = timings["windows"]
    if "linux" in timings:
        build_times["commonmain-plain-linux-exe"] = timings["linux"]
    if "mac" in timings:
        build_times["commonmain-plain-mac-exe"] = timings["mac"]

    print("game-of-life-kmp-commonmain reference application build completed successfully.")
    return build_times


def build_game_of_life_dedicatedmain_reference() -> dict[str, float]:
    print()
    print("==========================================")
    print("## Building game-of-life-kmp-dedicatedmain reference application (without plugin)")
    print("==========================================")

    path = _REPO_ROOT / "kmp-examples" / "game-of-life-kmp-dedicatedmain"
    timings = invoke_kmp_build_with_timings(
        "game-of-life-kmp-dedicatedmain reference application", path
    )

    build_times: dict[str, float] = {}
    if "jvm" in timings:
        build_times["dedicatedmain-plain-jar"] = timings["jvm"]
    if "js" in timings:
        build_times["dedicatedmain-plain-node"] = timings["js"]
    if "windows" in timings:
        build_times["dedicatedmain-plain-win-exe"] = timings["windows"]
    if "linux" in timings:
        build_times["dedicatedmain-plain-linux-exe"] = timings["linux"]
    if "mac" in timings:
        build_times["dedicatedmain-plain-mac-exe"] = timings["mac"]

    print("game-of-life-kmp-dedicatedmain reference application build completed successfully.")
    return build_times


def build_game_of_life_commonmain_ioa() -> dict[str, float]:
    print()
    print("==========================================")
    print("## Building game-of-life-kmp-commonmain-ioa application")
    print("==========================================")

    path = _REPO_ROOT / "kmp-examples" / "game-of-life-kmp-commonmain-ioa"
    timings = invoke_kmp_build_with_timings(
        "game-of-life-kmp-commonmain-ioa application", path
    )

    build_times: dict[str, float] = {}
    if "jvm" in timings:
        build_times["commonmain-ioa-jar"] = timings["jvm"]
    if "js" in timings:
        build_times["commonmain-ioa-node"] = timings["js"]
    if "windows" in timings:
        build_times["commonmain-ioa-win-exe"] = timings["windows"]
    if "linux" in timings:
        build_times["commonmain-ioa-linux-exe"] = timings["linux"]
    if "mac" in timings:
        build_times["commonmain-ioa-mac-exe"] = timings["mac"]

    print("game-of-life-kmp-commonmain-ioa build completed successfully.")
    return build_times


def build_game_of_life_commonmain_ioa_variant(config: IoaConfig) -> dict[str, float]:
    project_name = "game-of-life-kmp-commonmain-ioa"
    project_path = _REPO_ROOT / "kmp-examples" / project_name
    suffix = config.suffix()
    gradle_args = [f"-PioaKind={config.kind}"]

    print()
    print(f"## Building {project_name} with suffix: {suffix}...")
    timings = invoke_kmp_build_with_timings(
        f"{project_name} ({suffix})", project_path, gradle_args=gradle_args
    )

    # Copy artifacts from dist/ into bin/<suffix>/ so multiple IoaKind variants can coexist
    bin_dir = project_path / "bin" / suffix
    bin_dir.mkdir(parents=True, exist_ok=True)
    dist_dir = project_path / "dist"
    if dist_dir.exists():
        for item in dist_dir.iterdir():
            if item.is_file():
                shutil.copy2(str(item), str(bin_dir))

    build_times: dict[str, float] = {}
    if "jvm" in timings:
        build_times[f"commonmain-ioa-{suffix}-jar"] = timings["jvm"]
    if "js" in timings:
        build_times[f"commonmain-ioa-{suffix}-node"] = timings["js"]
    if "windows" in timings:
        build_times[f"commonmain-ioa-{suffix}-win-exe"] = timings["windows"]
    if "linux" in timings:
        build_times[f"commonmain-ioa-{suffix}-linux-exe"] = timings["linux"]
    if "mac" in timings:
        build_times[f"commonmain-ioa-{suffix}-mac-exe"] = timings["mac"]

    print(f"{project_name} build with {suffix} completed successfully.")
    return build_times


def get_k_perf_suffix(config: KPerfConfig) -> str:
    return (
        f"enabled-{'true' if config.enabled else 'false'}"
        f"-flushEarly-{'true' if config.flush_early else 'false'}"
        f"-propAccessors-{'true' if config.instrument_property_accessors else 'false'}"
        f"-testKIR-{'true' if config.test_kir else 'false'}"
    )


def build_game_of_life_k_perf_variant(
    game_type: GameType, config: KPerfConfig
) -> dict[str, float]:
    if game_type == GameType.CommonMain:
        project_name = "game-of-life-kmp-commonmain-k-perf"
    else:
        project_name = "game-of-life-kmp-dedicatedmain-k-perf"

    project_path = _REPO_ROOT / "kmp-examples" / project_name
    suffix = get_k_perf_suffix(config)
    gradle_args = [
        f"-PkperfEnabled={str(config.enabled).lower()}",
        f"-PkperfFlushEarly={str(config.flush_early).lower()}",
        f"-PkperfInstrumentPropertyAccessors={str(config.instrument_property_accessors).lower()}",
        f"-PkperfTestKIR={str(config.test_kir).lower()}",
        f"-PkperfMethods={config.methods}",
    ]

    print()
    print(f"## Building {project_name} with suffix: {suffix}...")
    title = f"{project_name} ({suffix})"
    timings = invoke_kmp_build_with_timings(title, project_path, gradle_args=gradle_args)

    # Copy artifacts from dist/ into bin/<suffix>/ so multiple configs can coexist
    bin_dir = project_path / "bin" / suffix
    bin_dir.mkdir(parents=True, exist_ok=True)
    dist_dir = project_path / "dist"
    if dist_dir.exists():
        for item in dist_dir.iterdir():
            if item.is_file():
                shutil.copy2(str(item), str(bin_dir))

    build_times: dict[str, float] = {}
    game_type_str = get_game_type_string(game_type)

    if "jvm" in timings:
        build_times[f"{game_type_str}-k-perf-{suffix}-jar"] = timings["jvm"]
    if "js" in timings:
        build_times[f"{game_type_str}-k-perf-{suffix}-node"] = timings["js"]
    if "windows" in timings:
        build_times[f"{game_type_str}-k-perf-{suffix}-win-exe"] = timings["windows"]
    if "linux" in timings:
        build_times[f"{game_type_str}-k-perf-{suffix}-linux-exe"] = timings["linux"]
    if "mac" in timings:
        build_times[f"{game_type_str}-k-perf-{suffix}-mac-exe"] = timings["mac"]

    print(f"{project_name} build with {suffix} completed successfully.")
    return build_times


def invoke_get_executables(
    game_types: list[GameType],
    k_perf_combinations: list[KPerfConfig],
    reference: bool,
    jvm: bool,
    js: bool,
    native: bool,
    native_ext: str,
    artifact_version: str,
) -> list:
    from benchmark_types import BenchmarkExecutable, ExecutableType

    executables: list[BenchmarkExecutable] = []

    for game_type in game_types:
        game_type_str = get_game_type_string(game_type)

        if game_type == GameType.CommonMain:
            project_name = "game-of-life-kmp-commonmain"
            k_perf_project_name = "game-of-life-kmp-commonmain-k-perf"
        else:
            project_name = "game-of-life-kmp-dedicatedmain"
            k_perf_project_name = "game-of-life-kmp-dedicatedmain-k-perf"

        plain_root = _REPO_ROOT / "kmp-examples" / project_name
        k_perf_root = _REPO_ROOT / "kmp-examples" / k_perf_project_name

        if reference and jvm:
            executables.append(
                BenchmarkExecutable(
                    name=f"{game_type_str}-plain-jar",
                    path=str(plain_root / "dist" / f"{project_name}-jvm-{artifact_version}.jar"),
                    type=ExecutableType.Jar,
                    config=None,
                )
            )

        if reference and js:
            executables.append(
                BenchmarkExecutable(
                    name=f"{game_type_str}-plain-node",
                    path=str(plain_root / "dist" / f"{project_name}.js"),
                    type=ExecutableType.Node,
                    config=None,
                )
            )

        if reference and native:
            executables.append(
                BenchmarkExecutable(
                    name=f"{game_type_str}-plain-{NATIVE_PLATFORM_LABEL}-exe",
                    path=str(plain_root / "dist" / f"{project_name}{native_ext}"),
                    type=ExecutableType.Exe,
                    config=None,
                )
            )

        for config in k_perf_combinations:
            suffix = get_k_perf_suffix(config)

            if jvm:
                executables.append(
                    BenchmarkExecutable(
                        name=f"{game_type_str}-k-perf-{suffix}-jar",
                        path=str(
                            k_perf_root
                            / "bin"
                            / suffix
                            / f"{k_perf_project_name}-jvm-{artifact_version}.jar"
                        ),
                        type=ExecutableType.Jar,
                        config=config,
                    )
                )

            if js:
                executables.append(
                    BenchmarkExecutable(
                        name=f"{game_type_str}-k-perf-{suffix}-node",
                        path=str(k_perf_root / "bin" / suffix / f"{k_perf_project_name}.js"),
                        type=ExecutableType.Node,
                        config=config,
                    )
                )

            if native:
                executables.append(
                    BenchmarkExecutable(
                        name=f"{game_type_str}-k-perf-{suffix}-{NATIVE_PLATFORM_LABEL}-exe",
                        path=str(
                            k_perf_root / "bin" / suffix / f"{k_perf_project_name}{native_ext}"
                        ),
                        type=ExecutableType.Exe,
                        config=config,
                    )
                )

    return executables


def invoke_get_ioa_executables(
    ioa_configs: list[IoaConfig],
    reference: bool,
    ioa: bool,
    jvm: bool,
    js: bool,
    native: bool,
    artifact_version: str,
) -> list:
    from benchmark_types import BenchmarkExecutable, ExecutableType

    executables: list[BenchmarkExecutable] = []

    plain_project_name = "game-of-life-kmp-commonmain"
    ioa_project_name = "game-of-life-kmp-commonmain-ioa"
    plain_root = _REPO_ROOT / "kmp-examples" / plain_project_name
    ioa_root = _REPO_ROOT / "kmp-examples" / ioa_project_name

    if reference and jvm:
        executables.append(
            BenchmarkExecutable(
                name="commonmain-plain-jar",
                path=str(plain_root / "dist" / f"{plain_project_name}-jvm-{artifact_version}.jar"),
                type=ExecutableType.Jar,
                config=None,
            )
        )

    if reference and js:
        executables.append(
            BenchmarkExecutable(
                name="commonmain-plain-node",
                path=str(plain_root / "dist" / f"{plain_project_name}.js"),
                type=ExecutableType.Node,
                config=None,
            )
        )

    if reference and native:
        executables.append(
            BenchmarkExecutable(
                name=f"commonmain-plain-{NATIVE_PLATFORM_LABEL}-exe",
                path=str(plain_root / "dist" / f"{plain_project_name}{NATIVE_EXT}"),
                type=ExecutableType.Exe,
                config=None,
            )
        )

    for config in ioa_configs:
        suffix = config.suffix()

        if ioa and jvm:
            executables.append(
                BenchmarkExecutable(
                    name=f"commonmain-ioa-{suffix}-jar",
                    path=str(
                        ioa_root / "bin" / suffix / f"{ioa_project_name}-jvm-{artifact_version}.jar"
                    ),
                    type=ExecutableType.Jar,
                    config=config,
                )
            )

        if ioa and js:
            executables.append(
                BenchmarkExecutable(
                    name=f"commonmain-ioa-{suffix}-node",
                    path=str(ioa_root / "bin" / suffix / f"{ioa_project_name}.js"),
                    type=ExecutableType.Node,
                    config=config,
                )
            )

        if ioa and native:
            executables.append(
                BenchmarkExecutable(
                    name=f"commonmain-ioa-{suffix}-{NATIVE_PLATFORM_LABEL}-exe",
                    path=str(ioa_root / "bin" / suffix / f"{ioa_project_name}{NATIVE_EXT}"),
                    type=ExecutableType.Exe,
                    config=config,
                )
            )

    return executables

