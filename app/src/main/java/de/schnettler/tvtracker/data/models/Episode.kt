package de.schnettler.tvtracker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class EpisodeResponse(
    val season: Long,
    val number: Long,
    val title: String?,
    val overview: String?,
    val ids: ShowIdRemote,
    val translations: List<EpisodeTranslationResponse>
)

@Entity(tableName = "table_episode")
data class EpisodeEntity(
    @PrimaryKey val id: Long,
    val seasonId: Long,
    val season: Long,
    val number: Long,
    val title: String?,
    val overview: String?
)

data class EpisodeDomain(
    val id: Long,
    val seasonId: Long,
    val season: Long,
    val number: Long,
    val title: String?,
    val overview: String?
)

data class EpisodeTranslationResponse(
    val title: String?,
    val overview: String?,
    val language: String
)