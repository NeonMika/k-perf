#!/usr/bin/env python3
# python

import platform
import subprocess
import json
import os
import re
import shutil
import datetime
import time
import getpass
from typing import Any, Dict, List, Optional

try:
    import psutil
except Exception:
    psutil = None

def run(cmd: List[str], capture_stderr=False, text=True):
    try:
        p = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=text)
        out = p.stdout.strip()
        err = p.stderr.strip()
        return p.returncode, out, err if capture_stderr else out
    except FileNotFoundError:
        return 127, "", ""
    except Exception as e:
        return 1, "", str(e)

def which_binary(name: str) -> Optional[str]:
    return shutil.which(name)

def get_platform_basic() -> Dict[str, Any]:
    system = platform.system()
    arch = platform.machine()
    version = platform.version()
    release = platform.release()
    return {
        "os": system,
        "os_release": release,
        "os_version": version,
        "os_architecture": arch
    }

def cpu_info() -> Dict[str, Any]:
    cpu = {}
    try:
        if platform.system() == "Windows":
            # try wmic
            rc, out, _ = run(["wmic", "cpu", "get", "Name,NumberOfCores,NumberOfLogicalProcessors,MaxClockSpeed", "/format:csv"])
            if rc == 0 and out:
                lines = [l for l in out.splitlines() if l.strip()]
                if len(lines) >= 2:
                    headers = [h.strip() for h in lines[0].split(",")]
                    vals = [v.strip() for v in lines[1].split(",")]
                    d = dict(zip(headers, vals))
                    cpu["name"] = d.get("Name")
                    cpu["cores_physical"] = int(d.get("NumberOfCores")) if d.get("NumberOfCores") else None
                    cpu["cores_logical"] = int(d.get("NumberOfLogicalProcessors")) if d.get("NumberOfLogicalProcessors") else None
                    cpu["max_clock_mhz"] = int(d.get("MaxClockSpeed")) if d.get("MaxClockSpeed") else None
        elif platform.system() == "Darwin":
            rc, out, _ = run(["sysctl", "-n", "machdep.cpu.brand_string"])
            cpu["name"] = out if rc == 0 else None
            rc, out, _ = run(["sysctl", "-n", "hw.physicalcpu"])
            cpu["cores_physical"] = int(out) if rc == 0 and out.isdigit() else None
            rc, out, _ = run(["sysctl", "-n", "hw.logicalcpu"])
            cpu["cores_logical"] = int(out) if rc == 0 and out.isdigit() else None
            rc, out, _ = run(["sysctl", "-n", "hw.cpufrequency_max"])
            if rc == 0 and out.isdigit():
                cpu["max_clock_mhz"] = int(int(out) / 1_000_000)
        else:
            # Linux and others
            if os.path.exists("/proc/cpuinfo"):
                with open("/proc/cpuinfo", "r", encoding="utf-8", errors="ignore") as f:
                    txt = f.read()
                m = re.search(r"model name\s*:\s*(.+)", txt)
                cpu["name"] = m.group(1).strip() if m else None
                # cores
                rcnt = len(re.findall(r"^processor\s*:", txt, flags=re.MULTILINE))
                cpu["cores_logical"] = rcnt or None
                # physical cores - try lscpu
                rc, out, _ = run(["lscpu"])
                if rc == 0 and out:
                    m = re.search(r"Core\(s\) per socket:\s*(\d+)", out)
                    sockets = re.search(r"Socket\(s\):\s*(\d+)", out)
                    if m:
                        cores_per_socket = int(m.group(1))
                        sockets_n = int(sockets.group(1)) if sockets else 1
                        cpu["cores_physical"] = cores_per_socket * sockets_n
                    freq_m = re.search(r"CPU max MHz:\s*([\d.]+)", out) or re.search(r"CPU MHz:\s*([\d.]+)", out)
                    if freq_m:
                        try:
                            cpu["max_clock_mhz"] = int(float(freq_m.group(1)))
                        except Exception:
                            pass
    except Exception:
        pass

    # fallback via psutil
    try:
        if psutil:
            if "cores_logical" not in cpu or cpu.get("cores_logical") is None:
                cpu["cores_logical"] = psutil.cpu_count(logical=True)
            if "cores_physical" not in cpu or cpu.get("cores_physical") is None:
                cpu["cores_physical"] = psutil.cpu_count(logical=False)
            if "max_clock_mhz" not in cpu or cpu.get("max_clock_mhz") is None:
                try:
                    f = psutil.cpu_freq()
                    cpu["max_clock_mhz"] = int(f.max) if f and f.max else None
                except Exception:
                    pass
    except Exception:
        pass

    return cpu

