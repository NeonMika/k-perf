package at.ssw.compilerplugin

import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

data class PropertyInfo(
    val name: String,
    val visibility: String,
    val returnType: String,
    val id: Int,
    val value: String
)

fun inspectProperties(obj: Any, objects: MutableList<Any>): List<PropertyInfo> =
    obj::class.memberProperties.map { prop ->
        prop.isAccessible = true

        val vis = prop.visibility?.name?.lowercase() ?: ""

        val typeStr = prop.returnType.toString()

        val value = try {
            prop.getter.call(obj)
        } catch (_: Exception) {
            null
        }

        if(value!=null){
            objects.add(value)
        }

        PropertyInfo(
            name = prop.name,
            visibility = vis,
            returnType = typeStr,
            id = objects.size-1,
            value = value.toString()
        )
    }