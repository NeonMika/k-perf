package at.jku.ssw.shared

enum class IoaKind {
  None,

  IncrementIntCounter,
  IncrementIntCounterAndPrint,

  StandardOut,
  AppendToStringBuilder,
}