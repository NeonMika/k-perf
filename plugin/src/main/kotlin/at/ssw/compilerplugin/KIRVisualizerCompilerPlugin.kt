package at.ssw.compilerplugin

import at.ssw.compilerplugin.ExampleConfigurationKeys.KEY_ENABLED
import com.google.gson.GsonBuilder
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import java.awt.Desktop
import java.net.URI
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


object ExampleConfigurationKeys {
    val KEY_ENABLED: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("enabled")
}

/*
Commandline processor to process options.
This is the entry point for the compiler plugin.
It is found via a ServiceLoader.
Thus, we need an entry in META-INF/services/org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
that reads at.ssw.compilerplugin.KIRVisualizerCommandLineProcessor
 */
@OptIn(ExperimentalCompilerApi::class)
class KIRVisualizerCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "k-ir-visualizer-compiler-plugin"
    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            "enabled",
            "<true|false>",
            "whether plugin is enabled"
        )
    )

    init {
        println("KIRVisualizerCommandLineProcessor - init")
    }

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        println("KIRVisualizerCommandLineProcessor - processOption ($option, $value)")
        when (option.optionName) {
            "enabled" -> configuration.put(KEY_ENABLED, value.toBoolean())

            else -> throw CliOptionProcessingException("KIRVisualizerCommandLineProcessor.processOption encountered unknown CLI compiler plugin option: ${option.optionName}")
        }
    }
}

/*
Registrar to register all registrars.
It is found via a ServiceLoader.
Thus, we need an entry in META-INF/services/org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
that reads at.ssw.compilerplugin.IRVisualizerComponentRegistrar
 */
@OptIn(ExperimentalCompilerApi::class)
class IRVisualizerComponentRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    init {
        println("IRVisualizerComponentRegistrar - init")
    }

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == false) {
            return
        }

        // Backend plugin
        IrGenerationExtension.registerExtension(IRVisualizeExtension())
    }
}

/*
Backend plugin
 */
class IRVisualizeExtension : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val objects: MutableList<Any> = ArrayList()
        val jsonTree = moduleFragment.accept(JSONIrTreeVisitor(), PassedData("",objects))
        val jsonString = GsonBuilder().setPrettyPrinting().create().toJson(jsonTree)

        val continueLatch = CountDownLatch(1)

        val server = embeddedServer(Netty, port=0) {
            install(ContentNegotiation) {
                gson {
                }
            }
            routing {
                staticResources("/", "website")

                post("/continue") {
                    call.respondText("Resuming compilation")
                    continueLatch.countDown()
                }

                get("/irtree.json") {
                    call.respondText(
                        text = jsonString,
                        contentType = ContentType.Application.Json
                    )
                }

                get("/inspect") {
                    try{
                        val idParam = call.request.queryParameters["id"]?.toIntOrNull()
                            ?: return@get call.respond(HttpStatusCode.BadRequest, "No object id provided")

                        if(idParam in objects.indices){
                            val target = objects[idParam]
                            val props = inspectProperties(target, objects)
                            call.respond(props)
                        }else{
                            return@get call.respond(HttpStatusCode.BadRequest, "Unknown object id: $idParam")
                        }
                    }catch (e: Exception){
                        call.respond(HttpStatusCode.BadRequest, "Unknown backend error occurred")
                    }

                }
            }
        }

        server.start(wait = false)
        val port = runBlocking {
            server.engine.resolvedConnectors().first().port
        }
        val url = URI("http://localhost:$port/visualizer.html")

        openBrowser(url)
        println(url)

        continueLatch.await()

        server.stop(0, 0, TimeUnit.MILLISECONDS)
    }

    private fun openBrowser(url: URI){
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(url)
        }else {
            val os = System.getProperty("os.name").lowercase()
            val command = when {
                os.contains("win") -> listOf("rundll32", "url.dll,FileProtocolHandler", url.toString())
                os.contains("mac") -> listOf("open", url.toString())
                os.contains("nix") || os.contains("nux") || os.contains("aix") -> listOf("xdg-open", url.toString())
                else -> throw UnsupportedOperationException("Unsupported OS: $os")
            }
            try {
                ProcessBuilder(command).start()
            } catch (_: Exception) {
            }
        }
    }
}