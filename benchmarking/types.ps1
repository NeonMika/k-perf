# Shared type definitions for benchmarking scripts.
# All enums, classes, and type-helper functions are centralised here
# so that build.ps1, run.ps1 and their consumers can rely on a single
# source of truth for the domain model.

# ---------------------------------------------------------------------------
# Enums
# ---------------------------------------------------------------------------

enum ExecutableType {
  Jar
  Node
  Exe
}

enum GameType {
  CommonMain
  DedicatedMain
}

# ---------------------------------------------------------------------------
# Value-object classes
# ---------------------------------------------------------------------------

class KPerfConfig {
  [bool]$Enabled                      # When false the plugin is applied but does not instrument
  [bool]$FlushEarly
  [bool]$InstrumentPropertyAccessors
  [bool]$TestKIR
  [string]$Methods

  KPerfConfig([bool]$Enabled, [bool]$FlushEarly, [bool]$InstrumentPropertyAccessors, [bool]$TestKIR, [string]$Methods) {
    $this.Enabled                     = $Enabled
    $this.FlushEarly                  = $FlushEarly
    $this.InstrumentPropertyAccessors = $InstrumentPropertyAccessors
    $this.TestKIR                     = $TestKIR
    $this.Methods                     = $Methods
  }
}

class BenchmarkExecutable {
  [string]$Name
  [string]$Path
  [ExecutableType]$Type
  [KPerfConfig]$Config   # $null for reference (uninstrumented) executables

  BenchmarkExecutable([string]$Name, [string]$Path, [ExecutableType]$Type, [KPerfConfig]$Config) {
    $this.Name   = $Name
    $this.Path   = $Path
    $this.Type   = $Type
    $this.Config = $Config
  }
}

# Represents a 95 % confidence interval.
# Property names are intentionally lowercase so that ConvertTo-Json produces
# the same "lower"/"upper" keys as the previous ordered-hashtable output.
class ConfidenceInterval95 {
  [double]$lower
  [double]$upper

  ConfidenceInterval95([double]$lower, [double]$upper) {
    $this.lower = $lower
    $this.upper = $upper
  }
}

# Holds the descriptive statistics computed over a set of benchmark samples.
# Numeric fields are typed [object] so that the empty-result case (count = 0)
# can return $null without PowerShell silently coercing it to 0.0.
# Property names are intentionally lowercase for JSON-format compatibility.
class BenchmarkStatistics {
  [int]$count
  [object]$mean       # [double] or $null when count = 0
  [object]$median     # [double] or $null when count = 0
  [object]$stddev     # [double] or $null when count = 0
  [object]$min        # [double] or $null when count = 0
  [object]$max        # [double] or $null when count = 0
  [object]$ci95       # [ConfidenceInterval95] or $null when count = 0

  # Constructor for the empty (no-data) case
  BenchmarkStatistics([int]$count) {
    $this.count  = $count
    $this.mean   = $null
    $this.median = $null
    $this.stddev = $null
    $this.min    = $null
    $this.max    = $null
    $this.ci95   = $null
  }

  # Constructor for the populated case
  BenchmarkStatistics(
    [int]$count,
    [double]$mean,
    [double]$median,
    [double]$stddev,
    [double]$min,
    [double]$max,
    [ConfidenceInterval95]$ci95
  ) {
    $this.count  = $count
    $this.mean   = $mean
    $this.median = $median
    $this.stddev = $stddev
    $this.min    = $min
    $this.max    = $max
    $this.ci95   = $ci95
  }
}

# ---------------------------------------------------------------------------
# GameType helpers
# ---------------------------------------------------------------------------

# Returns the artifact-name fragment for a GameType (e.g. "commonmain").
# This replaces the former $gameTypeStringMap hashtable.
function Get-GameTypeString {
  param([GameType]$GameType)
  switch ($GameType) {
    ([GameType]::CommonMain)    { return "commonmain" }
    ([GameType]::DedicatedMain) { return "dedicatedmain" }
  }
}
