package at.ssw.helpers

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.doNothing

/**
 * Data class to hold the individual parts of a signature.
 *
 * A signature is a string that uniquely identifies a class, function or property.
 * It consists of the package name, the class name and the member name.
 *
 * @property packageName The package name of the signature.
 * @property className The class name of the signature.
 * @property memberName The member name of the signature.
 * @property packageForFindClass The package name for the [IrPluginContext.findClass] function.
 */
private data class SignatureParts(
    val packageName: String,
    val className: String,
    val memberName: String,
    val packageForFindClass: String
)

/**
 * Represents the type of signature used in method or parameter matching.
 *
 * - `Star`: Indicates a wildcard parameter, allowing arbitrary parameters from this point onward.
 * - `Generic`: Represents a placeholder for generic types.
 * - `Type`: Specifies a concrete type for matching.
 */
enum class SignatureType {
    Star,
    Generic,
    Type
}

/**
 * Finds a class by its signature.
 *
 * The signature is a string that uniquely identifies a class. It consists of the package name and the class name.
 * The class name is the last part of the signature, and the package name is everything before the last '/'.
 * If the class is not found in Kotlin, it attempts to find it in the Java context.
 * See [getTypePackage] for predefined packages.
 *
 * @param signature The signature of the class to find in the form "package/outerClasses.ClassName".
 * @return The found class symbol or null if it was not found.
 * @throws IllegalArgumentException If the signature does not include a package path.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.findClass(signature: String): IrClassSymbol? {
    val packageSearch = getTypePackage(signature.lowercase())
    val searchString = if (signature.contains('/')) {
        //check signature
        signature
    } else {
        if (packageSearch != signature.lowercase()) {
            packageSearch
        } else {
            throw IllegalArgumentException("Package path must be included in findClass signature")
        }
    }
    val classId = ClassId.fromString(searchString)

    //try to resolve type alias since it's more specific
    val typeAlias = referenceTypeAlias(classId)
    if (typeAlias != null) {
        return typeAlias.owner.expandedType.classOrNull
    }

    val result = referenceClass(classId)

    //if not found try again and check if it's a java type
    if (result == null && !signature.contains('/') && !signature.startsWith("java")) {
        return findClass("java$signature")
    }

    return result
}

/**
 * Finds a function by its signature in the given class.
 *
 * Searches for a function in the class and its companion object. The function signature should include the name
 * and optionally the parameter types. If multiple matching functions are found, an exception is thrown.
 *
 * @param pluginContext The IR plugin context.
 * @param signature The signature of the function to find, in the format "functionName(params?)".
 *                  "*" can be used as parameter if from here the parameters are arbitrary. It can only be used once and only at the end of the signature.
 *                  "G" can be used as a placeholder for generic parameters.
 * @param extensionReceiverType The type of the extension receiver, if the function is an extension function.
 * @param ignoreNullability Whether to ignore nullability when comparing parameter types.
 * @return The found function symbol or null if it was not found.
 * @throws IllegalStateException If multiple matching functions are found.
 */

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrClassSymbol.findFunction(pluginContext: IrPluginContext, signature: String, extensionReceiverType: IrType? = null, ignoreNullability: Boolean = false): IrSimpleFunctionSymbol? {
    val (functionName, params, paramKinds) = parseFunctionParameters(pluginContext, signature)

    // search in class
    val matchingFunctionsInClass = this.functions.filter { func ->
        func.owner.name.asString() == functionName &&
                checkMethodSignature(func, params, paramKinds, ignoreNullability) &&
                checkExtensionFunctionReceiverType(func, extensionReceiverType)
    }
    when (matchingFunctionsInClass.count()) {
        0 -> doNothing()
        1 -> return matchingFunctionsInClass.single()
        else -> throw IllegalStateException("Multiple matching functions found in class for signature: $signature")
    }

    // search in companion object
    val companionObject = this.owner.declarations.filterIsInstance<IrClass>()
        .firstOrNull { it.isCompanion }

    val matchingFunctionsInCompanion = companionObject?.declarations
        ?.filterIsInstance<IrSimpleFunction>()
        ?.filter { func ->
            func.name.asString() == functionName &&
                    checkMethodSignature(func.symbol, params, paramKinds, ignoreNullability) &&
                    checkExtensionFunctionReceiverType(func.symbol, extensionReceiverType)
        } ?: emptyList()

    return when (matchingFunctionsInCompanion.count()) {
        0 -> null
        1 -> matchingFunctionsInCompanion.single().symbol
        else -> throw IllegalStateException("Multiple matching functions found in class for signature: $signature")
    }
}

