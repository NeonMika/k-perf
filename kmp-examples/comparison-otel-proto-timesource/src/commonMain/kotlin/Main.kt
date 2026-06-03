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
    // "Black hole" pattern: the println never fires (fib(20) = 6765 is always
    // > 0) but the compiler can't prove that without recursively unrolling
    // fibonacci. It must therefore treat the branch as live and execute both
    // fibonacci() and bubbleSort() every call. Defeats Kotlin/Native LLVM
    // dead-code-elimination at -O2 with zero I/O cost in the hot path.
    if (fib < 0) println("$fib ${arr[0]}")
}

fun main(args: Array<String>) {
    val steps = args.mapNotNull { it.toIntOrNull() }.firstOrNull() ?: 1

    val start = TimeSource.Monotonic.markNow()
    repeat(steps) { i ->
        val stepStart = start.elapsedNow()
        workload()
        println("!!! Elapsed time $i: ${(start.elapsedNow() - stepStart).inWholeNanoseconds}")
    }
    println("### Elapsed time: ${start.elapsedNow().inWholeNanoseconds}")
}
