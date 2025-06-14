package at.ssw.helpers

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
fun IrBlockBodyBuilder.enableCallDSL(pluginContext: IrPluginContext, block: IrCallDsl.() -> Unit) {
    IrCallDsl(this, pluginContext).block()
}


/**
 * A helper function for constructing an IR expression body using a DSL scope.
 *
 * @receiver The `DeclarationIrBuilder` used to build the IR expression.
 * @param block A lambda with a receiver of type `IrCallDsl` used to define
 *              the IR call expression to be included in the expression body.
 * @return An `IrExpressionBody` containing the constructed IR expression.
 */
fun DeclarationIrBuilder.callExpression(pluginContext: IrPluginContext, block: IrCallDsl.() -> IrExpression): IrExpressionBody {
    return irExprBody(IrCallDsl(this, pluginContext).block())
}

/**
 * Constructs and returns an `IrFunctionAccessExpression` using a DSL block.
 *
 * @receiver The `DeclarationIrBuilder` used to build the IR expression.
 * @param block A lambda with a receiver of type `IrCallDsl` to define the IR function access expression.
 * @return The constructed `IrFunctionAccessExpression`.
 */
fun DeclarationIrBuilder.getCall(pluginContext: IrPluginContext, block: IrCallDsl.() -> IrFunctionAccessExpression): IrFunctionAccessExpression {
    return IrCallDsl(this, pluginContext).block()
}

/**
 * Creates a new `IrField` with the specified properties and an initializer expression.
 *
 * @receiver The `IrPluginContext` used to create the field.
 * @param parentSymbol The parent symbol to which the field belongs.
 * @param fieldName The name of the field to be created.
 * @param isFinal Whether the field is final (default is `true`).
 * @param isStatic Whether the field is static (default is `true`).
 * @param initializerBlock A lambda defining the initializer expression for the field.
 * @return The created `IrField` with the specified properties and initializer.
 */
