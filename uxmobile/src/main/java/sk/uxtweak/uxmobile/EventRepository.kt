package sk.uxtweak.uxmobile

import sk.uxtweak.uxmobile.model.events.Event

interface EventRepository {
    fun persistEvent(event: Event)
}

class NetworkRepository : EventRepository {
    override fun persistEvent(event: Event) {

    }
}

class MemoryRepository : EventRepository {
    override fun persistEvent(event: Event) {

    }
}

class DatabaseRepository : EventRepository {
    override fun persistEvent(event: Event) {

    }
}

class EventStore : EventRepository {
    override fun persistEvent(event: Event) {

    }
}
