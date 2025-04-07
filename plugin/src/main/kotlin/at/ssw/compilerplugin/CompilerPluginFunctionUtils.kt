package at.ssw.compilerplugin

import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.util.*

/**
 * A helper function for building an IR block body using a DSL scope.
 *
 * This function creates an instance of `IrCallDsl` and applies the provided
 * block of code to it. The DSL scope allows for building IR call expressions
 * in a fluent manner. After executing the block, it constructs the body of the
 * IR block using the statements accumulated in the DSL.
 *
 * @receiver The `IrBlockBodyBuilder` that constructs the IR block body.
 * @param block A lambda with a receiver of type `IrCallDsl` used to define
 *              the IR call expressions to be included in the block body.
 */
fun IrBlockBodyBuilder.functionBodyHelper(block: IrCallDsl.() -> Unit) {
    val helper = IrCallDsl(this)
    helper.block()
    helper.buildBody(this)
}

/**
 * Extension function for DeclarationIrBuilder to create a helper DSL scope.
 */
fun DeclarationIrBuilder.callHelper(block: IrCallDsl.() -> IrExpression): IrExpressionBody {
    return irExprBody(IrCallDsl(this).block())
}

/**
 * A DSL scope for building IR call expressions in a fluent manner.
 *
 * This class is used by the [functionBodyHelper] and [callHelper] functions to
 * allow for building IR call expressions in a fluent manner. The DSL scope is
 * used to accumulate the IR call expressions, and when the block is executed, the
 * accumulated expressions are used to construct the IR block body.
 *
 * @property builder The IrBuilderWithScope used to construct the IR call
 *                    expressions.
 */
class IrCallDsl(private val builder: IrBuilderWithScope) {
    private val statements: MutableList<IrStatement> = mutableListOf()
    /**
     * Call a function on a property or other symbol.
     *
     * @param func The function symbol to call
     * @param args Variable number of arguments to pass to the function
     * @return A ChainableCall that can be further chained
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun IrSymbol.call(func: IrFunctionSymbol, vararg args: Any): ChainableCall {

        val receiver = when (this) {
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
        }

        val nonDefaultParameters = func.owner.valueParameters.filter { !it.hasDefaultValue() }
        if(nonDefaultParameters.size != args.size) {
            throw IllegalArgumentException("Expected ${nonDefaultParameters.size} arguments, got ${args.size}")
        }

        val newArgs = args.map { builder.convertToIrExpression(it) }.toMutableList()

        val dispatchReceiver = if (func.owner.extensionReceiverParameter == null && func.owner.dispatchReceiverParameter != null) {
            receiver
        } else {
            null
        }

        val extensionReceiver = if (func.owner.extensionReceiverParameter != null) {
            receiver
        } else {
            null
        }

        return ChainableCall(builder, func, dispatchReceiver, extensionReceiver, newArgs)
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
     * Constructs the body of an IR block by adding the accumulated statements.
     *
     * This function iterates over the statements that have been collected in
     * the DSL and adds each one to the provided `IrBlockBodyBuilder`. The
     * `IrBlockBodyBuilder` is then responsible for constructing the IR block
     * body using these statements.
     *
     * @param irBlockBodyBuilder The builder used to create the IR block body.
     */
    fun buildBody(irBlockBodyBuilder: IrBlockBodyBuilder) {
        irBlockBodyBuilder.run {
            statements.forEach {
                +it
            }
        }
    }

    /**
     * Builds an IR expression representing a string concatenation of the given params.
     *
     * @param params The variable number of arguments to be concatenated.
     * @return An IR expression representing a string concatenation of the given params.
     */
    fun irConcat(vararg params: Any): IrStringConcatenation {
        val concat = builder.irConcat()
        for (param in params) {
            concat.addArgument(builder.convertToIrExpression(param))
        }
        return concat
    }

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

        /**
         * Builds an IR function call expression for the current chainable call.
         *
         * @return An IrFunctionAccessExpression representing the built function call.
         */
        fun build(): IrFunctionAccessExpression {
            val call = builder.irCall(callee).apply {
                this.dispatchReceiver = this@ChainableCall.dispatchReceiver
                this.extensionReceiver = this@ChainableCall.extensionReceiver
            }

            args.forEachIndexed { index, value ->
                call.putValueArgument(index, value)
            }

            return call
        }

        /**
         * Adds the built function call to the IR expression chain.
         *
         * This function will take the current chainable call and build an IR function call expression.
         * The resulting expression will then be added to the IR expression chain.
         */
        operator fun unaryPlus() {
            statements.add(this.build())
        }


        /**
         * Builds an IR return expression for the current chainable call and adds it to the statements list.
         *
         * This function constructs an IR return expression using the current chainable call and appends
         * it to the list of statements. The resulting expression represents the return of the built function call.
         */
        fun buildReturn() = statements.add(builder.irReturn(this.build()))
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