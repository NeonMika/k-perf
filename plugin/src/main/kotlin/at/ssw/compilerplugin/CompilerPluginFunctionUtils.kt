package at.ssw.compilerplugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.toIrConst

@OptIn(UnsafeDuringIrConstructionAPI::class)
operator fun IrConstructorSymbol.invoke(pluginContext: IrPluginContext, symbol: IrSymbol, vararg args : Any?) : IrExpressionBody {
    if (this.owner.valueParameters.size != args.size) {
        throw IllegalArgumentException("Expected ${this.owner.valueParameters.size} arguments for constructor call, but got ${args.size}")
    }

    val builder = DeclarationIrBuilder(pluginContext, symbol)

    val argsList = args.map {
        builder.convertToIrExpression(it, pluginContext)
    }

    val constructorCall = builder.irCallConstructor(
        this,
        //TODO handle generics
        listOf()
    )

    this.owner.valueParameters.forEachIndexed { index, _ ->
        constructorCall.putValueArgument(index, argsList[index])
    }

    return builder.irExprBody(constructorCall)
}

fun IrBuilderWithScope.convertToIrExpression(value: Any?, pluginContext: IrPluginContext): IrExpression {
    if (value == null) return irNull()

    return when (value) {
        is Boolean -> value.toIrConst(pluginContext.irBuiltIns.booleanType)
        is Byte -> value.toIrConst(pluginContext.irBuiltIns.byteType)
        is Short -> value.toIrConst(pluginContext.irBuiltIns.shortType)
        is Int -> value.toIrConst(pluginContext.irBuiltIns.intType)
        is Long -> value.toIrConst(pluginContext.irBuiltIns.longType)
        is Float -> value.toIrConst(pluginContext.irBuiltIns.floatType)
        is Double -> value.toIrConst(pluginContext.irBuiltIns.doubleType)
        is Char -> value.toIrConst(pluginContext.irBuiltIns.charType)

        is String -> irString(value)
        is IrCallImpl -> value
        is IrCall -> irCall(value.symbol)
        is IrFunction -> irCall(value)
        is IrProperty -> irCall(value.getter ?: error("Property has no getter"))
        is IrField -> irGetField(null, value)
        is IrValueParameter -> irGet(value)
        is IrVariable -> irGet(value)
        is IrClassSymbol -> irGetObject(value)
        is IrConst<*> -> value

        else -> error("Cannot convert $value to IrExpression")
    }
}