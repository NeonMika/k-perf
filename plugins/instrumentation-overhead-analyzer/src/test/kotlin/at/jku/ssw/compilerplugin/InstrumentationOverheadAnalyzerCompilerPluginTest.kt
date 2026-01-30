import at.jku.ssw.compilerplugin.InstrumentationOverheadAnalyzerComponentRegistrar
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InstrumentationOverheadAnalyzerCompilerPluginTest {

  @OptIn(ExperimentalCompilerApi::class)
  @Test
  fun `Plugin test`() {
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

                        repeat(1000) { topLevelFunction(17) }
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
    compilerPluginRegistrar: CompilerPluginRegistrar = InstrumentationOverheadAnalyzerComponentRegistrar(),
  ): JvmCompilationResult {
    return KotlinCompilation().apply {
      // To have access to kotlinx.io
      inheritClassPath = true
      sources = sourceFiles
      compilerPluginRegistrars = listOf(compilerPluginRegistrar)
      // commandLineProcessors = ...
    }.compile()
  }

  @OptIn(ExperimentalCompilerApi::class)
  fun compile(
    sourceFile: SourceFile,
    compilerPluginRegistrar: CompilerPluginRegistrar = InstrumentationOverheadAnalyzerComponentRegistrar(),
  ) = compile(listOf(sourceFile), compilerPluginRegistrar)
}

@OptIn(ExperimentalCompilerApi::class)
private fun JvmCompilationResult.main(packageName: String = "") {
  val className = if (packageName.isNotEmpty()) "$packageName.MainKt" else "MainKt"
  val kClazz = classLoader.loadClass(className)
  val main = kClazz.declaredMethods.single { it.name.endsWith("main") && it.parameterCount == 0 }
  main.invoke(null)
}