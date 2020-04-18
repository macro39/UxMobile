package sk.uxtweak.uxmobile.persister.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordings")
data class RecordingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "session_id") val sessionId: String,
    @ColumnInfo(name = "study_id") val studyId: String?
)
