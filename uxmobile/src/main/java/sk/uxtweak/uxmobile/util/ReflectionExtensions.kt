package sk.uxtweak.uxmobile.util

import java.lang.reflect.Field

fun <T> Any.getFieldInstance(name: String): T? {
    return try {
        val field = this::class.java.getFieldExtended(name)
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        field.get(this) as T
    } catch (exception: Exception) {
        null
    }
}

fun Class<*>.getFieldExtended(name: String): Field {
    var clazz: Class<*>? = this
    var lastClass: Class<*>? = null
    while (clazz != Object::class.java && lastClass != clazz) {
        declaredFields.forEach {
            if (it.name == name) {
                return it
            }
        }
        lastClass = clazz
        clazz = superclass
    }
    throw NoSuchFieldException("Field $name not found for class $this")
}
