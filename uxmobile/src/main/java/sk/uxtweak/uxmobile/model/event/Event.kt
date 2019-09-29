package sk.uxtweak.uxmobile.model.event

import org.json.JSONArray

abstract class Event(private val startTime: Long) {
    protected abstract fun getType(): String

    open fun toJson(): JSONArray = JSONArray()
        .put(INDEX_TYPE, getType())
        .put(INDEX_TIME, startTime)

    companion object {
        const val TYPE_CUSTOM = "u"
        const val TYPE_CLICK = "c"
        const val TYPE_EXCEPTION = "e"
        const val TYPE_FLING = "f"
        const val TYPE_LONG_PRESS = "l"
        const val TYPE_SCROLL = "s"
        const val TYPE_ORIENTATION = "o"

        const val TYPE_TASK = "t"

        const val TYPE_SESSION_END = "x"   // dummy event

        const val INDEX_TYPE = 0
        const val INDEX_TIME = 1
    }
}
