package at.ssw.compilerplugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

data class ParsedSignature(
    val packageName: String,
    val className: String? = null,
    val functionName: String? = null,
    val propertyName: String? = null,
    val parameters: List<IrType> = emptyList()
) {
    init {
        require(packageName.isNotBlank()) { "packageName must not be blank" }
        require(functionName != null || className != null || propertyName != null) {
            "At least one of functionName, className, or propertyName must be provided. \nIf you think you provided at least one of those, double check your signature!\nSignature in this style: path/to/package/Class?.Name?.functionName(param1?, param2?, ...)?"
        }
    }
}

fun IrPluginContext.findFunction(signature: String, extensionReceiverType: IrType? = null): IrSimpleFunctionSymbol? {
    val parsedSignature = parseSignature(this, signature)
    val callableId = buildCallableId(parsedSignature) ?: return null

    return this.referenceFunctions(callableId)
        .singleOrNull { func ->
            checkMethodSignature(func, parsedSignature.parameters) && checkExtensionFunctionReceiverType(func, extensionReceiverType)
        }
}

fun IrPluginContext.getIrType(typeString: String): IrType? {
    val isNullable = typeString.endsWith("?", ignoreCase = true)
    val baseType = if (isNullable) typeString.removeSuffix("?") else typeString
    val normalizedType = baseType.lowercase()

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
            //TODO: replace with find class
            val classId = ClassId.fromString(baseType)
            this.referenceClass(classId)?.defaultType ?: return null
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
    return func.owner.valueParameters.zip(paramTypes).all { (param, expectedType) -> param.type == expectedType }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun checkExtensionFunctionReceiverType(func: IrSimpleFunctionSymbol, extensionReceiverType: IrType? = null) = extensionReceiverType?.let { func.owner.extensionReceiverParameter?.type == it } ?: true

private fun parseSignature(pluginContext: IrPluginContext, signature: String): ParsedSignature {
    val parts = signature.split('/')
    val functionParts = parts.last().split('.')
    val packageName = parts.dropLast(1).joinToString(".")
    val functionName = functionParts.last().substringBefore("(").takeIf { it.isNotBlank() }
    val paramsString = functionParts.last().substringAfter("(").substringBefore(")")
    val className = if(functionParts.last().contains('(')) {
        functionParts.dropLast(1).joinToString(".").takeIf { it.isNotBlank() }
    } else {
        functionParts.joinToString(".").takeIf { it.isNotBlank() }
    }

    //functions must have parenthesis
    if(functionName != null) {
        require(functionParts.last().contains('('))
    }

    //parse parameters
    val params = if (paramsString.isBlank()) {
        emptyList()
    } else {
        paramsString.split(",").map { it.trim() }.mapNotNull  { pluginContext.getIrType(it) }
    }

    return ParsedSignature(
        packageName = packageName,
        className = className,
        functionName = functionName,
        parameters = params
    )
}

private fun buildCallableId(parsedSignature: ParsedSignature): CallableId? {
    //functionName is needed
    if (parsedSignature.functionName == null) return null
    //create Id for cases class exist or don't exist
    return if (parsedSignature.className != null) {
        CallableId(
            FqName(parsedSignature.packageName),
            FqName(parsedSignature.className),
            Name.identifier(parsedSignature.functionName)
        )
    } else {
        CallableId(
            FqName(parsedSignature.packageName),
            Name.identifier(parsedSignature.functionName)
        )
    }
}