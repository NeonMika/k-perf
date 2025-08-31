package at.ssw.compilerplugin

import groovy.lang.Tuple4
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.DescriptorVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.util.collectionUtils.concat

//#region find
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.findClass(name: String): IrClassSymbol {
    val classId = ClassId.fromString(name)
    return IrClassSymbolWrapper(this.referenceClass(classId) ?: this.referenceTypeAlias(classId)?.owner?.expandedType?.classOrNull ?: error("class not found: $name"), this)
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
private const val groupParametersExisting = 5
private const val groupParameters = 6
private const val groupReturnType = 7
private val regexFun by lazy { Regex("""((?:((?:[a-zA-Z]\w*\/)*[a-zA-Z]\w*)\/)?(?:((?:[a-zA-Z]\w*\.)*[a-zA-Z]\w*)\.)?([a-zA-Z][\w<>]*))(\(((?:\s*(?:[a-zA-Z]\w*\:\s*)?[a-zA-Z][\w<>\.\/]*\??|\*|\?)(?:\s*,\s*(?:[a-zA-Z]\w*\:\s*)?(?:[a-zA-Z][\w<>\.\/]*\??|\*|\?)\s*)*)*\))?(?:(?:\s*:\s*((?:((?:[a-zA-Z]\w*[\.\/])*[a-zA-Z]\w*)[\.\/])?(?:[a-zA-Z][\w<>]*)))?)?""") }

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
private fun findReferences(pluginContext: IrPluginContext, match: MatchResult, methodName: String, callFun: (CallableId) -> Collection<IrFunctionSymbol>, selector: (IrClassSymbol?) -> Sequence<IrFunctionSymbol>?): Sequence<IrFunctionSymbol> {
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
            return selector(pluginContext.findClass(fullMethodName))?.filter { it.owner.name.asString() == methodName }!!
        }
    }
}

