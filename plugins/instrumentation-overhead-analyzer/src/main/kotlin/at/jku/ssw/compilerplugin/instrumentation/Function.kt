@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irSetField
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.statements

fun modifyFunction(function: IrFunction) = with(IoaContext.pluginContext) {
  when (IoaContext.instrumentationKind) {
    IoaKind.None -> {}
    IoaKind.IncrementIntCounter, IoaKind.IncrementIntCounterAndPrint -> modifyFunctionIncreaseCounter(function)
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