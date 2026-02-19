package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name


fun createSut(): IrField? = with(IoaContext.pluginContext) {
  return when (IoaContext.instrumentationKind) {
    IoaKind.None, IoaKind.StandardOut -> null
    IoaKind.IncrementIntCounter, IoaKind.IncrementIntCounterAndPrint -> createFieldOfType(irBuiltIns.intType)
  }
}

fun IrPluginContext.createFieldOfType(type: IrType): IrField {
  return irFactory.buildField {
    this.name = Name.identifier("__ioa_sut")
    this.type = type
    this.isStatic = true
  }
}