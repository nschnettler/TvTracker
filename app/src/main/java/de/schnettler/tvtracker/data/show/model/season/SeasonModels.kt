package de.schnettler.tvtracker.data.show.model.season

import androidx.room.Entity
import com.squareup.moshi.Json

data class SeasonResponse(
    val number: Long,
    val ids: SeasonIdResponse,
    val rating: Float,
    val title: String,
    val overview: String?,
    @Json(name = "first_aired") val firstAired: String?,
    @Json(name = "episode_count") val episodeCount: Long?
)

data class SeasonIdResponse(
    val trakt: Long,
    val tvdb: Long?,
    val tmdb: Long?
)

@Entity(tableName = "table_seasons", primaryKeys = ["showId", "number"])
data class SeasonEntity(
    val number: Long,
    val showId: Long,
    val id: Long,
    val rating: Long,
    val title: String,
    val overview: String?,
    val firstAired: String?,
    val episodeCount: Long?
)

data class SeasonDomain(
    val number: Long,
    val id: Long,
    val rating: Long,
    val title: String,
    val overview: String?,
    val firstAired: String?,
    val episodeCount: Long?,
    val isExpanded: Boolean = false
)