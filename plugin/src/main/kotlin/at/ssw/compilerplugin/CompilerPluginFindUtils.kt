package at.ssw.compilerplugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

private data class SignatureParts(
    val packageName: String,
    val className: String,
    val memberName: String,
    val packageForFindClass: String
)

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.findClass(signature: String): IrClassSymbol? {
    val packageSearch = getTypePackage(signature.lowercase())
    val searchString = if (signature.contains('/')) {
        //check signature
        signature
    } else {
        if (packageSearch != signature) {
            packageSearch
        } else {
            error("Package path must be included in findClass signature")
        }
    }
    val classId = ClassId.fromString(searchString)

    //try to resolve type alias since it's more specific
    val typeAlias = referenceTypeAlias(classId)
    if (typeAlias != null) {
        return typeAlias.owner.expandedType.classOrNull
    }

    val result = referenceClass(classId)

    //if not found try again and check if it's a java type
    if (result == null && !signature.contains('/') && !signature.startsWith("java")) {
        return findClass("java$signature")
    }

    return result
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrClassSymbol.findFunction(pluginContext: IrPluginContext, signature: String, extensionReceiverType: IrType? = null, ignoreNullability: Boolean = false): IrSimpleFunctionSymbol? {
    //TODO ohne pluginContext
    val (functionName, params) = parseFunctionParameters(pluginContext, signature)

    return this.functions
        .firstOrNull() { func ->
            func.owner.name.asString() == functionName && checkMethodSignature(func, params, ignoreNullability) && checkExtensionFunctionReceiverType(func, extensionReceiverType)
        }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrClassSymbol.findProperty(signature: String): IrPropertySymbol? = this.owner.properties.find { it.name.asString().lowercase() == signature.lowercase() }?.symbol

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrClassSymbol.findConstructor(pluginContext: IrPluginContext, signature: String = "()", ignoreNullability: Boolean = false): IrConstructorSymbol? {
    val (_, expectedParams) = parseFunctionParameters(pluginContext, "<init>$signature")

    return this.constructors.firstOrNull { constructor ->
        checkMethodSignature(constructor, expectedParams, ignoreNullability)
    }
}

fun IrPluginContext.findConstructor(signature: String, ignoreNullability: Boolean = false): IrConstructorSymbol? {
    val (_, outerClasses, constructorPart, packageForFindClass) = parseSignature(signature)
    val (className, _) = parseFunctionParameters(this, constructorPart)

    val classSymbol = if (className.isNotBlank()) {
        findClass("$packageForFindClass/$outerClasses.$className")
    } else {
        null
    }

    return classSymbol?.findConstructor(this, "(" +constructorPart.substringAfter("("), ignoreNullability)
}

//TODO allow default paramets without mentioning them in signature
fun IrPluginContext.findFunction(signature: String, extensionReceiverType: IrType? = null, ignoreNullability: Boolean = false): IrSimpleFunctionSymbol? {
    val (packageName, className, functionPart, packageForFindClass) = parseSignature(signature)
    val (functionName, params) = parseFunctionParameters(this, functionPart)

    val classSymbol = if (className.isNotBlank()) {
        findClass("$packageForFindClass/$className")
    } else {
        null
    }

    return classSymbol?.findFunction(this, functionPart, extensionReceiverType, ignoreNullability)
        ?: referenceFunctions(CallableId(FqName(packageName), Name.identifier(functionName)))
            .firstOrNull { func ->
                checkMethodSignature(func, params, ignoreNullability) &&
                        checkExtensionFunctionReceiverType(func, extensionReceiverType)
            }
}

fun IrPluginContext.findProperty(signature: String): IrPropertySymbol? {
    val parts = parseSignature(signature)

    val classSymbol = if (parts.className.isNotBlank()) {
        findClass("${parts.packageForFindClass}/${parts.className}")
    } else {
        null
    }

    return classSymbol?.findProperty(parts.memberName)
        ?: referenceProperties(CallableId(FqName(parts.packageName), Name.identifier(parts.memberName)))
            .singleOrNull()
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrPluginContext.getIrType(typeString: String): IrType? {
    val isNullable = typeString.endsWith("?", ignoreCase = true)
    val baseType = if (isNullable) typeString.removeSuffix("?") else typeString

    val parsedType = parseGenericTypes(baseType)
    val normalizedType = parsedType.first.lowercase()
    val typeArguments = parsedType.second

    val type = when (normalizedType) {
        // primitive types
        "int", "integer" -> this.irBuiltIns.intType
        "long" -> this.irBuiltIns.longType
        "short" -> this.irBuiltIns.shortType
        "byte" -> this.irBuiltIns.byteType
        "boolean" -> this.irBuiltIns.booleanType
        "char", "character" -> this.irBuiltIns.charType
        "float" -> this.irBuiltIns.floatType
        "double" -> this.irBuiltIns.doubleType
        "void" -> this.irBuiltIns.unitType
        "unit" -> this.irBuiltIns.unitType
        "nothing" -> this.irBuiltIns.nothingType
        "any", "object" -> this.irBuiltIns.anyType

        //primitive arrays
        "chararray", "characterarray" -> this.irBuiltIns.charArray.defaultType
        "bytearray" -> this.irBuiltIns.byteArray.defaultType
        "shortarray" -> this.irBuiltIns.shortArray.defaultType
        "intarray", "integerarray" -> this.irBuiltIns.intArray.defaultType
        "longarray" -> this.irBuiltIns.longArray.defaultType
        "floatarray" -> this.irBuiltIns.floatArray.defaultType
        "doublearray" -> this.irBuiltIns.doubleArray.defaultType
        "booleanarray" -> this.irBuiltIns.booleanArray.defaultType

        //handle other types dynamically
        else -> {
            val fqName = getTypePackage(normalizedType)

            val classSymbol = findClass(fqName) ?: return null

            //validate number of parameters
            val expectedParamCount = classSymbol.owner.typeParameters.size
            if (typeArguments.size != expectedParamCount) {
                return null
            }

            //apply type arguments if present
            if (typeArguments.isEmpty()) {
                classSymbol.defaultType
            } else {
                if (typeArguments.any { it == null }) {
                    return null
                } else {
                    classSymbol.typeWith(*typeArguments.requireNoNulls().toTypedArray())
                }
            }
        }
    }

    return if (isNullable) type.makeNullable() else type
}

fun IrPluginContext.parseGenericTypes(signature: String): Pair<String, List<IrType?>> {
    //no generic part in string
    if (!signature.contains("<")) {
        return Pair(signature, emptyList())
    }

    require(signature.count { it == '<' } == signature.count { it == '>' }) {
        "Generic parameter string must have matching '<' and '>'"
    }

    val mainType = signature.substringBefore("<")
    val genericPart = signature.substring(
        signature.indexOf('<') + 1,
        signature.lastIndexOf('>')
    )

    //no generic parameters
    if (genericPart.isBlank()) {
        return Pair(mainType, emptyList())
    }

    val params = mutableListOf<IrType?>()
    var currentParam = StringBuilder()
    var nestedLevel = 0

    //parse nested parameters
    for (char in genericPart) {
        when (char) {
            '<' -> {
                nestedLevel++
                currentParam.append(char)
            }
            '>' -> {
                nestedLevel--
                currentParam.append(char)
            }
            ',' -> {
                if (nestedLevel == 0) {
                    //only split outer most level
                    params.add(getIrType(currentParam.toString().trim()))
                    currentParam = StringBuilder()
                } else {
                    currentParam.append(char)
                }
            }
            else -> currentParam.append(char)
        }
    }

    if (currentParam.isNotEmpty()) {
        params.add(getIrType(currentParam.toString().trim()))
    }

    return Pair(mainType, params)
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun checkMethodSignature(
    func: IrFunctionSymbol,
    paramTypes: List<IrType>,
    ignoreNullability: Boolean = false
): Boolean {
    val parameters = func.owner.valueParameters

    //try exact match first
    if (parameters.size == paramTypes.size) {
        val matches = parameters.zip(paramTypes).all { (param, expectedType) ->
            if (ignoreNullability) {
                param.type.makeNotNull().equalsIgnorePlatform(expectedType.makeNotNull())
            } else {
                param.type.equalsIgnorePlatform(expectedType)
            }
        }
        if (matches) return true
    }

    //try again, ignore default parameters
    if (parameters.size >= paramTypes.size) {
        return paramTypes.withIndex().all { (index, expectedType) ->
            if (ignoreNullability) {
                parameters[index].type.makeNotNull().equalsIgnorePlatform(expectedType.makeNotNull())
            } else {
                parameters[index].type.equalsIgnorePlatform(expectedType)
            }
        }
    }

    return false
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun checkExtensionFunctionReceiverType(func: IrSimpleFunctionSymbol, extensionReceiverType: IrType? = null) = extensionReceiverType?.let {
    func.owner.extensionReceiverParameter?.type?.let { it1 -> it1.equalsIgnorePlatform(it)
} } ?: true

private fun parseSignature(signature: String): SignatureParts {
    require(signature.contains('/')) { "Package path must be included in signature" }

    val parts = signature.split('/')
    val memberParts = parts.last().split('.')
    val memberName = memberParts.last()
    val packageName = parts.dropLast(1).joinToString(".")
    val packageForFindClass = packageName.replace(".", "/")
    val className = memberParts.dropLast(1).joinToString(".")

    return SignatureParts(packageName, className, memberName, packageForFindClass)
}

private fun parseFunctionParameters(pluginContext: IrPluginContext, signature: String): Pair<String, List<IrType>> {
    //functions must have parenthesis
    require(signature.contains('('))

    val functionName = signature.substringBefore("(")
    val paramsString = signature.substringAfter("(").substringBefore(")")

    //parse parameters
    val params = if (paramsString.isBlank()) {
        emptyList()
    } else {
        paramsString.split(",").map { it.trim() }.mapNotNull  { pluginContext.getIrType(it) }
    }

    return Pair(functionName, params)
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrType.equalsIgnorePlatform(type2: IrType): Boolean {
    //handle platform types
    if (this.isPlatformType() || type2.isPlatformType()) {
        //for platform types ignore nullability
        return this.erasedUpperBound() == type2.erasedUpperBound()
    }

    return this == type2
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
fun IrType.isPlatformType(): Boolean {
    return this is IrSimpleType && annotations.any {
        it.symbol.owner.name.asString() == "PlatformType"
    }
}

fun IrType.erasedUpperBound(): IrType {
    return when {
        this is IrSimpleType -> makeNotNull()
        else -> this
    }
}

private fun getTypePackage(typeString: String) = when (typeString) {
    //basic types
    "string" -> "kotlin/String"
    "javastring" -> "java/lang/String"
    "array" -> "kotlin/Array"
    "javaarray" -> "java/lang/Array"
    "throwable" -> "kotlin/Throwable"
    "javathrowable" -> "java/lang/Throwable"
    "exception" -> "kotlin/Exception"
    "javaexception" -> "java/lang/Exception"
    "runtimeexception" -> "kotlin/RuntimeException"
    "javaruntimeexception" -> "java/lang/RuntimeException"
    "error" -> "kotlin/Error"
    "javaerror" -> "java/lang/Error"

    //char related
    "char" -> "kotlin/Char"
    "javachar" -> "java/lang/Character"
    "charsequence" -> "kotlin/CharSequence"
    "javacharsequence" -> "java/lang/CharSequence"
    "chararray" -> "kotlin/CharArray"
    "javachararray" -> "java/lang/CharArray"
    "stringbuilder" -> "kotlin/text/StringBuilder"
    "javastringbuilder" -> "java/lang/StringBuilder"
    "stringbuffer" -> "java/lang/StringBuffer"

    //kotlin collections
    "list" -> "kotlin/collections/List"
    "javalist" -> "java/util/List"
    "mutablelist" -> "kotlin/collections/MutableList"
    "set" -> "kotlin/collections/Set"
    "javaset" -> "java/util/Set"
    "mutableset" -> "kotlin/collections/MutableSet"
    "map" -> "kotlin/collections/Map"
    "javamap" -> "java/util/Map"
    "mutablemap" -> "kotlin/collections/MutableMap"
    "collection" -> "kotlin/collections/Collection"
    "javacollection" -> "java/util/Collection"
    "mutablecollection" -> "kotlin/collections/MutableCollection"
    "iterable" -> "kotlin/collections/Iterable"
    "javaiterable" -> "java/lang/Iterable"
    "mutableiterable" -> "kotlin/collections/MutableIterable"

    //java collections
    "arraylist" -> "java/util/ArrayList"
    "linkedlist" -> "java/util/LinkedList"
    "vector" -> "java/util/Vector"
    "stack" -> "java/util/Stack"
    "hashset" -> "java/util/HashSet"
    "linkedhashset" -> "java/util/LinkedHashSet"
    "treeset" -> "java/util/TreeSet"
    "hashmap" -> "java/util/HashMap"
    "linkedhashmap" -> "java/util/LinkedHashMap"
    "treemap" -> "java/util/TreeMap"
    "hashtable" -> "java/util/Hashtable"
    "queue" -> "java/util/Queue"
    "deque" -> "java/util/Deque"
    "priorityqueue" -> "java/util/PriorityQueue"
    "arraydeque" -> "java/util/ArrayDeque"
    "concurrenthashmap" -> "java/util/concurrent/ConcurrentHashMap"
    "concurrentlinkedqueue" -> "java/util/concurrent/ConcurrentLinkedQueue"
    "blockingqueue" -> "java/util/concurrent/BlockingQueue"
    "linkedblockingqueue" -> "java/util/concurrent/LinkedBlockingQueue"

    //kotlin specific
    "sequence" -> "kotlin/sequences/Sequence"
    "mutablesequence" -> "kotlin/sequences/MutableSequence"
    "pair", "tuple2" -> "kotlin/Pair"
    "triple", "tuple3" -> "kotlin/Triple"
    "range" -> "kotlin/ranges/Range"
    "progression" -> "kotlin/ranges/Progression"
    "regex" -> "kotlin/text/Regex"

    //java utilities
    "optional" -> "java/util/Optional"
    "stream" -> "java/util/stream/Stream"
    "date" -> "java/util/Date"
    "calendar" -> "java/util/Calendar"
    "locale" -> "java/util/Locale"
    "timezone" -> "java/util/TimeZone"
    "uuid" -> "java/util/UUID"

    //java Time
    "localdate" -> "java/time/LocalDate"
    "localtime" -> "java/time/LocalTime"
    "localdatetime" -> "java/time/LocalDateTime"
    "zoneddatetime" -> "java/time/ZonedDateTime"
    "instant" -> "java/time/Instant"
    "duration" -> "java/time/Duration"
    "kotlinduration" -> "kotlin/time/Duration"
    "period" -> "java/time/Period"

    //java IO
    "file" -> "java/io/File"
    "inputstream" -> "java/io/InputStream"
    "outputstream" -> "java/io/OutputStream"
    "reader" -> "java/io/Reader"
    "writer" -> "java/io/Writer"
    "bufferedreader" -> "java/io/BufferedReader"
    "bufferedwriter" -> "java/io/BufferedWriter"
    "printwriter" -> "java/io/PrintWriter"
    "printstream" -> "java/io/PrintStream"

    //java NIO
    "path" -> "java/nio/file/Path"
    "files" -> "java/nio/file/Files"
    "channel" -> "java/nio/channels/Channel"
    "bytebuffer" -> "java/nio/ByteBuffer"
    "charbuffer" -> "java/nio/CharBuffer"

    //primitive types and their wrappers
    "boolean" -> "kotlin/Boolean"
    "javaboolean" -> "java/lang/Boolean"
    "byte" -> "kotlin/Byte"
    "javabyte" -> "java/lang/Byte"
    "short" -> "kotlin/Short"
    "javashort" -> "java/lang/Short"
    "int" -> "kotlin/Int"
    "javaint" -> "java/lang/Integer"
    "long" -> "kotlin/Long"
    "javalong" -> "java/lang/Long"
    "float" -> "kotlin/Float"
    "javafloat" -> "java/lang/Float"
    "double" -> "kotlin/Double"
    "javadouble" -> "java/lang/Double"

    //further java types
    "biginteger" -> "java/math/BigInteger"
    "bigdecimal" -> "java/math/BigDecimal"
    "pattern" -> "java/util/regex/Pattern"
    "matcher" -> "java/util/regex/Matcher"

    //reflection
    "class" -> "kotlin/reflect/KClass"
    "javaclass" -> "java/lang/Class"
    "method" -> "kotlin/reflect/KFunction"
    "javamethod" -> "java/lang/reflect/Method"
    "field" -> "kotlin/reflect/KProperty"
    "javafield" -> "java/lang/reflect/Field"
    "constructor" -> "kotlin/reflect/KFunction"
    "javaconstructor" -> "java/lang/reflect/Constructor"

    //thread related
    "thread" -> "kotlin/concurrent/Thread"
    "javathread" -> "java/lang/Thread"
    "runnable" -> "kotlin/Runnable"
    "javarunnable" -> "java/lang/Runnable"
    "callable" -> "java/util/concurrent/Callable"
    "future" -> "java/util/concurrent/Future"
    "completablefuture" -> "java/util/concurrent/CompletableFuture"
    "executorservice" -> "java/util/concurrent/ExecutorService"

    else -> typeString //here we assume we have fully qualified name
}