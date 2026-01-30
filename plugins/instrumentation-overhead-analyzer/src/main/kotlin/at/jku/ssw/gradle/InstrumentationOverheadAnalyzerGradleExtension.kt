package at.jku.ssw.gradle

import at.jku.ssw.shared.InstrumentationOverheadAnalyzerKind

class InstrumentationOverheadAnalyzerGradleExtension() {
  var enabled: Boolean = true
  var kind: InstrumentationOverheadAnalyzerKind = InstrumentationOverheadAnalyzerKind.StringBuilderAppend
}