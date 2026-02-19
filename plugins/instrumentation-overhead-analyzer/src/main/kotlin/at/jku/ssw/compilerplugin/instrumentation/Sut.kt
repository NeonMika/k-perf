package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.name.Name


fun createSut(): IrField? = with(IoaContext.pluginContext) {
  return when (IoaContext.instrumentationKind) {
    IoaKind.None, IoaKind.StandardOut -> null
    IoaKind.IncrementIntCounter, IoaKind.IncrementIntCounterAndPrint, IoaKind.RandomValue -> createFieldOfType(irBuiltIns.intType)

    IoaKind.AppendToStringBuilder -> createFieldOfType(IoaContext.stringBuilderClass.defaultType) {
      DeclarationIrBuilder(this, it.symbol).irCallConstructor(IoaContext.stringBuilderConstructor, listOf())
    }
  }
}

fun IrPluginContext.createFieldOfType(type: IrType, initializer: ((IrField) -> IrExpression)? = null): IrField {
  return irFactory.buildField {
    this.name = Name.identifier("__ioa_sut")
    this.type = type
    this.isStatic = true
  }.also {
    if (initializer != null) {
      it.initializer = DeclarationIrBuilder(this, it.symbol).irExprBody(initializer(it))
    }
  }
}