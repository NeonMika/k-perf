# Statistics, machine info, and CSV/JSON export utilities for benchmarking scripts

function Get-BenchmarkStatistics {
  param(
    [double[]]$Values
  )

  $count = $Values.Count
  if ($count -eq 0) {
    return [BenchmarkStatistics]::new(0)
  }

  $sorted = $Values | Sort-Object
  $sum = ($Values | Measure-Object -Sum).Sum
  $mean = $sum / $count

  # Median calculation
  if ($count % 2 -eq 1) {
    $median = $sorted[([int]($count / 2))]
  }
  else {
    $mid = $count / 2
    $median = ($sorted[$mid - 1] + $sorted[$mid]) / 2
  }

  $min = $sorted[0]
  $max = $sorted[$count - 1]

  if ($count -gt 1) {
    $varianceSum = 0.0
    foreach ($v in $Values) {
      $varianceSum += [math]::Pow(($v - $mean), 2)
    }
    $variance = $varianceSum / ($count - 1)
    $stddev = [math]::Sqrt($variance)
    $stderr = $stddev / [math]::Sqrt($count)

    # T-Distribution Critical Values (Two-tailed, alpha=0.05)
    # Lookup table for degrees of freedom (df = count - 1)
    $tValues = @{
      1 = 12.71; 2 = 4.30; 3 = 3.18; 4 = 2.78; 5 = 2.57;
      6 = 2.45; 7 = 2.36; 8 = 2.31; 9 = 2.26; 10 = 2.23;
      11 = 2.20; 12 = 2.18; 13 = 2.16; 14 = 2.14; 15 = 2.13;
      16 = 2.12; 17 = 2.11; 18 = 2.10; 19 = 2.09; 20 = 2.09;
      21 = 2.08; 22 = 2.07; 23 = 2.07; 24 = 2.06; 25 = 2.06;
      26 = 2.06; 27 = 2.05; 28 = 2.05; 29 = 2.05
    }

    $df = $count - 1
    if ($tValues.Contains($df)) {
      $tScore = $tValues[$df]
    }
    else {
      # Fallback to Z-score (1.96) for N > 30, which is statistically acceptable
      $tScore = 1.96
    }

    $ciHalfWidth = $tScore * $stderr
    $ci95 = [ConfidenceInterval95]::new($mean - $ciHalfWidth, $mean + $ciHalfWidth)
  }
  else {
    $stddev = 0.0
    $ci95 = [ConfidenceInterval95]::new($mean, $mean)
  }

  return [BenchmarkStatistics]::new($count, $mean, $median, $stddev, $min, $max, $ci95)
}

function Merge-Hashtable {
  param(
    [System.Collections.IDictionary]$Target,
    [System.Collections.IDictionary]$Source
  )

  if ($null -eq $Target) {
    $Target = @{}
  }
  if ($null -eq $Source) {
    return $Target
  }

  foreach ($key in $Source.Keys) {
    $Target[$key] = $Source[$key]
  }

  return $Target
}

