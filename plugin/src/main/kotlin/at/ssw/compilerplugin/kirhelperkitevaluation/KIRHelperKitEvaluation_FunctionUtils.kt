package at.jku.ssw.compilerplugin

import at.jku.ssw.kir.call.createField
import at.jku.ssw.kir.call.enableCallDSL
import at.jku.ssw.kir.find.irclasssymbol.findConstructor
import at.jku.ssw.kir.find.irclasssymbol.findFunction
import at.jku.ssw.kir.find.irplugincontext.findClass
import at.jku.ssw.kir.find.irplugincontext.findFunction
import at.jku.ssw.kir.find.irplugincontext.findProperty
import at.jku.ssw.kir.find.irvariable.findProperty
import at.jku.ssw.compilerplugin.ExampleConfigurationKeys.KEY_ENABLED
import at.jku.ssw.compilerplugin.ExampleConfigurationKeys.LOG_ANNOTATION_KEY
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
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin.Companion.ADAPTER_FOR_CALLABLE_REFERENCE
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.presentableDescription
import kotlin.time.ExperimentalTime

object ExampleConfigurationKeysFunction {
    val KEY_ENABLED: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("enabled.function")
    val LOG_ANNOTATION_KEY: CompilerConfigurationKey<MutableList<String>> =
        CompilerConfigurationKey.create("measure.annotation.function")
}
@OptIn(ExperimentalCompilerApi::class)
class KPerfMeasureCommandLineProcessorFunction : CommandLineProcessor {
    override val pluginId: String = "k-perf-compiler-plugin-Function"
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
        println("KPerfMeasureCommandLineProcessorFunction - init")
    }
    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        println("KPerfMeasureCommandLineProcessorFunction - processOption ($option, $value)")
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
class PerfMeasureComponentRegistrarFunctionUtil : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true
    init {
        println("PerfMeasureComponentRegistrarFunctionUtil - init")
    }
    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == false) {
            return
        }
        val messageCollector = configuration.get(CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY)!!
        IrGenerationExtension.registerExtension(PerfMeasureExtensionFunctionUtil(MessageCollector.NONE))
    }
}
class PerfMeasureExtensionFunctionUtil(
    private val messageCollector: MessageCollector
) : IrGenerationExtension {
    val STRINGBUILDER_MODE = false
    @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val timeMarkClass: IrClassSymbol = pluginContext.findClass("kotlin/time/TimeMark")
        val stringBuilderClass = pluginContext.findClass("kotlin/text/StringBuilder")
        val stringBuilderAppendIntFunc = stringBuilderClass.findFunction(pluginContext, "append(int)")
        val stringBuilderAppendStringFunc = stringBuilderClass.findFunction(pluginContext, "append(string?)")
        val writeStringFunc = pluginContext.findFunction("kotlinx/io/writeString(string,int,int)")
        val pathConstructionFunc = pluginContext.findFunction("kotlinx/io/files/Path(string)")
        val flushFunc = pluginContext.findFunction("kotlinx/io/Sink.flush()")
        val systemFileSystem = pluginContext.findProperty("kotlinx/io/files/SystemFileSystem")
        val firstFile = moduleFragment.files[0]
        val stringBuilder = pluginContext.createField(firstFile.symbol, "_stringBuilder")  { stringBuilderClass.findConstructor(pluginContext)() }
        val randomDefaultObjectClass = pluginContext.findClass("kotlin/random/Random.Default")
        val randomNumber = pluginContext.createField(firstFile.symbol, "_randomNumber") {randomDefaultObjectClass.call("nextInt()")}
        firstFile.declarations.add(randomNumber)
        randomNumber.parent = firstFile
        val bufferedTraceFileName = pluginContext.createField(firstFile.symbol, "_bufferedTraceFileName") {
            irConcat("./trace_${pluginContext.platform!!.presentableDescription}_", randomNumber, ".txt")
        }
        firstFile.declarations.add(bufferedTraceFileName)
        bufferedTraceFileName.parent = firstFile
        val bufferedTraceFileSink = pluginContext.createField(firstFile.symbol, "_bufferedTraceFileSink") {
            systemFileSystem.call("sink(*)", pathConstructionFunc(bufferedTraceFileName))
                .call("kotlinx/io/buffered()")
        }
        firstFile.declarations.add(bufferedTraceFileSink)
        bufferedTraceFileSink.parent = firstFile
        val bufferedSymbolsFileName = pluginContext.createField(firstFile.symbol, "_bufferedSymbolsFileName") {
            irConcat("./symbols_${pluginContext.platform!!.presentableDescription}_", randomNumber, ".txt")
        }
        firstFile.declarations.add(bufferedSymbolsFileName)
        bufferedSymbolsFileName.parent = firstFile
        val bufferedSymbolsFileSink = pluginContext.createField(firstFile.symbol, "_bufferedSymbolsFileSink") {
            systemFileSystem.call("sink(*)", pathConstructionFunc(bufferedSymbolsFileName))
                .call("kotlinx/io/buffered()")
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
                    name = Name.identifier("methodId")
                    type = pluginContext.irBuiltIns.intType
                }
                body = DeclarationIrBuilder(
                    pluginContext,
                    symbol,
                    startOffset,
                    endOffset
                ).irBlockBody {
                    enableCallDSL(pluginContext) {
                        if (STRINGBUILDER_MODE) {
                            +stringBuilder.call(stringBuilderAppendStringFunc, ">;")
                            +stringBuilder.call(stringBuilderAppendIntFunc, valueParameters[0])
                            +stringBuilder.call(stringBuilderAppendStringFunc, "\n")
                        } else {
                            +bufferedTraceFileSink.call(writeStringFunc, irConcat(">;", valueParameters[0], "\n"))
                        }
                        +irReturn(pluginContext.findClass("kotlin/time/TimeSource.Monotonic").call("markNow()"))
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
                    name = Name.identifier("methodId")
                    type = pluginContext.irBuiltIns.intType
                }
                addValueParameter {
                    name = Name.identifier("startTime")
                    type = timeMarkClass.defaultType
                }
                body = DeclarationIrBuilder(pluginContext, symbol, startOffset, endOffset).irBlockBody {
                    enableCallDSL(pluginContext) {
                        val elapsedDuration = irTemporary(valueParameters[1].call("elapsedNow()"))
                        val elapsedMicrosProp = elapsedDuration.findProperty("inWholeMicroseconds")
                        val elapsedMicros = irTemporary(elapsedDuration.call(elapsedMicrosProp))
                        if (STRINGBUILDER_MODE) {
                            +stringBuilder.call(stringBuilderAppendStringFunc, "<;")
                            +stringBuilder.call(stringBuilderAppendIntFunc, valueParameters[0])
                            +stringBuilder.call(stringBuilderAppendStringFunc, ";")
                            +stringBuilder.call("append(long)", elapsedMicros)
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
            return pluginContext.irFactory.buildFun {
                name = Name.identifier("_exit_main")
                returnType = pluginContext.irBuiltIns.unitType
            }.apply {
                addValueParameter {
                    name = Name.identifier("startTime")
                    type = timeMarkClass.defaultType
                }
                body = DeclarationIrBuilder(pluginContext, symbol, startOffset, endOffset).irBlockBody {
                    enableCallDSL(pluginContext) {
                        +bufferedTraceFileSink.call(flushFunc)
                        +exitFunc(methodIdMap["main"]!!, valueParameters[0])
                        if (STRINGBUILDER_MODE) {
                            +bufferedTraceFileSink.call(writeStringFunc, stringBuilder.call("kotlin/toString()"))
                        }
                        +bufferedSymbolsFileSink.call(writeStringFunc, "{ " + methodIdMap.map { (name, id) -> id to name }
                            .sortedBy { (id, _) -> id }
                            .joinToString(",\n") { (id, name) -> "\"$id\": \"$name\"" } + " }")
                        +bufferedSymbolsFileSink.call(flushFunc)
                        +bufferedTraceFileSink.call(flushFunc)
                        +call("kotlin/io/println(any?)", bufferedTraceFileName)
                        +call("kotlin/io/println(any?)", bufferedSymbolsFileName)
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
                enableCallDSL(pluginContext) {
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