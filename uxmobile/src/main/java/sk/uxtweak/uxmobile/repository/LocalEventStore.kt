package sk.uxtweak.uxmobile.repository

import sk.uxtweak.uxmobile.model.event.Event

// NOTE: Might need to change database schema to match with new changes
class LocalEventStore(private val database: EventsDatabase) {
    suspend fun storeEvents(events: List<Event>) {
        // TODO: Store events in database
    }

    suspend fun deleteEvents(events: List<Event>) {
        // TODO: Delete events from database
    }

    suspend fun getAllLocalEvents(): List<Event> {
        // TODO: Query all events from database
        return listOf()
    }
}
