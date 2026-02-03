# Common utility functions for benchmarking scripts

function Get-BenchmarkStatistics {
  param(
    [double[]]$Values
  )

  $count = $Values.Count
  if ($count -eq 0) {
    return [ordered]@{ count = 0; mean = $null; median = $null; stddev = $null; min = $null; max = $null; ci95 = $null }
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
    
    # --- FIX STARTS HERE ---
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
    # --- FIX ENDS HERE ---

    $ci95 = [ordered]@{
      lower = $mean - $ciHalfWidth
      upper = $mean + $ciHalfWidth
    }
  }
  else {
    $stddev = 0.0
    $ci95 = [ordered]@{
      lower = $mean
      upper = $mean
    }
  }

  return [ordered]@{
    count  = $count
    mean   = $mean
    median = $median
    stddev = $stddev
    min    = $min
    max    = $max
    ci95   = $ci95
  }
}

function Find-FirstGradleTask {
  param(
    [string[]]$TaskList,
    [string[]]$Candidates
  )

  foreach ($candidate in $Candidates) {
    $pattern = "(?m)^(?:\s*:)?$candidate\b"
    if ($TaskList -match $pattern) {
      return $candidate
    }
  }

  return $null
}

function Invoke-GradleTaskIfPresent {
  param(
    [string]$TaskName,
    [string]$Title
  )

  if ([string]::IsNullOrWhiteSpace($TaskName)) {
    Write-Host "Skipping $Title (task not found)"
    return
  }

  Write-Host ""
  Write-Host "=========================================="
  Write-Host $Title
  Write-Host "Task: $TaskName"
  Write-Host "=========================================="

  & .\gradlew $TaskName
  if ($LASTEXITCODE -ne 0) {
    throw "$Title failed with exit code $LASTEXITCODE"
  }

  Write-Host "$Title completed successfully."
}


function Invoke-KmpBuild {
  param(
    [string]$Title,
    [string]$Path,
    [bool]$CleanBuild = $true
  )

  Write-Host ""
  Write-Host "=========================================="
  Write-Host "$Title (Kotlin Multiplatform)"
  Write-Host "Path: $Path"
  Write-Host "=========================================="

  Push-Location $Path
  try {
    if ($CleanBuild) {
      & .\gradlew clean
      if ($LASTEXITCODE -ne 0) {
        throw "$Title clean failed with exit code $LASTEXITCODE"
      }
    }

    $taskList = & .\gradlew -q tasks --all
    if ($LASTEXITCODE -ne 0) {
      throw "$Title task discovery failed with exit code $LASTEXITCODE"
    }

    $jvmTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("jvmJar", "compileKotlinJvm")
    $jsTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("jsProductionExecutableCompileSync", "jsProductionExecutableCompile", "jsNodeProductionExecutableCompileSync", "jsNodeProductionExecutableCompile", "jsBrowserProductionWebpack", "compileKotlinJs")
    $windowsTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableMingwX64", "linkDebugExecutableMingwX64")
    $linuxTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableLinuxX64", "linkDebugExecutableLinuxX64")
    $macTask = Find-FirstGradleTask -TaskList $taskList -Candidates @("linkReleaseExecutableMacosX64", "linkDebugExecutableMacosX64", "linkReleaseExecutableMacosArm64", "linkDebugExecutableMacosArm64")
  
    Invoke-GradleTaskIfPresent -TaskName $jvmTask -Title "$Title - JVM build"
    Invoke-GradleTaskIfPresent -TaskName $jsTask -Title "$Title - JS build"
    Invoke-GradleTaskIfPresent -TaskName $windowsTask -Title "$Title - Windows build"
    Invoke-GradleTaskIfPresent -TaskName $linuxTask -Title "$Title - Linux build"
    Invoke-GradleTaskIfPresent -TaskName $macTask -Title "$Title - Mac build"
  }
  finally {
    Pop-Location
  }

  Write-Host "$Title completed successfully."
}

