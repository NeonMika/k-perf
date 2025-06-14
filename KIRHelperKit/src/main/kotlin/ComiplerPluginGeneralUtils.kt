package at.ssw.helpers

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStringConcatenation
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import kotlin.io.path.Path
import kotlin.io.path.exists

private object StringBuilderCounter {
    private var counter = 0
    fun next(): Int = counter++
}

private object FileCounter {
    private var counter = 0
    fun next(): Int = counter++
}

/**
 * Utility class for creating and managing a Kotlin IR-based StringBuilder.
 *
 * @property pluginContext The IR plugin context used for creating IR elements.
 * @param file The IR file where the StringBuilder field is declared.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
class IrStringBuilder(
    private val pluginContext: IrPluginContext,
    file: IrFile
) {
    private val stringBuilderField: IrField
    private val sbClass = pluginContext.findClass("kotlin/text/StringBuilder")!!
    private val deleteMethod = sbClass.findFunction(pluginContext, "delete(int, int)")!!
    private val toStringMethod = sbClass.findFunction(pluginContext, "toString()")!!

    init {
        stringBuilderField = pluginContext.createField(file.symbol, "_stringBuilder_${StringBuilderCounter.next()}") {
            sbClass.findConstructor(pluginContext)!!()
        }
        file.declarations.add(stringBuilderField)
        stringBuilderField.parent = file
    }

    /**
     * Appends a value to the StringBuilder.
     *
     * @param value The value to append.
     * @return An IR function access expression representing the append operation.
     * @throws IllegalArgumentException If the append method for the given value type is not found.
     */
    fun append(value: Any): IrFunctionAccessExpression {
        val paramType = extractType(value)
        val appendMethod = sbClass.findFunction(pluginContext, "append(${paramType})", ignoreNullability = true)
            ?: throw IllegalArgumentException("Method append($paramType) not found in StringBuilder")

        return DeclarationIrBuilder(pluginContext, stringBuilderField.symbol, stringBuilderField.startOffset, stringBuilderField.endOffset).getCall(pluginContext) {
            stringBuilderField.call(appendMethod, value)
        }
    }

    /**
     * Inserts a value at a specified index in the StringBuilder.
     *
     * @param index The index at which the value should be inserted.
     * @param value The value to insert.
     * @return An IR function access expression representing the insert operation.
     * @throws IllegalArgumentException If the insert method for the given value type is not found.
     */
    fun insert(index: Int, value: Any): IrFunctionAccessExpression {
        val paramType = extractType(value)
        val insertMethod = sbClass.findFunction(pluginContext, "insert(int, ${paramType})", ignoreNullability = true)
            ?: throw IllegalArgumentException("Method insert(int, ${paramType}) not found in StringBuilder")

        return DeclarationIrBuilder(pluginContext, stringBuilderField.symbol, stringBuilderField.startOffset, stringBuilderField.endOffset).getCall(pluginContext) {
            stringBuilderField.call(insertMethod, index, value)
        }
    }

    /**
     * Deletes a range of characters from the StringBuilder.
     *
     * @param start The start index of the range to delete.
     * @param end The end index of the range to delete.
     * @return An IR function access expression representing the delete operation.
     */
    fun delete(start: Int, end: Int): IrFunctionAccessExpression = DeclarationIrBuilder(pluginContext, stringBuilderField.symbol, stringBuilderField.startOffset, stringBuilderField.endOffset).getCall(pluginContext) {
            stringBuilderField.call(deleteMethod, start, end)
        }

    /**
     * Converts the StringBuilder to a string.
     *
     * @return An IR function access expression representing the toString operation.
     */
    fun irToString(): IrFunctionAccessExpression = DeclarationIrBuilder(pluginContext, stringBuilderField.symbol, stringBuilderField.startOffset, stringBuilderField.endOffset).getCall(pluginContext) {
            stringBuilderField.call(toStringMethod)
        }
}

