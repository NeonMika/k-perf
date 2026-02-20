@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.statements

fun modifyFunction(function: IrFunction) = with(IoaContext.pluginContext) {
  when (IoaContext.instrumentationKind) {
    IoaKind.IncrementIntCounter, IoaKind.IncrementIntCounterAndPrint -> modifyFunctionIncrementCounter(function)
    IoaKind.IncrementAtomicIntCounter -> modifyFunctionIncrementAtomicCounter(function)
    IoaKind.RandomValue -> modifyFunctionRandomValue(function)
    IoaKind.StandardOut -> modifyFunctionStandardOut(function)
    IoaKind.AppendToStringBuilder -> modifyFunctionAppendToStringBuilder(function)
    else -> {}
  }
}

fun IrPluginContext.modifyFunctionAtBegining(function: IrFunction, block: IrBlockBodyBuilder.() -> Unit) {
  function.body = DeclarationIrBuilder(this, function.symbol).irBlockBody {
    block()

    for (statement in function.body!!.statements) {
      +statement
    }
  }
}

fun IrPluginContext.modifyFunctionIncrementCounter(function: IrFunction) = modifyFunctionAtBegining(function) {
  +irSetField(null, IoaContext.sutField!!, irCall(IoaContext.incrementIntFunction).apply {
    dispatchReceiver = irGetField(null, IoaContext.sutField!!)
  })
}

fun IrPluginContext.modifyFunctionIncrementAtomicCounter(function: IrFunction) = modifyFunctionAtBegining(function) {
  +irCall(IoaContext.fetchAndIncrementFunction).apply {
    arguments[0] = irGetField(null, IoaContext.sutField!!)
  }
}

fun IrPluginContext.modifyFunctionRandomValue(function: IrFunction) = modifyFunctionAtBegining(function) {
  +irSetField(null, IoaContext.sutField!!, irCall(IoaContext.randomNextIntFunction).apply {
    dispatchReceiver = irGetObject(IoaContext.randomDefaultClass)
  })
}

fun IrPluginContext.modifyFunctionStandardOut(function: IrFunction) = modifyFunctionAtBegining(function) {
  +irCall(IoaContext.printlnFunction).apply {
    arguments[0] = irString("Entering function ${function.name.asString()}")
  }
}

fun IrPluginContext.modifyFunctionAppendToStringBuilder(function: IrFunction) = modifyFunctionAtBegining(function) {
  +irCall(IoaContext.stringBuilderAppendStringFunction).apply {
    dispatchReceiver = irGetField(null, IoaContext.sutField!!)
    arguments[1] = irString("Entering function ${function.name.asString()}\n")
  }
}