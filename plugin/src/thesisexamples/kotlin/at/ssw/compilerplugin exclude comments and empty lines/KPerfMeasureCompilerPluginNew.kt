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
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
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
        fun findClass(name: String): IrClassSymbol = pluginContext.findClass(name)
        fun findFunction(name: String): IrSimpleFunctionSymbol = pluginContext.findFunction(name)
        fun findProperty(name: String): IrPropertySymbol = pluginContext.findProperty(name)
        fun IrBuilderWithScope.call(function: IrSimpleFunctionSymbol, receiver: Any?, vararg parameters: Any?) = this.call(pluginContext, function, receiver, *parameters)
        fun IrBuilderWithScope.concat(vararg parameters: Any?) = this.irConcat(pluginContext, *parameters)
        val timeMarkClass = findClass("kotlin/time/TimeMark")
        val stringBuilderClass = findClass("kotlin/text/StringBuilder")
        val stringBuilderConstructor = stringBuilderClass.findConstructor("kotlin/text/StringBuilder()")
        val stringBuilderAppendIntFunc = stringBuilderClass.findFunction("kotlin/text/StringBuilder.append(Int)")
        val stringBuilderAppendLongFunc = stringBuilderClass.findFunction("kotlin/text/StringBuilder.append(Long)")
        val stringBuilderAppendStringFunc = stringBuilderClass.findFunction("kotlin/text/StringBuilder.append(String?)")
        val printlnFunc = findFunction("kotlin/io/println(String)")
        val debugFile = File("./DEBUG.txt")
        debugFile.delete()
        val rawSinkClass = findClass("kotlinx/io/RawSink")
        val pathConstructionFunc = findFunction("kotlinx/io/files/Path(?)")
        val systemFileSystem = findProperty("kotlinx/io/files/SystemFileSystem")
        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail
        val sinkFunc = systemFileSystemClass.findFunction("sink(*)")
        val bufferedFunc = findFunction("kotlinx/io/buffered: kotlinx/io/Sink")
        debugFile.appendText("1")
        debugFile.appendText(pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                Name.identifier("writeString")
            )
        ).joinToString(";") { it.owner.valueParameters.joinToString(",") { it.type.classFqName.toString() } })
        val writeStringFunc = pluginContext.findFunction("kotlinx/io/writeString(String, Int, Int)")
        val flushFunc = pluginContext.findFunction("kotlinx/io/Sink.flush")
        debugFile.appendText("2")
        val toStringFunc = pluginContext.findFunction("kotlin/toString")
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
            pluginContext.findClass("kotlin/random/Random.Default")
        val nextIntFunc = pluginContext.findFunction("kotlin/random/Random.Default.nextInt()")
        val randomNumber = pluginContext.irFactory.buildField {
            name = Name.identifier("_randNumber")
            type = pluginContext.irBuiltIns.intType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(call(pluginContext, nextIntFunc, randomDefaultObjectClass))
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
                    concat("./trace_${pluginContext.platform!!.presentableDescription}_", randomNumber, ".txt")
                )
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
                irExprBody(call(bufferedFunc, call(sinkFunc, systemFileSystem.owner, call(pathConstructionFunc, null, bufferedTraceFileName))))
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
                    concat("./symbols_${pluginContext.platform!!.presentableDescription}_", randomNumber, ".txt")
                )
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
                irExprBody(call(bufferedFunc, call(sinkFunc, systemFileSystem.owner, call(pathConstructionFunc, null, bufferedSymbolsFileName))))
            }
        }
        firstFile.declarations.add(bufferedSymbolsFileSink)
        bufferedSymbolsFileSink.parent = firstFile
        val methodMap = mutableMapOf<String, IrFunction>()
        val methodIdMap = mutableMapOf<String, Int>()
        var currMethodId = 0
        fun buildEnterMethodFunction(): IrFunction {
            val timeSourceMonotonicClass = pluginContext.findClass("kotlin/time/TimeSource.Monotonic")
            val funMarkNow = pluginContext.findFunction("kotlin/time/TimeSource.Monotonic.markNow")
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
                        +call(stringBuilderAppendStringFunc, stringBuilder, ">;")
                        +call(stringBuilderAppendIntFunc, stringBuilder, valueParameters[0])
                        +call(stringBuilderAppendStringFunc, stringBuilder, "\n")
                    } else {
                        +call(writeStringFunc, bufferedTraceFileSink, concat(">;", valueParameters[0], "\n"))
                    }
                    +irReturn(call(funMarkNow, timeSourceMonotonicClass))
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
                    val elapsedDuration = irTemporary(call(pluginContext, funElapsedNow, valueParameters[1]))
                    val elapsedMicrosProp: IrProperty = elapsedDuration.type.findProperty("inWholeMicroseconds").owner
                    val elapsedMicros = irTemporary(call(pluginContext, elapsedMicrosProp.getter!!, elapsedDuration))
                    if (STRINGBUILDER_MODE) {
                        +call(stringBuilderAppendStringFunc, stringBuilder, "<;")
                        +call(stringBuilderAppendIntFunc, stringBuilder, valueParameters[0])
                        +call(stringBuilderAppendStringFunc, stringBuilder, ";")
                        +call(stringBuilderAppendLongFunc, stringBuilder, elapsedMicros)
                        +call(stringBuilderAppendStringFunc, stringBuilder, "\n")
                    } else {
                        +call(writeStringFunc, bufferedTraceFileSink, concat("<;", valueParameters[0], ";", elapsedMicros, "\n"))
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
                +call(printlnFunc, null, bufferedTraceFileName)
                if (STRINGBUILDER_MODE) {
                    +call(writeStringFunc, bufferedTraceFileSink, call(toStringFunc, stringBuilder))
                }
                +call(flushFunc, bufferedTraceFileSink)
                +call(flushFunc, null, bufferedTraceFileSink)
                +call(printlnFunc, null, bufferedSymbolsFileName)
                +call(writeStringFunc, bufferedSymbolsFileSink, "{ " + methodIdMap.map { (name, id) -> id to name }
                    .sortedBy { (id, _) -> id }
                    .joinToString(",\n") { (id, name) -> "\"$id\": \"$name\"" } + " }")
                +call(flushFunc, bufferedSymbolsFileSink)
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
                startTime: IrVariable
            ) = irCall(exitFunc).apply {
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
                                        +irCallExitFunc(exitFunc, func, startTime)
                                        +expression.apply {
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
                                +irCallExitFunc(exitFunc, func, startTime)
                                +irThrow(irGet(catchVar))
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