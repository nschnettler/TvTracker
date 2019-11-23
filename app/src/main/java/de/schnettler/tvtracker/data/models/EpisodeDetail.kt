package de.schnettler.tvtracker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class EpisodeDetailsResponse(
    val ids: ShowIdRemote,
    val firstAired: String,
    val rating: Int
)

@Entity(tableName = "table_episode_details")
data class EpisodeDetailsEntity(
    @PrimaryKey val id: Long,
    val title: String?
)