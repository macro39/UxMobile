package sk.uxtweak.uxmobile.persister.room

import androidx.room.*

@Entity(
    tableName = "event_entities", foreignKeys = [ForeignKey(
        entity = SessionEntity::class,
        parentColumns = ["uuid"],
        childColumns = ["session_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["session_id"])]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "session_id") val sessionId: String,
    val json: String
)
