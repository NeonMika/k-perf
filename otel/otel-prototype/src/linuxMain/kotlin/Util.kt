import kotlinx.coroutines.runBlocking

actual fun await() = runBlocking {
    exporter.await()
}
