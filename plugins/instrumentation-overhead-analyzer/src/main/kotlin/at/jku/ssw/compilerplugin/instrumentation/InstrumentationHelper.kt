package at.jku.ssw.compilerplugin.instrumentation

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
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

fun IrBlockBuilder.addAllStatements(function: IrFunction) {
  for (statement in function.body!!.statements) {
    +statement
  }
}

fun modifyFunctionAtBeginning(function: IrFunction, block: IrBlockBodyBuilder.() -> Unit) =
  setFunctionBody(function) {
    block()
    addAllStatements(function)
  }

fun modifyFunctionBeforeEachReturnOrAtEnd(function: IrFunction, block: IrBlockBodyBuilder.() -> Unit) {
  val transformer = BeforeEachReturnTransformer(function, block)
  function.body = function.body!!.transform(transformer, null)

  if (!transformer.hasReturn) {
    setFunctionBody(function) {
      addAllStatements(function)
      block()
    }
  }
}

class BeforeEachReturnTransformer(val function: IrFunction, val modify: IrBlockBodyBuilder.() -> Unit) :
  IrElementTransformerVoidWithContext() {

  var hasReturn = false

  override fun visitReturn(expression: IrReturn): IrExpression {
    if (expression.returnTargetSymbol != function.symbol) return super.visitReturn(expression)

    hasReturn = true
    return with(DeclarationIrBuilder(IoaContext.pluginContext, function.symbol)) {
      irBlock {
        irBlockBody {
          modify()
          +expression
        }
      }
    }
  }

}