package at.jku.ssw.kir.find

import at.jku.ssw.kir.find.irplugincontext.getIrType
import at.jku.ssw.kir.find.irtype.equalsIgnorePlatform
import at.jku.ssw.kir.find.irtype.isGenericType
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType

/**
 * Data class to hold the individual parts of a signature.
 *
 * A signature is a string that uniquely identifies a class, function or property.
 * It consists of the package name, the class name and the member name.
 *
 * @property packageName The package name of the signature.
 * @property className The class name of the signature.
 * @property memberName The member name of the signature.
 * @property packageForFindClass The package name for the [IrPluginContext.findClassOrNull] function.
 */
data class SignatureParts(
  val packageName: String,
  val className: String,
  val memberName: String,
  val packageForFindClass: String
)

/**
 * Represents the type of signature used in method or parameter matching.
 *
 * - `Star`: Indicates a wildcard parameter, allowing arbitrary parameters from this point onward.
 * - `Generic`: Represents a placeholder for generic types.
 * - `Type`: Specifies a concrete type for matching.
 */
enum class SignatureType {
  Star,
  Generic,
  Type
}

/**
 * Maps a shorthand type name to its corresponding fully-qualified name.
 *
 * @param shorthand The shorthand type name
 * @return Either the fully qualified name, or paramater `shorthand` if it does not match one of the predefined shorthand names.
 */
fun resolveDefaultTypeNameToFQTypeName(shorthand: String) = when (shorthand) {
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
  "javachararray" -> "java/lang/CharArray"
  "stringbuilder" -> "kotlin/text/StringBuilder"
  "javastringbuilder" -> "java/lang/StringBuilder"
  "stringbuffer" -> "java/lang/StringBuffer"

  //primitive kotlin arrays
  "chararray" -> "kotlin/CharArray"
  "bytearray" -> "kotlin/ByteArray"
  "shortarray" -> "kotlin/ShortArray"
  "intarray" -> "kotlin/IntArray"
  "longarray" -> "kotlin/LongArray"
  "floatarray" -> "kotlin/FloatArray"
  "doublearray" -> "kotlin/DoubleArray"
  "booleanarray" -> "kotlin/BooleanArray"

  //collections
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

  //further java collections
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

  else -> shorthand //here we assume we have fully qualified name instead of a shorthand
}

/**
 * Parses a function signature string to extract the function name and its parameter types.
 *
 * The function expects the signature to be in the format "functionName(paramType1, paramType2, ...)".
 * It extracts the function name and resolves the parameter types using the provided `IrPluginContext`.
 *
 * @param pluginContext The context used to resolve parameter types.
 * @param signature The function signature string to parse.
 * @return A Triple containing the function name, a list of resolved parameter types, and a list of parameter kinds.
 * @throws IllegalArgumentException If the signature does not contain parentheses
 *                                  "*" is used more than once or in the wrong place.
 *                                  And also if a parameter type cannot be resolved.
 */
fun parseFunctionParameters(
  pluginContext: IrPluginContext,
  signature: String
): Triple<String, List<IrType>, List<SignatureType>> {
  //functions must have parenthesis
  require(signature.contains('(')) { "The signature must contain opening parentheses." }
  require(signature.contains(')')) { "The signature must contain closing parentheses." }
  //"*" can only be used once and only at the end of the signature
  require(signature.count { it == '*' } <= 1) { "The signature must contain '*' at most once." }
  //-2 because closing parenthesis is expected after "*"
  require(signature.lastIndexOf('*') == signature.length - 2 || !signature.contains('*')) { "The '*' must be the last symbol in the signature if present." }

  val functionName = signature.substringBefore("(")
  val paramsString = signature.substringAfter("(").substringBefore(")")

  //parse parameters
  val params = if (paramsString.isBlank()) {
    emptyList()
  } else {
    paramsString.split(",").map { it.trim() }.mapNotNull { pluginContext.getIrType(it) }
  }

  val paramKinds = if (paramsString.isBlank()) {
    emptyList()
  } else {
    paramsString.split(",").map { it.trim() }.map {
      when (it) {
        "*" -> SignatureType.Star
        "G" -> SignatureType.Generic
        else -> SignatureType.Type
      }
    }
  }

  return Triple(functionName, params, paramKinds)
}

/**
 * Parses a signature into its components.
 *
 * The signature is split into four parts: the package name, the class name, the member name, and the package name as it should be used with [IrPluginContext.findClassOrNull].
 *
 * @param signature The signature to parse. It should be in the format "package/outerClasses.Member".
 * @return The parsed signature parts.
 */
fun parseSignature(signature: String): SignatureParts {
  require(signature.contains('/')) { "Package path must be included in signature" }

  val parts = signature.split('/')
  val memberParts = parts.last().split('.')
  val memberName = memberParts.last()
  val packageName = parts.dropLast(1).joinToString(".")
  val packageForFindClass = packageName.replace(".", "/")
  val className = memberParts.dropLast(1).joinToString(".")

  return SignatureParts(packageName, className, memberName, packageForFindClass)
}

/**
 * Verifies if the given function symbol matches the specified parameter types.
 *
 * There are two modes of verification. The first mode attempts to match the exact number of parameters.
 * If this fails, the second mode is used, which ignores default parameters.
 *
 * @param func The function symbol to verify.
 * @param paramTypes The parameter types to verify against.
 * @param paramKinds The kinds of parameters (e.g., normal, generic, or wildcard).
 * @param ignoreNullability Specifies whether nullability should be ignored when comparing parameters.
 * @return `true` if the function symbol matches the specified parameter types, otherwise `false`.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun checkMethodSignature(
  func: IrFunctionSymbol,
  paramTypes: List<IrType>,
  paramKinds: List<SignatureType>,
  ignoreNullability: Boolean = false
): Boolean {
  val parameters =
    func.owner.parameters.filter { it.kind != IrParameterKind.DispatchReceiver && it.kind != IrParameterKind.ExtensionReceiver }

  return if (paramKinds.contains(SignatureType.Star)) {
    // Check if the given parameters match the first few
    if (parameters.size < paramTypes.size) return false //ignore generic types
    paramTypes.withIndex().all { (index, actualType) ->
      if (paramKinds[index] == SignatureType.Generic && parameters[index].type.isGenericType()) {
        true //ignore generic types
      } else {
        parameters[index].type.equalsIgnorePlatform(actualType, ignoreNullability)
      }
    }
  } else {
    // Check exact match
    if (parameters.size != paramKinds.size) return false
    if (paramKinds.filter { it != SignatureType.Generic }.size != paramTypes.size) return false
    parameters.withIndex().all { (index, expectedParameter) ->
      if (paramKinds[index] == SignatureType.Generic && parameters[index].type.isGenericType()) {
        true //ignore generic types
      } else {
        if (paramTypes.size > index) {
          paramTypes[index].equalsIgnorePlatform(expectedParameter.type, ignoreNullability)
        } else {
          return false //if there are not enough parameters to match
        }
      }
    }
  }
}