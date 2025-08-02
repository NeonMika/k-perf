package at.ssw.compilerplugin

import at.ssw.compilerplugin.ExampleConfigurationKeys.KEY_ENABLED
import at.ssw.compilerplugin.ExampleConfigurationKeys.LOG_ANNOTATION_KEY
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin.Companion.ADAPTER_FOR_CALLABLE_REFERENCE
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.presentableDescription
import java.io.File
import kotlin.collections.set
import kotlin.time.ExperimentalTime
import at.ssw.helpers.*

object ExampleConfigurationKeys {
    val KEY_ENABLED: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("enabled")
    val LOG_ANNOTATION_KEY: CompilerConfigurationKey<MutableList<String>> =
        CompilerConfigurationKey.create("measure annotation")
}

/*
Commandline processor to process options.
This is the entry point for the compiler plugin.
It is found via a ServiceLoader.
Thus, we need an entry in META-INF/services/org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
that reads at.ssw.compilerplugin.KPerfMeasureCommandLineProcessor
 */
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

/*
Registrar to register all registrars.
It is found via a ServiceLoader.
Thus, we need an entry in META-INF/services/org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
that reads at.ssw.compilerplugin.PerfMeasureComponentRegistrar
 */
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

        // org.jetbrains.kotlin.cli.common.CLIConfigurationKeys contains default configuration keys
        val messageCollector = configuration.get(CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY)!!

        /*
        println(":) :) :)")
        messageCollector.report(
            CompilerMessageSeverity.INFO,
            "CLIConfigurationKeys.ALLOW_KOTLIN_PACKAGE - ${CLIConfigurationKeys.ALLOW_KOTLIN_PACKAGE} - ${
                configuration.get(CLIConfigurationKeys.ALLOW_KOTLIN_PACKAGE)
            }"
        )
        messageCollector.report(
            CompilerMessageSeverity.INFO,
            "CLIConfigurationKeys.CONTENT_ROOTS - ${CLIConfigurationKeys.CONTENT_ROOTS} - ${
                configuration.get(CLIConfigurationKeys.CONTENT_ROOTS)
            }"
        )
        messageCollector.report(
            CompilerMessageSeverity.INFO,
            "CLIConfigurationKeys.FLEXIBLE_PHASE_CONFIG - ${CLIConfigurationKeys.FLEXIBLE_PHASE_CONFIG} - ${
                configuration.get(CLIConfigurationKeys.FLEXIBLE_PHASE_CONFIG)
            }"
        )
        messageCollector.report(
            CompilerMessageSeverity.INFO,
            "CLIConfigurationKeys.INTELLIJ_PLUGIN_ROOT - ${CLIConfigurationKeys.INTELLIJ_PLUGIN_ROOT} - ${
                configuration.get(CLIConfigurationKeys.INTELLIJ_PLUGIN_ROOT)
            }"
        )
        messageCollector.report(
            CompilerMessageSeverity.INFO,
            "CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY - ${CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY} - ${
                configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY)
            }"
        )
        messageCollector.report(
            CompilerMessageSeverity.INFO,
            "CLIConfigurationKeys.METADATA_DESTINATION_DIRECTORY - ${CLIConfigurationKeys.METADATA_DESTINATION_DIRECTORY} - ${
                configuration.get(CLIConfigurationKeys.METADATA_DESTINATION_DIRECTORY)
            }"
        )
        messageCollector.report(
            CompilerMessageSeverity.INFO,
            "CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY - ${CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY} - ${
                configuration.get(CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY)
            }"
        )
        messageCollector.report(
            CompilerMessageSeverity.INFO,
            "CLIConfigurationKeys.PATH_TO_KOTLIN_COMPILER_JAR - ${CLIConfigurationKeys.PATH_TO_KOTLIN_COMPILER_JAR} - ${
                configuration.get(CLIConfigurationKeys.PATH_TO_KOTLIN_COMPILER_JAR)
            }"
        )
        messageCollector.report(
            CompilerMessageSeverity.INFO,
            "CLIConfigurationKeys.PERF_MANAGER - ${CLIConfigurationKeys.PERF_MANAGER} - ${
                configuration.get(CLIConfigurationKeys.PERF_MANAGER)
            }"
        )
        messageCollector.report(
            CompilerMessageSeverity.INFO,
            "CLIConfigurationKeys.RENDER_DIAGNOSTIC_INTERNAL_NAME - ${CLIConfigurationKeys.RENDER_DIAGNOSTIC_INTERNAL_NAME} - ${
                configuration.get(CLIConfigurationKeys.RENDER_DIAGNOSTIC_INTERNAL_NAME)
            }"
        )
        messageCollector.report(
            CompilerMessageSeverity.INFO,
            "CLIConfigurationKeys.PHASE_CONFIG - ${CLIConfigurationKeys.PHASE_CONFIG} - ${
                configuration.get(CLIConfigurationKeys.PHASE_CONFIG)
            }"
        )
        */

        // Frontend plugin registrar
        /*
        FirExtensionRegistrarAdapter.registerExtension(
            PerfMeasureExtensionRegistrar(
                configuration[LOG_ANNOTATION_KEY] ?: listOf()
            )
        )
        */

        // Backend plugin
        IrGenerationExtension.registerExtension(PerfMeasureExtension2(MessageCollector.NONE))
    }
}

/*
Frontend plugin registrar
 */
/*
class PerfMeasureExtensionRegistrar(val annotations: List<String>) : FirExtensionRegistrar() {
    @OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::PerfMeasureExtension
    }
}
*/

