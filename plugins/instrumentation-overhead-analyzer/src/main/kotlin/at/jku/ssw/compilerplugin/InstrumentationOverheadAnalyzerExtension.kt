package at.jku.ssw.compilerplugin

import at.jku.ssw.shared.InstrumentationOverheadAnalyzerKind
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin.Companion.ADAPTER_FOR_CALLABLE_REFERENCE
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.*
import java.io.File
import kotlin.time.ExperimentalTime

class InstrumentationOverheadAnalyzerExtension(
  private val messageCollector: MessageCollector,
  private val kind : InstrumentationOverheadAnalyzerKind
) : IrGenerationExtension {
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
      println("# ---${file.name}---")
      file.transform(object : IrElementTransformerVoidWithContext() {
        override fun visitFunctionNew(declaration: IrFunction): IrStatement {
          val body = declaration.body
          if (declaration.name.asString() == "_enter_method" ||
            declaration.name.asString() == "_exit_method" ||
            declaration.name.asString() == "_exit_main" ||
            body == null ||
            declaration.origin == ADAPTER_FOR_CALLABLE_REFERENCE ||
            declaration.fqNameWhenAvailable?.asString()?.contains("<init>") != false ||
            declaration.fqNameWhenAvailable?.asString()?.contains("<anonymous>") != false
          ) {
            // do not further transform this method, e.g., its statements are not transformed
            println("### Do not modify body of ${declaration.name}() (${declaration.fqNameWhenAvailable?.asString()}):\n${declaration.dump()}")
            return declaration
          }

          // TODO: modify declaration according to kind
          println("### In the future I will modify the body of ${declaration.name}() (${declaration.fqNameWhenAvailable?.asString()}) to introduce specific overhead:\n${declaration.dump()}")

          return super.visitFunctionNew(declaration)
        }
      }, null)
      println("# ---${file.name} MODIFIED ---")
      println(file.dump())
    }
  }
}