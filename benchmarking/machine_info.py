import getpass
import json
import platform
import re
import subprocess
import time
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


def _read_first_line(path: str) -> Optional[str]:
    try:
        with open(path) as fh:
            return fh.readline().strip() or None
    except FileNotFoundError:
        return None
    except PermissionError:
        return None


def _pwsh_json(script: str) -> Optional[dict]:
    rc, out, _ = run(['powershell', '-NoProfile', '-NonInteractive', '-Command', script])
    if rc != 0 or not out:
        return None
    try:
        return json.loads(out)
    except json.JSONDecodeError:
        return None


def _pwsh_text(script: str) -> Optional[str]:
    rc, out, _ = run(['powershell', '-NoProfile', '-NonInteractive', '-Command', script])
    if rc != 0 or not out:
        return None
    return out.strip()


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

    if os == "Windows":
        pass
    elif os == "Darwin": # Prepare system profiler results for macOS
        result, out, _ = run(
            ["system_profiler", "SPHardwareDataType", "SPMemoryDataType", "-json"])
        if result != 0 or not out:
            raise Exception("Failed to get system profiler results")

        system_profiler_data = json.loads(out)
    else: # Prepare /etc/os-release data for Linux
        os_release_data: Dict[str, str] = {}
        try:
            with open('/etc/os-release') as fh:
                for line in fh:
                    if '=' in line:
                        k, v = line.strip().split('=', 1)
                        os_release_data[k] = v.strip('"')
        except FileNotFoundError:
            pass

    # Additional OS information
    if os == "Windows":
        os_info = _pwsh_json('Get-CimInstance Win32_OperatingSystem | Select-Object Caption,Version,OSArchitecture,BuildNumber | ConvertTo-Json -Compress') or {}
        machine_info['os'].update({
            'caption': os_info.get('Caption'),
            'build_number': os_info.get('BuildNumber'),
            'architecture': os_info.get('OSArchitecture') or machine_info['os']['architecture'],
        })
    elif os == "Darwin":
        result, out, _ = run(['sw_vers'])
        if result == 0 and out:
            version = re.search(r"ProductVersion:\s+(\d+(?:\.\d+)*)", out)
            machine_info['os']['macos_version'] = version.group(1) if version else None
    else:  # Linux
        # noinspection PyUnboundLocalVariable
        machine_info['os']['distro'] = os_release_data.get('NAME')
        machine_info['os']['distro_version'] = os_release_data.get('VERSION') or os_release_data.get('VERSION_ID')

    # Device
    if os == "Windows":
        cs = _pwsh_json('Get-CimInstance Win32_ComputerSystem | Select-Object Manufacturer,Model | ConvertTo-Json -Compress') or {}
        machine_info['device'] = {
            'manufacturer': cs.get('Manufacturer'),
            'model': cs.get('Model'),
            'identifier': platform.node()
        }
    elif os == "Darwin":
        # noinspection PyUnboundLocalVariable
        hardware = system_profiler_data['SPHardwareDataType'][0]

        machine_info['device'] = {
            'manufacturer': 'Apple Inc.',
            'model': hardware['machine_name'],
            'identifier': hardware['machine_model']
        }
        pass
    else:  # Linux
        machine_info['device'] = {
            'manufacturer': _read_first_line('/sys/devices/virtual/dmi/id/sys_vendor'),
            'model': _read_first_line('/sys/devices/virtual/dmi/id/product_name'),
            'identifier': _read_first_line('/sys/devices/virtual/dmi/id/product_version') or platform.node()
        }

    # CPU
    logical_cores = psutil.cpu_count()
    physical_cores = psutil.cpu_count(logical=False)
    max_frequency = psutil.cpu_freq().max

    cpu_name = None
    if os == "Windows":
        cpu_info = _pwsh_json('Get-CimInstance Win32_Processor | Select-Object -First 1 Name,NumberOfCores,NumberOfLogicalProcessors,MaxClockSpeed | ConvertTo-Json -Compress') or {}
        cpu_name = cpu_info.get('Name')
        physical_cores = cpu_info.get('NumberOfCores') or physical_cores
        logical_cores = cpu_info.get('NumberOfLogicalProcessors') or logical_cores
        max_frequency = cpu_info.get('MaxClockSpeed') or max_frequency
    elif os == "Darwin":
        cpu_name = system_profiler_data['SPHardwareDataType'][0]['chip_type']
    else:  # Linux
        try:
            with open('/proc/cpuinfo') as fh:
                for line in fh:
                    if line.lower().startswith('model name'):
                        cpu_name = line.split(':', 1)[1].strip()
                        break
        except FileNotFoundError:
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
        ram_info = _pwsh_json('Get-CimInstance Win32_PhysicalMemory | Select-Object -First 1 Manufacturer,MemoryType,Speed,ConfiguredClockSpeed,PartNumber | ConvertTo-Json -Compress') or {}
        ram_manufacturer = ram_info.get('Manufacturer')
        ram_type = str(ram_info.get('MemoryType')) if ram_info.get('MemoryType') is not None else None
        ram_speed = ram_info.get('ConfiguredClockSpeed') or ram_info.get('Speed')
    elif os == "Darwin":
        memory = system_profiler_data['SPMemoryDataType'][0]

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
        ram_manufacturer = _read_first_line('/sys/devices/virtual/dmi/id/board_vendor')
        ram_type = None
        ram_speed = None

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
        vm_info = _pwsh_json('Get-CimInstance Win32_ComputerSystem | Select-Object Model,Manufacturer | ConvertTo-Json -Compress') or {}
        mm = f"{vm_info.get('Model','')} {vm_info.get('Manufacturer','')}"
        likely_vm = bool(re.search(r"Virtual|VMware|VirtualBox|Hyper-V|KVM|QEMU", mm, re.I))

        hypervisor_flag = _pwsh_text('Get-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V -ErrorAction SilentlyContinue')

        machine_info['virtualization'] = {
            'hypervisor': hypervisor_flag and 'Enabled' in hypervisor_flag,
            'vm': likely_vm,
        }
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
        hypervisor_flag = False
        try:
            with open('/proc/cpuinfo') as fh:
                cpuinfo = fh.read()
                hypervisor_flag = 'hypervisor' in cpuinfo
        except FileNotFoundError:
            cpuinfo = ''

        virt_type = None
        rc, out, _ = run(['systemd-detect-virt'])
        if rc == 0 and out:
            virt_type = out.strip()

        likely_vm = hypervisor_flag or bool(virt_type and virt_type != 'none')
        machine_info['virtualization'] = {
            'hypervisor': hypervisor_flag,
            'vm': likely_vm,
            'type': virt_type
        }

    # OS Specific
    if os == "Windows":
        machine_info['os_specific'] = {
            'power_plan': _pwsh_text('(Get-CimInstance -Namespace root\\cimv2\\power -ClassName Win32_PowerPlan -ErrorAction SilentlyContinue | Where-Object {$_.IsActive -eq $true} | Select-Object -First 1 -ExpandProperty ElementName)'),
        }
    elif os == "Darwin":
        pass
    else:  # Linux
        machine_info['os_specific'] = {
            'pretty_name': os_release_data.get('PRETTY_NAME'),
            'kernel': platform.release(),
            'hostname': platform.node()
        }

    # Timestamp

    return machine_info


def main():
    info = get_machine_info('../kmp-examples/game-of-life-kmp-commonmain')
    print(json.dumps(info, indent=2, ensure_ascii=False))


if __name__ == "__main__":
    main()