/*
Frontend plugin
 */
/*
@ExperimentalTopLevelDeclarationsGenerationApi
class PerfMeasureExtension(
    session: FirSession
) : FirDeclarationGenerationExtension(session) {

    init {
        println("PerfMeasureExtension - init")
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(LookupPredicate.create {
            annotatedOrUnder(FqName("MyAnnotation"))
        })
    }

    override fun generateTopLevelClassLikeDeclaration(classId: ClassId): FirClassLikeSymbol<*>? {
        println("PerfMeasureExtension.generateTopLevelClassLikeDeclaration: $classId")
        return super.generateTopLevelClassLikeDeclaration(classId)
    }

    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        println("PerfMeasureExtension.generateFunctions: $callableId $context")

        println(context?.declaredScope?.classId)
        println(context?.owner)
        return super.generateFunctions(callableId, context)
    }
}
*/

/*
Backend plugin
 */
class PerfMeasureExtension2(
    private val messageCollector: MessageCollector
) : IrGenerationExtension {

    val STRINGBUILDER_MODE = false

    val debugFile = File("./DEBUG.txt")

    init {
        debugFile.delete()
    }

    fun appendToDebugFile(str: String) {
        debugFile.appendText(str)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val firstFile = moduleFragment.files[0]

        val timeMarkClass: IrClassSymbol =
            pluginContext.referenceClass(ClassId.fromString("kotlin/time/TimeMark"))!!

        val stringBuilderClassId = ClassId.fromString("kotlin/text/StringBuilder")
        // In JVM, StringBuilder is a type alias (see https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/)
        val stringBuilderTypeAlias = pluginContext.referenceTypeAlias(stringBuilderClassId)
        val stringBuilderClass = stringBuilderTypeAlias?.owner?.expandedType?.classOrFail
            ?: pluginContext.referenceClass(stringBuilderClassId)!! // In native and JS, StringBuilder is a class

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

        val rawSinkClass =
            pluginContext.referenceClass(ClassId.fromString("kotlinx/io/RawSink"))!!

        // Watch out, Path does not use constructors but functions to build
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

        //Test findProperty inside class
        val sizeProperty = pluginContext.referenceProperties(
            CallableId(
                FqName("kotlin.collections"),
                FqName("ArrayList"),
                Name.identifier("size")
            )
        ).single()


        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail
        val sinkFunc = systemFileSystemClass.functions.single { it.owner.name.asString() == "sink" }

        val bufferedFuncs = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                Name.identifier("buffered")
            )
        )
        val bufferedFunc = bufferedFuncs.single { it.owner.extensionReceiverParameter!!.type == sinkFunc.owner.returnType }

        /*appendToDebugFile("Different versions of kotlinx.io.writeString:\n")
        appendToDebugFile(
            pluginContext.referenceFunctions(
                CallableId(
                    FqName("kotlinx.io"),
                    Name.identifier("writeString")
                )
            ).joinToString("\n") { func ->
                "kotlinx.io.writeString(${func.owner.valueParameters.joinToString(",") { param -> param.type.classFqName.toString() }})"
            }
        )*/
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

        //debugFile.appendText("2")
        val toStringFunc = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlin"),
                Name.identifier("toString")
            )
        ).single()

        //debugFile.appendText("3")

        val stringBuilder: IrField = pluginContext.irFactory.buildField {
            name = Name.identifier("_stringBuilder")
            type = stringBuilderClass.defaultType
            isFinal = false
            isStatic = true
        }.apply {
            //val sb = ....
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
                        //use kotlinx.datetime.Clock.System.now()
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
            // _bufferedTraceFileSink = SystemFileSystem.sink(Path(bufferedTraceFileName)).buffered()
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
                        //use kotlinx.datetime.Clock.System.now()
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
        moduleFragment.files.forEach { file ->
            file.transform(object : IrElementTransformerVoidWithContext() {
                override fun visitFunctionNew(declaration: IrFunction): IrStatement {
                    methodMap[declaration.kotlinFqName.asString()] = declaration
                    methodIdMap[declaration.kotlinFqName.asString()] = currMethodId++
                    // do not transform at all
                    // we just use a transformer because it correctly descends recursively
                    return super.visitFunctionNew(declaration)
                }
            }, null)
        }

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

        /* val funMarkNowViaClass = classMonotonic.functions.find { it.owner.name.asString() == "markNow" }!! */
        // assertion: funMarkNowViaClass == funMarkNow
        var enterMethod: IrSimpleFunction? = null

        fun buildEnterFunction(): IrFunction {
            enterMethod = pluginContext.irFactory.buildFun {
                name = Name.identifier("_enter_method")
                returnType = timeMarkClass.defaultType
            }.apply {
                addValueParameter {
                    /*
                name = Name.identifier("method")
                type = pluginContext.irBuiltIns.stringType
                */
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
            return enterMethod!!
        }

        val enterFunc = buildEnterFunction()
        firstFile.declarations.add(enterFunc)
        enterFunc.parent = firstFile

        val funElapsedNow =
            pluginContext.referenceFunctions(
                CallableId(
                    FqName("kotlin.time"),
                    FqName("TimeMark"),
                    Name.identifier("elapsedNow")
                )
            ).single()

        var exitFunctionSymbol: IrSimpleFunctionSymbol? = null
        var exitFunctionStart: Int? = null
        var exitFunctionEnd: Int? = null
        var exitParam0: IrValueParameter? = null
        var exitParam1: IrValueParameter? = null
        var exitBody: IrBlockBody? = null

        fun buildGeneralExitFunction(): IrFunction {
            return pluginContext.irFactory.buildFun {
                name = Name.identifier("_exit_method")
                returnType = pluginContext.irBuiltIns.unitType
            }.apply {
                addValueParameter {
                    /*
                    name = Name.identifier("method")
                    type = pluginContext.irBuiltIns.stringType */
                    name = Name.identifier("methodId")
                    type = pluginContext.irBuiltIns.intType
                }
                addValueParameter {
                    name = Name.identifier("startTime")
                    type = timeMarkClass.defaultType
                } /*
                addValueParameter {
                    name = Name.identifier("result")
                    type = pluginContext.irBuiltIns.anyNType
                } */
                exitFunctionSymbol = symbol
                exitFunctionStart = startOffset
                exitFunctionEnd = endOffset
                exitParam0 = valueParameters[0]
                exitParam1 = valueParameters[1]

                val oldBody = DeclarationIrBuilder(pluginContext, symbol, startOffset, endOffset).irBlockBody {
                    // Duration
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
                exitBody = oldBody
                body = oldBody

            }
        }

        val exitFunc = buildGeneralExitFunction()
        firstFile.declarations.add(exitFunc)
        exitFunc.parent = firstFile

        fun buildMainExitFunction(): IrSimpleFunction {
            fun IrBlockBodyBuilder.flushTraceFile() {
                +irCall(flushFunc).apply {
                    dispatchReceiver = irGetField(null, bufferedTraceFileSink)
                }
            }

            fun IrBlockBodyBuilder.writeAndFlushSymbolsFile() {
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

            fun IrBlockBodyBuilder.printFileNamesToStdout() {
                +irCall(printlnFunc).apply {
                    putValueArgument(0, irGetField(null, bufferedTraceFileName))
                }
                +irCall(printlnFunc).apply {
                    putValueArgument(
                        0, irGetField(null, bufferedSymbolsFileName)
                    )
                }
            }

            return pluginContext.irFactory.buildFun {
                name = Name.identifier("_exit_main")
                returnType = pluginContext.irBuiltIns.unitType
            }.apply {
                addValueParameter {
                    name = Name.identifier("startTime")
                    type = timeMarkClass.defaultType
                } /*
                addValueParameter {
                    name = Name.identifier("result")
                    type = pluginContext.irBuiltIns.anyNType
                } */

                body = DeclarationIrBuilder(pluginContext, symbol, startOffset, endOffset).irBlockBody {
                    flushTraceFile()
                    val mainMethodId = methodIdMap.entries.find { it.key.endsWith("main") }?.value
                        ?: throw IllegalStateException("main method not found")
                    +irCall(exitFunc).apply {
                        putValueArgument(0, mainMethodId.toIrConst(pluginContext.irBuiltIns.intType))
                        putValueArgument(1, irGet(valueParameters[0]))
                    }

                    if (STRINGBUILDER_MODE) {
                        +irCall(writeStringFunc).apply {
                            extensionReceiver = irGetField(null, bufferedTraceFileSink)
                            putValueArgument(0, irCall(toStringFunc).apply {
                                extensionReceiver = irGetField(null, stringBuilder)
                            })
                        }
                    }
                    writeAndFlushSymbolsFile()

                    flushTraceFile()

                    printFileNamesToStdout()
                }
            }
        }

        val exitMainFunc = buildMainExitFunction()
        firstFile.declarations.add(exitMainFunc)
        exitMainFunc.parent = firstFile

        fun buildBodyWithMeasureCode(func: IrFunction): IrBody {

            println("# Wrapping body of ${func.name} (origin: ${func.origin})")
            return DeclarationIrBuilder(pluginContext, func.symbol).irBlockBody {
                // no +needed on irTemporary as it is automatically added to the builder
                val startTime = irTemporary(irCall(enterFunc).apply {
                    putValueArgument(
                        0,
                        methodIdMap[func.kotlinFqName.asString()]!!.toIrConst(pluginContext.irBuiltIns.intType)
                    )
                })

                val tryBlock: IrExpression = irBlock(resultType = func.returnType) {
                    for (statement in func.body?.statements ?: listOf()) +statement
                }

                +irTry(
                    tryBlock.type,
                    tryBlock,
                    listOf(),
                    if (func.name.asString() == "main") irCall(exitMainFunc).apply {
                        putValueArgument(0, irGet(startTime))
                    } else irCall(exitFunc).apply {
                        putValueArgument(
                            0,
                            methodIdMap[func.kotlinFqName.asString()]!!.toIrConst(pluginContext.irBuiltIns.intType)
                        )
                        putValueArgument(1, irGet(startTime))
                    }
                )
            }
        }

        main_tests(pluginContext, timeMarkClass,
            stringBuilderClass, stringBuilderConstructor, stringBuilderAppendIntFunc,
            stringBuilderAppendLongFunc, stringBuilderAppendStringFunc, printlnFunc,
            rawSinkClass, pathConstructionFunc, systemFileSystem, sizeProperty, firstFile,
            stringBuilder, sinkFunc, bufferedFunc, writeStringFunc, flushFunc, toStringFunc,
            randomDefaultObjectClass, nextIntFunc, randomNumber, bufferedTraceFileName,
            bufferedTraceFileSink, bufferedSymbolsFileName, bufferedSymbolsFileSink,
            timeSourceMonotonicClass, funMarkNow, enterMethod!!, funElapsedNow, exitFunctionSymbol!!,
            exitFunctionStart!!, exitFunctionEnd!!,  exitParam0!!, exitParam1!!, exitBody!!)

        // IrElementVisitor / IrElementVisitorVoid
        // IrElementTransformer / IrElementTransformerVoid / IrElementTransformerVoidWithContext
        // IrElementTransformerVoidWithContext().visitfile(file, null)

        moduleFragment.files.forEach { file ->
            file.transform(object : IrElementTransformerVoidWithContext() {
                override fun visitFunctionNew(declaration: IrFunction): IrStatement {
                    val body = declaration.body
                    if (declaration.name.asString() == "_enter_method" ||
                        declaration.name.asString() == "_exit_method" ||
                        declaration.name.asString() == "_exit_main" ||
                        body == null ||
                        declaration.origin == ADAPTER_FOR_CALLABLE_REFERENCE ||
                        declaration.fqNameWhenAvailable?.asString()?.contains("<init>") != false ||
                        declaration.fqNameWhenAvailable?.asString()?.contains("<anonymous>") != false
                    ) {
                        // do not further transform this method, e.g., its statements are not transformed
                        println("# Do not wrap body of ${declaration.name} (${declaration.fqNameWhenAvailable?.asString()}):\n${declaration.dump()}")
                        return declaration
                    }
                    declaration.body = buildBodyWithMeasureCode(declaration)

                    return super.visitFunctionNew(declaration)
                }
            }, null)
            println("---${file.name}---")
            println(file.dump())
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //test functions
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun main_tests(pluginContext: IrPluginContext, timeMarkClass: IrClassSymbol, stringBuilderClass: IrClassSymbol, stringBuilderConstructor: IrConstructorSymbol, stringBuilderAppendIntFunc: IrSimpleFunctionSymbol, stringBuilderAppendLongFunc: IrSimpleFunctionSymbol, stringBuilderAppendStringFunc: IrSimpleFunctionSymbol, printlnFunc: IrSimpleFunctionSymbol, rawSinkClass: IrClassSymbol, pathConstructionFunc: IrSimpleFunctionSymbol, systemFileSystem: IrPropertySymbol, sizeProperty: IrPropertySymbol,
                           firstFile: IrFile, stringBuilder: IrField, sinkFunc: IrSimpleFunctionSymbol, bufferedFunc: IrSimpleFunctionSymbol, writeStringFunc: IrSimpleFunctionSymbol, flushFunc: IrSimpleFunctionSymbol, toStringFunc: IrSimpleFunctionSymbol, randomDefaultObjectClass: IrClassSymbol, nextIntFunc: IrSimpleFunctionSymbol, randomNumber: IrField, bufferedTraceFileName: IrField, bufferedTraceFileSink: IrField, bufferedSymbolsFileName: IrField, bufferedSymbolsFileSink: IrField,
                           timeSourceMonotonicClass: IrClassSymbol, funMarkNow: IrSimpleFunctionSymbol, enterMethod: IrSimpleFunction, funElapsedNow: IrSimpleFunctionSymbol, symbol: IrSimpleFunctionSymbol, startOffset: Int, endOffset: Int, valueParam0: IrValueParameter, valueParam1: IrValueParameter, oldBody: IrBlockBody) {

        val timeMarkClassNew = pluginContext.findClassOrNull("kotlin/time/TimeMark")
        compareClassSymbols(timeMarkClass, timeMarkClassNew)

        val stringBuilderClassNew = pluginContext.findClassOrNull("kotlin/text/StringBuilder")
        compareClassSymbols(stringBuilderClass, stringBuilderClassNew)

        val stringClass = pluginContext.findClassOrNull("string")
        val stringEqualsFunc = stringClass?.findFunctionOrNull(pluginContext, "equals()")

        //basic constructor test
        val stringBuilderConstructorNew = stringBuilderClassNew?.findConstructorOrNull(pluginContext)
        compareConstructorSymbols(stringBuilderConstructor, stringBuilderConstructorNew)

        //multiple parameter constructor test with java class
        val fileClassNew = pluginContext.findClassOrNull("java/io/File")
        val fileConstructor = fileClassNew?.constructors?.singleOrNull() { it.owner.valueParameters.size == 2 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.stringType.makeNullable() && it.owner.valueParameters[1].type == pluginContext.irBuiltIns.stringType.makeNullable() }
        val fileConstructorNew = fileClassNew?.findConstructorOrNull(pluginContext, "(String?, String?)")
        compareConstructorSymbols(fileConstructor!!, fileConstructorNew)

        //single parameter test with kotlin class
        val regexClass = pluginContext.findClass("kotlin/text/Regex")
        val regexConstructor = regexClass.constructors.singleOrNull { it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.stringType }
        val regexConstructorNew = regexClass.findConstructorOrNull(pluginContext, "(String)")
        compareConstructorSymbols(regexConstructor!!, regexConstructorNew)

        //subclass constructor test
        val defaultClass = pluginContext.findClassOrNull("kotlin/random/Random.Default")
        val defaultClassConstructor = defaultClass?.findConstructorOrNull(pluginContext)

        val defaultClassConstructorDirect = pluginContext.findConstructorOrNull("kotlin/random/Random.Default()")
        compareConstructorSymbols(defaultClassConstructor!!, defaultClassConstructorDirect)

        val findFunctionDefaultTestWithout = pluginContext.findFunctionOrNull("kotlin/collections/joinToString(*)", pluginContext.getIrType("bytearray"))
        val findFunctionDefaultTestWith1 = pluginContext.findFunctionOrNull("kotlin/collections/joinToString(charsequence, *)", pluginContext.getIrType("bytearray"))
        compareFunctionSymbols(findFunctionDefaultTestWithout!!, findFunctionDefaultTestWith1)

        //non existin constructor test
        val nonExistentConstructorNew = stringBuilderClassNew?.findConstructorOrNull(pluginContext, "(Boolean, String)")
        appendToDebugFile("NonExistingTest for constructor (should be null): $nonExistentConstructorNew \n\n")

        val stringBuilderAppendIntFuncNew = stringBuilderClassNew?.findFunctionOrNull(pluginContext, "append(int)")
        compareFunctionSymbols(stringBuilderAppendIntFunc, stringBuilderAppendIntFuncNew, true)

        val stringBuilderAppendLongFuncNew = stringBuilderClassNew?.findFunctionOrNull(pluginContext, "append(long)")
        compareFunctionSymbols(stringBuilderAppendLongFunc, stringBuilderAppendLongFuncNew, true)

        val stringBuilderAppendStringFuncNew = stringBuilderClassNew?.findFunctionOrNull(pluginContext, "append(string?)")
        compareFunctionSymbols(stringBuilderAppendStringFunc, stringBuilderAppendStringFuncNew, true)

        val printlnFuncNew = pluginContext.findFunctionOrNull("kotlin/io/println(any?)")
        compareFunctionSymbols(printlnFunc, printlnFuncNew)

        //negative example for function:
        val nonExistingFunc = pluginContext.findFunctionOrNull("kotlin/io/blabliblup()")
        appendToDebugFile("NonExistingTest for function: $nonExistingFunc \n\n")

        //no parenthesis test for function (this should fail):
        runCatching {
            pluginContext.findFunctionOrNull("kotlin/io/println")
        }.onFailure { _ ->
            appendToDebugFile("NoParenthesisTest for function failed successfully \n\n")
        }.onSuccess {
            appendToDebugFile("ERROR: NoParenthesisTest for function did not fail! \n\n")
        }

        //only package test for function (this should fail):
        runCatching {
            pluginContext.findFunctionOrNull("kotlin/io/")
        }.onFailure { _ ->
            appendToDebugFile("onlyPackageTest for function failed successfully \n\n")
        }.onSuccess {
            appendToDebugFile("ERROR: onlyPackageTest for function did not fail! \n\n")
        }

        //negative example for class:
        val nonExistingClass = pluginContext.findClassOrNull("kotlin/io/Blabliblup")
        appendToDebugFile("NonExistingTest for class: $nonExistingClass \n\n")

        //only package test for class (this should fail):
        runCatching {
            pluginContext.findClassOrNull("kotlin/text/")
        }.onFailure { _ ->
            appendToDebugFile("onlyPackageTest for class failed successfully \n\n")
        }.onSuccess {
            appendToDebugFile("ERROR: onlyPackageTest for class did not fail! \n\n")
        }

        val rawSinkClassNew = pluginContext.findClassOrNull("kotlinx/io/RawSink")
        compareClassSymbols(rawSinkClass, rawSinkClassNew)
        val pathConstructionFuncNew = pluginContext.findFunctionOrNull("kotlinx/io/files/Path(string)")
        compareFunctionSymbols(pathConstructionFunc, pathConstructionFuncNew)

        //Test findProperty toplevel
        val systemFileSystemNew = pluginContext.findPropertyOrNull("kotlinx/io/files/SystemFileSystem")
        comparePropertySymbols(systemFileSystem, systemFileSystemNew)
        val sizePropertyNew = pluginContext.findPropertyOrNull("kotlin/collections/ArrayList.size")
        comparePropertySymbols(sizeProperty, sizePropertyNew)

        //Test findProperty on IrClass
        val arrayListClass = pluginContext.findClassOrNull("kotlin/collections/ArrayList")
        val sizePropertyNewClass = arrayListClass?.findPropertyOrNull("size")
        comparePropertySymbols(sizePropertyNewClass!!, sizeProperty, true)

        //Test findProperty with function call
        val functionCallTest = arrayListClass.findPropertyOrNull("size()")
        appendToDebugFile("FunctionCallTest for property (null): $functionCallTest\n\n")

        //negative example for property:
        val nonExistingProperty = pluginContext.findPropertyOrNull("kotlin/io/Blabliblup")
        appendToDebugFile("NonExistingTest for property: $nonExistingProperty \n\n")

        val sinkFuncNew = systemFileSystem.findFunctionOrNull(pluginContext, "sink(*)")
        compareFunctionSymbols(sinkFunc, sinkFuncNew)

        val bufferedFuncNew = pluginContext.findFunctionOrNull("kotlinx/io/buffered()", sinkFunc.owner.returnType)
        compareFunctionSymbols(bufferedFunc, bufferedFuncNew)

        val writeStringFuncNew = pluginContext.findFunctionOrNull("kotlinx/io/writeString(string,int,int)")
        compareFunctionSymbols(writeStringFunc, writeStringFuncNew)

        val flushFuncNew = pluginContext.findFunctionOrNull("kotlinx/io/Sink.flush()")
        compareFunctionSymbols(flushFunc, flushFuncNew)

        val toStringFuncNew = pluginContext.findFunctionOrNull("kotlin/toString()")
        compareFunctionSymbols(toStringFunc, toStringFuncNew)

        val stringBuilderNew: IrField = pluginContext.irFactory.buildField {
            name = Name.identifier("_stringBuilder2")
            type = stringBuilderClass.defaultType
            isFinal = false
            isStatic = true
        }.apply {
            this.initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).callExpression(pluginContext) { stringBuilderConstructor() }
        }
        compareFieldDumps(stringBuilder.dump(), stringBuilderNew.dump(), "stringBuilder")
        firstFile.declarations.add(stringBuilderNew)
        stringBuilderNew.parent = firstFile

        val stringBuilderNewCall: IrField = pluginContext.irFactory.buildField {
            name = Name.identifier("_stringBuilder3")
            type = stringBuilderClass.defaultType
            isFinal = false
            isStatic = true
        }.apply {
            this.initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).callExpression(pluginContext) { stringBuilderClass() }
        }
        compareFieldDumps(stringBuilder.dump(), stringBuilderNewCall.dump(), "stringBuilder")
        firstFile.declarations.add(stringBuilderNewCall)
        stringBuilderNewCall.parent = firstFile

        val stringBuilderNewCallDirect: IrField = pluginContext.irFactory.buildField {
            name = Name.identifier("_stringBuilder4")
            type = stringBuilderClass.defaultType
            isFinal = false
            isStatic = true
        }.apply {
            this.initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).callExpression(pluginContext) { call("kotlin/text/StringBuilder()") }
        }
        compareFieldDumps(stringBuilder.dump(), stringBuilderNewCallDirect.dump(), "stringBuilder")
        firstFile.declarations.add(stringBuilderNewCallDirect)
        stringBuilderNewCallDirect.parent = firstFile

        val stringBuilderSimpleField = pluginContext.createField(firstFile.symbol, "_stringBuilder5") { stringBuilderClass() }
        compareFieldDumps(stringBuilder.dump(), stringBuilderSimpleField.dump(), "stringBuilder")
        firstFile.declarations.add(stringBuilderSimpleField)
        stringBuilderSimpleField.parent = firstFile

        val randomDefaultObjectClassNew = pluginContext.findClassOrNull("kotlin/random/Random.Default")
        compareClassSymbols(randomDefaultObjectClass, randomDefaultObjectClassNew)

        val nextIntFuncNew = pluginContext.findFunctionOrNull("kotlin/random/Random.Default.nextInt()")
        compareFunctionSymbols(nextIntFunc, nextIntFuncNew)

        val randomNumber2 = pluginContext.irFactory.buildField {
            name = Name.identifier("_randNumber2")
            type = pluginContext.irBuiltIns.intType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                callExpression(pluginContext) {
                    randomDefaultObjectClass.call(nextIntFunc)
                }
            }
        }
        compareFieldDumps(randomNumber.dump(), randomNumber2.dump(), "randomNumber")
        firstFile.declarations.add(randomNumber2)
        randomNumber2.parent = firstFile

        val randomNumber3 = pluginContext.irFactory.buildField {
            name = Name.identifier("_randNumber3")
            type = pluginContext.irBuiltIns.intType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                callExpression(pluginContext) {
                    randomDefaultObjectClass.call("nextInt()")
                }
            }
        }
        compareFieldDumps(randomNumber.dump(), randomNumber3.dump(), "randomNumber")
        firstFile.declarations.add(randomNumber3)
        randomNumber3.parent = firstFile

        val randomNumber4 = pluginContext.createField(firstFile.symbol, "_randomNumber4") {randomDefaultObjectClass.call(nextIntFunc)}
        compareFieldDumps(randomNumber.dump(), randomNumber4.dump(), "randomNumber")
        firstFile.declarations.add(randomNumber4)
        randomNumber4.parent = firstFile

        val irFileHandle = IrFileWriter(pluginContext, firstFile, "./trace_${pluginContext.platform!!.presentableDescription}_${(0..10000).random()}.txt")

        val bufferedTraceFileSink2 = pluginContext.irFactory.buildField {
            name = Name.identifier("_bufferedTraceFileSink2")
            type = rawSinkClass.defaultType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                callExpression(pluginContext) {
                    systemFileSystem.call(sinkFunc, pathConstructionFunc(bufferedTraceFileName))
                        .call(bufferedFunc)
                }
            }
        }
        compareFieldDumps(bufferedTraceFileSink.dump(), bufferedTraceFileSink2.dump(), "bufferedTraceFileSink")

        firstFile.declarations.add(bufferedTraceFileSink2)
        bufferedTraceFileSink2.parent = firstFile

        val bufferedTraceFileSink3 = pluginContext.irFactory.buildField {
            name = Name.identifier("_bufferedTraceFileSink3")
            type = rawSinkClass.defaultType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                callExpression(pluginContext) {
                    systemFileSystem.call("sink(*)", pathConstructionFunc(bufferedTraceFileName))
                        .call(bufferedFunc)
                }
            }
        }
        compareFieldDumps(bufferedTraceFileSink.dump(), bufferedTraceFileSink3.dump(), "bufferedTraceFileSink")

        firstFile.declarations.add(bufferedTraceFileSink3)
        bufferedTraceFileSink3.parent = firstFile

        val bufferedTraceFileSink4 = pluginContext.createField(firstFile.symbol, "_bufferedTraceFileSink4") {
            systemFileSystem.call(sinkFunc, pathConstructionFunc(bufferedTraceFileName))
                .call(bufferedFunc)
        }
        compareFieldDumps(bufferedTraceFileSink.dump(), bufferedTraceFileSink4.dump(), "bufferedTraceFileSink")

        firstFile.declarations.add(bufferedTraceFileSink4)
        bufferedTraceFileSink4.parent = firstFile

        val bufferedSymbolsFileName2 = pluginContext.createField(firstFile.symbol, "_bufferedSymbolsFileName2") {
            irConcat("./symbols_${pluginContext.platform!!.presentableDescription}_", randomNumber, ".txt")
        }
        firstFile.declarations.add(bufferedSymbolsFileName2)
        bufferedSymbolsFileName2.parent = firstFile
        compareFieldDumps(bufferedSymbolsFileName.dump(), bufferedSymbolsFileName2.dump(), "bufferedSymbolsFileName")

        val bufferedSymbolsFileSink2 = pluginContext.irFactory.buildField {
            name = Name.identifier("_bufferedSymbolsFileSink2")
            type = rawSinkClass.defaultType
            isFinal = false
            isStatic = true
        }.apply {
            this.initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).callExpression(pluginContext) {
                systemFileSystem.call(sinkFunc, pathConstructionFunc(bufferedSymbolsFileName))
                    .call("kotlinx/io/buffered()")
            }
        }
        firstFile.declarations.add(bufferedSymbolsFileSink2)
        bufferedSymbolsFileSink2.parent = firstFile
        compareFieldDumps(bufferedSymbolsFileSink.dump(), bufferedSymbolsFileSink2.dump(), "bufferedSymbolsFileSink")

        val timeSourceMonotonicClassNew = pluginContext.findClassOrNull("kotlin/time/TimeSource.Monotonic")
        compareClassSymbols(timeSourceMonotonicClass, timeSourceMonotonicClassNew)

        val funMarkNowNew = pluginContext.findFunctionOrNull("kotlin/time/TimeSource.Monotonic.markNow()")
        compareFunctionSymbols(funMarkNow, funMarkNowNew)

        val enterMethodNew = pluginContext.irFactory.buildFun {
            name = Name.identifier("_enter_method_new")
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
            ).irBlockBody{
                enableCallDSL(pluginContext) {
                    if (STRINGBUILDER_MODE) {
                        +stringBuilderClass.call(stringBuilderAppendStringFunc, ">;")
                        +stringBuilderClass.call(stringBuilderAppendIntFunc, valueParameters[0])
                        +stringBuilderClass.call(stringBuilderAppendStringFunc, "\n")
                    } else {
                        +bufferedTraceFileSink.call(writeStringFunc, irConcat(">;", valueParameters[0], "\n"))
                    }
                    +irReturn(timeSourceMonotonicClass.call(funMarkNow))
                }
            }
        }
        compareFieldDumps(enterMethod.dump(), enterMethodNew.dump(), "_enter_method")

        val funElapsedNowNew = pluginContext.findFunctionOrNull("kotlin/time/TimeMark.elapsedNow()")
        compareFunctionSymbols(funElapsedNow, funElapsedNowNew)

        val newBody = DeclarationIrBuilder(pluginContext, symbol, startOffset, endOffset).irBlockBody {
            enableCallDSL(pluginContext) {
                val elapsedDuration = irTemporary(valueParam1.call(funElapsedNow))
                val elapsedMicrosProp = elapsedDuration.findProperty("inWholeMicroseconds")
                val elapsedMicros = irTemporary(elapsedDuration.call(elapsedMicrosProp))

                if (STRINGBUILDER_MODE) {
                    +stringBuilder.call(stringBuilderAppendStringFunc, "<;")
                    +stringBuilder.call(stringBuilderAppendIntFunc, valueParam0)
                    +stringBuilder.call(stringBuilderAppendStringFunc, ";")
                    +stringBuilder.call(stringBuilderAppendLongFunc, elapsedMicros)
                    +stringBuilder.call(stringBuilderAppendStringFunc, "\n")
                } else {
                    +bufferedTraceFileSink.call(writeStringFunc, irConcat("<;", valueParam0, ";", elapsedMicros, "\n"))
                }
            }
        }
        compareFieldDumps(oldBody.dump(), newBody.dump(), "testBodyWithTempVar")

        val printLnIntCall = DeclarationIrBuilder(pluginContext, firstFile.symbol).getCall(pluginContext) {
            callPrintLn(pluginContext, 42)
        }
        appendToDebugFile("printLn with Number: " + printLnIntCall + "\n\n")

        val atomicIntegerClass = pluginContext.findClass("java/util/concurrent/atomic/AtomicInteger")
        val constructor = atomicIntegerClass.findConstructor(pluginContext)
        val counterField = pluginContext.createField(firstFile.symbol, "_globalCounter") { constructor() }
        firstFile.declarations.add(counterField)
        counterField.parent = firstFile
        val printLnFieldCall = DeclarationIrBuilder(pluginContext, firstFile.symbol).getCall(pluginContext) {
            callPrintLn(pluginContext, counterField)
        }
        appendToDebugFile("printLn with Field: " + printLnFieldCall + "\n\n")

        //myClass tests
        val myClass = pluginContext.findClassOrNull("test/MyClass")

        if(myClass != null) {
            appendToDebugFile("myClass found\n")
            // Test for a generic function with one type parameter
            val genericFunction = myClass.findFunctionOrNull(pluginContext, "genericFunction(*)")
            if (genericFunction != null) {
                appendToDebugFile("genericFunction found\n")
            } else {
                appendToDebugFile("genericFunction not found\n")
            }

            // Test for a generic function with a different type parameter
            val anotherGenericFunction = myClass.findFunctionOrNull(pluginContext, "anotherGenericFunction(G)")
            if (anotherGenericFunction != null) {
                appendToDebugFile("anotherGenericFunction found\n")
            } else {
                appendToDebugFile("anotherGenericFunction not found\n")
            }

            // Test for a static function in the companion object
            val staticFunction = myClass.findFunctionOrNull(pluginContext, "staticFunction()")
            if (staticFunction != null) {
                appendToDebugFile("staticFunction found\n")
            } else {
                appendToDebugFile("staticFunction not found\n")
            }

            // Test a normal function in a class with similar other function
            val normalFunction = myClass.findFunctionOrNull(pluginContext, "normalFunction(int)")
            if (normalFunction != null) {
                appendToDebugFile("normalFunction found\n")
            } else {
                appendToDebugFile("normalFunction not found\n")
            }

            // Test a normal function in a class with all default parameters given
            val defaultParamFunction = myClass.findFunctionOrNull(pluginContext, "normalFunction(Int, String)", ignoreNullability = true)
            if (defaultParamFunction != null) {
                appendToDebugFile("Function with default params found\n")
            } else {
                appendToDebugFile("Function with default params not found\n")
            }

            // Test for a top-level function with a primitive type parameter
            val topLevelFunction = pluginContext.findFunctionOrNull("test/topLevelFunction(Int)")
            if (topLevelFunction != null) {
                appendToDebugFile("topLevelFunction found\n")
            } else {
                appendToDebugFile("topLevelFunction not found\n")
            }

            val testString = pluginContext.createField(firstFile.symbol, "_testString") { convertArgToIrExpression("test") }
            firstFile.declarations.add(testString)
            testString.parent = firstFile

            val pairCall = DeclarationIrBuilder(pluginContext, firstFile.symbol).getCall(pluginContext) {
                Pair(myClass, testString.symbol).call("foo(int)", 42)
            }
            appendToDebugFile(pairCall.dump())
        }
    }

    //helper functions

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun compareFunctionSymbols(original: IrSimpleFunctionSymbol, new: IrSimpleFunctionSymbol?, classCall: Boolean = false) {
        if (classCall) appendToDebugFile("IrClassSymbol.findFunction call:\n")

        if (new == null) {
            appendToDebugFile("New method returned null for ${original.owner.name}\n\n")
            return
        }

        val matches = original == new
        appendToDebugFile("Function ${original.owner.name}: ${if (matches) "MATCH" else "MISMATCH"}\n")
        if (!matches) {
            appendToDebugFile("  Original: ${original.owner.kotlinFqName}\n")
            appendToDebugFile("  New: ${new.owner.kotlinFqName}\n")
        }
        appendToDebugFile("\n")
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun compareClassSymbols(original: IrClassSymbol, new: IrClassSymbol?) {
        if (new == null) {
            appendToDebugFile("New class returned null for ${original.owner.kotlinFqName}\n\n")
            return
        }

        val matches = original == new
        appendToDebugFile("Class ${original.owner.kotlinFqName}: ${if (matches) "MATCH" else "MISMATCH"}\n")
        if (!matches) {
            appendToDebugFile("  Original: ${original.owner.kotlinFqName}\n")
            appendToDebugFile("  New: ${new.owner.kotlinFqName}\n")
        }
        appendToDebugFile("\n")
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun comparePropertySymbols(original: IrPropertySymbol, new: IrPropertySymbol?, classCall: Boolean = false) {
        if (classCall) appendToDebugFile("IrClassSymbol.findProperty call:\n")

        if (new == null) {
            appendToDebugFile("New property returned null for ${original.owner.name}\n\n")
            return
        }

        val matches = original == new
        val propertyFqName = "${original.owner.parent.kotlinFqName}.${original.owner.name}"
        appendToDebugFile("Property $propertyFqName: ${if (matches) "MATCH" else "MISMATCH"}\n")
        if (!matches) {
            appendToDebugFile("  Original: $propertyFqName\n")
            appendToDebugFile("  New: ${new.owner.parent.kotlinFqName}.${new.owner.name}\n")
        }
        appendToDebugFile("\n")
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun compareConstructorSymbols(original: IrConstructorSymbol, new: IrConstructorSymbol?) {
        if (new == null) {
            appendToDebugFile("New constructor returned null for ${original.owner.parent.kotlinFqName}\n\n")
            return
        }
        val matches = original == new
        val constructorFqName = "${original.owner.parent.kotlinFqName}.${original.owner.name}"
        appendToDebugFile("Constructor $constructorFqName: ${if (matches) "MATCH" else "MISMATCH"}\n")
        if (!matches) {
            appendToDebugFile("  Original: $constructorFqName\n")
            appendToDebugFile("  New: ${new.owner.parent.kotlinFqName}.${new.owner.name}\n")
        }
        appendToDebugFile("\n")
    }

    private fun compareFieldDumps(dump1: String, dump2: String, variableName: String) {

        val normalizedDump1 = dump1.lines()
        val normalizedDump2 = dump2.lines()

        if (normalizedDump1.size != normalizedDump2.size) {
            appendToDebugFile("fields $variableName do not match\n\n")
            return
        } else {
            for (i in normalizedDump1.indices) {
                val line1 = normalizedDump1[i].trim().split(" ").filter { !it.contains(variableName) }.joinToString(" ")
                val line2 = normalizedDump1[i].trim().split(" ").filter { !it.contains(variableName) }.joinToString(" ")
                if (line1 != line2) {
                    appendToDebugFile("fields $variableName do not match\n\n")
                    return
                }
            }
            appendToDebugFile("fields $variableName match\n\n")
        }
    }
}