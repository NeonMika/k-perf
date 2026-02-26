package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasShape
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

@OptIn(UnsafeDuringIrConstructionAPI::class)
object IoaContext {

  lateinit var pluginContext: IrPluginContext
  lateinit var firstFile: IrFile

  var instrumentationKind: IoaKind = IoaKind.None
    set(value) {
      field = value
      sutFields = createSuts()
    }

  var sutFields: SutFields = SutFields(emptyList())
    private set

  // <editor-fold desc="Time and Clock">
  val clockSystemClass by lazy {
    pluginContext.referenceClass(
      ClassId(
        FqName("kotlin.time"),
        FqName("Clock.System"),
        false
      )
    )!!
  }

  val clockNowFunction by lazy {
    clockSystemClass.functions.single {
      it.owner.hasShape(dispatchReceiver = true, regularParameters = 0)
          && it.owner.name.asString() == "now"
    }
  }

  val instantClass by lazy { clockNowFunction.owner.returnType.classOrFail }
  val instantMinusFunction by lazy {
    instantClass.functions.single {
      it.owner.hasShape(dispatchReceiver = true, regularParameters = 1)
          && it.owner.name.asString() == "minus"
          && it.owner.parameters[1].type == instantClass.defaultType
    }
  }

  val durationClass by lazy { instantMinusFunction.owner.returnType.classOrFail }
  val durationMinusFunction by lazy {
    durationClass.functions.single {
      it.owner.hasShape(dispatchReceiver = true, regularParameters = 1)
          && it.owner.name.asString() == "minus"
          && it.owner.parameters[1].type == durationClass.defaultType
    }
  }

  val timeSourceMonotonicClass by lazy {
    pluginContext.referenceClass(ClassId(FqName("kotlin.time"), FqName("TimeSource.Monotonic"), false))!!
  }

  val timeSourceMonotonicMarkNowFunction by lazy {
    timeSourceMonotonicClass.functions.single {
      it.owner.hasShape(dispatchReceiver = true, regularParameters = 0)
          && it.owner.name.asString() == "markNow"
    }
  }

  val valueTimeMarkerClass by lazy { timeSourceMonotonicMarkNowFunction.owner.returnType.classOrFail }
  val valueTimeMarkerElapsedNowFunction by lazy {
    valueTimeMarkerClass.functions.single {
      it.owner.hasShape(dispatchReceiver = true, regularParameters = 0)
          && it.owner.name.asString() == "elapsedNow"
    }
  }
  // </editor-fold>

  // <editor-fold desc="Integer and Atomic Integer">
  val intIncrementFunction by lazy {
    pluginContext.referenceFunctions(
      CallableId(
        FqName("kotlin"),
        FqName("Int"),
        Name.identifier("inc")
      )
    ).single { it.owner.hasShape(dispatchReceiver = true, regularParameters = 0) }
  }

  val atomicIntClass by lazy {
    pluginContext.referenceClass(
      ClassId(
        FqName("kotlin.concurrent.atomics"),
        Name.identifier("AtomicInt")
      )
    )!!
  }

  val atomicIntConstructor by lazy {
    atomicIntClass.constructors.single { it.owner.hasShape(regularParameters = 1) && it.owner.parameters[0].type == pluginContext.irBuiltIns.intType }
  }

  val atomicIntFetchAndIncrementFunction by lazy {
    pluginContext.referenceFunctions(
      CallableId(
        FqName("kotlin.concurrent.atomics"),
        Name.identifier("fetchAndIncrement")
      )
    ).single {
      it.owner.hasShape(
        extensionReceiver = true,
        regularParameters = 0
      ) && it.owner.parameters[0].type == atomicIntClass.defaultType
    }
  }
  // </editor-fold>

  // <editor-fold desc="Random">
  val randomDefaultClass by lazy {
    pluginContext.referenceClass(
      ClassId(
        FqName("kotlin.random"),
        FqName("Random.Default"),
        false
      )
    )!!
  }

  val randomNextIntFunction by lazy {
    randomDefaultClass.functions.single {
      it.owner.name.asString() == "nextInt" &&
          it.owner.hasShape(dispatchReceiver = true, regularParameters = 0)
    }
  }
  // </editor-fold>

  // <editor-fold desc="println">
  val printlnFunction by lazy {
    pluginContext.referenceFunctions(
      CallableId(
        FqName("kotlin.io"),
        Name.identifier("println")
      )
    ).single {
      it.owner.hasShape(regularParameters = 1) &&
          it.owner.parameters[0].type == pluginContext.irBuiltIns.anyNType
    }
  }
  // </editor-fold>

  // <editor-fold desc="StringBuilder">
  val stringBuilderClass by lazy {
    val classId = ClassId(FqName("kotlin.text"), Name.identifier("StringBuilder"))
    pluginContext.referenceTypeAlias(classId)?.owner?.expandedType?.classOrFail
      ?: pluginContext.referenceClass(classId)!!
  }

  val stringBuilderConstructor by lazy {
    stringBuilderClass.constructors.single { it.owner.hasShape(regularParameters = 0) }
  }

  val stringBuilderAppendStringFunction by lazy {
    stringBuilderClass.functions.single {
      it.owner.name.asString() == "append" &&
          it.owner.hasShape(dispatchReceiver = true, regularParameters = 1) &&
          it.owner.parameters[1].type == pluginContext.irBuiltIns.stringType.makeNullable()
    }
  }
  // </editor-fold>

  // <editor-fold desc="File Handling">
  val pathFunction by lazy {
    pluginContext.referenceFunctions(
      CallableId(
        FqName("kotlinx.io.files"),
        Name.identifier("Path")
      )
    ).single {
      it.owner.hasShape(regularParameters = 1) &&
          it.owner.parameters[0].type == pluginContext.irBuiltIns.stringType
    }
  }

  val systemFileSystemProperty by lazy {
    pluginContext.referenceProperties(
      CallableId(
        FqName("kotlinx.io.files"),
        Name.identifier("SystemFileSystem")
      )
    ).single()
  }

  val fileSystemClass by lazy {
    pluginContext.referenceClass(
      ClassId(
        FqName("kotlinx.io.files"),
        Name.identifier("FileSystem")
      )
    )!!
  }

  val fileSystemSinkFunction by lazy { fileSystemClass.functions.single { it.owner.name.asString() == "sink" } }
  val sinkClass by lazy { fileSystemSinkFunction.owner.returnType.classOrFail }

  val sinkBufferedFunction by lazy {
    pluginContext.referenceFunctions(
      CallableId(
        FqName("kotlinx.io"),
        Name.identifier("buffered")
      )
    ).single {
      it.owner.hasShape(extensionReceiver = true, regularParameters = 0) &&
          it.owner.parameters[0].type == sinkClass.defaultType
    }
  }

  val sinkWriteStringFunction by lazy {
    pluginContext.referenceFunctions(
      CallableId(
        FqName("kotlinx.io"),
        Name.identifier("writeString")
      )
    ).single {
      it.owner.hasShape(extensionReceiver = true, regularParameters = 3) &&
          it.owner.parameters[1].type == pluginContext.irBuiltIns.stringType &&
          it.owner.parameters[2].type == pluginContext.irBuiltIns.intType &&
          it.owner.parameters[3].type == pluginContext.irBuiltIns.intType
    }
  }

  val sinkFlushFunction by lazy {
    sinkClass.functions.single {
      it.owner.name.asString() == "flush" &&
          it.owner.hasShape(dispatchReceiver = true, regularParameters = 0)
    }
  }
  // </editor-fold>

}