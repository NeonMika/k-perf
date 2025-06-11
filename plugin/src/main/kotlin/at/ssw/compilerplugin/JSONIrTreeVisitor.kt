package at.ssw.compilerplugin

import com.google.gson.*
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.RenderIrElementVisitor
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.util.sourceElement
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import java.io.File

data class PassedData(val property: String, val map: MutableMap<Int, Any>)

class JSONIrTreeVisitor : IrElementVisitor<JsonElement, PassedData> {

    private val renderVisitor = RenderIrElementVisitor()

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
        jsonObj.add("Content", JsonPrimitive(File(declaration.path).readText().replace("\r\n", "\n").replace("\r", "\n")))
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

    @OptIn(UnsafeDuringIrConstructionAPI::class)
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

    @OptIn(UnsafeDuringIrConstructionAPI::class)
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

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitConstructorCall(expression: IrConstructorCall, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Call", expression.symbol.owner.name.asString(), expression, data)

        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitDelegatingConstructorCall(expression: IrDelegatingConstructorCall, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        return jsonWithDefault("DelegatingConstructorCall", caption, expression, data)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitEnumConstructorCall(expression: IrEnumConstructorCall, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        return jsonWithDefault("EnumConstructorCall", caption, expression, data)
    }

    override fun visitInstanceInitializerCall(expression: IrInstanceInitializerCall, data: PassedData): JsonElement {
        val caption = ""
        return jsonWithDefault("InstanceInitializerCall", caption, expression, data)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitSetValue(expression: IrSetValue, data: PassedData): JsonElement {
        val caption = "var: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}"
        return jsonWithDefault("SetValue", caption, expression, data)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitSetField(expression: IrSetField, data: PassedData): JsonElement {
        val captionBuilder = StringBuilder()
        captionBuilder.append("field: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}")
        expression.superQualifierSymbol?.let {
            captionBuilder.append(" superQualifier: ${it.owner.name}")
        }
        captionBuilder.append(" origin: ${expression.origin}")
        return jsonWithDefault("SetField", captionBuilder.toString(), expression, data)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitGetObjectValue(expression: IrGetObjectValue, data: PassedData): JsonElement {
        val caption = "object: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}"
        return jsonWithDefault("GetObjectValue", caption, expression, data)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
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

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitFunctionReference(expression: IrFunctionReference, data: PassedData): JsonElement {
        val caption =
            "symbol: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}, origin: ${expression.origin}, reflectionTarget: ${
                renderReflectionTarget(expression)
            }"
        return jsonWithDefault("FunctionReference", caption, expression, data)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitRawFunctionReference(expression: IrRawFunctionReference, data: PassedData): JsonElement {
        val caption = "symbol: ${expression.symbol.owner.name.asString()}, type: ${expression.type.render()}"
        return jsonWithDefault("RawFunctionReference", caption, expression, data)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun renderReflectionTarget(expression: IrFunctionReference): String =
        if (expression.symbol == expression.reflectionTarget)
            "<same>"
        else
            expression.reflectionTarget?.owner?.name?.asString() ?: ""


    private fun jsonWithDefault(typeName: String, caption: String, irElement: IrElement, data: PassedData): JsonObject {
        data.map[System.identityHashCode(irElement)]=irElement
        val jsonObj = JsonObject().apply {
            add("NodeType", JsonPrimitive(irElement::class.simpleName))
            add("NodeName", JsonPrimitive(typeName))
            add("Caption", JsonPrimitive(caption))
            add("Dump", JsonPrimitive(irElement.accept(renderVisitor, null)))
            val startOffset = irElement.sourceElement()?.startOffset
            val endOffset = irElement.sourceElement()?.endOffset
            if (startOffset != null) {
                add("StartOffset", JsonPrimitive(startOffset))
            }
            if (endOffset != null) {
                add("EndOffset", JsonPrimitive(endOffset))
            }
            add("Relationship", JsonPrimitive(data.property))
            add("ObjectIdentity", JsonPrimitive(System.identityHashCode(irElement)))
            add("Children", JsonArray().also { childrenArray ->
                irElement.acceptChildren(object : IrElementVisitor<Unit, PassedData> {
                    override fun visitElement(element: IrElement, data: PassedData) {
                        val property: String = irElement.getPropertyName(element) ?: "Not found"
                        childrenArray.add(element.accept(this@JSONIrTreeVisitor, PassedData(property, data.map)))
                    }
                }, PassedData("", data.map))
            })
        }
        return jsonObj
    }


}
