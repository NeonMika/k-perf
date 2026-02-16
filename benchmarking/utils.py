import math
import os
import re
import statistics
import subprocess
from contextlib import contextmanager

from typing import Dict, Optional, Iterator

@contextmanager
def in_dir(path: str) -> Iterator[None]:
    current_dir = os.getcwd()
    os.chdir(path)
    try:
        yield
    finally:
        os.chdir(current_dir)

def get_benchmark_statistics(values: list[float]) -> Dict[str, Optional[float | Dict[str, float]]]:
    count = len(values)
    if count == 0:
        return {
            'count': 0,
            'mean': None,
            'median': None,
            'stddev': None,
            'min': None,
            'max': None,
            'ci95': None
        }

    sorted_values = sorted(values)
    mean = sum(values) / count
    median = sorted_values[count // 2] if count % 2 == 1 else (sorted_values[count // 2 - 1] + sorted_values[
        count // 2]) / 2
    min_value = sorted_values[0]
    max_value = sorted_values[-1]

    if count > 1:
        stddev = statistics.stdev(values)
        stderr = stddev / math.sqrt(count)

        # --- FIX STARTS HERE ---
        # T-Distribution Critical Values (Two-tailed, alpha=0.05)
        # Lookup table for degrees of freedom (df = count - 1)
        t_values = {
            1: 12.71, 2: 4.30, 3: 3.18, 4: 2.78, 5: 2.57,
            6: 2.45, 7: 2.36, 8: 2.31, 9: 2.26, 10: 2.23,
            11: 2.20, 12: 2.18, 13: 2.16, 14: 2.14, 15: 2.13,
            16: 2.12, 17: 2.11, 18: 2.10, 19: 2.09, 20: 2.09,
            21: 2.08, 22: 2.07, 23: 2.07, 24: 2.06, 25: 2.06,
            26: 2.06, 27: 2.05, 28: 2.05, 29: 2.05
        }

        df = count - 1
        t_score = t_values.get(df, 1.96)

        ci_half_width = stderr * t_score
        # --- FIX ENDS HERE ---

        ci98 = {'lower': mean - ci_half_width, 'upper': mean + ci_half_width}
    else:
        stddev = 0.0
        ci98 = {'lower': mean, 'upper': mean}

    return {
        'count': count,
        'mean': mean,
        'median': median,
        'stddev': stddev,
        'min': min_value,
        'max': max_value,
        'ci98': ci98
    }


def find_first_gradle_task(task_list: str, candidates: list[str]) -> Optional[str]:
    for candidate in candidates:
        pattern = rf"(?m)^(?:\s*:)?{candidate}\b"
        if re.search(pattern, task_list):
            return candidate

    return None


def invoke_gradle_task_if_present(task: Optional[str], title: str):
    if not task:
        print(f"Skipping {title} (task not found)")
        return

    print("")
    print("==========================================")
    print(f"{title}")
    print(f"Task: {task}")
    print("==========================================")

    # execute ./gradlew <task> and check return code
    result = subprocess.run(['./gradlew', task], capture_output=True, text=True)

    if result.returncode != 0:
        raise Exception(f"Failed to execute task {task} with exit code {result.returncode}: {result.stderr}")

    print(f"{title} completed successfully.")

def invoke_kmp_build(title: str, path: str, clean_build: bool = True):
    print("")
    print("==========================================")
    print(f"{title} (Kotlin Multiplatform)")
    print(f"Path: {path}")
    print("==========================================")

    with in_dir(path):
        if clean_build:
            result = subprocess.run(['./gradlew', 'clean'], capture_output=True, text=True)
            if result.returncode != 0:
                raise Exception(f"{title} clean failed with exit code {result.returncode}: {result.stderr}")

        result = subprocess.run(['./gradlew', '-q', 'tasks', '--all'], capture_output=True, text=True)
        if result.returncode != 0:
            raise Exception(f"{title} tasks discovery failed with exit code {result.returncode}: {result.stderr}")

        task_list = result.stdout

        jvm_task = find_first_gradle_task(task_list, ['jvmJar', 'compileKotlinJvm'])
        js_task = find_first_gradle_task(task_list, ['jsProductionExecutableCompileSync', 'jsProductionExecutableCompile', 'jsNodeProductionExecutableCompileSync', 'jsNodeProductionExecutableCompile', 'jsBrowserProductionWebpack', 'compileKotlinJs'])
        windows_task = find_first_gradle_task(task_list, ['linkReleaseExecutableMingwX64', 'linkDebugExecutableMingwX64'])
        linux_task = find_first_gradle_task(task_list, ['linkReleaseExecutableLinuxX64', 'linkDebugExecutableLinuxX64'])
        mac_task = find_first_gradle_task(task_list, ['linkReleaseExecutableMacosX64', 'linkDebugExecutableMacosX64', 'linkReleaseExecutableMacosArm64', 'linkDebugExecutableMacosArm64'])

        invoke_gradle_task_if_present(jvm_task, f"{title} - JVM Build")
        invoke_gradle_task_if_present(js_task, f"{title} - JS Build")
        invoke_gradle_task_if_present(windows_task, f"{title} - Windows Build")
        invoke_gradle_task_if_present(linux_task, f"{title} - Linux Build")
        invoke_gradle_task_if_present(mac_task, f"{title} - macOS Build")

    print(f"{title} completed successfully.")

def get_machine_info(gradle_project_path: str = '../../kmp-examples/game-of-life-kmp-commonmain') -> Dict[str, str]:
    machine_info = {}

