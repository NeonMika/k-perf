package at.jku.ssw.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption


// KotlinCompilerPluginSupportPlugin inherits from Plugin<Project>, which is the base class for Gradle Plugins
class KPerfGradlePlugin : KotlinCompilerPluginSupportPlugin {
  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

  override fun apply(target: Project) {
    // Could be overridden
    super.apply(target)

    println("KPerfGradlePlugin - apply")

    target.tasks.register("KPerfInfo") {
      doLast {
        println("KPerfGradlePlugin - KPerfInfo")
      }
    }
  }

  override fun applyToCompilation(
    kotlinCompilation: KotlinCompilation<*>
  ): Provider<List<SubpluginOption>> {
    println("KPerfGradlePlugin - applyToCompilation (${kotlinCompilation.name})")

    return kotlinCompilation.target.project.provider {
      // internally the parameters defined in applyToCompilation are passed as
      // "-P plugin:<compilerPluginId>:<key>=<value>" on the command line
      // TODO: Extract options from build file
      // Something like:
      // k-perf {
      //   enabled = true
      //   ...
      // }
      listOf(
        SubpluginOption("enabled", "true"),
        SubpluginOption("annotation", "at.jku.ssw.Measure")
      )
    }
  }

  // must be the same as "override val pluginId" in compiler plugin
  // based on this id the command line parameters will be passed to the compiler
  // internally the parameters defined in applyToCompilation are passed as
  // "-P plugin:<compilerPluginId>:<key>=<value>" on the command line
  override fun getCompilerPluginId(): String {
    // besides the jar name and the Gradle plugin (see described in getPluginArtifact) this is the third identifier relevant to compiler plugin development
    return "k-perf-compiler-plugin"
  }

  // the name of the project that contains the compiler plugin
  // this will be looked up on maven
  override fun getPluginArtifact(): SubpluginArtifact {
    // Why different names at all (k-perf-compiler-plugin VS at.jku.ssw.k-perf VS at.jku.ssw.k-perf-plugin)?

    // 1. k-perf-compiler-plugin:
    // - Defined in gradle plugin, function "getCompilerPluginId"
    // - Defined in Kotlin compiler plugin command line processor, property "pluginId"
    // - Name of the compiler plugin, used internally to pass parameters to the plugin via command line and to build a "bridge" between the gradle plugin and the Kotlin compiler plugin

    // 2. at.jku.ssw.k-perf-plugin
    //   - Defined in the gradlePlugin section of the Gradle build script
    //   - Defined in settings.gradle.kts
    //   - Defines the name of the Gradle plugin
    //   - We use this name later in other projects to use our plugin: plugins { id("at.jku.ssw.k-perf-plugin") }

    // 3. at.jku.ssw.k-perf
    // - Defined here
    // - Basically is the .jar file that contains the compiler plugin
    return SubpluginArtifact(groupId = "at.jku.ssw", artifactId = "k-perf", version = "0.0.3")
  }
}