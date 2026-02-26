package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.name.Name


@OptIn(UnsafeDuringIrConstructionAPI::class)
fun createSuts(): List<IrField> = with(IoaContext.pluginContext) {
  return when (IoaContext.instrumentationKind) {
    IoaKind.TryFinally, IoaKind.IncrementIntCounter, IoaKind.RandomValue -> listOf(createFieldOfType(irBuiltIns.intType))

    IoaKind.TimeClock, IoaKind.TimeMonotonicFunction -> listOf(createFieldOfType(IoaContext.durationClass.defaultType))
    IoaKind.TimeMonotonicGlobal -> listOf(
      createFieldOfType(IoaContext.valueTimeMarkerClass.defaultType) {
        irCall(IoaContext.timeSourceMonotonicMarkNowFunction).apply {
          dispatchReceiver = irGetObject(IoaContext.timeSourceMonotonicClass)
        }
      },
      createFieldOfType(IoaContext.durationClass.defaultType, suffix = "1")
    )

    IoaKind.IncrementAtomicIntCounter -> listOf(createFieldOfType(IoaContext.atomicIntClass.defaultType) {
      irCallConstructor(IoaContext.atomicIntConstructor, listOf(irBuiltIns.intType))
        .apply { arguments[0] = DeclarationIrBuilder(this@with, it.symbol).irInt(0) }
    })

    IoaKind.AppendToStringBuilder -> listOf(createFieldOfType(IoaContext.stringBuilderClass.defaultType) {
      irCallConstructor(IoaContext.stringBuilderConstructor, listOf())
    })

    IoaKind.FileEagerFlush, IoaKind.FileLazyFlush -> listOf(createFieldOfType(IoaContext.sinkClass.defaultType) {
      irCall(IoaContext.sinkBufferedFunction).apply {
        arguments[0] = irCall(IoaContext.fileSystemSinkFunction).apply {
          arguments[0] = irCall(IoaContext.systemFileSystemProperty.owner.getter!!)
          arguments[1] = irCall(IoaContext.pathFunction).apply {
            arguments[0] = irString("__ioa_out.txt")
          }
        }
      }
    })

    else -> emptyList()
  }
}

fun IrPluginContext.createFieldOfType(
  type: IrType,
  suffix: String = "0",
  initializer: (DeclarationIrBuilder.(IrField) -> IrExpression)? = null
): IrField {
  return irFactory.buildField {
    this.name = Name.identifier("__ioa_sut_$suffix")
    this.type = type
    this.isStatic = true
  }.also {
    if (initializer != null) {
      it.initializer = with(DeclarationIrBuilder(this, it.symbol)) {
        irExprBody(initializer(it))
      }
    }
  }
}