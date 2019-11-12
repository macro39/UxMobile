package sk.uxtweak.uxmobile.repository.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import sk.uxtweak.uxmobile.repository.entities.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    suspend fun getAll(): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg events: EventEntity): List<Long>
}