function Merge-Hashtable {
  param(
    [hashtable]$Target,
    [hashtable]$Source
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
    if ($firstModule.Speed) {
      $machineInfo.RAMSpeedMHz = $firstModule.Speed
    }
    if ($firstModule.Manufacturer) {
      $machineInfo.RAMManufacturer = $firstModule.Manufacturer.Trim()
    }
    if ($firstModule.PartNumber) {
      $machineInfo.RAMPartNumber = $firstModule.PartNumber.Trim()
    }
    # Get capacity of each module
    $moduleCapacities = $ramModules | ForEach-Object { [math]::Round($_.Capacity / 1GB, 2) }
    $machineInfo.RAMModuleCapacitiesGB = ($moduleCapacities -join ' + ')
  }

  # Disk
  $disk = Get-CimInstance Win32_DiskDrive | Where-Object { $_.DeviceID -eq "\\.\PHYSICALDRIVE0" } | Select-Object -First 1
  if ($disk) {
    $machineInfo.DiskModel = $disk.Model.Trim()
    $machineInfo.DiskSizeGB = [math]::Round($disk.Size / 1GB, 2)
    $machineInfo.DiskMediaType = $disk.MediaType
    if ($disk.InterfaceType) {
      $machineInfo.DiskInterfaceType = $disk.InterfaceType
    }
  }

  # Available Disk Space
  try {
    $systemDrive = Get-CimInstance Win32_LogicalDisk | Where-Object { $_.DeviceID -eq "C:" }
    if ($systemDrive) {
      $machineInfo.SystemDriveFreeSpaceGB = [math]::Round($systemDrive.FreeSpace / 1GB, 2)
    }
  }
  catch {
    $machineInfo.SystemDriveFreeSpaceGB = "Not available"
  }

  # Available Free RAM
  try {
    $availableRAMGB = [math]::Round($os.FreePhysicalMemory / 1MB, 2)
    $machineInfo.AvailableRAMGB = $availableRAMGB
  }
  catch {
    $machineInfo.AvailableRAMGB = "Not available"
  }

  # Power Plan
  try {
    $activePowerPlan = Get-CimInstance -Namespace root\cimv2\power -ClassName Win32_PowerPlan -ErrorAction SilentlyContinue | Where-Object { $_.IsActive -eq $true }
    if ($activePowerPlan) {
      $machineInfo.PowerPlan = $activePowerPlan.ElementName
    }
    else {
      $machineInfo.PowerPlan = "Not available"
    }
  }
  catch {
    $machineInfo.PowerPlan = "Not available"
  }

  # System Uptime
  try {
    $uptime = (Get-Date) - $os.LastBootUpTime
    $machineInfo.SystemUptimeHours = [math]::Round($uptime.TotalHours, 2)
  }
  catch {
    $machineInfo.SystemUptimeHours = "Not available"
  }

  # Windows Build Number
  $machineInfo.WindowsBuildNumber = $os.BuildNumber

  # Time Zone
  $machineInfo.TimeZone = (Get-TimeZone).Id

  # Current User
  $machineInfo.Username = $env:USERNAME

  # BIOS/UEFI Information
  try {
    $bios = Get-CimInstance Win32_BIOS
    if ($bios) {
      $machineInfo.BIOSVersion = $bios.SMBIOSBIOSVersion
      $machineInfo.BIOSManufacturer = $bios.Manufacturer
    }
  }
  catch {
    $machineInfo.BIOSVersion = "Not available"
  }

  # Virtualization Status
  try {
    $isVM = $false
    $computerModel = $computerSystem.Model
    if ($computerModel -match "Virtual|VMware|VirtualBox|Hyper-V|KVM|QEMU") {
      $isVM = $true
    }
    $machineInfo.IsVirtualMachine = $isVM
  }
  catch {
    $machineInfo.IsVirtualMachine = "Unknown"
  }

  # Hyper-V Status
  try {
    $hyperV = Get-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V -ErrorAction SilentlyContinue
    if ($hyperV) {
      $machineInfo.HyperVEnabled = ($hyperV.State -eq "Enabled")
    }
  }
  catch {
    $machineInfo.HyperVEnabled = "Unknown"
  }

  # Secure Boot Status
  try {
    $secureBoot = Confirm-SecureBootUEFI -ErrorAction SilentlyContinue
    $machineInfo.SecureBootEnabled = $secureBoot
  }
  catch {
    $machineInfo.SecureBootEnabled = "Unknown"
  }

  # Running Process Count
  try {
    $processCount = (Get-Process).Count
    $machineInfo.RunningProcessCount = $processCount
  }
  catch {
    $machineInfo.RunningProcessCount = "Not available"
  }

  # Windows Defender Status
  try {
    $defender = Get-MpComputerStatus -ErrorAction SilentlyContinue
    if ($defender) {
      $machineInfo.WindowsDefenderEnabled = $defender.AntivirusEnabled
      $machineInfo.WindowsDefenderRealTimeProtection = $defender.RealTimeProtectionEnabled
    }
  }
  catch {
    $machineInfo.WindowsDefenderEnabled = "Unknown"
  }

  # PowerShell Version
  $machineInfo.PowerShellVersion = "$($PSVersionTable.PSVersion.Major).$($PSVersionTable.PSVersion.Minor).$($PSVersionTable.PSVersion.Patch)"

  # Java Version and Distribution
  try {
    $javaVersionOutput = java -version 2>&1
    $javaVersionLine = $javaVersionOutput | Select-Object -First 1
    if ($javaVersionLine -match '"(.+?)"') {
      $machineInfo.JavaVersion = $Matches[1]
    }
    # Try to detect JDK distribution
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
  try {
    $nodeVersion = node --version 2>&1
    $machineInfo.NodeVersion = $nodeVersion.ToString().Trim()
  }
  catch {
    $machineInfo.NodeVersion = "Not available"
  }

  # Python Version
  try {
    $pythonVersionOutput = python --version 2>&1
    $machineInfo.PythonVersion = $pythonVersionOutput.ToString().Trim()
  }
  catch {
    $machineInfo.PythonVersion = "Not available"
  }

  # Gradle Version (from one of the projects)
  try {
    $gradleVersionOutput = & "$GradleProjectPath\gradlew.bat" --version 2>&1 | Select-String "Gradle "
    if ($gradleVersionOutput) {
      $machineInfo.GradleVersion = $gradleVersionOutput.ToString().Trim()
    }
  }
  catch {
    $machineInfo.GradleVersion = "Not available"
  }

  # Kotlin Version
  try {
    $kotlinVersionOutput = & "$GradleProjectPath\gradlew.bat" --version 2>&1 | Select-String "Kotlin:"
    if ($kotlinVersionOutput) {
      $machineInfo.KotlinVersion = $kotlinVersionOutput.ToString().Trim()
    }
  }
  catch {
    $machineInfo.KotlinVersion = "Not available"
  }

  # Git Commit Hash
  try {
    $gitCommit = git rev-parse HEAD 2>&1
    if ($LASTEXITCODE -eq 0) {
      $machineInfo.GitCommitHash = $gitCommit.ToString().Trim()
      $gitBranch = git rev-parse --abbrev-ref HEAD 2>&1
      if ($LASTEXITCODE -eq 0) {
        $machineInfo.GitBranch = $gitBranch.ToString().Trim()
      }
    }
  }
  catch {
    $machineInfo.GitCommitHash = "Not available"
  }

  # Timestamp
  $machineInfo.CollectionTimestamp = (Get-Date -Format "yyyy-MM-dd HH:mm:ss")

  return $machineInfo
}
