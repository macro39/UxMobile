package sk.uxtweak.uxmobile.persister.room

import androidx.room.*

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(session: SessionEntity): Long

    @Delete
    fun delete(vararg sessions: SessionEntity)

    @Query("SELECT * FROM session_entities")
    fun getAll(): List<SessionEntity>
}
