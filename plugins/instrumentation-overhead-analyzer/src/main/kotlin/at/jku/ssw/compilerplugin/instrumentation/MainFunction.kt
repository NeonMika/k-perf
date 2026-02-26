package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irConcat
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.addArgument
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI

fun modifyMainFunction(function: IrFunction) = with(IoaContext.pluginContext) {
  when (IoaContext.instrumentationKind) {
    IoaKind.FileLazyFlush -> modifyMainFunctionFileLazyFlush(function)
    else -> {}
  }

  if (IoaContext.sutFields.actualProperties.isNotEmpty()) {
    modifyMainFunctionPrintSut(function)
  }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.modifyMainFunctionFileLazyFlush(function: IrFunction) = modifyFunctionAtEnd(function) {
  +irCall(IoaContext.sinkFlushFunction).apply {
    dispatchReceiver = IoaContext.sutFields[0]
  }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.modifyMainFunctionPrintSut(function: IrFunction) = modifyFunctionAtEnd(function) {
  +irCall(IoaContext.printlnFunction).apply {
    arguments[0] = irConcat().apply {
      addArgument(irString("Sut fields after execution: "))
      IoaContext.sutFields.actualProperties.forEach {
        addArgument(irString("\n - ${it.name} = "))
        addArgument(irCall(it.getter!!).apply { dispatchReceiver = null })
      }
    }
  }
}