package at.jku.ssw.shared

enum class IoaKind {
  None,

  IncrementIntCounter,
  IncrementIntCounterAndPrint,

  RandomValue,

  StandardOut,
  AppendToStringBuilder,
}