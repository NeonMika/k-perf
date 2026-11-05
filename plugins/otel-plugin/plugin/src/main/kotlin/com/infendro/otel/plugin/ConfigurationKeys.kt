package com.infendro.otel.plugin

import org.jetbrains.kotlin.config.CompilerConfigurationKey

object ConfigurationKeys {
    val KEY_ENABLED: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("enabled")
    val KEY_DEBUG: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("debug")
    val KEY_HOST: CompilerConfigurationKey<String> = CompilerConfigurationKey.create("host")
    val KEY_SERVICE: CompilerConfigurationKey<String> = CompilerConfigurationKey.create("service")
    val KEY_INSTRUMENT_PROPERTY_ACCESSORS: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("instrumentPropertyAccessors")
}
