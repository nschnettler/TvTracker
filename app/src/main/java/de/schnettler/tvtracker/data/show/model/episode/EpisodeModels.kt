package de.schnettler.tvtracker.data.show.model.episode

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.schnettler.tvtracker.data.show.model.ShowIdRemote

data class EpisodeResponse(
    val season: Long,
    val number: Long,
    val title: String?,
    val ids: ShowIdRemote
)

@Entity(tableName = "table_episode")
data class EpisodeEntity(
    @PrimaryKey val id: Long,
    val seasonId: Long,
    val season: Long,
    val number: Long,
    val title: String?
)

data class EpisodeDomain(
    val id: Long,
    val seasonId: Long,
    val season: Long,
    val number: Long,
    val title: String?
)