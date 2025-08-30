package at.ssw.compilerplugin.thesisexamples

import at.ssw.compilerplugin.call
import at.ssw.compilerplugin.findClass
import at.ssw.compilerplugin.findFunction
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import kotlin.time.ExperimentalTime

class PerfMeasureExtension2Ex2New : IrGenerationExtension {
    @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val randomDefaultObjectClass = pluginContext.findClass("kotlin/random/Random.Default")
        val nextIntFunc = pluginContext.findFunction("kotlin/random/Random.Default.nextInt()")
        val firstFile = moduleFragment.files[0]

        val randomNumber = pluginContext.irFactory.buildField {
            name = Name.identifier("_randNumber")
            type = pluginContext.irBuiltIns.intType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(call(pluginContext, nextIntFunc, irGetObject(randomDefaultObjectClass)))
            }
        }
    }
}