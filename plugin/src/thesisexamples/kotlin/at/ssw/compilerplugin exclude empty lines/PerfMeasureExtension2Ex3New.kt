val timeMarkClass: IrClassSymbol = pluginContext.findClass("kotlin/time/TimeMark")
val stringBuilderClass = pluginContext.findClass("kotlin/text/StringBuilder")
val stringBuilderConstructor = stringBuilderClass.findConstructor("StringBuilder()")
val stringBuilderAppendFunc = stringBuilderClass.findFunction("append(Int)")
val stringBuilderAppendLongFunc = stringBuilderClass.findFunction("append(Long)")
val stringBuilderAppendStringFunc = stringBuilderClass.findFunction("append(String)")
val firstFile = moduleFragment.files[0]
val stringBuilder: IrField = pluginContext.irFactory.buildField {
    name = Name.identifier("_stringBuilder")
    type = stringBuilderClass.defaultType
    isFinal = false
    isStatic = true
}.apply {
    this.initializer =
        DeclarationIrBuilder(pluginContext, firstFile.symbol).irExprBody(
            DeclarationIrBuilder(pluginContext, firstFile.symbol).irCallConstructor(
                stringBuilderConstructor,
                listOf()
            )
        )
}
firstFile.declarations.add(stringBuilder)
stringBuilder.parent = firstFile
fun buildExitMethodFunction(): IrFunction {
    val funElapsedNow = pluginContext.findFunction("kotlin/time/TimeMark.elapsedNow")
    return pluginContext.irFactory.buildFun {
        name = Name.identifier("_exit_method")
        returnType = pluginContext.irBuiltIns.unitType
    }.apply {
        addValueParameter {
            name = Name.identifier("methodId")
            type = pluginContext.irBuiltIns.intType
        }
        addValueParameter {
            name = Name.identifier("startTime")
            type = timeMarkClass.defaultType
        }
        body = DeclarationIrBuilder(pluginContext, symbol, startOffset, endOffset).irBlockBody {
            // Duration
            val elapsedDuration = irTemporary(call(pluginContext, funElapsedNow, valueParameters[1]))
            val elapsedMicrosProp: IrProperty = elapsedDuration.type.findProperty("inWholeMicroseconds").owner
            val elapsedMicros = irTemporary(call(pluginContext, elapsedMicrosProp.getter!!, elapsedDuration))
            +call(pluginContext, stringBuilderAppendStringFunc, stringBuilder, "<;")
            +call(pluginContext, stringBuilderAppendFunc, stringBuilder, valueParameters[0])
            +call(pluginContext, stringBuilderAppendStringFunc, stringBuilder, ";")
            +call(pluginContext, stringBuilderAppendLongFunc, stringBuilder, elapsedMicros)
            +call(pluginContext, stringBuilderAppendStringFunc, stringBuilder, "\n")
        }
    }
}