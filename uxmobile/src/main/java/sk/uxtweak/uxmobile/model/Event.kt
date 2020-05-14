package sk.uxtweak.uxmobile.model

import android.os.SystemClock
import com.fasterxml.jackson.annotation.JsonProperty

sealed class Event(val type: Int, var at: Long = SystemClock.elapsedRealtime()) {
    object StartEvent : Event(1)
    object EndEvent : Event(2)

    data class TapEvent(
        val x: Float,
        val y: Float,
        @get:JsonProperty("view_type") val viewType: Int?,
        @get:JsonProperty("view_text") val viewText: String?,
        @get:JsonProperty("view_value") val viewValue: String?
    ) : Event(3)

    data class DoubleTapEvent(
        val x: Float,
        val y: Float,
        @get:JsonProperty("view_type") val viewType: Int?,
        @get:JsonProperty("view_text") val viewText: String?,
        @get:JsonProperty("view_value") val viewValue: String?
    ) : Event(4)

    data class LongPressEvent(
        val x: Float,
        val y: Float,
        @get:JsonProperty("view_type") val viewType: Int?,
        @get:JsonProperty("view_text") val viewText: String?,
        @get:JsonProperty("view_value") val viewValue: String?
    ) : Event(5)

    data class FlingEvent(
        val x: Float,
        val y: Float,
        @get:JsonProperty("velocity_x") val velocityX: Float,
        @get:JsonProperty("velocity_y") val velocityY: Float,
        @get:JsonProperty("view_type") val viewType: Int?,
        @get:JsonProperty("view_text") val viewText: String?,
        @get:JsonProperty("view_value") val viewValue: String?
    ) : Event(6)

    data class ScrollEvent(
        val x: Float,
        val y: Float,
        @get:JsonProperty("distance_x") val distanceX: Float,
        @get:JsonProperty("distance_y") val distanceY: Float,
        @get:JsonProperty("view_type") val viewType: Int?,
        @get:JsonProperty("view_text") val viewText: String?,
        @get:JsonProperty("view_value") val viewValue: String?
    ) : Event(7)

    data class ActivityStartedEvent(
        @get:JsonProperty("activity_name") val activityName: String
    ) : Event(8)

    data class OrientationEvent(
        val orientation: Int
    ) : Event(9)

    data class ExceptionEvent(
        val throwable: Throwable
    ) : Event(10)

    data class VideoChunkEvent(
        val name: String,
        @get:JsonProperty("is_last") val isLast: Boolean,
        val data: String
    ) : Event(11) {
        override fun toString() = "VideoChunkEvent ${data.substring(0, 8)}"
    }

    object VideoStartEvent : Event(12)
}
