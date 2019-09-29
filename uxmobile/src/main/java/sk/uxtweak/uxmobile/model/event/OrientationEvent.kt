package sk.uxtweak.uxmobile.model.event

import org.json.JSONArray

class OrientationEvent(startTime: Long, private val orientation: Int) : Event(startTime) {
    override fun getType() = TYPE_ORIENTATION

    override fun toJson(): JSONArray = super.toJson()
        .put(INDEX_ORIENTATION, orientation)

    companion object {
        const val INDEX_ORIENTATION = 2
    }
}
