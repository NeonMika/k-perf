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
    IoaKind.TryFinally -> modifyFunctionTryFinally(function)
    IoaKind.TimeClock -> modifyFunctionTimeClock(function)
    IoaKind.TimeMonotonicFunction -> modifyFunctionTimeMonotonicFunction(function)
    IoaKind.TimeMonotonicGlobal -> modifyFunctionTimeMonotonicGlobal(function)
    IoaKind.IncrementIntCounter -> modifyFunctionIncrementCounter(function)
    IoaKind.IncrementAtomicIntCounter -> modifyFunctionIncrementAtomicCounter(function)
    IoaKind.RandomValue -> modifyFunctionRandomValue(function)
    IoaKind.StandardOut -> modifyFunctionStandardOut(function)
    IoaKind.AppendToStringBuilder -> modifyFunctionAppendToStringBuilder(function)
    else -> {}
  }
}

fun IrPluginContext.modifyFunctionTryFinally(function: IrFunction) {
  function.body = DeclarationIrBuilder(this, function.symbol).irBlockBody {
    +irTry(
      function.returnType,
      irBlock(resultType = function.returnType) {
        for (statement in function.body!!.statements) {
          +statement
        }
      },
      listOf(),
      irBlock {
        +irSetField(null, IoaContext.sutField, irInt(0))
      }
    )
  }
}

fun IrPluginContext.modifyFunctionTimeClock(function: IrFunction) = setFunctionBody(function) {
  val start = irTemporary(irCall(IoaContext.clockNowFunction).apply {
    dispatchReceiver = irGetObject(IoaContext.clockSystemClass)
  })

  addAllStatements(function)

  +irSetField(null, IoaContext.sutField, irCall(IoaContext.instantMinusFunction).apply {
    dispatchReceiver = irCall(IoaContext.clockNowFunction).apply {
      dispatchReceiver = irGetObject(IoaContext.clockSystemClass)
    }
    arguments[1] = irGet(start)
  })
}

fun IrPluginContext.modifyFunctionTimeMonotonicFunction(function: IrFunction) = setFunctionBody(function) {
  val now = irTemporary(irCall(IoaContext.timeSourceMonotonicMarkNowFunction).apply {
    dispatchReceiver = irGetObject(IoaContext.timeSourceMonotonicClass)
  })

  addAllStatements(function)

  +irSetField(null, IoaContext.sutField, irCall(IoaContext.valueTimeMarkerElapsedNowFunction).apply {
    dispatchReceiver = irGet(now)
  })
}

fun IrPluginContext.modifyFunctionTimeMonotonicGlobal(function: IrFunction) = setFunctionBody(function) {
  val elapseStart = irTemporary(irCall(IoaContext.valueTimeMarkerElapsedNowFunction).apply {
    dispatchReceiver = irGetField(null, IoaContext.sutFields[0])
  })

  addAllStatements(function)

  +irSetField(null, IoaContext.sutFields[1], irCall(IoaContext.durationMinusFunction).apply {
    dispatchReceiver = irCall(IoaContext.valueTimeMarkerElapsedNowFunction).apply {
      dispatchReceiver = irGetField(null, IoaContext.sutFields[0])
    }
    arguments[1] = irGet(elapseStart)
  })
}

fun IrPluginContext.modifyFunctionIncrementCounter(function: IrFunction) = modifyFunctionAtBeginning(function) {
  +irSetField(null, IoaContext.sutField, irCall(IoaContext.incrementIntFunction).apply {
    dispatchReceiver = irGetField(null, IoaContext.sutField)
  })
}

fun IrPluginContext.modifyFunctionIncrementAtomicCounter(function: IrFunction) = modifyFunctionAtBeginning(function) {
  +irCall(IoaContext.fetchAndIncrementFunction).apply {
    arguments[0] = irGetField(null, IoaContext.sutField)
  }
}

fun IrPluginContext.modifyFunctionRandomValue(function: IrFunction) = modifyFunctionAtBeginning(function) {
  +irSetField(null, IoaContext.sutField, irCall(IoaContext.randomNextIntFunction).apply {
    dispatchReceiver = irGetObject(IoaContext.randomDefaultClass)
  })
}

fun IrPluginContext.modifyFunctionStandardOut(function: IrFunction) = modifyFunctionAtBeginning(function) {
  +irCall(IoaContext.printlnFunction).apply {
    arguments[0] = irString("Entering function ${function.name.asString()}")
  }
}

fun IrPluginContext.modifyFunctionAppendToStringBuilder(function: IrFunction) = modifyFunctionAtBeginning(function) {
  +irCall(IoaContext.stringBuilderAppendStringFunction).apply {
    dispatchReceiver = irGetField(null, IoaContext.sutField)
    arguments[1] = irString("Entering function ${function.name.asString()}\n")
  }
}