import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun Any.getPropertyName(target: Any): String? {
    for (prop in this::class.memberProperties) {
        prop.isAccessible = true;

        if (prop.getter.visibility != KVisibility.PUBLIC) {
            prop.getter.isAccessible = true;
        }

        var value: Any?
        try {
            value = prop.getter.call(this) ?: continue
        } catch (e: Exception) {
            continue
        }

        if (value === target) {
            return prop.name
        }

        if (value is Collection<*>) {
            val idx = value.indexOf(target)
            if (idx >= 0) {
                return "${prop.name}[$idx]"
            }
        }

        if (value is Array<*>) {
            val idx = value.indexOf(target)
            if (idx >= 0) {
                return "${prop.name}[$idx]"
            }
        }
    }

    return null
}

