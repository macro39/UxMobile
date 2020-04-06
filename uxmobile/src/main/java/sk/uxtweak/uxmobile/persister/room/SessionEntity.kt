package sk.uxtweak.uxmobile.persister.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_entities")
data class SessionEntity(
    @PrimaryKey val uuid: String
)