def memory_info() -> Dict[str, Any]:
    mem = {"total": None, "available": None, "modules": None}
    try:
        if psutil:
            vm = psutil.virtual_memory()
            mem["total"] = vm.total
            mem["available"] = vm.available
    except Exception:
        pass

    modules = []
    try:
        syst = platform.system()
        if syst == "Windows":
            rc, out, _ = run(["wmic", "memorychip", "get", "Capacity,Speed,Manufacturer,PartNumber,DeviceLocator"], capture_stderr=True)
            if rc == 0 and out:
                lines = [l for l in out.splitlines() if l.strip()]
                if len(lines) > 1:
                    headers = [h.strip() for h in lines[0].split()]
                    for ln in lines[1:]:
                        parts = ln.split()
                        # fallback naive parsing
                        vals = parts
                        # try regex line parsing
                        match = re.findall(r"(\S+)", ln)
                        if match and len(match) >= 4:
                            # attempt best-effort mapping
                            modules.append({
                                "manufacturer": match[0],
                                "capacity": int(match[1]) if match[1].isdigit() else None,
                                "speed_mhz": int(match[2]) if match[2].isdigit() else None,
                                "part_number": match[3]
                            })
        elif syst == "Darwin":
            rc, out, _ = run(["system_profiler", "SPMemoryDataType"])
            if rc == 0 and out:
                blocks = re.split(r"\n+\s*\n+", out)
                for b in blocks:
                    m_capacity = re.search(r"Size:\s*(.+)", b)
                    m_speed = re.search(r"Speed:\s*(.+)", b)
                    m_part = re.search(r"Part Number:\s*(.+)", b)
                    m_man = re.search(r"Manufacturer:\s*(.+)", b)
                    if m_capacity:
                        cap = m_capacity.group(1).strip()
                        cap_bytes = None
                        mm = re.match(r"(\d+)\s*GB", cap)
                        if mm:
                            cap_bytes = int(mm.group(1)) * 1024**3
                        modules.append({
                            "manufacturer": m_man.group(1).strip() if m_man else None,
                            "capacity": cap_bytes,
                            "speed_mhz": int(re.search(r"(\d+)", m_speed.group(1)).group(1)) if m_speed and re.search(r"(\d+)", m_speed.group(1)) else None,
                            "part_number": m_part.group(1).strip() if m_part else None
                        })
        else:
            # Linux: try dmidecode (may require root)
            if which_binary("dmidecode"):
                rc, out, _ = run(["dmidecode", "--type", "17"])
                if rc == 0 and out:
                    entries = out.split("\n\n")
                    for e in entries:
                        if "Memory Device" in e:
                            m_man = re.search(r"Manufacturer:\s*(.+)", e)
                            m_part = re.search(r"Part Number:\s*(.+)", e)
                            m_size = re.search(r"Size:\s*(.+)", e)
                            m_speed = re.search(r"Speed:\s*(.+)", e)
                            cap = None
                            if m_size:
                                s = m_size.group(1).strip()
                                if s.endswith("MB"):
                                    cap = int(float(s.split()[0])) * 1024**2
                                elif s.endswith("GB"):
                                    cap = int(float(s.split()[0])) * 1024**3
                                elif s.isdigit():
                                    cap = int(s)
                            modules.append({
                                "manufacturer": m_man.group(1).strip() if m_man else None,
                                "part_number": m_part.group(1).strip() if m_part else None,
                                "capacity": cap,
                                "speed_mhz": int(re.search(r"(\d+)", m_speed.group(1)).group(1)) if m_speed and re.search(r"(\d+)", m_speed.group(1)) else None
                            })
    except Exception:
        pass

    if modules:
        mem["modules"] = modules
        mem["module_count"] = len(modules)
        try:
            mem["ram_capacity_gb"] = sum(m.get("capacity") or 0 for m in modules) / (1024**3)
        except Exception:
            mem["ram_capacity_gb"] = None
    else:
        mem["modules"] = None
        mem["module_count"] = None
        mem["ram_capacity_gb"] = (mem["total"] / (1024**3)) if mem["total"] else None

    return mem

