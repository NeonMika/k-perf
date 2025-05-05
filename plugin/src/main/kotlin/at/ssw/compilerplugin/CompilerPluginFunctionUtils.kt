package at.ssw.compilerplugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name

/**
 * A helper function for building an IR block body using a DSL scope.
 *
 * @receiver The `IrBlockBodyBuilder` that constructs the IR block body.
 * @param block A lambda with a receiver of type `IrCallDsl` used to define
 *              the IR call expressions to be included in the block body.
 */
fun IrBlockBodyBuilder.enableCallDSL(block: IrCallDsl.() -> Unit) {
    IrCallDsl(this).block()
}


/**
 * A helper function for constructing an IR expression body using a DSL scope.
 *
 * @receiver The `DeclarationIrBuilder` used to build the IR expression.
 * @param block A lambda with a receiver of type `IrCallDsl` used to define
 *              the IR call expression to be included in the expression body.
 * @return An `IrExpressionBody` containing the constructed IR expression.
 */
fun DeclarationIrBuilder.callExpression(block: IrCallDsl.() -> IrExpression): IrExpressionBody {
    return irExprBody(IrCallDsl(this).block())
}

fun IrPluginContext.createField(
    parentSymbol: IrSymbol,
    fieldName: String,
    isFinal: Boolean = true,
    isStatic: Boolean = true,
    initializerBlock: IrCallDsl.() -> IrExpression
): IrField {
    val initializerExpression = DeclarationIrBuilder(this, parentSymbol).callExpression {
        initializerBlock()
    }

    return this.irFactory.buildField {
        name = Name.identifier(fieldName)
        type = initializerExpression.expression.type
        this.isFinal = isFinal
        this.isStatic = isStatic
    }.apply {
        this.initializer = DeclarationIrBuilder(this@createField, parentSymbol).irExprBody(initializerExpression.expression)
    }
}

/**
 * A DSL scope for building IR call expressions in a fluent manner.
 *
 * @property builder The IrBuilderWithScope used to construct the IR call
 *                    expressions.
 */
class IrCallDsl(private val builder: IrBuilderWithScope) {

    /**
     * Chains a function call to the current `IrFunctionAccessExpression` and returns the resulting expression.
     *
     * @param func The `IrFunctionSymbol` representing the function to be called.
     * @param args A variable number of arguments to be passed to the function.
     * @return A new `IrFunctionAccessExpression` representing the chained function call.
     */
    fun IrFunctionAccessExpression.chain(func: IrFunctionSymbol, vararg args: Any): IrFunctionAccessExpression {
        val newCall = builder.irCall(func).apply {
            this.extensionReceiver = this@chain
        }

        args.forEachIndexed { index, arg ->
            newCall.putValueArgument(index, builder.convertToIrExpression(arg))
        }

        return newCall
    }

    /**
     * Chains a function call to the current `IrFunctionAccessExpression` using a function name and `pluginContext` to find the function.
     *
     * @param signature The full path to the function (no parameters) e.g.: `"package/outerClasses.ClassName.funcName"`.
     * @param pluginContext The `IrPluginContext` used to find the function symbol.
     * @param args A variable number of arguments to be passed to the function.
     * @return A new `IrFunctionAccessExpression` representing the chained function call.
     */
    fun IrFunctionAccessExpression.chain(pluginContext: IrPluginContext, signature: String, vararg args: Any): IrFunctionAccessExpression {
        val params = args.joinToString(separator = ", ", prefix = "(", postfix = ")") { it::class.simpleName?.lowercase() ?: "unknown" }
        val funcSymbol = pluginContext.findFunction(signature + params, this.type)
            ?: throw IllegalArgumentException("Function $signature not found in the provided plugin context")

        val newCall = builder.irCall(funcSymbol).apply {
            this.extensionReceiver = this@chain
        }

        args.forEachIndexed { index, arg ->
            newCall.putValueArgument(index, builder.convertToIrExpression(arg))
        }

        return newCall
    }

