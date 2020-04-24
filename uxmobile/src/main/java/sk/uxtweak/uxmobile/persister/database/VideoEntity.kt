package sk.uxtweak.uxmobile.persister.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "recording_id") val recordingId: Long,
    @ColumnInfo(name = "chunk_id") val chunkId: Int
) {
    val path: String
        get() = "$recordingId/$chunkId.mp4"
}
