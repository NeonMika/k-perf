package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irInt
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.name.Name


fun createSuts(): List<IrField> = with(IoaContext.pluginContext) {
  return when (IoaContext.instrumentationKind) {
    IoaKind.TryFinally, IoaKind.IncrementIntCounter, IoaKind.RandomValue -> listOf(createFieldOfType(irBuiltIns.intType))

    IoaKind.IncrementAtomicIntCounter -> listOf(createFieldOfType(IoaContext.atomicIntegerClass.defaultType) {
      DeclarationIrBuilder(this, it.symbol)
        .irCallConstructor(IoaContext.atomicIntegerConstructor, listOf(irBuiltIns.intType))
        .apply { arguments[0] = DeclarationIrBuilder(this@with, it.symbol).irInt(0) }
    })

    IoaKind.AppendToStringBuilder -> listOf(createFieldOfType(IoaContext.stringBuilderClass.defaultType) {
        DeclarationIrBuilder(this, it.symbol)
            .irCallConstructor(IoaContext.stringBuilderConstructor, listOf())
    })

    else -> emptyList()
  }
}

fun IrPluginContext.createFieldOfType(type: IrType, suffix: String = "0", initializer: ((IrField) -> IrExpression)? = null): IrField {
  return irFactory.buildField {
    this.name = Name.identifier("__ioa_sut_$suffix")
    this.type = type
    this.isStatic = true
  }.also {
    if (initializer != null) {
      it.initializer = DeclarationIrBuilder(this, it.symbol).irExprBody(initializer(it))
    }
  }
}