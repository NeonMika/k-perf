package at.jku.ssw.compilerplugin.instrumentation

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrBlockBuilder
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.util.statements

fun setFunctionBody(function: IrFunction, block: IrBlockBodyBuilder.() -> Unit) {
  function.body = DeclarationIrBuilder(IoaContext.pluginContext, function.symbol).irBlockBody {
    block()
  }
}

fun IrBlockBodyBuilder.addAllStatements(function: IrFunction) {
  for (statement in function.body!!.statements) {
    +statement
  }
}

fun modifyFunctionAtBeginning(function: IrFunction, block: IrBlockBodyBuilder.() -> Unit) =
  setFunctionBody(function) {
    block()
    addAllStatements(function)
  }

fun modifyFunctionBeforeEachReturn(function: IrFunction, block: IrBlockBuilder.() -> Unit) {
  function.body = function.body!!.transform(BeforeEachReturnTransformer(function, block), null)
}

class BeforeEachReturnTransformer(val function: IrFunction, val modify: IrBlockBuilder.() -> Unit) :
  IrElementTransformerVoidWithContext() {

  override fun visitReturn(expression: IrReturn): IrExpression {
    if (expression.returnTargetSymbol != function.symbol) return super.visitReturn(expression)

    return with(DeclarationIrBuilder(IoaContext.pluginContext, function.symbol)) {
      irBlock {
        modify()
        +expression
      }
    }
  }

}

fun modifyFunctionAtBeginningAndBeforeEachReturn(function: IrFunction, beginning: IrBlockBodyBuilder.() -> Unit, beforeReturn: IrBlockBuilder.() -> Unit) {
  modifyFunctionAtBeginning(function, beginning)
  modifyFunctionBeforeEachReturn(function, beforeReturn)
}