/**
 * Finds a property by its name in the given class.
 *
 * @param signature The name of the property to find.
 * @return The found property symbol or null if it was not found.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrClassSymbol.findProperty(signature: String): IrPropertySymbol? = this.owner.properties.find { it.name.asString().lowercase() == signature.lowercase() }?.symbol

/**
 * Finds a constructor by its signature in the given class.
 *
 * Searches for a constructor with the specified parameter types. If multiple matching constructors are found,
 * an exception is thrown.
 *
 * @param pluginContext The IR plugin context.
 * @param signature The signature of the constructor's parameters to find. Defaults to "()".
 *                  "*" can be used as parameter if from here the parameters are arbitrary. It can only be used once and only at the end of the signature.
 *                   "G" can be used as a placeholder for generic parameters.
 * @param ignoreNullability Whether to ignore nullability when comparing parameter types.
 * @return The found constructor symbol or null if it was not found.
 * @throws IllegalStateException If multiple matching constructors are found.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrClassSymbol.findConstructor(pluginContext: IrPluginContext, signature: String = "()", ignoreNullability: Boolean = false): IrConstructorSymbol? {
    val (_, expectedParams, paramKinds) = parseFunctionParameters(pluginContext, "<init>$signature")

    val cons = this.constructors.filter { constructor ->
        checkMethodSignature(constructor, expectedParams, paramKinds, ignoreNullability)
    }
    return when (cons.count()) {
        0 -> null
        1 -> cons.single()
        else -> throw IllegalStateException("Multiple matching constructors found in class for signature: $signature")
    }
}

/**
 * Returns the class type of the variable if the type is a class type.
 *
 * @throws IllegalArgumentException if the variable's type is not a class type.
 * @return The class type of the variable.
 */
fun IrVariable.getTypeClass() = this.type.getClass() ?: throw IllegalArgumentException("Type is not a class")

/**
 * Searches for a property by its name in the class type of the variable.
 *
 * @param name The name of the property to search for.
 * @return The found property symbol or `null` if the property is not found.
 * @throws IllegalArgumentException If the type of the variable is not a class.
 */
fun IrVariable.findProperty(name: String): IrPropertySymbol? = this.getTypeClass().symbol.findProperty(name)

/**
 * Finds a function by its signature in the class type of the variable.
 *
 * @param pluginContext The IR plugin context used to resolve the function.
 * @param signature The signature of the function to find. It should be in the format "functionName(params?)".
 *                  "*" can be used as parameter if from here the parameters are arbitrary. It can only be used once and only at the end of the signature.
 *                  "G" can be used as a placeholder for generic parameters.
 * @param extensionReceiverType The type of the extension receiver, if the function is an extension function.
 * @param ignoreNullability Whether to ignore nullability when comparing the parameters.
 * @return The found function symbol or null if it was not found.
 * @throws IllegalStateException If multiple matching functions are found.
 */
fun IrVariable.findFunction(pluginContext: IrPluginContext, signature: String, extensionReceiverType: IrType? = null, ignoreNullability: Boolean = false) = try {
        val classSymbol = this.getTypeClass().symbol
        classSymbol.findFunction(pluginContext, signature, extensionReceiverType, ignoreNullability)
    } catch (e: Exception) {
        null
    }

/**
 * Finds a constructor by its signature in the class type of the variable.
 *
 * @param pluginContext The IR plugin context used to resolve the constructor.
 * @param signature The signature of the constructor to find. Defaults to "()".
 *                  "*" can be used as parameter if from here the parameters are arbitrary. It can only be used once and only at the end of the signature.
 *                  "G" can be used as a placeholder for generic parameters.
 * @param ignoreNullability Whether to ignore nullability when comparing the parameters.
 * @return The found constructor symbol or null if it was not found.
 * @throws IllegalStateException If multiple matching constructors are found.
 */
