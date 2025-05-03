package at.ssw.compilerplugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.addArgument
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.util.collectionUtils.concat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.abs

class KPerfMeasureCompilerPluginExtensionsTest {
    companion object {
        var random = kotlin.random.Random(0)

        @Language("kotlin")
        private val testCodes = listOf("""
        annotation class MyAnnotation

        fun main() {
          val v1 = 5
          val addRes = v1 + 17
          val threeDots = ".".repeat(3)
          val str = debug() + " Test!"
          output(str)
          a()
          val bRes = try { b() } catch (t: Throwable) { t.printStackTrace() }
        }

        @MyAnnotation
        fun debug() = "Hello, World!"

        fun output(str: String, builder : StringBuilder? = null) {
          val pr : (String) -> Unit = if(builder == null) ::print else builder::append
          pr(str)
        }

        fun a() {
            repeat(5) {
                println("a is a unit method and prints this")
            }
        }

        fun b() : Int {
            a()
            return 100 / 0
        }

        fun greet(greeting: String = "Hello", name: String = "World"): String {
          println("⇢ greet(greeting=${'$'}greeting, name=${'$'}name)")
          val startTime = kotlin.time.TimeSource.Monotonic.markNow()
          println("⇠ greet [${'$'}{startTime.elapsedNow()}] = threw RuntimeException")
          throw RuntimeException("Testexception")
          try {
            val result = "${'$'}{'$'}greeting, ${'$'}{'$'}name!"
            println("⇠ greet [${'$'}{startTime.elapsedNow()}] = ${'$'}result")
            return result
          } catch (t: Throwable) {
            println("⇠ greet [${'$'}{startTime.elapsedNow()}] = ${'$'}t")
            throw t
          }
        }
        """, """
        fun main() {
          sayHello()
          sayHello("Hi", "SSP")
        }

        fun sayHello(greeting: String = "Hello", name: String = "World") {
            val result = "${'$'}greeting, ${'$'}name!"
            println(result)
        }
        """)

        // language="kotlin", prefix="override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) { val actual: String; val expected: String", suffix="}"
        private val testCasesFind = listOf("""
        // 101

        actual = pluginContext.findClass("kotlin/time/TimeMark")
        expected = pluginContext.referenceClass(ClassId.fromString("kotlin/time/TimeMark"))!!
    """, """
        // 102
        
        actual = findClass("kotlin/text/StringBuilder")
        val stringBuilderClassId = ClassId.fromString("kotlin/text/StringBuilder")
        // In JVM, StringBuilder is a type alias (see https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/)
        val stringBuilderTypeAlias = pluginContext.referenceTypeAlias(stringBuilderClassId)
        expected = stringBuilderTypeAlias?.owner?.expandedType?.classOrFail?: pluginContext.referenceClass(stringBuilderClassId)!!
    """, """
        // 103

        // Wish: IrClass.find<XYZ> extension functions
        // Wish: IrClassSymbol.find<XYZ> extension functions
        // Wish: IrSymbolOwner.find<XYZ> extension functions
        val stringBuilderClass: IrClassSymbol = pluginContext.findClass("kotlin/text/StringBuilder")

        // Wish: stringBuilderClass.findConstructor("()")
        actual = stringBuilderClass.findConstructor("kotlin/text/StringBuilder()")
        expected = stringBuilderClass.constructors.single { it.owner.valueParameters.isEmpty() }
    """, """
        // 104

        val stringBuilderClass: IrClassSymbol = pluginContext.findClass("kotlin/text/StringBuilder")
        
        // Wish: stringBuilderClass.findFunction("append(Int)")
        actual = stringBuilderClass.findFunction("kotlin/text/StringBuilder.append(Int)")
        expected = stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.intType }
    """, """
        // 105

        val stringBuilderClass: IrClassSymbol = pluginContext.findClass("kotlin/text/StringBuilder")

        // Wish: stringBuilderClass.findFunction("append(Long)")
        actual = stringBuilderClass.findFunction("kotlin/text/StringBuilder.append(Long)")
        expected = stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.longType }
    """, """
        // 106

        val stringBuilderClass: IrClassSymbol = findClass("kotlin/text/StringBuilder")

        actual = stringBuilderClass.findFunction("append(String?)")
        expected = stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.stringType.makeNullable() }
    """, """
        // 107

        actual = findFunction("kotlin/io/println(Any?)")
        expected = pluginContext.referenceFunctions(CallableId(FqName("kotlin.io"), Name.identifier("println"))).single {
            it.owner.valueParameters.run { size == 1 && get(0).type == pluginContext.irBuiltIns.anyNType }
        }

        assert(actual == findFunction("kotlin/io/println(kotlin/text/StringBuilder?)"))
        assert(actual != findFunction("kotlin/io/println(Int)"))
    """, """
        // 107a
        error = "org.opentest4j.AssertionFailedError(\"<FUN IR_EXTERNAL_DECLARATION_STUB name:println visibility:public modality:FINAL <> (message:kotlin.Int) returnType:kotlin.Unit [inline]>\")"

        actual = findFunction("kotlin/io/println(Any?)")
        expected = findFunction("kotlin/io/println(Int)")

        org.junit.jupiter.api.Assertions.assertEquals(actual, expected)
    """, """
        // 107b

        actual = findFunction("kotlin/io/println(Any?)")
        expected = findFunction("kotlin/io/println(Int)")

        actual = actual == expected
        expected = false
    """, """
        // 107c

        error("Was wollte ich da nochmal machen?")

        actual = findClass("kotlin/text/StringBuilder").findFunction("append(String?)")
        expected = findFunction("kotlin/io/println(kotlin/text/String?)")
    """, """
        // 107c

        error("Was wollte ich da nochmal machen?")

        actual = findClass("kotlin/text/StringBuilder").findFunction("append(String?)")
        expected = findFunction("kotlin/io/println(kotlin/text/String?)")
    """, """
        // 108

        // Wish:  findFunction("kotlin/io/MyClass.fooFunc(kotlin/text/StringBuilder,Int?)")
        actual = findFunction("at/ssw/compilerplugin/KPerfMeasureCompilerPluginExtensionsTest.MyClass.fooFunc(kotlin/text/StringBuilder,Int?)")
        
        expected = pluginContext.referenceFunctions(CallableId(FqName("at.ssw.compilerplugin"), FqName("KPerfMeasureCompilerPluginExtensionsTest.MyClass"), Name.identifier("fooFunc"))).single {
            func -> func.owner.valueParameters.size == 2 &&
                func.owner.valueParameters[0].type == findClass("kotlin/text/StringBuilder").defaultType &&
                func.owner.valueParameters[1].type == pluginContext.irBuiltIns.intType.makeNullable()
        }
        // Wish:  findClass("kotlin/io/MyClass").findFunction("fooFunc(kotlin/text/StringBuilder,Int?)")
        /* pluginContext.referenceClass(ClassId.fromString("kotlin/io/MyClass")).functions.single {
            // ...
        } */
        //expected = findFunction("kotlin/io/MyClass.fooFunc(kotlin/text/StringBuilder,Int?)")
    """, """
        // 108a

        // Wish:  findFunction("kotlin/io/MyClass.fooFunc(kotlin/text/StringBuilder,Int?)")
        actual = findClass("at/ssw/compilerplugin/KPerfMeasureCompilerPluginExtensionsTest.MyClass").findFunction("fooFunc(kotlin/text/StringBuilder,Int?)")
        
        expected = pluginContext.referenceFunctions(CallableId(FqName("at.ssw.compilerplugin"), FqName("KPerfMeasureCompilerPluginExtensionsTest.MyClass"), Name.identifier("fooFunc"))).single {
            func -> func.owner.valueParameters.size == 2 &&
                func.owner.valueParameters[0].type == findClass("kotlin/text/StringBuilder").defaultType &&
                func.owner.valueParameters[1].type == pluginContext.irBuiltIns.intType.makeNullable()
        }
    """, """
        // 109

        actual = pluginContext.findClass("kotlinx/io/RawSink")
        expected = pluginContext.referenceClass(ClassId.fromString("kotlinx/io/RawSink"))!!
    """, """
        // 110

        // Watch out, Path does not use constructors but functions to build
        actual = pluginContext.findFunction("kotlinx/io/files/Path(?)")
        expected = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io.files"),
                Name.identifier("Path")
            )
        ).single { it.owner.valueParameters.size == 1 }
    """, """
        // 111

        actual = findProperty("kotlinx/io/files/SystemFileSystem")
        expected = pluginContext.referenceProperties(
            CallableId(
                FqName("kotlinx.io.files"),
                Name.identifier("SystemFileSystem")
            )
        ).single()
    """, """
        // 112
        
        actual = findFunction("kotlinx/io/buffered: kotlinx/io/Sink")

        val systemFileSystem = pluginContext.referenceProperties(
            CallableId(
                FqName("kotlinx.io.files"),
                Name.identifier("SystemFileSystem")
            )
        ).single()
        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail
        val sinkFunc = systemFileSystemClass.functions.single { it.owner.name.asString() == "sink" }
        expected = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                Name.identifier("buffered")
            )
        ).single { it.owner.extensionReceiverParameter!!.type == sinkFunc.owner.returnType }
    """, """
        // 112a

        actual = findFunction("kotlinx/io/buffered(*): kotlinx/io/Sink")

        //val systemFileSystem = findProperty("kotlinx/io/files/SystemFileSystem")
        val systemFileSystem = pluginContext.referenceProperties(
            CallableId(
                FqName("kotlinx.io.files"),
                Name.identifier("SystemFileSystem")
            )
        ).single()
        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail
        val sinkFunc = systemFileSystemClass.functions.single { it.owner.name.asString() == "sink" }
        expected = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                Name.identifier("buffered")
            )
        ).single { it.owner.extensionReceiverParameter!!.type == sinkFunc.owner.returnType }
    """, """
        // 112b

        actual = findFunction("kotlinx/io/buffered(?): kotlinx/io/Sink")

        val systemFileSystem = pluginContext.referenceProperties(
            CallableId(
                FqName("kotlinx.io.files"),
                Name.identifier("SystemFileSystem")
            )
        ).single()
        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail
        val sinkFunc = systemFileSystemClass.functions.single { it.owner.name.asString() == "sink" }
        expected = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                Name.identifier("buffered")
            )
        ).single { it.owner.extensionReceiverParameter!!.type == sinkFunc.owner.returnType }
    """, """
        // 113

        // Wish: findFunction("kotlinx.io/writeString(String,Int,Int)")
        actual = pluginContext.findFunction("kotlinx/io/writeString(String, Int, Int)")
        expected = pluginContext.referenceFunctions(
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
    """, """
        // 114

        actual = pluginContext.findFunction("kotlinx/io/Sink.flush")
        expected = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlinx.io"),
                FqName("Sink"),
                Name.identifier("flush")
            )
        ).single()
    """, """
        // 115

        actual = pluginContext.findFunction("kotlin/toString(*)")
        expected = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlin"),
                Name.identifier("toString")
            )
        ).single()
    """, """
        // 116

        actual = pluginContext.findClass("kotlin/random/Random.Default")
        expected = pluginContext.referenceClass(ClassId.fromString("kotlin/random/Random.Default"))!!
    """, """
        // 117

        actual = pluginContext.findFunction("kotlin/random/Random.Default.nextInt()")
        expected = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlin.random"),
                FqName("Random.Default"),
                Name.identifier("nextInt")
            )
        ).single {
            it.owner.valueParameters.isEmpty()
        }
    """, """
        // 118

        actual = pluginContext.findClass("kotlin/time/TimeSource.Monotonic")
        expected = pluginContext.referenceClass(ClassId.fromString("kotlin/time/TimeSource.Monotonic"))!!
    """, """
        // 119

        actual = pluginContext.findFunction("kotlin/time/TimeSource.Monotonic.markNow")
        expected = pluginContext.referenceFunctions(
            CallableId(
                FqName("kotlin.time"),
                FqName("TimeSource.Monotonic"),
                Name.identifier("markNow")
            )
        ).single()
    """, """
        // 120

        val systemFileSystem = pluginContext.findProperty("kotlinx/io/files/SystemFileSystem")
        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail

        actual = systemFileSystemClass.findFunction("sink")
        expected = systemFileSystemClass.functions.single { it.owner.name.asString() == "sink" }
    """, """
        // 120a

        val systemFileSystem = pluginContext.findProperty("kotlinx/io/files/SystemFileSystem")
        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail

        actual = systemFileSystemClass.findFunction("sink(*)")
        expected = systemFileSystemClass.functions.single { it.owner.name.asString() == "sink" }
    """, """
        // 120b

        val systemFileSystem = pluginContext.findProperty("kotlinx/io/files/SystemFileSystem")
        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail

        actual = systemFileSystemClass.findFunction("sink(?)")
        expected = systemFileSystemClass.findFunction("sink(*)")
    """, """
        // 120c

        val systemFileSystem = pluginContext.findProperty("kotlinx/io/files/SystemFileSystem")
        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail

        actual = systemFileSystemClass.findFunction("sink(?, ?)")
        expected = systemFileSystemClass.findFunction("sink(?)")
    """, """
        // 120d

        val systemFileSystem = pluginContext.findProperty("kotlinx/io/files/SystemFileSystem")
        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail

        actual = systemFileSystemClass.findFunction("sink(?, *)")
        expected = systemFileSystemClass.findFunction("sink(?)")
    """, """
        // 120e

        val systemFileSystem = pluginContext.findProperty("kotlinx/io/files/SystemFileSystem")
        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail
        
        actual = systemFileSystemClass.findFunction("sink(kotlinx/io/files/Path, Boolean)")
        expected = systemFileSystemClass.findFunction("sink(kotlinx/io/files/Path)")
    """)

        // language="kotlin", prefix="override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) { val actual: String; val expected: String", suffix="}"
        private val testCasesBuild = listOf("""
        // 201

        val firstFile = moduleFragment.files[0]
        val stringBuilderClass: IrClassSymbol = pluginContext.findClass("kotlin/text/StringBuilder")
        val stringBuilderConstructor = stringBuilderClass.findConstructor("kotlin/text/StringBuilder()")
        // ☼ Todo: umwadeln!
        actual = pluginContext.irFactory.buildField {
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
        }.dump()
        expected = pluginContext.irFactory.buildField {
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
        }.dump()
    """, """
        // 202

        val firstFile = moduleFragment.files[0]
        val nextIntFunc = pluginContext.findFunction("kotlin/random/Random.Default.nextInt()")
        val bufferedFunc = pluginContext.findFunction("kotlinx/io/buffered(): kotlinx/io/Sink")
        val randomDefaultObjectClass = pluginContext.findClass("kotlin/random/Random.Default")
        // ☼ Todo: umwadeln!
        actual = pluginContext.irFactory.buildField {
            name = Name.identifier("_randNumber")
            type = pluginContext.irBuiltIns.intType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(this.call(pluginContext, nextIntFunc, randomDefaultObjectClass))
            }
        }.dump()
        expected = pluginContext.irFactory.buildField {
            name = Name.identifier("_randNumber")
            type = pluginContext.irBuiltIns.intType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(this.call(pluginContext, nextIntFunc, randomDefaultObjectClass))
            }
        }.dump()
    """, """
        // 203

        val firstFile = moduleFragment.files[0]
        val nextIntFunc = pluginContext.findFunction("kotlin/random/Random.Default.nextInt()")
        val randomDefaultObjectClass = pluginContext.findClass("kotlin/random/Random.Default")
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

        actual = pluginContext.irFactory.buildField {
            name = Name.identifier("_bufferedTraceFileName")
            type = pluginContext.irBuiltIns.stringType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(
                    concat("./trace_$\{pluginContext.platform!!.presentableDescription}_", randomNumber, ".txt")
                )
            }
        }.dump()
        expected = pluginContext.irFactory.buildField {
                name = Name.identifier("_bufferedTraceFileName")
                type = pluginContext.irBuiltIns.stringType
                isFinal = false
                isStatic = true
            }.apply {
                initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                    irExprBody(
                        // Wish: irConcat("./trace_$\{pluginContext.platform!!.presentableDescription}_", randomNumber, ".txt")
                        irConcat().apply {
                            addArgument(irString("./trace_$\{pluginContext.platform!!.presentableDescription}_"))
                            // TODO: use kotlinx.datetime.Clock.System.now()
                            addArgument(irGetField(null, randomNumber))
                            addArgument(irString(".txt"))
                        }
                    )
                }
            }.dump()
    """, """
        // 204

        val firstFile = moduleFragment.files[0]
        val systemFileSystem = pluginContext.findProperty("kotlinx/io/files/SystemFileSystem")
        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail
        val sinkFunc = systemFileSystemClass.findFunction("sink()")
        val bufferedFunc = pluginContext.findFunction("kotlinx/io/buffered(): kotlinx/io/Sink")
        val randomDefaultObjectClass = pluginContext.findClass("kotlin/random/Random.Default")
        val rawSinkClass = pluginContext.findClass("kotlinx/io/RawSink")
        val pathConstructionFunc = pluginContext.findFunction("kotlinx/io/files/Path(String)")
        val nextIntFunc = pluginContext.findFunction("kotlin/random/Random.Default.nextInt()")

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

        val bufferedTraceFileName = pluginContext.irFactory.buildField { // Test Case 203
            name = Name.identifier("_bufferedTraceFileName")
            type = pluginContext.irBuiltIns.stringType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(
                    concat("./trace_$\{pluginContext.platform!!.presentableDescription}_", randomNumber, ".txt")
                )
            }
        }

        actual = pluginContext.irFactory.buildField {
            name = Name.identifier("_bufferedTraceFileSink")
            type = rawSinkClass.defaultType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(call(bufferedFunc, call(sinkFunc, systemFileSystem.owner, call(pathConstructionFunc, null, bufferedTraceFileName))))
            }
        }.dump()
        expected = pluginContext.irFactory.buildField {
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
        }.dump()
    """, """
        // 205

        val firstFile = moduleFragment.files[0]
        val nextIntFunc = pluginContext.findFunction("kotlin/random/Random.Default.nextInt()")
        val randomDefaultObjectClass = pluginContext.findClass("kotlin/random/Random.Default")

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

        actual = pluginContext.irFactory.buildField {
            name = Name.identifier("_bufferedSymbolsFileName")
            type = pluginContext.irBuiltIns.stringType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(
                    concat("./symbols_$\{pluginContext.platform!!.presentableDescription}_", randomNumber, ".txt")
                )
            }
        }.dump()
        expected = pluginContext.irFactory.buildField {
            name = Name.identifier("_bufferedSymbolsFileName")
            type = pluginContext.irBuiltIns.stringType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(
                    irConcat().apply {
                        addArgument(irString("./symbols_$\{pluginContext.platform!!.presentableDescription}_"))
                        // TODO: use kotlinx.datetime.Clock.System.now()
                        addArgument(irGetField(null, randomNumber))
                        addArgument(irString(".txt"))
                    })
            }
        }.dump()
    """, """
        // 206

        val firstFile = moduleFragment.files[0]
        val rawSinkClass = pluginContext.findClass("kotlinx/io/RawSink")
        val systemFileSystem = pluginContext.findProperty("kotlinx/io/files/SystemFileSystem")
        val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail
        val sinkFunc = systemFileSystemClass.findFunction("sink()")
        val bufferedFunc = pluginContext.findFunction("kotlinx/io/buffered(): kotlinx/io/Sink")
        val pathConstructionFunc = pluginContext.findFunction("kotlinx/io/files/Path(String)")
        val nextIntFunc = pluginContext.findFunction("kotlin/random/Random.Default.nextInt()")
        val randomDefaultObjectClass = pluginContext.findClass("kotlin/random/Random.Default")

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

        val bufferedSymbolsFileName = pluginContext.irFactory.buildField { // Test Case 205
            name = Name.identifier("_bufferedSymbolsFileName")
            type = pluginContext.irBuiltIns.stringType
            isFinal = false
            isStatic = true
        }.apply {
            initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(
                    concat("./symbols_$\{pluginContext.platform!!.presentableDescription}_", randomNumber, ".txt")
                )
            }
        }

        actual = pluginContext.irFactory.buildField {
            name = Name.identifier("_bufferedSymbolsFileSink")
            type = rawSinkClass.defaultType
            isFinal = false
            isStatic = true
        }.apply {
            this.initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
                irExprBody(call(bufferedFunc, call(sinkFunc, systemFileSystem.owner, call(pathConstructionFunc, null, bufferedSymbolsFileName))))
            }
        }.dump()
        expected = pluginContext.irFactory.buildField {
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
        }.dump()
    """)

        // language="kotlin", prefix="override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) { val actual: String; val expected: String", suffix="}"
        private val testCasesConcat: List<String> = listOf(/)
    }

