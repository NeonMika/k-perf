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
    private val DEBUG = true

    @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        println("PerfMeasureExtension2.generate")
        messageCollector.report(
            CompilerMessageSeverity.STRONG_WARNING,
            "PerfMeasureExtension2.generate"
        )

        // #region local shortcuts
        fun findClass(name: String): IrClassSymbol = pluginContext.findClass(name)
        fun findFunction(name: String): IrSimpleFunctionSymbol = pluginContext.findFunction(name)
        fun IrClassSymbol.findFunction(name: String): IrSimpleFunctionSymbol = pluginContext.findFunction(name, this)
        fun IrClass.findFunction(name: String): IrSimpleFunctionSymbol = pluginContext.findFunction(name, this.symbol)
        fun IrClassSymbol.findConstructor(name: String): IrConstructorSymbol = pluginContext.findConstructor(name, this)
        fun IrClass.findConstructor(name: String): IrConstructorSymbol = pluginContext.findConstructor(name, this.symbol)

        fun IrBuilderWithScope.call(function: IrFunction, receiver : Any?, vararg parameters: Any?) = this.call(pluginContext, function, receiver, *parameters)
        fun IrBuilderWithScope.call(function: IrSimpleFunctionSymbol, receiver : Any?, vararg parameters: Any?) = this.call(pluginContext, function, receiver, *parameters)
        fun IrBuilderWithScope.concat(vararg parameters: Any?) = this.irConcat(pluginContext, *parameters)
        // #endregion local shortcuts

        if (DEBUG) {
            val flushFuncOld = pluginContext.referenceFunctions(
                CallableId(
                    FqName("kotlinx.io"),
                    FqName("Sink"),
                    Name.identifier("flush")
                )
            ).single()
            val flushFuncNew = pluginContext.findFunction("kotlinx/io/Sink.flush")
            assert(flushFuncOld == flushFuncNew)
        }

        val timeMarkClass: IrClassSymbol = pluginContext.findClass("kotlin/time/TimeMark") // Test Case 101

        if (DEBUG) {
            assert (timeMarkClass == (pluginContext.referenceClass(ClassId.fromString("kotlin/time/TimeMark"))!!))
        }
        // In JVM, StringBuilder is a type alias (see https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/)
        // In native and JS, StringBuilder is a class
        val stringBuilderClass: IrClassSymbol = pluginContext.findClass("kotlin/text/StringBuilder") // Test Case 102
        if (DEBUG) {
            val stringBuilderClassId = ClassId.fromString("kotlin/text/StringBuilder")
            val stringBuilderTypeAlias = pluginContext.referenceTypeAlias(stringBuilderClassId)
            assert(stringBuilderClass == (stringBuilderTypeAlias?.owner?.expandedType?.classOrFail?: pluginContext.referenceClass(stringBuilderClassId)!!)) // In native and JS, StringBuilder is a class
        }

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

        val stringBuilderConstructor = stringBuilderClass.findConstructor("kotlin/text/StringBuilder()") // Test Case 103
        val stringBuilderAppendIntFunc = stringBuilderClass.findFunction("kotlin/text/StringBuilder.append(Int)") // Test Case 104
        val stringBuilderAppendLongFunc = stringBuilderClass.findFunction("kotlin/text/StringBuilder.append(Long)") // Test Case 105
        val stringBuilderAppendStringFunc = stringBuilderClass.findFunction("kotlin/text/StringBuilder.append(String?)") // Test Case 106
        val printlnFunc = pluginContext.findFunction("kotlin/io/println(String)") // Test Case 107

        if (DEBUG) {
            // Wish: IrClass.find<XYZ> extension functions
            // Wish: IrClassSymbol.find<XYZ> extension functions
            // Wish: IrSymbolOwner.find<XYZ> extension functions

            // Wish: stringBuilderClass.findConstructor("()")
            val stringBuilderConstructorOld =
                stringBuilderClass.constructors.single { it.owner.valueParameters.isEmpty() }
            assert(stringBuilderConstructor == stringBuilderConstructorOld)

            // Wish: stringBuilderClass.findFunction("append(Int)")
            val stringBuilderAppendIntFuncOld =
                stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.intType }
            assert(stringBuilderAppendIntFunc == stringBuilderAppendIntFuncOld)

            // Wish: stringBuilderClass.findFunction("append(Long)")
            val stringBuilderAppendLongFuncOld =
                stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.longType }
            assert(stringBuilderAppendLongFunc == stringBuilderAppendLongFuncOld)

            // Wish: stringBuilderClass.findFunction("append(String?)")
            val stringBuilderAppendStringFuncOld =
                stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.stringType.makeNullable() }
            assert(stringBuilderAppendStringFunc == stringBuilderAppendStringFuncOld)

            // Wish:  findFunction("kotlin/io/println(String)")
            val printlnFuncOld =
                pluginContext.referenceFunctions(CallableId(FqName("kotlin.io"), Name.identifier("println"))).single {
                    it.owner.valueParameters.run { size == 1 && get(0).type == pluginContext.irBuiltIns.anyNType }
                }
            assert(printlnFunc == printlnFuncOld)
            assert(printlnFunc == pluginContext.findFunction("kotlin/io/println(kotlin/text/StringBuilder?)"))
            assert(printlnFunc != pluginContext.findFunction("kotlin/io/println(Int)"))
        }

        // Wish:  findFunction("kotlin/io/MyClass.fooFunc(kotlin/text/StringBuilder,Int?)") // Test Case 108
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

        val readLinesExtFunc = findFunction("kotlin/io/readLines")

