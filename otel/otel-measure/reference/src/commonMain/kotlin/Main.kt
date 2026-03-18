import kotlinx.datetime.Clock

fun main() {
    val start = Clock.System.now()

    GameOfLife.play()

    val end = Clock.System.now()
    val ms = (end - start).inWholeMilliseconds
    println("Execution finished - $ms ms elapsed")
}
