package at.jku.ssw.compilerplugin.instrumentation

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.statements

fun IrPluginContext.setFunctionBody(function: IrFunction, block: IrBlockBodyBuilder.() -> Unit) {
  function.body = DeclarationIrBuilder(this, function.symbol).irBlockBody {
    block()
  }
}

fun IrBlockBodyBuilder.addAllStatements(function: IrFunction) {
  for (statement in function.body!!.statements) {
    +statement
  }
}

fun IrPluginContext.modifyFunctionAtBeginning(function: IrFunction, block: IrBlockBodyBuilder.() -> Unit) =
  setFunctionBody(function) {
    block()
    addAllStatements(function)
  }