fun IrVariable.findConstructor(pluginContext: IrPluginContext, signature: String = "()", ignoreNullability: Boolean = false) = try {
    val classSymbol = this.getTypeClass().symbol
    classSymbol.findConstructor(pluginContext, signature, ignoreNullability)
} catch (e: Exception) {
    null
}
/**
 * Finds a function by its signature in the class type of the property.
 *
 * @param pluginContext The IR plugin context used to resolve the function.
 * @param signature The signature of the function to find. It should be in the format "functionName(params?)".
 *                  "*" can be used as parameter if from here the parameters are arbitrary. It can only be used once and only at the end of the signature.
 *                  "G" can be used as a placeholder for generic parameters.
 * @param extensionReceiverType The type of the extension receiver, if the function is an extension function.
 * @param ignoreNullability Whether to ignore nullability when comparing the parameters.
 * @return The found function symbol or null if it was not found.
 * @throws IllegalStateException If multiple matching functions are found.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPropertySymbol.findFunction(pluginContext: IrPluginContext, signature: String, extensionReceiverType: IrType? = null, ignoreNullability: Boolean = false) = try {
    val classSymbol = this.owner.getter?.returnType?.getClass()?.symbol
    classSymbol?.findFunction(pluginContext, signature, extensionReceiverType, ignoreNullability)
} catch (e: Exception) {
    null
}

    /**
     * Finds a constructor by its signature in the given package or class.
     *
     * @param signature The signature of the constructor to find. It should be in the format
     *                  "package/outerClasses.ClassName(params?)".
     *                  "*" can be used as parameter if from here the parameters are arbitrary. It can only be used once and only at the end of the signature.
     *                  "G" can be used as a placeholder for generic parameters.
     *                  Some standard packages are predefined, and if a package is not found in kotlin it is searched for in the java context.
     *   See [getTypePackage] for predefined Packages
     * @param ignoreNullability Whether to ignore nullability when comparing the parameters.
     * @return The found constructor symbol or null if it was not found.
     * @throws IllegalStateException If multiple matching constructors are found.
     */
fun IrPluginContext.findConstructor(signature: String, ignoreNullability: Boolean = false): IrConstructorSymbol? {
    val (_, outerClasses, constructorPart, packageForFindClass) = parseSignature(signature)
    val (className, _) = parseFunctionParameters(this, constructorPart)

    val classSymbol = if (className.isNotBlank()) {
        findClass("$packageForFindClass/$outerClasses${if (outerClasses.isNotEmpty()) "." else ""}$className")
    } else {
        null
    }

    return classSymbol?.findConstructor(this, "(" +constructorPart.substringAfter("("), ignoreNullability)
}

    /**
     * Finds a function by its signature in the given package or class.
     *
     * @param signature The signature of the function to find. It should be in the format
     *                  "package/outerClasses.ClassName.funcName(params?)".
     *                  "*" can be used as parameter if from here the parameters are arbitrary. It can only be used once and only at the end of the signature.
     *                  "G" can be used as a placeholder for generic parameters.
     *                  Some standard packages are predefined, and if a package is not found in kotlin it is searched for in the java context.
     *   See [getTypePackage] for predefined Packages
     * @param extensionReceiverType The expected type of the extension receiver, if any.
     * @param ignoreNullability Whether to ignore nullability when comparing the parameters.
     * @return The found function symbol or null if it was not found.
     * @throws IllegalStateException If multiple matching constructors are found.
     */
    fun IrPluginContext.findFunction(signature: String, extensionReceiverType: IrType? = null, ignoreNullability: Boolean = false): IrSimpleFunctionSymbol? {
    val (packageName, className, functionPart, packageForFindClass) = parseSignature(signature)
    val (functionName, params, paramKinds) = parseFunctionParameters(this, functionPart)

    var classSymbol: IrClassSymbol? = null
    var propertySymbol: IrPropertySymbol? = null

    if (className.isNotBlank()) {
        classSymbol = findClass("$packageForFindClass/$className")
        propertySymbol = findProperty("$packageForFindClass/$className")
    }

    val classSearch = classSymbol?.findFunction(this, functionPart, extensionReceiverType, ignoreNullability)
                        ?: propertySymbol?.findFunction(this, functionPart, extensionReceiverType, ignoreNullability)

    if (classSearch == null) {
        val plainSearch = referenceFunctions(CallableId(FqName(packageName), Name.identifier(functionName)))
            .filter { func ->
                checkMethodSignature(func, params, paramKinds, ignoreNullability) &&
                        checkExtensionFunctionReceiverType(func, extensionReceiverType)
            }
        return when (plainSearch.count()) {
            0 -> null
            1 -> plainSearch.single()
            else -> throw IllegalStateException("Multiple matching constructors found in class for signature: $signature")
        }
    } else return classSearch
}

    /**
     * Finds a property by its signature in the given package or class.
     *
     * @param signature The signature of the property to find. It should be in the format
     *                  "package/outerClasses.ClassName.propertyName".
     *                  Some standard packages are predefined, and if a package is not found in kotlin it is searched for in the java context.
     *   See [getTypePackage] for predefined Packages
     * @return The found property symbol or null if it was not found.
     */
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