def disk_info() -> Dict[str, Any]:
    res = {"disks": [], "total_free_bytes": None}
    try:
        if psutil:
            total_free = 0
            for part in psutil.disk_partitions(all=False):
                try:
                    usage = psutil.disk_usage(part.mountpoint)
                    total_free += usage.free
                except Exception:
                    continue
            res["total_free_bytes"] = total_free
    except Exception:
        pass

    syst = platform.system()
    try:
        if syst == "Windows":
            rc, out, _ = run(["wmic", "diskdrive", "get", "Model,Size,InterfaceType,MediaType"])
            if rc == 0 and out:
                lines = [l for l in out.splitlines() if l.strip()]
                headers = re.split(r"\s{2,}", lines[0].strip())
                for ln in lines[1:]:
                    vals = re.split(r"\s{2,}", ln.strip())
                    if len(vals) == len(headers):
                        d = dict(zip(headers, vals))
                        res["disks"].append({
                            "model": d.get("Model"),
                            "size": int(d.get("Size")) if d.get("Size") and d.get("Size").isdigit() else None,
                            "type": d.get("MediaType"),
                            "interface": d.get("InterfaceType")
                        })
        elif syst == "Darwin":
            rc, out, _ = run(["system_profiler", "SPStorageDataType", "-detailLevel", "mini"])
            if rc == 0 and out:
                # best-effort parse of Model and Size
                blocks = out.split("\n\n")
                for b in blocks:
                    m_model = re.search(r"Device Name:\s*(.+)", b) or re.search(r"Media Name:\s*(.+)", b)
                    m_size = re.search(r"Capacity:\s*(.+)", b)
                    if m_model:
                        sz = None
                        if m_size:
                            m = re.search(r"(\d+(?:\.\d+)?)\s*(GB|TB|MB)", m_size.group(1))
                            if m:
                                v, unit = float(m.group(1)), m.group(2)
                                mul = {"MB": 1024**2, "GB": 1024**3, "TB": 1024**4}[unit]
                                sz = int(v * mul)
                        res["disks"].append({
                            "model": m_model.group(1).strip(),
                            "size": sz,
                            "type": None,
                            "interface": None
                        })
        else:
            # Linux
            if which_binary("lsblk"):
                rc, out, _ = run(["lsblk", "-o", "NAME,MODEL,SIZE,ROTA,TYPE,TRAN", "-b", "-J"])
                # Some lsblk implementations support -J JSON; fallback to text
                try:
                    j = json.loads(out)
                    for d in j.get("blockdevices", []):
                        if d.get("type") in ("disk",):
                            res["disks"].append({
                                "model": d.get("model"),
                                "size": int(d.get("size")) if isinstance(d.get("size"), (int, float)) else None,
                                "type": ("HDD" if d.get("rota") == "1" else "SSD") if "rota" in d else None,
                                "interface": d.get("tran")
                            })
                except Exception:
                    # text fallback
                    rc, out2, _ = run(["lsblk", "-o", "NAME,MODEL,SIZE,ROTA,TYPE,TRAN"])
                    for ln in out2.splitlines()[1:]:
                        parts = [p for p in re.split(r"\s{2,}", ln.strip()) if p]
                        if len(parts) >= 6 and parts[4] == "disk":
                            res["disks"].append({
                                "model": parts[1],
                                "size": None,
                                "type": ("HDD" if parts[3] == "1" else "SSD"),
                                "interface": parts[5]
                            })
    except Exception:
        pass

    return res

