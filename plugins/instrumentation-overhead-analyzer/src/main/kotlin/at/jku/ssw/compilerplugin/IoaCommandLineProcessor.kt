package at.jku.ssw.compilerplugin

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.compiler.plugin.*
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
    ),
    CliOption(
      "instrumentPropertyAccessors",
      "<true|false>",
      "whether the plugin should instrument property accessors, i.e., getters and setters, or not (note: compilation backends such as JVM backend may decide to remove getter calls and directly access backing fields as performance optimization)",
      required = false,
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
      "instrumentPropertyAccessors" -> configuration.put(
        IoaConfigurationKeys.INSTRUMENT_PROPERTY_ACCESSORS,
        value.toBoolean()
      )

      else -> throw CliOptionProcessingException("command line processor encountered unknown CLI compiler plugin option: ${option.optionName}")
    }
  }
}