function Get-MachineInfo {
  param(
    [string]$GradleProjectPath = "..\..\kmp-examples\game-of-life-kmp-commonmain"
  )

  $machineInfo = [ordered]@{}

  # --- OS-specific hardware and system information ---

  if ($IsWindows) {
    # Computer/Device Information
    $computerSystem = Get-CimInstance Win32_ComputerSystem
    $machineInfo.DeviceManufacturer = $computerSystem.Manufacturer
    $machineInfo.DeviceModel = $computerSystem.Model

    # Operating System
    $os = Get-CimInstance Win32_OperatingSystem
    $machineInfo.OS = "$($os.Caption) $($os.Version)"
    $machineInfo.OSArchitecture = $os.OSArchitecture

    # CPU
    $cpu = Get-CimInstance Win32_Processor | Select-Object -First 1
    $machineInfo.CPU = $cpu.Name.Trim()
    $machineInfo.CPUCores = $cpu.NumberOfCores
    $machineInfo.CPULogicalProcessors = $cpu.NumberOfLogicalProcessors
    $machineInfo.CPUMaxClockSpeedMHz = $cpu.MaxClockSpeed

    # RAM
    $totalRAMGB = [math]::Round($computerSystem.TotalPhysicalMemory / 1GB, 2)
    $machineInfo.TotalRAMGB = $totalRAMGB

    # Detailed RAM information
    $ramModules = Get-CimInstance Win32_PhysicalMemory
    if ($ramModules) {
      $machineInfo.RAMModuleCount = $ramModules.Count
      $firstModule = $ramModules | Select-Object -First 1
      if ($firstModule.Speed) { $machineInfo.RAMSpeedMHz = $firstModule.Speed }
      if ($firstModule.Manufacturer) { $machineInfo.RAMManufacturer = $firstModule.Manufacturer.Trim() }
      if ($firstModule.PartNumber) { $machineInfo.RAMPartNumber = $firstModule.PartNumber.Trim() }
      $moduleCapacities = $ramModules | ForEach-Object { [math]::Round($_.Capacity / 1GB, 2) }
      $machineInfo.RAMModuleCapacitiesGB = ($moduleCapacities -join ' + ')
    }

    # Disk
    $disk = Get-CimInstance Win32_DiskDrive | Where-Object { $_.DeviceID -eq "\\.\PHYSICALDRIVE0" } | Select-Object -First 1
    if ($disk) {
      $machineInfo.DiskModel = $disk.Model.Trim()
      $machineInfo.DiskSizeGB = [math]::Round($disk.Size / 1GB, 2)
      $machineInfo.DiskMediaType = $disk.MediaType
      if ($disk.InterfaceType) { $machineInfo.DiskInterfaceType = $disk.InterfaceType }
    }

    # Available Disk Space
    try {
      $systemDrive = Get-CimInstance Win32_LogicalDisk | Where-Object { $_.DeviceID -eq "C:" }
      if ($systemDrive) { $machineInfo.SystemDriveFreeSpaceGB = [math]::Round($systemDrive.FreeSpace / 1GB, 2) }
    }
    catch { $machineInfo.SystemDriveFreeSpaceGB = "Not available" }

    # Available Free RAM
    try { $machineInfo.AvailableRAMGB = [math]::Round($os.FreePhysicalMemory / 1MB, 2) }
    catch { $machineInfo.AvailableRAMGB = "Not available" }

    # Power Plan
    try {
      $activePowerPlan = Get-CimInstance -Namespace root\cimv2\power -ClassName Win32_PowerPlan -ErrorAction SilentlyContinue | Where-Object { $_.IsActive -eq $true }
      $machineInfo.PowerPlan = if ($activePowerPlan) { $activePowerPlan.ElementName } else { "Not available" }
    }
    catch { $machineInfo.PowerPlan = "Not available" }

    # System Uptime
    try { $machineInfo.SystemUptimeHours = [math]::Round(((Get-Date) - $os.LastBootUpTime).TotalHours, 2) }
    catch { $machineInfo.SystemUptimeHours = "Not available" }

    # Windows Build Number
    $machineInfo.WindowsBuildNumber = $os.BuildNumber

    # Time Zone / User
    $machineInfo.TimeZone = (Get-TimeZone).Id
    $machineInfo.Username = $env:USERNAME

    # BIOS/UEFI
    try {
      $bios = Get-CimInstance Win32_BIOS
      if ($bios) {
        $machineInfo.BIOSVersion = $bios.SMBIOSBIOSVersion
        $machineInfo.BIOSManufacturer = $bios.Manufacturer
      }
    }
    catch { $machineInfo.BIOSVersion = "Not available" }

    # Virtualization
    try {
      $machineInfo.IsVirtualMachine = $computerSystem.Model -match "Virtual|VMware|VirtualBox|Hyper-V|KVM|QEMU"
    }
    catch { $machineInfo.IsVirtualMachine = "Unknown" }

    # Hyper-V
    try {
      $hyperV = Get-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V -ErrorAction SilentlyContinue
      if ($hyperV) { $machineInfo.HyperVEnabled = ($hyperV.State -eq "Enabled") }
    }
    catch { $machineInfo.HyperVEnabled = "Unknown" }

    # Secure Boot
    try { $machineInfo.SecureBootEnabled = Confirm-SecureBootUEFI -ErrorAction SilentlyContinue }
    catch { $machineInfo.SecureBootEnabled = "Unknown" }

    # Running Process Count
    try { $machineInfo.RunningProcessCount = (Get-Process).Count }
    catch { $machineInfo.RunningProcessCount = "Not available" }

    # Windows Defender
    try {
      $defender = Get-MpComputerStatus -ErrorAction SilentlyContinue
      if ($defender) {
        $machineInfo.WindowsDefenderEnabled = $defender.AntivirusEnabled
        $machineInfo.WindowsDefenderRealTimeProtection = $defender.RealTimeProtectionEnabled
      }
    }
    catch { $machineInfo.WindowsDefenderEnabled = "Unknown" }

  } elseif ($IsLinux) {
    # Device info (from DMI when accessible — requires root on some systems)
    try {
      if (Test-Path "/sys/class/dmi/id/sys_vendor") {
        $machineInfo.DeviceManufacturer = (Get-Content "/sys/class/dmi/id/sys_vendor" -Raw).Trim()
      }
      if (Test-Path "/sys/class/dmi/id/product_name") {
        $machineInfo.DeviceModel = (Get-Content "/sys/class/dmi/id/product_name" -Raw).Trim()
      }
    }
    catch { }

    # Operating System
    try {
      $osInfo = @{}
      if (Test-Path "/etc/os-release") {
        Get-Content "/etc/os-release" | Where-Object { $_ -match '=' } | ForEach-Object {
          $parts = $_ -split '=', 2
          $osInfo[$parts[0].Trim()] = $parts[1].Trim().Trim('"')
        }
      }
      $machineInfo.OS = if ($osInfo['PRETTY_NAME']) { $osInfo['PRETTY_NAME'] } else { (uname -a 2>&1).ToString().Trim() }
    }
    catch { $machineInfo.OS = (uname -a 2>&1).ToString().Trim() }
    $machineInfo.OSArchitecture = (uname -m 2>&1).ToString().Trim()

    # CPU
    try {
      if (Test-Path "/proc/cpuinfo") {
        $cpuInfo = Get-Content "/proc/cpuinfo"
        $cpuModelLine = $cpuInfo | Where-Object { $_ -match '^model name' } | Select-Object -First 1
        if ($cpuModelLine -match ':\s*(.+)$') { $machineInfo.CPU = $Matches[1].Trim() }
        $cpuCoresLine = $cpuInfo | Where-Object { $_ -match '^cpu cores' } | Select-Object -First 1
        if ($cpuCoresLine -match ':\s*(\d+)') { $machineInfo.CPUCores = [int]$Matches[1] }
      }
      $logicalProcs = (nproc 2>&1).ToString().Trim()
      if ($logicalProcs -match '^\d+$') { $machineInfo.CPULogicalProcessors = [int]$logicalProcs }
    }
    catch { }

    # RAM
    try {
      if (Test-Path "/proc/meminfo") {
        $memInfo = Get-Content "/proc/meminfo"
        $memTotalLine = $memInfo | Where-Object { $_ -match '^MemTotal:' } | Select-Object -First 1
        if ($memTotalLine -match ':\s*(\d+)\s*kB') {
          $machineInfo.TotalRAMGB = [math]::Round([long]$Matches[1] * 1KB / 1GB, 2)
        }
        $memAvailLine = $memInfo | Where-Object { $_ -match '^MemAvailable:' } | Select-Object -First 1
        if ($memAvailLine -match ':\s*(\d+)\s*kB') {
          $machineInfo.AvailableRAMGB = [math]::Round([long]$Matches[1] * 1KB / 1GB, 2)
        }
      }
    }
    catch { }

    # Disk
    try {
      $dfLine = (df -BG / 2>&1 | Select-Object -Skip 1 | Select-Object -First 1).ToString()
      if ($dfLine -match '\s+(\d+)G\s+\d+G\s+(\d+)G') {
        $machineInfo.DiskSizeGB = [int]$Matches[1]
        $machineInfo.SystemDriveFreeSpaceGB = [int]$Matches[2]
      }
    }
    catch { }

    # System Uptime
    try {
      $uptimeRaw = (Get-Content "/proc/uptime" -Raw).Trim().Split(' ')[0]
      $machineInfo.SystemUptimeHours = [math]::Round([double]$uptimeRaw / 3600.0, 2)
    }
    catch { $machineInfo.SystemUptimeHours = "Not available" }

    # Time Zone / User
    $machineInfo.TimeZone = try { (Get-TimeZone).Id } catch { "Not available" }
    $machineInfo.Username = if ($env:USER) { $env:USER } else { try { (whoami 2>&1).ToString().Trim() } catch { "Not available" } }

    # Running Process Count
    try { $machineInfo.RunningProcessCount = (Get-Process).Count }
    catch { $machineInfo.RunningProcessCount = "Not available" }

    # Virtualization
    try {
      $productName = if (Test-Path "/sys/class/dmi/id/product_name") {
        (Get-Content "/sys/class/dmi/id/product_name" -Raw).Trim()
      } else { "" }
      $machineInfo.IsVirtualMachine = [bool]($productName -match "Virtual|VMware|VirtualBox|KVM|QEMU|HVM")
    }
    catch { $machineInfo.IsVirtualMachine = "Unknown" }

  } elseif ($IsMacOS) {
    # Operating System
    try { $machineInfo.OS = "$(sw_vers -productName 2>&1) $(sw_vers -productVersion 2>&1)".Trim() }
    catch { $machineInfo.OS = (uname -a 2>&1).ToString().Trim() }
    $machineInfo.OSArchitecture = (uname -m 2>&1).ToString().Trim()

    # CPU
    try {
      $machineInfo.CPU = (sysctl -n machdep.cpu.brand_string 2>&1).ToString().Trim()
      $machineInfo.CPUCores = [int](sysctl -n hw.physicalcpu 2>&1).ToString().Trim()
      $machineInfo.CPULogicalProcessors = [int](sysctl -n hw.logicalcpu 2>&1).ToString().Trim()
    }
    catch { }

    # RAM
    try {
      $memBytes = [long](sysctl -n hw.memsize 2>&1).ToString().Trim()
      $machineInfo.TotalRAMGB = [math]::Round($memBytes / 1GB, 2)
    }
    catch { }

    # Disk
    try {
      $dfLine = (df -g / 2>&1 | Select-Object -Skip 1 | Select-Object -First 1).ToString()
      if ($dfLine -match '\s+(\d+)\s+\d+\s+(\d+)') {
        $machineInfo.DiskSizeGB = [int]$Matches[1]
        $machineInfo.SystemDriveFreeSpaceGB = [int]$Matches[2]
      }
    }
    catch { }

    # Time Zone / User
    $machineInfo.TimeZone = try { (Get-TimeZone).Id } catch { "Not available" }
    $machineInfo.Username = if ($env:USER) { $env:USER } else { try { (whoami 2>&1).ToString().Trim() } catch { "Not available" } }

    # Running Process Count
    try { $machineInfo.RunningProcessCount = (Get-Process).Count }
    catch { $machineInfo.RunningProcessCount = "Not available" }

    $machineInfo.IsVirtualMachine = $false
  }

  # --- Cross-platform runtime information ---

  # PowerShell Version
  $machineInfo.PowerShellVersion = "$($PSVersionTable.PSVersion.Major).$($PSVersionTable.PSVersion.Minor).$($PSVersionTable.PSVersion.Patch)"

  # Java Version and Distribution
  try {
    $javaVersionOutput = java -version 2>&1
    $javaVersionLine = $javaVersionOutput | Select-Object -First 1
    if ($javaVersionLine -match '"(.+?)"') { $machineInfo.JavaVersion = $Matches[1] }
    $javaDistLine = $javaVersionOutput | Select-Object -Skip 1 -First 2
    if ($javaDistLine -match "OpenJDK|Oracle|Adoptium|Temurin|Azul|Amazon|GraalVM") {
      $machineInfo.JavaDistribution = $Matches[0]
    }
  }
  catch {
    $machineInfo.JavaVersion = "Not available"
    $machineInfo.JavaDistribution = "Not available"
  }

  # Node.js Version
  try { $machineInfo.NodeVersion = (node --version 2>&1).ToString().Trim() }
  catch { $machineInfo.NodeVersion = "Not available" }

  # Python Version
  try {
    $pythonCmd = if (Get-Command python3 -ErrorAction SilentlyContinue) { "python3" } else { "python" }
    $machineInfo.PythonVersion = (& $pythonCmd --version 2>&1).ToString().Trim()
  }
  catch { $machineInfo.PythonVersion = "Not available" }

  # Gradle + Kotlin Version (via the project's gradlew wrapper)
  $gradlewName   = if ($IsWindows) { "gradlew.bat" } else { "gradlew" }
  $gradleWrapper = Join-Path $GradleProjectPath $gradlewName
  try {
    $gradleOut = & $gradleWrapper --version 2>&1
    $gradleVersionLine = $gradleOut | Select-String "Gradle "
    if ($gradleVersionLine) { $machineInfo.GradleVersion = $gradleVersionLine.ToString().Trim() }
    $kotlinVersionLine = $gradleOut | Select-String "Kotlin:"
    if ($kotlinVersionLine) { $machineInfo.KotlinVersion = $kotlinVersionLine.ToString().Trim() }
  }
  catch {
    $machineInfo.GradleVersion = "Not available"
    $machineInfo.KotlinVersion = "Not available"
  }

  # Git Commit Hash
  try {
    $gitCommit = git rev-parse HEAD 2>&1
    if ($LASTEXITCODE -eq 0) {
      $machineInfo.GitCommitHash = $gitCommit.ToString().Trim()
      $gitBranch = git rev-parse --abbrev-ref HEAD 2>&1
      if ($LASTEXITCODE -eq 0) { $machineInfo.GitBranch = $gitBranch.ToString().Trim() }
    }
  }
  catch { $machineInfo.GitCommitHash = "Not available" }

  # Timestamp
  $machineInfo.CollectionTimestamp = (Get-Date -Format "yyyy-MM-dd HH:mm:ss")

  return $machineInfo
}