    /**
     * Calls a function on a property or other symbol.
     *
     * This function supports various symbol types (e.g., `IrPropertySymbol`, `IrFieldSymbol`, etc.)
     * and constructs an `IrCall` representing the function call. It ensures that the number of
     * arguments matches the number of non-default parameters in the function being called.
     *
     * @param func The function to call. Supported types include:
     *             - `IrFunctionSymbol`: Directly calls the function.
     *             - `IrFunction`: Calls the function represented by its symbol.
     *             - `IrProperty`: Calls the getter of the property.
     *             - `IrPropertySymbol`: Calls the getter of the property symbol.
     * @param args Variable number of arguments to pass to the function. The number of arguments
     *             must match the number of non-default parameters in the function.
     * @throws IllegalArgumentException If the number of arguments does not match the number of
     *                                  non-default parameters, the property does not have a getter,
     *                                  or the provided function type is unsupported.
     * @throws IllegalStateException If the field is not a top-level field.
     * @return An `IrCall` representing the constructed function call, including dispatch or
     *         extension receivers if applicable.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun IrSymbol.call(func: Any, vararg args: Any): IrFunctionAccessExpression {
        //TODO: doesn't support setting dispatch and extension receiver
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
            is IrCall -> this
            is IrConstructorSymbol -> null
            is IrSimpleFunctionSymbol -> null
            //restrict - yes probably with generics -> the best way to do this in kotlin
            else -> throw IllegalArgumentException("IrCallHelper: Unsupported symbol type: ${this::class.simpleName}")
        }

        val nonDefaultParameters = functionCall.owner.valueParameters.filter { !it.hasDefaultValue() }
        if(nonDefaultParameters.size != args.size) {
            throw IllegalArgumentException("Expected ${nonDefaultParameters.size} arguments, got ${args.size}")
        }

        val newArgs = args.map { builder.convertToIrExpression(it) }.toMutableList()

        return builder.irCall(functionCall).apply {
            dispatchReceiver = if (functionCall.owner.extensionReceiverParameter == null && functionCall.owner.dispatchReceiverParameter != null) {
                receiver
            } else {
                null
            }
            extensionReceiver = if (functionCall.owner.extensionReceiverParameter != null) {
                receiver
            } else {
                null
            }
            newArgs.forEachIndexed { index, value -> putValueArgument(index, value) }
        }
    }

    /**
     * Calls a function on the given plugin context with the specified function signature and arguments.
     *
     * @param pluginContext The plugin context to use for finding the function symbol.
     * @param funcSignature The signature of the function to find. It should be in the format
     *                      "package/outerClasses.ClassName.funcName".
     *                      Some standard packages are predefined, and if a package is not found in kotlin it is searched for in the java context.
     * @param extensionReceiverType The type of the extension receiver, if any.
     * @param args The arguments to pass to the function.
     * @return An IrFunctionAccessExpression representing the function call.
     */
    fun call(pluginContext: IrPluginContext, funcSignature: String, extensionReceiverType: IrType?, vararg args: Any): IrFunctionAccessExpression {
        val params = args.joinToString(separator = ", ", prefix = "(", postfix = ")") { it::class.simpleName?.lowercase() ?: "unknown" }
        val funcSymbol = pluginContext.findFunction(funcSignature + params, extensionReceiverType) ?: throw IllegalArgumentException("IrCallHelper: Function $funcSignature not found")
        return funcSymbol(*args)
    }

    /**
     * Calls a constructor of a class using the given function signature and arguments.
     *
     * @param pluginContext The `IrPluginContext` used to find the constructor symbol.
     * @param funcSignature The signature of the constructor to call, in the format
     *                      "package/outerClasses.ClassName".
     * @param args The arguments to pass to the constructor. The number and types of arguments
     *             must match the constructor's parameters.
     * @return An `IrFunctionAccessExpression` representing the constructor call.
     * @throws IllegalArgumentException If the constructor cannot be found.
     */
    fun callConstructor(pluginContext: IrPluginContext, funcSignature: String, vararg args: Any): IrFunctionAccessExpression {
        val params = args.joinToString(separator = ", ", prefix = "(", postfix = ")") { it::class.simpleName?.lowercase() ?: "unknown" }
        val funcSymbol = pluginContext.findConstructor(funcSignature + params) ?: throw IllegalArgumentException("IrCallHelper: Constructor $funcSignature not found")
        return funcSymbol(*args)
    }