/**
 * Attempts to resolve the IR type for a given type string. The method first checks for primitive types,
 * then primitive arrays, and finally tries to locate a class with the specified name to return its type.
 *
 * @param typeString The type string to resolve. Primitive types can be specified in short form (e.g., int, short, etc.).
 * Additional types are supported in short form; see [getTypePackage] for details.
 * Alternatively, the type string can be in the format "package.ClassName<typeParameters>" for a full class search.
 *
 * @return The resolved IR type, or `null` if the type string contains "*" (wildcard) or "G" (generic placeholder).
 * @throws IllegalArgumentException If the type cannot be resolved.
 */
fun IrPluginContext.getIrType(typeString: String): IrType? {
    val isNullable = typeString.endsWith("?", ignoreCase = true)
    val baseType = if (isNullable) typeString.removeSuffix("?").lowercase() else typeString.lowercase()

    val type = when (baseType) {
        // primitive types
        "int", "integer" -> this.irBuiltIns.intType
        "long" -> this.irBuiltIns.longType
        "short" -> this.irBuiltIns.shortType
        "byte" -> this.irBuiltIns.byteType
        "boolean" -> this.irBuiltIns.booleanType
        "char", "character" -> this.irBuiltIns.charType
        "float" -> this.irBuiltIns.floatType
        "double" -> this.irBuiltIns.doubleType
        "void" -> this.irBuiltIns.unitType
        "unit" -> this.irBuiltIns.unitType
        "nothing" -> this.irBuiltIns.nothingType
        "any", "object" -> this.irBuiltIns.anyType

        //special handeling
        "g", "*" -> return null

        //primitive arrays
        "chararray", "characterarray" -> this.irBuiltIns.charArray.defaultType
        "bytearray" -> this.irBuiltIns.byteArray.defaultType
        "shortarray" -> this.irBuiltIns.shortArray.defaultType
        "intarray", "integerarray" -> this.irBuiltIns.intArray.defaultType
        "longarray" -> this.irBuiltIns.longArray.defaultType
        "floatarray" -> this.irBuiltIns.floatArray.defaultType
        "doublearray" -> this.irBuiltIns.doubleArray.defaultType
        "booleanarray" -> this.irBuiltIns.booleanArray.defaultType

        //handle other types dynamically
        else -> {
            val fqName = getTypePackage(baseType)

            val classSymbol = if (fqName == baseType) null else findClass(fqName)
            classSymbol?.defaultType ?: throw IllegalArgumentException("getIrType: Type $typeString not found in context")
        }
    }

    return if (isNullable) type.makeNullable() else type
}

/**
 * Verifies if the given function symbol matches the specified parameter types.
 *
 * There are two modes of verification. The first mode attempts to match the exact number of parameters.
 * If this fails, the second mode is used, which ignores default parameters.
 *
 * @param func The function symbol to verify.
 * @param paramTypes The parameter types to verify against.
 * @param paramKinds The kinds of parameters (e.g., normal, generic, or wildcard).
 * @param ignoreNullability Specifies whether nullability should be ignored when comparing parameters.
 * @return `true` if the function symbol matches the specified parameter types, otherwise `false`.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun checkMethodSignature(
    func: IrFunctionSymbol,
    paramTypes: List<IrType>,
    paramKinds: List<SignatureType>,
    ignoreNullability: Boolean = false
): Boolean {
    val parameters = func.owner.valueParameters

        return if (paramKinds.contains(SignatureType.Star)) {
            // Check if the given parameters match the first few
            if (parameters.size < paramTypes.size) return false //ignore generic types
            paramTypes.withIndex().all { (index, actualType) ->
                if(paramKinds[index] == SignatureType.Generic && parameters[index].type.isGenericType()){
                    true //ignore generic types
                } else {
                    parameters[index].type.equalsIgnorePlatform(actualType, ignoreNullability)
                }
            }
        } else {
            // Check exact match
            if (parameters.size != paramKinds.size) return false
            if (paramKinds.filter { it != SignatureType.Generic }.size != paramTypes.size) return false
            parameters.withIndex().all { (index, expectedType) ->
                if (paramKinds[index] == SignatureType.Generic && parameters[index].type.isGenericType()){
                    true //ignore generic types
                } else {
                    if(paramTypes.size > index) {
                        paramTypes[index].type.equalsIgnorePlatform(expectedType.type, ignoreNullability)
                    } else {
                        return false //if there are not enough parameters to match
                    }
                }
            }
        }
}

/**
 * Checks if the current type is a generic type.
 *
 * A type is considered generic if it is an `IrSimpleType` and its classifier is an `IrTypeParameterSymbol`.
 * This method also handles nullability by ensuring the type is non-null before performing the check.
 *
 * @return `true` if the type is generic, `false` otherwise.
 */
