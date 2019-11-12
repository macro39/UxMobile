package sk.uxtweak.uxmobile.repository.daos

import androidx.room.*
import sk.uxtweak.uxmobile.repository.entities.ActivityEntity

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities")
    suspend fun getAll(): List<ActivityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: ActivityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg activities: ActivityEntity): List<Long>
}
