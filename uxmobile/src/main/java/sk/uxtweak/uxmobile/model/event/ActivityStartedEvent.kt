package sk.uxtweak.uxmobile.model.event

class ActivityStartedEvent(startTime: Long, private val activityName: String) : Event(startTime) {
    override fun getType() = TYPE_ACTIVITY_START

    override fun toJson() = super.toJson()
        .put(INDEX_ACTIVITY_NAME, activityName)

    companion object {
        const val INDEX_ACTIVITY_NAME = 2
    }
}
