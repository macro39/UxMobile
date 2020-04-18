package sk.uxtweak.uxmobile.persister.database

import androidx.room.*

@Entity(
    tableName = "events", foreignKeys = [ForeignKey(
        entity = RecordingEntity::class,
        parentColumns = ["id"],
        childColumns = ["recording_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["recording_id"])]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "recording_id") val recordingId: String,
    val json: String
)
