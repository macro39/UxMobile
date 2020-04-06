package sk.uxtweak.uxmobile.persister.room

import androidx.room.*

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(events: List<EventEntity>)

    @Delete
    suspend fun delete(events: List<EventEntity>)

    @Delete
    fun deleteEvents(events: List<EventEntity>)

    @Query("SELECT * FROM event_entities")
    suspend fun getAll(): List<EventEntity>

    @Query("SELECT * FROM event_entities WHERE session_id = :sessionId")
    fun getForSessionId(sessionId: String): List<EventEntity>

    @Query("SELECT COUNT(*) FROM event_entities WHERE session_id = :id")
    fun countForId(id: String): Long
}
