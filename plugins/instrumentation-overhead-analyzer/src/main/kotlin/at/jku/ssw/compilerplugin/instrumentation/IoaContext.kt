package at.jku.ssw.compilerplugin.instrumentation

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

@OptIn(UnsafeDuringIrConstructionAPI::class)
object IoaContext {

  lateinit var pluginContext: IrPluginContext
  lateinit var instrumentationKind: IoaKind

  val sutField: IrField? by lazy { createSut() }

  val printlnFunction by lazy {
    pluginContext.referenceFunctions(CallableId(
      FqName("kotlin.io"),
      Name.identifier("println")
    )).first {
      it.owner.parameters.size == 1 &&
      it.owner.parameters[0].type == pluginContext.irBuiltIns.anyNType
    }
  }

}