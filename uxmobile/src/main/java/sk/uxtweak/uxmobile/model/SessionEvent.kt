package sk.uxtweak.uxmobile.model

import com.fasterxml.jackson.annotation.JsonProperty
import sk.uxtweak.uxmobile.model.events.Event

data class SessionEvent(
    @get:JsonProperty("session_id") val sessionId: String?,
    val at: Long,
    val event: Event
)
