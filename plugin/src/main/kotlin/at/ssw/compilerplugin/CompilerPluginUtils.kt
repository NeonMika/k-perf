package at.ssw.compilerplugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

private data class SignatureParts(
    val packageName: String,
    val className: String,
    val memberName: String,
    val packageForFindClass: String
)

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.findClass(signature: String): IrClassSymbol? {
    //check signature
    require(signature.contains('/')) {"Package path must be included in findClass signature"}
    val classId = ClassId.fromString(signature)

    //try to resolve type alias since it's more specific
    val typeAlias = referenceTypeAlias(classId)
    if (typeAlias != null) {
        return typeAlias.owner.expandedType.classOrNull
    }

    return referenceClass(classId)
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrClassSymbol.findFunction(pluginContext: IrPluginContext, signature: String, extensionReceiverType: IrType? = null): IrSimpleFunctionSymbol? {
    //TODO ohne pluginContext
    val (functionName, params) = parseFunctionParameters(pluginContext, signature)

    return this.functions
        .singleOrNull { func ->
            func.owner.name.asString() == functionName && checkMethodSignature(func, params) && checkExtensionFunctionReceiverType(func, extensionReceiverType)
        }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrClassSymbol.findProperty(signature: String): IrPropertySymbol? = this.owner.properties.find { it.name.asString().lowercase() == signature.lowercase() }?.symbol

fun IrPluginContext.findFunction(signature: String, extensionReceiverType: IrType? = null): IrSimpleFunctionSymbol? {
    val (packageName, className, functionPart, packageForFindClass) = parseSignature(signature)
    val (functionName, params) = parseFunctionParameters(this, functionPart)

    val classSymbol = if (className.isNotBlank()) {
        findClass("$packageForFindClass/$className")
    } else {
        null
    }

    return classSymbol?.findFunction(this, functionPart, extensionReceiverType)
        ?: referenceFunctions(CallableId(FqName(packageName), Name.identifier(functionName)))
            .singleOrNull { func ->
                checkMethodSignature(func, params) &&
                        checkExtensionFunctionReceiverType(func, extensionReceiverType)
            }
}

fun IrPluginContext.findProperty(signature: String): IrPropertySymbol? {
    val parts = parseSignature(signature)

    val classSymbol = if (parts.className.isNotBlank()) {
        findClass("${parts.packageForFindClass}/${parts.className}")
    } else {
        null
    }

    return classSymbol?.findProperty(parts.memberName)
        ?: referenceProperties(CallableId(FqName(parts.packageName), Name.identifier(parts.memberName)))
            .singleOrNull()
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrClassSymbol.findConstructor(pluginContext: IrPluginContext, signature: String? = "()"): IrConstructorSymbol? {
    val (_, expectedParams) = parseFunctionParameters(pluginContext, "<init>$signature")

    return this.constructors.singleOrNull { constructor ->
        constructor.owner.valueParameters.size == expectedParams.size &&
                constructor.owner.valueParameters.zip(expectedParams).all { (param, expectedType) ->
                    areTypesEquivalent(param.type, expectedType)
                }
    }
}

fun IrPluginContext.getIrType(typeString: String): IrType? {
    val isNullable = typeString.endsWith("?", ignoreCase = true)
    val baseType = if (isNullable) typeString.removeSuffix("?") else typeString
    val normalizedType = baseType.lowercase()
    this.irBuiltIns.intType.classifierOrFail

    val type = when (normalizedType) {
        "int" -> this.irBuiltIns.intType
        "long" -> this.irBuiltIns.longType
        "short" -> this.irBuiltIns.shortType
        "byte" -> this.irBuiltIns.byteType
        "boolean" -> this.irBuiltIns.booleanType
        "char" -> this.irBuiltIns.charType
        "float" -> this.irBuiltIns.floatType
        "double" -> this.irBuiltIns.doubleType

        "string" -> this.irBuiltIns.stringType
        "unit" -> this.irBuiltIns.unitType
        "nothing" -> this.irBuiltIns.nothingType
        "any" -> this.irBuiltIns.anyType

        "array" -> this.irBuiltIns.arrayClass.defaultType
        "list" -> this.irBuiltIns.listClass.defaultType
        "set" -> this.irBuiltIns.setClass.defaultType
        "map" -> this.irBuiltIns.mapClass.defaultType
        "collection" -> this.irBuiltIns.collectionClass.defaultType
        "iterable" -> this.irBuiltIns.iterableClass.defaultType
        "mutablelist" -> this.irBuiltIns.mutableListClass.defaultType
        "mutableset" -> this.irBuiltIns.mutableSetClass.defaultType
        "mutablemap" -> this.irBuiltIns.mutableMapClass.defaultType
        "mutablecollection" -> this.irBuiltIns.mutableCollectionClass.defaultType
        "mutableiterable" -> this.irBuiltIns.mutableIterableClass.defaultType

        "enum" -> this.irBuiltIns.enumClass.defaultType

        //user defined types: (must contain package)
        else -> {
            this.findClass(normalizedType)?.defaultType ?: return null
        }
    }

    //make nullable if needed
    return if (isNullable) type.makeNullable() else type
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun checkMethodSignature(func: IrSimpleFunctionSymbol, paramTypes: List<IrType>): Boolean {
    //check params count
    if (func.owner.valueParameters.size != paramTypes.size) {
        return false
    }

    //check parameter types
    return func.owner.valueParameters.zip(paramTypes).all { (param, expectedType) -> areTypesEquivalent(param.type, expectedType) }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun checkExtensionFunctionReceiverType(func: IrSimpleFunctionSymbol, extensionReceiverType: IrType? = null) = extensionReceiverType?.let {
    func.owner.extensionReceiverParameter?.type?.let { it1 -> areTypesEquivalent(it1, it)
} } ?: true

private fun parseSignature(signature: String): SignatureParts {
    require(signature.contains('/')) { "Package path must be included in signature" }

    val parts = signature.split('/')
    val memberParts = parts.last().split('.')
    val memberName = memberParts.last()
    val packageName = parts.dropLast(1).joinToString(".")
    val packageForFindClass = packageName.replace(".", "/")
    val className = memberParts.dropLast(1).joinToString(".")

    return SignatureParts(packageName, className, memberName, packageForFindClass)
}

private fun parseFunctionParameters(pluginContext: IrPluginContext, signature: String): Pair<String, List<IrType>> {
    //functions must have parenthesis
    require(signature.contains('('))

    val functionName = signature.substringBefore("(")
    val paramsString = signature.substringAfter("(").substringBefore(")")

    //parse parameters
    val params = if (paramsString.isBlank()) {
        emptyList()
    } else {
        paramsString.split(",").map { it.trim() }.mapNotNull  { pluginContext.getIrType(it) }
    }

    return Pair(functionName, params)
}

private fun areTypesEquivalent(expected: IrType, actual: IrType): Boolean {
    val expectedClassifier = expected.classifierOrNull
    val actualClassifier = actual.classifierOrNull
    return if (expectedClassifier != null && actualClassifier != null) {
        expectedClassifier == actualClassifier
    } else {
        //fallback for dynamic types(JS)
        expected == actual
    }
}