"""Statistics, machine info, and CSV/JSON export utilities for benchmarking scripts."""

from __future__ import annotations

import csv
import json
import math
import os
import subprocess
import sys
from datetime import datetime
from pathlib import Path
from typing import Any, Optional

from benchmark_types import BenchmarkStatistics, ConfidenceInterval95

_BENCHMARKING_DIR = Path(__file__).resolve().parent

# T-Distribution critical values (two-tailed, alpha=0.05), keyed by degrees of freedom.
_T_VALUES = {
    1: 12.71, 2: 4.30, 3: 3.18, 4: 2.78, 5: 2.57,
    6: 2.45, 7: 2.36, 8: 2.31, 9: 2.26, 10: 2.23,
    11: 2.20, 12: 2.18, 13: 2.16, 14: 2.14, 15: 2.13,
    16: 2.12, 17: 2.11, 18: 2.10, 19: 2.09, 20: 2.09,
    21: 2.08, 22: 2.07, 23: 2.07, 24: 2.06, 25: 2.06,
    26: 2.06, 27: 2.05, 28: 2.05, 29: 2.05,
}

_MACHINE_INFO_CSV_ORDER = [
    "CollectionTimestamp", "GitCommitHash", "GitBranch",
    "DeviceManufacturer", "DeviceModel", "IsVirtualMachine",
    "OS", "OSArchitecture", "WindowsBuildNumber", "TimeZone", "Username",
    "BIOSVersion", "BIOSManufacturer",
    "SecureBootEnabled", "HyperVEnabled",
    "CPU", "CPUCores", "CPULogicalProcessors", "CPUMaxClockSpeedMHz",
    "TotalRAMGB", "AvailableRAMGB", "RAMModuleCount", "RAMSpeedMHz",
    "RAMManufacturer", "RAMPartNumber", "RAMModuleCapacitiesGB",
    "DiskModel", "DiskSizeGB", "DiskMediaType", "DiskInterfaceType",
    "SystemDriveFreeSpaceGB",
    "PowerPlan", "SystemUptimeHours", "RunningProcessCount",
    "WindowsDefenderEnabled", "WindowsDefenderRealTimeProtection",
    "PythonVersion", "JavaVersion", "JavaDistribution",
    "NodeVersion", "GradleVersion", "KotlinVersion",
]


