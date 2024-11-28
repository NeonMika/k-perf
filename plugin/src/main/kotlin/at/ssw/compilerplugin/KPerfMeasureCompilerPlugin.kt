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
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
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
    @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        println("PerfMeasureExtension2.generate")
        messageCollector.report(
            CompilerMessageSeverity.STRONG_WARNING,
            "PerfMeasureExtension2.generate"
        )

        // Wish: findClass("kotlin/time/TimeMark")
        val timeMarkClass: IrClassSymbol = pluginContext.findClass("kotlin/time/TimeMark")
        assert (timeMarkClass == (pluginContext.referenceClass(ClassId.fromString("kotlin/time/TimeMark"))!!))

        val stringBuilderClassId = ClassId.fromString("kotlin/text/StringBuilder")
        // In JVM, StringBuilder is a type alias (see https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/)
        val stringBuilderTypeAlias = pluginContext.referenceTypeAlias(stringBuilderClassId)
        val stringBuilderClass: IrClassSymbol = pluginContext.findClass("kotlin/text/StringBuilder")
        assert (stringBuilderClass == (stringBuilderTypeAlias?.owner?.expandedType?.classOrFail?: pluginContext.referenceClass(stringBuilderClassId)!!)) // In native and JS, StringBuilder is a class

        // This is how we build extension functions in Kotlin, just say <class>.<funcname>
        /*
        fun IrClassSymbol.funcCount() : Int {
            // this cannot access private entities
            return this.owner.functions.count()
        }
        */
        /*
        this is what is generated approx.:
        fun static funcCount(thiz: IrClassSymbol) : Int {
            // this cannot access private entities
            return thiz.owner.functions.count()
        }
        */

        // Wish: IrClass.find<XYZ> extension functions
        // Wish: IrClassSymbol.find<XYZ> extension functions
        // Wish: IrSymbolOwner.find<XYZ> extension functions
        // Wish: stringBuilderClass.findConstructor("()")
        val stringBuilderConstructor =
            stringBuilderClass.constructors.single { it.owner.valueParameters.isEmpty() }
        // Wish: stringBuilderClass.findFunction("append(Int)")
        val stringBuilderAppendIntFunc =
            stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.intType }
        // Wish: stringBuilderClass.findFunction("append(Long)")
        val stringBuilderAppendLongFunc =
            stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.longType }
        // Wish: stringBuilderClass.findFunction("append(String?)")
        val stringBuilderAppendStringFuncOld =
            stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.stringType.makeNullable() }
        val stringBuilderAppendStringFunc = pluginContext.findFunction("kotlin/text/StringBuilder.append(String?)")

        // Wish:  findFunction("kotlin/io/println(String)")
        val printlnFuncOld =
            pluginContext.referenceFunctions(CallableId(FqName("kotlin.io"), Name.identifier("println"))).single {
                it.owner.valueParameters.run { size == 1 && get(0).type == pluginContext.irBuiltIns.anyNType }
            }
        //val testFunc = pluginContext.findFunction("PerfMeasureExtension2/test(String, Int)")
        val printlnFunc = pluginContext.findFunction("kotlin/io/println(String)")
        assert(printlnFunc == pluginContext.findFunction("kotlin/io/println(kotlin/text/StringBuilder?)"))
        assert(printlnFunc != pluginContext.findFunction("kotlin/io/println(Int)"))

        // Wish:  findFunction("kotlin/io/MyClass.fooFunc(kotlin/text/StringBuilder,Int?)")
        /* pluginContext.referenceFunctions(CallableId(FqName("kotlin.io"), FqName("MyClass"), Name.identifier("fooFunc"))).single {
            func -> func.owner.valueParameters.size == 2 &&
                func.owner.valueParameters[0].type == findClass("kotlin/text/StringBuilder").type &&
                func.owner.valueParameters[1].type == pluginContext.irBuiltIns.intType.makeNullable()
        }*/
        // Wish:  findClass("kotlin/io/MyClass").findFunction("fooFunc(kotlin/text/StringBuilder,Int?)")
        /* pluginContext.referenceClass(ClassId.fromString("kotlin/io/MyClass")).functions.single {
            // ...
        } */


        val debugFile = File("./DEBUG.txt")
        debugFile.delete()
