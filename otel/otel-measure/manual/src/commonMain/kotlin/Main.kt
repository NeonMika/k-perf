import kotlinx.datetime.Clock

fun main() {
    val start = Clock.System.now()

    span("main") {
        GameOfLife.play()
    }

    val end = Clock.System.now()
    val ms = (end - start).inWholeMilliseconds
    println("Execution finished - $ms ms elapsed")

    processor.shutdown()
    await(exporter, start)
}
