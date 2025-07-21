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

data class PassedData(val relationship: String, val objects: MutableList<Any>, val functionOwners: MutableMap<Any, Int>) {
    fun getFunctionId(owner: Any): Int {
        var id = functionOwners[owner]
        if (id == null) {
            id = functionOwners.size
            functionOwners[owner] = id
        }
        return id
    }
}

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
        jsonObj.add(
            "Content",
            JsonPrimitive(File(declaration.path).readText().replace("\r\n", "\n").replace("\r", "\n"))
        )
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
        jsonObj.add("FunctionIdentity", JsonPrimitive(data.getFunctionId(declaration.symbol.owner)))
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
        jsonObj.add("FunctionIdentity", JsonPrimitive(data.getFunctionId(expression.symbol.owner)))
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
        jsonObj.add("FunctionIdentity", JsonPrimitive(data.getFunctionId(declaration.symbol.owner)))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitProperty(declaration: IrProperty, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Property", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name.toString()))
        jsonObj.add("Visibility", JsonPrimitive(declaration.visibility.name))
        jsonObj.add("Modality", JsonPrimitive(declaration.modality.name))
        jsonObj.add("IsVar", JsonPrimitive(declaration.isVar))
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
        val jsonObj = jsonWithDefault("While Loop", "", loop, data)
        jsonObj.add("Origin", JsonPrimitive(loop.origin.toString()))
        return jsonObj
    }

    override fun visitExpression(expression: IrExpression, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Expression", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitDeclaration(declaration: IrDeclarationBase, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Declaration", "", declaration, data)
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitFunction(declaration: IrFunction, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Function", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name.toString()))
        jsonObj.add("ReturnType", JsonPrimitive(declaration.returnType.render()))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        jsonObj.add("FunctionIdentity", JsonPrimitive(data.getFunctionId(declaration.symbol.owner)))
        return jsonObj
    }

    override fun visitExternalPackageFragment(declaration: IrExternalPackageFragment, data: PassedData): JsonElement {
        val caption = declaration.packageFqName.toString()
        val jsonObj = jsonWithDefault("External Package Fragment", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.packageFqName.toString()))
        return jsonObj
    }

    override fun visitScript(declaration: IrScript, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Script", "", declaration, data)
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitVariable(declaration: IrVariable, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Variable", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name.toString()))
        jsonObj.add("Type", JsonPrimitive(declaration.type.render()))
        jsonObj.add("IsVar", JsonPrimitive(declaration.isVar))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitEnumEntry(declaration: IrEnumEntry, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Enum Entry", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name.toString()))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitAnonymousInitializer(declaration: IrAnonymousInitializer, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Anonymous Initializer", "", declaration, data)
        jsonObj.add("IsStatic", JsonPrimitive(declaration.isStatic))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitTypeParameter(declaration: IrTypeParameter, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Type Parameter", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name.toString()))
        jsonObj.add("Index", JsonPrimitive(declaration.index))
        jsonObj.add("Variance", JsonPrimitive(declaration.variance.toString()))
        jsonObj.add("IsReified", JsonPrimitive(declaration.isReified))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitLocalDelegatedProperty(declaration: IrLocalDelegatedProperty, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Local Delegated Property", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name.toString()))
        jsonObj.add("Type", JsonPrimitive(declaration.type.render()))
        jsonObj.add("IsVar", JsonPrimitive(declaration.isVar))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitTypeAlias(declaration: IrTypeAlias, data: PassedData): JsonElement {
        val caption = declaration.name.toString()
        val jsonObj = jsonWithDefault("Type Alias", caption, declaration, data)
        jsonObj.add("Name", JsonPrimitive(declaration.name.toString()))
        jsonObj.add("Visibility", JsonPrimitive(declaration.visibility.name))
        jsonObj.add("ExpandedType", JsonPrimitive(declaration.expandedType.render()))
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitSyntheticBody(body: IrSyntheticBody, data: PassedData): JsonElement {
        val caption = body.kind.toString()
        val jsonObj = jsonWithDefault("Synthetic Body", caption, body, data)
        jsonObj.add("Kind", JsonPrimitive(body.kind.toString()))
        return jsonObj
    }

    override fun visitVararg(expression: IrVararg, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Vararg", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("VarargElementType", JsonPrimitive(expression.varargElementType.render()))
        return jsonObj
    }

    override fun visitSpreadElement(spread: IrSpreadElement, data: PassedData): JsonElement {
        return jsonWithDefault("Spread Element", "", spread, data)
    }

    override fun visitComposite(expression: IrComposite, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Composite", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        return jsonObj
    }

    override fun visitReturn(expression: IrReturn, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Return", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitConstructorCall(expression: IrConstructorCall, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Constructor Call", caption, expression, data)
        jsonObj.add("ConstructorName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("ReturnType", JsonPrimitive(expression.type.render()))
        jsonObj.add("FunctionIdentity", JsonPrimitive(data.getFunctionId(expression.symbol.owner)))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        jsonObj.add("Name", JsonPrimitive(expression.symbol.owner.name.asString()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitDelegatingConstructorCall(
        expression: IrDelegatingConstructorCall,
        data: PassedData
    ): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Delegating Constructor Call", caption, expression, data)
        jsonObj.add("ConstructorName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("ReturnType", JsonPrimitive(expression.type.render()))
        jsonObj.add("FunctionIdentity", JsonPrimitive(data.getFunctionId(expression.symbol.owner)))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        jsonObj.add("Name", JsonPrimitive(expression.symbol.owner.name.asString()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitEnumConstructorCall(expression: IrEnumConstructorCall, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Enum Constructor Call", caption, expression, data)
        jsonObj.add("ConstructorName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("ReturnType", JsonPrimitive(expression.type.render()))
        jsonObj.add("FunctionIdentity", JsonPrimitive(data.getFunctionId(expression.symbol.owner)))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        jsonObj.add("Name", JsonPrimitive(expression.symbol.owner.name.asString()))
        return jsonObj
    }

    override fun visitInstanceInitializerCall(expression: IrInstanceInitializerCall, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Instance Initializer Call", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitSetValue(expression: IrSetValue, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Set Value", caption, expression, data)
        jsonObj.add("OwnerName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitSetField(expression: IrSetField, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Set Field", caption, expression, data)
        jsonObj.add("OwnerName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitGetObjectValue(expression: IrGetObjectValue, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Get Object Value", caption, expression, data)
        jsonObj.add("OwnerName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitGetEnumValue(expression: IrGetEnumValue, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Get Enum Value", caption, expression, data)
        jsonObj.add("OwnerName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitDoWhileLoop(loop: IrDoWhileLoop, data: PassedData): JsonElement {
        val caption = loop.label ?: ""
        val jsonObj = jsonWithDefault("Do While Loop", caption, loop, data)
        jsonObj.add("Type", JsonPrimitive(loop.type.render()))
        jsonObj.add("Origin", JsonPrimitive(loop.origin.toString()))
        return jsonObj
    }

    override fun visitBreak(jump: IrBreak, data: PassedData): JsonElement {
        val caption = jump.label ?: ""
        val jsonObj = jsonWithDefault("Break", caption, jump, data)
        jsonObj.add("Type", JsonPrimitive(jump.type.render()))
        return jsonObj
    }

    override fun visitContinue(jump: IrContinue, data: PassedData): JsonElement {
        val caption = jump.label ?: ""
        val jsonObj = jsonWithDefault("Continue", caption, jump, data)
        jsonObj.add("Type", JsonPrimitive(jump.type.render()))
        return jsonObj
    }

    override fun visitThrow(expression: IrThrow, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Throw", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitFunctionReference(expression: IrFunctionReference, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Function Reference", caption, expression, data)
        jsonObj.add("FunctionName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitRawFunctionReference(expression: IrRawFunctionReference, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Raw Function Reference", caption, expression, data)
        jsonObj.add("FunctionName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitCatch(aCatch: IrCatch, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Catch", "", aCatch, data)
        return jsonObj
    }

    override fun visitClassReference(expression: IrClassReference, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Class Reference", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitConstantArray(expression: IrConstantArray, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Constant Array", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitConstantObject(expression: IrConstantObject, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Constant Object", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitConstantPrimitive(expression: IrConstantPrimitive, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Constant Primitive", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitDynamicMemberExpression(expression: IrDynamicMemberExpression, data: PassedData): JsonElement {
        val caption = expression.memberName
        val jsonObj = jsonWithDefault("Dynamic Member Expression", caption, expression, data)
        jsonObj.add("MemberName", JsonPrimitive(expression.memberName))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitDynamicOperatorExpression(
        expression: IrDynamicOperatorExpression,
        data: PassedData
    ): JsonElement {
        val jsonObj = jsonWithDefault("Dynamic Operator Expression", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitErrorCallExpression(expression: IrErrorCallExpression, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Error Call Expression", "", expression, data)
        jsonObj.add("Description", JsonPrimitive(expression.description))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitErrorDeclaration(declaration: IrErrorDeclaration, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Error Declaration", "", declaration, data)
        jsonObj.add("Origin", JsonPrimitive(declaration.origin.toString()))
        return jsonObj
    }

    override fun visitErrorExpression(expression: IrErrorExpression, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Error Expression", "", expression, data)
        jsonObj.add("Description", JsonPrimitive(expression.description))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Function Expression", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        return jsonObj
    }

    override fun visitGetClass(expression: IrGetClass, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Get Class", "", expression, data)
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitLocalDelegatedPropertyReference(
        expression: IrLocalDelegatedPropertyReference,
        data: PassedData
    ): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Local Delegated Property Reference", caption, expression, data)
        jsonObj.add("OwnerName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("Delegate", JsonPrimitive(expression.delegate.owner.name.toString()))
        jsonObj.add("Getter", JsonPrimitive(expression.getter.owner.name.toString()))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        return jsonObj
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitPropertyReference(expression: IrPropertyReference, data: PassedData): JsonElement {
        val caption = expression.symbol.owner.name.asString()
        val jsonObj = jsonWithDefault("Property Reference", caption, expression, data)
        jsonObj.add("OwnerName", JsonPrimitive(expression.symbol.owner.name.asString()))
        jsonObj.add("Type", JsonPrimitive(expression.type.render()))
        jsonObj.add("Field", JsonPrimitive(expression.field?.owner?.name.toString()))
        jsonObj.add("Getter", JsonPrimitive(expression.getter?.owner?.name.toString()))
        jsonObj.add("Setter", JsonPrimitive(expression.setter?.owner?.name.toString()))
        jsonObj.add("Origin", JsonPrimitive(expression.origin.toString()))
        return jsonObj
    }

    override fun visitTry(aTry: IrTry, data: PassedData): JsonElement {
        val jsonObj = jsonWithDefault("Try", "", aTry, data)
        jsonObj.add("Type", JsonPrimitive(aTry.type.render()))
        return jsonObj
    }


    private fun jsonWithDefault(typeName: String, caption: String, irElement: IrElement, data: PassedData): JsonObject {
        data.objects.add(irElement)
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
            add("Relationship", JsonPrimitive(data.relationship))
            add("ObjectIdentity", JsonPrimitive(data.objects.size - 1))
            add("Children", JsonArray().also { childrenArray ->
                irElement.acceptChildren(object : IrElementVisitor<Unit, PassedData> {
                    override fun visitElement(element: IrElement, data: PassedData) {
                        val property: String = irElement.getPropertyName(element) ?: "Not found"
                        childrenArray.add(
                            element.accept(
                                this@JSONIrTreeVisitor,
                                PassedData(property, data.objects, data.functionOwners)
                            )
                        )
                    }
                }, PassedData("", data.objects, data.functionOwners))
            })
        }
        return jsonObj
    }


}
