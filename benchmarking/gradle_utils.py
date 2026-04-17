"""Gradle helper utilities for benchmarking scripts."""

from __future__ import annotations

import re
import subprocess
import sys
import time
from pathlib import Path
from typing import Optional

GRADLEW_CMD = "gradlew.bat" if sys.platform.startswith("win") else "./gradlew"

_JVM_CANDIDATES = ["jvmJar", "compileKotlinJvm"]
_JS_CANDIDATES = [
    "jsProductionExecutableCompileSync",
    "jsProductionExecutableCompile",
    "jsNodeProductionExecutableCompileSync",
    "jsNodeProductionExecutableCompile",
    "jsBrowserProductionWebpack",
    "compileKotlinJs",
]
_WINDOWS_CANDIDATES = ["linkReleaseExecutableMingwX64", "linkDebugExecutableMingwX64"]
_LINUX_CANDIDATES = ["linkReleaseExecutableLinuxX64", "linkDebugExecutableLinuxX64"]
_MAC_CANDIDATES = [
    "linkReleaseExecutableMacosX64",
    "linkDebugExecutableMacosX64",
    "linkReleaseExecutableMacosArm64",
    "linkDebugExecutableMacosArm64",
]


def find_first_gradle_task(task_list: str, candidates: list[str]) -> Optional[str]:
    for candidate in candidates:
        pattern = rf"(?m)^(?:\s*:)?{re.escape(candidate)}\b"
        if re.search(pattern, task_list):
            return candidate
    return None


def invoke_gradle_task_if_present(
    task_name: Optional[str], title: str, cwd: Path
) -> None:
    if not task_name or not task_name.strip():
        print(f"Skipping {title} (task not found)")
        return

    print()
    print("==========================================")
    print(title)
    print(f"Task: {task_name}")
    print("==========================================")

    result = subprocess.run([GRADLEW_CMD, task_name], cwd=cwd)
    if result.returncode != 0:
        raise RuntimeError(f"{title} failed with exit code {result.returncode}")

    print(f"{title} completed successfully.")


def invoke_gradle_task_if_present_timed(
    task_name: Optional[str],
    title: str,
    cwd: Path,
    gradle_args: Optional[list[str]] = None,
) -> Optional[float]:
    if not task_name or not task_name.strip():
        print(f"Skipping {title} (task not found)")
        return None

    if gradle_args is None:
        gradle_args = []

    print()
    print("==========================================")
    print(f"### {title}")
    print(f"Task: {task_name}")
    print("==========================================")

    task_start = time.monotonic()
    result = subprocess.run([GRADLEW_CMD] + gradle_args + [task_name], cwd=cwd)
    task_end = time.monotonic()

    if result.returncode != 0:
        raise RuntimeError(f"{title} failed with exit code {result.returncode}")

    task_duration_ms = (task_end - task_start) * 1000
    print(f"{title} completed successfully in {round(task_duration_ms, 2)} ms.")
    return task_duration_ms


def invoke_gradle_clean(path: Path, name: str) -> None:
    print()
    print("==========================================")
    print(f"## Cleaning {name}")
    print("==========================================")

    result = subprocess.run([GRADLEW_CMD, "clean"], cwd=path)
    if result.returncode != 0:
        print(f"ERROR: {name} clean failed!")
        sys.exit(1)


def invoke_kmp_build(title: str, path: Path, clean_build: bool = True) -> None:
    print()
    print("==========================================")
    print(f"{title} (Kotlin Multiplatform)")
    print(f"Path: {path}")
    print("==========================================")

    if clean_build:
        result = subprocess.run([GRADLEW_CMD, "clean"], cwd=path)
        if result.returncode != 0:
            raise RuntimeError(
                f"{title} clean failed with exit code {result.returncode}"
            )

    task_list_result = subprocess.run(
        [GRADLEW_CMD, "-q", "tasks", "--all"],
        cwd=path,
        capture_output=True,
        text=True,
    )
    if task_list_result.returncode != 0:
        raise RuntimeError(
            f"{title} task discovery failed with exit code {task_list_result.returncode}"
        )

    task_list = task_list_result.stdout

    jvm_task = find_first_gradle_task(task_list, _JVM_CANDIDATES)
    js_task = find_first_gradle_task(task_list, _JS_CANDIDATES)
    windows_task = find_first_gradle_task(task_list, _WINDOWS_CANDIDATES)
    linux_task = find_first_gradle_task(task_list, _LINUX_CANDIDATES)
    mac_task = find_first_gradle_task(task_list, _MAC_CANDIDATES)

    invoke_gradle_task_if_present(jvm_task, f"{title} - JVM build", cwd=path)
    invoke_gradle_task_if_present(js_task, f"{title} - JS build", cwd=path)
    invoke_gradle_task_if_present(windows_task, f"{title} - Windows build", cwd=path)
    invoke_gradle_task_if_present(linux_task, f"{title} - Linux build", cwd=path)
    invoke_gradle_task_if_present(mac_task, f"{title} - Mac build", cwd=path)

    print(f"{title} completed successfully.")


def invoke_kmp_build_with_timings(
    title: str, path: Path, gradle_args: Optional[list[str]] = None
) -> dict[str, float]:
    if gradle_args is None:
        gradle_args = []

    print()
    print("==========================================")
    print(f"## {title} (Kotlin Multiplatform)")
    print(f"Path: {path}")
    print("==========================================")

    timings: dict[str, float] = {}

    task_list_result = subprocess.run(
        [GRADLEW_CMD, "-q", "tasks", "--all"],
        cwd=path,
        capture_output=True,
        text=True,
    )
    if task_list_result.returncode != 0:
        raise RuntimeError(
            f"{title} task discovery failed with exit code {task_list_result.returncode}"
        )

    task_list = task_list_result.stdout

    jvm_task = find_first_gradle_task(task_list, _JVM_CANDIDATES)
    js_task = find_first_gradle_task(task_list, _JS_CANDIDATES)
    windows_task = find_first_gradle_task(task_list, _WINDOWS_CANDIDATES)
    linux_task = find_first_gradle_task(task_list, _LINUX_CANDIDATES)
    mac_task = find_first_gradle_task(task_list, _MAC_CANDIDATES)

    jvm_duration = invoke_gradle_task_if_present_timed(
        jvm_task, f"{title} - JVM build", cwd=path, gradle_args=gradle_args
    )
    if jvm_duration is not None:
        timings["jvm"] = jvm_duration

    js_duration = invoke_gradle_task_if_present_timed(
        js_task, f"{title} - JS build", cwd=path, gradle_args=gradle_args
    )
    if js_duration is not None:
        timings["js"] = js_duration

    windows_duration = invoke_gradle_task_if_present_timed(
        windows_task, f"{title} - Windows build", cwd=path, gradle_args=gradle_args
    )
    if windows_duration is not None:
        timings["windows"] = windows_duration

    linux_duration = invoke_gradle_task_if_present_timed(
        linux_task, f"{title} - Linux build", cwd=path, gradle_args=gradle_args
    )
    if linux_duration is not None:
        timings["linux"] = linux_duration

    mac_duration = invoke_gradle_task_if_present_timed(
        mac_task, f"{title} - Mac build", cwd=path, gradle_args=gradle_args
    )
    if mac_duration is not None:
        timings["mac"] = mac_duration

    print(f"{title} completed successfully.")
    return timings