const val NONE = 0
const val STAR = 1
const val EXCLAMATION_MARK = 2
const val QUESTION_MARK = 4
const val ANY = 8
const val SUBTYPE = 16
const val EXACT_NO_VARARG = 32
const val EXACT_NULL = 64
const val EXACT = 128
const val EXACT_SUPER = 256

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun <T>find(
    name: String,
    findReferences: (match: MatchResult, methodName: String) -> Sequence<IrFunctionSymbol>,
    getAnyNullType: () -> T,
    tryFindIrType: (typeName: String) -> T,
    typeEvaluator: (requiredType: T, existingFunctionParameter: IrValueParameter, anyNullType: T) -> Int): IrFunctionSymbol {

    val match = regexFun.matchEntire(name)
    if (match != null) {
        val size = match.groups.size
        if (size >= groupMethod) {
            val methodName = match.groups[groupMethod]?.value
            if (!methodName.isNullOrEmpty()) {
                var existingFunctionReferences = findReferences(match, methodName)
                if (existingFunctionReferences.any()) {

                    /// This part is for functions & constructors only!
                    if (match.groups[groupParametersExisting] != null) {
                        val parametersStrings = match.groups[groupParameters]?.value?.split(',')?.map {
                            val index = it.indexOf(':')
                            if (index >= 0) {
                                Pair(it.substring(0, index).trim(), it.substring(index + 2).trim())
                            } else {
                                Pair(null, it.trim())
                            }
                        }?.toList()

                        if (parametersStrings != null) {
                            if (parametersStrings.any()) {
                                val anyNullType = getAnyNullType()
                                val star = "*"

                                if (parametersStrings.size != 1 || parametersStrings[0].second != star) {
                                    val em = "!"
                                    val qm = "?"

                                    val (requiredTypes, requiredTypesPerName) = parametersStrings.mapIndexed { index, (argumentName, typeName) ->
                                        val type: T?
                                        val tName: String?
                                        if (typeName == star || typeName == qm) {
                                            type = null
                                            tName = typeName
                                        } else {
                                            type = tryFindIrType(typeName)
                                            tName = null
                                        }
                                        Tuple4(index, argumentName, tName, type)
                                    }.partition { it.v2 == null }

                                    fun typeEvaluator(requiredTypeEntry: Tuple4<Int, String?, String?, T?>, existingFunctionParameter: IrValueParameter) = when (requiredTypeEntry.v3) {
                                        star -> STAR
                                        em -> EXCLAMATION_MARK
                                        qm -> QUESTION_MARK
                                        else -> if (requiredTypeEntry.v4 == null) NONE else typeEvaluator(requiredTypeEntry.v4!!, existingFunctionParameter, anyNullType)
                                    }

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
                                                Triple(index, existingValueParameter, typeEvaluator(requiredTypeEntry, existingValueParameter))
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
                                                /*
                                                if (wildcard == qm && existingFunctionParametersIt.hasNext()) {
                                                    existingFunctionParametersIt.next()
                                                    continue
                                                }
                                                */

                                                val isStar = wildcard == star
                                                while (wildcard == star && requiredTypesIt.hasNext()) {
                                                    entry = requiredTypesIt.next()
                                                    wildcard = entry.v3
                                                }

                                                loopCheckExisting@ while (existingFunctionParametersIt.hasNext()) {
                                                    val (index, existingValueParameter) = existingFunctionParametersIt.next()

                                                    val derivationLevel = typeEvaluator(entry, existingValueParameter)
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
                                                while (existingFunctionParametersIt.hasNext()) {
                                                    if (existingFunctionParametersIt.next().second.defaultValue == null) {
                                                        break
                                                    }
                                                }

                                                if (existingFunctionParametersIt.hasNext()) {
                                                    return@map Pair(false, it)
                                                }
                                            }

                                            return@map Pair(true, Triple(existingFunctionReference, existingHasNoNameFilter, existingHasName))
                                        }

                                        if (requiredTypes.all { it.v3 == star }) {
                                            return@map Pair(true, Triple(existingFunctionReference, existingHasNoName.map { (index, existingValueParameter) -> Triple(index, existingValueParameter, STAR) }, existingHasName))
                                        }

                                        return@map Pair(false, it)
                                    }
                                        .filter { it.first }
                                        .map { it.second }
                                        .toList()

                                    if (existingFunctionReferenceValueParametersList.size >= 2) {
                                        val max = existingFunctionReferenceValueParametersList.maxOf { (_, existingHasNoName, existingHasName) -> existingHasNoName.concat(existingHasName)!!.maxOf { it.third } }
                                        if (max > NONE) {
                                            var result = existingFunctionReferenceValueParametersList.filter { (_, existingHasNoName, existingHasName) -> existingHasNoName.concat(existingHasName)!!.all { it.third == max } }.toList()
                                            if (result.any()) {
                                                if (result.size >= 2) {
                                                    if (max and ANY > NONE || max and STAR > NONE) {
                                                        val minParametersResult = result.map {
                                                            Pair(it.first.owner.valueParameters.size, it)
                                                        }
                                                        val minParameterSize = minParametersResult.minOf { it.first }
                                                        result = minParametersResult
                                                            .filter { it.first == minParameterSize }
                                                            .map { it.second }
                                                    }
                                                }

                                                existingFunctionReferenceValueParametersList = result
                                            }
                                        }
                                    }

                                    existingFunctionReferences = existingFunctionReferenceValueParametersList.map { it.first }.asSequence()
                                }

                                if (existingFunctionReferences.take(2).count() >= 2) {
                                    val anyNullTypeCounts = existingFunctionReferences.map { Pair(it, it.owner.valueParameters.count { p -> p.type == anyNullType }) }
                                    val nullMin = anyNullTypeCounts.map { p -> p.second }.min()
                                    existingFunctionReferences = anyNullTypeCounts
                                        .filter { p -> p.second == nullMin }
                                        .map { p -> p.first }

                                    if (existingFunctionReferences.take(2).count() >= 2) {
                                        val optionalParametersCount = existingFunctionReferences.map { Pair(it, it.owner.valueParameters.count { p -> p.defaultValue != null || p.varargElementType != null }) }
                                        val optionalMin = optionalParametersCount.map { p -> p.second }.min()
                                        existingFunctionReferences = optionalParametersCount
                                            .filter { p -> p.second == optionalMin }
                                            .map { p -> p.first }
                                    }
                                }
                            }
                        } else {
                            existingFunctionReferences = existingFunctionReferences.filter { !it.owner.valueParameters.any() }
                        }
                    }

                    if (size >= groupReturnType) {
                        val returnTypeName = match.groups[groupReturnType]?.value
                        if (!returnTypeName.isNullOrEmpty()) {
                            val returnType = tryFindIrType(returnTypeName)
                            existingFunctionReferences = existingFunctionReferences.filter { it.owner.returnType == returnType }
                        }
                    }

                    val count = existingFunctionReferences.take(2).count()
                    if (count > 0) {
                        check(count <= 1) { "ambiguous function signature: $name, ${existingFunctionReferences.map { it }}" }
                        return existingFunctionReferences.single()
                    }
                }
            }
        }
    }

    error("function not found: $name")
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun find(pluginContext: IrPluginContext, name: String, findReferences: (match: MatchResult, methodName: String) -> Sequence<IrFunctionSymbol>): IrFunctionSymbol = find(
    name,
    findReferences,
    { -> pluginContext.irBuiltIns.anyNType },
    { typeName -> pluginContext.tryFindIrType(typeName) },
    irTypeEvaluator
)

