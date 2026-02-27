package at.jku.ssw.compilerplugin

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.config.CompilerConfigurationKey

object IoaConfigurationKeys {
  val ENABLED: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("enabled")
  val KIND: CompilerConfigurationKey<IoaKind> = CompilerConfigurationKey.create("kind")
  val INSTRUMENT_PROPERTY_ACCESSORS: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("instrument_property_accessors")
}