package sk.uxtweak.uxmobile.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import sk.uxtweak.uxmobile.repository.daos.ActivityDao
import sk.uxtweak.uxmobile.repository.daos.EventDao
import sk.uxtweak.uxmobile.repository.daos.SessionDao
import sk.uxtweak.uxmobile.repository.entities.ActivityEntity
import sk.uxtweak.uxmobile.repository.entities.EventEntity
import sk.uxtweak.uxmobile.repository.entities.SessionEntity

@Database(entities = [SessionEntity::class, ActivityEntity::class, EventEntity::class], version = 1)
abstract class EventsDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun activityDao(): ActivityDao
    abstract fun eventDao(): EventDao
}
