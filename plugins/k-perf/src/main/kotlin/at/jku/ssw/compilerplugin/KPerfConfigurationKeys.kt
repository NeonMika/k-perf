package at.jku.ssw.compilerplugin

import org.jetbrains.kotlin.config.CompilerConfigurationKey

object KPerfConfigurationKeys {
  val ENABLED: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("enabled")
  val TEST_KIR: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("testKIR")
}