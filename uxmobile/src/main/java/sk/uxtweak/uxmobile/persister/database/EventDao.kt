package sk.uxtweak.uxmobile.persister.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(events: List<EventEntity>)

    @Delete
    suspend fun delete(events: List<EventEntity>)

    @Delete
    fun deleteEvents(events: List<EventEntity>)

    @Query("SELECT * FROM events")
    suspend fun getAll(): List<EventEntity>

    @Query("SELECT * FROM events WHERE recording_id = :recordingId")
    fun getForRecording(recordingId: Long): List<EventEntity>

    @Query("SELECT * FROM events WHERE recording_id = :recordingId")
    fun getForRecordingLive(recordingId: Long): LiveData<List<EventEntity>>

    @Query("SELECT COUNT(*) FROM events WHERE recording_id = :id")
    fun countForId(id: String): Long
}
