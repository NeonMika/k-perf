import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

data class PropertyInfo(
    val name: String,
    val visibility: String,
    val returnType: String,
    val id: Int,
    val value: String
)

fun inspectProperties(obj: Any, map: MutableMap<Int, Any>): List<PropertyInfo> =
    obj::class.memberProperties.map { prop ->
        prop.isAccessible = true

        val vis = when (prop.visibility) {
            KVisibility.PUBLIC    -> "public"
            KVisibility.PROTECTED -> "protected"
            KVisibility.PRIVATE   -> "private"
            else                   -> "internal"
        }

        val typeStr = prop.returnType.toString()

        val value = try {
            prop.getter.call(obj)
        } catch (_: Exception) {
            null
        }

        val childId = System.identityHashCode(value)
        if(value!=null){
            map[childId]=value
        }

        PropertyInfo(
            name = prop.name,
            visibility = vis,
            returnType = typeStr,
            id = childId,
            value = value.toString()
        )
    }
