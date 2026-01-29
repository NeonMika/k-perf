package at.jku.ssw.kir.find.irplugincontext

import at.jku.ssw.kir.find.checkExtensionFunctionReceiverType
import at.jku.ssw.kir.find.checkMethodSignature
import at.jku.ssw.kir.find.resolveDefaultTypeNameToFQTypeName
import at.jku.ssw.kir.find.irclasssymbol.findConstructorOrNull
import at.jku.ssw.kir.find.irclasssymbol.findFunctionOrNull
import at.jku.ssw.kir.find.irclasssymbol.findPropertyOrNull
import at.jku.ssw.kir.find.irplugincontext.IrPluginContextExtensions.findClassOrNull
import at.jku.ssw.kir.find.irpropertysymbol.findFunctionOrNull
import at.jku.ssw.kir.find.parseFunctionParameters
import at.jku.ssw.kir.find.parseSignature
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object IrPluginContextExtensions {

  /**
   * Finds a class by its signature.
   *
   * The signature is a string that uniquely identifies a class. It consists of the package name and the class name.
   * The class name is the last part of the signature, and the package name is everything before the last '/'.
   * If the class is not found in Kotlin, it attempts to find it in the Java context.
   * See [at.jku.ssw.kir.find.resolveDefaultTypeNameToFQTypeName] for predefined packages.
   *
   * @param signature The signature of the class to find in the form "package/outerClasses.ClassName".
   * @return The found class symbol or null if it was not found.
   * @throws IllegalArgumentException If the signature does not include a package path.
   */
  @OptIn(UnsafeDuringIrConstructionAPI::class)
  fun IrPluginContext.findClassOrNull(signature: String): IrClassSymbol? {
    val packageSearch = resolveDefaultTypeNameToFQTypeName(signature.lowercase())
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
      return findClassOrNull("java$signature")
    }

    return result
  }

}

/**
 * Finds a class by its signature.
 * See [findClassOrNull] for details.
 *
 * @throws IllegalArgumentException If the class with the given signature is not found.
 */
fun IrPluginContext.findClass(signature: String): IrClassSymbol =
  this.findClassOrNull(signature) ?: throw IllegalArgumentException("Class with signature $signature not found")

/**
 * Finds a constructor by its signature in the given package or class.
 *
 * @param signature The signature of the constructor to find. It should be in the format
 *                  "package/outerClasses.ClassName(params?)".
 *                  "*" can be used as parameter if from here the parameters are arbitrary. It can only be used once and only at the end of the signature.
 *                  "G" can be used as a placeholder for generic parameters.
 *                  Some standard packages are predefined, and if a package is not found in kotlin it is searched for in the java context.
 *   See [resolveDefaultTypeNameToFQTypeName] for predefined Packages
 * @param ignoreNullability Whether to ignore nullability when comparing the parameters.
 * @return The found constructor symbol or null if it was not found.
 * @throws IllegalStateException If multiple matching constructors are found.
 */
fun IrPluginContext.findConstructorOrNull(
  signature: String,
  ignoreNullability: Boolean = false
): IrConstructorSymbol? {
  val (_, outerClasses, constructorPart, packageForFindClass) = parseSignature(signature)
  val (className, _) = parseFunctionParameters(this, constructorPart)

  val classSymbol = if (className.isNotBlank()) {
    findClassOrNull("$packageForFindClass/$outerClasses${if (outerClasses.isNotEmpty()) "." else ""}$className")
  } else {
    null
  }

  return classSymbol?.findConstructorOrNull(this, "(" + constructorPart.substringAfter("("), ignoreNullability)
}

/**
 * Finds a constructor by its signature.
 * See [IrPluginContext.findConstructorOrNull] for details.
 *
 * @throws IllegalArgumentException If the constructor with the given signature is not found.
 */
fun IrPluginContext.findConstructor(signature: String, ignoreNullability: Boolean = false): IrConstructorSymbol =
  this.findConstructorOrNull(signature, ignoreNullability)
    ?: throw IllegalArgumentException("Constructor with signature $signature not found in context")