def get_benchmark_statistics(values: list[float]) -> BenchmarkStatistics:
    count = len(values)
    if count == 0:
        return BenchmarkStatistics(count=0)

    sorted_vals = sorted(values)
    total = sum(values)
    mean = total / count

    if count % 2 == 1:
        median = sorted_vals[count // 2]
    else:
        mid = count // 2
        median = (sorted_vals[mid - 1] + sorted_vals[mid]) / 2

    min_val = sorted_vals[0]
    max_val = sorted_vals[-1]

    if count > 1:
        variance_sum = sum((v - mean) ** 2 for v in values)
        variance = variance_sum / (count - 1)
        stddev = math.sqrt(variance)
        stderr = stddev / math.sqrt(count)

        df = count - 1
        t_score = _T_VALUES.get(df, 1.96)  # Fall back to Z-score for N > 30

        ci_half_width = t_score * stderr
        ci95 = ConfidenceInterval95(lower=mean - ci_half_width, upper=mean + ci_half_width)
    else:
        stddev = 0.0
        ci95 = ConfidenceInterval95(lower=mean, upper=mean)

    return BenchmarkStatistics(
        count=count,
        mean=mean,
        median=median,
        stddev=stddev,
        min=min_val,
        max=max_val,
        ci95=ci95,
    )


def merge_dict(target: dict, source: dict) -> dict:
    if target is None:
        target = {}
    if source is None:
        return target
    target.update(source)
    return target


def _run_cmd(*args: str) -> str:
    """Run a command and return stdout, or empty string on failure."""
    try:
        result = subprocess.run(list(args), capture_output=True, text=True)
        return result.stdout.strip()
    except Exception:
        return ""


def _run_ps(cmd: str) -> str:
    """Run a PowerShell command and return stdout (Windows only)."""
    try:
        result = subprocess.run(
            ["powershell.exe", "-NoProfile", "-NonInteractive", "-Command", cmd],
            capture_output=True,
            text=True,
        )
        return result.stdout.strip()
    except Exception:
        return ""


def get_machine_info(
    gradle_project_path: Optional[Path] = None,
) -> dict[str, Any]:
    if gradle_project_path is None:
        gradle_project_path = (
            _BENCHMARKING_DIR.parent / "kmp-examples" / "game-of-life-kmp-commonmain"
        )

    info: dict[str, Any] = {}

    if sys.platform.startswith("win"):
        # --- Windows: hardware and system information via PowerShell ---
        try:
            cs = _run_ps(
                "(Get-CimInstance Win32_ComputerSystem | "
                "Select-Object Manufacturer,Model | ConvertTo-Json -Compress)"
            )
            if cs:
                d = json.loads(cs)
                info["DeviceManufacturer"] = d.get("Manufacturer", "")
                info["DeviceModel"] = d.get("Model", "")
        except Exception:
            pass

        try:
            os_info = _run_ps(
                "(Get-CimInstance Win32_OperatingSystem | "
                "Select-Object Caption,Version,OSArchitecture,BuildNumber,"
                "FreePhysicalMemory,LastBootUpTime | ConvertTo-Json -Compress)"
            )
            if os_info:
                d = json.loads(os_info)
                info["OS"] = f"{d.get('Caption','')} {d.get('Version','')}".strip()
                info["OSArchitecture"] = d.get("OSArchitecture", "")
                info["WindowsBuildNumber"] = d.get("BuildNumber", "")
                try:
                    free_kb = d.get("FreePhysicalMemory", 0)
                    info["AvailableRAMGB"] = round(int(free_kb) / (1024 * 1024), 2)
                except Exception:
                    pass
                try:
                    boot = d.get("LastBootUpTime", "")
                    if boot:
                        # Parse WMI datetime format (yyyyMMddHHmmss.ffffff+tz)
                        from datetime import timezone
                        boot_dt = datetime.strptime(boot[:14], "%Y%m%d%H%M%S")
                        uptime_h = (datetime.now() - boot_dt).total_seconds() / 3600
                        info["SystemUptimeHours"] = round(uptime_h, 2)
                except Exception:
                    info["SystemUptimeHours"] = "Not available"
        except Exception:
            pass

        try:
            cpu_info = _run_ps(
                "(Get-CimInstance Win32_Processor | Select-Object -First 1 | "
                "Select-Object Name,NumberOfCores,NumberOfLogicalProcessors,MaxClockSpeed "
                "| ConvertTo-Json -Compress)"
            )
            if cpu_info:
                d = json.loads(cpu_info)
                info["CPU"] = (d.get("Name") or "").strip()
                info["CPUCores"] = d.get("NumberOfCores")
                info["CPULogicalProcessors"] = d.get("NumberOfLogicalProcessors")
                info["CPUMaxClockSpeedMHz"] = d.get("MaxClockSpeed")
        except Exception:
            pass

        try:
            mem_info = _run_ps(
                "(Get-CimInstance Win32_ComputerSystem | "
                "Select-Object TotalPhysicalMemory | ConvertTo-Json -Compress)"
            )
            if mem_info:
                d = json.loads(mem_info)
                total = d.get("TotalPhysicalMemory", 0)
                info["TotalRAMGB"] = round(int(total) / (1024 ** 3), 2)
        except Exception:
            pass

        try:
            ram_modules = _run_ps(
                "(Get-CimInstance Win32_PhysicalMemory | "
                "Select-Object Speed,Manufacturer,PartNumber,Capacity | ConvertTo-Json -Compress)"
            )
            if ram_modules:
                mods = json.loads(ram_modules)
                if isinstance(mods, dict):
                    mods = [mods]
                if mods:
                    info["RAMModuleCount"] = len(mods)
                    first = mods[0]
                    if first.get("Speed"):
                        info["RAMSpeedMHz"] = first["Speed"]
                    if first.get("Manufacturer"):
                        info["RAMManufacturer"] = (first["Manufacturer"] or "").strip()
                    if first.get("PartNumber"):
                        info["RAMPartNumber"] = (first["PartNumber"] or "").strip()
                    caps = [round(int(m.get("Capacity", 0)) / (1024 ** 3), 2) for m in mods]
                    info["RAMModuleCapacitiesGB"] = " + ".join(str(c) for c in caps)
        except Exception:
            pass

        try:
            disk_info = _run_ps(
                "(Get-CimInstance Win32_DiskDrive | Where-Object {$_.DeviceID -eq '\\\\.\\PHYSICALDRIVE0'} | "
                "Select-Object -First 1 | Select-Object Model,Size,MediaType,InterfaceType "
                "| ConvertTo-Json -Compress)"
            )
            if disk_info:
                d = json.loads(disk_info)
                info["DiskModel"] = (d.get("Model") or "").strip()
                if d.get("Size"):
                    info["DiskSizeGB"] = round(int(d["Size"]) / (1024 ** 3), 2)
                if d.get("MediaType"):
                    info["DiskMediaType"] = d["MediaType"]
                if d.get("InterfaceType"):
                    info["DiskInterfaceType"] = d["InterfaceType"]
        except Exception:
            pass

        try:
            sys_drive = _run_ps(
                "(Get-CimInstance Win32_LogicalDisk | Where-Object {$_.DeviceID -eq 'C:'} | "
                "Select-Object FreeSpace | ConvertTo-Json -Compress)"
            )
            if sys_drive:
                d = json.loads(sys_drive)
                free = d.get("FreeSpace", 0)
                info["SystemDriveFreeSpaceGB"] = round(int(free) / (1024 ** 3), 2)
        except Exception:
            info["SystemDriveFreeSpaceGB"] = "Not available"

        try:
            power = _run_ps(
                "(Get-CimInstance -Namespace root\\cimv2\\power -ClassName Win32_PowerPlan "
                "| Where-Object {$_.IsActive} | Select-Object ElementName | ConvertTo-Json -Compress)"
            )
            if power:
                d = json.loads(power)
                info["PowerPlan"] = d.get("ElementName", "Not available")
            else:
                info["PowerPlan"] = "Not available"
        except Exception:
            info["PowerPlan"] = "Not available"

        try:
            proc_count = _run_ps("(Get-Process).Count")
            info["RunningProcessCount"] = int(proc_count) if proc_count.isdigit() else "Not available"
        except Exception:
            info["RunningProcessCount"] = "Not available"

        model = info.get("DeviceModel", "")
        info["IsVirtualMachine"] = bool(
            model and any(
                kw in model for kw in ["Virtual", "VMware", "VirtualBox", "Hyper-V", "KVM", "QEMU"]
            )
        )

        try:
            hyper_v = _run_ps(
                "(Get-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V "
                "-ErrorAction SilentlyContinue | Select-Object State | ConvertTo-Json -Compress)"
            )
            if hyper_v:
                d = json.loads(hyper_v)
                info["HyperVEnabled"] = d.get("State") == "Enabled"
            else:
                info["HyperVEnabled"] = "Unknown"
        except Exception:
            info["HyperVEnabled"] = "Unknown"

        try:
            secure_boot = _run_ps("Confirm-SecureBootUEFI -ErrorAction SilentlyContinue")
            info["SecureBootEnabled"] = secure_boot.lower() == "true" if secure_boot else "Unknown"
        except Exception:
            info["SecureBootEnabled"] = "Unknown"

        try:
            defender = _run_ps(
                "(Get-MpComputerStatus -ErrorAction SilentlyContinue | "
                "Select-Object AntivirusEnabled,RealTimeProtectionEnabled | ConvertTo-Json -Compress)"
            )
            if defender:
                d = json.loads(defender)
                info["WindowsDefenderEnabled"] = d.get("AntivirusEnabled")
                info["WindowsDefenderRealTimeProtection"] = d.get("RealTimeProtectionEnabled")
        except Exception:
            info["WindowsDefenderEnabled"] = "Unknown"

        info["TimeZone"] = _run_ps("(Get-TimeZone).Id") or "Not available"
        info["Username"] = os.environ.get("USERNAME", "Not available")

        try:
            bios_info = _run_ps(
                "(Get-CimInstance Win32_BIOS | Select-Object SMBIOSBIOSVersion,Manufacturer "
                "| ConvertTo-Json -Compress)"
            )
            if bios_info:
                d = json.loads(bios_info)
                info["BIOSVersion"] = d.get("SMBIOSBIOSVersion", "Not available")
                info["BIOSManufacturer"] = d.get("Manufacturer", "Not available")
        except Exception:
            info["BIOSVersion"] = "Not available"

    elif sys.platform.startswith("linux"):
        # --- Linux ---
        try:
            vendor_path = Path("/sys/class/dmi/id/sys_vendor")
            model_path = Path("/sys/class/dmi/id/product_name")
            if vendor_path.exists():
                info["DeviceManufacturer"] = vendor_path.read_text().strip()
            if model_path.exists():
                info["DeviceModel"] = model_path.read_text().strip()
        except Exception:
            pass

        try:
            os_release = Path("/etc/os-release")
            if os_release.exists():
                os_kv: dict[str, str] = {}
                for line in os_release.read_text().splitlines():
                    if "=" in line:
                        k, _, v = line.partition("=")
                        os_kv[k.strip()] = v.strip().strip('"')
                info["OS"] = os_kv.get("PRETTY_NAME") or _run_cmd("uname", "-a")
            else:
                info["OS"] = _run_cmd("uname", "-a")
        except Exception:
            info["OS"] = _run_cmd("uname", "-a")

        info["OSArchitecture"] = _run_cmd("uname", "-m")

        try:
            cpu_info_path = Path("/proc/cpuinfo")
            if cpu_info_path.exists():
                cpu_text = cpu_info_path.read_text()
                for line in cpu_text.splitlines():
                    if line.startswith("model name"):
                        _, _, val = line.partition(":")
                        info["CPU"] = val.strip()
                        break
                for line in cpu_text.splitlines():
                    if line.startswith("cpu cores"):
                        _, _, val = line.partition(":")
                        try:
                            info["CPUCores"] = int(val.strip())
                        except ValueError:
                            pass
                        break
            nproc_out = _run_cmd("nproc")
            if nproc_out.isdigit():
                info["CPULogicalProcessors"] = int(nproc_out)
        except Exception:
            pass

        try:
            mem_path = Path("/proc/meminfo")
            if mem_path.exists():
                for line in mem_path.read_text().splitlines():
                    if line.startswith("MemTotal:"):
                        kb = int(line.split()[1])
                        info["TotalRAMGB"] = round(kb / (1024 * 1024), 2)
                    elif line.startswith("MemAvailable:"):
                        kb = int(line.split()[1])
                        info["AvailableRAMGB"] = round(kb / (1024 * 1024), 2)
        except Exception:
            pass

        try:
            df_out = _run_cmd("df", "-BG", "/")
            lines = df_out.splitlines()
            if len(lines) > 1:
                m = __import__("re").search(r"\s+(\d+)G\s+\d+G\s+(\d+)G", lines[1])
                if m:
                    info["DiskSizeGB"] = int(m.group(1))
                    info["SystemDriveFreeSpaceGB"] = int(m.group(2))
        except Exception:
            pass

        try:
            uptime_raw = Path("/proc/uptime").read_text().split()[0]
            info["SystemUptimeHours"] = round(float(uptime_raw) / 3600.0, 2)
        except Exception:
            info["SystemUptimeHours"] = "Not available"

        info["TimeZone"] = _run_cmd("timedatectl", "show", "--property=Timezone", "--value") or "Not available"
        info["Username"] = os.environ.get("USER") or _run_cmd("whoami") or "Not available"

        try:
            import re as _re
            proc_count = len(list(Path("/proc").glob("[0-9]*")))
            info["RunningProcessCount"] = proc_count
        except Exception:
            info["RunningProcessCount"] = "Not available"

        try:
            product_name_path = Path("/sys/class/dmi/id/product_name")
            product = product_name_path.read_text().strip() if product_name_path.exists() else ""
            info["IsVirtualMachine"] = bool(
                product and any(
                    kw in product for kw in ["Virtual", "VMware", "VirtualBox", "KVM", "QEMU", "HVM"]
                )
            )
        except Exception:
            info["IsVirtualMachine"] = "Unknown"

    elif sys.platform == "darwin":
        # --- macOS ---
        try:
            product_name = _run_cmd("sw_vers", "-productName")
            product_version = _run_cmd("sw_vers", "-productVersion")
            info["OS"] = f"{product_name} {product_version}".strip()
        except Exception:
            info["OS"] = _run_cmd("uname", "-a")

        info["OSArchitecture"] = _run_cmd("uname", "-m")

        try:
            info["CPU"] = _run_cmd("sysctl", "-n", "machdep.cpu.brand_string")
            phys_cpu = _run_cmd("sysctl", "-n", "hw.physicalcpu")
            if phys_cpu.isdigit():
                info["CPUCores"] = int(phys_cpu)
            log_cpu = _run_cmd("sysctl", "-n", "hw.logicalcpu")
            if log_cpu.isdigit():
                info["CPULogicalProcessors"] = int(log_cpu)
        except Exception:
            pass

        try:
            mem_bytes_str = _run_cmd("sysctl", "-n", "hw.memsize")
            if mem_bytes_str.isdigit():
                info["TotalRAMGB"] = round(int(mem_bytes_str) / (1024 ** 3), 2)
        except Exception:
            pass

        try:
            df_out = _run_cmd("df", "-g", "/")
            lines = df_out.splitlines()
            if len(lines) > 1:
                parts = lines[1].split()
                if len(parts) >= 4:
                    info["DiskSizeGB"] = int(parts[1])
                    info["SystemDriveFreeSpaceGB"] = int(parts[3])
        except Exception:
            pass

        info["TimeZone"] = _run_cmd("readlink", "/etc/localtime").replace(
            "/var/db/timezone/zoneinfo/", ""
        ) or "Not available"
        info["Username"] = os.environ.get("USER") or _run_cmd("whoami") or "Not available"

        try:
            proc_count_raw = _run_cmd("ps", "-e")
            info["RunningProcessCount"] = len(proc_count_raw.splitlines()) - 1
        except Exception:
            info["RunningProcessCount"] = "Not available"

        info["IsVirtualMachine"] = False

    # --- Cross-platform runtime information ---

    info["PythonVersion"] = sys.version.split()[0]

    try:
        java_out = subprocess.run(
            ["java", "-version"], capture_output=True, text=True
        )
        # java -version writes to stderr
        java_lines = (java_out.stderr or java_out.stdout).splitlines()
        if java_lines:
            import re as _re
            m = _re.search(r'"(.+?)"', java_lines[0])
            if m:
                info["JavaVersion"] = m.group(1)
        for line in java_lines[1:3]:
            for dist in ["OpenJDK", "Oracle", "Adoptium", "Temurin", "Azul", "Amazon", "GraalVM"]:
                if dist in line:
                    info["JavaDistribution"] = dist
                    break
    except Exception:
        info["JavaVersion"] = "Not available"
        info["JavaDistribution"] = "Not available"

    try:
        info["NodeVersion"] = _run_cmd("node", "--version")
    except Exception:
        info["NodeVersion"] = "Not available"

    gradlew_name = "gradlew.bat" if sys.platform.startswith("win") else "gradlew"
    gradlew_path = gradle_project_path / gradlew_name
    try:
        gradle_out = subprocess.run(
            [str(gradlew_path), "--version"],
            capture_output=True,
            text=True,
            cwd=gradle_project_path,
        )
        output = gradle_out.stdout + gradle_out.stderr
        for line in output.splitlines():
            if line.startswith("Gradle "):
                info["GradleVersion"] = line.strip()
            if "Kotlin:" in line:
                info["KotlinVersion"] = line.strip()
    except Exception:
        info["GradleVersion"] = "Not available"
        info["KotlinVersion"] = "Not available"

    try:
        git_hash = subprocess.run(
            ["git", "rev-parse", "HEAD"], capture_output=True, text=True
        )
        if git_hash.returncode == 0:
            info["GitCommitHash"] = git_hash.stdout.strip()
            git_branch = subprocess.run(
                ["git", "rev-parse", "--abbrev-ref", "HEAD"],
                capture_output=True,
                text=True,
            )
            if git_branch.returncode == 0:
                info["GitBranch"] = git_branch.stdout.strip()
    except Exception:
        info["GitCommitHash"] = "Not available"

    info["CollectionTimestamp"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    return info


def export_benchmark_results_to_csv(results: list[dict], output_path: Path) -> None:
    if not results:
        print(f"CSV results exported to: {output_path} (empty)")
        return

    fieldnames = list(results[0].keys())
    with open(output_path, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames, extrasaction="ignore")
        writer.writeheader()
        writer.writerows(results)

    print(f"CSV results exported to: {output_path}")


def export_benchmark_results_to_json(results: list[dict], output_path: Path) -> None:
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(results, f, indent=4, default=_json_serialise)

    print(f"JSON results exported to: {output_path}")


def _json_serialise(obj: Any) -> Any:
    if hasattr(obj, "__dict__"):
        return obj.__dict__
    return str(obj)


def build_benchmark_csv_record(
    executable_name: str,
    statistics: BenchmarkStatistics,
    machine_info: dict[str, Any],
    repetition_count: int,
    clean_build: bool,
    step_count: int,
    build_time: Optional[float],
    additional_parameters: Optional[dict] = None,
) -> dict:
    record: dict = {
        "mean": statistics.mean,
        "median": statistics.median,
        "stddev": statistics.stddev,
        "min": statistics.min,
        "max": statistics.max,
        "ci95_lower": statistics.ci95.lower if statistics.ci95 else None,
        "ci95_upper": statistics.ci95.upper if statistics.ci95 else None,
        "executable": executable_name,
        "buildTimeMs": build_time,
        "RepetitionCount": repetition_count,
        "CleanBuild": clean_build,
        "StepCount": step_count,
    }

    if additional_parameters:
        record.update(additional_parameters)

    for key in _MACHINE_INFO_CSV_ORDER:
        if key in machine_info:
            record[key] = machine_info[key]

    return record
