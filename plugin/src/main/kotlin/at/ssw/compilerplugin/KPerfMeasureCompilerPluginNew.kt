package at.ssw.compilerplugin

import at.ssw.compilerplugin.ExampleConfigurationKeysNew.KEY_ENABLED
import at.ssw.compilerplugin.ExampleConfigurationKeysNew.LOG_ANNOTATION_KEY
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
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin.Companion.ADAPTER_FOR_CALLABLE_REFERENCE
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.presentableDescription
import java.io.File
import kotlin.collections.set
import kotlin.time.ExperimentalTime

object ExampleConfigurationKeysNew {
    val KEY_ENABLED: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("enabled.new")
    val LOG_ANNOTATION_KEY: CompilerConfigurationKey<MutableList<String>> =
        CompilerConfigurationKey.create("measure.annotation.new")
}


/*
Commandline processor to process options.
This is the entry point for the compiler plugin.
It is found via a ServiceLoader.
Thus, we need an entry in META-INF/services/org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
that reads at.ssw.compilerplugin.KPerfMeasureCommandLineProcessor
 */
@OptIn(ExperimentalCompilerApi::class)
class KPerfMeasureCommandLineProcessorNew : CommandLineProcessor {
    override val pluginId: String = "k-perf-measure-compiler-plugin-New"
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
        println("KPerfMeasureCommandLineProcessorNew - init")
    }

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        println("KPerfMeasureCommandLineProcessorNew - processOption ($option, $value)")
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
class PerfMeasureComponentRegistrarNew : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    init {
        println("PerfMeasureComponentRegistrarNew - init")
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
class PerfMeasureExtension2New(
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
        val timeMarkClass: IrClassSymbol = pluginContext.findClass("kotlin/time/TimeMark") ?: error("Cannot find class kotlin.time.TimeMark")

        val stringBuilderAppendIntFunc = pluginContext.findFunction("kotlin/text/StringBuilder.append(int)") ?: error("Cannot find function append(int) in kotlin.text.StringBuilder")
        val stringBuilderAppendStringFunc = pluginContext.findFunction("kotlin/text/StringBuilder.append(String?)") ?: error("Cannot find function append(String?) in kotlin.text.StringBuilder")

        val printlnFunc = pluginContext.findFunction("kotlin/io/println(Any?)") ?: error("Cannot find function println(Any?)")

        // Watch out, Path does not use constructors but functions to build
        val pathConstructionFunc = pluginContext.findFunction("kotlinx/io/files/Path(string)") ?: error("Cannot find function Path(String)")

        val systemFileSystem = pluginContext.findProperty("kotlinx/io/files/SystemFileSystem") ?: error("Cannot find property kotlinx.io.files.SystemFileSystem")
        val sinkFunc = systemFileSystem.findFunction(pluginContext, "sink(*)") ?: error("Cannot find function sink() in SystemFileSystem")
        appendToDebugFile("Different versions of kotlinx.io.writeString:\n")
        appendToDebugFile(
            pluginContext.referenceFunctions(
                CallableId(
                    FqName("kotlinx.io"),
                    Name.identifier("writeString")
                )
            ).joinToString("\n") { func ->
                "kotlinx.io.writeString(${func.owner.valueParameters.joinToString(",") { param -> param.type.classFqName.toString() }})"
            }
        )
        val writeStringFunc = pluginContext.findFunction("kotlinx/io/writeString(String, int, int)") ?: error("Cannot find function writeString(String, int, int)")
        val flushFunc = pluginContext.findFunction("kotlinx/io/Sink.flush()") ?: error("Cannot find function flush() in kotlinx.io.Sink.flush()")
        debugFile.appendText("2")
        debugFile.appendText("3")

        val firstFile = moduleFragment.files[0]

        val stringBuilder: IrField = pluginContext.createField(firstFile.symbol, "_stringBuilder") {
            pluginContext.findConstructor("kotlin/text/StringBuilder()")?.invoke() ?:
            throw IllegalStateException("Cannot find constructor kotlin.text.StringBuilder()")
        }
        firstFile.declarations.add(stringBuilder)
        stringBuilder.parent = firstFile

        val randomDefaultObjectClass = pluginContext.findClass("kotlin/random/Random.Default") ?: error("Cannot find class kotlin.random.Random.Default")

        val randomNumber = pluginContext.createField(firstFile.symbol, "_randNumber", false) {
            randomDefaultObjectClass.call(pluginContext, "nextInt")
        }
        firstFile.declarations.add(randomNumber)
        randomNumber.parent = firstFile

        val bufferedTraceFileName = pluginContext.createField(firstFile.symbol, "_bufferedTraceFileName", false) {
            irConcat("./trace_${pluginContext.platform!!.presentableDescription}_", randomNumber, ".txt")
        }
        firstFile.declarations.add(bufferedTraceFileName)
        bufferedTraceFileName.parent = firstFile

        val bufferedTraceFileSink = pluginContext.createField(firstFile.symbol, "_bufferedTraceFileSink", false) {
            systemFileSystem.call(sinkFunc, pathConstructionFunc(bufferedTraceFileName))
                .chain(pluginContext, "kotlinx/io/buffered")
        }
        firstFile.declarations.add(bufferedTraceFileSink)
        bufferedTraceFileSink.parent = firstFile

        val bufferedSymbolsFileName = pluginContext.createField(firstFile.symbol, "_bufferedSymbolsFileName", false) {
            irConcat("./symbols_${pluginContext.platform!!.presentableDescription}_", randomNumber, ".txt")
        }
        firstFile.declarations.add(bufferedSymbolsFileName)
        bufferedSymbolsFileName.parent = firstFile

        val bufferedSymbolsFileSink = pluginContext.createField(firstFile.symbol, "_bufferedSymbolsFileSink", false) {
            systemFileSystem.call(sinkFunc, pathConstructionFunc(bufferedSymbolsFileName))
                .chain(pluginContext, "kotlinx/io/buffered")
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

        fun buildEnterFunction(): IrFunction {
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
                    enableCallDSL {
                        if (STRINGBUILDER_MODE) {
                            +stringBuilder.call(stringBuilderAppendIntFunc, ">;")
                            +stringBuilder.call(stringBuilderAppendIntFunc, valueParameters[0])
                            +stringBuilder.call(stringBuilderAppendStringFunc, "\n")
                        } else {
                            bufferedTraceFileSink.call(writeStringFunc, irConcat(">;", valueParameters[0], "\n"))
                        }
                        /* val funMarkNowViaClass = classMonotonic.functions.find { it.owner.name.asString() == "markNow" }!! */

                        // assertion: funMarkNowViaClass == funMarkNow
                        +irReturn(pluginContext.findClass("kotlin/time/TimeSource.Monotonic")!!.call(pluginContext,"markNow"))
                    }
                }
            }
        }

