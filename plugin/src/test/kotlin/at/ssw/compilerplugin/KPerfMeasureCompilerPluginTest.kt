import at.ssw.compilerplugin.PerfMeasureComponentRegistrar
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KPerfMeasureCompilerPluginTest {
    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun `plugin success`() {
        val result = compile(
            SourceFile.kotlin(
                "main.kt",
                """

                    annotation class MyAnnotation

                    fun main() {
                      val v1 = 5
                      val addRes = v1 + 17
                      val threeDots = ".".repeat(3)
                      val str = debug() + " Test!"
                      output(str)
                      a()
                      val bRes = try { b() } catch (t: Throwable) { t.printStackTrace() }
                    }

                    @MyAnnotation
                    fun debug() = "Hello, World!"

                    fun output(str: String, builder : StringBuilder? = null) {
                      val pr : (String) -> Unit = if(builder == null) ::print else builder::append
                      pr(str)
                    }

                    fun a() {
                        repeat(5) {
                            println("a is a unit method and prints this")
                        }
                    }

                    fun b() : Int {
                        a()
                        return 100 / 0
                    }

                    fun greet(greeting: String = "Hello", name: String = "World"): String {
                      println("⇢ greet(greeting=${'$'}greeting, name=${'$'}name)")
                      val startTime = kotlin.time.TimeSource.Monotonic.markNow()
                      println("⇠ greet [${'$'}{startTime.elapsedNow()}] = threw RuntimeException")
                      throw RuntimeException("Testexception")
                      try {
                        val result = "${'$'}{'$'}greeting, ${'$'}{'$'}name!"
                        println("⇠ greet [${'$'}{startTime.elapsedNow()}] = ${'$'}result")
                        return result
                      } catch (t: Throwable) {
                        println("⇠ greet [${'$'}{startTime.elapsedNow()}] = ${'$'}t")
                        throw t
                      }
                    }
                    """
            )
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        result.main()
    }

    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun `SSP example`() {
        val result = compile(
            SourceFile.kotlin(
                "main.kt",
                """
                    class Dog(val name: String, val age: Int)
                    fun main() {
                      sayHello()
                      sayHello("Hi", "SSP")
                      add(1, 2)
                      val charac = 'c'
                      if(charac is String){
                        val dog = getDog("John", 1)
                      }
                    }

                    fun sayHello(greeting: String = "Hello", name: String = "World \\\"'") {
                        val result = "${'$'}greeting, ${'$'}name!"
                        println(result)
                    }

                    fun add(i1: Int, i2: Int) = i1 + i2

                    fun getDog(name: String, age: Int): Dog{
                        return Dog(name, age)
                    }
                    """
            )
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        result.main()
    }

    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun `BigTest`() {
        val result = compile(
            SourceFile.kotlin(
                "main.kt",
                """
                    class TDArray<T>(
    val dimension: Pair<Int, Int>, // the number of rows and columns
    initFn: (row: Int, col: Int) -> T // init function to initialize each cell
) {
    // internal "array" (MutableList of MutableLists)
    // Use the following MutableList constructor:
    // MutableList(size: Int, init: (index: Int) -> T): MutableList<T>
    private val data = MutableList(dimension.first) { row -> MutableList(dimension.second) { col -> initFn(row, col) } }

    // number of rows without backing field
    val nRows
        get() = dimension.first

    // number of columns without backing field
    val nColumns
        get() = dimension.second

    // secondary constructor, just call this with appropriate values
    // you can assume that each row has the same length
    constructor(data: List<List<T>>) : this(data.size to if (data.isEmpty()) 0 else data[0].size,
        { row, col -> data[row][col] })

    // Nicely formatted 2D Array string
    // Just use .toString() on each value
    // For left-alignment, you can use .padStart() based on the longest string
    override fun toString(): String {
        val lengthOfLongest = data.maxOf { it.maxOf { it.toString().length } }
        return buildString {
            // The "this" in this block is a StringBuilder
            // You can just call append("...") here to add stuff
            // to the final string
            for (row in data) {
                for (i in row.indices) {
                    append(row[i].toString().padStart(lengthOfLongest))
                    if (i != row.size - 1) append(" ")
                }
                appendLine()
            }
        }
    }

    // !arr should return dimension pair of arr (e.g., (15, 10))
    operator fun not() = dimension

    // +arr should return number of rows of arr
    operator fun unaryPlus() = nRows

    // -arr should return number of columns of arr
    operator fun unaryMinus() = nColumns

    // arr[row, col] should return value stored at that index or throw an error if index is invalid
    operator fun get(row: Int, col: Int): T {
        if (row !in 0..<dimension.first || col !in 0..<dimension.second) {
            error("Index out of Bounds")
        }
        return data[row][col]
    }

    // arr(row, col) should behave as arr[row, col]
    operator fun invoke(row: Int, col: Int) = this[row, col]

    // arr[row, col] = value should set the value at the corresponding location if
    // the location is valid
    operator fun set(row: Int, col: Int, value: T) {
        if (row in 0..<nRows && col in 0..<nColumns) {
            data[row][col] = value
        }
    }

    // arr(row, col, value) should have as arr[row, col] = value
    operator fun invoke(row: Int, col: Int, value: T) {
        this[row, col] = value
    }

    // arr[rows, cols] (for example arr[1 to 4, 2 to 3]) should return a new TDArray
    // with the content in the specified ranges (in the example the rows 1, 2, 3, 4 and
    // the columns 2, 3)
    operator fun get(rows: Pair<Int, Int>, cols: Pair<Int, Int>): TDArray<T> {
        val maxRows = if (rows.second < nRows) rows.second else nRows - 1
        val maxCols = if (cols.second < nColumns) cols.second else nColumns - 1

        return TDArray(
            maxRows - rows.first + 1 to maxCols - cols.first + 1
        ) { row, col -> this[rows.first + row, cols.first + col] }
    }


    // arr(rows, cols) should behave as arr[rows, cols]
    operator fun invoke(rows: Pair<Int, Int>, cols: Pair<Int, Int>) = this[rows, cols]

    // x in arr should check if x is contained in any of the array's cells
    operator fun contains(value: T): Boolean {
        for (row in data) {
            if (value in row) {
                return true
            }
        }
        return false
    }

    // for (x in arr) should be possible, thus iterator() must be implemented
    // use an anonymous object for that
    operator fun iterator(): Iterator<T> = object : Iterator<T> {
        private var row = 0
        private var col = 0
        override fun hasNext() = row < nRows && col < nColumns

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException("No remaining element")
            val rv = data[row][col]
            col++
            if (col >= nColumns) {
                col = 0
                row++
            }
            return rv
        }


    }
}

// operator for TDArray<Int> + Int
operator fun TDArray<Int>.plus(other: Int) = TDArray(dimension) { row, col -> this[row, col] + other }

// operator for TDArray<Int> - Int
operator fun TDArray<Int>.minus(other: Int) = TDArray(dimension) { row, col -> this[row, col] - other }

// operator for TDArray<Int> * Int
operator fun TDArray<Int>.times(other: Int) = TDArray(dimension) { row, col -> this[row, col] * other }

// operator for TDArray<Int> / Int
operator fun TDArray<Int>.div(other: Int) = TDArray(dimension) { row, col -> this[row, col] / other }

// operator for TDArray<Int> + TDArray<Int>
operator fun TDArray<Int>.plus(other: TDArray<Int>): TDArray<Int> {
    if (dimension != other.dimension) {
        error("Dimensions must be equal")
    }
    return TDArray(dimension) { row, col -> this[row, col] + other[row, col] }
}

// operator for TDArray<Int> - TDArray<Int>
operator fun TDArray<Int>.minus(other: TDArray<Int>): TDArray<Int> {
    if (dimension != other.dimension) {
        error("Dimensions must be equal")
    }
    return TDArray(dimension) { row, col -> this[row, col] - other[row, col] }
}

// for TDArrays of Comparables we want an extension function to derive the minimum
// If the array is empty, returns null
fun <T : Comparable<T>> TDArray<T>.min(): T? {
    var min: T? = null
    for (row in 0..<nRows) {
        for (col in 0..<nColumns) {
            if (min == null || this[row, col] < min) {
                min = this[row, col]
            }
        }
    }
    return min
}

// for TDArrays of Comparables we want an extension function to derive the maximum
// If the array is empty, returns null
fun <T : Comparable<T>> TDArray<T>.max(): T? {
    var max: T? = null
    for (row in 0..<nRows) {
        for (col in 0..<nColumns) {
            if (max == null || this[row, col] > max) {
                max = this[row, col]
            }
        }
    }
    return max
}
fun main() {
    // Persons have a name (identifying property), a hobby (delegated to a FileStorage) and an age (delegated to an FileStorage)
// hobby and age are null by default
    val markus = Person("Markus")
    val daniel = Person("Daniel")
    val sandra = Person("Sandra")
    // If everything is implemented correct, this prints null hobbies and null ages on the first run, but markus should start completely initialized on a second program run
    println(markus)
    println(daniel)
    println(sandra)
    // Set all properties, this writes to six files (and creates them if not existing)
    markus.age = 29
    daniel.age = 24
    sandra.age = 27
    markus.hobby = "Board gaming"
    daniel.hobby = "Geocaching"
    sandra.hobby = "Breakdance"
    println(markus)
    println(daniel)
    println(sandra)
    // Deletes the two property files
    daniel.age = null
    sandra.hobby = null

// Two properties show as null (daniel's age and sandra's hobby)
    println(markus)
    println(daniel)
    println(sandra)


    val arr: TDArray<Int> = TDArray(15 to 10) { r, c -> (r + 1) * (c + 1) }
    /*
    1 2 3 4 5 6 7 8 9 10
    2 4 6 8 10 12 14 16 18 20
    3 6 9 12 15 18 21 24 27 30
    4 8 12 16 20 24 28 32 36 40
    5 10 15 20 25 30 35 40 45 50
    6 12 18 24 30 36 42 48 54 60
    7 14 21 28 35 42 49 56 63 70
    8 16 24 32 40 48 56 64 72 80
    9 18 27 36 45 54 63 72 81 90
    10 20 30 40 50 60 70 80 90 100
    11 22 33 44 55 66 77 88 99 110
    12 24 36 48 60 72 84 96 108 120
    13 26 39 52 65 78 91 104 117 130
    14 28 42 56 70 84 98 112 126 140
    15 30 45 60 75 90 105 120 135 150
    */
    println(arr)
    println()
    // (15, 10)
    println(arr.dimension)
    println()
    // (15, 10)
    println(!arr)
    println()
    // 15
    println(arr.nRows)
    println()
    // 15
    println(+arr)
    println()
    // 10
    println(arr.nColumns)
    println()
    // 10
    println(-arr)
    println()
    // 6
    println(arr[1, 2])
    println()
    /*
    18 20
    27 30
    36 40
    45 50
    */
    println(arr[1 to 4, 8 to 9])
    println()
    /*
    77 88 99 110
    84 96 108 120
    91 104 117 130
    98 112 126 140
    105 120 135 150
    */
    println(arr[10 to 100, 6 to 100])
    println()
    // true
    println(2 in arr)
    println()
    // false
    println(23 in arr)
    println()
    /*
 2 3 4 5 6 7 8 9 10 11
 3 5 7 9 11 13 15 17 19 21
 4 7 10 13 16 19 22 25 28 31
 5 9 13 17 21 25 29 33 37 41
 6 11 16 21 26 31 36 41 46 51
 7 13 19 25 31 37 43 49 55 61
 8 15 22 29 36 43 50 57 64 71
 9 17 25 33 41 49 57 65 73 81
 10 19 28 37 46 55 64 73 82 91
 11 21 31 41 51 61 71 81 91 101
 12 23 34 45 56 67 78 89 100 111
 13 25 37 49 61 73 85 97 109 121
 14 27 40 53 66 79 92 105 118 131
 15 29 43 57 71 85 99 113 127 141
 16 31 46 61 76 91 106 121 136 151
 */
    println(arr + 1)
    println()
    /*
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    0 0 0 0 0 0 0 0 0 0
    */
    println(arr - arr)
    println()
    // 1
    println(arr.min())
    println()
    // 150
    println(arr.max())
    println()
    arr[0, 0] = 420
    arr(0, 1, -1)
    // -1
    println(arr.min())
    println()
    // 420
    println(arr.max())
    println()
}


                    """
            )
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        result.main()
    }

    @OptIn(ExperimentalCompilerApi::class)
    fun compile(
        sourceFiles: List<SourceFile>,
        compilerPluginRegistrar: CompilerPluginRegistrar = PerfMeasureComponentRegistrar(),
    ): JvmCompilationResult {
        return KotlinCompilation().apply {
            // To have access to kotlinx.io
            inheritClassPath = true
            sources = sourceFiles
            compilerPluginRegistrars = listOf(compilerPluginRegistrar)
            // commandLineProcessors = ...
            // inheritClassPath = true
        }.compile()
    }

    @OptIn(ExperimentalCompilerApi::class)
    fun compile(
        sourceFile: SourceFile,
        compilerPluginRegistrar: CompilerPluginRegistrar = PerfMeasureComponentRegistrar(),
    ) = compile(listOf(sourceFile), compilerPluginRegistrar)
}

@OptIn(ExperimentalCompilerApi::class)
private fun JvmCompilationResult.main() {
    val kClazz = classLoader.loadClass("MainKt")
    val main = kClazz.declaredMethods.single { it.name == "main" && it.parameterCount == 0 }
    main.invoke(null)
}
