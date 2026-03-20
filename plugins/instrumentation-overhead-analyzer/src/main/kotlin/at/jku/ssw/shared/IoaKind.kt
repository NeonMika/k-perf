package at.jku.ssw.shared

enum class IoaKind {
  None,

  TryFinally,

  TimeClock,
  TimeMonotonicFunction,
  TimeMonotonicFunctionInWholeMilliseconds,
  TimeMonotonicFunctionInWholeMicroseconds,
  TimeMonotonicFunctionInWholeNanoseconds,
  TimeMonotonicGlobal,
  TimeMonotonicGlobalInWholeMilliseconds,
  TimeMonotonicGlobalInWholeMicroseconds,
  TimeMonotonicGlobalInWholeNanoseconds,

  IncrementIntCounter,
  IncrementAtomicIntCounter,

  RandomValue,

  StandardOut,
  AppendToStringBuilder,

  FileEagerFlush,
  FileLazyFlush,

  AddToList,
  AddDuplicatesToSet,
  AddUniqueToSet,
}