        val enterFunc = buildEnterFunction()
        firstFile.declarations.add(enterFunc)
        enterFunc.parent = firstFile

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

                body = DeclarationIrBuilder(pluginContext, symbol, startOffset, endOffset).irBlockBody {
                    enableCallDSL {
                        val elapsedDuration = irTemporary(valueParameters[1].call(pluginContext, "elapsedNow"))
                        val elapsedMicrosProp: IrProperty = elapsedDuration.findProperty("inWholeMicroseconds")
                        val elapsedMicros = irTemporary(elapsedDuration.call(elapsedMicrosProp))

                        if (STRINGBUILDER_MODE) {
                            +stringBuilder.call(stringBuilderAppendStringFunc, "<;")
                            +stringBuilder.call(stringBuilderAppendIntFunc, valueParameters[0])
                            +stringBuilder.call(stringBuilderAppendStringFunc, ";")
                            +stringBuilder.call(pluginContext, "append")
                            +stringBuilder.call(stringBuilderAppendStringFunc, "\n")
                        } else {
                            +bufferedTraceFileSink.call(writeStringFunc, irConcat("<;", valueParameters[0], ";", elapsedMicros, "\n"))
                        }
                    }
                }
            }
        }

        val exitFunc = buildGeneralExitFunction()
        firstFile.declarations.add(exitFunc)
        exitFunc.parent = firstFile

        fun buildMainExitFunction(): IrSimpleFunction {
            fun IrBlockBodyBuilder.flushTraceFile() {
                enableCallDSL {
                    bufferedTraceFileSink.call(flushFunc)
                }
            }

            fun IrBlockBodyBuilder.writeAndFlushSymbolsFile() {
                enableCallDSL {
                    +bufferedSymbolsFileSink.call(writeStringFunc, "{ " + methodIdMap.map { (name, id) -> id to name }
                        .sortedBy { (id, _) -> id }
                        .joinToString(",\n") { (id, name) -> "\"$id\": \"$name\"" } + " }")
                    +bufferedSymbolsFileSink.call(flushFunc)
                }
            }

            fun IrBlockBodyBuilder.printFileNamesToStdout() {
                enableCallDSL {
                    +printlnFunc(bufferedTraceFileName)
                    +printlnFunc(bufferedSymbolsFileName)
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
                    enableCallDSL {
                        flushTraceFile()

                        +exitFunc(methodIdMap["main"]!!, valueParameters[0])

                        if (STRINGBUILDER_MODE) {
                            +bufferedTraceFileSink.call(writeStringFunc, stringBuilder.call(pluginContext, "toString"))
                        }

                        writeAndFlushSymbolsFile()

                        flushTraceFile()

                        printFileNamesToStdout()
                    }
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
                enableCallDSL {
                    val startTime = irTemporary(enterFunc(methodIdMap[func.kotlinFqName.asString()]!!))

                    val tryBlock: IrExpression = irBlock(resultType = func.returnType) {
                        for (statement in func.body?.statements ?: listOf()) +statement
                    }

                    +irTry(
                        tryBlock.type,
                        tryBlock,
                        listOf(),
                        if (func.name.asString() == "main") exitMainFunc.invoke(startTime)
                        else exitFunc(methodIdMap[func.kotlinFqName.asString()]!!, startTime)
                    )
                }
            }
        }

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
}