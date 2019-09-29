package sk.uxtweak.uxmobile.model.event

import org.json.JSONArray

class ScrollEvent(
    startTime: Long,
    x: Float,
    y: Float,
    private val distanceX: Float,
    private val distanceY: Float
) : TouchEvent(startTime, x, y) {
    override fun getType() = TYPE_SCROLL

    override fun toJson(): JSONArray = super.toJson()
        .put(INDEX_DISTANCE_X, distanceX)
        .put(INDEX_DISTANCE_Y, distanceY)

    companion object {
        const val INDEX_DISTANCE_X = 4
        const val INDEX_DISTANCE_Y = 5
    }
}