    //#region unit tests

    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-101`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-102`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-103`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-104`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-105`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-106`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-107`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-107a`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-108`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-108a`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-109`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-110`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-111`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-112`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-112a`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-113`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-114`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-115`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-116`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-117`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-118`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-119`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-120`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-120a`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-120b`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-120c`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-120d`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-120e`() = test()

    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-201`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-202`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-203`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-204`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-205`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 1-206`() = test()

    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-101`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-102`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-103`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-104`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-105`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-106`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-107`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-107a`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-108`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-108a`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-109`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-110`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-111`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-112`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-112a`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-113`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-114`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-115`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-116`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-117`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-118`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-119`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-120`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-120a`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-120b`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-120c`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-120d`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-120e`() = test()

    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-201`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-202`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-203`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-204`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-205`() = test()
    @Test @OptIn(ExperimentalCompilerApi::class) fun `Test 2-206`() = test()

    //#endregion

    @OptIn(ExperimentalCompilerApi::class)
    fun test(@Language("kotlin", prefix = "override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) { val actual: String?; val expected: String?; val error: String?", suffix = "}") sourceCode: String? = null, testCode: Int? = null, expectedKotlinCompilationResult: KotlinCompilation.ExitCode = KotlinCompilation.ExitCode.OK, compilerPluginRegistrar: CompilerPluginRegistrar = PerfMeasureComponentRegistrar()) {
        val params = Thread.currentThread().stackTrace[3].methodName.split(' ').last().split('-')
        val testName = params.last()
        val list = when (testName[0] - '0') {
            1 -> testCasesFind
            2 -> testCasesBuild
            3 -> testCasesConcat
            else -> error("unknown test set")
        }

        val r = Regex("""\s*\/\/\s*(\w+)[^\r\n]*[\r\n]\s*""")
        val source = sourceCode ?: list.single { r.find(it)!!.groups[1]!!.value == testName }
        val code = source.replace("\$\\{", "\${")
        val namespace = "at.ssw.compilerplugin"
        val className = "TestPluginExtensions_${abs(code.hashCode())}_${abs(random.nextInt())}"

        /// Compile the test case
        val program = KotlinCompilation().apply {
            inheritClassPath = true
            sources = listOf(SourceFile.kotlin("test_${className}.kt", """
package ${(namespace)}

import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.backend.common.lower.*
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.platform.presentableDescription
import kotlin.time.ExperimentalTime

class ${className}: PerfMeasureExtensionTest() {
    @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        fun findClass(name: String): IrClassSymbol = pluginContext.findClass(name)
        fun findFunction(name: String): IrSimpleFunctionSymbol = pluginContext.findFunction(name)
        fun findProperty(name: String): IrPropertySymbol = pluginContext.findProperty(name)
        fun IrClassSymbol.findFunction(name: String): IrSimpleFunctionSymbol = pluginContext.findFunction(name, this)
        fun IrClass.findFunction(name: String): IrSimpleFunctionSymbol = pluginContext.findFunction(name, this.symbol)
        fun IrClassSymbol.findConstructor(name: String): IrConstructorSymbol = pluginContext.findConstructor(name, this)
        fun IrClass.findConstructor(name: String): IrConstructorSymbol = pluginContext.findConstructor(name, this.symbol)

        fun IrBuilderWithScope.call(function: IrFunction, receiver : Any?, vararg parameters: Any?) = this.call(pluginContext, function, receiver, *parameters)
        fun IrBuilderWithScope.call(function: IrSimpleFunctionSymbol, receiver : Any?, vararg parameters: Any?) = this.call(pluginContext, function, receiver, *parameters)
        fun IrBuilderWithScope.concat(vararg parameters: Any?) = this.irConcat(pluginContext, *parameters)

        ${(code)}
    }
}
"""
            ))
            compilerPluginRegistrars = listOf(compilerPluginRegistrar)
        }.compile()

        assertEquals(expectedKotlinCompilationResult, program.exitCode, program.messages)

        /// Execute the test
        val clazz = program.classLoader.loadClass( "${namespace}.${className}")
        val obj = clazz.getDeclaredConstructor().newInstance() as PerfMeasureExtensionTest

        val result = KotlinCompilation().apply {
            inheritClassPath = true
            sources = listOf(SourceFile.kotlin("main_${className}.kt", testCodes[testCode ?: (params.first().toInt() - 1)]))
            compilerPluginRegistrars = listOf(PerfMeasureComponentRegistrarTest(obj))
        }.compile()

        if (obj.error == null) {
            assertEquals(expectedKotlinCompilationResult, result.exitCode, result.messages)
            assertEquals(obj.expected, obj.actual)
        } else {
            val expected = obj.error
            if (expected is Exception && result is Exception) {
                assertEquals(expected::class, result::class)
                assertEquals(expected.message, result.message)
            } else {
                assertEquals(expected, result)
            }
        }
    }

    class MyClass {
        fun fooFunc(sb: kotlin.text.StringBuilder, index: Int?) {
        }
    }

    class Dummy {
        fun fooFunc(sb: kotlin.text.StringBuilder, index: Int?) {
        }
    }
}

@OptIn(ExperimentalCompilerApi::class)
class PerfMeasureComponentRegistrarTest(private val generationExtension: PerfMeasureExtensionTest) : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration)  = IrGenerationExtension.registerExtension(generationExtension)
}

abstract class PerfMeasureExtensionTest: IrGenerationExtension {
    var expected: Any? = null
    var actual: Any? = null
    var error: Any? = null
}