//        val pathClass =
//            pluginContext.referenceClass(ClassId.fromString("kotlinx/io/files/Path"))!!
        val rawSinkClass = pluginContext.findClass("kotlinx/io/RawSink")
        assert (rawSinkClass == (pluginContext.referenceClass(ClassId.fromString("kotlinx/io/RawSink"))!!))

        // Watch out, Path does not use constructors but functions to build
        val pathConstructionFunc = pluginContext.findFunction("kotlinx.io.files.Path(String)")
        assert(pathConstructionFunc == pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io.files"),
                Name.identifier("Path")
            )
        ).single { it.owner.valueParameters.size == 1 })

        // Wish: findProperty("kotlinx/io/files/SystemFileSystem")
        val systemFileSystem = pluginContext.findProperty("kotlinx.io.files/SystemFileSystem")
        assert(systemFileSystem == pluginContext.referenceProperties(
            CallableId(
                FqName("kotlinx.io.files"),
                Name.identifier("SystemFileSystem")
            )
        ).single())

        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail
        val sinkFunc = systemFileSystemClass.functions.single { it.owner.name.asString() == "sink" }
        val bufferedFunc = pluginContext.findFunction("kotlinx.io.buffered(): kotlinx.io.Sink")
        assert(bufferedFunc == (pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                Name.identifier("buffered")
            )
        ).single { it.owner.extensionReceiverParameter!!.type == sinkFunc.owner.returnType }))

        debugFile.appendText("1")
        debugFile.appendText(pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                Name.identifier("writeString")
            )
        ).joinToString(";") { it.owner.valueParameters.joinToString(",") { it.type.classFqName.toString() } })

        // Wish: findFunction("kotlinx.io/writeString(String,Int,Int)")
        val writeStringFunc = pluginContext.findFunction("kotlinx.io/writeString(String, Int, Int)")
        assert (writeStringFunc == pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                Name.identifier("writeString")
            )
        ).single {
            it.owner.valueParameters.size == 3 &&
                    it.owner.valueParameters[0].type == pluginContext.irBuiltIns.stringType &&
                    it.owner.valueParameters[1].type == pluginContext.irBuiltIns.intType &&
                    it.owner.valueParameters[2].type == pluginContext.irBuiltIns.intType
        })

        val flushFunc = pluginContext.findFunction("kotlinx.io.Sink.flush")
        assert(flushFunc == pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                FqName("Sink"),
                Name.identifier("flush")
            )
        ).single())

        debugFile.appendText("2")
        val toStringFunc = pluginContext.findFunction("kotlin.toString()")
        assert (toStringFunc == pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlin"),
                Name.identifier("toString")
            )
        ).single())
        debugFile.appendText("3")

        val STRINGBUILDER_MODE = false

        /*
        val repeatFunc = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlin.text"),
                null,
                Name.identifier("repeat")
            )
        ).first()

         */

        val firstFile = moduleFragment.files[0]

        /*
        val depth: IrField = pluginContext.irFactory.buildField {
            name = Name.identifier("_depth")
            type = pluginContext.irBuiltIns.intType
            isFinal = false
            isStatic = true
        }.apply {
            this.initializer =
                DeclarationIrBuilder(pluginContext, firstFile.symbol).irExprBody(
                    IrConstImpl.int(0, 0, pluginContext.irBuiltIns.intType, 0)
                )
        }
        firstFile.declarations.add(depth)
        depth.parent = firstFile

         */

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

        val randomDefaultObjectClass = pluginContext.findClass("kotlin/random/Random.Default")
        assert (randomDefaultObjectClass == pluginContext.referenceClass(ClassId.fromString("kotlin/random/Random.Default"))!!)

        val nextIntFunc = pluginContext.findFunction("kotlin.random/Random.Default/nextInt()")
        assert(nextIntFunc == pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlin.random"),
                FqName("Random.Default"),
                Name.identifier("nextInt")
            )
        ).single {
            it.owner.valueParameters.isEmpty()
        })

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
                    // Wish: irConcat("./trace_${pluginContext.platform!!.presentableDescription}_", randomNumber, ".txt")
                    irConcat().apply {
                        addArgument(irString("./trace_${pluginContext.platform!!.presentableDescription}_"))
                        // TODO: use kotlinx.datetime.Clock.System.now()
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
                // Wish: We want to be able to call functions on objects returned from other function calls
                // Wish: systemFileSystem.call(sinkFunc, bufferedTraceFileName).call(bufferedFunc)
                irExprBody(irCall(bufferedFunc).apply {
                    extensionReceiver = irCall(sinkFunc).apply {
                        // Wish: We want to be able to call functions on objects stored in different locations such as fields or properties
                        // Wish: call(sinkFunc).on(systemFileSystem).with(bufferedTraceFileName)
                        // Even better Wish: systemFileSystem.call(sinkFunc, bufferedTraceFileName)
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
                        // TODO: use kotlinx.datetime.Clock.System.now()
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
            val timeSourceMonotonicClass: IrClassSymbol = pluginContext.findClass("kotlin/time/TimeSource.Monotonic")
            assert (timeSourceMonotonicClass == pluginContext.referenceClass(ClassId.fromString("kotlin/time/TimeSource.Monotonic"))!!)

            /*
            classMonotonic.functions.forEach {
                println("${classMonotonic.defaultType.classFqName} | ${classMonotonic.owner.name}.${it.owner.name.asString()}")
            }
            */

            // abstract method
            /*
            val abstractFunMarkNow =
                pluginContext.referenceFunctions(
                    CallableId(
                        FqName("kotlin.time"),
                        FqName("TimeSource"),
                        Name.identifier("markNow")
                    )
                ).single() */

            /* val funMarkNowViaClass = classMonotonic.functions.find { it.owner.name.asString() == "markNow" }!! */

            val funMarkNow = pluginContext.findFunction("kotlin.time/TimeSource.Monotonic/markNow")
            assert (funMarkNow == (pluginContext.referenceFunctions(
                CallableId(
                    FqName("kotlin.time"),
                    FqName("TimeSource.Monotonic"),
                    Name.identifier("markNow")
                )
            ).single()))

            // assertion: funMarkNowViaClass == funMarkNow

            return pluginContext.irFactory.buildFun {
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
                    /*
                    +irSetField(null, depth, irCall(pluginContext.irBuiltIns.intPlusSymbol).apply {
                        dispatchReceiver = irGetField(null, depth)
                        putValueArgument(0, irInt(1))
                    }) */
                    /*
                    +irCall(printlnFunc).apply {
                        putValueArgument(0, irConcat().apply {
                            addArgument(irCall(repeatFunc).apply {
                                extensionReceiver = irString("-")
                                putValueArgument(0, irGetField(null, depth))
                            })
                            addArgument(irString("> "))
                            addArgument(irGet(valueParameters[0]))
                        })
                    }
                    */
                    /*
                +irCall(stringBuilderAppendStringFunc).apply {
                    dispatchReceiver = irGetField(null, stringBuilder)
                    putValueArgument(0, irConcat().apply {
                        addArgument(irCall(repeatFunc).apply {
                            extensionReceiver = irString("-")
                            putValueArgument(0, irGetField(null, depth))
                        })
                        addArgument(irString("> "))
                        addArgument(irGet(valueParameters[0]))
                    })
                }
                */
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
                            // Wish: irConcat(">;", valueParameters[0], "\n")
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

                body = DeclarationIrBuilder(pluginContext, symbol, startOffset, endOffset).irBlockBody {
                    // Duration
                    val elapsedDuration = irTemporary(irCall(funElapsedNow).apply {
                        dispatchReceiver = irGet(valueParameters[1])
                    })
                    // Wish: elapsedDuration.type.findProperty("inWholeMicroseconds")
                    val elapsedMicrosProp: IrProperty =
                        elapsedDuration.type.getClass()!!.properties.single { it.name.asString() == "inWholeMicroseconds" }

                    val elapsedMicros = irTemporary(irCall(elapsedMicrosProp.getter!!).apply {
                        dispatchReceiver = irGet(elapsedDuration)
                    })

                    /*
                    val concat = irConcat()
                    concat.addArgument(irCall(repeatFunc).apply {
                        extensionReceiver = irString("-")
                        putValueArgument(0, irGetField(null, depth))
                    })
                    concat.addArgument(irString("< "))
                    concat.addArgument(irGet(valueParameters[0]))
                    concat.addArgument(irString(" after "))
                    concat.addArgument(irGet(elapsedMicros))
                    concat.addArgument(irString("us"))
                    */
                    /*
                    concat.addArgument(irString(" with "))
                    concat.addArgument(irGet(valueParameters[2]))
                    */
                    /*
                    +irCall(printlnFunc).apply {
                        putValueArgument(0, concat)
                    }
                    */
                    /*
                    +irCall(stringBuilderAppendStringFunc).apply {
                        dispatchReceiver = irGetField(null, stringBuilder)
                        putValueArgument(0, concat)
                    }*/

                    if (STRINGBUILDER_MODE) {
                        // Wish: stringBuilder.call(stringBuilderAppendStringFunc, "<;")
                        +irCall(stringBuilderAppendStringFunc).apply {
                            dispatchReceiver = irGetField(null, stringBuilder)
                            putValueArgument(0, irString("<;"))
                        }
                        // Wish: stringBuilder.call(stringBuilderAppendIntFunc, valueParameters[0])
                        +irCall(stringBuilderAppendIntFunc).apply {
                            dispatchReceiver = irGetField(null, stringBuilder)
                            putValueArgument(0, irGet(valueParameters[0]))
                        }
                        // Wish: stringBuilder.call(stringBuilderAppendStringFunc, ";")
                        +irCall(stringBuilderAppendStringFunc).apply {
                            dispatchReceiver = irGetField(null, stringBuilder)
                            putValueArgument(0, irString(";"))
                        }
                        // Wish: stringBuilder.call(stringBuilderAppendStringFunc, ";")
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
                    /*
                    +irSetField(null, depth, irCall(pluginContext.irBuiltIns.intPlusSymbol).apply {
                        dispatchReceiver = irGetField(null, depth)
                        putValueArgument(0, irInt(-1))
                    })
                     */
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
            /*
            if (pluginContext.platform.isJvm()) {
                val fileClass = pluginContext.referenceClass(ClassId.fromString("java/io/File"))!!
                val fileConstructor =
                    fileClass.constructors.single { it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.stringType.makeNullable() }
                val writeTextExtensionFunc =
                    pluginContext.referenceFunctions(
                        CallableId(
                            FqName("kotlin.io"),
                            Name.identifier("writeText")
                        )
                    ).single()

                val toStringFunc =
                    pluginContext.referenceFunctions(
                        CallableId(
                            FqName("kotlin"),
                            Name.identifier("toString")
                        )
                    ).single()

                irBlock {
                    +irCall(writeTextExtensionFunc).apply {
                        // same as irCallConstructor(fileConstructor, listOf())
                        extensionReceiver = irCall(fileConstructor).apply {
                            putValueArgument(
                                0,
                                irString("./${pluginContext.platform!!.presentableDescription}_trace_${currentMillis}.txt")
                            )
                        }
                        putValueArgument(0, irCall(toStringFunc).apply {
                            extensionReceiver = irGetField(null, stringBuilder)
                        })
                    }
                    +irCall(writeTextExtensionFunc).apply {
                        // same as irCallConstructor(fileConstructor, listOf())
                        extensionReceiver = irCall(fileConstructor).apply {
                            putValueArgument(
                                0,
                                irString("./${pluginContext.platform!!.presentableDescription}_symbols_${currentMillis}.txt")
                            )
                        }
                        putValueArgument(
                            0,
                            irString("{ " + methodIdMap.map { (name, id) -> id to name }
                                .sortedBy { (id, _) -> id }
                                .joinToString("\n") { (id, name) -> "\"$id\": \"$name\"" } + " }"))
                    }
                    +irCall(printlnFunc).apply {
                        putValueArgument(0, irConcat().apply {
                            addArgument(
                                irString(
                                    Paths.get("./${pluginContext.platform!!.presentableDescription}_trace_${currentMillis}.txt")
                                        .absolutePathString()
                                )
                            )
                        })
                    }
                }
            } else {*/
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
                //Wish: flushFunc.withThis(bufferedTraceFileSink).call()
                +irCall(flushFunc).apply {
                    dispatchReceiver = irGetField(null, bufferedTraceFileSink)
                }
                // Wish: printlnFunc.call(bufferedSymbolsFileName)
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
                    /*
                    putValueArgument(
                        0, irString(buildString {
                            append(from.fqNameWhenAvailable?.asString() ?: from.name)
                            append("(")
                            append(from.valueParameters.joinToString(", ") {
                                it.type.classFqName?.asString() ?: "???"
                            })
                            append(")")
                            append(" - ")
                            append(from.origin)
                        })
                    )*/
                    putValueArgument(
                        0,
                        methodIdMap[from.kotlinFqName.asString()]!!.toIrConst(pluginContext.irBuiltIns.intType)
                    )
                }

            fun IrBlockBuilder.irCallExitFunc(
                exitFunc: IrFunction,
                from: IrFunction,
                startTime: IrVariable  //, result: IrExpression = irNull(from.returnType)
            ) = irCall(exitFunc).apply {
                /*
                putValueArgument(0, irString(buildString {
                    append(from.fqNameWhenAvailable?.asString() ?: from.name)
                    append("(")
                    append(from.valueParameters.joinToString(", ") {
                        it.type.classFqName?.asString() ?: "???"
                    })
                    append(")")
                }))*/
                putValueArgument(
                    0,
                    methodIdMap[from.kotlinFqName.asString()]!!.toIrConst(pluginContext.irBuiltIns.intType)
                )
                putValueArgument(1, irGet(startTime))
                // putValueArgument(2, result)
            }

            println("Wrapping body of ${func.name} (origin: ${func.origin})")
            return DeclarationIrBuilder(pluginContext, func.symbol).irBlockBody {
                /*
                val enterFunc = file.declarations.filterIsInstance<IrFunction>().single {
                    it.getNameWithAssert().asString() == "_enter_method"
                }
                val exitFunc = file.declarations.filterIsInstance<IrFunction>().single {
                    it.getNameWithAssert().asString() == "_exit_method"
                }
                */
                // no +needed on irTemporary as it is automatically added to the builder
                val startTime = irTemporary(irCallEnterFunc(enterFunc, func))

                val tryBlock: IrExpression = irBlock(resultType = func.returnType) {
                    for (statement in func.body?.statements ?: listOf()) {
                        +(statement.transform(object : IrElementTransformerVoidWithContext() {
                            override fun visitReturn(expression: IrReturn): IrExpression {
                                if (expression.returnTargetSymbol == func.symbol) {
                                    return DeclarationIrBuilder(pluginContext, func.symbol).irBlock {
                                        val returnExpression = irTemporary(expression.value)
                                        +irCallExitFunc(exitFunc, func, startTime)//, irGet(returnExpression))
                                        +expression.apply {
                                            // do not calculate expression again but use value from the temporary
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
                //+tryBlock

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
                                +irCallExitFunc(exitFunc, func, startTime) //, irGet(catchVar))
                                +irThrow(irGet(catchVar))
                            })
                    ),
                    if (func.name.asString() == "main") buildMainFinally(func)
                    else null
                )
            }
        }

        // IrElementVisitor / IrElementVisitorVoid
        // IrElementTransformer / IrElementTransformerVoid / IrElementTransformerVoidWithContext
        // IrElementTransformerVoidWithContext().visitfile(file, null)

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
                        // do not further transform this method, e.g., its statements are not transformed
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

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.findClass(name: String): IrClassSymbol {
    fun getType(typeName: String): IrClassSymbol? {
        val classId = ClassId.fromString(typeName)
        return this.referenceClass(classId) ?: this.referenceTypeAlias(classId)?.owner?.expandedType?.classOrNull
    }

    // ☼ Todo: Should all variations be tested or can a specific search be made for packages, classes, methods or properties?
    return getType(name) ?: getType(name.replace('/', '.')) ?: getType(name.replace('.', '/')) as IrClassSymbol
}

private val regexProp = Regex("(?:((?:[a-zA-Z]\\w*[\\.\\/])*[a-zA-Z]\\w*)[\\.\\/])?([a-zA-Z][\\w<>]*)");

fun IrPluginContext.findProperty(name: String): IrPropertySymbol {
    val match = regexProp.matchEntire(name)
    if (match != null && match.groups.size >= 3) {
        val identifier = match.groups[2]?.value
        if (!identifier.isNullOrEmpty()) {
            val id = Name.identifier(identifier);
            val fqName = match.groups[1]?.value

            val ci = if (fqName.isNullOrEmpty()) {
                CallableId(id)
            } else {
                CallableId(FqName(fqName), id)
            }

            return this.referenceProperties(ci).single()
        }
    }

    throw Exception("properties not found: $name")
}

private val regexFun = Regex("(?:((?:[a-zA-Z]\\w*[\\.\\/])*[a-zA-Z]\\w*)[\\.\\/])?([a-zA-Z][\\w<>]*)(?:[(]([\\w<>\\.\\/]+\\??(?:,\\s*[\\w<>\\.\\/]+\\??)*)*[)](?:\\s*:\\s*((?:(?:(?:[a-zA-Z]\\w*[\\.\\/])*[a-zA-Z]\\w*)[\\.\\/])?(?:[a-zA-Z][\\w<>]*)))?)?")

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun IrPluginContext.getIrTypeFromName(parameterType: String): IrType {
    val qm = '?'
    val isNullable = parameterType.endsWith(qm)
    val typeName = if (isNullable) parameterType.trimEnd(qm) else parameterType
    val result = when (typeName) {
        "Any" -> this.irBuiltIns.anyNType
        "Byte" -> this.irBuiltIns.byteType
        "Short" -> this.irBuiltIns.shortType
        "Int" -> this.irBuiltIns.intType
        "Long" -> this.irBuiltIns.longType
        "Float" -> this.irBuiltIns.floatType
        "Double" -> this.irBuiltIns.doubleType
        "Char" -> this.irBuiltIns.charType
        "String" -> this.irBuiltIns.stringType

        "CharArray" -> this.irBuiltIns.charArray.defaultType
        "ByteArray" -> this.irBuiltIns.byteArray.defaultType
        "ShortArray" -> this.irBuiltIns.shortArray.defaultType
        "IntArray" -> this.irBuiltIns.intArray.defaultType
        "LongArray" -> this.irBuiltIns.longArray.defaultType
        "FloatArray" -> this.irBuiltIns.floatArray.defaultType
        "DoubleArray" -> this.irBuiltIns.doubleArray.defaultType
        "BooleanArray" -> this.irBuiltIns.booleanArray.defaultType

        else -> {
            fun getType(typeName: String): IrType? {
                val classId = ClassId.fromString(typeName)
                return (this.referenceClass(classId) ?: this.referenceTypeAlias(classId)?.owner?.expandedType) as IrType?
            }
            (getType(typeName) ?: getType(typeName.replace('/', '.')) ?: getType(typeName.replace('.', '/'))) as IrType
        }
    }

    return if (isNullable) result.makeNullable() else result
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.findFunction(name: String): IrSimpleFunctionSymbol {
    val match = regexFun.matchEntire(name)
    if (match != null && match.groups.size >= 4) {
        val identifier = match.groups[2]?.value
        if (!identifier.isNullOrEmpty()) {
            val id = Name.identifier(identifier);
            val fqName = match.groups[1]?.value

            val referenceFunctions: Sequence<IrSimpleFunctionSymbol>
            if (fqName.isNullOrEmpty()) {
                referenceFunctions = this.referenceFunctions(CallableId(id)).asSequence()
            } else {
                fun getClass(fqName: String): Sequence<IrSimpleFunctionSymbol>? {
                    val func = this.referenceFunctions(CallableId(FqName(fqName.replace('/', '.')), id))
                    if (func.any()) {
                        return func.asSequence()
                    } else {
                        // In JVM, StringBuilder is a type alias (see https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/)
                        val classId = ClassId.fromString(fqName)
                        return (this.referenceClass(classId) ?: this.referenceTypeAlias(classId)?.owner?.expandedType?.classOrNull)?.functions?.filter { it.owner.name.asString() == identifier }
                    }
                }

                var classes = getClass(fqName)
                if (classes == null || !classes.any()) {
                    classes = getClass(fqName.replace('/', '.'))
                    if (classes == null || !classes.any()) {
                        classes = getClass(fqName.replace('.', '/'))
                    }
                }
                referenceFunctions = classes!!
            }

            /// is function[s] found?
            if (referenceFunctions.any()) {
                val requiredTypes =
                    match.groups[3]?.value?.split(Regex("[\\s,]+"))?.map { typeName -> this.getIrTypeFromName(typeName) }
                        ?: listOf()

                val nix = this.irBuiltIns.anyNType
                var result = referenceFunctions.filter {
                    val valueParameters = it.owner.valueParameters
                    valueParameters.size == requiredTypes.size && valueParameters.run {
                        size == requiredTypes.size && valueParameters.zip(requiredTypes).all { t ->
                            val existing = t.first
                            val required = t.second
                            existing == required || existing.type == required.type || existing.type == nix
                        }
                    }
                }

                if (match.groups.size >= 5) {
                    val returnTypeName = match.groups[4]?.value
                    if (!returnTypeName.isNullOrEmpty()) {
                        val returnType = this.findClass(returnTypeName)
                        result = result.filter {
                            it.owner.returnType == returnType.defaultType
                        }
                    }
                }

                if (result.take(2).count() >= 2) {
                    val pain = result.map { Pair(it, it.owner.valueParameters.count { p -> p.type == nix } ) }
                    val min = pain.map { p -> p.second }.min()
                    result = pain
                        .filter { p -> p.second == min }
                        .map { p -> p.first }
                }

                return result.single()
            }
        }
    }

    throw Exception("function not found: $name")
}