package sk.uxtweak.uxmobile.persister.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VideoDao {
    @Insert
    fun insert(videoEntity: VideoEntity): Long

    @Delete
    fun delete(videoEntity: VideoEntity)

    @Query("SELECT * FROM videos")
    fun getAll(): LiveData<List<VideoEntity>>
}
