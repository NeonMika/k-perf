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
        // besides the jar name and the Gradle plugin (see described in getPluginArtifact) this is the third identifier relevant to compiler plugin development
        return "k-perf-measure-compiler-plugin"
    }

    // the name of the project that contains the compiler plugin
    // this will be looked up on maven
    override fun getPluginArtifact(): SubpluginArtifact =
        // This is defined in settings.gradle.kts, see:
        // rootProject.name = "k-perf-measure"

        // Why different names at all (at.ssw.k-perf-measure VS at.ssw.k-perf-measure-plugin)?
        // at.ssw.k-perf-measure (which is defined here) basically is the .jar file that contains the compiler plugin
        // at.ssw.k-perf-measure-plugin (defined in the gradlePlugin section of the Gradle build script) defines the name of the Gradle plugin
        SubpluginArtifact(groupId = "at.ssw", artifactId = "k-perf-measure", version = "0.0.2")
}
