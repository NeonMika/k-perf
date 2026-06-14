import io.opentelemetry.kotlin.context.Context
import kotlin.time.TimeSource

fun fibonacci(n: Int): Long {
    if (n <= 1) return n.toLong()
    return fibonacci(n - 1) + fibonacci(n - 2)
}

fun bubbleSort(arr: IntArray) {
    val n = arr.size
    for (i in 0 until n - 1) {
        for (j in 0 until n - i - 1) {
            if (arr[j] > arr[j + 1]) {
                val temp = arr[j]
                arr[j] = arr[j + 1]
                arr[j + 1] = temp
            }
        }
    }
}

fun workload() {
    val fib = fibonacci(20)
    val arr = intArrayOf(64, 34, 25, 12, 22, 11, 90, 88, 77, 66, 55, 44, 33, 22, 11)
    bubbleSort(arr)
    if (fib < 0) println("$fib ${arr[0]}")
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
