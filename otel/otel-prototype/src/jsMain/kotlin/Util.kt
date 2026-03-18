import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import node.process.process

actual fun await() {
    CoroutineScope(Dispatchers.Default).launch {
        exporter.await()
        process.exit()
    }
}
