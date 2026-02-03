fun main(args: Array<String>) {
  val steps = args.getOrNull(0)?.toIntOrNull() ?: 500
  game.gol.play(steps)
}
