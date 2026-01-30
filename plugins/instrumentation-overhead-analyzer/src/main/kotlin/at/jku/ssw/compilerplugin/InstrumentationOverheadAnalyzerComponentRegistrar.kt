package at.jku.ssw.compilerplugin

import at.jku.ssw.shared.InstrumentationOverheadAnalyzerKind
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
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
class InstrumentationOverheadAnalyzerComponentRegistrar : CompilerPluginRegistrar() {
  override val supportsK2: Boolean = true

  override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
    // org.jetbrains.kotlin.cli.common.CLIConfigurationKeys contains default configuration keys
    val messageCollector = configuration.get(CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY)!!

    val enabled = configuration[InstrumentationOverheadAnalyzerConfigurationKeys.ENABLED] ?: true
    val kind = configuration[InstrumentationOverheadAnalyzerConfigurationKeys.KIND]
      ?: InstrumentationOverheadAnalyzerKind.StringBuilderAppend

    if (enabled) {
      IrGenerationExtension.registerExtension(InstrumentationOverheadAnalyzerExtension(MessageCollector.NONE, kind))
    }
  }
}