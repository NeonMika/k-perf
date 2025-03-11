package org.jetbrains.kotlin.ir.util

import org.gradle.internal.impldep.com.google.gson.JsonArray
import org.gradle.internal.impldep.com.google.gson.JsonElement
import org.gradle.internal.impldep.com.google.gson.JsonObject
import org.gradle.internal.impldep.com.google.gson.JsonPrimitive
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.utils.Printer


class JSONIrTreeVisitor(
    private val options: DumpIrTreeOptions = DumpIrTreeOptions()
) : IrElementVisitor<JsonElement, Unit> {

    private val renderVisitor = RenderIrElementVisitor(options)

    override fun visitElement(element: IrElement, data: Unit): JsonElement {
        val jsonObj = JsonObject().apply {
            add("nodeType", JsonPrimitive(element::class.simpleName ?: "Unknown"))
            if (element is IrDeclarationWithName) {
                add("caption", JsonPrimitive(element.name.asString()))
            } else {
                add("caption", JsonPrimitive(""))
            }
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
