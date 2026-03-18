class GameOfLife(
    private val size: Int
) {
    private var grid: Array<Array<Boolean>> = Array(size) { _ -> Array(size) { _ -> false } }

    fun set(
        x: Int,
        y: Int,
    ) = span("set") {
        grid[x][y] = true
    }

    fun step() = span("step") {
        val g: Array<Array<Boolean>> = Array(size) { i -> Array(size) { j -> false } }

        for (x in 0 until size) {
            for (y in 0 until size) {
                g[x][y] = shouldBecomeAlive(x, y)
            }
        }

        grid = g
    }

    fun shouldBecomeAlive(
        x: Int,
        y: Int
    ): Boolean = span("shouldBecomeAlive") {
        val alive = neighbors(x, y)
        return@span when (alive) {
            0, 1 -> false
            2 -> grid[x][y]
            3 -> true
            else -> false
        }
    }

    private fun neighbors(
        x: Int,
        y: Int
    ): Int = span("neighbors") {
        var count = 0
        for (nx in x - 1..x + 1) {
            for (ny in y - 1..y + 1) {
                if (nx == x && ny == y) continue
                if (!isValidPosition(nx, ny)) continue

                if (grid[nx][ny])
                    count++
            }
        }
        return@span count
    }

    private fun isValidPosition(
        x: Int,
        y: Int
    ): Boolean = span("isValidPosition") {
        return@span x in 0..<size && y in 0..<size
    }

    fun print() = span("print") {
        val alive = '■'
        val dead = '□'

        val text = buildString {
            for (row in grid) {
                appendLine(row.map { if (it) alive else dead }.joinToString(""))
            }
            appendLine()
        }
        println(text)
    }

    companion object {
        fun play() = span("play") {
            val game = GameOfLife(20)

            game.set(1, 3)
            game.set(2, 1)
            game.set(2, 3)
            game.set(3, 2)
            game.set(3, 3)

            game.print()

            repeat(500) {
                game.step()
            }

            game.print()
        }
    }
}
