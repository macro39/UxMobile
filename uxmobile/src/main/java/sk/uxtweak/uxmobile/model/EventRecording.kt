package sk.uxtweak.uxmobile.model

import android.app.Activity
import org.json.JSONArray
import org.json.JSONObject
import sk.uxtweak.uxmobile.model.event.Event

class EventRecording(
    private val activityName: String,
    private val startTime: Long
) {
    private val events: MutableList<Event> = mutableListOf()

    constructor(activity: Activity, startTime: Long) : this(activity.localClassName, startTime)

    fun addEvent(event: Event) {
        events += event
    }

    fun toJson() = JSONObject()
        .put(TAG_ACTIVITY_NAME, activityName)
        .put(TAG_START_TIME, startTime)
        .put(TAG_EVENTS, inputsToJson())

    private fun inputsToJson(): JSONArray {
        val out = JSONArray()

        for (i in 0 until events.size) {
            out.put(i, events[i].toJson())
        }

        return out
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventRecording

        if (activityName != other.activityName) return false
        if (startTime != other.startTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = activityName.hashCode()
        result = 31 * result + startTime.hashCode()
        return result
    }


    companion object {
        const val TAG_ACTIVITY_NAME = "activity_name"
        const val TAG_EVENTS = "events"
        const val TAG_START_TIME = "start_time"
    }
}
