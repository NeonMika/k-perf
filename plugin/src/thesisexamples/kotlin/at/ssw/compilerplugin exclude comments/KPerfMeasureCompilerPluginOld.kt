package at.ssw.compilerplugin

import at.ssw.compilerplugin.ExampleConfigurationKeys.KEY_ENABLED
import at.ssw.compilerplugin.ExampleConfigurationKeys.LOG_ANNOTATION_KEY
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irCatch
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.declarations.buildVariable
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin.Companion.ADAPTER_FOR_CALLABLE_REFERENCE
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.presentableDescription
import java.io.File
import java.nio.file.Paths
import kotlin.collections.set
import kotlin.io.path.absolutePathString
import kotlin.time.ExperimentalTime

object ExampleConfigurationKeys {
    val KEY_ENABLED: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("enabled")
    val LOG_ANNOTATION_KEY: CompilerConfigurationKey<MutableList<String>> =
        CompilerConfigurationKey.create("measure annotation")
}

@OptIn(ExperimentalCompilerApi::class)
class KPerfMeasureCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "k-perf-measure-compiler-plugin"
    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            "enabled",
            "<true|false>",
            "whether plugin is enabled"
        ),
        CliOption(
            "annotation",
            "<fully qualified annotation name>",
            "methods that are annotated with this name will be measured",
            required = true,
            allowMultipleOccurrences = true
        )
    )

    init {
        println("KPerfMeasureCommandLineProcessor - init")
    }

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        println("KPerfMeasureCommandLineProcessor - processOption ($option, $value)")
        when (option.optionName) {
            "enabled" -> configuration.put(KEY_ENABLED, value.toBoolean())
            "annotation" -> {
                configuration.putIfAbsent(LOG_ANNOTATION_KEY, mutableListOf()).add(value)
            }

            else -> throw CliOptionProcessingException("KPerfMeasureCommandLineProcessor.processOption encountered unknown CLI compiler plugin option: ${option.optionName}")
        }
    }
}

@OptIn(ExperimentalCompilerApi::class)
class PerfMeasureComponentRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    init {
        println("PerfMeasureComponentRegistrar - init")
    }

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == false) {
            return
        }

        val messageCollector = configuration.get(CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY)!!

        IrGenerationExtension.registerExtension(PerfMeasureExtension2(MessageCollector.NONE))
    }
}

