package at.jku.ssw.kir.find

import at.jku.ssw.kir.find.irtype.equalsIgnorePlatform
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStringConcatenation
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName


/**
 * Checks if the given function symbol has the given extension receiver type.
 *
 * @param func The function symbol to check.
 * @param extensionReceiverType The expected type of the extension receiver, if any.
 * @return `true` if the function symbol matches the given extension receiver type, `false` otherwise.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun checkExtensionFunctionReceiverType(func: IrSimpleFunctionSymbol, extensionReceiverType: IrType? = null) =
  extensionReceiverType?.let {
    func.owner.parameters.firstOrNull { p -> p.kind == IrParameterKind.ExtensionReceiver }?.type?.equalsIgnorePlatform(it)
  } ?: true

/**
 * Extracts the type of a given value as a string.
 *
 * @param value The value whose type is to be extracted.
 * @return The type of the value as a lowercase string.
 * @throws IllegalArgumentException If the type cannot be extracted.
 */
fun extractFQTypeNameFromIrNode(value: Any): String {
  val typeFqName = when (value) {
    is IrProperty -> value.backingField?.type?.classFqName
    is IrField -> value.type.classFqName
    is IrValueParameter -> value.type.classFqName
    is IrVariable -> value.type.classFqName
    is IrFunctionAccessExpression -> value.type.classFqName
    is IrStringConcatenation -> value.type.classFqName
    else -> return value::class.simpleName?.lowercase()
      ?: throw IllegalArgumentException("cannot extract type out of value $value")
  }

  return typeFqName?.shortName()?.asString()?.lowercase()
    ?: throw IllegalArgumentException("cannot extract type out of value $value")
}