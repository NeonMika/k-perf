package at.jku.ssw.compilerplugin

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

/*
Commandline processor to process options.
This is the entry point for the compiler plugin.
It is found via a ServiceLoader.
Thus, we need an entry in META-INF/services/org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
that reads at.jku.ssw.compilerplugin.KPerfCommandLineProcessor
 */
@OptIn(ExperimentalCompilerApi::class)
class KPerfCommandLineProcessor : CommandLineProcessor {
  override val pluginId: String = "k-perf-compiler-plugin"

  override val pluginOptions: Collection<CliOption> = listOf(
    CliOption(
      "enabled",
      "<true|false>",
      "whether plugin is enabled",
      false,
      false
    ),
    CliOption(
      "flushEarly",
      "<true|false>",
      "whether the plugin should flush after every trace state (true) or only once at the end (false)",
      false,
      false
    ),
    CliOption(
      "testKIR",
      "<true|false>",
      "whether KIRHelperKit should be tested using KIRHelperKitTestingExtension",
      false,
      false
    )
  )

  override fun processOption(
    option: AbstractCliOption,
    value: String,
    configuration: CompilerConfiguration
  ) {
    println("KPerfCommandLineProcessor - processOption ($option, $value)")
    when (option.optionName) {
      "enabled" -> configuration.put(KPerfConfigurationKeys.ENABLED, value.toBoolean())
      "flushEarly" -> configuration.put(KPerfConfigurationKeys.FLUSH_EARLY, value.toBoolean())
      "testKIR" -> configuration.put(KPerfConfigurationKeys.TEST_KIR, value.toBoolean())
      else -> throw CliOptionProcessingException("KPerfCommandLineProcessor.processOption encountered unknown CLI compiler plugin option: ${option.optionName}")
    }
  }
}