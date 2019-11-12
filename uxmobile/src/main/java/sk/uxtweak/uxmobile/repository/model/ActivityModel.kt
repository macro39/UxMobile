package sk.uxtweak.uxmobile.repository.model

import sk.uxtweak.uxmobile.repository.entities.ActivityEntity
import sk.uxtweak.uxmobile.repository.entities.EventEntity

data class ActivityModel(
    val activity: ActivityEntity,
    val events: List<EventEntity>
)
