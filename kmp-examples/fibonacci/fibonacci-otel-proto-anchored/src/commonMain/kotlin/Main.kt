import io.opentelemetry.kotlin.context.Context
import kotlin.time.TimeSource

fun fibonacci(n: Int): Long {
    if (n <= 1) return n.toLong()
    return fibonacci(n - 1) + fibonacci(n - 2)
}

fun workload() {
    val fib = fibonacci(20)
    // DCE black-hole: never fires (fib(20)=6765), but the compiler cannot prove
    // that, so Kotlin/Native LLVM must keep the fibonacci call live.
    if (fib < 0) println(fib)
}

fun main(args: Array<String>) {
    val steps = args.mapNotNull { it.toIntOrNull() }.firstOrNull() ?: 1

    val start = TimeSource.Monotonic.markNow()
    repeat(steps) { i ->
        val scope = Context.root().makeCurrent()
        try {
            val stepStart = start.elapsedNow()
            workload()
            println("!!! Elapsed time $i: ${(start.elapsedNow() - stepStart).inWholeNanoseconds}")
        } finally {
            scope.close()
        }
    }
    println("### Elapsed time: ${start.elapsedNow().inWholeNanoseconds}")
}
