import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

actual fun await(
    exporter: NoopExporter,
    start: Instant
) = runBlocking {
    exporter.await()

    val end = Clock.System.now()
    val ms = (end - start).inWholeMilliseconds
    println("Flush finished - $ms ms elapsed")
}
