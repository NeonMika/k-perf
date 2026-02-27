package at.jku.ssw.compilerplugin

import at.jku.ssw.compilerplugin.instrumentation.IoaContext
import at.jku.ssw.compilerplugin.instrumentation.modifyFunction
import at.jku.ssw.compilerplugin.instrumentation.modifyMainFunction
import at.jku.ssw.shared.IoaKind
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin.Companion.ADAPTER_FOR_CALLABLE_REFERENCE
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin.Companion.DEFAULT_PROPERTY_ACCESSOR
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import java.io.File
import kotlin.time.ExperimentalTime

class IoaGenerationExtension(val kind: IoaKind, val instrumentPropertyAccessors: Boolean) : IrGenerationExtension {
  val debugFile = File("./DEBUG.txt")

  init {
    debugFile.delete()
  }

  fun appendToDebugFile(str: String) {
    debugFile.appendText(str)
    debugFile.appendText("\n")
  }

  @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
    IoaContext.pluginContext = pluginContext

    val file = moduleFragment.files.firstOrNull()
    if (file == null) {
      appendToDebugFile("No files in module fragment, nothing to instrument!")
      return
    } else {
      appendToDebugFile("Storing first file for SUT in ${file.name}.")
      IoaContext.firstFile = file
    }

    IoaContext.instrumentationKind = kind

    moduleFragment.files.forEach { file ->
      appendToDebugFile("# ---${file.name}---")
      file.transform(object : IrElementTransformerVoidWithContext() {
        override fun visitFunctionNew(declaration: IrFunction): IrStatement {
          if (declaration.body == null || // Do not instrument empty functions.
            declaration.origin == ADAPTER_FOR_CALLABLE_REFERENCE || // Do not instrument function references using :: operator
            (!instrumentPropertyAccessors && declaration.origin == DEFAULT_PROPERTY_ACCESSOR) || // Do not instrument property accessors if disabled.
            declaration.fqNameWhenAvailable?.asString()?.contains("__ioa_sut") != false || // Do not instrument functions that are part of the ioa sut fields.
            declaration.fqNameWhenAvailable?.asString()?.contains("<init>") != false || // Do not instrument constructors.
            declaration.fqNameWhenAvailable?.asString()?.contains("<anonymous>") != false // Do not instrument anonymous functions.
          ) {
            // do not further transform this method, e.g., its statements are not transformed
            appendToDebugFile("### Do not modify body of ${declaration.name}() (${declaration.fqNameWhenAvailable?.asString()}):\n${declaration.dump()}")
            return declaration
          }

          modifyFunction(declaration)

          // If this is the main function, we also want to modify it, e.g., to print out the SUT before or after the main function is executed.
          if (declaration.name.asString() == "main") {
            modifyMainFunction(declaration)
          }

          return super.visitFunctionNew(declaration)
        }
      }, null)

      appendToDebugFile("# ---${file.name} MODIFIED ---")
      appendToDebugFile(file.dump())
    }
  }
}