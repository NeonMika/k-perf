package at.jku.ssw.kperfotel.proto.anchored.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
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
    var maxQueueSize: Int = 2048
    var maxExportBatchSize: Int = 512
    var instrumentPropertyAccessors: Boolean = false
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
                .getByType(KotlinMultiplatformExtension::class.java)
                .addDependencies()
        }
        target.extensions.add("kperfOtelProtoAnchored", Extension())
        super.apply(target)
    }

    fun KotlinMultiplatformExtension.addDependencies() {
        sourceSets.getByName("commonMain").dependencies {
            implementation("io.opentelemetry.kotlin.api:all:1.0.570")
            implementation("io.opentelemetry.kotlin.sdk:sdk-trace:1.0.570")
            implementation("io.github.neonmika:k-perf-otlp-exporter-proto:0.2.1")
            implementation("io.github.neonmika:k-perf-otel-proto-utilities:0.2.1")
        }
    }

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        return project.provider {
            val extension = project.extensions.findByName("kperfOtelProtoAnchored") as Extension
            buildList {
                add(SubpluginOption("enabled", extension.enabled.toString()))
                add(SubpluginOption("debug", extension.debug.toString()))
                if (extension.host != null) add(SubpluginOption("host", extension.host!!))
                if (extension.service != null) add(SubpluginOption("service", extension.service!!))
                add(SubpluginOption("maxQueueSize", extension.maxQueueSize.toString()))
                add(SubpluginOption("maxExportBatchSize", extension.maxExportBatchSize.toString()))
                add(SubpluginOption("instrumentPropertyAccessors", extension.instrumentPropertyAccessors.toString()))
            }
        }
    }

    override fun getCompilerPluginId(): String = "k-perf-otel-proto-anchored-compiler-plugin"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "io.github.neonmika",
        artifactId = "k-perf-otel-proto-anchored",
        version = "0.2.1"
    )
}
