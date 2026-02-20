package at.jku.ssw.shared

enum class IoaKind {
  None,

  TryFinally,

  IncrementIntCounter,
  IncrementIntCounterAndPrint,
  IncrementAtomicIntCounter,

  RandomValue,

  StandardOut,
  AppendToStringBuilder,
}