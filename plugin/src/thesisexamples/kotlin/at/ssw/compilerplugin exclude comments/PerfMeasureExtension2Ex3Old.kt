val timeMarkClass: IrClassSymbol =
    pluginContext.referenceClass(ClassId.fromString("kotlin/time/TimeMark"))!!

val stringBuilderClassId = ClassId.fromString("kotlin/text/StringBuilder")
val stringBuilderTypeAlias = pluginContext.referenceTypeAlias(stringBuilderClassId)
val stringBuilderClass = stringBuilderTypeAlias?.owner?.expandedType?.classOrFail
    ?: pluginContext.referenceClass(stringBuilderClassId)!!
val stringBuilderConstructor =
    stringBuilderClass.constructors.single { it.owner.valueParameters.isEmpty() }

val stringBuilderAppendFunc =
    stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.intType }
val stringBuilderAppendLongFunc =
    stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.longType }
val stringBuilderAppendStringFunc =
    stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.stringType.makeNullable() }

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
    val funElapsedNow =
        pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlin.time"),
                FqName("TimeMark"),
                Name.identifier("elapsedNow")
            )
        ).single()

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
            val elapsedDuration = irTemporary(irCall(funElapsedNow).apply {
                dispatchReceiver = irGet(valueParameters[1])
            })
            val elapsedMicrosProp: IrProperty =
                elapsedDuration.type.getClass()!!.properties.single { it.name.asString() == "inWholeMicroseconds" }

            val elapsedMicros = irTemporary(irCall(elapsedMicrosProp.getter!!).apply {
                dispatchReceiver = irGet(elapsedDuration)
            })

            +irCall(stringBuilderAppendStringFunc).apply {
                dispatchReceiver = irGetField(null, stringBuilder)
                putValueArgument(0, irString("<;"))
            }
            +irCall(stringBuilderAppendFunc).apply {
                dispatchReceiver = irGetField(null, stringBuilder)
                putValueArgument(0, irGet(valueParameters[0]))
            }
            +irCall(stringBuilderAppendStringFunc).apply {
                dispatchReceiver = irGetField(null, stringBuilder)
                putValueArgument(0, irString(";"))
            }
            +irCall(stringBuilderAppendLongFunc).apply {
                dispatchReceiver = irGetField(null, stringBuilder)
                putValueArgument(0, irGet(elapsedMicros))
            }
            +irCall(stringBuilderAppendStringFunc).apply {
                dispatchReceiver = irGetField(null, stringBuilder)
                putValueArgument(0, irString("\n"))
            }
        }
    }
}