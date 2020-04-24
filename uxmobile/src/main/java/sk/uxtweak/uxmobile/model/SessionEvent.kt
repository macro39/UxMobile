package sk.uxtweak.uxmobile.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SessionEvent(
    @get:JsonProperty("recording_id") var recordingId: String?,
    @get:JsonProperty("session_id") var sessionId: String?,
    val event: Event
)
