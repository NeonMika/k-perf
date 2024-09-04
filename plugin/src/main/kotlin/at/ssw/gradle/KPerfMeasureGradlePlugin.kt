package at.ssw.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.*


// KotlinCompilerPluginSupportPlugin inherits from Plugin<Project>
class KPerfMeasureGradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun apply(target: Project) {
        // Could be overridden
        super.apply(target)

        println("KPerfMeasureGradlePlugin - apply")

        target.tasks.register("kPerfMeasureInfo") {
            doLast {
                println("KPerfMeasureGradlePlugin - kPerfMeasureInfo")
            }
        }
    }

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        println("KPerfMeasureGradlePlugin - applyToCompilation (${kotlinCompilation.name})")

        return kotlinCompilation.target.project.provider {
            // internally the parameters defined in applyToCompilation are passed as
            // "-P plugin:<compilerPluginId>:<key>=<value>" on the command line
            // TODO: Extract options from build file
            // Something like:
            // k-perf-measure {
            //   enabled = true
            //   ...
            // }
            listOf(
                SubpluginOption("enabled", "true"),
                SubpluginOption("annotation", "at.ssw.Measure")
            )
        }
    }

    // must be the same as "override val pluginId" in compiler plugin
    // based on this id the command line parameters will be passed to the compiler
    // internally the parameters defined in applyToCompilation are passed as
    // "-P plugin:<compilerPluginId>:<key>=<value>" on the command line
    override fun getCompilerPluginId(): String {
        return "k-perf-measure-compiler-plugin"
    }

    // the name of the project that contains the compiler plugin
    // this will be looked up on maven
    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(groupId = "at.ssw", artifactId = "k-perf-measure", version = "0.0.1")
}
