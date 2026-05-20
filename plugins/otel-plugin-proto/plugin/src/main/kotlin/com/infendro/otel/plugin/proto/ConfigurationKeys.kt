package com.infendro.otel.plugin.proto

import org.jetbrains.kotlin.config.CompilerConfigurationKey

object ConfigurationKeys {
    val KEY_ENABLED: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("enabled")
    val KEY_DEBUG: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("debug")
    val KEY_HOST: CompilerConfigurationKey<String> = CompilerConfigurationKey.create("host")
    val KEY_SERVICE: CompilerConfigurationKey<String> = CompilerConfigurationKey.create("service")
    val KEY_MAX_QUEUE_SIZE: CompilerConfigurationKey<Int> = CompilerConfigurationKey.create("maxQueueSize")
    val KEY_MAX_EXPORT_BATCH_SIZE: CompilerConfigurationKey<Int> = CompilerConfigurationKey.create("maxExportBatchSize")
}
