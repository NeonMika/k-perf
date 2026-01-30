package at.jku.ssw.compilerplugin

import at.jku.ssw.shared.InstrumentationOverheadAnalyzerKind
import org.jetbrains.kotlin.config.CompilerConfigurationKey

object InstrumentationOverheadAnalyzerConfigurationKeys {
  val ENABLED: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("enabled")
  val KIND: CompilerConfigurationKey<InstrumentationOverheadAnalyzerKind> = CompilerConfigurationKey.create("kind")
}