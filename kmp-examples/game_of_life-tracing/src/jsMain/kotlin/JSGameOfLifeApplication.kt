import kotlin.time.TimeSource

fun main() {
    val start = TimeSource.Monotonic.markNow()
    val game = GameOfLife(width = 20, height = 20)

    // Add some initial seeds (glider pattern)
    game.addSeed(1, 2)
    game.addSeed(2, 3)
    game.addSeed(3, 1)
    game.addSeed(3, 2)
    game.addSeed(3, 3)

    println("Initial state:")
    game.print()

    repeat(500) {
        game.step()
    }

    println("State after 500 steps:")
    game.print()
    println("### Elapsed time: ${start.elapsedNow().inWholeMicroseconds}")
}