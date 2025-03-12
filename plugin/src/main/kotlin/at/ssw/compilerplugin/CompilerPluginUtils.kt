package at.ssw.compilerplugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

fun IrPluginContext.findFunction(signature: String, extensionReceiverType: IrType? = null): IrSimpleFunctionSymbol? {
    validateSignature(signature)

    val (fqName, functionName, paramTypes) = parseSignature(signature)

    val callableId = buildCallableId(fqName, functionName)

    return this.referenceFunctions(callableId)
        .singleOrNull { func ->
            checkMethodSignature(this, func, paramTypes) && checkExtensionFunctionReceiverType(func, extensionReceiverType)
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

private fun buildCallableId(path: String, functionName: String): CallableId {
    val parts = path.split('.')

    //find start of classes
    val classStartIndex = parts.indexOfFirst { it.first().isUpperCase() }

    //create Id for cases class exist or dont exist
    return if (classStartIndex != -1) {
        val packageName = parts.take(classStartIndex).joinToString(".")
        val className = parts.drop(classStartIndex).joinToString(".")

        CallableId(
            FqName(packageName),
            FqName(className),
            Name.identifier(functionName)
        )
    } else {
        CallableId(
            FqName(path),
            Name.identifier(functionName)
        )
    }
}

private fun checkMethodSignature(pluginContext: IrPluginContext, func: IrSimpleFunctionSymbol, paramTypes: List<String>): Boolean {
    try {
        //check params count
        if (func.owner.valueParameters.size != paramTypes.size) {
            return false
        }

        //check parameter types
        val paramsMatch = func.owner.valueParameters.zip(paramTypes).all { (param, expectedTypeStr) ->
            val expectedType = pluginContext.getIrType(expectedTypeStr.trim())
            param.type == expectedType
        }

        return paramsMatch
    } catch (e: Exception) {
        return false
    }
}

private fun checkExtensionFunctionReceiverType(func: IrSimpleFunctionSymbol, extentionRecieverType: IrType? = null) = if (extentionRecieverType != null) func.owner.extensionReceiverParameter?.type == extentionRecieverType else true

private fun parseSignature(signature: String): Triple<String, String, List<String>> {
    val functionPath = signature.substringBeforeLast("/")
    val functionPart = signature.substringAfterLast("/")
    val functionName = functionPart.substringBefore("(")
    val paramsString = functionPart.substringAfter("(").substringBefore(")")

    //parse parameters
    val params = if (paramsString.isBlank()) {
        emptyList()
    } else {
        paramsString.split(",").map { it.trim() }
    }

    return Triple(functionPath, functionName, params)
}

private fun validateSignature(signature: String) {
    require(signature.contains("/")) { "Signature must contain package separator '/'" }
    require(signature.contains("(")) { "Signature must contain parameter start '('" }
    require(signature.contains(")")) { "Signature must contain parameter end ')'" }
}