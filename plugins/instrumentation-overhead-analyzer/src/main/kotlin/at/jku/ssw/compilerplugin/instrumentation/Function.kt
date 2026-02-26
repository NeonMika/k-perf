@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI

fun modifyFunction(function: IrFunction) =
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
    IoaKind.FileEagerFlush -> modifyFunctionFileEagerFlush(function)
    IoaKind.FileLazyFlush -> modifyFunctionFileLazyFlush(function)
    else -> {}
  }

fun modifyFunctionTryFinally(function: IrFunction) {
  setFunctionBody(function) {
    +irTry(
      function.returnType,
      irBlock(resultType = function.returnType) {
        addAllStatements(function)
      },
      listOf(),
      irBlock {
        IoaContext.sutFields[0] = irInt(0)
      }
    )
  }
}

fun modifyFunctionTimeClock(function: IrFunction) {
  var start: IrVariable? = null
  modifyFunctionAtBeginning(function) {
    start = irTemporary(irCall(IoaContext.clockNowFunction).apply {
      dispatchReceiver = irGetObject(IoaContext.clockSystemClass)
    })
  }

  modifyFunctionBeforeEachReturnOrAtEnd(function) {
    IoaContext.sutFields[0] = irCall(IoaContext.instantMinusFunction).apply {
      dispatchReceiver = irCall(IoaContext.clockNowFunction).apply {
        dispatchReceiver = irGetObject(IoaContext.clockSystemClass)
      }
      arguments[1] = irGet(start!!)
    }
  }
}

fun modifyFunctionTimeMonotonicFunction(function: IrFunction) {
  var now: IrVariable? = null
  modifyFunctionAtBeginning(function) {
     now = irTemporary(irCall(IoaContext.timeSourceMonotonicMarkNowFunction).apply {
      dispatchReceiver = irGetObject(IoaContext.timeSourceMonotonicClass)
    })
  }

  modifyFunctionBeforeEachReturnOrAtEnd(function) {
    IoaContext.sutFields[0] = irCall(IoaContext.valueTimeMarkerElapsedNowFunction).apply {
      dispatchReceiver = irGet(now!!)
    }
  }
}

fun modifyFunctionTimeMonotonicGlobal(function: IrFunction) {
  var elapseStart: IrVariable? = null
  modifyFunctionAtBeginning(function) {
     elapseStart = irTemporary(irCall(IoaContext.valueTimeMarkerElapsedNowFunction).apply {
      dispatchReceiver = IoaContext.sutFields[0]
    })
  }

  modifyFunctionBeforeEachReturnOrAtEnd(function) {
      IoaContext.sutFields[1] = irCall(IoaContext.durationMinusFunction).apply {
        dispatchReceiver = irCall(IoaContext.valueTimeMarkerElapsedNowFunction).apply {
          dispatchReceiver = IoaContext.sutFields[0]
        }
        arguments[1] = irGet(elapseStart!!)
      }
  }
}

fun modifyFunctionIncrementCounter(function: IrFunction) = modifyFunctionAtBeginning(function) {
  IoaContext.sutFields[0] = irCall(IoaContext.intIncrementFunction).apply {
    dispatchReceiver = IoaContext.sutFields[0]
  }
}

fun modifyFunctionIncrementAtomicCounter(function: IrFunction) = modifyFunctionAtBeginning(function) {
  +irCall(IoaContext.atomicIntFetchAndIncrementFunction).apply {
    arguments[0] = IoaContext.sutFields[0]
  }
}

fun modifyFunctionRandomValue(function: IrFunction) = modifyFunctionAtBeginning(function) {
  IoaContext.sutFields[0] = irCall(IoaContext.randomNextIntFunction).apply {
    dispatchReceiver = irGetObject(IoaContext.randomDefaultClass)
  }
}

fun modifyFunctionStandardOut(function: IrFunction) = modifyFunctionAtBeginning(function) {
  +irCall(IoaContext.printlnFunction).apply {
    arguments[0] = irString("Entering function ${function.name.asString()}")
  }
}

fun modifyFunctionAppendToStringBuilder(function: IrFunction) = modifyFunctionAtBeginning(function) {
  +irCall(IoaContext.stringBuilderAppendStringFunction).apply {
    dispatchReceiver = IoaContext.sutFields[0]
    arguments[1] = irString("Entering function ${function.name.asString()}\n")
  }
}

fun modifyFunctionFileEagerFlush(function: IrFunction) = modifyFunctionAtBeginning(function) {
  +irCall(IoaContext.sinkWriteStringFunction).apply {
    arguments[0] = IoaContext.sutFields[0]
    arguments[1] = irString("Entering function ${function.name.asString()}\n")
  }
  +irCall(IoaContext.sinkFlushFunction).apply {
    dispatchReceiver = IoaContext.sutFields[0]
  }
}

fun modifyFunctionFileLazyFlush(function: IrFunction) = modifyFunctionAtBeginning(function) {
  +irCall(IoaContext.sinkWriteStringFunction).apply {
    arguments[0] = IoaContext.sutFields[0]
    arguments[1] = irString("Entering function ${function.name.asString()}\n")
  }
}