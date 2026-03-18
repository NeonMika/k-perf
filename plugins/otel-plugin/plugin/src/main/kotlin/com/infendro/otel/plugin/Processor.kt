package com.infendro.otel.plugin

import com.infendro.otel.plugin.ConfigurationKeys.KEY_DEBUG
import com.infendro.otel.plugin.ConfigurationKeys.KEY_ENABLED
import com.infendro.otel.plugin.ConfigurationKeys.KEY_HOST
import com.infendro.otel.plugin.ConfigurationKeys.KEY_SERVICE
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class Processor : CommandLineProcessor {
    override val pluginId: String = "otel-plugin"

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
            else -> throw CliOptionProcessingException("unknown option: ${option.optionName}")
        }
    }
}
