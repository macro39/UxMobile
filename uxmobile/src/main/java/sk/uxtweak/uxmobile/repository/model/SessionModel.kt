package sk.uxtweak.uxmobile.repository.model

import sk.uxtweak.uxmobile.repository.entities.SessionEntity

data class SessionModel(
    val session: SessionEntity,
    val activities: List<ActivityModel>
)
