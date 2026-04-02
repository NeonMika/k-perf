import at.jku.ssw.compilerplugin.KPerfComponentRegistrar
import at.jku.ssw.compilerplugin.KPerfExtension
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCompilerApi::class)
class KPerfCompilerPluginTest {

  @Test
  fun `SSP (Symposium for Software Performance) simple example`() {
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

  @Test
  fun `Are getters functions_yes they are`() {
    val result = compile(
      SourceFile.kotlin(
        "main.kt",
        """
                    val x = 10 // Check whether the getter of this recognized as an IrFunction --> Yes: FUN DEFAULT_PROPERTY_ACCESSOR name:<get-x> ...
                    // Attention: JVM backend nevertheless eliminates the getter call and directly accesses the backing field.
                    // JS and Native backend represent every property access as function call. Thus, instrumenting functions with origin DEFAULT_PROPERTY_ACCESSOR leads to
                    // higher overhead for JS and Native than for JVM.

                    fun main() {
                      println(x)
                    }
                    """
      )
    )
    assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

    result.main()
  }

  @Test
  fun `Big example`() {
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

  @Test
  fun `Complex class example`() {
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

                        fun String.foo(x: Int): String = "Host($this) + ${'$'}x"
            
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

  @Test
  fun `methods filter - only functions matching regex are instrumented`() {
    // Instrument only functions in package "test"; functions in "otherPackage" must be left untouched
    val result = compile(
      SourceFile.kotlin(
        "main.kt",
        """
                    package test

                    class MyClass {
                        fun doWork(): String = "done"
                    }

                    fun main() {
                        val result = MyClass().doWork()
                        println(result)
                    }
                    """
      ),
      SourceFile.kotlin(
        "other.kt",
        """
                    package otherPackage

                    class MyClassNotInstrumented {
                        fun doWorkNotInstrumented(): String = "done"
                    }

                    fun fooNotInstrumented() {
                        val result = MyClassNotInstrumented().doWorkNotInstrumented()
                        println(result)
                    }
                    """
      ),
      compilerPluginRegistrar = KPerfComponentRegistrarWithMethods("test\\..*")
    )
    assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    result.main("test")
  }

  @Test
  fun `methods filter - empty match instruments nothing and program still runs`() {
    // No functions match the regex; plugin instruments nothing but compilation must still succeed
    val result = compile(
      SourceFile.kotlin(
        "main.kt",
        """
                    fun main() {
                        println("no instrumentation")
                    }
                    """
      ),
      compilerPluginRegistrar = KPerfComponentRegistrarWithMethods("nonexistent\\..*")
    )
    assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    result.main()
  }

  fun compile(
    vararg sourceFiles: SourceFile,
    compilerPluginRegistrar: CompilerPluginRegistrar = KPerfComponentRegistrar(),
  ): JvmCompilationResult {
    return KotlinCompilation().apply {
      inheritClassPath = true
      sources = sourceFiles.toList()
      compilerPluginRegistrars = listOf(compilerPluginRegistrar)
    }.compile()
  }
}

@OptIn(ExperimentalCompilerApi::class)
private fun JvmCompilationResult.main(packageName: String = "") {
  val className = if (packageName.isNotEmpty()) "$packageName.MainKt" else "MainKt"
  val kClazz = classLoader.loadClass(className)
  val main = kClazz.declaredMethods.single { it.name.endsWith("main") && it.parameterCount == 0 }
  main.invoke(null)
}

@OptIn(ExperimentalCompilerApi::class)
private class KPerfComponentRegistrarWithMethods(private val methods: String) : CompilerPluginRegistrar() {
  override val pluginId: String = "k-perf-compiler-plugin"
  override val supportsK2: Boolean = true
  override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
    IrGenerationExtension.registerExtension(KPerfExtension(MessageCollector.NONE, false, false, methods))
  }
}