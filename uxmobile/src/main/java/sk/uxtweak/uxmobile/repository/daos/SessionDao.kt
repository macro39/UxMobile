package sk.uxtweak.uxmobile.repository.daos

import androidx.room.*
import sk.uxtweak.uxmobile.repository.entities.SessionEntity

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions")
    suspend fun getAll(): List<SessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: SessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg sessions: SessionEntity): List<Long>
}