function Export-BenchmarkResultsToCSV {
  param(
    [array]$Results,
    [string]$OutputPath
  )

  # Convert OrderedDictionaries to PSCustomObjects for proper CSV output
  $csvObjects = $Results | ForEach-Object { New-Object PSObject -Property $_ }
  $csvObjects | ConvertTo-Csv -NoTypeInformation | Out-File -FilePath $OutputPath -Encoding utf8

  Write-Host "CSV results exported to: $OutputPath"
}

function Export-BenchmarkResultsToJSON {
  param(
    [array]$Results,
    [string]$OutputPath
  )

  # Export array of results as JSON with proper formatting
  $Results | ConvertTo-Json -Depth 6 | Out-File -FilePath $OutputPath -Encoding utf8

  Write-Host "JSON results exported to: $OutputPath"
}

function Build-BenchmarkCSVRecord {
  param(
    [string]$ExecutableName,
    [BenchmarkStatistics]$Statistics,
    [object]$MachineInfo,
    [int]$RepetitionCount,
    [bool]$CleanBuild,
    [int]$StepCount,
    $BuildTime,
    [hashtable]$AdditionalParameters
  )

  $record = [ordered]@{
    mean            = $Statistics.mean
    median          = $Statistics.median
    stddev          = $Statistics.stddev
    min             = $Statistics.min
    max             = $Statistics.max
    ci95_lower      = $Statistics.ci95.lower
    ci95_upper      = $Statistics.ci95.upper
    executable      = $ExecutableName
    buildTimeMs     = $BuildTime
    RepetitionCount = $RepetitionCount
    CleanBuild      = $CleanBuild
    StepCount       = $StepCount
  }

  # Add additional test parameters
  foreach ($key in $AdditionalParameters.Keys) {
    $record[$key] = $AdditionalParameters[$key]
  }

  # Add machine information fields
  $machineInfoOrder = @(
    'CollectionTimestamp', 'GitCommitHash', 'GitBranch',
    'DeviceManufacturer', 'DeviceModel', 'IsVirtualMachine',
    'OS', 'OSArchitecture', 'WindowsBuildNumber', 'TimeZone', 'Username',
    'BIOSVersion', 'BIOSManufacturer',
    'SecureBootEnabled', 'HyperVEnabled',
    'CPU', 'CPUCores', 'CPULogicalProcessors', 'CPUMaxClockSpeedMHz',
    'TotalRAMGB', 'AvailableRAMGB', 'RAMModuleCount', 'RAMSpeedMHz', 'RAMManufacturer', 'RAMPartNumber', 'RAMModuleCapacitiesGB',
    'DiskModel', 'DiskSizeGB', 'DiskMediaType', 'DiskInterfaceType', 'SystemDriveFreeSpaceGB',
    'PowerPlan', 'SystemUptimeHours', 'RunningProcessCount',
    'WindowsDefenderEnabled', 'WindowsDefenderRealTimeProtection',
    'PowerShellVersion', 'JavaVersion', 'JavaDistribution', 'NodeVersion', 'PythonVersion', 'GradleVersion', 'KotlinVersion'
  )

  foreach ($key in $machineInfoOrder) {
    if ($MachineInfo.Contains($key)) {
      $record[$key] = $MachineInfo[$key]
    }
  }

  return $record
}
