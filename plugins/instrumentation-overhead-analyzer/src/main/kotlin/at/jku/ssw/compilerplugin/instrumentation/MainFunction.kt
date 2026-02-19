package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irConcat
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.addArgument
import org.jetbrains.kotlin.ir.util.statements

fun modifyMainFunction(function: IrFunction) = with(IoaContext.pluginContext) {
  when (IoaContext.instrumentationKind) {
    IoaKind.None, IoaKind.IncrementIntCounter, IoaKind.StandardOut -> {}
    IoaKind.IncrementIntCounterAndPrint,  IoaKind.AppendToStringBuilder -> modifyMainFunctionPrintSut(function)
  }
}


fun IrPluginContext.modifyMainFunctionPrintSut(function: IrFunction) {
  function.body = DeclarationIrBuilder(this, function.symbol).irBlockBody {
    for (statement in function.body!!.statements) {
      +statement
    }

    +irCall(IoaContext.printlnFunction).apply {
      arguments[0] = irConcat().apply {
        addArgument(irString("Sut field after execution: "))
        addArgument(irGetField(null, IoaContext.sutField!!))
      }
    }
  }
}