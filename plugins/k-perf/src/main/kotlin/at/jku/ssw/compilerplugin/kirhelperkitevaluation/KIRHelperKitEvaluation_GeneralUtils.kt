/*
package at.jku.ssw.compilerplugin

import at.jku.ssw.compilerplugin.ExampleConfigurationKeysGeneral.KEY_ENABLED
import at.jku.ssw.compilerplugin.ExampleConfigurationKeysGeneral.LOG_ANNOTATION_KEY
import at.jku.ssw.kir.call.enableCallDSL
import at.jku.ssw.kir.find.irplugincontext.findClass
import at.jku.ssw.kir.find.irvariable.findProperty
import at.jku.ssw.kir.general.IrFileWriter
import at.jku.ssw.kir.general.IrStringBuilder
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

object ExampleConfigurationKeysGeneral {
    val KEY_ENABLED: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("enabled.general")
    val LOG_ANNOTATION_KEY: CompilerConfigurationKey<MutableList<String>> =
        CompilerConfigurationKey.create("measure.annotation.general")
}
@OptIn(ExperimentalCompilerApi::class)
class KPerfMeasureCommandLineProcessorGeneral : CommandLineProcessor {
    override val pluginId: String = "k-perf-compiler-plugin-General"
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
        println("KPerfMeasureCommandLineProcessorGeneral - init")
    }
    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        println("KPerfMeasureCommandLineProcessorGeneral - processOption ($option, $value)")
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
class PerfMeasureComponentRegistrarGeneralUtil : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true
    init {
        println("PerfMeasureComponentRegistrarGeneralUtil - init")
    }
    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == false) {
            return
        }
        val messageCollector = configuration.get(CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY)!!
        IrGenerationExtension.registerExtension(PerfMeasureExtensionGeneralUtil(MessageCollector.NONE))
    }
}
class PerfMeasureExtensionGeneralUtil(
    private val messageCollector: MessageCollector
) : IrGenerationExtension {
    val STRINGBUILDER_MODE = false
    @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val timeMarkClass: IrClassSymbol = pluginContext.findClass("kotlin/time/TimeMark")
        val firstFile = moduleFragment.files[0]
        val stringBuilder = IrStringBuilder(pluginContext, firstFile)
        val randNr = (0..10000).random()
        val bufferedTraceFileName = "./trace_${pluginContext.platform!!.presentableDescription}_$randNr.txt"
        val bufferedTraceFileSink = IrFileWriter(pluginContext, firstFile, bufferedTraceFileName)
        val bufferedSymbolsFileName = "./symbols_${pluginContext.platform!!.presentableDescription}_$randNr.txt"
        val bufferedSymbolsFileSink = IrFileWriter(pluginContext, firstFile, bufferedSymbolsFileName)
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
                            +stringBuilder.append(">;")
                            +stringBuilder.append(valueParameters[0])
                            +stringBuilder.append("\n")
                        } else {
                            +bufferedTraceFileSink.writeData(irConcat(">;", valueParameters[0], "\n"))
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
                            +stringBuilder.append("<;")
                            +stringBuilder.append(valueParameters[0])
                            +stringBuilder.append(";")
                            +stringBuilder.append(elapsedMicros)
                            +stringBuilder.append("\n")
                        } else {
                            +bufferedTraceFileSink.writeData(irConcat("<;", valueParameters[0], ";", elapsedMicros, "\n"))
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
                        +bufferedTraceFileSink.flushSink()
                        +exitFunc(methodIdMap["main"]!!, valueParameters[0])
                        if (STRINGBUILDER_MODE) {
                            +bufferedTraceFileSink.writeData(stringBuilder.irToString())
                        }
                        +bufferedSymbolsFileSink.writeData("{ " + methodIdMap.map { (name, id) -> id to name }
                            .sortedBy { (id, _) -> id }
                            .joinToString(",\n") { (id, name) -> "\"$id\": \"$name\"" } + " }")
                        +bufferedSymbolsFileSink.flushSink()
                        +bufferedTraceFileSink.flushSink()
                        +callPrintLn(pluginContext, bufferedTraceFileName)
                        +callPrintLn(pluginContext, bufferedSymbolsFileName)
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
*/