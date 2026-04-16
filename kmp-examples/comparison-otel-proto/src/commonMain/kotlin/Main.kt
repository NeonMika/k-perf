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
    // 1. Recursive workload
    val fibResult = fibonacci(25)
    println("Fibonacci(25) = $fibResult")

    // 2. Iterative workload
    val arr = intArrayOf(64, 34, 25, 12, 22, 11, 90, 88, 77, 66, 55, 44, 33, 22, 11)
    bubbleSort(arr)
    println("Sorted array: ${arr.joinToString(", ")}")
}

fun main() {
    println("Starting workload...")
    val start = TimeSource.Monotonic.markNow()
    
    workload()
    
    val end = TimeSource.Monotonic.markNow()
    val ms = (end - start).inWholeMilliseconds
    println("Workload finished.")
    println("Execution finished - $ms ms elapsed")
}
