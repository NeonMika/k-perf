package at.ssw.compilerplugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.util.*


/**
 * Extension function to create a helper DSL scope for building IR expressions.
 */
fun IrBuilderWithScope.callHelper(block: IrCallDsl.() -> IrExpression): IrExpression {
    return IrCallDsl(this).block()
}

/**
 * Extension function for DeclarationIrBuilder to create a helper DSL scope.
 */
fun DeclarationIrBuilder.callHelper(block: IrCallDsl.() -> IrExpression): IrExpressionBody {
    return irExprBody(IrCallDsl(this).block())
}

/**
 * DSL for building IR call expressions with a fluent interface.
 */
class IrCallDsl(private val builder: IrBuilderWithScope) {
    /**
     * Call a function on a property or other symbol.
     *
     * @param func The function symbol to call
     * @param args Variable number of arguments to pass to the function
     * @return A ChainableCall that can be further chained
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun IrSymbol.call(func: IrFunctionSymbol, debug: (String) -> Unit, vararg args: Any): ChainableCall {
        val irCall = builder.irCall(func).apply {
            this.dispatchReceiver = when (this@call) {
                is IrPropertySymbol -> {
                    val getter = owner.getter
                        ?: throw IllegalArgumentException("Property ${this@call} does not have a getter")
                    builder.irCall(getter.symbol)
                }
                is IrFieldSymbol -> builder.irGetField(null, owner)
                is IrFunctionSymbol -> builder.irCall(this@call)
                else -> throw IllegalArgumentException(
                    "Unsupported IrSymbol type for dispatchReceiver: ${this::class.simpleName}"
                )
            }

            val nonDefaultParameters = func.owner.valueParameters.filter { !it.hasDefaultValue() }
            if(nonDefaultParameters.size != args.size) {
                throw IllegalArgumentException("Expected ${nonDefaultParameters.size} arguments, got ${args.size}")
            }

            args.forEachIndexed { index, arg ->
                putValueArgument(index, builder.convertToIrExpression(arg))
            }
        }

        debug(irCall.dump())
        return ChainableCall(builder, irCall)
    }

    fun IrProperty.call(func: IrFunctionSymbol, debug: (String) -> Unit, vararg args: Any): ChainableCall = this.symbol.call(func, debug, *args)

    fun IrField.call(func: IrFunctionSymbol, debug: (String) -> Unit, vararg args: Any): ChainableCall = this.symbol.call(func, debug, *args)

    operator fun IrFunction.invoke(debug: (String) -> Unit, vararg args: Any): ChainableCall = this.symbol.call(this.symbol, debug, *args)

    operator fun IrFunctionSymbol.invoke(debug: (String) -> Unit, vararg args: Any) : ChainableCall = this.call(this, debug, *args)

    /**
     * Call a function on an expression.
     */
    /*fun IrExpression.call(func: IrFunctionSymbol, vararg args: Any): ChainableCall {
        val call = builder.irCall(func).apply {
            this.dispatchReceiver = this@call

            args.forEachIndexed { index, arg ->
                putValueArgument(index, builder.convertToIrExpression(arg))
            }
        }

        return ChainableCall(builder, call)
    }*/
    /**
     * Create a constructor call.
     */
    //TODO rewrite for constructorsymbol
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun new(classSymbol: IrClassSymbol, vararg args: Any): IrExpression {
        val constructor = classSymbol.constructors.first()
        return builder.irCall(constructor).apply {
            args.forEachIndexed { index, arg ->
                putValueArgument(index, builder.convertToIrExpression(arg))
            }
        }
    }

    /**
     * Chainable call class that allows fluid method chaining.
     */
    inner class ChainableCall(
        private val builder: IrBuilderWithScope,
        private val call: IrFunctionAccessExpression
    ) {
        /**
         * Chain another method call with an explicit function symbol.
         */
        fun chain(func: IrFunctionSymbol, vararg args: Any): ChainableCall {
            val chainedCall = builder.irCall(func).apply {
                this.extensionReceiver = call

                args.forEachIndexed { index, arg ->
                    putValueArgument(index, builder.convertToIrExpression(arg))
                }
            }

            return ChainableCall(builder, chainedCall)
        }

        fun build(): IrExpression = call

        fun buildReturn(): IrReturn = builder.irReturn(call)
    }
}

fun IrBuilderWithScope.convertToIrExpression(value: Any?): IrExpression {
    if (value == null) return irNull()

    return when (value) {
        is Boolean -> irBoolean(value)
        is Byte -> irByte(value)
        is Short -> irShort(value)
        is Int -> irInt(value)
        is Long -> irLong(value)
        is Float -> value.toIrConst(context.irBuiltIns.floatType)
        is Double -> value.toIrConst(context.irBuiltIns.doubleType)
        is Char -> irChar(value)

        is String -> irString(value)
        is IrCallImpl -> value
        is IrCall -> irCall(value.symbol)
        is IrExpression -> value
        is IrFunctionAccessExpression -> value
        is IrCallDsl.ChainableCall -> value.build()
        is IrFunction -> irCall(value)
        is IrProperty -> irCall(value.getter ?: error("Property has no getter"))
        is IrField -> irGetField(null, value)
        is IrValueParameter -> irGet(value)
        is IrVariable -> irGet(value)
        is IrClassSymbol -> irGetObject(value)
        is IrValueDeclaration -> irGet(value)
        is IrStringConcatenation -> return value
        is IrConst<*> -> value

        else -> error("Cannot convert $value to IrExpression")
    }
}