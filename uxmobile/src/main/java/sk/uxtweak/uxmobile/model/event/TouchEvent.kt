package sk.uxtweak.uxmobile.model.event

import org.json.JSONArray

abstract class TouchEvent(startTime: Long, val x: Float, val y: Float) : Event(startTime) {
    override fun toJson(): JSONArray = super.toJson()
        .put(INDEX_X, x)
        .put(INDEX_Y, y)

    companion object {
        const val INDEX_X = 2
        const val INDEX_Y = 3
    }
}
