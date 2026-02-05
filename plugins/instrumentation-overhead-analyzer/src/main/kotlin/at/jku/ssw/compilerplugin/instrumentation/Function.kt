package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.statements

fun modifyFunction(function: IrFunction) = with(IoaContext.pluginContext) {
  when (IoaContext.instrumentationKind) {
    IoaKind.None -> {}
    IoaKind.StandardOut -> modifyFunctionStandardOut(function)
  }
}

fun IrPluginContext.modifyFunctionStandardOut(function: IrFunction) {
  function.body = DeclarationIrBuilder(this, function.symbol).irBlockBody {
    +irCall(IoaContext.printlnFunction).apply {
      arguments[0] = irString("Entering function ${function.name.asString()}")
    }

    for (statement in function.body!!.statements) {
      +statement
    }
  }
}