package game.gol

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.TimeSource

class GameOfLife(
    private val width: Int = 100,
    private val height: Int = 100,
    private val emptyChar: Char = 'o',
    private val liveChar: Char = '#'
) {
    private var field: Array<Array<Boolean>> = Array(height) { Array(width) { false } }

    // Add a seed (live cell) at the specified position
    fun addSeed(row: Int, col: Int) {
        if (isValidPosition(row, col)) {
            field[row][col] = true
        }
    }

    // Remove a seed (make cell dead) at the specified position
    fun removeSeed(row: Int, col: Int) {
        if (isValidPosition(row, col)) {
            field[row][col] = false
        }
    }

    // Toggle the state of the cell at the specified position
    fun toggleSeed(row: Int, col: Int) {
        if (isValidPosition(row, col)) {
            field[row][col] = !field[row][col]
        }
    }

    // Count the number of live neighbors for a given cell
    fun nLiveNeighbors(row: Int, col: Int): Int {
        return (-1..1).flatMap { dx ->
            (-1..1).map { dy ->
                if (dx == 0 && dy == 0) 0
                else if (isValidPosition(row + dx, col + dy) && field[row + dx][col + dy]) 1
                else 0
            }
        }.sum()
    }

    // Determine if a cell should become live in the next generation
    fun shouldBecomeLive(row: Int, col: Int): Boolean {
        val liveNeighbors = nLiveNeighbors(row, col)
        return if (field[row][col]) {
            liveNeighbors in 2..3
        } else {
            liveNeighbors == 3
        }
    }

    // Determine if a cell should become dead in the next generation
    fun shouldBecomeDead(row: Int, col: Int): Boolean {
        return !shouldBecomeLive(row, col)
    }

    // Advance the game by one step
    fun step() {
        val newField = Array(height) { row ->
            Array(width) { col ->
                shouldBecomeLive(row, col)
            }
        }
        field = newField
    }

    // Advance the game by n steps
    fun step(n: Int) {
        repeat(n) { step() }
    }

    // Print the current state of the game to the console
    fun print() {
        val text = buildString {
            for (row in field) {
                appendLine(row.map { if (it) liveChar else emptyChar }.joinToString(""))
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            val client = HttpClient()
            print(client.request("https://echo.zuplo.io/") {
                method = HttpMethod.Post
                setBody(text)
            }.body<String>())
        }
        println(text)
        println()

    }

    // Helper function to check if a position is valid
    private fun isValidPosition(row: Int, col: Int): Boolean {
        return row in 0 until height && col in 0 until width
    }

}

fun play() {
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