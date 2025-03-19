package org.jetbrains.kotlin.ir.util

import org.gradle.internal.impldep.com.google.gson.JsonArray
import org.gradle.internal.impldep.com.google.gson.JsonElement
import org.gradle.internal.impldep.com.google.gson.JsonObject
import org.gradle.internal.impldep.com.google.gson.JsonPrimitive
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.backend.js.JsIrBackendContext
import org.jetbrains.kotlin.ir.backend.js.utils.asString
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.utils.Printer
import java.io.File


class JSONIrTreeVisitor(
    private val options: DumpIrTreeOptions = DumpIrTreeOptions()
) : IrElementVisitor<JsonElement, Unit> {

    private val renderVisitor = RenderIrElementVisitor(options)

    override fun visitElement(element: IrElement, data: Unit): JsonElement {
        var caption = ""
        if (element is IrDeclarationWithName) {
            caption = element.name.asString()
        }

        val jsonObj = jsonWithDefault(element::class.simpleName ?: "Unknown", caption, element)
        return jsonObj
    }

    override fun visitModuleFragment(declaration: IrModuleFragment, data: Unit): JsonElement {
        val caption = declaration.name.asString()
        val jsonObj = jsonWithDefault("Module Fragment", caption, declaration)
        jsonObj.add("Name", JsonPrimitive(declaration.name.asString()))
        return jsonObj
    }

    override fun visitFile(declaration: IrFile, data: Unit): JsonElement {
        val caption = declaration.name
        val jsonObj = jsonWithDefault("File", caption, declaration)
        jsonObj.add("Name", JsonPrimitive(declaration.name))
        jsonObj.add("Path", JsonPrimitive(declaration.path))
        return jsonObj
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction, data: Unit): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Function", caption, declaration)
        jsonObj.add("Visibility", JsonPrimitive(declaration.visibility.name))
        jsonObj.add("Modality", JsonPrimitive(declaration.modality.name))
        jsonObj.add("ReturnType", JsonPrimitive(declaration.returnType.render()))
        return jsonObj
    }

    override fun visitBlockBody(body: IrBlockBody, data: Unit): JsonElement {
        return jsonWithDefault("Block Body", "", body)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitCall(expression: IrCall, data: Unit): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Call", caption, expression)
        jsonObj.add("FunctionName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("ReturnType", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitConst(expression: IrConst<*>, data: Unit): JsonElement {
        val caption = "${expression.kind.asString}"
        val jsonObj = jsonWithDefault("Constant", caption, expression)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("Kind", JsonPrimitive(expression.kind.asString))
        jsonObj.add("Value", JsonPrimitive(expression.value.toString()))
        return jsonObj
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): JsonElement {
        val jsonObj = jsonWithDefault("Type Operator", "", expression)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("TypeOperand", JsonPrimitive(expression.typeOperand.render()))
        jsonObj.add("Operator", JsonPrimitive(expression.operator.name))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitGetValue(expression: IrGetValue, data: Unit): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Get Value", caption, expression)
        jsonObj.add("VariableName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitBlock(expression: IrBlock, data: Unit): JsonElement {
        val name = when (expression) {
            is IrReturnableBlock -> "Returnable Block"
            is IrInlinedFunctionBlock -> "Inlined Block"
            else -> "Block"
        }
        val jsonObj = jsonWithDefault(name, "", expression)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitWhen(expression: IrWhen, data: Unit): JsonElement {
        val jsonObj = jsonWithDefault("When", "", expression)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitBranch(branch: IrBranch, data: Unit): JsonElement {
        return jsonWithDefault("Branch", "", branch)
    }

    override fun visitExpressionBody(body: IrExpressionBody, data: Unit): JsonElement {
        return jsonWithDefault("ExpressionBody", "", body)
    }

    override fun visitValueParameter(declaration: IrValueParameter, data: Unit): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Value Parameter", caption, declaration)
        jsonObj.add("Type", JsonPrimitive(declaration.type.render()))
        jsonObj.add("Name", JsonPrimitive(declaration.name.asString()))
        jsonObj.add("Index", JsonPrimitive(declaration.index))
        return jsonObj
    }

    override fun visitStringConcatenation(expression: IrStringConcatenation, data: Unit): JsonElement {
        val jsonObj = jsonWithDefault("String Concatenation", "", expression)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    override fun visitDeclaration(declaration: IrDeclarationBase, data: Unit): JsonElement {
        val caption = declaration::class.java.simpleName
        return jsonWithDefault("Declaration", caption, declaration)
    }

    override fun visitFunction(declaration: IrFunction, data: Unit): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Function", caption, declaration)
        return jsonObj
    }

    override fun visitExternalPackageFragment(declaration: IrExternalPackageFragment, data: Unit): JsonElement {
        val caption = declaration.packageFqName.toString()
        return jsonWithDefault("ExternalPackageFragment", caption, declaration)
    }

    override fun visitScript(declaration: IrScript, data: Unit): JsonElement {
        return jsonWithDefault("Script", "Script", declaration)
    }

// (Helper renderValueParameterTypes() is used only for string-based rendering so we omit it in JSON mode)

    override fun visitConstructor(declaration: IrConstructor, data: Unit): JsonElement {
        // Constructors often don’t have a “name” beyond "<init>", so we use that.
        val caption = declaration.name.toString()
        return jsonWithDefault("Constructor", caption, declaration)
    }

    override fun visitProperty(declaration: IrProperty, data: Unit): JsonElement {
        val caption = declaration.name.toString()
        return jsonWithDefault("Property", caption, declaration)
    }

    override fun visitField(declaration: IrField, data: Unit): JsonElement {
        val caption = declaration.name.toString()
        return jsonWithDefault("Field", caption, declaration)
    }

    override fun visitClass(declaration: IrClass, data: Unit): JsonElement {
        val caption = declaration.name.toString()
        return jsonWithDefault("Class", caption, declaration)
    }

    override fun visitVariable(declaration: IrVariable, data: Unit): JsonElement {
        // Assuming normalizedName(variableNameData) gives a proper name string.
        val caption = declaration.name.asString()
        return jsonWithDefault("Variable", caption, declaration)
    }

    override fun visitEnumEntry(declaration: IrEnumEntry, data: Unit): JsonElement {
        val caption = declaration.name.toString()
        return jsonWithDefault("EnumEntry", caption, declaration)
    }

    override fun visitAnonymousInitializer(declaration: IrAnonymousInitializer, data: Unit): JsonElement {
        val caption = if (declaration.isStatic) "static" else "instance"
        return jsonWithDefault("AnonymousInitializer", caption, declaration)
    }

    override fun visitTypeParameter(declaration: IrTypeParameter, data: Unit): JsonElement {
        val caption = declaration.name.toString()
        return jsonWithDefault("TypeParameter", caption, declaration)
    }

    override fun visitLocalDelegatedProperty(declaration: IrLocalDelegatedProperty, data: Unit): JsonElement {
        val caption = declaration.name.toString()
        return jsonWithDefault("LocalDelegatedProperty", caption, declaration)
    }

    override fun visitTypeAlias(declaration: IrTypeAlias, data: Unit): JsonElement {
        val caption = declaration.name.toString()
        return jsonWithDefault("TypeAlias", caption, declaration)
    }

    override fun visitSyntheticBody(body: IrSyntheticBody, data: Unit): JsonElement {
        val caption = body.kind.toString()
        return jsonWithDefault("SyntheticBody", caption, body)
    }

    override fun visitExpression(expression: IrExpression, data: Unit): JsonElement {
        val caption = "${expression::class.java.simpleName} type: ${expression.type.render()}"
        return jsonWithDefault("Expression", caption, expression)
    }

    override fun visitVararg(expression: IrVararg, data: Unit): JsonElement {
        val caption = "type: ${expression.type.render()}, varargElementType: ${expression.varargElementType.render()}"
        return jsonWithDefault("Vararg", caption, expression)
    }

    override fun visitSpreadElement(spread: IrSpreadElement, data: Unit): JsonElement {
        return jsonWithDefault("SpreadElement", "SpreadElement", spread)
    }

    override fun visitComposite(expression: IrComposite, data: Unit): JsonElement {
        val caption = "type=${expression.type.render()}, origin=${expression.origin}"
        return jsonWithDefault("Composite", caption, expression)
    }

    override fun visitReturn(expression: IrReturn, data: Unit): JsonElement {
        val caption = ""
        return jsonWithDefault("Return", caption, expression)
    }

    override fun visitConstructorCall(expression: IrConstructorCall, data: Unit): JsonElement {
        val jsonObj = jsonWithDefault("Call", expression.symbol.owner.name.asString(), expression)

        return jsonObj
    }

    override fun visitDelegatingConstructorCall(expression: IrDelegatingConstructorCall, data: Unit): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        return jsonWithDefault("DelegatingConstructorCall", caption, expression)
    }

    override fun visitEnumConstructorCall(expression: IrEnumConstructorCall, data: Unit): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        return jsonWithDefault("EnumConstructorCall", caption, expression)
    }

    override fun visitInstanceInitializerCall(expression: IrInstanceInitializerCall, data: Unit): JsonElement {
        val caption = ""
        return jsonWithDefault("InstanceInitializerCall", caption, expression)
    }

    override fun visitSetValue(expression: IrSetValue, data: Unit): JsonElement {
        val caption = "var: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}"
        return jsonWithDefault("SetValue", caption, expression)
    }

    override fun visitGetField(expression: IrGetField, data: Unit): JsonElement {
        val captionBuilder = StringBuilder()
        captionBuilder.append("field: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}")
        expression.superQualifierSymbol?.let {
            // Here we render the super qualifier’s owner (for example, its name).
            captionBuilder.append(" superQualifier: ${it.owner.name}")
        }
        captionBuilder.append(" origin: ${expression.origin}")
        return jsonWithDefault("GetField", captionBuilder.toString(), expression)
    }

    override fun visitSetField(expression: IrSetField, data: Unit): JsonElement {
        val captionBuilder = StringBuilder()
        captionBuilder.append("field: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}")
        expression.superQualifierSymbol?.let {
            captionBuilder.append(" superQualifier: ${it.owner.name}")
        }
        captionBuilder.append(" origin: ${expression.origin}")
        return jsonWithDefault("SetField", captionBuilder.toString(), expression)
    }

    override fun visitGetObjectValue(expression: IrGetObjectValue, data: Unit): JsonElement {
        val caption = "object: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}"
        return jsonWithDefault("GetObjectValue", caption, expression)
    }

    override fun visitGetEnumValue(expression: IrGetEnumValue, data: Unit): JsonElement {
        val caption = "enum: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}"
        return jsonWithDefault("GetEnumValue", caption, expression)
    }

    override fun visitWhileLoop(loop: IrWhileLoop, data: Unit): JsonElement {
        val caption = "label: ${loop.label}, origin: ${loop.origin}"
        return jsonWithDefault("WhileLoop", caption, loop)
    }

    override fun visitDoWhileLoop(loop: IrDoWhileLoop, data: Unit): JsonElement {
        val caption = "label: ${loop.label}, origin: ${loop.origin}"
        return jsonWithDefault("DoWhileLoop", caption, loop)
    }

    override fun visitBreak(jump: IrBreak, data: Unit): JsonElement {
        val caption = "label: ${jump.label}, loop label: ${jump.loop.label}"
        return jsonWithDefault("Break", caption, jump)
    }

    override fun visitContinue(jump: IrContinue, data: Unit): JsonElement {
        val caption = "label: ${jump.label}, loop label: ${jump.loop.label}"
        return jsonWithDefault("Continue", caption, jump)
    }

    override fun visitThrow(expression: IrThrow, data: Unit): JsonElement {
        val caption = "type: ${expression.type.render()}"
        return jsonWithDefault("Throw", caption, expression)
    }

    override fun visitFunctionReference(expression: IrFunctionReference, data: Unit): JsonElement {
        val caption = "symbol: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}, origin: ${expression.origin}, reflectionTarget: ${renderReflectionTarget(expression)}"
        return jsonWithDefault("FunctionReference", caption, expression)
    }

    override fun visitRawFunctionReference(expression: IrRawFunctionReference, data: Unit): JsonElement {
        val caption = "symbol: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}"
        return jsonWithDefault("RawFunctionReference", caption, expression)
    }

    // Helper used by visitFunctionReference.
    private fun renderReflectionTarget(expression: IrFunctionReference): String =
        if (expression.symbol == expression.reflectionTarget)
            "<same>"
        else
            expression.reflectionTarget?.owner?.name?.asString() ?: ""



    fun jsonWithDefault(typeName: String, caption: String, element: IrElement): JsonObject {
        val jsonObj = JsonObject().apply {
            add("NodeType", JsonPrimitive(element::class.simpleName))
            add("NodeName", JsonPrimitive(typeName))
            add("Caption", JsonPrimitive(caption))
            add("Dump", JsonPrimitive(element.accept(renderVisitor, null)))
            add("Children", JsonArray().also { childrenArray ->
                element.acceptChildren(object : IrElementVisitor<Unit, Unit> {
                    override fun visitElement(child: IrElement, data: Unit) {
                        childrenArray.add(child.accept(this@JSONIrTreeVisitor, Unit))
                    }
                }, Unit)
            })
        }
        return jsonObj
    }


}