private fun IrType.isGenericType() = when (type) {
        is IrSimpleType -> type.classifierOrNull is IrTypeParameterSymbol
        else -> type.makeNotNull().let { it is IrSimpleType && it.classifier is IrTypeParameterSymbol }
    }

    /**
     * Checks if the given function symbol has the given extension receiver type.
     *
     * @param func The function symbol to check.
     * @param extensionReceiverType The expected type of the extension receiver, if any.
     * @return `true` if the function symbol matches the given extension receiver type, `false` otherwise.
     */
@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun checkExtensionFunctionReceiverType(func: IrSimpleFunctionSymbol, extensionReceiverType: IrType? = null) = extensionReceiverType?.let {
        func.owner.extensionReceiverParameter?.type?.equalsIgnorePlatform(it)
    } ?: true

    /**
     * Parses a signature into its components.
     *
     * The signature is split into four parts: the package name, the class name, the member name, and the package name as it should be used with [IrPluginContext.findClass].
     *
     * @param signature The signature to parse. It should be in the format "package/outerClasses.Member".
     * @return The parsed signature parts.
     */
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

/**
 * Parses a function signature string to extract the function name and its parameter types.
 *
 * The function expects the signature to be in the format "functionName(paramType1, paramType2, ...)".
 * It extracts the function name and resolves the parameter types using the provided `IrPluginContext`.
 *
 * @param pluginContext The context used to resolve parameter types.
 * @param signature The function signature string to parse.
 * @return A Triple containing the function name, a list of resolved parameter types, and a list of parameter kinds.
 * @throws IllegalArgumentException If the signature does not contain parentheses
 *                                  "*" is used more than once or in the wrong place.
 *                                  And also if a parameter type cannot be resolved.
 */
private fun parseFunctionParameters(pluginContext: IrPluginContext, signature: String): Triple<String, List<IrType>, List<SignatureType>> {
    //functions must have parenthesis
    require(signature.contains('(')) { "The signature must contain opening parentheses." }
    require(signature.contains(')')) { "The signature must contain closing parentheses." }
    //"*" can only be used once and only at the end of the signature
    require(signature.count { it == '*' } <= 1) { "The signature must contain '*' at most once." }
    //-2 because closing parenthesis is expected after "*"
    require(signature.lastIndexOf('*') == signature.length - 2 || !signature.contains('*')) { "The '*' must be the last symbol in the signature if present." }

    val functionName = signature.substringBefore("(")
    val paramsString = signature.substringAfter("(").substringBefore(")")

    //parse parameters
    val params = if (paramsString.isBlank()) {
        emptyList()
    } else {
        paramsString.split(",").map { it.trim() }.mapNotNull  { pluginContext.getIrType(it) }
    }

    val paramKinds = if (paramsString.isBlank()) {
        emptyList()
    } else {
        paramsString.split(",").map { it.trim() }.map  {
            when (it) {
                "*" -> SignatureType.Star
                "G" -> SignatureType.Generic
                else -> SignatureType.Type
            }
        }
    }

    return Triple(functionName, params, paramKinds)
}

/**
 * Compares this type with another type, ignoring platform-specific differences.
 *
 * If either type is a platform type, the comparison ignores nullability and compares
 * their erased upper bounds instead. Otherwise, the types are compared directly.
 *
 * @param t2 The type to compare with this type.
 * @param ignoreNullability Whether to ignore nullability when comparing the types.
 * @return `true` if the types are considered equal, `false` otherwise.
 */
