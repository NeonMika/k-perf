@file:OptIn(ExperimentalCompilerApi::class)

package at.jku.ssw.compilerplugin

import at.jku.ssw.shared.IoaKind
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class IoaCompilerPluginTest {

  @Language("kotlin")
  private val code = $$"""
    package test

    class MyClass<T>(val value: T) {
        fun genericFunction(param: T): T {
            return param
        }

        fun <R> anotherGenericFunction(param: R): R {
            return param
        }

        fun normalFunction(param: Int, param2: String? = "Test"): String {
            return "Normal Function: $param"
        }

        fun normalFunction(param: Int): String {
            return "Normal Function: $param"
        }

        fun String.foo(x: Int): String = "Host($this) + $x"

        companion object {
            fun staticFunction() = "Static Function"
        }
    }
    fun topLevelFunction(param: Int, param2: String? = "Test"): String {
        return "Top Level Function: $param"
    }

    fun topLevelFunction(param: Int): String {
        return "Top Level Function: $param"
    }

    fun main() {
        val instance = MyClass(42)
        val result = instance.genericFunction(100)
        val anotherResult = instance.anotherGenericFunction("Hello")
        val staticResult = MyClass.staticFunction()
        val topLevelResult = topLevelFunction(10)

        repeat(1000) { topLevelFunction(17) }
    }
  """.trimIndent()

  fun testKind(kind: IoaKind) {
    val result = compile(SourceFile.kotlin("main.kt", code), kind)
    assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    result.main("test")
  }

  @Test
  @Disabled
  fun `generate simple ir`() {
    val result = compile(SourceFile.kotlin("main.kt", $$"""
      package test

      fun main() {
          println("Hello, World!")
      }
    """.trimIndent()))

    assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    result.main("test")
  }

  @Test
  fun `test none kind`() = testKind(IoaKind.None)

  @Test
  fun `test try finally kind`() = testKind(IoaKind.TryFinally)

  @Test
  fun `test time clock kind`() = testKind(IoaKind.TimeClock)

  @Test
  fun `test time monotonic function kind`() = testKind(IoaKind.TimeMonotonicFunction)

  @Test
  fun `test time monotonic global kind`() = testKind(IoaKind.TimeMonotonicGlobal)

  @Test
  fun `test increment int counter kind`() = testKind(IoaKind.IncrementIntCounter)

  @Test
  fun `test increment atomic int counter kind`() = testKind(IoaKind.IncrementAtomicIntCounter)

  @Test
  fun `test random value kind`() = testKind(IoaKind.RandomValue)

  @Test
  fun `test standard out kind`() = testKind(IoaKind.StandardOut)

  @Test
  fun `test append to string builder kind`() = testKind(IoaKind.AppendToStringBuilder)

  @Test
  fun `test file eager flush kind`() = testKind(IoaKind.FileEagerFlush)

  @Test
  fun `test file lazy flush kind`() = testKind(IoaKind.FileLazyFlush)

  fun compile(
    sourceFiles: List<SourceFile>,
    compilerPluginRegistrar: CompilerPluginRegistrar = IoaComponentRegistrar(),
    commandLineProcessor: CommandLineProcessor = IoaCommandLineProcessor(),
    pluginOptions: List<PluginOption> = listOf(),
  ): JvmCompilationResult {
    return KotlinCompilation().apply {
      // To have access to kotlinx.io
      inheritClassPath = true
      sources = sourceFiles
      compilerPluginRegistrars = listOf(compilerPluginRegistrar)
      commandLineProcessors = listOf(commandLineProcessor)
      this.pluginOptions = pluginOptions
    }.compile()
  }

  fun compile(
    sourceFile: SourceFile,
    compilerPluginRegistrar: CompilerPluginRegistrar = IoaComponentRegistrar(),
    commandLineProcessor: CommandLineProcessor = IoaCommandLineProcessor(),
    pluginOptions: List<PluginOption> = listOf(),
  ) = compile(listOf(sourceFile), compilerPluginRegistrar, commandLineProcessor, pluginOptions)


  fun compile(sourceFile: SourceFile, ioaKind: IoaKind = IoaKind.None, ): JvmCompilationResult {
    return compile(sourceFile, pluginOptions = listOf(
      PluginOption("instrumentation-overhead-analyzer-plugin", "kind", ioaKind.name)
    ))
  }
}

private fun JvmCompilationResult.main(packageName: String = "") {
  val className = if (packageName.isNotEmpty()) "$packageName.MainKt" else "MainKt"
  val kClazz = classLoader.loadClass(className)
  val main = kClazz.declaredMethods.single { it.name.endsWith("main") && it.parameterCount == 0 }
  main.invoke(null)
}