package at.jku.ssw.compilerplugin

import at.jku.ssw.shared.IoaKind
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
that reads at.jku.ssw.compilerplugin.IoaCommandLineProcessor
 */
@OptIn(ExperimentalCompilerApi::class)
class IoaCommandLineProcessor : CommandLineProcessor {
  override val pluginId: String = "instrumentation-overhead-analyzer-plugin"

  override val pluginOptions: Collection<CliOption> = listOf(
    CliOption(
      "enabled",
      "<true|false>",
      "whether plugin is enabled",
      required = false,
      allowMultipleOccurrences = false
    ),
    CliOption(
      "kind",
      "<${IoaKind.entries.joinToString("|")}>",
      "the kind of instrumentation that should be analyzed",
      required = true,
      allowMultipleOccurrences = false
    )
  )

  override fun processOption(
    option: AbstractCliOption,
    value: String,
    configuration: CompilerConfiguration
  ) {
    when (option.optionName) {
      "enabled" -> configuration.put(IoaConfigurationKeys.ENABLED, value.toBoolean())
      "kind" -> configuration.put(IoaConfigurationKeys.KIND, IoaKind.valueOf(value))
      else -> throw CliOptionProcessingException("command line processor encountered unknown CLI compiler plugin option: ${option.optionName}")
    }
  }
}