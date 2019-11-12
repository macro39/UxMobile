package sk.uxtweak.uxmobile.repository.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "events", foreignKeys = [ForeignKey(
        entity = ActivityEntity::class,
        parentColumns = ["id"],
        childColumns = ["activity_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "event_data") val eventData: String,
    @ColumnInfo(name = "activity_id", index = true) val activityId: Long
)
