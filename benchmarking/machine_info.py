import getpass
import json
import platform
import re
import shutil
import subprocess
import time
from collections.abc import Callable
from datetime import datetime
from typing import Dict, List, Optional

import psutil

from utils import in_dir


def run(cmd: List[str]):
    try:
        p = subprocess.run(cmd, capture_output=True, text=True, encoding='utf-8')

        out = p.stdout.strip()
        err = p.stderr.strip()

        return p.returncode, out, err
    except FileNotFoundError:
        return 127, "", ""
    except Exception as e:
        return 1, "", str(e)


def which_binary(name: str) -> Optional[str]:
    return shutil.which(name)


def by_os(os: str, windows: Optional[Callable], darwin: Optional[Callable], linux: Optional[Callable]):
    (windows if os == "Windows" else darwin if os == "Darwin" else linux)()


def get_machine_info(gradle_project_path: str) -> Dict[str, any]:
    machine_info = {}

    # Operating System
    os = platform.system()
    machine_info['os'] = {
        'name': os,
        'release': platform.release(),
        'version': platform.version(),
        'architecture': platform.machine()
    }

    # Prepare system profiler results for macOS
    if os == "Darwin":
        result, out, _ = run(
            ["system_profiler", "SPHardwareDataType", "SPMemoryDataType", "-json"])
        if result != 0 or not out:
            raise Exception("Failed to get system profiler results")

        data = json.loads(out)

    # Additional OS information
    if os == "Windows":
        pass
    elif os == "Darwin":
        result, out, _ = run(['sw_vers'])
        if result == 0 and out:
            version = re.search(r"ProductVersion:\s+(\d+(?:\.\d+)*)", out)
            machine_info['os']['macos_version'] = version.group(1) if version else None
    else:  # Linux
        pass

    # Device
    if os == "Windows":
        pass
    elif os == "Darwin":
        # noinspection PyUnboundLocalVariable
        hardware = data['SPHardwareDataType'][0]

        machine_info['device'] = {
            'manufacturer': 'Apple Inc.',
            'model': hardware['machine_name'],
            'identifier': hardware['machine_model']
        }
        pass
    else:  # Linux
        pass

    # CPU
    logical_cores = psutil.cpu_count()
    physical_cores = psutil.cpu_count(logical=False)
    max_frequency = psutil.cpu_freq().max

    if os == "Windows":
        pass
    elif os == "Darwin":
        cpu_name = data['SPHardwareDataType'][0]['chip_type']
    else:  # Linux
        pass

    machine_info['cpu'] = {
        'name': cpu_name,
        'physical_cores': physical_cores,
        'logical_cores': logical_cores,
        'max_clock_mhz': max_frequency
    }

    # RAM
    virtual_memory = psutil.virtual_memory()
    total_ram = virtual_memory.total // 1_048_576
    free_ram = virtual_memory.available // 1_048_576

    if os == "Windows":
        pass
    elif os == "Darwin":
        memory = data['SPMemoryDataType'][0]

        ram_manufacturer = memory['dimm_manufacturer']
        ram_type = memory['dimm_type']

        ram_speed = None
        for key in ("dimm_speed", "memory_speed", "ram_speed"):
            v = memory.get(key)
            if isinstance(v, str):
                mm = re.search(r"(\d+)\s*MHz", v)
                if mm:
                    ram_speed = int(mm.group(1))
                    break
    else:  # Linux
        pass

    machine_info['memory'] = {
        'total': total_ram,
        'free': free_ram,
        'manufacturer': ram_manufacturer,
        'type': ram_type,
        'speed': ram_speed
    }

    # Disk
    main_disk = [disk for disk in psutil.disk_partitions() if disk.mountpoint == '/' or disk.mountpoint == r'C:\\'][0]
    main_disk_usage = psutil.disk_usage(main_disk.mountpoint)
    total_space = main_disk_usage.total // 1_000_000
    free_space = main_disk_usage.free // 1_000_000

    machine_info['disk'] = {
        'total': total_space,
        'free': free_space,
        'type': main_disk.fstype,
    }

    # System
    boot_time = psutil.boot_time()

    now = int(time.time())
    uptime_seconds = now - boot_time
    uptime_minutes = uptime_seconds // 60
    uptime_hours = uptime_minutes // 60
    uptime_days = uptime_hours // 24

    boot_datetime = datetime.fromtimestamp(boot_time)

    machine_info['system'] = {
        'uptime': {
            'total_seconds': uptime_seconds,
            'seconds': uptime_seconds % 60,
            'minutes': uptime_minutes % 60,
            'hours': uptime_hours % 24,
            'days': uptime_days
        },
        'boot_datetime': boot_datetime.isoformat(),
        'timezone': datetime.now().astimezone().tzname(),
        'user': getpass.getuser()
    }

    # Virtualization and Hypervisor
    if os == "Windows":
        pass
    elif os == "Darwin":
        _, hw_model, _ = run(['sysctl', '-n', 'hw.model'])
        _, brand, _ = run(['sysctl', '-n', 'machdep.cpu.brand_string'])
        _, features, _ = run(['sysctl', '-n', 'machdep.cpu.features'])
        _, leaf7_features, _ = run(['sysctl', '-n', 'machdep.cpu.leaf7_features'])

        hypervisor_flag = any(k in (features + " " + leaf7_features) for k in ("HYPERVISOR", "VMM"))
        likely_vm = hypervisor_flag or bool(
            re.search(r"vmware|virtualbox|parallels|qemu|kvm|xen|hyper-v|virtual", hw_model + " " + brand, re.I))

        machine_info['virtualization'] = {
            'hypervisor': hypervisor_flag,
            'vm': likely_vm,
        }
    else:  # Linux
        pass

    # Process Status
    machine_info['prcesses'] = len(psutil.pids())

    # Versions
    machine_info['versions'] = {}

    result, _, err = run(['java', '-version'])
    if result == 0 and err:
        m = re.search(r"version \"(.+?)\"", err.splitlines()[0])
        java_version = m.group(1) if m else None

        m = re.search("OpenJDK|Oracle|Adoptium|Temurin|Azul|Amazon|GraalVM", err)
        java_distribution = m.group(0) if m else None

        machine_info['versions']['java'] = {
            'version': java_version,
            'distribution': java_distribution
        }

    result, out, _ = run(['node', '--version'])
    if result == 0 and out:
        machine_info['versions']['node'] = out.strip()

    result, out, _ = run(['python', '--version'])
    if result == 0 and out:
        machine_info['versions']['python'] = out.strip()

    with in_dir(gradle_project_path):
        if os == "Windows":
            result, out, _ = run(['./gradlew.bat', '--version'])
        else:
            result, out, _ = run(['./gradlew', '--version'])

    if result == 0 and out:
        m = re.search(r"Gradle ((\d+\.)+\d+)", out)
        gradle_version = m.group(1) if m else None

        m = re.search(r"Kotlin:\s*((\d+\.)+\d+)", out)
        kotlin_version = m.group(1) if m else None

        machine_info['versions']['gradle'] = gradle_version
        machine_info['versions']['kotlin'] = kotlin_version

    # Git
    _, git_hash, _ = run(['git', 'rev-parse', 'HEAD'])
    _, branch, _ = run(['git', 'rev-parse', '--abbrev-ref', 'HEAD'])
    machine_info['git'] = {
        'hash': git_hash,
        'branch': branch
    }

    # OS Specific
    if os == "Windows":
        pass
    elif os == "Darwin":
        pass
    else:  # Linux
        pass

    # Timestamp

    return machine_info


def main():
    info = get_machine_info('../kmp-examples/game-of-life-kmp-commonmain')
    print(json.dumps(info, indent=2, ensure_ascii=False))


if __name__ == "__main__":
    main()
