import com.infendro.otlp.OtlpExporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import node.process.process

actual fun await(
    exporter: OtlpExporter,
    start: Instant
) {
    CoroutineScope(Dispatchers.Default).launch {
        exporter.await()

        val end = Clock.System.now()
        val ms = (end - start).inWholeMilliseconds
        println("Flush finished - $ms ms elapsed")

        process.exit()
    }
}
