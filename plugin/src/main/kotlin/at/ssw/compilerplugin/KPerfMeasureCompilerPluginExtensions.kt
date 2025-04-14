package at.ssw.compilerplugin

import groovy.lang.Tuple4
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.util.collectionUtils.concat

//#region find
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.findClass(name: String): IrClassSymbol {
    val classId = ClassId.fromString(name)
    return this.referenceClass(classId) ?: this.referenceTypeAlias(classId)?.owner?.expandedType?.classOrNull ?: error("class not found: $name")
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun IrPluginContext.findIrType(name: String): IrType {
    val classId = ClassId.fromString(name)
    return (this.referenceClass(classId)?.defaultType ?: this.referenceTypeAlias(classId)?.owner?.expandedType!!)
}

private const val groupFqMethod = 1
private const val groupPackage = 2
private const val groupClass = 3
private const val groupMethod = 4
private const val groupParameters = 5
private const val groupReturnType = 6
private val regexFun by lazy { Regex("""((?:((?:[a-zA-Z]\w*\/)*[a-zA-Z]\w*)\/)?(?:((?:[a-zA-Z]\w*\.)*[a-zA-Z]\w*)\.)?([a-zA-Z][\w<>]*))(?:\(((?:\s*(?:[a-zA-Z]\w*\:\s*)?[a-zA-Z][\w<>\.\/]*\??|\*)(?:\s*,\s*(?:[a-zA-Z]\w*\:\s*)?(?:[a-zA-Z][\w<>\.\/]*\??|\*)\s*)*)*\)(?:\s*:\s*((?:((?:[a-zA-Z]\w*[\.\/])*[a-zA-Z]\w*)[\.\/])?(?:[a-zA-Z][\w<>]*)))?)?""") }

private fun IrPluginContext.tryFindIrType(parameterType: String): IrType {
    val qm = '?'
    val isNullable = parameterType.endsWith(qm)
    val typeName = if (isNullable) parameterType.trimEnd(qm) else parameterType
    val result = when (typeName) {
        "" -> this.irBuiltIns.anyNType
        "Null" -> this.irBuiltIns.anyNType
        "Any" -> this.irBuiltIns.anyType
        "Boolean" -> this.irBuiltIns.booleanType
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
    if (match != null) {
        val size = match.groups.count { it != null }
        if (size >= groupMethod) {
            val methodName = match.groups[groupMethod]?.value
            if (!methodName.isNullOrEmpty()) {
                var existingFunctionReferences: Sequence<IrFunctionSymbol> = findReferences(match, methodName)
                if (existingFunctionReferences.any()) {
                    val parametersStrings = match.groups[groupParameters]?.value?.split(',')?.map {
                        val index = it.indexOf(':')
                        if (index >= 0) {
                            Pair(it.substring(0, index).trim(), it.substring(index + 2).trim())
                        } else {
                            Pair(null, it.trim())
                        }
                    }?.toList()

                    if (parametersStrings != null) {
                        val nix = context.irBuiltIns.anyNType

                        if (parametersStrings.any()) {
                            val star = "*"
                            if (parametersStrings.size != 1 || parametersStrings[0].second != star) {
                                val (requiredTypes, requiredTypesPerName) = parametersStrings.mapIndexed { index, (argumentName, typeName) ->
                                    val type: IrType?
                                    val tName: String?
                                    if (typeName == star) {
                                        type = null
                                        tName = typeName
                                    } else {
                                        type = context.tryFindIrType(typeName)
                                        tName = null
                                    }
                                    Tuple4(index, argumentName, tName, type)
                                }.partition { it.v2 == null }

                                val NONE = 0
                                val STAR = 1
                                val ANY = 2
                                val SUBTYPE = 4
                                val EXACT = 8

                                fun typeCheck(requiredType: IrType, existingFunctionParameter: IrValueParameter) =
                                    (if (existingFunctionParameter == requiredType || existingFunctionParameter.type == requiredType.type) EXACT else NONE) or
                                    (if (requiredType.type.isSubtypeOfClass(existingFunctionParameter.type.classOrNull!!)) SUBTYPE else NONE) or
                                    (if (existingFunctionParameter.type == nix) ANY else NONE)

                                fun typeCheck(requiredTypeEntry: Tuple4<Int, String?, String?, IrType?>, existingFunctionParameter: IrValueParameter) = if (requiredTypeEntry.v3 == star) STAR else if (requiredTypeEntry.v4 == null) NONE else typeCheck(requiredTypeEntry.v4!!, existingFunctionParameter)

                                var existingFunctionReferenceValueParameters = existingFunctionReferences.map { existingFunctionReference ->
                                    val existingValueParameters = existingFunctionReference.owner.valueParameters.mapIndexed { index, p -> Triple(index, p, NONE) }
                                    Triple(existingFunctionReference, existingValueParameters, emptyList<Triple<Int, IrValueParameter, Int>>())
                                }

                                if (requiredTypesPerName.any()) {
                                    check(requiredTypesPerName.groupBy { it.v2 }.all { it.value.size <= 1 }) { "ambiguous parameter name: ${requiredTypesPerName.groupBy { it.v2 }.filter { it.value.size > 1 }}" }

                                    val requiredNames = requiredTypesPerName.map { it.v2!! }.toList()

                                    existingFunctionReferenceValueParameters = existingFunctionReferenceValueParameters.map { (existingFunctionReference, existingValueParameters) ->
                                        val (existingHasName, existingHasNoName) = existingValueParameters
                                            .map { p -> Pair(p, requiredNames.contains(p.second.name.asString())) }
                                            .partition { it.second }
                                        Triple(existingFunctionReference, existingHasNoName.map { it.first }, existingHasName.map { it.first })
                                    }

                                    val existingHasAllNames = existingFunctionReferenceValueParameters.filter { (_, _, existingHasName) ->  existingHasName.count() == requiredNames.count() }

                                    check(existingHasAllNames.any()) {
                                        val msg = "argument name not found"
                                        if (existingFunctionReferenceValueParameters.any()) {
                                            "$msg: ${requiredNames - existingFunctionReferenceValueParameters.sortedByDescending { it.second.count() }.first().second.map { it.second.name }.toSet()}"
                                        } else {
                                            msg
                                        }
                                    }

                                    /// Assign and compare types based on parameter names
                                    existingFunctionReferenceValueParameters = existingHasAllNames.map { (existingFunctionReference, existingHasNoName, existingHasName) ->
                                        val existingHasNameDerivationLevel = existingHasName.map { (index, existingValueParameter) ->
                                            val requiredTypeEntry = requiredTypesPerName.single { it.v2 == existingValueParameter.name.asString() }
                                            Triple(index, existingValueParameter, typeCheck(requiredTypeEntry, existingValueParameter))
                                        }
                                        Triple(existingFunctionReference, existingHasNoName, existingHasNameDerivationLevel)
                                    }.filter { (existingFunctionReference, existingHasNoName, existingHasNameDerivationLevel) ->
                                        existingHasNameDerivationLevel.all { (index, existingValueParameter, derivationLevel) -> derivationLevel != NONE }
                                    }
                                }

                                var existingFunctionReferenceValueParametersList = existingFunctionReferenceValueParameters.map {
                                    val (existingFunctionReference, existingHasNoName, existingHasName) = it
                                    val existingFunctionParametersIt = existingHasNoName.iterator()
                                    if (existingFunctionParametersIt.hasNext()) {
                                        val existingHasNoNameFilter = mutableListOf<Triple<Int, IrValueParameter, Int>>()

                                        val requiredTypesIt = requiredTypes.iterator()
                                        loopRequiredTypes@ while (requiredTypesIt.hasNext()) {
                                            var entry = requiredTypesIt.next()
                                            var wildcard = entry.v3
                                            var requiredType = entry.v4!!
                                            val isStar = wildcard == star

                                            while (wildcard == star && requiredTypesIt.hasNext()) {
                                                entry = requiredTypesIt.next()
                                                wildcard = entry.v3
                                                requiredType = entry.v4!!
                                            }

                                            var derivationLevel = NONE
                                            var index = 0
                                            var existingValueParameter: IrValueParameter? = null
                                            loopCheckExisting@ while (existingFunctionParametersIt.hasNext()) {
                                                val existingFunctionParameter = existingFunctionParametersIt.next()
                                                index = existingFunctionParameter.first
                                                existingValueParameter = existingFunctionParameter.second

                                                derivationLevel = typeCheck(requiredType, existingValueParameter)
                                                if (derivationLevel != NONE) {
                                                    existingHasNoNameFilter.add(Triple(index, existingValueParameter, derivationLevel))
                                                    continue@loopRequiredTypes
                                                // Wildcards and optional parameters
                                                } else if (isStar || existingValueParameter.defaultValue != null) {
                                                    existingHasNoNameFilter.add(Triple(index, existingValueParameter, STAR))
                                                    continue@loopCheckExisting
                                                } else {
                                                    return@map Pair(false, it)
                                                }
                                            }

                                            if (isStar && !requiredTypesIt.hasNext()) {
                                                while (existingFunctionParametersIt.hasNext()) {
                                                    val existingFunctionParameter = existingFunctionParametersIt.next()
                                                    existingHasNoNameFilter.add(Triple(existingFunctionParameter.first, existingFunctionParameter.second, STAR))
                                                }
                                                return@map Pair(true, Triple(existingFunctionReference, existingHasNoNameFilter, existingHasName))
                                            }

                                            return@map Pair(false, it)
                                        }

                                        if (existingFunctionParametersIt.hasNext()) {
                                            return@map Pair(false, it)
                                        } else {
                                            return@map Pair(true, Triple(existingFunctionReference, existingHasNoNameFilter, existingHasName))
                                        }
                                    }

                                    if (requiredTypes.all { it.v3 == star }) {
                                        return@map Pair(true, Triple(existingFunctionReference, existingHasNoName.map { (index, existingValueParameter) -> Triple(index, existingValueParameter, STAR) }, existingHasName))
                                    } else {
                                        return@map Pair(false, it)
                                    }
                                }
                                    .filter { it.first }
                                    .map { it.second }
                                    .toList()

                                if (existingFunctionReferenceValueParametersList.size >= 2) {
                                    for (mask in arrayOf(EXACT, EXACT or SUBTYPE, EXACT or SUBTYPE or ANY, EXACT or SUBTYPE or ANY or STAR)) {
                                        val result = existingFunctionReferenceValueParametersList.filter { (_, existingHasNoName, existingHasName) -> existingHasNoName.concat(existingHasName)!!.all { it.third == mask } }.toList()
                                        if (result.any()) {
                                            existingFunctionReferenceValueParametersList = result
                                            break
                                        }
                                    }
                                }

                                existingFunctionReferences = existingFunctionReferenceValueParametersList.map { it.first }.asSequence()
                            }

                            if (size >= groupReturnType) {
                                val returnTypeName = match.groups[groupReturnType]?.value
                                if (!returnTypeName.isNullOrEmpty()) {
                                    val returnType = context.findClass(returnTypeName).defaultType
                                    existingFunctionReferences = existingFunctionReferences.filter { it.owner.returnType == returnType }
                                }
                            }

                            if (existingFunctionReferences.take(2).count() >= 2) {
                                val pain = existingFunctionReferences.map { Pair(it, it.owner.valueParameters.count { p -> p.type == nix }) }
                                val min = pain.map { p -> p.second }.min()
                                existingFunctionReferences = pain
                                    .filter { p -> p.second == min }
                                    .map { p -> p.first }
                            }
                        }
                    } else {
                        existingFunctionReferences = existingFunctionReferences.filter { !it.owner.valueParameters.any() }
                    }

                    if (size >= groupReturnType) {
                        val returnTypeName = match.groups[groupReturnType]?.value
                        if (!returnTypeName.isNullOrEmpty()) {
                            val returnType = context.tryFindIrType(returnTypeName)
                            existingFunctionReferences = existingFunctionReferences.filter { it.owner.returnType == returnType }
                        }
                    }

                    val count = existingFunctionReferences.take(2).count()
                    if (count > 0) {
                        check(count <= 1) { "ambiguous ($count) function signature: $name" }
                        return existingFunctionReferences.single()
                    }
                }
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
    if (match != null && match.groups.count { it != null } >= groupMethod) {
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
fun IrBuilderWithScope.convert(pluginContext: IrPluginContext, parameter : Any?) : IrExpression? {
    if (parameter == null) {
        return null
    }

    val c: IrType
    when (parameter) {
        is Boolean -> c = pluginContext.irBuiltIns.booleanType
        is Byte -> c = pluginContext.irBuiltIns.byteType
        is Short -> c = pluginContext.irBuiltIns.shortType
        is Int -> c = pluginContext.irBuiltIns.intType
        is Long -> c = pluginContext.irBuiltIns.longType
        is Float -> c = pluginContext.irBuiltIns.floatType
        is Double -> c = pluginContext.irBuiltIns.doubleType
        is Char -> c = pluginContext.irBuiltIns.charType

        is String -> return irString(parameter)

        is IrCallImpl -> return parameter

        is IrCall -> return irCall(parameter.symbol)
        is IrFunction -> return irCall(parameter)
        is IrProperty -> return irCall(parameter.getter!!)
        is IrField -> return irGetField(null, parameter)
        is IrValueParameter -> return irGet(parameter)
        is IrVariable -> return irGet(parameter)
        is IrClassSymbol -> return irGetObject(parameter)

        is IrStringConcatenation -> return parameter
        is IrFunctionAccessExpression -> return parameter
        is IrGetObjectValue -> return parameter
        is IrGetField -> return parameter
        is IrConst<*> -> return parameter

        else -> throw NotImplementedError("for ${parameter.javaClass.name}")
    }

    return parameter.toIrConst(c)
}

fun IrBuilderWithScope.call(pluginContext: IrPluginContext, function: IrFunction, receiver : Any?, vararg parameters : Any?): IrFunctionAccessExpression {
    val expectedValueParameters = function.valueParameters
    assert(expectedValueParameters.size == parameters.size || (parameters.size <= expectedValueParameters.size && parameters.size >= (expectedValueParameters.size - expectedValueParameters.count { p -> p.defaultValue != null })), { "parameter count (${parameters.size}) is not equal to function parameter size (${expectedValueParameters.size})" })

    return irCall(function).apply {
        var index = 0
        for ((actualParameter, expectedIrValueParameter) in parameters.zip(expectedValueParameters)) {
            putValueArgument(index++, convert(pluginContext, actualParameter))
            continue

            // Todo:
            // In the case of optional parameters, search for suitable method signatures.
            val actualType = if (actualParameter == null) Any::class else actualParameter::class
            val actualTypeName = actualType.qualifiedName
            val expectedType = expectedIrValueParameter.type.classFqName?.asString()

            if (expectedType == actualTypeName || actualType == expectedIrValueParameter::class) {
                putValueArgument(index++, convert(pluginContext, actualParameter))
            } else {
                throw IllegalArgumentException("parameter type (${actualTypeName}) is not equal to function parameter type (${expectedType})")
            }
        }

        if (function.dispatchReceiverParameter != null) {
            dispatchReceiver = convert(pluginContext, receiver)
        } else if (function.extensionReceiverParameter != null) {
            extensionReceiver = convert(pluginContext, receiver)
        }
    }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrBuilderWithScope.call(context: IrPluginContext, functionSymbol : IrFunctionSymbol, receiver : Any?, vararg parameters : Any?) = call(context, functionSymbol.owner, receiver, *parameters)
//#endregion

//#region concat
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