package com.infendro.otel.plugin.proto.anchored

import com.infendro.otel.plugin.proto.anchored.ConfigurationKeys.KEY_DEBUG
import com.infendro.otel.plugin.proto.anchored.ConfigurationKeys.KEY_ENABLED
import com.infendro.otel.plugin.proto.anchored.ConfigurationKeys.KEY_HOST
import com.infendro.otel.plugin.proto.anchored.ConfigurationKeys.KEY_MAX_EXPORT_BATCH_SIZE
import com.infendro.otel.plugin.proto.anchored.ConfigurationKeys.KEY_MAX_QUEUE_SIZE
import com.infendro.otel.plugin.proto.anchored.ConfigurationKeys.KEY_SERVICE
import com.infendro.otel.plugin.proto.anchored.ConfigurationKeys.KEY_INSTRUMENT_PROPERTY_ACCESSORS
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class Registrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override val pluginId: String = "otel-plugin-proto-anchored"

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val enabled = configuration[KEY_ENABLED] ?: true
        val debug = configuration[KEY_DEBUG] ?: false
        val host = configuration[KEY_HOST]
        val service = configuration[KEY_SERVICE]
        val maxQueueSize = configuration[KEY_MAX_QUEUE_SIZE] ?: 2048
        val maxExportBatchSize = configuration[KEY_MAX_EXPORT_BATCH_SIZE] ?: Int.MAX_VALUE
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
