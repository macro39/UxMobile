package sk.uxtweak.uxmobile.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SessionEvent(
    @get:JsonProperty("session_id") var sessionId: String?,
    val at: Long,
    val event: Event
)
