package sk.uxtweak.uxmobile.model.event

import org.json.JSONArray

class FlingEvent(
    startTime: Long,
    x: Float,
    y: Float,
    private val velocityX: Float,
    private val velocityY: Float
) : TouchEvent(startTime, x, y) {
    override fun getType() = TYPE_FLING

    override fun toJson(): JSONArray = super.toJson()
        .put(INDEX_VELOCITY_X, velocityX)
        .put(INDEX_VELOCITY_Y, velocityY)

    companion object {
        const val INDEX_VELOCITY_X = 4
        const val INDEX_VELOCITY_Y = 5
    }
}
