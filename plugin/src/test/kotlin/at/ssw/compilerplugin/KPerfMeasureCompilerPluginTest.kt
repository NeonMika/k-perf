import at.ssw.compilerplugin.PerfMeasureComponentRegistrar
import at.ssw.compilerplugin.PerfMeasureComponentRegistrarNew
import at.ssw.compilerplugin.PerfMeasureComponentRegistrarOriginal
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KPerfMeasureCompilerPluginTest {
    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun `plugin success`() {
        val result = compile(
            SourceFile.kotlin(
                "main.kt",
                """

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
                    """
            )
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        result.main()
    }

    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun `SSP example`() {
        val result = compile(
            SourceFile.kotlin(
                "main.kt",
                """
                    fun main() {
                      sayHello()
                      sayHello("Hi", "SSP")
                    }

                    fun sayHello(greeting: String = "Hello", name: String = "World") {
                        val result = "${'$'}greeting, ${'$'}name!"
                        println(result)
                    }
                    """
            )
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        result.main()
    }

    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun `SSP example Original`() {
        val result = compileOriginal(
            SourceFile.kotlin(
                "main.kt",
                """
                    fun main() {
                      sayHello()
                      sayHello("Hi", "SSP")
                    }

                    fun sayHello(greeting: String = "Hello", name: String = "World") {
                        val result = "${'$'}greeting, ${'$'}name!"
                        println(result)
                    }
                    """
            )
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        result.main()
    }

    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun `SSP example New`() {
        val result = compileNew(
            SourceFile.kotlin(
                "main.kt",
                """
                    fun main() {
                      sayHello()
                      sayHello("Hi", "SSP")
                    }

                    fun sayHello(greeting: String = "Hello", name: String = "World") {
                        val result = "${'$'}greeting, ${'$'}name!"
                        println(result)
                    }
                    """
            )
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        result.main()
    }

    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun `Class example`() {
        val result = compile(
            SourceFile.kotlin(
                "main.kt",
                """
                    package test

                    class MyClass<T>(val value: T) {
                        fun genericFunction(param: T): T {
                            return param
                        }
            
                        fun <R> anotherGenericFunction(param: R): R {
                            return param
                        }

                        fun normalFunction(param: Int, param2: String? = "Test"): String {
                            return "Normal Function: ${'$'}param"
                        }

                        fun normalFunction(param: Int): String {
                            return "Normal Function: ${'$'}param"
                        }
            
                        companion object {
                            fun staticFunction() = "Static Function"
                        }
                    }
                    fun topLevelFunction(param: Int, param2: String? = "Test"): String {
                        return "Top Level Function: ${'$'}param"
                    }

                    fun topLevelFunction(param: Int): String {
                        return "Top Level Function: ${'$'}param"
                    }
            
                    fun main() {
                        val instance = MyClass(42)
                        val result = instance.genericFunction(100)
                        val anotherResult = instance.anotherGenericFunction("Hello")
                        val staticResult = MyClass.staticFunction()
                        val topLevelResult = topLevelFunction(10)
                    }
                """
            )
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        result.main("test")
    }

    @OptIn(ExperimentalCompilerApi::class)
    fun compile(
        sourceFiles: List<SourceFile>,
        compilerPluginRegistrar: CompilerPluginRegistrar = PerfMeasureComponentRegistrar(),
    ): JvmCompilationResult {
        return KotlinCompilation().apply {
            // To have access to kotlinx.io
            inheritClassPath = true
            sources = sourceFiles
            compilerPluginRegistrars = listOf(compilerPluginRegistrar)
            // commandLineProcessors = ...
            // inheritClassPath = true
        }.compile()
    }

    @OptIn(ExperimentalCompilerApi::class)
    fun compile(
        sourceFile: SourceFile,
        compilerPluginRegistrar: CompilerPluginRegistrar = PerfMeasureComponentRegistrar(),
    ) = compile(listOf(sourceFile), compilerPluginRegistrar)

    @OptIn(ExperimentalCompilerApi::class)
    fun compileOriginal(
        sourceFile: SourceFile,
        compilerPluginRegistrar: CompilerPluginRegistrar = PerfMeasureComponentRegistrarOriginal(),
    ) = compile(listOf(sourceFile), compilerPluginRegistrar)

    @OptIn(ExperimentalCompilerApi::class)
    fun compileNew(
        sourceFile: SourceFile,
        compilerPluginRegistrar: CompilerPluginRegistrar = PerfMeasureComponentRegistrarNew(),
    ) = compile(listOf(sourceFile), compilerPluginRegistrar)
}

@OptIn(ExperimentalCompilerApi::class)
private fun JvmCompilationResult.main(packageName: String = "") {
    val className = if (packageName.isNotEmpty()) "$packageName.MainKt" else "MainKt"
    val kClazz = classLoader.loadClass(className)
    val main = kClazz.declaredMethods.single { it.name.endsWith("main") && it.parameterCount == 0 }
    main.invoke(null)
}
