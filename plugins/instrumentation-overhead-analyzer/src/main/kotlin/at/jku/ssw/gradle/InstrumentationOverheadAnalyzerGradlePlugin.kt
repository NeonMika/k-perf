package at.jku.ssw.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import kotlin.reflect.KClass


// KotlinCompilerPluginSupportPlugin inherits from Plugin<Project>, which is the base class for Gradle Plugins
class InstrumentationOverheadAnalyzerGradlePlugin : KotlinCompilerPluginSupportPlugin {
  lateinit var target : Project
  
  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

  override fun apply(target: Project) {
    this.target = target
    target.plugins.withId("org.jetbrains.kotlin.multiplatform") {
      target.extensions.getByType(KotlinMultiplatformExtension::class.java).addDependencies()
    }
    target.extensions.add("instrumentationOverheadAnalyzer", InstrumentationOverheadAnalyzerGradleExtension())
    super.apply(target)
  }

  fun KotlinMultiplatformExtension.addDependencies() {
    sourceSets.getByName("commonMain").dependencies {
      // To be able to create files
      implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.5.3")
    }
  }

  override fun applyToCompilation(
    kotlinCompilation: KotlinCompilation<*>
  ): Provider<List<SubpluginOption>> {
    val project = kotlinCompilation.target.project
    return project.provider {
      // internally the parameters defined in applyToCompilation are passed as
      // "-P plugin:<compilerPluginId>:<key>=<value>" on the command line
      // TODO: Extract options from build file
      // Something like:
      // instrumentationOverheadAnalyzer {
      //   enabled = true
      //   ...
      // }
      val extension =
        project.extensions.findByName("instrumentationOverheadAnalyzer") as? InstrumentationOverheadAnalyzerGradleExtension
      if (extension == null) {
        error("instrumentationOverheadAnalyzer gradle extension not found!")
      }
      listOf(
        SubpluginOption("enabled", extension.enabled.toString()),
        SubpluginOption("kind", extension.kind.toString()),
      )
    }
  }

  // must be the same as "override val pluginId" in compiler plugin
  // based on this id the command line parameters will be passed to the compiler
  // internally the parameters defined in applyToCompilation are passed as
  // "-P plugin:<compilerPluginId>:<key>=<value>" on the command line
  override fun getCompilerPluginId(): String {
    return "instrumentation-overhead-analyzer-plugin"
  }

  override fun getPluginArtifact(): SubpluginArtifact {
    return SubpluginArtifact(
      groupId = "at.jku.ssw",
      artifactId = "instrumentation-overhead-analyzer",
      version = target.project.version.toString()
    )
  }
}