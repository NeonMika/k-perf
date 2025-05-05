package at.ssw.compilerplugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.dump

@OptIn(UnsafeDuringIrConstructionAPI::class)
class IrStringBuilder(
    private val pluginContext: IrPluginContext,
    file: IrFile
) {
    private val stringBuilderField: IrField
    private val sbClass = pluginContext.findClass("kotlin/text/StringBuilder")!!

    init {
        val randomNumber = DeclarationIrBuilder(pluginContext, file.symbol).callExpression {
            pluginContext.findClass("kotlin/random/Random.Default")!!.call(pluginContext, "nextInt")
        }

        stringBuilderField = pluginContext.createField(file.symbol, "_stringBuilder_${randomNumber.dump()}", isStatic = false) {
            sbClass.findConstructor(pluginContext)!!()
        }
        file.declarations.add(stringBuilderField)
        stringBuilderField.parent = file
    }

    fun append(builder: IrBlockBodyBuilder, value: Any): IrCall {
        val appendMethod = sbClass.findFunction(pluginContext, "append(${value::class.simpleName?.lowercase() ?: "any"})")
            ?: throw IllegalArgumentException("Method append not found in StringBuilder")

        return builder.irCall(appendMethod).apply {
            dispatchReceiver = builder.irGetField(null, stringBuilderField)
            putValueArgument(0, builder.convertToIrExpression(value))
        }
    }

    fun insert(builder: IrBlockBodyBuilder, index: Int, value: Any): IrCall {
        val insertMethod = sbClass.findFunction(pluginContext, "insert(int, ${value::class.simpleName?.lowercase() ?: "any"})")
            ?: throw IllegalArgumentException("Method insert not found in StringBuilder")

        return builder.irCall(insertMethod).apply {
            dispatchReceiver = builder.irGetField(null, stringBuilderField)
            putValueArgument(0, builder.convertToIrExpression(index))
            putValueArgument(1, builder.convertToIrExpression(value))
        }
    }

    fun delete(builder: IrBlockBodyBuilder, start: Int, end: Int): IrCall {
        val deleteMethod = sbClass.findFunction(pluginContext, "delete(int, int)")!!

        return builder.irCall(deleteMethod).apply {
            dispatchReceiver = builder.irGetField(null, stringBuilderField)
            putValueArgument(0, builder.convertToIrExpression(start))
            putValueArgument(1, builder.convertToIrExpression(end))
        }
    }

    fun IrToString(builder: IrBlockBodyBuilder): IrCall {
        val toStringMethod = sbClass.findFunction(pluginContext, "toString()")!!

        return builder.irCall(toStringMethod).apply {
            dispatchReceiver = builder.irGetField(null, stringBuilderField)
        }
    }


}