def bios_info() -> Dict[str, Any]:
    out = {"bios_version": None, "bios_manufacturer": None}
    syst = platform.system()
    try:
        if syst == "Windows":
            rc, outp, _ = run(["wmic", "bios", "get", "Manufacturer,SMBIOSBIOSVersion"], capture_stderr=True)
            if rc == 0 and outp:
                lines = [l for l in outp.splitlines() if l.strip()]
                if len(lines) >= 2:
                    cols = re.split(r"\s{2,}", lines[0].strip())
                    vals = re.split(r"\s{2,}", lines[1].strip())
                    d = dict(zip(cols, vals))
                    out["bios_manufacturer"] = d.get("Manufacturer")
                    out["bios_version"] = d.get("SMBIOSBIOSVersion")
        elif syst == "Darwin":
            rc, outp, _ = run(["system_profiler", "SPHardwareDataType"])
            if rc == 0 and outp:
                m_brm = re.search(r"Boot ROM Version:\s*(.+)", outp)
                if m_brm:
                    out["bios_version"] = m_brm.group(1).strip()
                    out["bios_manufacturer"] = "Apple"
        else:
            if which_binary("dmidecode"):
                rc, outp, _ = run(["dmidecode", "-t", "0"])
                if rc == 0 and outp:
                    m_man = re.search(r"Vendor:\s*(.+)", outp)
                    m_ver = re.search(r"Version:\s*(.+)", outp)
                    if m_man:
                        out["bios_manufacturer"] = m_man.group(1).strip()
                    if m_ver:
                        out["bios_version"] = m_ver.group(1).strip()
    except Exception:
        pass
    return out

def windows_only_checks() -> Dict[str, Any]:
    out = {}
    try:
        if platform.system() != "Windows":
            return out
        # Power plan
        rc, o, _ = run(["powercfg", "/getactivescheme"])
        out["power_plan"] = o if rc == 0 else None
        # Secure Boot (PowerShell)
        if which_binary("powershell"):
            rc, o, _ = run(["powershell", "-Command", "try { Confirm-SecureBootUEFI } catch { $null }"])
            out["secure_boot"] = True if rc == 0 and str(o).strip().lower() == "true" else False if rc == 0 else None
            # Defender status
            rc, o, _ = run(["powershell", "-Command", "try { (Get-MpComputerStatus).AMServiceEnabled } catch { $null }"])
            out["defender_enabled"] = True if rc == 0 and str(o).strip().lower() == "true" else None
            # Hyper-V state best-effort
            rc, o, _ = run(["powershell", "-Command", "Get-Service -Name vmms -ErrorAction SilentlyContinue | Select -ExpandProperty Status"])
            out["hyperv_vmms_status"] = o.strip() if rc == 0 and o else None
    except Exception:
        pass
    return out

