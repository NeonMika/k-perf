package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.*
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.name.Name

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun createSuts(): SutFields = SutFields(with(IoaContext.pluginContext) {
  when (IoaContext.instrumentationKind) {
    IoaKind.TryFinally, IoaKind.IncrementIntCounter, IoaKind.RandomValue -> listOf(createPropertyOfType(irBuiltIns.intType))

    IoaKind.TimeClock, IoaKind.TimeMonotonicFunction -> listOf(createPropertyOfType(IoaContext.durationClass.defaultType))
    IoaKind.TimeMonotonicGlobal -> listOf(
      createPropertyOfType(IoaContext.valueTimeMarkerClass.defaultType) {
        irCall(IoaContext.timeSourceMonotonicMarkNowFunction).apply {
          dispatchReceiver = irGetObject(IoaContext.timeSourceMonotonicClass)
        }
      },
      createPropertyOfType(IoaContext.durationClass.defaultType, suffix = "1")
    )

    IoaKind.IncrementAtomicIntCounter -> listOf(createPropertyOfType(IoaContext.atomicIntClass.defaultType) {
      irCallConstructor(IoaContext.atomicIntConstructor, listOf(irBuiltIns.intType))
        .apply { arguments[0] = DeclarationIrBuilder(this@with, it.symbol).irInt(0) }
    })

    IoaKind.AppendToStringBuilder -> listOf(createPropertyOfType(IoaContext.stringBuilderClass.defaultType) {
      irCallConstructor(IoaContext.stringBuilderConstructor, listOf())
    })

    IoaKind.FileEagerFlush, IoaKind.FileLazyFlush -> listOf(createPropertyOfType(IoaContext.sinkClass.defaultType) {
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
})

@OptIn(UnsafeDuringIrConstructionAPI::class)
data class SutFields(val actualProperties: List<IrProperty>) {

  context(builder: IrBuilder)
  operator fun get(index: Int) = builder.irCall(actualProperties[index].getter!!).apply { dispatchReceiver = null }

  context(builder: IrBuilder, statementBuilder: IrStatementsBuilder<*>)
  operator fun set(index: Int, value: IrExpression) = with(statementBuilder) {
    +builder.irCall(actualProperties[index].setter!!).apply {
      dispatchReceiver = null
      arguments[0] = value
    }
  }

}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.createPropertyOfType(
  type: IrType,
  suffix: String = "0",
  initializer: (DeclarationIrBuilder.(IrField) -> IrExpression)? = null
): IrProperty =
  irFactory.buildProperty {
    this.name = Name.identifier("__ioa_sut_$suffix")
    this.visibility = DescriptorVisibilities.PUBLIC
    this.isVar = true
  }.apply {
    parent = IoaContext.firstFile

    addBackingField {
      this.type = type
      this.isStatic = true
    }.also {
      if (initializer != null) {
        it.initializer = with(DeclarationIrBuilder(this@createPropertyOfType, it.symbol)) {
          irExprBody(initializer(it))
        }
      }
    }

    addGetter {
      this.returnType = type
      this.origin = IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR
    }.also {
      it.body = DeclarationIrBuilder(this@createPropertyOfType, it.symbol).irBlockBody {
        +irReturn(irGetField(null, backingField!!))
      }
    }

    addSetter {
      this.returnType = irBuiltIns.unitType
      this.origin = IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR
    }.also {
      it.addValueParameter {
        this.name = Name.identifier("value")
        this.type = type
      }
      it.body = DeclarationIrBuilder(this@createPropertyOfType, it.symbol).irBlockBody {
        +irSetField(null, backingField!!, irGet(it.parameters[0]))
      }
    }

    IoaContext.firstFile.declarations += this
  }