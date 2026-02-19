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
    IoaKind.None -> {}
    IoaKind.IncrementIntCounter, IoaKind.IncrementIntCounterAndPrint -> modifyFunctionIncreaseCounter(function)
    IoaKind.RandomValue -> modifyFunctionRandomValue(function)
    IoaKind.StandardOut -> modifyFunctionStandardOut(function)
    IoaKind.AppendToStringBuilder -> modifyFunctionAppendToStringBuilder(function)
  }
}

fun IrPluginContext.modifyFunctionIncreaseCounter(function: IrFunction) {
  function.body = DeclarationIrBuilder(this, function.symbol).irBlockBody {
    +irSetField(null, IoaContext.sutField!!, irCall(IoaContext.incrementIntFunction).apply {
      dispatchReceiver = irGetField(null, IoaContext.sutField!!)
    })

    for (statement in function.body!!.statements) {
      +statement
    }
  }
}

fun IrPluginContext.modifyFunctionRandomValue(function: IrFunction) {
  function.body = DeclarationIrBuilder(this, function.symbol).irBlockBody {
    +irSetField(null, IoaContext.sutField!!, irCall(IoaContext.randomNextIntFunction).apply {
      dispatchReceiver = irGetObject(IoaContext.randomDefaultClass)
    })

    for (statement in function.body!!.statements) {
      +statement
    }
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

fun IrPluginContext.modifyFunctionAppendToStringBuilder(function: IrFunction) {
  function.body = DeclarationIrBuilder(this, function.symbol).irBlockBody {
    +irCall(IoaContext.stringBuilderAppendStringFunction).apply {
      dispatchReceiver = irGetField(null, IoaContext.sutField!!)
      arguments[1] = irString("Entering function ${function.name.asString()}\n")
    }

    for (statement in function.body!!.statements) {
      +statement
    }
  }
}