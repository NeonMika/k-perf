package com.infendro.otel.plugin

import com.infendro.otel.plugin.ConfigurationKeys.KEY_DEBUG
import com.infendro.otel.plugin.ConfigurationKeys.KEY_ENABLED
import com.infendro.otel.plugin.ConfigurationKeys.KEY_HOST
import com.infendro.otel.plugin.ConfigurationKeys.KEY_SERVICE
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class Registrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val enabled = configuration[KEY_ENABLED] ?: true
        val debug = configuration[KEY_DEBUG] ?: false
        val host = configuration[KEY_HOST]
        val service = configuration[KEY_SERVICE]

        if (!enabled) return

        val extension = IrExtension(debug, host, service)
        IrGenerationExtension.registerExtension(extension)
    }
}
