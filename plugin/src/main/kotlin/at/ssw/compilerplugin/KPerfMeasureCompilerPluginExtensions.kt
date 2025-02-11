package at.ssw.compilerplugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrStringConcatenationImpl
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

//#region find
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.findClass(name: String): IrClassSymbol {
    val classId = ClassId.fromString(name)
    return this.referenceClass(classId) ?: this.referenceTypeAlias(classId)?.owner?.expandedType?.classOrNull ?: error("class not found: $name")
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun IrPluginContext.findIrType(name: String): IrType {
    val classId = ClassId.fromString(name)
    return (this.referenceClass(classId) ?: this.referenceTypeAlias(classId)?.owner?.expandedType) as IrType
}

private const val groupFqMethod = 1
private const val groupPackage = 2
private const val groupClass = 3
private const val groupMethod = 4
private const val groupParameters = 5
private const val groupReturnType = 6
private val regexFun by lazy { Regex("""((?:((?:[a-zA-Z]\w*\/)*[a-zA-Z]\w*)\/)?(?:((?:[a-zA-Z]\w*\.)*[a-zA-Z]\w*)\.)?([a-zA-Z][\w<>]*))(?:[(]([\w<>\.\/]+\??(?:,\s*[\w<>\.\/]+\??)*)*[)](?:\s*:\s*((?:(?:(?:[a-zA-Z]\w*[\.\/])*[a-zA-Z]\w*)[\.\/])?(?:[a-zA-Z][\w<>]*)))?)?""") }

private fun IrPluginContext.tryFindIrType(parameterType: String): IrType {
    val qm = '?'
    val isNullable = parameterType.endsWith(qm)
    val typeName = if (isNullable) parameterType.trimEnd(qm) else parameterType
    val result = when (typeName) {
        "Any" -> this.irBuiltIns.anyNType
        "Byte" -> this.irBuiltIns.byteType
        "Short" -> this.irBuiltIns.shortType
        "Int" -> this.irBuiltIns.intType
        "Long" -> this.irBuiltIns.longType
        "Float" -> this.irBuiltIns.floatType
        "Double" -> this.irBuiltIns.doubleType
        "Char" -> this.irBuiltIns.charType
        "String" -> this.irBuiltIns.stringType

        "CharArray" -> this.irBuiltIns.charArray.defaultType
        "ByteArray" -> this.irBuiltIns.byteArray.defaultType
        "ShortArray" -> this.irBuiltIns.shortArray.defaultType
        "IntArray" -> this.irBuiltIns.intArray.defaultType
        "LongArray" -> this.irBuiltIns.longArray.defaultType
        "FloatArray" -> this.irBuiltIns.floatArray.defaultType
        "DoubleArray" -> this.irBuiltIns.doubleArray.defaultType
        "BooleanArray" -> this.irBuiltIns.booleanArray.defaultType

        else -> this.findIrType(typeName)
    }

    return if (isNullable) result.makeNullable() else result
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun findReferences(context: IrPluginContext, match: MatchResult, methodName: String, callFun: (CallableId) -> Collection<IrFunctionSymbol>, selector: (IrClassSymbol?) -> Sequence<IrFunctionSymbol>?): Sequence<IrFunctionSymbol> {
    val packageName = match.groups[groupPackage]?.value
    val className = match.groups[groupClass]?.value
    val fqNameClass = if (className == null) null else FqName(className)
    val identifierMethod = Name.identifier(methodName)

    if (packageName.isNullOrEmpty()) {
        return callFun(CallableId(identifierMethod)).asSequence()
    } else {
        val fqNamePackage = FqName(packageName.replace('/', '.'))
        val callableIdMethod = CallableId(fqNamePackage, fqNameClass, identifierMethod)
        val functions = callFun(callableIdMethod)
        if (functions.any()) {
            return functions.asSequence()
        } else {
            // In JVM, StringBuilder is a type alias (see https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/)
            val fullMethodName = match.groups[groupFqMethod]!!.value
            return selector(context.findClass(fullMethodName))?.filter { it.owner.name.asString() == methodName }!!
        }
    }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun find(context: IrPluginContext, name: String, findReferences: (match: MatchResult, methodName: String) -> Sequence<IrFunctionSymbol>): IrFunctionSymbol {
    val match = regexFun.matchEntire(name)
    if (match != null && match.groups.size >= groupMethod) {
        val methodName = match.groups[groupMethod]?.value
        if (!methodName.isNullOrEmpty()) {
            val references: Sequence<IrFunctionSymbol> = findReferences(match, methodName)
            if (references.any()) {
                val requiredTypes = match.groups[groupParameters]?.value?.split(Regex("[\\s,]+"))?.map { typeName -> context.tryFindIrType(typeName) } ?: listOf()

                val nix = context.irBuiltIns.anyNType
                var result = references.filter {
                    val valueParameters = it.owner.valueParameters
                    valueParameters.size == requiredTypes.size && valueParameters.run {
                        size == requiredTypes.size && valueParameters.zip(requiredTypes).all { t ->
                            val existing = t.first
                            val required = t.second
                            existing == required || existing.type == required.type || existing.type == nix
                        }
                    }
                }

                if (match.groups.size >= groupReturnType) {
                    val returnTypeName = match.groups[groupReturnType]?.value
                    if (!returnTypeName.isNullOrEmpty()) {
                        val returnType = context.findClass(returnTypeName).defaultType
                        result = result.filter { it.owner.returnType == returnType }
                    }
                }

                if (result.take(2).count() >= 2) {
                    val pain = result.map { Pair(it, it.owner.valueParameters.count { p -> p.type == nix } ) }
                    val min = pain.map { p -> p.second }.min()
                    result = pain
                        .filter { p -> p.second == min }
                        .map { p -> p.first }
                }

                return result.single()
            }
        }
    }

    error("function not found: $name")
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private val selectorFunctions: (IrClassSymbol?) -> Sequence<IrFunctionSymbol>? = { classSymbol -> classSymbol?.functions}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private val selectorConstructors: (IrClassSymbol?) -> Sequence<IrFunctionSymbol>? = { classSymbol -> classSymbol?.constructors}

fun IrPluginContext.findFunction(name: String): IrSimpleFunctionSymbol {
    return find(this, name) { match, methodName -> findReferences(this, match, methodName, { callableId -> this.referenceFunctions(callableId) }, selectorFunctions) } as IrSimpleFunctionSymbol
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.findFunction(name: String, clazz: IrClassSymbol): IrSimpleFunctionSymbol {
    return find(this, name) { _, methodName -> selectorFunctions(clazz)?.filter { it.owner.name.asString() == methodName }!! } as IrSimpleFunctionSymbol
}

fun IrPluginContext.findConstructor(name: String): IrConstructorSymbol {
    return find(this, name) { match, methodName -> findReferences(this, match, methodName, { callableId -> this.referenceConstructors(ClassId.fromString(callableId.toString())) }, selectorConstructors) } as IrConstructorSymbol
}

fun IrPluginContext.findConstructor(name: String, clazz: IrClassSymbol): IrConstructorSymbol {
    return find(this, name) { _, _ -> selectorConstructors(clazz)!! } as IrConstructorSymbol
}

fun IrPluginContext.findProperty(name: String): IrPropertySymbol {
    val match = regexFun.matchEntire(name)
    if (match != null && match.groups.size >= groupMethod) {
        val nameProperty = match.groups[groupMethod]?.value
        if (!nameProperty.isNullOrEmpty()) {
            val identifierProperty = Name.identifier(nameProperty);
            val namePackage = match.groups[groupPackage]?.value?.replace('/', '.')
            val ci = if (namePackage.isNullOrEmpty()) CallableId(identifierProperty) else CallableId(FqName(namePackage), identifierProperty)
            return this.referenceProperties(ci).single()
        }
    }

    error("property not found: $name")
}

@UnsafeDuringIrConstructionAPI
fun IrClass.findProperty(name: String): IrPropertySymbol = this.properties.single { it.name.asString() == name }.symbol

@UnsafeDuringIrConstructionAPI
fun IrClassSymbol.findProperty(name: String): IrPropertySymbol = this.owner.findProperty(name)
//#endregion

//#region call
fun IrBuilderWithScope.convert(context: IrPluginContext, parameter : Any?) : IrExpression? {
    if (parameter == null) {
        return null
    }

    val c: IrType
    when (parameter) {
        is Boolean -> c = context.irBuiltIns.booleanType
        is Byte -> c = context.irBuiltIns.byteType
        is Short -> c = context.irBuiltIns.shortType
        is Int -> c = context.irBuiltIns.intType
        is Long -> c = context.irBuiltIns.longType
        is Float -> c = context.irBuiltIns.floatType
        is Double -> c = context.irBuiltIns.doubleType
        is Char -> c = context.irBuiltIns.charType
        is String -> return irString(parameter)
        is IrCall -> return irCall(parameter.symbol)
        is IrFunction -> return irCall(parameter)
        is IrField -> return irGetField(null, parameter)
        is IrProperty -> return irCall(parameter.getter!!)
        is IrClassSymbol -> return irGetObject(parameter)
        else -> throw NotImplementedError("for $parameter")
    }

    return parameter.toIrConst(c)
}

fun IrBuilderWithScope.call(context: IrPluginContext, function: IrFunction, receiver : Any?, vararg parameters : Any?): IrFunctionAccessExpression {
    val valueParameters = function.valueParameters
    if (valueParameters.size != parameters.size) {
        throw IllegalArgumentException("parameter count (${parameters.size}) is not equal to function parameter size (${valueParameters.size})")
    }

    return irCall(function).apply {
        var index = valueArgumentsCount
        for ((parameter, irValueParameter) in parameters.zip(valueParameters)) {
            val pType = if (parameter == null) Any::class else parameter::class
            val typeName = pType.qualifiedName
            val vType = irValueParameter.type

            if (vType.classFqName?.asString() == typeName) {
                putValueArgument(index++, convert(context, parameter))
            } else {
                throw IllegalArgumentException("parameter type (${typeName}) is not equal to function parameter type (${vType})")
            }
        }

        if (function.dispatchReceiverParameter != null) {
            dispatchReceiver = convert(context, receiver)
        } else if (function.extensionReceiverParameter != null) {
            extensionReceiver = convert(context, receiver)
        }
    }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrBuilderWithScope.call(context: IrPluginContext, functionSymbol : IrFunctionSymbol, receiver : Any?, vararg parameters : Any?) = call(context, functionSymbol.owner, receiver, *parameters)

fun IrBuilderWithScope.irConcat(context: IrPluginContext, vararg parameters: Any?): IrStringConcatenationImpl {
    return irConcat().apply {
        for (parameter in parameters) {
            val expression = convert(context, parameter)
            if (expression != null) {
                addArgument(expression)
            }
        }
    }
}
//#endregion