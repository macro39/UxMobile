package sk.uxtweak.uxmobile.util

import com.fasterxml.jackson.databind.ObjectMapper

object JsonUtils {
    val objectMapper = ObjectMapper()
}

fun Any.toJson() = JsonUtils.objectMapper.writeValueAsString(this)

fun <T> String.fromJson(clazz: Class<T>) = JsonUtils.objectMapper.readValue(this, clazz::class.java)

inline fun <reified T> String.fromJson() = fromJson(T::class.java)