/**
 * Finds a function by its signature in the given package or class.
 *
 * @param signature The signature of the function to find. It should be in the format
 *                  "package/outerClasses.ClassName.funcName(params?)".
 *                  "*" can be used as parameter if from here the parameters are arbitrary. It can only be used once and only at the end of the signature.
 *                  "G" can be used as a placeholder for generic parameters.
 *                  Some standard packages are predefined, and if a package is not found in kotlin it is searched for in the java context.
 *   See [resolveDefaultTypeNameToFQTypeName] for predefined Packages
 * @param extensionReceiverType The expected type of the extension receiver, if any.
 * @param ignoreNullability Whether to ignore nullability when comparing the parameters.
 * @return The found function symbol or null if it was not found.
 * @throws IllegalStateException If multiple matching constructors are found.
 */
fun IrPluginContext.findFunctionOrNull(
  signature: String,
  extensionReceiverType: IrType? = null,
  ignoreNullability: Boolean = false
): IrSimpleFunctionSymbol? {
  val (packageName, className, functionPart, packageForFindClass) = parseSignature(signature)
  val (functionName, params, paramKinds) = parseFunctionParameters(this, functionPart)

  var classSymbol: IrClassSymbol? = null
  var propertySymbol: IrPropertySymbol? = null

  if (className.isNotBlank()) {
    classSymbol = findClassOrNull("$packageForFindClass/$className")
    propertySymbol = findPropertyOrNull("$packageForFindClass/$className")
  }

  val classSearch = classSymbol?.findFunctionOrNull(this, functionPart, extensionReceiverType, ignoreNullability)
    ?: propertySymbol?.findFunctionOrNull(this, functionPart, extensionReceiverType, ignoreNullability)

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
 * Finds a function by its signature.
 * See [IrPluginContext.findFunctionOrNull] for details.
 *
 * @throws IllegalArgumentException If the function with the given signature is not found.
 */
fun IrPluginContext.findFunction(
  signature: String,
  extensionReceiverType: IrType? = null,
  ignoreNullability: Boolean = false
): IrSimpleFunctionSymbol =
  this.findFunctionOrNull(signature, extensionReceiverType, ignoreNullability)
    ?: throw IllegalArgumentException("Function with signature $signature not found in context")

/**
 * Finds a property by its signature in the given package or class.
 *
 * @param signature The signature of the property to find. It should be in the format
 *                  "package/outerClasses.ClassName.propertyName".
 *                  Some standard packages are predefined, and if a package is not found in kotlin it is searched for in the java context.
 *   See [resolveDefaultTypeNameToFQTypeName] for predefined Packages
 * @return The found property symbol or null if it was not found.
 */
fun IrPluginContext.findPropertyOrNull(signature: String): IrPropertySymbol? {
  val parts = parseSignature(signature)

  val classSymbol = if (parts.className.isNotBlank()) {
    findClassOrNull("${parts.packageForFindClass}/${parts.className}")
  } else {
    null
  }

  return classSymbol?.findPropertyOrNull(parts.memberName)
    ?: referenceProperties(CallableId(FqName(parts.packageName), Name.identifier(parts.memberName)))
      .singleOrNull()
}

/**
 * Finds a property by its signature.
 * See [IrPluginContext.findPropertyOrNull] for details.
 *
 * @throws IllegalArgumentException If the property with the given signature is not found.
 */
fun IrPluginContext.findProperty(signature: String): IrPropertySymbol =
  this.findPropertyOrNull(signature)
    ?: throw IllegalArgumentException("Property with signature $signature not found in context")

/**
 * Attempts to resolve the IR type for a given type string. The method first checks for primitive types,
 * then primitive arrays, and finally tries to locate a class with the specified name to return its type.
 *
 * @param typeString The type string to resolve. Primitive types can be specified in short form (e.g., int, short, etc.).
 * Additional types are supported in short form; see [resolveDefaultTypeNameToFQTypeName] for details.
 * Alternatively, the type string can be in the format "package.ClassName<typeParameters>" for a full class search.
 *
 * @return The resolved IR type, or `null` if the type string contains "*" (wildcard) or "G" (generic placeholder).
 * @throws IllegalArgumentException If the type cannot be resolved.
 */
fun IrPluginContext.getIrType(typeString: String): IrType? {
  val stripped = typeString.replace(" ", "").lowercase()
  val isNullable = stripped.endsWith("?", ignoreCase = true)
  val baseType = if (isNullable) stripped.removeSuffix("?") else stripped

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
      val fqName = resolveDefaultTypeNameToFQTypeName(baseType)

      val classSymbol = if (fqName == baseType) null else findClassOrNull(fqName)
      classSymbol?.defaultType ?: throw IllegalArgumentException("getIrType: Type $typeString not found in context")
    }
  }

  return if (isNullable) type.makeNullable() else type
}