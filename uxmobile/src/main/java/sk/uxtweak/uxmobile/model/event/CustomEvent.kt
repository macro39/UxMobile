package sk.uxtweak.uxmobile.model.event

import org.json.JSONArray
import org.json.JSONObject

class CustomEvent(
    startTime: Long,
    private val eventName: String,
    private val payload: Map<String, String> = mapOf()
) : Event(startTime) {
    override fun getType() = TYPE_CUSTOM

    override fun toJson(): JSONArray = super.toJson()
        .put(INDEX_EVENT_NAME, eventName)
        .put(INDEX_PAYLOAD, JSONObject(payload))

    companion object {
        const val INDEX_EVENT_NAME = 2
        const val INDEX_PAYLOAD = 3
    }
}
