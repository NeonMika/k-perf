package at.jku.ssw.shared

enum class IoaKind {
  None,

  IncrementIntCounter,
  IncrementIntCounterAndPrint,
  IncrementAtomicIntCounter,

  RandomValue,

  StandardOut,
  AppendToStringBuilder,
}