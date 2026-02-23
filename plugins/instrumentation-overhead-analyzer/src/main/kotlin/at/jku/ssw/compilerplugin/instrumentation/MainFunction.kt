package at.jku.ssw.compilerplugin.instrumentation

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.addArgument
import org.jetbrains.kotlin.ir.util.statements

fun modifyMainFunction(function: IrFunction) = with(IoaContext.pluginContext) {
  when (IoaContext.instrumentationKind) {
    else if IoaContext.sutFields.isNotEmpty() -> modifyMainFunctionPrintSut(function)
    else -> {}
  }
}


fun IrPluginContext.modifyMainFunctionPrintSut(function: IrFunction) {
  function.body = DeclarationIrBuilder(this, function.symbol).irBlockBody {
    for (statement in function.body!!.statements) {
      +statement
    }

    +irCall(IoaContext.printlnFunction).apply {
      arguments[0] = irConcat().apply {
        addArgument(irString("Sut fields after execution: "))
        IoaContext.sutFields.drop(1).forEach {
          addArgument(irString("\n - ${it.name} = "))
          addArgument(irGetField(null, it))
        }
      }
    }
  }
}