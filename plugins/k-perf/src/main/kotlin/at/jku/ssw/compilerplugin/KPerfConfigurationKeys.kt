package at.jku.ssw.compilerplugin

import org.jetbrains.kotlin.config.CompilerConfigurationKey

object KPerfConfigurationKeys {
  val ENABLED: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("enabled")
  val FLUSH_EARLY: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("flush_early")
  val TEST_KIR: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("testKIR")
}