package at.jku.ssw.kperfotel.proto.timesource.plugin

import at.jku.ssw.kperfotel.proto.timesource.plugin.ConfigurationKeys.KEY_DEBUG
import at.jku.ssw.kperfotel.proto.timesource.plugin.ConfigurationKeys.KEY_ENABLED
import at.jku.ssw.kperfotel.proto.timesource.plugin.ConfigurationKeys.KEY_HOST
import at.jku.ssw.kperfotel.proto.timesource.plugin.ConfigurationKeys.KEY_MAX_EXPORT_BATCH_SIZE
import at.jku.ssw.kperfotel.proto.timesource.plugin.ConfigurationKeys.KEY_MAX_QUEUE_SIZE
import at.jku.ssw.kperfotel.proto.timesource.plugin.ConfigurationKeys.KEY_SERVICE
import at.jku.ssw.kperfotel.proto.timesource.plugin.ConfigurationKeys.KEY_INSTRUMENT_PROPERTY_ACCESSORS
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class Registrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override val pluginId: String = "k-perf-otel-proto-timesource-compiler-plugin"

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val enabled = configuration[KEY_ENABLED] ?: true
        val debug = configuration[KEY_DEBUG] ?: false
        val host = configuration[KEY_HOST]
        val service = configuration[KEY_SERVICE]
        val maxQueueSize = configuration[KEY_MAX_QUEUE_SIZE] ?: 2048
        val maxExportBatchSize = configuration[KEY_MAX_EXPORT_BATCH_SIZE] ?: 512
        val instrumentPropertyAccessors = configuration[KEY_INSTRUMENT_PROPERTY_ACCESSORS] ?: false

        if (!enabled) return

        val extension = IrExtension(
            debug = debug,
            host = host,
            service = service,
            maxQueueSize = maxQueueSize,
            maxExportBatchSize = maxExportBatchSize,
            instrumentPropertyAccessors = instrumentPropertyAccessors,
        )
        IrGenerationExtension.registerExtension(extension)
    }
}
