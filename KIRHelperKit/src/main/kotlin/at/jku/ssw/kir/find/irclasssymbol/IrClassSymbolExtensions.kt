package at.jku.ssw.kir.find.irclasssymbol

import at.jku.ssw.kir.find.checkExtensionFunctionReceiverType
import at.jku.ssw.kir.find.checkMethodSignature
import at.jku.ssw.kir.find.parseFunctionParameters
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.utils.doNothing

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
fun IrClassSymbol.findFunctionOrNull(
  pluginContext: IrPluginContext,
  signature: String,
  extensionReceiverType: IrType? = null,
  ignoreNullability: Boolean = false
): IrSimpleFunctionSymbol? {
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
 * Finds a function by its signature in the given class.
 * See [findFunctionOrNull] for details.
 *
 * @throws IllegalArgumentException If the function with the given signature is not found in the class.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrClassSymbol.findFunction(
  pluginContext: IrPluginContext,
  signature: String,
  extensionReceiverType: IrType? = null,
  ignoreNullability: Boolean = false
): IrSimpleFunctionSymbol =
  this.findFunctionOrNull(pluginContext, signature, extensionReceiverType, ignoreNullability)
    ?: throw IllegalArgumentException("Function with signature $signature not found in class ${this.owner.name.asString()}")

/**
 * Finds a property by its name in the given class.
 *
 * @param signature The name of the property to find.
 * @return The found property symbol or null if it was not found.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrClassSymbol.findPropertyOrNull(signature: String): IrPropertySymbol? =
  this.owner.properties.find { it.name.asString().lowercase() == signature.lowercase() }?.symbol

/**
 * Finds a property by its name in the given class.
 * See [findPropertyOrNull] for details.
 *
 * @throws IllegalArgumentException If the property with the given signature is not found in the class.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrClassSymbol.findProperty(signature: String): IrPropertySymbol = this.findPropertyOrNull(signature)
  ?: throw IllegalArgumentException("Property with signature $signature not found in class ${this.owner.name.asString()}")

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
fun IrClassSymbol.findConstructorOrNull(
  pluginContext: IrPluginContext,
  signature: String = "()",
  ignoreNullability: Boolean = false
): IrConstructorSymbol? {
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
 * Finds a constructor by its signature in the given class.
 * See [findConstructorOrNull] for details.
 *
 * @throws IllegalArgumentException If the constructor with the given signature is not found in the class.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrClassSymbol.findConstructor(
  pluginContext: IrPluginContext,
  signature: String = "()",
  ignoreNullability: Boolean = false
): IrConstructorSymbol =
  this.findConstructorOrNull(pluginContext, signature, ignoreNullability)
    ?: throw IllegalArgumentException("Constructor with signature $signature not found in class ${this.owner.name.asString()}")