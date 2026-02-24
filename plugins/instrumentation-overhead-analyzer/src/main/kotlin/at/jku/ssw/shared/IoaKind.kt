package at.jku.ssw.shared

enum class IoaKind {
  None,

  TryFinally,

  TimeClock,
  TimeMonotonicFunction,
  TimeMonotonicGlobal,

  IncrementIntCounter,
  IncrementAtomicIntCounter,

  RandomValue,

  StandardOut,
  AppendToStringBuilder,
}