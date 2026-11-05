package at.jku.ssw.kperfotel.plugin

import at.jku.ssw.kperfotel.plugin.ConfigurationKeys.KEY_DEBUG
import at.jku.ssw.kperfotel.plugin.ConfigurationKeys.KEY_ENABLED
import at.jku.ssw.kperfotel.plugin.ConfigurationKeys.KEY_HOST
import at.jku.ssw.kperfotel.plugin.ConfigurationKeys.KEY_SERVICE
import at.jku.ssw.kperfotel.plugin.ConfigurationKeys.KEY_INSTRUMENT_PROPERTY_ACCESSORS
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class Processor : CommandLineProcessor {
    override val pluginId: String = "k-perf-otel-compiler-plugin"

    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            "enabled",
            "<true|false>",
            "whether the plugin is enabled",
            required = false
        ),
        CliOption(
            "debug",
            "<true|false>",
            "whether the debug output is enabled",
            required = false
        ),
        CliOption(
            "host",
            "<string>",
            "the host used when exporting",
            required = false
        ),
        CliOption(
            "service",
            "<string>",
            "the service name used when exporting",
            required = false
        ),
        CliOption(
            "instrumentPropertyAccessors",
            "<true|false>",
            "also instrument property getters/setters (default false: getters/setters skipped to match k-perf semantics)",
            required = false
        )
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        when (option.optionName) {
            "enabled" -> configuration.put(KEY_ENABLED, value.toBoolean())
            "debug" -> configuration.put(KEY_DEBUG, value.toBoolean())
            "host" -> configuration.put(KEY_HOST, value)
            "service" -> configuration.put(KEY_SERVICE, value)
            "instrumentPropertyAccessors" -> configuration.put(KEY_INSTRUMENT_PROPERTY_ACCESSORS, value.toBoolean())
            else -> throw CliOptionProcessingException("unknown option: ${option.optionName}")
        }
    }
}
