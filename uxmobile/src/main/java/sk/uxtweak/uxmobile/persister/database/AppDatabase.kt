package sk.uxtweak.uxmobile.persister.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [RecordingEntity::class, EventEntity::class, VideoEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordingDao(): RecordingDao
    abstract fun eventDao(): EventDao
    abstract fun videoDao(): VideoDao

    companion object {
        private const val DATABASE_NAME = "EventsDatabase"

        fun create(context: Context, inMemory: Boolean = false): AppDatabase {
            return if (inMemory) {
                Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
            } else {
                Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
            }
        }
    }
}