fun IrPluginContext.createField(
    parentSymbol: IrSymbol,
    fieldName: String,
    isFinal: Boolean = true,
    isStatic: Boolean = true,
    initializerBlock: IrCallDsl.() -> IrExpression
): IrField {
    val initializerExpression = DeclarationIrBuilder(this, parentSymbol).callExpression(this) {
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
 * A DSL scope for constructing IR call expressions in a fluent and readable manner.
 *
 * @property builder The `IrBuilderWithScope` used to create IR call expressions.
 * @property pluginContext The `IrPluginContext` providing access to the IR plugin environment.
 */
class IrCallDsl(private val builder: IrBuilderWithScope, private val pluginContext: IrPluginContext) {

    /**
     * Constructs an `IrFunctionAccessExpression` by calling the specified function with the given arguments.
     *
     * @param func The function to be called. Can be an `IrFunctionSymbol`, `IrFunction`, `IrProperty`, or a string representing the function (check [findFunction] for the exact signature requirements)
     * @param args The arguments to be passed to the function.
     * @return The constructed `IrFunctionAccessExpression` representing the function call.
     * @throws IllegalArgumentException If the number of arguments does not match the number of non-default parameters of the function.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun call(func: Any, vararg args: Any): IrFunctionAccessExpression {
        val function = extractFunctionSymbol(func)

        val nonDefaultParameters = function.owner.valueParameters.filter { !it.hasDefaultValue() }
        require(nonDefaultParameters.size == args.size) {"Expected ${nonDefaultParameters.size} arguments, got ${args.size}"}

        val newArgs = args.map { convertArgToIrExpression(it) }.toList()

        return builder.irCall(function).apply {
            newArgs.forEachIndexed { index, value -> putValueArgument(index, value) }
        }
    }

    /**
     * Calls the specified function with the given arguments using this `IrSymbol` as the receiver (checks the function signature if it should be used as extension- or dispatch receiver).
     *
     * @param func The function to be called. Can be an `IrFunctionSymbol`, `IrFunction`, `IrProperty`, or a string representing the function (check [findFunction] for the exact signature requirements)
     *             The function will be searched in the irSymbol this method is called on.
     * @param args The arguments to be passed to the function.
     * @return An `IrFunctionAccessExpression` representing the function call with the receiver set.
     * @throws IllegalArgumentException If the function cannot be resolved or the arguments are invalid.
     */
    fun IrSymbol.call(func: Any, vararg args: Any): IrFunctionAccessExpression {
        val functionCall: IrFunctionAccessExpression = if (func is String) {
            this@IrCallDsl.call(findFunction(func, this), *args)
        } else {
            this@IrCallDsl.call(func, *args)
        }
        val receiver = this.convertReceiverToIrExpression()
        return functionCall.setReceivers(receiver)
    }

    /**
     * Calls a function with the given arguments using a pair of `IrSymbol`s as receivers.
     *
     * This function is designed for a specific use case where both extension- and dispatch receiver need to be set.
     * The first symbol is expected to be the
     * dispatch receiver, and the function being called must belong to the dispatch receiver. Additionally,
     * the function must have an extension receiver of the type represented by the second symbol.
     *
     * @receiver A `Pair` of `IrSymbol`s, where the first symbol is the dispatch receiver and the second
     *           symbol is the extension receiver.
     * @param func The function to be called. Can be an `IrFunctionSymbol`, `IrFunction`, `IrProperty`, or a string representing the function (check [findFunction] for the exact signature requirements)
     *             The function will be searched in the dispatch receiver.
     * @param args The arguments to be passed to the function.
     * @return An `IrFunctionAccessExpression` representing the function call with the receivers set.
     * @throws IllegalArgumentException If the function cannot be resolved or the arguments are invalid.
     */
    fun Pair<IrSymbol, IrSymbol>.call(func: Any, vararg args: Any): IrFunctionAccessExpression {
        //this is a very specific case: we expect the first symbol to be the dispatch receiver and the function in this case can only be found in the dispatch receiver
        //furthermore, must the searched function have an extension receiver of the second symbol type
        val functionCall: IrFunctionAccessExpression = if (func is String) {
            this@IrCallDsl.call(findFunction(func, this.first, this.second.extractType()), *args)
        } else {
            this@IrCallDsl.call(func, *args)
        }

        return functionCall.setReceivers(this.first.convertReceiverToIrExpression(), this.second.convertReceiverToIrExpression())
    }

    /**
     * Method for chaining multiple irCalls together.
     *
     * @receiver The `IrFunctionAccessExpression` on which the function call is performed.
     * @param func The function to be called. Can be an `IrFunctionSymbol`, `IrFunction`, `IrProperty`, or a string representing the function (check [findFunction] for the exact signature requirements)
     * @param args The arguments to be passed to the function.
     * @return The modified `IrFunctionAccessExpression` with the receivers set.
     * @throws IllegalArgumentException If the function cannot be resolved or the arguments are invalid.
     */
    fun IrFunctionAccessExpression.call(func: Any, vararg args: Any): IrFunctionAccessExpression {
        return if (func is String) {
            val params = args.joinToString(separator = ", ", prefix = "(", postfix = ")") { extractType(it) }
            val funcSymbol = pluginContext.findFunction(func + params, this.type)
                ?: throw IllegalArgumentException("Function $func not found with params $params")
            this@IrCallDsl.call(funcSymbol, *args)
        } else {
            this@IrCallDsl.call(func, *args)
        }.setReceivers(this)
    }

    /**
     * Calls the specified function with the given arguments using this `IrDeclaration` as the receiver.
     *
     * @receiver The `IrDeclaration` used as the receiver for the function call.
     * @param func The function to be called. Can be an `IrFunctionSymbol`, `IrFunction`, `IrProperty`,
     *             or a string representing the function name (parameters signature will be calculated dynamically by the passed parameters).
     * @param args The arguments to be passed to the function.
     * @return An `IrFunctionAccessExpression` representing the function call with the receiver set.
     * @throws IllegalArgumentException If the function cannot be resolved or the arguments are invalid.
     */
    fun IrDeclaration.call(func: Any, vararg args: Any) = this.symbol.call(func, *args)

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
     * Invokes the constructor of the `IrClass` using the provided arguments.
     *
     * @receiver The `IrClass` whose constructor is to be invoked.
     * @param args The arguments to be passed to the constructor.
     * @return An `IrFunctionAccessExpression` representing the constructor call.
     * @throws IllegalArgumentException If the constructor cannot be resolved or the arguments are invalid.
     */
    operator fun IrClass.invoke(vararg args: Any) = this.symbol(*args)

    /**
     * Invokes the constructor of the `IrClass` represented by this `IrClassSymbol` with the provided arguments.
     *
     * @receiver The `IrClassSymbol` representing the class whose constructor is to be invoked.
     * @param args The arguments to be passed to the constructor. The constructor signature will be calculated dynamically based on the types of the provided arguments, so they must match exactly.
     * @return An `IrFunctionAccessExpression` representing the constructor call.
     * @throws IllegalArgumentException If the constructor cannot be resolved or the arguments are invalid.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    operator fun IrClassSymbol.invoke(vararg args: Any): IrFunctionAccessExpression {
        val params = args.joinToString(separator = ", ", prefix = "(", postfix = ")") { extractType(it) }
        return this.findConstructor(pluginContext, params)?.invoke(*args)
            ?: throw IllegalArgumentException("IrCallHelper: Constructor wit params: $params not found in class ${this.owner.name}")
    }

    /**
     * Constructs an `IrFunctionAccessExpression` to print a value using the `println` function.
     *
     * @param pluginContext The `IrPluginContext` used to find the `println` function symbol.
     * @param value The value to be printed. Its type is determined dynamically.
     * @return An `IrFunctionAccessExpression` representing the call to the `println` function.
     *         If the specific println function for the value type is not found, it falls back to println(any?).
     */
    fun irPrintLn(pluginContext: IrPluginContext, value: Any): IrFunctionAccessExpression {
        val paramType = extractType(value)
        val printMethod = pluginContext.findFunction("kotlin/io/println($paramType)")
            ?: pluginContext.findFunction("kotlin/io/println(any?)")!!

        return printMethod(value)
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
            concat.addArgument(convertArgToIrExpression(param))
        }
        return concat
    }

    /**
     * Converts the given value into an `IrExpression`.
     *
     * Supported types:
     * - Primitive types (`Boolean`, `Byte`, `Short`, `Int`, `Long`, `Float`, `Double`, `Char`)
     * - `String`
     * - `IrCall`, `IrCallImpl`
     * - `IrFunctionAccessExpression`
     * - `IrFunction`
     * - `IrProperty`
     * - `IrField`
     * - `IrValueParameter`
     * - `IrVariable`
     * - `IrClass`
     * - `IrClassSymbol`
     * - `IrValueDeclaration`
     * - `IrStringConcatenation`
     * - `IrConst<*>`
     * - `IrExpression`
     *
     * @param value The value to be converted into an `IrExpression`.
     * @return The corresponding `IrExpression`.
     * @throws IllegalArgumentException If the given value cannot be converted.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun convertArgToIrExpression(value: Any?): IrExpression {
        if (value == null) return builder.irNull()

        var toProcess = value
        if(value is IrSymbol) {
            toProcess = value.owner
        }

        return when (toProcess) {
            is Boolean -> builder.irBoolean(toProcess)
            is Byte -> builder.irByte(toProcess)
            is Short -> builder.irShort(toProcess)
            is Int -> builder.irInt(toProcess)
            is Long -> builder.irLong(toProcess)
            is Float -> toProcess.toIrConst(pluginContext.irBuiltIns.floatType)
            is Double -> toProcess.toIrConst(pluginContext.irBuiltIns.doubleType)
            is Char -> builder.irChar(toProcess)
            is String -> builder.irString(toProcess)

            is IrCallImpl -> toProcess
            is IrCall -> builder.irCall(toProcess.symbol)
            is IrFunctionAccessExpression -> toProcess
            is IrFunction -> builder.irCall(toProcess)
            is IrProperty -> builder.irCall(toProcess.getter ?: error("IrCallHelper-convertToIrExpression: Property has no getter"))
            is IrField -> {
                if (toProcess.parent !is IrFile) {
                    throw IllegalStateException("IrCallHelper: Only top-level fields are supported here")
                }
                builder.irGetField(null, toProcess)
            }
            is IrValueParameter -> builder.irGet(toProcess)
            is IrVariable -> builder.irGet(toProcess)
            is IrClass -> builder.irGetObject(toProcess.symbol)
            is IrValueDeclaration -> builder.irGet(toProcess)
            is IrStringConcatenation -> return toProcess
            is IrConst<*> -> toProcess
            is IrExpression -> toProcess

            else -> error("IrCallHelper-convertToIrExpression: Cannot convert $toProcess to IrExpression")
        }
    }

    /**
     * Extracts the `IrFunctionSymbol` from the given function representation.
     *
     * @param func The function to extract the symbol from. Can be one of the following:
     *             - `IrFunctionSymbol`: The symbol is returned directly.
     *             - `IrFunction`: The symbol of the function is returned.
     *             - `IrProperty`: The symbol of the property's getter is returned.
     *             - `IrPropertySymbol`: The symbol of the property's getter is returned.
     *             - `String`: The function is resolved dynamically using its name and arguments.
     * @return The extracted `IrFunctionSymbol`.
     * @throws IllegalArgumentException If the function type is unsupported or a getter is missing for a property.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun extractFunctionSymbol(func: Any) =
        when (func) {
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
            is String -> findFunction(func)
            else -> throw IllegalArgumentException("IrCallHelper: Unsupported function type: ${func::class.simpleName}")
        }

    /**
     * Finds a function symbol based on the provided signature, symbol, and arguments.
     *
     * @param symbol An optional `IrSymbol` representing the context in which the function is searched.
     *               If provided, the function is searched within the class or type represented by this symbol.
     * @param funcSignature The name of the function to find.
     *                      If no symbol is provided, this should include the package and class name (e.g., "package/class.function").
     *                      Parameters are expected after the function name, e.g., "function(int, param2Type)".
     *                      "*" can be used as parameter if from here the parameters are arbitrary. It can only be used once and only at the end of the signature.
     *                      "G" can be used as a placeholder for generic parameters.
     * @param extensionReceiverType An optional `IrType` representing the type of the extension receiver, if applicable.
     * @return The `IrFunctionSymbol` representing the found function.
     * @throws IllegalArgumentException If the function cannot be found or the symbol type is unsupported.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun findFunction(funcSignature: String, symbol: IrSymbol? = null, extensionReceiverType: IrType? = null): IrFunctionSymbol {
        val function = if(symbol != null) {
            val irClass = when (symbol) {
                is IrClassSymbol -> symbol.owner
                is IrVariableSymbol -> symbol.owner.type.getClass()
                is IrValueSymbol -> symbol.owner.type.getClass()
                is IrFieldSymbol ->  symbol.owner.type.getClass()
                is IrPropertySymbol -> symbol.owner.getter?.returnType?.getClass() ?: throw IllegalArgumentException("IrCallHelper: Property ${symbol.owner.name} does not have a getter")
                else -> throw IllegalArgumentException("Unsupported symbol type: ${symbol::class.simpleName}")
            } ?: throw IllegalArgumentException("Could not resolve class from symbol")

            irClass.symbol.findFunction(pluginContext, funcSignature, extensionReceiverType ?: irClass.defaultType)
        } else {
            if(!funcSignature.contains(".") && funcSignature.substringAfterLast("/").get(0).isUpperCase()) {
                pluginContext.findConstructor(funcSignature)
            } else {
                pluginContext.findFunction(funcSignature, extensionReceiverType)
            }
        }
        return function ?: throw IllegalArgumentException("IrCallHelper: Function $funcSignature not found!")
    }

    /**
     * Converts an `IrSymbol` into the corresponding `IrExpression` receiver.
     *
     * This function determines the type of the `IrSymbol` and returns the corresponding
     * `IrExpression` representing the receiver. It supports various symbol types such as
     * properties, fields, values, classes, constructors, and functions.
     *
     * @receiver The `IrSymbol` from which the receiver is extracted.
     * @return The extracted `IrExpression` representing the receiver.
     * @throws IllegalArgumentException If the symbol type is not supported or a required getter is missing.
     * @throws IllegalStateException If a field that is not at the top level is encountered.
     */
    private fun IrSymbol.convertReceiverToIrExpression(): IrExpression = when (this) {
        is IrPropertySymbol,
        is IrFieldSymbol,
        is IrValueSymbol,
        is IrClassSymbol,
        is IrConstructorSymbol,
        is IrSimpleFunctionSymbol -> convertArgToIrExpression(this)
        else -> throw IllegalArgumentException("IrCallHelper: Unsupported symbol type: ${this::class.simpleName}")
    }

    /**
     * Extracts the `IrType` associated with the given `IrSymbol`.
     *
     * This function determines the type of the symbol based on its specific kind, such as property, field, value, class,
     * constructor, or function. If the symbol type is unsupported or a required getter is missing, an exception is thrown.
     *
     * @receiver The `IrSymbol` whose type is to be extracted.
     * @return The `IrType` associated with the symbol.
     * @throws IllegalArgumentException If the symbol type is unsupported or a required getter is missing.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrSymbol.extractType(): IrType = when (this) {
        is IrPropertySymbol -> {
            owner.getter?.returnType
                ?: throw IllegalArgumentException("IrCallHelper: Property ${owner.name} does not have a getter")
        }
        is IrFieldSymbol -> owner.type
        is IrValueSymbol -> owner.type
        is IrClassSymbol -> owner.defaultType
        is IrConstructorSymbol -> owner.returnType
        is IrSimpleFunctionSymbol -> owner.returnType
        else -> throw IllegalArgumentException("IrCallHelper: Unsupported symbol type: ${this::class.simpleName}")
    }

    /**
     * Sets the dispatch and extension receivers for this `IrFunctionAccessExpression`.
     *
     * @param r1 The primary receiver to be set as the dispatch receiver if applicable.
     * @param r2 The secondary receiver to be set as the extension receiver if applicable.
     *           If `r2` is null, `r1` will be used as the extension receiver.
     * @return The modified `IrFunctionAccessExpression` with the receivers set.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrFunctionAccessExpression.setReceivers(r1: IrExpression, r2: IrExpression? = null) = this.apply{
        dispatchReceiver = if (this.symbol.owner.dispatchReceiverParameter != null) {
            r1
        } else null
        extensionReceiver = if (this.symbol.owner.extensionReceiverParameter != null) {
            r2 ?: r1
        } else null
    }
}