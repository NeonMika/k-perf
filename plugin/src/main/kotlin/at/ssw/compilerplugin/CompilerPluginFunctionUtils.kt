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
 * A helper function for constructing an IR expression body using a DSL scope.
 *
 * @receiver The `DeclarationIrBuilder` used to build the IR expression.
 * @param block A lambda with a receiver of type `IrCallDsl` used to define
 *              the IR call expression to be included in the expression body.
 * @return An `IrExpressionBody` containing the constructed IR expression.
 */
fun DeclarationIrBuilder.callHelper(block: IrCallDsl.() -> IrCallDsl.ChainableCall): IrExpressionBody {
    val helper = IrCallDsl(this)
    return irExprBody(helper.block().build())
}

/**
 * A DSL scope for building IR call expressions in a fluent manner.
 *
 * @property builder The IrBuilderWithScope used to construct the IR call
 *                    expressions.
 */
class IrCallDsl(private val builder: IrBuilderWithScope) {
    private val statements: MutableList<IrStatement> = mutableListOf()
    /**
     * Call a function on a property or other symbol.
     *
     * @param func The function to call. Can be a property, then the getter is called
     * @param args Variable number of arguments to pass to the function
     * @throws IllegalArgumentException if the number of arguments does not match the number of parameters
     * @throws IllegalArgumentException if the property does not have a getter
     * @throws IllegalArgumentException if the provided function to call is not callable (no property or function(symbol))
     * @throws IllegalStateException if the field is not a top-level field
     * @return A ChainableCall that can be further chained
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun IrSymbol.call(func: Any, vararg args: Any): ChainableCall {
        //TODO: restrict?
        val functionCall : IrFunctionSymbol = when (func) {
            is IrFunctionSymbol -> func
            is IrFunction -> func.symbol
            is IrProperty -> {
                val getter = func.getter
                    ?: throw IllegalArgumentException("IrCallHelper: Property ${func.name} does not have a getter")
                getter.symbol
            }
            is IrPropertySymbol -> {
                val getter = func.owner.getter
                    ?: throw IllegalArgumentException("IrCallHelper: Property ${func.owner.name} does not have a getter")
                getter.symbol
            }
            else -> throw IllegalArgumentException("IrCallHelper: Unsupported function type: ${func::class.simpleName}")
        }

        val receiver = when (this) {
            is IrPropertySymbol -> {
                val getter = owner.getter
                    ?: throw IllegalArgumentException("IrCallHelper: Property ${owner.name} does not have a getter")
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
            is IrVariableSymbol -> builder.irGet(owner)
            else -> null
        }

        val nonDefaultParameters = functionCall.owner.valueParameters.filter { !it.hasDefaultValue() }
        if(nonDefaultParameters.size != args.size) {
            throw IllegalArgumentException("Expected ${nonDefaultParameters.size} arguments, got ${args.size}")
        }

        val newArgs = args.map { builder.convertToIrExpression(it) }.toMutableList()

        val dispatchReceiver = if (functionCall.owner.extensionReceiverParameter == null && functionCall.owner.dispatchReceiverParameter != null) {
            receiver
        } else {
            null
        }

        val extensionReceiver = if (functionCall.owner.extensionReceiverParameter != null) {
            receiver
        } else {
            null
        }

        return ChainableCall(builder, functionCall, dispatchReceiver, extensionReceiver, newArgs)
    }

    /**
     * Extension function for [IrProperty] to call a function on this property with the given arguments.
     *
     * @param func The function symbol to call
     * @param args The arguments to be passed to the function
     * @return A ChainableCall that can be further chained
     */
    fun IrProperty.call(func: Any, vararg args: Any): ChainableCall = this.symbol.call(func, *args)

    /**
     * Calls a function on this field with the given arguments.
     *
     * @param func The function symbol to call
     * @param args The arguments to be passed to the function
     * @return A ChainableCall that can be further chained
     */
    fun IrField.call(func: Any, vararg args: Any): ChainableCall = this.symbol.call(func, *args)

    /**
     * Calls a function on this value parameter with the given arguments.
     *
     * @param func The function symbol to call
     * @param args The arguments to be passed to the function
     * @return A ChainableCall that can be further chained
     */
    fun IrValueParameter.call(func: Any, vararg args: Any): ChainableCall = this.symbol.call(func, *args)

    /**
     * Calls a function on this class with the given arguments.
     *
     * @param func The function symbol to call
     * @param args The arguments to be passed to the function
     * @return A ChainableCall that can be further chained
     */
    fun IrClass.call(func: Any, vararg args: Any): ChainableCall = this.symbol.call(func, *args)

    /**
     * Calls a function on this variable with the given arguments.
     *
     * @param func The function symbol to call
     * @param args The arguments to be passed to the function
     * @return A ChainableCall that can be further chained
     */
    fun IrVariable.call(func: Any, vararg args: Any): ChainableCall = this.symbol.call(func, *args)

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
     * the DSL by unary plus operators and adds each one to the provided `IrBlockBodyBuilder`. The
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
         * @throws error if the given value is not supported.
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