    /**
     * Calls a function on the given symbol with the specified function signature and arguments.
     *
     * @receiver The `IrSymbol` on which the function is to be called. Supported symbol types include:
     *           - `IrClassSymbol`
     *           - `IrVariableSymbol`
     *           - `IrValueSymbol`
     *           - `IrFieldSymbol`
     *           - `IrPropertySymbol`
     * @param pluginContext The `IrPluginContext` used to find the function symbol.
     * @param funcSignature The signature of the function to call, in the format
     *                      "funcName".
     * @param args The arguments to pass to the function.
     * @return An `IrFunctionAccessExpression` representing the function call.
     * @throws IllegalArgumentException If the symbol type is unsupported, the class cannot be resolved,
     *                                  or the function cannot be found.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun IrSymbol.call(pluginContext: IrPluginContext, funcSignature: String, vararg args: Any): IrFunctionAccessExpression {
        val irClass = when (this) {
            is IrClassSymbol -> this.owner
            is IrVariableSymbol -> this.owner.type.getClass()
            is IrValueSymbol -> this.owner.type.getClass()
            is IrFieldSymbol -> this.owner.type.getClass()
            is IrPropertySymbol -> this.owner.getter?.returnType?.getClass() ?: throw IllegalArgumentException("IrCallHelper: Property ${this.owner.name} does not have a getter")
            else -> throw IllegalArgumentException("Unsupported symbol type: ${this::class.simpleName}")
        } ?: throw IllegalArgumentException("Could not resolve class from symbol")

        val params = args.joinToString(separator = ", ", prefix = "(", postfix = ")") { it::class.simpleName?.lowercase() ?: "unknown" }
        val functionSymbol = irClass.symbol.findFunction(pluginContext, funcSignature + params, irClass.defaultType)
            ?: when (this) {
                is IrVariableSymbol -> this.owner.findFunction(pluginContext, funcSignature + params, this.owner.type)
                is IrPropertySymbol -> this.findFunction(pluginContext, funcSignature + params, this.owner.getter?.extensionReceiverParameter?.type)
                else -> null
            } ?: throw IllegalArgumentException(
                "IrCallHelper: Function $funcSignature with params $params not found in class ${irClass.name}"
            )

        return this.call(functionSymbol, *args)
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
     * Extension function for [IrProperty] to call a function on this property with the given arguments.
     *
     * @param func The function symbol to call
     * @param args The arguments to be passed to the function
     * @return An IrCall that can be further chained
     */
    fun IrProperty.call(func: Any, vararg args: Any): IrFunctionAccessExpression = this.symbol.call(func, *args)

    /**
     * Calls a function on this field with the given arguments.
     *
     * @param func The function symbol to call
     * @param args The arguments to be passed to the function
     * @return An IrCall that can be further chained
     */
    fun IrField.call(func: Any, vararg args: Any): IrFunctionAccessExpression = this.symbol.call(func, *args)

    /**
     * Calls a function on this value parameter with the given arguments.
     *
     * @param func The function symbol to call
     * @param args The arguments to be passed to the function
     * @return An IrCall that can be further chained
     */
    fun IrValueParameter.call(func: Any, vararg args: Any): IrFunctionAccessExpression = this.symbol.call(func, *args)

    /**
     * Calls a function on this class with the given arguments.
     *
     * @param func The function symbol to call
     * @param args The arguments to be passed to the function
     * @return An IrCall that can be further chained
     */
    fun IrClass.call(func: Any, vararg args: Any): IrFunctionAccessExpression = this.symbol.call(func, *args)

    /**
     * Calls a function on this variable with the given arguments.
     *
     * @param func The function symbol to call
     * @param args The arguments to be passed to the function
     * @return An IrCall that can be further chained
     */
    fun IrVariable.call(func: Any, vararg args: Any): IrFunctionAccessExpression = this.symbol.call(func, *args)

    /**
     * Calls this function with the given arguments and returns an IrCall to continue building
     * the IR expression tree.
     *
     * @param args The arguments to be passed to the function
     * @return An IrCall that can be further chained
     */
    operator fun IrFunction.invoke(vararg args: Any): IrFunctionAccessExpression = this.symbol.call(this.symbol, *args)

    /**
     * Calls this function with the given arguments and returns an IrCall to continue building
     * the IR expression tree.
     *
     * @param args The arguments to be passed to the function
     * @return An IrCall that can be further chained
     */
    operator fun IrFunctionSymbol.invoke(vararg args: Any) : IrFunctionAccessExpression = this.call(this, *args)