def vm_detection() -> Dict[str, Any]:
    out = {"is_vm": None, "hypervisor_flags": None}
    try:
        syst = platform.system()
        if syst == "Linux":
            if os.path.exists("/proc/cpuinfo"):
                with open("/proc/cpuinfo", "r", encoding="utf-8", errors="ignore") as f:
                    txt = f.read().lower()
                out["is_vm"] = any(x in txt for x in ("hypervisor", "virtualbox", "kvm", "vmware", "microsoft hyper-v"))
                out["hypervisor_flags"] = "hypervisor" in txt
        elif syst == "Windows":
            rc, outp, _ = run(["wmic", "computersystem", "get", "model,manufacturer"])
            if rc == 0 and outp:
                out["is_vm"] = any(k in outp.lower() for k in ("virtual", "vmware", "vbox", "hyper-v", "microsoft corporation"))
        elif syst == "Darwin":
            rc, outp, _ = run(["sysctl", "-n", "machdep.cpu.features"])
            if rc == 0 and outp:
                out["hypervisor_flags"] = "hypervisor" in outp.lower()
    except Exception:
        pass
    return out

def software_versions() -> Dict[str, Any]:
    out = {}
    cmds = {
        "java": (["java", "-version"], True),  # prints to stderr
        "node": (["node", "-v"], False),
        "python": (["python", "--version"], False),
        "gradle": (["gradle", "-v"], False),
        "kotlinc": (["kotlinc", "-version"], False),
        "yarn": (["yarn", "--version"], False)
    }
    for k, (cmd, stderr_to_out) in cmds.items():
        if which_binary(cmd[0]):
            try:
                if k == "java":
                    p = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
                    ver = p.stderr.splitlines()[0].strip() if p.stderr else p.stdout.splitlines()[0].strip()
                else:
                    rc, ver, _ = run(cmd)
                    if rc != 0:
                        ver = None
                out[k] = ver
            except Exception:
                out[k] = None
        else:
            out[k] = None
    # attempt distribution for Java
    try:
        if out.get("java"):
            if "OpenJDK" in out["java"] or "openjdk" in out["java"].lower():
                out["java_distribution"] = "OpenJDK"
            elif "Oracle" in out["java"]:
                out["java_distribution"] = "Oracle"
            else:
                out["java_distribution"] = None
    except Exception:
        out["java_distribution"] = None
    return out

def commit_hash() -> Optional[str]:
    if which_binary("git") and os.path.exists(".git"):
        rc, out, _ = run(["git", "rev-parse", "HEAD"])
        if rc == 0:
            return out.strip()
    return None

def timezone_info() -> Dict[str, Any]:
    try:
        tzname = datetime.datetime.now().astimezone().tzname()
        return {"timezone": tzname}
    except Exception:
        return {"timezone": None}

def uptime_info() -> Dict[str, Any]:
    try:
        if psutil:
            boot = psutil.boot_time()
            return {"boot_time": datetime.datetime.fromtimestamp(boot).isoformat(), "uptime_seconds": int(time.time() - boot)}
        else:
            if platform.system() == "Windows":
                rc, out, _ = run(["net", "stats", "srv"])
                return {"boot_time": None, "uptime_seconds": None}
            else:
                rc, out, _ = run(["uptime", "-s"])
                return {"boot_time": out if rc == 0 else None, "uptime_seconds": None}
    except Exception:
        return {"boot_time": None, "uptime_seconds": None}

def build_and_system() -> Dict[str, Any]:
    build = {}
    build["build_number"] = platform.version()
    build["current_user"] = getpass.getuser()
    build["timestamp_utc"] = datetime.datetime.utcnow().isoformat() + "Z"
    build["commit_hash"] = commit_hash()
    return build

def assemble_all() -> Dict[str, Any]:
    info: Dict[str, Any] = {}
    info.update(get_platform_basic())
    info["cpu"] = cpu_info()
    info["memory"] = memory_info()
    info["disks"] = disk_info()
    info["bios"] = bios_info()
    info["vm"] = vm_detection()
    if platform.system() == "Windows":
        info["windows_checks"] = windows_only_checks()
    info["software"] = software_versions()
    info["uptime"] = uptime_info()
    info["timezone"] = timezone_info()
    info.update(build_and_system())
    return info

def main():
    info = assemble_all()
    print(json.dumps(info, indent=2, ensure_ascii=False))

if __name__ == "__main__":
    main()