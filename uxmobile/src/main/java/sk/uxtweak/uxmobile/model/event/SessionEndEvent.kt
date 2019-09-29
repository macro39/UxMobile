package sk.uxtweak.uxmobile.model.event

class SessionEndEvent(startTime: Long) : Event(startTime) {
    override fun getType() = TYPE_SESSION_END
}
