package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classOrFail
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
  lateinit var instrumentationKind: IoaKind

  val sutField: IrField? by lazy { createSut() }

  val incrementIntFunction by lazy {
    pluginContext.referenceFunctions(
      CallableId(
        FqName("kotlin"),
        FqName("Int"),
        Name.identifier("inc")
      )
    ).single { it.owner.hasShape(dispatchReceiver = true, regularParameters = 0) }
  }

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

}