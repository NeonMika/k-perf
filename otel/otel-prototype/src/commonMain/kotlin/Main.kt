fun main() {
    val gol = GameOfLife(20)
    gol.initialize {
        it[0][2] = true
        it[1][0] = true
        it[1][2] = true
        it[2][1] = true
        it[2][2] = true
    }
    gol.simulate(50)

    // makes sure all leftover spans are exported
    processor.shutdown()
    await()
}
