package at.jku.ssw.kir.find.irpropertysymbol

import at.jku.ssw.kir.find.irclasssymbol.findFunctionOrNull
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass

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
 * @throws IllegalArgumentException If multiple matching functions are found (ambiguous signature).
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPropertySymbol.findFunctionOrNull(
  pluginContext: IrPluginContext,
  signature: String,
  extensionReceiverType: IrType? = null,
  ignoreNullability: Boolean = false
) = try {
  val classSymbol = this.owner.getter?.returnType?.getClass()?.symbol
  classSymbol?.findFunctionOrNull(pluginContext, signature, extensionReceiverType, ignoreNullability)
} catch (e: Exception) {
  null
}

/**
 * Finds a function by its signature in the class type of the property.
 * See [IrPropertySymbol.findFunctionOrNull] for details.
 *
 * @throws IllegalArgumentException If the function with the given signature is not found in the property.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPropertySymbol.findFunction(
  pluginContext: IrPluginContext,
  signature: String,
  extensionReceiverType: IrType? = null,
  ignoreNullability: Boolean = false
) =
  this.findFunctionOrNull(pluginContext, signature, extensionReceiverType, ignoreNullability)
    ?: throw IllegalArgumentException("Function with signature $signature not found in property ${this.owner.name.asString()}")