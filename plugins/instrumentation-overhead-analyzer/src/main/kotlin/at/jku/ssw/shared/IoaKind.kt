package at.jku.ssw.shared

enum class IoaKind {
  None,

  TryFinally,

  IncrementIntCounter,
  IncrementAtomicIntCounter,

  RandomValue,

  StandardOut,
  AppendToStringBuilder,
}