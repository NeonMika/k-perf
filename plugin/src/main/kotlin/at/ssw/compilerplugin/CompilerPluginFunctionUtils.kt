package at.ssw.compilerplugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.symbols.*
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
 * A DSL scope for building IR call expressions in a fluent manner.
 *
 * @property builder The IrBuilderWithScope used to construct the IR call
 *                    expressions.
 */
class IrCallDsl(private val builder: IrBuilderWithScope, private val pluginContext: IrPluginContext) {

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun call(func: Any, vararg args: Any): IrFunctionAccessExpression {
        val function = extractFunctionSymbol(func, *args)

        val nonDefaultParameters = function.owner.valueParameters.filter { !it.hasDefaultValue() }
        if(nonDefaultParameters.size != args.size) {
            throw IllegalArgumentException("Expected ${nonDefaultParameters.size} arguments, got ${args.size}")
        }

        val newArgs = args.map { builder.convertToIrExpression(it) }.toList()

        return builder.irCall(function).apply {
            newArgs.forEachIndexed { index, value -> putValueArgument(index, value) }
        }
    }

    fun IrSymbol.call(func: Any, vararg args: Any): IrFunctionAccessExpression {
        //TODO: test setting dispatch receiver
        val functionCall: IrFunctionAccessExpression = if (func is String) {
            this@IrCallDsl.call(findFunction(this, func, *args), *args)
        } else {
            this@IrCallDsl.call(func, *args)
        }
        val receiver = this.extractReceiver()
        return functionCall.setReceivers(receiver)
    }

    fun Pair<IrSymbol, IrSymbol>.call(func: Any, vararg args: Any): IrFunctionAccessExpression {
        //TODO: test this
        val functionCall: IrFunctionAccessExpression = if (func is String) {
            try {
                this@IrCallDsl.call(findFunction(this.first, func, *args), *args)
            } catch (_: Exception) {
                this@IrCallDsl.call(findFunction(this.second, func, *args), *args)
            }
        } else {
            this@IrCallDsl.call(func, *args)
        }

        return functionCall.setReceivers(this.first.extractReceiver(), this.second.extractReceiver())
    }

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

    operator fun IrClass.invoke(vararg args: Any) = this.symbol(*args)

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
            concat.addArgument(builder.convertToIrExpression(param))
        }
        return concat
    }

    private fun extractFunctionSymbol(func: Any, vararg args: Any) =
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
            is String -> findFunction(null, func, *args)
            else -> throw IllegalArgumentException("IrCallHelper: Unsupported function type: ${func::class.simpleName}")
        }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun findFunction(symbol: IrSymbol?, funcSignature: String, vararg args: Any): IrFunctionSymbol {
        val params = args.joinToString(separator = ", ", prefix = "(", postfix = ")") { extractType(it) }
        val function = if(symbol != null) {
            val irClass = when (symbol) {
                is IrClassSymbol -> symbol.owner
                is IrVariableSymbol -> symbol.owner.type.getClass()
                is IrValueSymbol -> symbol.owner.type.getClass()
                is IrFieldSymbol ->  symbol.owner.type.getClass()
                is IrPropertySymbol -> symbol.owner.getter?.returnType?.getClass() ?: throw IllegalArgumentException("IrCallHelper: Property ${symbol.owner.name} does not have a getter")
                else -> throw IllegalArgumentException("Unsupported symbol type: ${symbol::class.simpleName}")
            } ?: throw IllegalArgumentException("Could not resolve class from symbol")

            irClass.symbol.findFunction(pluginContext, funcSignature + params, irClass.defaultType)
        } else {
            if(!funcSignature.contains(".") && funcSignature.substringAfterLast("/").get(0).isUpperCase()) {
                pluginContext.findConstructor(funcSignature + params)
            } else {
                pluginContext.findFunction(funcSignature + params)
            }
        }
        return function ?: throw IllegalArgumentException("IrCallHelper: Function $funcSignature with params $params not found!")
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrSymbol.extractReceiver() = when (this) {
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
        is IrConstructorSymbol -> builder.irCall(this)
        is IrSimpleFunctionSymbol -> builder.irCall(this)
        //restrict - yes probably with generics -> the best way to do this in kotlin
        else -> throw IllegalArgumentException("IrCallHelper: Unsupported symbol type: ${this::class.simpleName}")
    }

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