//      val pathClass =
//          pluginContext.referenceClass(ClassId.fromString("kotlinx/io/files/Path"))!!
        val rawSinkClass = pluginContext.findClass("kotlinx/io/RawSink") // Test Case 109
        if (DEBUG) {
            assert(rawSinkClass == (pluginContext.referenceClass(ClassId.fromString("kotlinx/io/RawSink"))!!))
        }

        // Watch out, Path does not use constructors but functions to build
        val pathConstructionFunc = pluginContext.findFunction("kotlinx/io/files/Path(String)") // Test Case 110
        if (DEBUG) {
            assert(
                pathConstructionFunc == pluginContext.referenceFunctions(
                    CallableId(
                        FqName("kotlinx.io.files"),
                        Name.identifier("Path")
                    )
                ).single { it.owner.valueParameters.size == 1 })
        }

        val systemFileSystem = pluginContext.findProperty("kotlinx/io/files/SystemFileSystem") // Test Case 111
        if (DEBUG) {
            // Wish: findProperty("kotlinx/io/files/SystemFileSystem")
            assert(
                systemFileSystem == pluginContext.referenceProperties(
                    CallableId(
                        FqName("kotlinx.io.files"),
                        Name.identifier("SystemFileSystem")
                    )
                ).single()
            )
        }

        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail
        val sinkFunc = systemFileSystemClass.functions.single { it.owner.name.asString() == "sink" }
        val bufferedFunc = pluginContext.findFunction("kotlinx/io/buffered(): kotlinx/io/Sink") // Test Case 112
        if (DEBUG) {
            assert(
                bufferedFunc == (pluginContext.referenceFunctions(
                    CallableId(
                        FqName("kotlinx.io"),
                        Name.identifier("buffered")
                    )
                ).single { it.owner.extensionReceiverParameter!!.type == sinkFunc.owner.returnType })
            )
        }

        debugFile.appendText("1")
        debugFile.appendText(pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                Name.identifier("writeString")
            )
        ).joinToString(";") { it.owner.valueParameters.joinToString(",") { it.type.classFqName.toString() } })

        val writeStringFunc = pluginContext.findFunction("kotlinx/io/writeString(String, Int, Int)") // Test Case 113
        if (DEBUG) {
            // Wish: findFunction("kotlinx.io/writeString(String,Int,Int)")
            assert(
                writeStringFunc == pluginContext.referenceFunctions(
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
        }

        val flushFunc = pluginContext.findFunction("kotlinx/io/Sink.flush") // Test Case 114
        if (DEBUG) {
            assert(
                flushFunc == pluginContext.referenceFunctions(
                    CallableId(
                        FqName("kotlinx.io"),
                        FqName("Sink"),
                        Name.identifier("flush")
                    )
                ).single()
            )
        }

        debugFile.appendText("2")
        val toStringFunc = pluginContext.findFunction("kotlin/toString()") // Test Case 115
        if (DEBUG) {
            assert(
                toStringFunc == pluginContext.referenceFunctions(
                    CallableId(
                        FqName("kotlin"),
                        Name.identifier("toString")
                    )
                ).single()
            )
        }
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

        // Todo: umwadeln!
        val stringBuilder: IrField = pluginContext.irFactory.buildField { // Test Case 201
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
        if (DEBUG) {
            assert(stringBuilder.dump() == pluginContext.irFactory.buildField {
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
            }.dump())
        }
        firstFile.declarations.add(stringBuilder)
        stringBuilder.parent = firstFile


        val currentMillis = System.currentTimeMillis()

        val randomDefaultObjectClass = pluginContext.findClass("kotlin/random/Random.Default") // Test Case 116
        if (DEBUG) {
            assert(randomDefaultObjectClass == pluginContext.referenceClass(ClassId.fromString("kotlin/random/Random.Default"))!!)
        }

        val nextIntFunc = pluginContext.findFunction("kotlin/random/Random.Default.nextInt()") // Test Case 117
        if (DEBUG) {
            assert(
                nextIntFunc == pluginContext.referenceFunctions(
                    CallableId(
                        FqName("kotlin.random"),
                        FqName("Random.Default"),
                        Name.identifier("nextInt")
                    )
                ).single {
                    it.owner.valueParameters.isEmpty()
                })
        }

        // Todo: umwadeln!
        val randomNumber = pluginContext.irFactory.buildField { // Test Case 202
            name = Name.identifier("_randNumber")
            type = pluginContext.irBuiltIns.intType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(this.call(pluginContext, nextIntFunc, randomDefaultObjectClass))
            }
        }

        if (DEBUG) {
            assert(randomNumber.dump() == pluginContext.irFactory.buildField {
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
            }.dump())
        }

        firstFile.declarations.add(randomNumber)
        randomNumber.parent = firstFile

        val bufferedTraceFileName = pluginContext.irFactory.buildField { // Test Case 203
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

        if (DEBUG) {
            assert(bufferedTraceFileName.dump() == pluginContext.irFactory.buildField {
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
                        }
                    )
                }
            }.dump())
        }
        firstFile.declarations.add(bufferedTraceFileName)
        bufferedTraceFileName.parent = firstFile

        val bufferedTraceFileSink = pluginContext.irFactory.buildField { // Test Case 204
            name = Name.identifier("_bufferedTraceFileSink")
            type = rawSinkClass.defaultType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(call(bufferedFunc, call(sinkFunc, systemFileSystem.owner, call(pathConstructionFunc, null, bufferedTraceFileName))))
            }
        }
        if (DEBUG) {
            assert(bufferedTraceFileSink.dump() == pluginContext.irFactory.buildField {
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
            }.dump())
        }
        firstFile.declarations.add(bufferedTraceFileSink)
        bufferedTraceFileSink.parent = firstFile

        val bufferedSymbolsFileName = pluginContext.irFactory.buildField { // Test Case 205
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
        if (DEBUG) {
            assert(bufferedSymbolsFileName.dump() == pluginContext.irFactory.buildField {
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
            }.dump())
        }
        firstFile.declarations.add(bufferedSymbolsFileName)
        bufferedSymbolsFileName.parent = firstFile

        val bufferedSymbolsFileSink = pluginContext.irFactory.buildField { // Test Case 206
            name = Name.identifier("_bufferedSymbolsFileSink")
            type = rawSinkClass.defaultType
            isFinal = false
            isStatic = true
        }.apply {
            this.initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(call(bufferedFunc, call(sinkFunc, systemFileSystem.owner, call(pathConstructionFunc, null, bufferedSymbolsFileName))))
            }
        }
        if (DEBUG) {
            assert (bufferedSymbolsFileSink.dump() == pluginContext.irFactory.buildField {
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
            }.dump())
        }
        firstFile.declarations.add(bufferedSymbolsFileSink)
        bufferedSymbolsFileSink.parent = firstFile

        val methodMap = mutableMapOf<String, IrFunction>()
        val methodIdMap = mutableMapOf<String, Int>()
        var currMethodId = 0

        fun buildEnterMethodFunction(): IrFunction {
            val timeSourceMonotonicClass: IrClassSymbol = pluginContext.findClass("kotlin/time/TimeSource.Monotonic") // Test Case 118
            if (DEBUG) {
                assert(timeSourceMonotonicClass == pluginContext.referenceClass(ClassId.fromString("kotlin/time/TimeSource.Monotonic"))!!)
            }

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
                ).single()
            */

            /* val funMarkNowViaClass = classMonotonic.functions.find { it.owner.name.asString() == "markNow" }!! */

            val funMarkNow = pluginContext.findFunction("kotlin/time/TimeSource.Monotonic.markNow") // Test Case 119
            if (DEBUG) {
                assert(
                    funMarkNow == (pluginContext.referenceFunctions(
                        CallableId(
                            FqName("kotlin.time"),
                            FqName("TimeSource.Monotonic"),
                            Name.identifier("markNow")
                        )
                    ).single())
                )
            }

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
                    })
                    */
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
                        +call(stringBuilderAppendStringFunc, stringBuilder, ">;")
                        +call(stringBuilderAppendIntFunc, stringBuilder, valueParameters[0])
                        +call(stringBuilderAppendStringFunc, stringBuilder, "\n")
                    } else {
                        +call(writeStringFunc, bufferedTraceFileSink, concat(">;", valueParameters[0], "\n"))
                    }

                    if (DEBUG) {
                        assert(
                            call(stringBuilderAppendStringFunc, stringBuilder, ">;").dump() ==
                                irCall(stringBuilderAppendStringFunc).apply {
                                    dispatchReceiver = irGetField(null, stringBuilder)
                                    putValueArgument(0, irString(">;"))
                                }.dump()
                        )
                        assert(
                            call(stringBuilderAppendIntFunc, stringBuilder, valueParameters[0]).dump() ==
                                irCall(stringBuilderAppendIntFunc).apply {
                                    dispatchReceiver = irGetField(null, stringBuilder)
                                    putValueArgument(0, irGet(valueParameters[0]))
                                }.dump()
                        )
                        assert(
                            call(stringBuilderAppendStringFunc, stringBuilder, "\n").dump() ==
                                irCall(stringBuilderAppendStringFunc).apply {
                                    dispatchReceiver = irGetField(null, stringBuilder)
                                    putValueArgument(0, irString("\n"))
                                }.dump()
                        )

                        assert(
                            call(writeStringFunc, bufferedTraceFileSink, concat(">;", valueParameters[0], "\n")).dump() ==
                                irCall(writeStringFunc).apply {
                                    extensionReceiver = irGetField(null, bufferedTraceFileSink)
                                    // Wish: irConcat(">;", valueParameters[0], "\n")
                                    putValueArgument(0, irConcat().apply {
                                        addArgument(irString(">;"))
                                        addArgument(irGet(valueParameters[0]))
                                        addArgument(irString("\n"))
                                    })
                                }.dump()
                        )
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
                    type = pluginContext.irBuiltIns.stringType
                    */
                    name = Name.identifier("methodId")
                    type = pluginContext.irBuiltIns.intType
                }
                addValueParameter {
                    name = Name.identifier("startTime")
                    type = timeMarkClass.defaultType
                }
                /*
                addValueParameter {
                    name = Name.identifier("result")
                    type = pluginContext.irBuiltIns.anyNType
                }
                */

                body = DeclarationIrBuilder(pluginContext, symbol, startOffset, endOffset).irBlockBody {
                    // Duration
                    val elapsedDuration = irTemporary(irCall(funElapsedNow).apply {
                        dispatchReceiver = irGet(valueParameters[1])
                    })
                    // Wish: elapsedDuration.type.findProperty("inWholeMicroseconds")
                    val elapsedMicrosProp = elapsedDuration.type.getClass()!!.findProperty("inWholeMicroseconds").owner

                    if (DEBUG) {
                        assert(elapsedMicrosProp == elapsedDuration.type.getClass()!!.properties.single { it.name.asString() == "inWholeMicroseconds" })
                    }

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
                        +call(stringBuilderAppendStringFunc, stringBuilder, "<;")
                        +call(stringBuilderAppendIntFunc, stringBuilder, valueParameters[0])
                        +call(stringBuilderAppendStringFunc, stringBuilder, ";")
                        +call(stringBuilderAppendLongFunc, stringBuilder, elapsedMicros)
                        +call(stringBuilderAppendStringFunc, stringBuilder, "\n")
                    } else {
                        +call(writeStringFunc, bufferedTraceFileSink, concat("<;", valueParameters[0], ";", elapsedMicros, "\n"))
                    }

                    if (DEBUG) {
                        // Wish: stringBuilder.call(stringBuilderAppendStringFunc, "<;")
                        assert(call(stringBuilderAppendStringFunc, stringBuilder, "<;").dump() == irCall(stringBuilderAppendStringFunc).apply {
                            dispatchReceiver = irGetField(null, stringBuilder)
                            putValueArgument(0, irString("<;"))
                        }.dump())

                        // Wish: stringBuilder.call(stringBuilderAppendIntFunc, valueParameters[0])
                        assert(call(stringBuilderAppendIntFunc, stringBuilder, valueParameters[0]).dump() == irCall(stringBuilderAppendIntFunc).apply {
                            dispatchReceiver = irGetField(null, stringBuilder)
                            putValueArgument(0, irGet(valueParameters[0]))
                        }.dump())

                        // Wish: stringBuilder.call(stringBuilderAppendStringFunc, ";")
                        assert(call(stringBuilderAppendStringFunc, stringBuilder, ";").dump() == irCall(stringBuilderAppendStringFunc).apply {
                            dispatchReceiver = irGetField(null, stringBuilder)
                            putValueArgument(0, irString(";"))
                        }.dump())

                        // Wish: stringBuilder.call(stringBuilderAppendStringFunc, ";")
                        assert(call(stringBuilderAppendLongFunc, stringBuilder, elapsedMicros).dump() == irCall(stringBuilderAppendLongFunc).apply {
                            dispatchReceiver = irGetField(null, stringBuilder)
                            putValueArgument(0, irGet(elapsedMicros))
                        }.dump())

                        assert(call(stringBuilderAppendStringFunc, stringBuilder, "\n").dump() == irCall(stringBuilderAppendStringFunc).apply {
                            dispatchReceiver = irGetField(null, stringBuilder)
                            putValueArgument(0, irString("\n"))
                        }.dump())

                        assert(call(writeStringFunc, bufferedTraceFileSink, concat("<;", valueParameters[0], ";", elapsedMicros, "\n")).dump() == irCall(writeStringFunc).apply {
                            extensionReceiver = irGetField(null, bufferedTraceFileSink)
                            putValueArgument(0, irConcat().apply {
                                addArgument(irString("<;"))
                                addArgument(irGet(valueParameters[0]))
                                addArgument(irString(";"))
                                addArgument(irGet(elapsedMicros))
                                addArgument(irString("\n"))
                            })
                        }.dump())
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
            } else {
            */
            return DeclarationIrBuilder(pluginContext, func.symbol).irBlock {
                +call(printlnFunc, null, bufferedTraceFileName)
                /*
                +irCall(printlnFunc).apply {
                    putValueArgument(0, irGetField(null, bufferedTraceFileName))
                }
                */

                if (STRINGBUILDER_MODE) {
                    +call(writeStringFunc, bufferedTraceFileSink, call(toStringFunc, stringBuilder))
                    /*
                    +irCall(writeStringFunc).apply {
                        extensionReceiver = irGetField(null, bufferedTraceFileSink)
                        putValueArgument(0, irCall(toStringFunc).apply {
                            extensionReceiver = irGetField(null, stringBuilder)
                        })
                    }
                    */
                }

                +call(flushFunc, bufferedTraceFileSink)
                // ☼ TODO??
                //Wish: flushFunc.withThis(bufferedTraceFileSink).call()
                //flushFunc.withThis(bufferedTraceFileSink).call()
                /*
                +irCall(flushFunc).apply {
                    dispatchReceiver = irGetField(null, bufferedTraceFileSink)
                }
                */

                // Wish: printlnFunc.call(bufferedSymbolsFileName)
                +call(printlnFunc, null, bufferedSymbolsFileName)
                /*
                +irCall(printlnFunc).apply {
                    putValueArgument(
                        0, irGetField(null, bufferedSymbolsFileName)
                    )
                }
                */

                +call(writeStringFunc, bufferedSymbolsFileSink, "{ " + methodIdMap.map { (name, id) -> id to name }
                    .sortedBy { (id, _) -> id }
                    .joinToString(",\n") { (id, name) -> "\"$id\": \"$name\"" } + " }")
                /*
                +irCall(writeStringFunc).apply {
                    extensionReceiver = irGetField(null, bufferedSymbolsFileSink)
                    putValueArgument(0, irString("{ " + methodIdMap.map { (name, id) -> id to name }
                        .sortedBy { (id, _) -> id }
                        .joinToString(",\n") { (id, name) -> "\"$id\": \"$name\"" } + " }"))
                }
                */

                +call(flushFunc, bufferedSymbolsFileSink)
                /*
                +irCall(flushFunc).apply {
                    dispatchReceiver = irGetField(null, bufferedSymbolsFileSink)
                }
                */

                if (DEBUG) {
                    assert(
                        call(printlnFunc, null, bufferedTraceFileName).dump() ==
                            irCall(printlnFunc).apply {
                                putValueArgument(0, irGetField(null, bufferedTraceFileName))
                            }.dump()
                    )

                    assert(
                        call(writeStringFunc, bufferedTraceFileSink, call(toStringFunc, stringBuilder)).dump() ==
                            irCall(writeStringFunc).apply {
                                extensionReceiver = irGetField(null, bufferedTraceFileSink)
                                putValueArgument(0, irCall(toStringFunc).apply {
                                    extensionReceiver = irGetField(null, stringBuilder)
                                })
                            }.dump()
                    )

                    //Wish: flushFunc.withThis(bufferedTraceFileSink).call()
                    assert(
                        call(flushFunc, bufferedTraceFileSink).dump() ==
                            irCall(flushFunc).apply {
                                dispatchReceiver = irGetField(null, bufferedTraceFileSink)
                            }.dump()
                    )

                    // Wish: printlnFunc.call(bufferedSymbolsFileName)
                    assert(
                        call(printlnFunc, null, bufferedSymbolsFileName).dump() ==
                            irCall(printlnFunc).apply {
                                putValueArgument(
                                    0, irGetField(null, bufferedSymbolsFileName)
                                )
                            }.dump()
                    )

                    assert(
                        call(
                            writeStringFunc,
                            bufferedSymbolsFileSink,
                            "{ " + methodIdMap.map { (name, id) -> id to name }
                                .sortedBy { (id, _) -> id }
                                .joinToString(",\n") { (id, name) -> "\"$id\": \"$name\"" } + " }").dump() ==
                                irCall(writeStringFunc).apply {
                                    extensionReceiver = irGetField(null, bufferedSymbolsFileSink)
                                    putValueArgument(0, irString("{ " + methodIdMap.map { (name, id) -> id to name }
                                        .sortedBy { (id, _) -> id }
                                        .joinToString(",\n") { (id, name) -> "\"$id\": \"$name\"" } + " }"))
                                }.dump()
                    )

                    assert(
                        call(flushFunc, bufferedSymbolsFileSink).dump() ==
                            irCall(flushFunc).apply {
                                dispatchReceiver = irGetField(null, bufferedSymbolsFileSink)
                            }.dump()
                    )
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