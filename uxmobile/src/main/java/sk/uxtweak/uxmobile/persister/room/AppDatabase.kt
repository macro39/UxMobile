package sk.uxtweak.uxmobile.persister.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SessionEntity::class, EventEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun eventDao(): EventDao

    companion object {
        fun create(context: Context, inMemory: Boolean = false): AppDatabase {
            return if (inMemory) {
                Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
            } else {
                Room.databaseBuilder(context, AppDatabase::class.java, "EventsDatabase").build()
            }
        }
    }
}
