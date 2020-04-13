package sk.uxtweak.uxmobile.persister.database

import androidx.room.*

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(session: SessionEntity): Long

    @Update
    fun update(session: SessionEntity)

    @Delete
    fun delete(session: SessionEntity)

    @Query("SELECT * FROM session_entities")
    fun getAll(): List<SessionEntity>

    @Query("""
        SELECT session_entities.* FROM session_entities
        JOIN event_entities ON session_entities.uuid = event_entities.session_id
        GROUP BY session_entities.uuid
        HAVING COUNT(session_entities.uuid) > 0
        LIMIT 1
    """)
    fun first(): SessionEntity?
}
