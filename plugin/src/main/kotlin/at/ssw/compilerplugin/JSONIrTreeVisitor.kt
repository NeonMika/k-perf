package org.jetbrains.kotlin.ir.util

import org.gradle.internal.impldep.com.google.gson.JsonArray
import org.gradle.internal.impldep.com.google.gson.JsonElement
import org.gradle.internal.impldep.com.google.gson.JsonObject
import org.gradle.internal.impldep.com.google.gson.JsonPrimitive
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.backend.js.utils.asString
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrSymbol
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

    override fun visitSimpleFunction(declaration: IrSimpleFunction, data: Unit): JsonElement {
        val jsonObj = jsonWithDefault("Function", declaration.name.asString(), declaration)

        return jsonObj
    }

    override fun visitModuleFragment(declaration: IrModuleFragment, data: Unit): JsonElement {
        val jsonObj = jsonWithDefault("Module Fragment", declaration.name.asString(), declaration)

        return jsonObj
    }

    override fun visitFile(declaration: IrFile, data: Unit): JsonElement {
        val jsonObj = jsonWithDefault("File", File(declaration.path).name, declaration)

        return jsonObj
    }

    override fun visitBlockBody(body: IrBlockBody, data: Unit): JsonElement {
        val jsonObj = jsonWithDefault("Block Body", "", body)

        return jsonObj
    }

    override fun visitValueParameter(declaration: IrValueParameter, data: Unit): JsonElement {
        val jsonObj = jsonWithDefault("Value Parameter", declaration.name.asString(), declaration)

        return jsonObj

    }

    override fun visitCall(expression: IrCall, data: Unit): JsonElement {
        val jsonObj = jsonWithDefault("Call", expression.symbol.owner.name.asString(), expression)

        return jsonObj
    }

    override fun visitConst(expression: IrConst<*>, data: Unit): JsonElement {
        val jsonObj = jsonWithDefault("Const", "${expression.kind.asString}\n${expression.value}", expression)

        return jsonObj
    }


    fun jsonWithDefault(typeName: String, caption: String, element: IrElement): JsonElement {
        val jsonObj = JsonObject().apply {
            add("nodeType", JsonPrimitive(element::class.simpleName))
            add("typeName", JsonPrimitive(typeName))
            add("caption", JsonPrimitive(caption))
            add("render", JsonPrimitive(element.accept(renderVisitor, null)))
            add("children", JsonArray().also { childrenArray ->
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
