package sk.uxtweak.uxmobile.persister.database

import androidx.room.*

@Dao
interface RecordingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(recording: RecordingEntity): Long

    @Delete
    suspend fun delete(recording: RecordingEntity)

    @Update
    suspend fun update(recording: RecordingEntity)

    @Query("SELECT * FROM recordings")
    fun getAll(): List<RecordingEntity>

    @Query("SELECT * FROM recordings WHERE id != :recordingId")
    fun getAllExcept(recordingId: Long): List<RecordingEntity>

    @Query("SELECT * FROM recordings WHERE id = :id")
    fun getById(id: Long): RecordingEntity

    @Query("SELECT * FROM recordings WHERE session_id = :sessionId")
    fun getForSessionId(sessionId: String): List<RecordingEntity>
}
