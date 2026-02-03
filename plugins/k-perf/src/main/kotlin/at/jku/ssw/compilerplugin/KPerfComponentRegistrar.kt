package at.jku.ssw.compilerplugin

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
class KPerfComponentRegistrar : CompilerPluginRegistrar() {
  override val supportsK2: Boolean = true

  override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
    // org.jetbrains.kotlin.cli.common.CLIConfigurationKeys contains default configuration keys
    val messageCollector = configuration.get(CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY)!!

    /*
    println(":) :) :)")
    messageCollector.report(
        CompilerMessageSeverity.INFO,
        "CLIConfigurationKeys.ALLOW_KOTLIN_PACKAGE - ${CLIConfigurationKeys.ALLOW_KOTLIN_PACKAGE} - ${
            configuration.get(CLIConfigurationKeys.ALLOW_KOTLIN_PACKAGE)
        }"
    )
    messageCollector.report(
        CompilerMessageSeverity.INFO,
        "CLIConfigurationKeys.CONTENT_ROOTS - ${CLIConfigurationKeys.CONTENT_ROOTS} - ${
            configuration.get(CLIConfigurationKeys.CONTENT_ROOTS)
        }"
    )
    messageCollector.report(
        CompilerMessageSeverity.INFO,
        "CLIConfigurationKeys.FLEXIBLE_PHASE_CONFIG - ${CLIConfigurationKeys.FLEXIBLE_PHASE_CONFIG} - ${
            configuration.get(CLIConfigurationKeys.FLEXIBLE_PHASE_CONFIG)
        }"
    )
    messageCollector.report(
        CompilerMessageSeverity.INFO,
        "CLIConfigurationKeys.INTELLIJ_PLUGIN_ROOT - ${CLIConfigurationKeys.INTELLIJ_PLUGIN_ROOT} - ${
            configuration.get(CLIConfigurationKeys.INTELLIJ_PLUGIN_ROOT)
        }"
    )
    messageCollector.report(
        CompilerMessageSeverity.INFO,
        "CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY - ${CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY} - ${
            configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY)
        }"
    )
    messageCollector.report(
        CompilerMessageSeverity.INFO,
        "CLIConfigurationKeys.METADATA_DESTINATION_DIRECTORY - ${CLIConfigurationKeys.METADATA_DESTINATION_DIRECTORY} - ${
            configuration.get(CLIConfigurationKeys.METADATA_DESTINATION_DIRECTORY)
        }"
    )
    messageCollector.report(
        CompilerMessageSeverity.INFO,
        "CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY - ${CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY} - ${
            configuration.get(CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY)
        }"
    )
    messageCollector.report(
        CompilerMessageSeverity.INFO,
        "CLIConfigurationKeys.PATH_TO_KOTLIN_COMPILER_JAR - ${CLIConfigurationKeys.PATH_TO_KOTLIN_COMPILER_JAR} - ${
            configuration.get(CLIConfigurationKeys.PATH_TO_KOTLIN_COMPILER_JAR)
        }"
    )
    messageCollector.report(
        CompilerMessageSeverity.INFO,
        "CLIConfigurationKeys.PERF_MANAGER - ${CLIConfigurationKeys.PERF_MANAGER} - ${
            configuration.get(CLIConfigurationKeys.PERF_MANAGER)
        }"
    )
    messageCollector.report(
        CompilerMessageSeverity.INFO,
        "CLIConfigurationKeys.RENDER_DIAGNOSTIC_INTERNAL_NAME - ${CLIConfigurationKeys.RENDER_DIAGNOSTIC_INTERNAL_NAME} - ${
            configuration.get(CLIConfigurationKeys.RENDER_DIAGNOSTIC_INTERNAL_NAME)
        }"
    )
    messageCollector.report(
        CompilerMessageSeverity.INFO,
        "CLIConfigurationKeys.PHASE_CONFIG - ${CLIConfigurationKeys.PHASE_CONFIG} - ${
            configuration.get(CLIConfigurationKeys.PHASE_CONFIG)
        }"
    )
    */

    // Frontend plugin registrar
    /*
    FirExtensionRegistrarAdapter.registerExtension(
        PerfMeasureExtensionRegistrar(
            configuration[LOG_ANNOTATION_KEY] ?: listOf()
        )
    )
    */

    val enabled = configuration[KPerfConfigurationKeys.ENABLED] ?: true
    val flushEarly = configuration[KPerfConfigurationKeys.FLUSH_EARLY] ?: false
    val testKIR = configuration[KPerfConfigurationKeys.TEST_KIR] ?: false

    if (enabled) {
      IrGenerationExtension.registerExtension(KPerfExtension(MessageCollector.NONE, flushEarly))
    }
    if (testKIR) {
      IrGenerationExtension.registerExtension(KIRHelperKitTestingExtension(MessageCollector.NONE))
    }
  }
}