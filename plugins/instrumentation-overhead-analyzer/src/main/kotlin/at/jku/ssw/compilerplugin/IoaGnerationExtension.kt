package at.jku.ssw.compilerplugin

import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin.Companion.ADAPTER_FOR_CALLABLE_REFERENCE
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import java.io.File
import kotlin.time.ExperimentalTime

class IoaGnerationExtension(private val kind: IoaKind) : IrGenerationExtension {
  val debugFile = File("./DEBUG.txt")

  init {
    debugFile.delete()
  }

  fun appendToDebugFile(str: String) {
    debugFile.appendText(str)
  }

  @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
    // IrElementVisitor / IrElementVisitorVoid
    // IrElementTransformer / IrElementTransformerVoid / IrElementTransformerVoidWithContext
    // IrElementTransformerVoidWithContext().visitfile(file, null)

    moduleFragment.files.forEach { file ->
      appendToDebugFile("# ---${file.name}---")
      file.transform(object : IrElementTransformerVoidWithContext() {
        override fun visitFunctionNew(declaration: IrFunction): IrStatement {
          val body = declaration.body
          if (body == null ||
            declaration.origin == ADAPTER_FOR_CALLABLE_REFERENCE ||
            declaration.fqNameWhenAvailable?.asString()?.contains("<init>") != false ||
            declaration.fqNameWhenAvailable?.asString()?.contains("<anonymous>") != false
          ) {
            // do not further transform this method, e.g., its statements are not transformed
            appendToDebugFile("### Do not modify body of ${declaration.name}() (${declaration.fqNameWhenAvailable?.asString()}):\n${declaration.dump()}")
            return declaration
          }

          // TODO: modify declaration according to kind
          appendToDebugFile("### In the future I will modify the body of ${declaration.name}() (${declaration.fqNameWhenAvailable?.asString()}) to introduce specific overhead:\n${declaration.dump()}")

          return super.visitFunctionNew(declaration)
        }
      }, null)

      appendToDebugFile("# ---${file.name} MODIFIED ---")
      appendToDebugFile(file.dump())
    }
  }
}