package org.jetbrains.kotlin.ir.util

import getPropertyName
import org.gradle.internal.impldep.com.google.gson.JsonArray
import org.gradle.internal.impldep.com.google.gson.JsonElement
import org.gradle.internal.impldep.com.google.gson.JsonObject
import org.gradle.internal.impldep.com.google.gson.JsonPrimitive
import org.jetbrains.kotlin.backend.jvm.ir.hasChild
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
import org.jetbrains.kotlin.psi
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.utils.Printer
import java.io.File
import kotlin.reflect.full.memberProperties

data class PassedData(val property: String, val map: MutableMap<Int, Any>)

class JSONIrTreeVisitor(
    private val options: DumpIrTreeOptions = DumpIrTreeOptions()
) : IrElementVisitor<JsonElement, PassedData> {

    private val renderVisitor = RenderIrElementVisitor(options)

    override fun visitElement(element: IrElement, data: PassedData): JsonElement {
        var caption = ""
        if (element is IrDeclarationWithName) {
            caption = element.name.asString()
        }

        val jsonObj = jsonWithDefault(element::class.simpleName ?: "Unknown", caption, element, data)
        return jsonObj
    }

    override fun visitModuleFragment(declaration: IrModuleFragment, data: PassedData): JsonElement {
        val caption = declaration.name.asString()
        val jsonObj = jsonWithDefault("Module Fragment", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name.asString()))
        return jsonObj
    }

    override fun visitFile(declaration: IrFile, data: PassedData): JsonElement {
        val caption = declaration.name
        val jsonObj = jsonWithDefault("File", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name))
        jsonObj.add("Path", JsonPrimitive(declaration.path))
        jsonObj.add("Content", JsonPrimitive(File(declaration.path).readText()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitSimpleFunction(declaration: IrSimpleFunction, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Function", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name.toString()))
        jsonObj.add("Visibility", JsonPrimitive(declaration.visibility.name))
        jsonObj.add("Modality", JsonPrimitive(declaration.modality.name))
        jsonObj.add("ReturnType", JsonPrimitive(declaration.returnType.render()))
        jsonObj.add("FunctionIdentity", JsonPrimitive(System.identityHashCode(declaration.symbol.owner)))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitBlockBody(body: IrBlockBody, data: PassedData): JsonElement {
        return jsonWithDefault("Block Body", "", body, data)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitCall(expression: IrCall, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Call", caption, expression, data)
        jsonObj.add("FunctionName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("ReturnType", JsonPrimitive(expression.type.render()))
        jsonObj.add("FunctionIdentity", JsonPrimitive(System.identityHashCode(expression.symbol.owner)))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        jsonObj.add("Name", JsonPrimitive(expression.symbol.owner.name.asString()))
        return jsonObj
    }

    override fun visitConst(expression: IrConst<*>, data: PassedData): JsonElement {
        val caption = expression.kind.asString
        val jsonObj = jsonWithDefault("Constant", caption, expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("Kind", JsonPrimitive(expression.kind.asString))
        jsonObj.add("Value", JsonPrimitive(expression.value.toString()))
        return jsonObj
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Type Operator", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("TypeOperand", JsonPrimitive(expression.typeOperand.render()))
        jsonObj.add("Operator", JsonPrimitive(expression.operator.name))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitGetValue(expression: IrGetValue, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Get Value", caption, expression, data)
        jsonObj.add("VariableName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        return jsonObj
    }

    override fun visitBlock(expression: IrBlock, data: PassedData): JsonElement {
        val name = when (expression) {
            is IrReturnableBlock -> "Returnable Block"
            is IrInlinedFunctionBlock -> "Inlined Block"
            else -> "Block"
        }
        val jsonObj = jsonWithDefault(name, "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        return jsonObj
    }

    override fun visitWhen(expression: IrWhen, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("When", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        return jsonObj
    }

    override fun visitBranch(branch: IrBranch, data: PassedData): JsonElement {
        return jsonWithDefault("Branch", "", branch, data)
    }

    override fun visitExpressionBody(body: IrExpressionBody, data: PassedData): JsonElement {
        return jsonWithDefault("ExpressionBody", "", body, data)
    }

    override fun visitValueParameter(declaration: IrValueParameter, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Value Parameter", caption, declaration, data)
        jsonObj.add("Type", JsonPrimitive(declaration.type.render()))
        jsonObj.add("Name", JsonPrimitive(declaration.name.asString()))
        jsonObj.add("Index", JsonPrimitive(declaration.index))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitStringConcatenation(expression: IrStringConcatenation, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("String Concatenation", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitConstructor(declaration: IrConstructor, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Constructor", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name.toString()))
        jsonObj.add("Visibility", JsonPrimitive(declaration.visibility.name))
        jsonObj.add("ReturnType", JsonPrimitive(declaration.returnType.render()))
        jsonObj.add("FunctionIdentity", JsonPrimitive(System.identityHashCode(declaration.symbol.owner)))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitProperty(declaration: IrProperty, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Property", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name.toString()))
        jsonObj.add("Visibility", JsonPrimitive(declaration.visibility.name))
        jsonObj.add("Modality", JsonPrimitive(declaration.modality.name))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitField(declaration: IrField, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Field", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name.toString()))
        jsonObj.add("Visibility", JsonPrimitive(declaration.visibility.name))
        jsonObj.add("Type", JsonPrimitive(declaration.type.render()))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitClass(declaration: IrClass, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Class", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name.toString()))
        jsonObj.add("Visibility", JsonPrimitive(declaration.visibility.name))
        jsonObj.add("Modality", JsonPrimitive(declaration.modality.name))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitGetField(expression: IrGetField, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Get Field", caption, expression, data)
        jsonObj.add("VariableName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        return jsonObj
    }

    override fun visitWhileLoop(loop: IrWhileLoop, data: PassedData): JsonElement {
        return jsonWithDefault("WhileLoop", "", loop, data)
    }

    override fun visitExpression(expression: IrExpression, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Expression", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    override fun visitDeclaration(declaration: IrDeclarationBase, data: PassedData): JsonElement {
        val caption = declaration::class.java.simpleName
        return jsonWithDefault("Declaration", caption, declaration, data)
    }

    override fun visitFunction(declaration: IrFunction, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Function", caption, declaration, data)
        return jsonObj
    }

    override fun visitExternalPackageFragment(declaration: IrExternalPackageFragment, data: PassedData): JsonElement {
        val caption = declaration.packageFqName.toString()
        return jsonWithDefault("ExternalPackageFragment", caption, declaration, data)
    }

    override fun visitScript(declaration: IrScript, data: PassedData): JsonElement {
        return jsonWithDefault("Script", "Script", declaration, data)
    }

// (Helper renderValueParameterTypes() is used only for string-based rendering so we omit it in JSON mode)

    override fun visitVariable(declaration: IrVariable, data: PassedData): JsonElement {
        // Assuming normalizedName(variableNameData) gives a proper name string.
        val caption = declaration.name.asString()
        return jsonWithDefault("Variable", caption, declaration, data)
    }

    override fun visitEnumEntry(declaration: IrEnumEntry, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        return jsonWithDefault("EnumEntry", caption, declaration, data)
    }

    override fun visitAnonymousInitializer(declaration: IrAnonymousInitializer, data: PassedData): JsonElement {
        val caption = if (declaration.isStatic) "static" else "instance"
        return jsonWithDefault("AnonymousInitializer", caption, declaration, data)
    }

    override fun visitTypeParameter(declaration: IrTypeParameter, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        return jsonWithDefault("TypeParameter", caption, declaration, data)
    }

    override fun visitLocalDelegatedProperty(declaration: IrLocalDelegatedProperty, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        return jsonWithDefault("LocalDelegatedProperty", caption, declaration, data)
    }

    override fun visitTypeAlias(declaration: IrTypeAlias, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        return jsonWithDefault("TypeAlias", caption, declaration, data)
    }

    override fun visitSyntheticBody(body: IrSyntheticBody, data: PassedData): JsonElement {
        val caption = body.kind.toString()
        return jsonWithDefault("SyntheticBody", caption, body, data)
    }

    override fun visitVararg(expression: IrVararg, data: PassedData): JsonElement {
        val caption = "type: ${expression.type.render()}, varargElementType: ${expression.varargElementType.render()}"
        return jsonWithDefault("Vararg", caption, expression, data)
    }

    override fun visitSpreadElement(spread: IrSpreadElement, data: PassedData): JsonElement {
        return jsonWithDefault("SpreadElement", "SpreadElement", spread, data)
    }

    override fun visitComposite(expression: IrComposite, data: PassedData): JsonElement {
        val caption = "type=${expression.type.render()}, origin=${expression.origin}"
        return jsonWithDefault("Composite", caption, expression, data)
    }

    override fun visitReturn(expression: IrReturn, data: PassedData): JsonElement {
        val caption = ""
        return jsonWithDefault("Return", caption, expression, data)
    }

    override fun visitConstructorCall(expression: IrConstructorCall, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Call", expression.symbol.owner.name.asString(), expression, data)

        return jsonObj
    }

    override fun visitDelegatingConstructorCall(expression: IrDelegatingConstructorCall, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        return jsonWithDefault("DelegatingConstructorCall", caption, expression, data)
    }

    override fun visitEnumConstructorCall(expression: IrEnumConstructorCall, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        return jsonWithDefault("EnumConstructorCall", caption, expression, data)
    }

    override fun visitInstanceInitializerCall(expression: IrInstanceInitializerCall, data: PassedData): JsonElement {
        val caption = ""
        return jsonWithDefault("InstanceInitializerCall", caption, expression, data)
    }

    override fun visitSetValue(expression: IrSetValue, data: PassedData): JsonElement {
        val caption = "var: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}"
        return jsonWithDefault("SetValue", caption, expression, data)
    }

    override fun visitSetField(expression: IrSetField, data: PassedData): JsonElement {
        val captionBuilder = StringBuilder()
        captionBuilder.append("field: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}")
        expression.superQualifierSymbol?.let {
            captionBuilder.append(" superQualifier: ${it.owner.name}")
        }
        captionBuilder.append(" origin: ${expression.origin}")
        return jsonWithDefault("SetField", captionBuilder.toString(), expression, data)
    }

    override fun visitGetObjectValue(expression: IrGetObjectValue, data: PassedData): JsonElement {
        val caption = "object: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}"
        return jsonWithDefault("GetObjectValue", caption, expression, data)
    }

    override fun visitGetEnumValue(expression: IrGetEnumValue, data: PassedData): JsonElement {
        val caption = "enum: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}"
        return jsonWithDefault("GetEnumValue", caption, expression, data)
    }

    override fun visitDoWhileLoop(loop: IrDoWhileLoop, data: PassedData): JsonElement {
        val caption = "label: ${loop.label}, origin: ${loop.origin}"
        return jsonWithDefault("DoWhileLoop", caption, loop, data)
    }

    override fun visitBreak(jump: IrBreak, data: PassedData): JsonElement {
        val caption = "label: ${jump.label}, loop label: ${jump.loop.label}"
        return jsonWithDefault("Break", caption, jump, data)
    }

    override fun visitContinue(jump: IrContinue, data: PassedData): JsonElement {
        val caption = "label: ${jump.label}, loop label: ${jump.loop.label}"
        return jsonWithDefault("Continue", caption, jump, data)
    }

    override fun visitThrow(expression: IrThrow, data: PassedData): JsonElement {
        val caption = "type: ${expression.type.render()}"
        return jsonWithDefault("Throw", caption, expression, data)
    }

    override fun visitFunctionReference(expression: IrFunctionReference, data: PassedData): JsonElement {
        val caption =
            "symbol: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}, origin: ${expression.origin}, reflectionTarget: ${
                renderReflectionTarget(expression)
            }"
        return jsonWithDefault("FunctionReference", caption, expression, data)
    }

    override fun visitRawFunctionReference(expression: IrRawFunctionReference, data: PassedData): JsonElement {
        val caption = "symbol: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}"
        return jsonWithDefault("RawFunctionReference", caption, expression, data)
    }

    // Helper used by visitFunctionReference.
    private fun renderReflectionTarget(expression: IrFunctionReference): String =
        if (expression.symbol == expression.reflectionTarget)
            "<same>"
        else
            expression.reflectionTarget?.owner?.name?.asString() ?: ""


    fun jsonWithDefault(typeName: String, caption: String, element: IrElement, data: PassedData): JsonObject {
        data.map[System.identityHashCode(element)]=element
        val jsonObj = JsonObject().apply {
            add("NodeType", JsonPrimitive(element::class.simpleName))
            add("NodeName", JsonPrimitive(typeName))
            add("Caption", JsonPrimitive(caption))
            add("Dump", JsonPrimitive(element.accept(renderVisitor, null)))
            val startOffset = element.sourceElement()?.startOffset;
            val endOffset = element.sourceElement()?.endOffset
            if (startOffset != null) {
                add("StartOffset", JsonPrimitive(startOffset))
            }
            if (endOffset != null) {
                add("EndOffset", JsonPrimitive(endOffset))
            }
            add("Property", JsonPrimitive(data.property))
            add("ObjectIdentity", JsonPrimitive(System.identityHashCode(element)))
            add("Children", JsonArray().also { childrenArray ->
                element.acceptChildren(object : IrElementVisitor<Unit, PassedData> {
                    override fun visitElement(child: IrElement, data: PassedData) {
                        val property: String = element.getPropertyName(child) ?: "Not found";
                        childrenArray.add(child.accept(this@JSONIrTreeVisitor, PassedData(property, data.map)))
                    }
                }, PassedData("", data.map))
            })
        }
        return jsonObj
    }


}
