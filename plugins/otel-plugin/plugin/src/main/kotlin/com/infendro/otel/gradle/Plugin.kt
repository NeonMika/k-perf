package com.infendro.otel.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class Extension() {
    var enabled: Boolean = true
    var debug: Boolean = false
    var host: String? = null
    var service: String? = null
}

class Plugin : KotlinCompilerPluginSupportPlugin {
    override fun isApplicable(
        kotlinCompilation: KotlinCompilation<*>
    ): Boolean = true

    override fun apply(
        target: Project
    ) {
        target.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            target.extensions
                .getByType<KotlinMultiplatformExtension>()
                .addDependencies()
        }
        target.extensions.add("otel", Extension())
        super.apply(target)
    }

    fun KotlinMultiplatformExtension.addDependencies() {
        sourceSets.getByName("commonMain").dependencies {
            implementation("io.opentelemetry.kotlin.api:all:1.0.570")
            implementation("io.opentelemetry.kotlin.sdk:sdk-trace:1.0.570")
            implementation("com.infendro.otel:otlp-exporter:1.0.0")
            implementation("com.infendro.otel:util:1.0.0")
        }
    }

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        return project.provider {
            val extension = project.extensions.findByName("otel") as Extension
            buildList {
                add(SubpluginOption("enabled", extension.enabled.toString()))
                add(SubpluginOption("debug", extension.debug.toString()))
                if (extension.host != null) add(SubpluginOption("host", extension.host!!))
                if (extension.service != null) add(SubpluginOption("service", extension.service!!))
            }
        }
    }

    override fun getCompilerPluginId(): String = "otel-plugin"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "com.infendro.otel",
        artifactId = "plugin",
        version = "1.0.0"
    )
}
