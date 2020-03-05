package sk.uxtweak.uxmobile.model.events

import com.fasterxml.jackson.annotation.JsonProperty

sealed class Event(val type: Int) {
    object StartEvent : Event(1)
    object EndEvent : Event(2)

    data class TapEvent(
        val x: Float,
        val y: Float
    ) : Event(3)

    data class DoubleTapEvent(
        val x: Float,
        val y: Float
    ) : Event(4)

    data class LongPressEvent(
        val x: Float,
        val y: Float
    ) : Event(5)

    data class FlingEvent(
        val x: Float,
        val y: Float,
        @get:JsonProperty("velocity_x") val velocityX: Float,
        @get:JsonProperty("velocity_y") val velocityY: Float
    ) : Event(6)

    data class ScrollEvent(
        val x: Float,
        val y: Float,
        @get:JsonProperty("distance_x") val distanceX: Float,
        @get:JsonProperty("distance_y") val distanceY: Float
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
        val data: String
    ) : Event(11) {
        override fun toString() = "VideoChunkEvent ${data.substring(0, 8)}"
    }
}