fun IrType.equalsIgnorePlatform(t2: IrType, ignoreNullability: Boolean = false): Boolean {
    val type1 = if (ignoreNullability) this.makeNotNull() else this
    val type2 = if (ignoreNullability) t2.makeNotNull() else t2
    //handle platform types
    if (type1.isPlatformType() || type2.isPlatformType()) {
        //for platform types ignore nullability
        return type1.erasedUpperBound() == type2.erasedUpperBound()
    }

    return type1 == type2
}

/**
 * Checks if the type is a platform type.
 *
 * A platform type is a type that is explicitly annotated with the "PlatformType" annotation.
 * This annotation is used by the Kotlin compiler to indicate that a type is a platform type,
 * which are types that may be different on different platforms.
 *
 * @return true if the type is a platform type, false otherwise.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrType.isPlatformType(): Boolean {
    return this is IrSimpleType && annotations.any {
        it.symbol.owner.name.asString() == "PlatformType"
    }
}

/**
 * Returns the upper bound of the type, ignoring nullability.
 *
 * If the type is a [IrSimpleType], it returns the non-null version of the type.
 * Otherwise, it returns the type as is.
 *
 * @return The upper bound of the type, ignoring nullability.
 */
fun IrType.erasedUpperBound(): IrType {
    return when {
        this is IrSimpleType -> makeNotNull()
        else -> this
    }
}

/**
 * Maps a type string to its corresponding package name.
 *
 * @param typeString the type string to map
 * @return the corresponding package name
 */
