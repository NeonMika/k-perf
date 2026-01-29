package at.jku.ssw.kir.find.irtype

import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.types.makeNotNull

/**
 * Checks if the current type is a generic type.
 *
 * A type is considered generic if it is an `IrSimpleType` and its classifier is an `IrTypeParameterSymbol`.
 * This method also handles nullability by ensuring the type is non-null before performing the check.
 *
 * @return `true` if the type is generic, `false` otherwise.
 */
fun IrType.isGenericType() = when (this) {
  is IrSimpleType -> classifierOrNull is IrTypeParameterSymbol
  else -> makeNotNull().let { it is IrSimpleType && it.classifier is IrTypeParameterSymbol }
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