class PerfMeasureExtension2(
    private val messageCollector: MessageCollector
) : IrGenerationExtension {
    @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        println("PerfMeasureExtension2.generate")
        messageCollector.report(
            CompilerMessageSeverity.STRONG_WARNING,
            "PerfMeasureExtension2.generate"
        )

        val timeMarkClass: IrClassSymbol =
            pluginContext.referenceClass(ClassId.fromString("kotlin/time/TimeMark"))!!

        val stringBuilderClassId = ClassId.fromString("kotlin/text/StringBuilder")
        val stringBuilderTypeAlias = pluginContext.referenceTypeAlias(stringBuilderClassId)
        val stringBuilderClass = stringBuilderTypeAlias?.owner?.expandedType?.classOrFail
            ?: pluginContext.referenceClass(stringBuilderClassId)!!

        val stringBuilderConstructor =
            stringBuilderClass.constructors.single { it.owner.valueParameters.isEmpty() }
        val stringBuilderAppendIntFunc =
            stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.intType }
        val stringBuilderAppendLongFunc =
            stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.longType }
        val stringBuilderAppendStringFunc =
            stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.stringType.makeNullable() }

        val printlnFunc =
            pluginContext.referenceFunctions(CallableId(FqName("kotlin.io"), Name.identifier("println"))).single {
                it.owner.valueParameters.run { size == 1 && get(0).type == pluginContext.irBuiltIns.anyNType }
            }

        val debugFile = File("./DEBUG.txt")
        debugFile.delete()
        val rawSinkClass =
            pluginContext.referenceClass(ClassId.fromString("kotlinx/io/RawSink"))!!

        val pathConstructionFunc = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io.files"),
                Name.identifier("Path")
            )
        ).single { it.owner.valueParameters.size == 1 }

        val systemFileSystem = pluginContext.referenceProperties(
            CallableId(
                FqName("kotlinx.io.files"),
                Name.identifier("SystemFileSystem")
            )
        ).single()
        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail
        val sinkFunc = systemFileSystemClass.functions.single { it.owner.name.asString() == "sink" }
        val bufferedFunc = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                Name.identifier("buffered")
            )
        ).single { it.owner.extensionReceiverParameter!!.type == sinkFunc.owner.returnType }
        debugFile.appendText("1")
        debugFile.appendText(pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                Name.identifier("writeString")
            )
        ).joinToString(";") { it.owner.valueParameters.joinToString(",") { it.type.classFqName.toString() } })
        val writeStringFunc = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                Name.identifier("writeString")
            )
        ).single {
            it.owner.valueParameters.size == 3 &&
                    it.owner.valueParameters[0].type == pluginContext.irBuiltIns.stringType &&
                    it.owner.valueParameters[1].type == pluginContext.irBuiltIns.intType &&
                    it.owner.valueParameters[2].type == pluginContext.irBuiltIns.intType
        }
        val flushFunc = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                FqName("Sink"),
                Name.identifier("flush")
            )
        ).single()
        debugFile.appendText("2")
        val toStringFunc = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlin"),
                Name.identifier("toString")
            )
        ).single()
        debugFile.appendText("3")

        val STRINGBUILDER_MODE = false

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


        val currentMillis = System.currentTimeMillis()

        val randomDefaultObjectClass =
            pluginContext.referenceClass(ClassId.fromString("kotlin/random/Random.Default"))!!
        val nextIntFunc = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlin.random"),
                FqName("Random.Default"),
                Name.identifier("nextInt")
            )
        ).single {
            it.owner.valueParameters.isEmpty()
        }

        val randomNumber = pluginContext.irFactory.buildField {
            name = Name.identifier("_randNumber")
            type = pluginContext.irBuiltIns.intType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(irCall(nextIntFunc).apply {
                    dispatchReceiver = irGetObject(randomDefaultObjectClass)
                })
            }
        }
        firstFile.declarations.add(randomNumber)
        randomNumber.parent = firstFile

        val bufferedTraceFileName = pluginContext.irFactory.buildField {
            name = Name.identifier("_bufferedTraceFileName")
            type = pluginContext.irBuiltIns.stringType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(
                    irConcat().apply {
                        addArgument(irString("./trace_${pluginContext.platform!!.presentableDescription}_"))
                        addArgument(irGetField(null, randomNumber))
                        addArgument(irString(".txt"))
                    })
            }
        }
        firstFile.declarations.add(bufferedTraceFileName)
        bufferedTraceFileName.parent = firstFile

        val bufferedTraceFileSink = pluginContext.irFactory.buildField {
            name = Name.identifier("_bufferedTraceFileSink")
            type = rawSinkClass.defaultType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(irCall(bufferedFunc).apply {
                    extensionReceiver = irCall(sinkFunc).apply {
                        dispatchReceiver = irCall(systemFileSystem.owner.getter!!)
                        putValueArgument(
                            0,
                            irCall(pathConstructionFunc).apply {
                                putValueArgument(0, irGetField(null, bufferedTraceFileName))
                            })
                    }
                })
            }
        }
        firstFile.declarations.add(bufferedTraceFileSink)
        bufferedTraceFileSink.parent = firstFile

        val bufferedSymbolsFileName = pluginContext.irFactory.buildField {
            name = Name.identifier("_bufferedSymbolsFileName")
            type = pluginContext.irBuiltIns.stringType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(
                    irConcat().apply {
                        addArgument(irString("./symbols_${pluginContext.platform!!.presentableDescription}_"))
                        addArgument(irGetField(null, randomNumber))
                        addArgument(irString(".txt"))
                    })
            }
        }
        firstFile.declarations.add(bufferedSymbolsFileName)
        bufferedSymbolsFileName.parent = firstFile


        val bufferedSymbolsFileSink = pluginContext.irFactory.buildField {
            name = Name.identifier("_bufferedSymbolsFileSink")
            type = rawSinkClass.defaultType
            isFinal = false
            isStatic = true
        }.apply {
            this.initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(irCall(bufferedFunc).apply {
                    extensionReceiver = irCall(sinkFunc).apply {
                        dispatchReceiver = irCall(systemFileSystem.owner.getter!!)
                        putValueArgument(
                            0,
                            irCall(pathConstructionFunc).apply {
                                putValueArgument(0, irGetField(null, bufferedSymbolsFileName))
                            })
                    }
                })
            }
        }
        firstFile.declarations.add(bufferedSymbolsFileSink)
        bufferedSymbolsFileSink.parent = firstFile

        val methodMap = mutableMapOf<String, IrFunction>()
        val methodIdMap = mutableMapOf<String, Int>()
        var currMethodId = 0

        fun buildEnterMethodFunction(): IrFunction {
            val timeSourceMonotonicClass: IrClassSymbol =
                pluginContext.referenceClass(ClassId.fromString("kotlin/time/TimeSource.Monotonic"))!!

            val funMarkNow =
                pluginContext.referenceFunctions(
                    CallableId(
                        FqName("kotlin.time"),
                        FqName("TimeSource.Monotonic"),
                        Name.identifier("markNow")
                    )
                ).single()


            return pluginContext.irFactory.buildFun {
                name = Name.identifier("_enter_method")
                returnType = timeMarkClass.defaultType
            }.apply {
                addValueParameter {
                    name = Name.identifier("methodId")
                    type = pluginContext.irBuiltIns.intType
                }

                body = DeclarationIrBuilder(
                    pluginContext,
                    symbol,
                    startOffset,
                    endOffset
                ).irBlockBody {
                    if (STRINGBUILDER_MODE) {
                        +irCall(stringBuilderAppendStringFunc).apply {
                            dispatchReceiver = irGetField(null, stringBuilder)
                            putValueArgument(0, irString(">;"))
                        }
                        +irCall(stringBuilderAppendIntFunc).apply {
                            dispatchReceiver = irGetField(null, stringBuilder)
                            putValueArgument(0, irGet(valueParameters[0]))
                        }
                        +irCall(stringBuilderAppendStringFunc).apply {
                            dispatchReceiver = irGetField(null, stringBuilder)
                            putValueArgument(0, irString("\n"))
                        }
                    } else {
                        +irCall(writeStringFunc).apply {
                            extensionReceiver = irGetField(null, bufferedTraceFileSink)
                            putValueArgument(0, irConcat().apply {
                                addArgument(irString(">;"))
                                addArgument(irGet(valueParameters[0]))
                                addArgument(irString("\n"))
                            })
                        }
                    }
                    +irReturn(irCall(funMarkNow).also { call ->
                        call.dispatchReceiver = irGetObject(timeSourceMonotonicClass)
                    })
                }
            }
        }

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
                    val elapsedDuration = irTemporary(irCall(funElapsedNow).apply {
                        dispatchReceiver = irGet(valueParameters[1])
                    })
                    val elapsedMicrosProp: IrProperty =
                        elapsedDuration.type.getClass()!!.properties.single { it.name.asString() == "inWholeMicroseconds" }

                    val elapsedMicros = irTemporary(irCall(elapsedMicrosProp.getter!!).apply {
                        dispatchReceiver = irGet(elapsedDuration)
                    })

                    if (STRINGBUILDER_MODE) {
                        +irCall(stringBuilderAppendStringFunc).apply {
                            dispatchReceiver = irGetField(null, stringBuilder)
                            putValueArgument(0, irString("<;"))
                        }
                        +irCall(stringBuilderAppendIntFunc).apply {
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
                    } else {
                        +irCall(writeStringFunc).apply {
                            extensionReceiver = irGetField(null, bufferedTraceFileSink)
                            putValueArgument(0, irConcat().apply {
                                addArgument(irString("<;"))
                                addArgument(irGet(valueParameters[0]))
                                addArgument(irString(";"))
                                addArgument(irGet(elapsedMicros))
                                addArgument(irString("\n"))
                            })
                        }
                    }
                }
            }
        }

        val enterFunc = buildEnterMethodFunction()
        firstFile.declarations.add(enterFunc)
        enterFunc.parent = firstFile

        val exitFunc = buildExitMethodFunction()
        firstFile.declarations.add(exitFunc)
        exitFunc.parent = firstFile

        fun buildMainFinally(func: IrFunction): IrContainerExpression {
            return DeclarationIrBuilder(pluginContext, func.symbol).irBlock {
                +irCall(printlnFunc).apply {
                    putValueArgument(0, irGetField(null, bufferedTraceFileName))
                }
                if (STRINGBUILDER_MODE) {
                    +irCall(writeStringFunc).apply {
                        extensionReceiver = irGetField(null, bufferedTraceFileSink)
                        putValueArgument(0, irCall(toStringFunc).apply {
                            extensionReceiver = irGetField(null, stringBuilder)
                        })
                    }
                }
                +irCall(flushFunc).apply {
                    dispatchReceiver = irGetField(null, bufferedTraceFileSink)
                }
                +irCall(printlnFunc).apply {
                    putValueArgument(
                        0, irGetField(null, bufferedSymbolsFileName)
                    )
                }
                +irCall(writeStringFunc).apply {
                    extensionReceiver = irGetField(null, bufferedSymbolsFileSink)
                    putValueArgument(0, irString("{ " + methodIdMap.map { (name, id) -> id to name }
                        .sortedBy { (id, _) -> id }
                        .joinToString(",\n") { (id, name) -> "\"$id\": \"$name\"" } + " }"))
                }
                +irCall(flushFunc).apply {
                    dispatchReceiver = irGetField(null, bufferedSymbolsFileSink)
                }
            }
        }

        fun buildBodyWithMeasureCode(func: IrFunction): IrBody {
            fun IrBlockBodyBuilder.irCallEnterFunc(enterFunc: IrFunction, from: IrFunction) =
                irCall(enterFunc).apply {
                    putValueArgument(
                        0,
                        methodIdMap[from.kotlinFqName.asString()]!!.toIrConst(pluginContext.irBuiltIns.intType)
                    )
                }

            fun IrBlockBuilder.irCallExitFunc(
                exitFunc: IrFunction,
                from: IrFunction,
                startTime: IrVariable            ) = irCall(exitFunc).apply {
                putValueArgument(
                    0,
                    methodIdMap[from.kotlinFqName.asString()]!!.toIrConst(pluginContext.irBuiltIns.intType)
                )
                putValueArgument(1, irGet(startTime))
            }

            println("Wrapping body of ${func.name} (origin: ${func.origin})")
            return DeclarationIrBuilder(pluginContext, func.symbol).irBlockBody {
                val startTime = irTemporary(irCallEnterFunc(enterFunc, func))

                val tryBlock: IrExpression = irBlock(resultType = func.returnType) {
                    for (statement in func.body?.statements ?: listOf()) {
                        +(statement.transform(object : IrElementTransformerVoidWithContext() {
                            override fun visitReturn(expression: IrReturn): IrExpression {
                                if (expression.returnTargetSymbol == func.symbol) {
                                    return DeclarationIrBuilder(pluginContext, func.symbol).irBlock {
                                        val returnExpression = irTemporary(expression.value)
                                        +irCallExitFunc(exitFunc, func, startTime)                                        +expression.apply {
                                            value = irGet(returnExpression)
                                        }
                                    }


                                }
                                return super.visitReturn(expression) as IrReturn
                            }
                        }, null) as IrStatement)
                    }
                    if (func.returnType == pluginContext.irBuiltIns.unitType) {
                        +irCallExitFunc(exitFunc, func, startTime)
                    }
                }

                val catchVar = buildVariable(
                    scope.getLocalDeclarationParent(),
                    startOffset,
                    endOffset,
                    IrDeclarationOrigin.CATCH_PARAMETER,
                    Name.identifier("t"),
                    pluginContext.irBuiltIns.throwableType
                )
                +irTry(
                    tryBlock.type,
                    tryBlock,
                    listOf(
                        irCatch(catchVar,
                            irBlock {
                                +irCallExitFunc(exitFunc, func, startTime)                                +irThrow(irGet(catchVar))
                            })
                    ),
                    if (func.name.asString() == "main") buildMainFinally(func)
                    else null
                )
            }
        }


        moduleFragment.files.forEach { file ->
            file.transform(object : IrElementTransformerVoidWithContext() {
                override fun visitFunctionNew(declaration: IrFunction): IrStatement {
                    methodMap[declaration.kotlinFqName.asString()] = declaration
                    methodIdMap[declaration.kotlinFqName.asString()] = currMethodId++
                    return super.visitFunctionNew(declaration)
                }
            }, null)
        }

        moduleFragment.files.forEach { file ->
            file.transform(object : IrElementTransformerVoidWithContext() {
                override fun visitFunctionNew(declaration: IrFunction): IrStatement {
                    val body = declaration.body
                    if (declaration.name.asString() == "_enter_method" ||
                        declaration.name.asString() == "_exit_method" ||
                        body == null ||
                        declaration.origin == ADAPTER_FOR_CALLABLE_REFERENCE ||
                        declaration.fqNameWhenAvailable?.asString()?.contains("<init>") != false ||
                        declaration.fqNameWhenAvailable?.asString()?.contains("<anonymous>") != false
                    ) {
                        println("Do not wrap body of ${declaration.name} (${declaration.fqNameWhenAvailable?.asString()}):\n${declaration.dump()}")
                        return declaration
                    }
                    declaration.body = buildBodyWithMeasureCode(declaration)

                    return super.visitFunctionNew(declaration)
                }
            }, null)
            println(file.name)
            println(file.dump())
        }
    }
}