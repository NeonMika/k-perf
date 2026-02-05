package at.jku.ssw.compilerplugin

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

/*
Registrar to register all registrars.
It is found via a ServiceLoader.
Thus, we need an entry in META-INF/services/org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
that reads at.jku.ssw.compilerplugin.PerfMeasureComponentRegistrar
 */
@OptIn(ExperimentalCompilerApi::class)
class IoaComponentRegistrar : CompilerPluginRegistrar() {
  override val pluginId: String = "instrumentation-overhead-analyzer-plugin"

  override val supportsK2: Boolean = true

  override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
    val enabled = configuration[IoaConfigurationKeys.ENABLED] ?: true
    val kind = configuration[IoaConfigurationKeys.KIND] ?: IoaKind.None

    if (enabled) {
      IrGenerationExtension.registerExtension(IoaGnerationExtension(kind))
    }
  }
}