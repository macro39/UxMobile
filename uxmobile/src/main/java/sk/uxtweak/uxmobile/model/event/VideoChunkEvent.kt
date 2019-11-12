package sk.uxtweak.uxmobile.model.event

import org.json.JSONArray

class VideoChunkEvent(startTime: Long, val data: String) : Event(startTime) {
    override fun getType() = TYPE_VIDEO_CHUNK

    override fun toJson(): JSONArray = super.toJson()
        .put(INDEX_VIDEO, data)

    companion object {
        const val INDEX_VIDEO = 2
    }
}