/**
 * Abstract base class for handling file operations in Kotlin IR.
 *
 * @property pluginContext The IR plugin context used for creating IR elements.
 * @param file The IR file where the file-related fields are declared.
 * @param fileName The name of the file to read from or write to.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
abstract class IrFileHandler(
    protected val pluginContext: IrPluginContext,
    private val file: IrFile,
    private val fileName: String
) {
    protected val filePathField: IrField
    protected val systemFileSystemProperty = pluginContext.findProperty("kotlinx/io/files/SystemFileSystem")!!

    init {
        val pathFunction = pluginContext.findFunction("kotlinx/io/files/Path(string)")!!
        filePathField = pluginContext.createField(file.symbol, "_filePath_${FileCounter.next()}") {
            pathFunction(fileName)
        }
        file.declarations.add(filePathField)
        filePathField.parent = file
    }
}

/**
 * Class for handling file reading operations in Kotlin IR.
 *
 * @property pluginContext The IR plugin context used for creating IR elements.
 * @param file The IR file where the file-related fields are declared.
 * @param fileName The name of the file to read from.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
class IrFileReader(
    pluginContext: IrPluginContext,
    file: IrFile,
    fileName: String
) : IrFileHandler(pluginContext, file, fileName) {
    private var sourceField: IrField? = null
    private val readStringFunction = pluginContext.findFunction("kotlinx/io/readString()", extensionReceiverType = pluginContext.findClass("kotlinx/io/Source")!!.defaultType)!!
    private val closeSourceFunction = pluginContext.findFunction("kotlinx/io/Source.close()")!!

    init {
        val sourceFunction = systemFileSystemProperty.findFunction(pluginContext, "source(*)")!!
        val bufferedSourceFunction = pluginContext.findFunction("kotlinx/io/buffered()", sourceFunction.owner.returnType)!!

        if (Path(fileName).exists()) {
            sourceField = pluginContext.createField(file.symbol, "_fileSource_${FileCounter.next()}") {
                systemFileSystemProperty.call(sourceFunction, filePathField).call(bufferedSourceFunction)
            }
            file.declarations.add(sourceField!!)
            sourceField!!.parent = file
        }
    }

    fun readData(): IrFunctionAccessExpression {
        require(sourceField != null) { "Cannot read from a non-existent file!" }
        return DeclarationIrBuilder(pluginContext, sourceField!!.symbol).getCall(pluginContext) {
            sourceField!!.call(readStringFunction)
        }
    }

    fun closeSource(): IrFunctionAccessExpression {
        require(sourceField != null) { "Cannot close a non-existent source!" }
        return DeclarationIrBuilder(pluginContext, sourceField!!.symbol).getCall(pluginContext) {
            sourceField!!.call(closeSourceFunction)
        }
    }
}

/**
 * Class for handling file writing operations in Kotlin IR.
 * Currently only supports writing Strings.
 *
 * @property pluginContext The IR plugin context used for creating IR elements.
 * @param file The IR file where the file-related fields are declared.
 * @param fileName The name of the file to write to.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
class IrFileWriter(
    pluginContext: IrPluginContext,
    file: IrFile,
    fileName: String
) : IrFileHandler(pluginContext, file, fileName) {
    private var sinkField: IrField? = null
    private val writeStringFunction = pluginContext.findFunction("kotlinx/io/writeString(string, int, int)")!!
    private val flushFunction = pluginContext.findFunction("kotlinx/io/Sink.flush()")!!
    private val closeSinkFunction = pluginContext.findFunction("kotlinx/io/Sink.close()")!!

    init {
        val sinkFunction = systemFileSystemProperty.findFunction(pluginContext, "sink(*)")!!
        val bufferedSinkFunction = pluginContext.findFunction("kotlinx/io/buffered()", sinkFunction.owner.returnType)!!

        sinkField = pluginContext.createField(file.symbol, "_fileSink_${FileCounter.next()}") {
            systemFileSystemProperty.call(sinkFunction, filePathField).call(bufferedSinkFunction)
        }
        file.declarations.add(sinkField!!)
        sinkField!!.parent = file
    }

    fun writeData(data: Any): IrFunctionAccessExpression {
        require(extractType(data) == "string") { "Write function only supports string types" }
        return DeclarationIrBuilder(pluginContext, sinkField!!.symbol).getCall(pluginContext) {
            sinkField!!.call(writeStringFunction, data)
        }
    }

    fun flushSink(): IrFunctionAccessExpression {
        return DeclarationIrBuilder(pluginContext, sinkField!!.symbol).getCall(pluginContext) {
            sinkField!!.call(flushFunction)
        }
    }

    fun closeSink(): IrFunctionAccessExpression {
        return DeclarationIrBuilder(pluginContext, sinkField!!.symbol).getCall(pluginContext) {
            sinkField!!.call(closeSinkFunction)
        }
    }
}

/**
 * Extracts the type of a given value as a string.
 *
 * @param value The value whose type is to be extracted.
 * @return The type of the value as a lowercase string.
 * @throws IllegalArgumentException If the type cannot be extracted.
 */
fun extractType(value: Any): String {
    val typeFqName = when (value) {
        is IrProperty -> value.backingField?.type?.classFqName
        is IrField -> value.type.classFqName
        is IrValueParameter -> value.type.classFqName
        is IrVariable -> value.type.classFqName
        is IrFunctionAccessExpression -> value.type.classFqName
        is IrStringConcatenation -> value.type.classFqName
        else -> return value::class.simpleName?.lowercase()
            ?: throw IllegalArgumentException("cannot extract type out of value $value")
    }

    return typeFqName?.shortName()?.asString()?.lowercase()
        ?: throw IllegalArgumentException("cannot extract type out of value $value")
}