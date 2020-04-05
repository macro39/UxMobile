package sk.uxtweak.uxmobile.persister.room

import androidx.room.*

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(events: List<EventEntity>)

    @Delete
    suspend fun delete(events: List<EventEntity>)

    @Query("SELECT * FROM event_entities")
    fun getAll(): List<EventEntity>

    @Query("SELECT COUNT(*) FROM event_entities")
    fun count(): Long
}
