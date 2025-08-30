package at.ssw.compilerplugin.thesisexamples

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import kotlin.time.ExperimentalTime

class PerfMeasureExtension2Ex2Old : IrGenerationExtension {
    @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val randomDefaultObjectClass = pluginContext.referenceClass(ClassId.fromString("kotlin/random/Random.Default"))!!
        val nextIntFunc = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlin.random"),
                FqName("Random.Default"),
                Name.identifier("nextInt")
            )
        ).single {
            it.owner.valueParameters.isEmpty()
        }
        val firstFile = moduleFragment.files[0]

        val randomNumber = pluginContext.irFactory.buildField {
            name = Name.identifier("_randNumber")
            type = pluginContext.irBuiltIns.intType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(irCall(nextIntFunc).apply {
                    dispatchReceiver = irGetObject(randomDefaultObjectClass)
                })
            }
        }
    }
}