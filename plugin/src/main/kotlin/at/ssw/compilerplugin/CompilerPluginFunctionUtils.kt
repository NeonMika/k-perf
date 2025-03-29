package at.ssw.compilerplugin

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

fun DeclarationIrBuilder.innerCallHelper(block: IrCallDsl.() -> IrExpression): IrExpression {
    return IrCallDsl(this).block()
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
    fun IrSymbol.call(func: IrFunctionSymbol, vararg args: Any): ChainableCall {

        /*val receiver = when (this) {
            is IrPropertySymbol -> {
                val getter = owner.getter
                    ?: throw IllegalArgumentException("IrCallHelper: Property ${this.owner.name} does not have a getter")
                builder.irCall(getter)
            }
            is IrFieldSymbol -> {
                //only top level fields supported
                if (owner.parent !is IrFile) {
                    throw IllegalStateException("IrCallHelper: Only top-level fields are supported here")
                }
                builder.irGetField(null, owner)
            }
            is IrValueSymbol -> builder.irGet(owner)
            is IrClassSymbol -> builder.irGetObject(this)
            else -> null
        }*/

        val nonDefaultParameters = func.owner.valueParameters.filter { !it.hasDefaultValue() }
        if(nonDefaultParameters.size != args.size) {
            throw IllegalArgumentException("Expected ${nonDefaultParameters.size} arguments, got ${args.size}")
        }

        val newArgs = args.map { builder.convertToIrExpression(it) }.toMutableList()

        /*val dispatchReceiver = if (func.owner.extensionReceiverParameter == null && func.owner.dispatchReceiverParameter != null) {
            receiver
        } else {
            null
        }

        val extensionReceiver = if (func.owner.extensionReceiverParameter != null) {
            receiver
        } else {
            null
        }

        return ChainableCall(builder, func, dispatchReceiver, extensionReceiver, newArgs)*/
        return ChainableCall(builder, func, null, null, newArgs)
    }

    /**
     * Extension function for [IrProperty] to call a function on this property with the given arguments.
     *
     * @param func The function symbol to call
     * @param args The arguments to be passed to the function
     * @return A ChainableCall that can be further chained
     */
    fun IrProperty.call(func: IrFunctionSymbol, vararg args: Any): ChainableCall = this.symbol.call(func, *args)

    /**
     * Calls a function on this field with the given arguments.
     *
     * @param func The function symbol to call
     * @param args The arguments to be passed to the function
     * @return A ChainableCall that can be further chained
     */
    fun IrField.call(func: IrFunctionSymbol, vararg args: Any): ChainableCall = this.symbol.call(func, *args)

    /**
     * Calls this function with the given arguments and returns a ChainableCall to continue building
     * the IR expression tree.
     *
     * @param args The arguments to be passed to the function
     * @return A ChainableCall that can be further chained
     */
    operator fun IrFunction.invoke(vararg args: Any): ChainableCall = this.symbol.call(this.symbol, *args)

    /**
     * Calls this function with the given arguments and returns a ChainableCall to continue building
     * the IR expression tree.
     *
     * @param args The arguments to be passed to the function
     * @return A ChainableCall that can be further chained
     */
    operator fun IrFunctionSymbol.invoke(vararg args: Any) : ChainableCall = this.call(this, *args)

    /**
     * Chainable call class that allows fluid method chaining.
     */
    inner class ChainableCall(
        private val builder: IrBuilderWithScope,
        private val callee: IrFunctionSymbol,
        private var dispatchReceiver: IrExpression? = null,
        private var extensionReceiver: IrExpression? = null,
        private val args: MutableList<IrExpression> = mutableListOf()
    ) {

        /**
         * Chains another function call to the current IR expression chain.
         *
         * @param func The function symbol representing the function to be called.
         * @param args The arguments to be passed to the function.
         * @return A new ChainableCall instance representing the chained function call.
         */
        fun chain(func: IrFunctionSymbol, vararg args: Any): ChainableCall {
            val receiver = this.build()
            val newArgs = args.map { builder.convertToIrExpression(it) }.toMutableList()

            return ChainableCall(builder, func, null, receiver, newArgs)
        }

        fun withDispatchReceiver(receiver: IrExpression): ChainableCall {
            this.dispatchReceiver = receiver
            return this
        }

        /**
         * Builds an IR function call expression for the current chainable call.
         *
         * @return An IrFunctionAccessExpression representing the built function call.
         */
        fun build(): IrFunctionAccessExpression {
            val call = builder.irCall(callee).apply {
                dispatchReceiver = this.dispatchReceiver
                extensionReceiver = this.extensionReceiver
            }

            args.forEachIndexed { index, value ->
                call.putValueArgument(index, value)
            }

            return call
        }

        /**
         * Builds an IR return expression for the current chainable call.
         *
         * @return An IrReturn expression representing the return of the built function call.
         */
        fun buildReturn(): IrReturn = builder.irReturn(this.build())
    }
}

        /**
         * Converts the given value into an IrExpression. This will be used as a function argument.
         *
         * Supports the following types:
         * - Primitive types
         * - String
         * - IrCall
         * - IrCallDsl.ChainableCall
         * - IrFunction
         * - IrProperty
         * - IrField
         * - IrValueParameter
         * - IrVariable
         * - IrClassSymbol
         * - IrValueDeclaration
         * - IrStringConcatenation
         * - IrConst<*>
         *
         * If the given value is not supported, an error will be thrown.
         */
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
        is IrProperty -> irCall(value.getter ?: error("IrCallHelper-convertToIrExpression: Property has no getter"))
        is IrField -> irGetField(null, value)
        is IrValueParameter -> irGet(value)
        is IrVariable -> irGet(value)
        is IrClassSymbol -> irGetObject(value)
        is IrValueDeclaration -> irGet(value)
        is IrStringConcatenation -> return value
        is IrConst<*> -> value

        else -> error("IrCallHelper-convertToIrExpression: Cannot convert $value to IrExpression")
    }
}