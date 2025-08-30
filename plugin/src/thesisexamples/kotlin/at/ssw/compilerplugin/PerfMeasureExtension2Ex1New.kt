package at.ssw.compilerplugin.thesisexamples

import at.ssw.compilerplugin.findFunction
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import kotlin.time.ExperimentalTime

class PerfMeasureExtension2Ex1New : IrGenerationExtension {
    @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val funMarkNow = pluginContext.findFunction("kotlin/time/TimeSource.Monotonic/markNow")
    }
}