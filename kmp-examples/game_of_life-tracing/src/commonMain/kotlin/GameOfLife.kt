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
        for (row in field) {
            println(row.map { if (it) liveChar else emptyChar }.joinToString(""))
        }
        println()
    }

    // Helper function to check if a position is valid
    private fun isValidPosition(row: Int, col: Int): Boolean {
        return row in 0 until height && col in 0 until width
    }
}