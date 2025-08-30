package at.ssw.compilerplugin.thesisexamples

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import kotlin.time.ExperimentalTime

class PerfMeasureExtension2Ex1Old : IrGenerationExtension {
    @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val funMarkNow = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlin.time"),
                FqName("TimeSource.Monotonic"),
                Name.identifier("markNow")
            )
        ).single()
    }
}