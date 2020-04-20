package sk.uxtweak.uxmobile.model

import com.fasterxml.jackson.annotation.JsonProperty

data class EventsList(
    @get:JsonProperty("recording_id") val recordingId: Long,
    @get:JsonProperty("session_id") val sessionId: String,
    @get:JsonProperty("study_id") val studyId: Int?,
    val events: List<Event>
)