private fun getTypePackage(typeString: String) = when (typeString) {
    //basic types
    "string" -> "kotlin/String"
    "javastring" -> "java/lang/String"
    "array" -> "kotlin/Array"
    "javaarray" -> "java/lang/Array"
    "throwable" -> "kotlin/Throwable"
    "javathrowable" -> "java/lang/Throwable"
    "exception" -> "kotlin/Exception"
    "javaexception" -> "java/lang/Exception"
    "runtimeexception" -> "kotlin/RuntimeException"
    "javaruntimeexception" -> "java/lang/RuntimeException"
    "error" -> "kotlin/Error"
    "javaerror" -> "java/lang/Error"

    //char related
    "char" -> "kotlin/Char"
    "javachar" -> "java/lang/Character"
    "charsequence" -> "kotlin/CharSequence"
    "javacharsequence" -> "java/lang/CharSequence"
    "chararray" -> "kotlin/CharArray"
    "javachararray" -> "java/lang/CharArray"
    "stringbuilder" -> "kotlin/text/StringBuilder"
    "javastringbuilder" -> "java/lang/StringBuilder"
    "stringbuffer" -> "java/lang/StringBuffer"

    //primitive kotlin arrays
    "chararray" -> "kotlin/CharArray"
    "bytearray" -> "kotlin/ByteArray"
    "shortarray" -> "kotlin/ShortArray"
    "intarray" -> "kotlin/IntArray"
    "longarray" -> "kotlin/LongArray"
    "floatarray" -> "kotlin/FloatArray"
    "doublearray" -> "kotlin/DoubleArray"
    "booleanarray" -> "kotlin/BooleanArray"

    //collections
    "list" -> "kotlin/collections/List"
    "javalist" -> "java/util/List"
    "mutablelist" -> "kotlin/collections/MutableList"
    "set" -> "kotlin/collections/Set"
    "javaset" -> "java/util/Set"
    "mutableset" -> "kotlin/collections/MutableSet"
    "map" -> "kotlin/collections/Map"
    "javamap" -> "java/util/Map"
    "mutablemap" -> "kotlin/collections/MutableMap"
    "collection" -> "kotlin/collections/Collection"
    "javacollection" -> "java/util/Collection"
    "mutablecollection" -> "kotlin/collections/MutableCollection"
    "iterable" -> "kotlin/collections/Iterable"
    "javaiterable" -> "java/lang/Iterable"
    "mutableiterable" -> "kotlin/collections/MutableIterable"

    //further java collections
    "arraylist" -> "java/util/ArrayList"
    "linkedlist" -> "java/util/LinkedList"
    "vector" -> "java/util/Vector"
    "stack" -> "java/util/Stack"
    "hashset" -> "java/util/HashSet"
    "linkedhashset" -> "java/util/LinkedHashSet"
    "treeset" -> "java/util/TreeSet"
    "hashmap" -> "java/util/HashMap"
    "linkedhashmap" -> "java/util/LinkedHashMap"
    "treemap" -> "java/util/TreeMap"
    "hashtable" -> "java/util/Hashtable"
    "queue" -> "java/util/Queue"
    "deque" -> "java/util/Deque"
    "priorityqueue" -> "java/util/PriorityQueue"
    "arraydeque" -> "java/util/ArrayDeque"
    "concurrenthashmap" -> "java/util/concurrent/ConcurrentHashMap"
    "concurrentlinkedqueue" -> "java/util/concurrent/ConcurrentLinkedQueue"
    "blockingqueue" -> "java/util/concurrent/BlockingQueue"
    "linkedblockingqueue" -> "java/util/concurrent/LinkedBlockingQueue"

    //kotlin specific
    "sequence" -> "kotlin/sequences/Sequence"
    "mutablesequence" -> "kotlin/sequences/MutableSequence"
    "pair", "tuple2" -> "kotlin/Pair"
    "triple", "tuple3" -> "kotlin/Triple"
    "range" -> "kotlin/ranges/Range"
    "progression" -> "kotlin/ranges/Progression"
    "regex" -> "kotlin/text/Regex"

    //java utilities
    "optional" -> "java/util/Optional"
    "stream" -> "java/util/stream/Stream"
    "date" -> "java/util/Date"
    "calendar" -> "java/util/Calendar"
    "locale" -> "java/util/Locale"
    "timezone" -> "java/util/TimeZone"
    "uuid" -> "java/util/UUID"

    //java Time
    "localdate" -> "java/time/LocalDate"
    "localtime" -> "java/time/LocalTime"
    "localdatetime" -> "java/time/LocalDateTime"
    "zoneddatetime" -> "java/time/ZonedDateTime"
    "instant" -> "java/time/Instant"
    "duration" -> "java/time/Duration"
    "kotlinduration" -> "kotlin/time/Duration"
    "period" -> "java/time/Period"

    //java IO
    "file" -> "java/io/File"
    "inputstream" -> "java/io/InputStream"
    "outputstream" -> "java/io/OutputStream"
    "reader" -> "java/io/Reader"
    "writer" -> "java/io/Writer"
    "bufferedreader" -> "java/io/BufferedReader"
    "bufferedwriter" -> "java/io/BufferedWriter"
    "printwriter" -> "java/io/PrintWriter"
    "printstream" -> "java/io/PrintStream"

    //java NIO
    "path" -> "java/nio/file/Path"
    "files" -> "java/nio/file/Files"
    "channel" -> "java/nio/channels/Channel"
    "bytebuffer" -> "java/nio/ByteBuffer"
    "charbuffer" -> "java/nio/CharBuffer"

    //primitive types and their wrappers
    "boolean" -> "kotlin/Boolean"
    "javaboolean" -> "java/lang/Boolean"
    "byte" -> "kotlin/Byte"
    "javabyte" -> "java/lang/Byte"
    "short" -> "kotlin/Short"
    "javashort" -> "java/lang/Short"
    "int" -> "kotlin/Int"
    "javaint" -> "java/lang/Integer"
    "long" -> "kotlin/Long"
    "javalong" -> "java/lang/Long"
    "float" -> "kotlin/Float"
    "javafloat" -> "java/lang/Float"
    "double" -> "kotlin/Double"
    "javadouble" -> "java/lang/Double"

    //further java types
    "biginteger" -> "java/math/BigInteger"
    "bigdecimal" -> "java/math/BigDecimal"
    "pattern" -> "java/util/regex/Pattern"
    "matcher" -> "java/util/regex/Matcher"

    //reflection
    "class" -> "kotlin/reflect/KClass"
    "javaclass" -> "java/lang/Class"
    "method" -> "kotlin/reflect/KFunction"
    "javamethod" -> "java/lang/reflect/Method"
    "field" -> "kotlin/reflect/KProperty"
    "javafield" -> "java/lang/reflect/Field"
    "constructor" -> "kotlin/reflect/KFunction"
    "javaconstructor" -> "java/lang/reflect/Constructor"

    //thread related
    "thread" -> "kotlin/concurrent/Thread"
    "javathread" -> "java/lang/Thread"
    "runnable" -> "kotlin/Runnable"
    "javarunnable" -> "java/lang/Runnable"
    "callable" -> "java/util/concurrent/Callable"
    "future" -> "java/util/concurrent/Future"
    "completablefuture" -> "java/util/concurrent/CompletableFuture"
    "executorservice" -> "java/util/concurrent/ExecutorService"

    else -> typeString //here we assume we have fully qualified name
}