package at.jku.ssw.kir.find.irvariable

import at.jku.ssw.kir.find.irclasssymbol.findConstructorOrNull
import at.jku.ssw.kir.find.irclasssymbol.findFunctionOrNull
import at.jku.ssw.kir.find.irclasssymbol.findPropertyOrNull
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass

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
fun IrVariable.findPropertyOrNull(name: String): IrPropertySymbol? = this.getTypeClass().symbol.findPropertyOrNull(name)

/**
 * Finds a property by its name in the class type of the variable.
 * See [IrVariable.findPropertyOrNull] for details.
 *
 * @throws IllegalArgumentException If the property with the given name is not found in the variable.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrVariable.findProperty(name: String): IrPropertySymbol = this.findPropertyOrNull(name)
  ?: throw IllegalArgumentException("Property with name $name not found in variable ${this.name.asString()}")

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
 * @throws IllegalArgumentException If multiple matching functions are found (ambiguous signature).
 */
fun IrVariable.findFunctionOrNull(
  pluginContext: IrPluginContext,
  signature: String,
  extensionReceiverType: IrType? = null,
  ignoreNullability: Boolean = false
) = try {
  val classSymbol = this.getTypeClass().symbol
  classSymbol.findFunctionOrNull(pluginContext, signature, extensionReceiverType, ignoreNullability)
} catch (e: Exception) {
  null
}

/**
 * Finds a function by its signature in the class type of the variable.
 * See [IrVariable.findFunctionOrNull] for details.
 *
 * @throws IllegalArgumentException If the function with the given signature is not found in the variable.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrVariable.findFunction(
  pluginContext: IrPluginContext,
  signature: String,
  extensionReceiverType: IrType? = null,
  ignoreNullability: Boolean = false
) = this.findFunctionOrNull(pluginContext, signature, extensionReceiverType, ignoreNullability)
  ?: throw IllegalArgumentException("Function with signature $signature not found in variable ${this.name.asString()}")

/**
 * Finds a constructor by its signature in the class type of the variable.
 *
 * @param pluginContext The IR plugin context used to resolve the constructor.
 * @param signature The signature of the constructor to find. Defaults to "()".
 *                  "*" can be used as parameter if from here the parameters are arbitrary. It can only be used once and only at the end of the signature.
 *                  "G" can be used as a placeholder for generic parameters.
 * @param ignoreNullability Whether to ignore nullability when comparing the parameters.
 * @return The found constructor symbol or null if it was not found.
 * @throws IllegalArgumentException If multiple matching constructors are found (ambiguous signature).
 */
fun IrVariable.findConstructorOrNull(
  pluginContext: IrPluginContext, signature: String = "()", ignoreNullability: Boolean = false
) = try {
  val classSymbol = this.getTypeClass().symbol
  classSymbol.findConstructorOrNull(pluginContext, signature, ignoreNullability)
} catch (e: Exception) {
  null
}

/**
 * Finds a constructor by its signature in the class type of the variable.
 * See [IrVariable.findConstructorOrNull] for details.
 *
 * @throws IllegalArgumentException If the constructor with the given signature is not found in the variable.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrVariable.findConstructor(
  pluginContext: IrPluginContext, signature: String = "()", ignoreNullability: Boolean = false
) = this.findConstructorOrNull(pluginContext, signature, ignoreNullability)
  ?: throw IllegalArgumentException("Constructor with signature $signature not found in variable ${this.name.asString()}")