    /**
     * Calls a function on this class with the given arguments.
     *
     * @param pluginContext The `IrPluginContext` used to find the function symbol.
     * @param funcSignature The signature of the function to call, in the format "funcName(params?)".
     * @param args The arguments to pass to the function.
     * @return An `IrFunctionAccessExpression` representing the function call.
     */
    fun IrClass.call(pluginContext: IrPluginContext, funcSignature: String, vararg args: Any): IrFunctionAccessExpression =
        this.symbol.call(pluginContext, funcSignature, *args)

    /**
     * Calls a constructor of the class represented by this `IrClassSymbol` with the given arguments.
     *
     * @receiver The `IrClassSymbol` representing the class whose constructor is to be called.
     * @param pluginContext The `IrPluginContext` used to find the constructor symbol.
     * @param args The arguments to pass to the constructor. The types of the arguments are used to
     *             determine the constructor signature.
     * @return An `IrFunctionAccessExpression` representing the constructor call.
     * @throws IllegalArgumentException If the constructor cannot be found in the class.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun IrClassSymbol.callConstructor(pluginContext: IrPluginContext, vararg args: Any): IrFunctionAccessExpression {
        val params = args.joinToString(separator = ", ", prefix = "(", postfix = ")") { it::class.simpleName?.lowercase() ?: "unknown" }
        return this.findConstructor(pluginContext, params)?.invoke(*args)
            ?: throw IllegalArgumentException("IrCallHelper: Constructor wit params: $params not found in class ${this.owner.name}")
    }

    /**
     * Calls a function on this variable with the given arguments.
     *
     * @param pluginContext The `IrPluginContext` used to find the function symbol.
     * @param funcSignature The signature of the function to call, in the format "funcName(params?)".
     * @param args The arguments to pass to the function.
     * @return An `IrFunctionAccessExpression` representing the function call.
     */
    fun IrVariable.call(pluginContext: IrPluginContext, funcSignature: String, vararg args: Any): IrFunctionAccessExpression =
        this.symbol.call(pluginContext, funcSignature, *args)

    /**
     * Calls a function on this value parameter with the given arguments.
     *
     * @param pluginContext The `IrPluginContext` used to find the function symbol.
     * @param funcSignature The signature of the function to call, in the format "funcName(params?)".
     * @param args The arguments to pass to the function.
     * @return An `IrFunctionAccessExpression` representing the function call.
     */
    fun IrValueParameter.call(pluginContext: IrPluginContext, funcSignature: String, vararg args: Any): IrFunctionAccessExpression =
        this.symbol.call(pluginContext, funcSignature, *args)

    /**
     * Calls a function on this field with the given arguments.
     *
     * @param pluginContext The `IrPluginContext` used to find the function symbol.
     * @param funcSignature The signature of the function to call, in the format "funcName(params?)".
     * @param args The arguments to pass to the function.
     * @return An `IrFunctionAccessExpression` representing the function call.
     */
    fun IrField.call(pluginContext: IrPluginContext, funcSignature: String, vararg args: Any): IrFunctionAccessExpression =
        this.symbol.call(pluginContext, funcSignature, *args)

    /**
     * Calls a function on this property with the given arguments.
     *
     * @param pluginContext The `IrPluginContext` used to find the function symbol.
     * @param funcSignature The signature of the function to call, in the format "funcName(params?)".
     * @param args The arguments to pass to the function.
     * @return An `IrFunctionAccessExpression` representing the function call.
     */
    fun IrProperty.call(pluginContext: IrPluginContext, funcSignature: String, vararg args: Any): IrFunctionAccessExpression =
        this.symbol.call(pluginContext, funcSignature, *args)

}

/**
 * Converts the given value into an IrExpression. This will be used as a function argument.
 *
 * Supports the following types:
 * - Primitive types
 * - String
 * - IrCall
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
        is IrFunctionAccessExpression -> value
        is IrFunction -> irCall(value)
        is IrProperty -> irCall(value.getter ?: error("IrCallHelper-convertToIrExpression: Property has no getter"))
        is IrField -> irGetField(null, value)
        is IrValueParameter -> irGet(value)
        is IrVariable -> irGet(value)
        is IrClassSymbol -> irGetObject(value)
        is IrValueDeclaration -> irGet(value)
        is IrStringConcatenation -> return value
        is IrConst<*> -> value
        is IrExpression -> value

        else -> error("IrCallHelper-convertToIrExpression: Cannot convert $value to IrExpression")
    }
}