private val irTypeEvaluator: (IrType, IrValueParameter, IrType) -> Int = { requiredType, existingFunctionParameter, anyNullType ->
    var result = NONE

    if (existingFunctionParameter == requiredType) {
        EXACT_SUPER
    }

    if (existingFunctionParameter.type.classOrFail == requiredType.type.classOrFail) {
        val requiredIsNullable = requiredType.type.isNullable()
        val existingIsNullable = existingFunctionParameter.type.isNullable()

        if (requiredIsNullable == existingIsNullable) {
            result = EXACT
        } else if (existingIsNullable) {
            result = EXACT_NULL
        }
    }

    result = result or
    (if (requiredType.type.isSubtypeOfClass(existingFunctionParameter.type.classOrFail)) SUBTYPE else NONE) or
    (if (existingFunctionParameter.type == anyNullType) ANY else NONE)

    if (result != NONE && !existingFunctionParameter.isVararg) result or EXACT_NO_VARARG else result
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun find(clazz: IrClassSymbol, name: String, findReferences: (match: MatchResult, methodName: String) -> Sequence<IrFunctionSymbol>): IrFunctionSymbol = find(
    name,
    findReferences,
    { -> "Any?" },
    { methodName -> selectorFunctionSymbols(clazz)?.filter { it.owner.name.asString() == methodName }!! },
        { requiredType, existingFunctionParameter, anyNullType -> (if (existingFunctionParameter == requiredType || existingFunctionParameter.type == requiredType) EXACT else NONE) or
                (if (existingFunctionParameter.type == anyNullType) ANY else NONE)
    }
)

@OptIn(UnsafeDuringIrConstructionAPI::class)
private val selectorFunctionSymbols: (IrClassSymbol?) -> Sequence<IrFunctionSymbol>? = { classSymbol -> classSymbol?.functions }

@OptIn(UnsafeDuringIrConstructionAPI::class)
private val selectorFunctions: (IrClass?) -> Sequence<IrFunction>? = { clazz -> clazz?.functions }

@OptIn(UnsafeDuringIrConstructionAPI::class)
private val selectorConstructorSymbols: (IrClassSymbol?) -> Sequence<IrConstructorSymbol>? = { classSymbol -> classSymbol?.constructors }

@OptIn(UnsafeDuringIrConstructionAPI::class)
private val selectorConstructors: (IrClass?) -> Sequence<IrConstructor>? = { clazz -> clazz?.constructors }

fun IrPluginContext.findFunction(name: String): IrSimpleFunctionSymbolWrapper = IrSimpleFunctionSymbolWrapper(find(this, name) { match, methodName -> findReferences(this, match, methodName, { callableId -> this.referenceFunctions(callableId) }, selectorFunctionSymbols) } as IrSimpleFunctionSymbol, this)

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.findFunction(name: String, clazz: IrClassSymbol): IrSimpleFunctionSymbolWrapper = IrSimpleFunctionSymbolWrapper(find(this, name) { _, methodName -> selectorFunctionSymbols(clazz)?.filter { it.owner.name.asString() == methodName }!! } as IrSimpleFunctionSymbol, this)

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.findFunction(name: String, clazz: IrClass): IrSimpleFunctionSymbolWrapper = this.findFunction(name, clazz.symbol)

@UnsafeDuringIrConstructionAPI
fun IrClass.findFunction(name: String): IrSimpleFunctionSymbol = if (this is IrClassWrapper) this.findFunction(name) else this.symbol.findFunction(name)

fun IrClassWrapper.findFunction(name: String): IrSimpleFunctionSymbolWrapper = IrSimpleFunctionSymbolWrapper(this.pluginContext.findFunction(name), this.pluginContext)

@UnsafeDuringIrConstructionAPI
fun IrClassSymbol.findFunction(name: String): IrSimpleFunctionSymbol {
    if (this is IrClassSymbolWrapper) {
        return this.findFunction(name)
    }

    val owner = this.owner as? IrClassWrapper
    if (owner != null) {
        return owner.findFunction(name)
    }

    return find(this, name) { _, methodName -> selectorFunctionSymbols(this)?.filter { it.owner.name.asString() == methodName }!! } as IrSimpleFunctionSymbol
}

@UnsafeDuringIrConstructionAPI
fun IrClassSymbolWrapper.findFunction(name: String): IrSimpleFunctionSymbolWrapper = this.pluginContext.findFunction(name, this)

@UnsafeDuringIrConstructionAPI
fun IrType.findFunction(name: String): IrSimpleFunctionSymbol = this.getClass()!!.findFunction(name)

@UnsafeDuringIrConstructionAPI
fun IrProperty.findFunction(name: String): IrSimpleFunctionSymbol = this.getter!!.returnType.classOrFail.findFunction(name)

@UnsafeDuringIrConstructionAPI
fun IrPropertySymbol.findFunction(name: String): IrSimpleFunctionSymbol = this.owner.findFunction(name)

fun IrPluginContext.findConstructor(name: String): IrConstructorSymbol = IrConstructorSymbolWrapper(find(this, name) { match, methodName -> findReferences(this, match, methodName, { callableId -> this.referenceConstructors(ClassId.fromString(callableId.toString())) }, selectorConstructorSymbols) } as IrConstructorSymbol, this)

fun IrPluginContext.findConstructor(name: String, clazz: IrClassSymbol): IrConstructorSymbolWrapper = IrConstructorSymbolWrapper(find(this, name) { _, _ -> selectorConstructorSymbols(clazz)!! } as IrConstructorSymbol, this)

@UnsafeDuringIrConstructionAPI
fun IrClassSymbol.findConstructor(name: String): IrConstructorSymbol = if (this is IrClassSymbolWrapper) this.pluginContext.findConstructor(name, this) else this.owner.findConstructor(name)

fun IrClassSymbolWrapper.findConstructor(name: String): IrConstructorSymbolWrapper = this.pluginContext.findConstructor(name, this)

fun IrType.findConstructor(name: String): IrConstructorSymbol = this.getClass()!!.findConstructor(name)

fun IrClass.findConstructor(name: String): IrConstructorSymbol {
    error("constructor not found: $name")
}

@UnsafeDuringIrConstructionAPI
fun IrProperty.findConstructor(name: String): IrConstructorSymbol = this.getter!!.returnType.classOrFail.findConstructor(name)

@UnsafeDuringIrConstructionAPI
fun IrPropertySymbol.findConstructor(name: String): IrConstructorSymbol = this.owner.findConstructor(name)

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

@UnsafeDuringIrConstructionAPI
fun IrType.findProperty(name: String): IrPropertySymbol = this.getClass()!!.findProperty(name)
//#endregion find

//#region call
/**
 * Creates a suitable IR expression type from various data types, including the primitive data types.
 */
fun IrBuilderWithScope.convert(pluginContext: IrPluginContext, parameter: Any?): IrExpression? {
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

        is IrCall -> return parameter
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

fun IrBuilderWithScope.call(pluginContext: IrPluginContext, function: IrFunction, receiver: Any?, vararg parameters: Any?): IrFunctionAccessExpression {
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
fun IrBuilderWithScope.call(pluginContext: IrPluginContext, functionSymbol: IrFunctionSymbol, receiver: Any?, vararg parameters: Any?) = call(pluginContext,IrFunctionWrapper(functionSymbol.owner, pluginContext), receiver, *parameters)
//#endregion

//#region concat
fun IrBuilderWithScope.concat(pluginContext: IrPluginContext, vararg parameters: Any?): IrStringConcatenationImpl = irConcat().apply {
    for (parameter in parameters) {
        val expression = convert(pluginContext, parameter)
        if (expression != null) {
            addArgument(expression)
        }
    }
}

fun IrBuilderWithScope.irConcat(pluginContext: IrPluginContext, vararg parameters: Any?): IrStringConcatenationImpl = this.concat(pluginContext, *parameters)
//#endregion

interface IrPluginContextWrapper<T> {
    val content: T
    val pluginContext: IrPluginContext

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String
}

class IrClassSymbolWrapper(val symbol: IrClassSymbol, override val pluginContext: IrPluginContext): IrClassSymbol, IrPluginContextWrapper<IrClassSymbol> {
    override val content = symbol

    @UnsafeDuringIrConstructionAPI
    override val owner = symbol.owner

    @ObsoleteDescriptorBasedAPI
    override val descriptor = symbol.descriptor

    override fun bind(owner: IrClass) = symbol.bind(owner)

    @ObsoleteDescriptorBasedAPI
    override val hasDescriptor = symbol.hasDescriptor

    override val isBound = symbol.isBound

    override val signature = symbol.signature

    override var privateSignature: IdSignature?
        get() = symbol.privateSignature
        set(value) { symbol.privateSignature = value }

    override fun equals(other: Any?) = this === other || content == other || other is IrClassSymbolWrapper && content == other.content

    override fun hashCode() = content.hashCode()

    override fun toString() = symbol.toString()
}

class IrClassWrapper(override val content: IrClass, override val pluginContext: IrPluginContext): IrClass(), IrPluginContextWrapper<IrClass> {

    @ObsoleteDescriptorBasedAPI
    override val descriptor = content.descriptor

    override val symbol = content.symbol

    override var kind = content.kind

    override var modality = content.modality

    override var isCompanion = content.isCompanion

    override var isInner = content.isInner

    override var isData = content.isData

    override var isValue = content.isValue

    override var isExpect = content.isExpect

    override var isFun = content.isFun

    override var hasEnumEntries = content.hasEnumEntries

    override val source = content.source

    override var superTypes = content.superTypes

    override var thisReceiver = content.thisReceiver

    override var valueClassRepresentation = content.valueClassRepresentation

    override var sealedSubclasses = content.sealedSubclasses

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitClass(this, data)

    override var origin: IrDeclarationOrigin
        get() = content.origin
        set(value) { content.origin = value }

    override val factory = content.factory

    override var annotations: List<IrConstructorCall>
        get() = content.annotations
        set(value) { content.annotations = value }

    override var isExternal: Boolean
        get() = content.isExternal
        set(value) { content.isExternal = value }

    override var name: Name
        get() = content.name
        set(value) { content.name = value }

    override var visibility: DescriptorVisibility
        get() = content.visibility
        set(value) { content.visibility = value }

    override var typeParameters: List<IrTypeParameter>
        get() = content.typeParameters
        set(value) { content.typeParameters = value }

    @UnsafeDuringIrConstructionAPI
    override val declarations = content.declarations

    override var attributeOwnerId: IrAttributeContainer
        get() = content.attributeOwnerId
        set(value) { content.attributeOwnerId = value }

    override var originalBeforeInline: IrAttributeContainer?
        get() = content.originalBeforeInline
        set(value) { content.originalBeforeInline = value }

    override var metadata: MetadataSource?
        get() = content.metadata
        set(value) { content.metadata = value }

    override val startOffset = content.startOffset

    override val endOffset = content.endOffset

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        typeParameters.forEach { it.accept(visitor, data) }
        declarations.forEach { it.accept(visitor, data) }
        thisReceiver?.accept(visitor, data)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        typeParameters = typeParameters.transformIfNeeded(transformer, data)
        declarations.transformInPlace(transformer, data)
        thisReceiver = thisReceiver?.transform(transformer, data)
    }

    override fun equals(other: Any?) = this === other || other is IrClassWrapper && content == other.content

    override fun hashCode() = content.hashCode()

    override fun toString() = content.toString()
}

class IrFunctionWrapper(override val content: IrFunction, override val pluginContext: IrPluginContext): IrFunction(), IrPluginContextWrapper<IrFunction> {

    @ObsoleteDescriptorBasedAPI
    override val descriptor = content.descriptor

    override val symbol = content.symbol

    override var isInline: Boolean
        get() = content.isInline
        set(value) { content.isInline = value }

    override var isExpect: Boolean
        get() = content.isExpect
        set(value) { content.isExpect = value }

    override var returnType: IrType
        get() = content.returnType
        set(value) { content.returnType = value }

    override var dispatchReceiverParameter: IrValueParameter?
        get() = content.dispatchReceiverParameter
        set(value) { content.dispatchReceiverParameter = value }

    override var extensionReceiverParameter: IrValueParameter?
        get() = content.extensionReceiverParameter
        set(value) { content.extensionReceiverParameter = value }

    override var valueParameters: List<IrValueParameter>
        get() = content.valueParameters
        set(value) { content.valueParameters = value }

    override var contextReceiverParametersCount: Int
        get() = content.contextReceiverParametersCount
        set(value) { content.contextReceiverParametersCount = value }

    override var body: IrBody?
        get() = content.body
        set(value) { content.body = value }

    override val startOffset = content.startOffset

    override val endOffset = content.endOffset

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D) = content.accept(visitor, data)

    override var origin: IrDeclarationOrigin
        get() = content.origin
        set(value) { content.origin = value }

    override val factory = content.factory

    override var annotations: List<IrConstructorCall>
        get() = content.annotations
        set(value) { content.annotations = value }

    override var isExternal: Boolean
        get() = content.isExternal
        set(value) { content.isExternal = value }

    override var name: Name
        get() = content.name
        set(value) { content.name = value }

    override var visibility: DescriptorVisibility
        get() = content.visibility
        set(value) { content.visibility = value }

    override var typeParameters: List<IrTypeParameter>
        get() = content.typeParameters
        set(value) { content.typeParameters = value }

    override val containerSource = content.containerSource

    override var metadata: MetadataSource?
        get() = content.metadata
        set(value) { content.metadata = value }

    override fun equals(other: Any?) = this === other || content == other || other is IrFunctionWrapper && content == other.content

    override fun hashCode() = content.hashCode()

    override fun toString() = content.toString()
}

class IrSimpleFunctionSymbolWrapper(override val content: IrSimpleFunctionSymbol, override val pluginContext: IrPluginContext): IrSimpleFunctionSymbol, IrPluginContextWrapper<IrSimpleFunctionSymbol> {

    @UnsafeDuringIrConstructionAPI
    override val owner = content.owner

    override fun bind(owner: IrSimpleFunction) = content.bind(owner)

    @ObsoleteDescriptorBasedAPI
    override val descriptor = content.descriptor

    @ObsoleteDescriptorBasedAPI
    override val hasDescriptor = content.hasDescriptor

    override val isBound = content.isBound

    override val signature = content.signature

    override var privateSignature: IdSignature?
        get() = content.privateSignature
        set(value) { content.privateSignature = value }

    override fun equals(other: Any?) = this === other || content == other || other is IrSimpleFunctionSymbolWrapper && content == other.content

    override fun hashCode() = content.hashCode()

    override fun toString() = content.toString()
}

class IrSimpleFunctionWrapper(override val content: IrSimpleFunction, override val pluginContext: IrPluginContext): IrSimpleFunction(), IrPluginContextWrapper<IrSimpleFunction> {

    @ObsoleteDescriptorBasedAPI
    override val descriptor = content.descriptor

    override val symbol = content.symbol

    override var overriddenSymbols: List<IrSimpleFunctionSymbol>
        get() = overriddenSymbols
        set(value) { content.overriddenSymbols = value }

    override var isTailrec: Boolean
        get() = content.isTailrec
        set(value) { content.isTailrec = value }

    override var isSuspend: Boolean
        get() = content.isSuspend
        set(value) { content.isSuspend = value }

    override var isOperator: Boolean
        get() = content.isOperator
        set(value) { content.isOperator = value }

    override var isInfix: Boolean
        get() = content.isInfix
        set(value) { content.isInfix = value }

    override var correspondingPropertySymbol: IrPropertySymbol?
        get() = content.correspondingPropertySymbol
        set(value) { content.correspondingPropertySymbol = value }

    override var isInline: Boolean
        get() = content.isInline
        set(value) { content.isInline = value }

    override var isExpect: Boolean
        get() = content.isExpect
        set(value) { content.isExpect = value }

    override var returnType: IrType
        get() = content.returnType
        set(value) { content.returnType = value }

    override var dispatchReceiverParameter: IrValueParameter?
        get() = content.dispatchReceiverParameter
        set(value) { content.dispatchReceiverParameter = value }

    override var extensionReceiverParameter: IrValueParameter?
        get() = content.extensionReceiverParameter
        set(value) { content.extensionReceiverParameter = value }

    override var valueParameters: List<IrValueParameter>
        get() = content.valueParameters
        set(value) { content.valueParameters = value }

    override var contextReceiverParametersCount: Int
        get() = content.contextReceiverParametersCount
        set(value) { content.contextReceiverParametersCount = value }

    override var body: IrBody?
        get() = content.body
        set(value) { content.body = value }

    override var startOffset: Int
        get() = content.startOffset
        set(value) { content.startOffset = value }

    override var endOffset: Int
        get() = content.endOffset
        set(value) { content.endOffset = value }

    override var isFakeOverride: Boolean
        get() = content.isFakeOverride
        set(value) { content.isFakeOverride = value }

    override var origin: IrDeclarationOrigin
        get() = content.origin
        set(value) { content.origin = value }

    override val factory = content.factory

    override var annotations: List<IrConstructorCall>
        get() = content.annotations
        set(value) { content.annotations = value }

    override var isExternal: Boolean
        get() = content.isExternal
        set(value) { content.isExternal = value }

    override var name: Name
        get() = content.name
        set(value) { content.name = value }

    override var visibility: DescriptorVisibility
        get() = content.visibility
        set(value) { content.visibility = value }

    override var typeParameters: List<IrTypeParameter>
        get() = content.typeParameters
        set(value) { content.typeParameters = value }

    override val containerSource = content.containerSource

    override var metadata: MetadataSource?
        get() = content.metadata
        set(value) { content.metadata = value }

    override var modality: Modality
        get() = content.modality
        set(value) { content.modality = value }

    override var attributeOwnerId: IrAttributeContainer
        get() = content.attributeOwnerId
        set(value) { content.attributeOwnerId = value }

    override var originalBeforeInline: IrAttributeContainer?
        get() = content.originalBeforeInline
        set(value) { content.originalBeforeInline = value }

    override fun equals(other: Any?) = this === other || content == other || other is IrSimpleFunctionWrapper && content == other.content

    override fun hashCode() = content.hashCode()

    override fun toString() = content.toString()
}

class IrConstructorSymbolWrapper(override val content: IrConstructorSymbol, override val pluginContext: IrPluginContext): IrConstructorSymbol, IrPluginContextWrapper<IrConstructorSymbol> {

    @UnsafeDuringIrConstructionAPI
    override val owner = content.owner

    @ObsoleteDescriptorBasedAPI
    override val descriptor = content.descriptor

    override fun bind(owner: IrConstructor) = content.bind(owner)

    @ObsoleteDescriptorBasedAPI
    override val hasDescriptor = content.hasDescriptor

    override val isBound = content.isBound

    override val signature = content.signature

    override var privateSignature: IdSignature?
        get() = content.privateSignature
        set(value) { content.privateSignature = value }

    override fun equals(other: Any?) = this === other || content == other || other is IrSimpleFunctionWrapper && content == other.content

    override fun hashCode() = content.hashCode()

    override fun toString() = content.toString()
}

class IrPropertySymbolWrapper(override val content: IrPropertySymbol, override val pluginContext: IrPluginContext): IrPropertySymbol, IrPluginContextWrapper<IrPropertySymbol> {

    @UnsafeDuringIrConstructionAPI
    override val owner = content.owner

    @ObsoleteDescriptorBasedAPI
    override val descriptor = content.descriptor

    override fun bind(owner: IrProperty) = content.bind(owner)

    @ObsoleteDescriptorBasedAPI
    override val hasDescriptor = content.hasDescriptor

    override val isBound = content.isBound

    override val signature = content.signature

    override var privateSignature: IdSignature?
        get() = content.privateSignature
        set(value) { content.privateSignature = value }

    override fun equals(other: Any?) = this === other || content == other || other is IrPropertySymbolWrapper && content == other.content

    override fun hashCode() = content.hashCode()

    override fun toString() = content.toString()
}

class IrPropertyWrapper(override val content: IrProperty, override val pluginContext: IrPluginContext): IrProperty(), IrPluginContextWrapper<IrProperty> {

    @ObsoleteDescriptorBasedAPI
    override val descriptor = content.descriptor

    override val symbol = content.symbol

    override var overriddenSymbols: List<IrPropertySymbol>
        get() = content.overriddenSymbols
        set(value) { content.overriddenSymbols = value }

    override var isVar: Boolean
        get() = content.isVar
        set(value) { content.isVar = value }

    override var isConst: Boolean
        get() = content.isConst
        set(value) { content.isConst = value }

    override var isLateinit: Boolean
        get() = content.isLateinit
        set(value) { content.isLateinit = value }

    override var isDelegated: Boolean
        get() = content.isDelegated
        set(value) { content.isDelegated = value }

    override var isExpect: Boolean
        get() = content.isExpect
        set(value) { content.isExpect = value }

    override var backingField: IrField?
        get() = content.backingField
        set(value) { content.backingField = value }

    override var getter: IrSimpleFunction?
        get() = content.getter
        set(value) { content.getter = value }

    override var setter: IrSimpleFunction?
        get() = content.setter
        set(value) { content.setter = value }

    override var startOffset: Int
        get() = content.startOffset
        set(value) { content.startOffset = value }

    override var endOffset: Int
        get() = content.endOffset
        set(value) { content.endOffset = value }

    override var isFakeOverride: Boolean
        get() = content.isFakeOverride
        set(value) { content.isFakeOverride = value }

    override var origin: IrDeclarationOrigin
        get() = content.origin
        set(value) { content.origin = value }

    override val factory = content.factory

    override var annotations: List<IrConstructorCall>
        get() = content.annotations
        set(value) { content.annotations = value }

    override var isExternal: Boolean
        get() = content.isExternal
        set(value) { content.isExternal = value }

    override var name: Name
        get() = content.name
        set(value) { content.name = value }

    override var modality: Modality
        get() = content.modality
        set(value) { content.modality = value }

    override var visibility: DescriptorVisibility
        get() = content.visibility
        set(value) { content.visibility = value }

    override var metadata: MetadataSource?
        get() = content.metadata
        set(value) { content.metadata = value }

    override var attributeOwnerId: IrAttributeContainer
        get() = content.attributeOwnerId
        set(value) { content.attributeOwnerId = value }

    override var originalBeforeInline: IrAttributeContainer?
        get() = content.originalBeforeInline
        set(value) { content.originalBeforeInline = value }

    override val containerSource = content.containerSource

    override fun equals(other: Any?) = this === other || content == other || other is IrPropertyWrapper && content == other.content

    override fun hashCode() = content.hashCode()

    override fun toString() = content.toString()
}