package de.schnettler.tvtracker.data.show.model.season

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.squareup.moshi.Json
import de.schnettler.tvtracker.data.show.model.episode.EpisodeDomain
import de.schnettler.tvtracker.data.show.model.episode.EpisodeEntity

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

data class SeasonWithEpisodes(
    @Embedded val season: SeasonEntity,
    @Relation(
        parentColumn = "id",
        entity = EpisodeEntity::class,
        entityColumn = "seasonId"
    )
    val episodes: List<EpisodeEntity>
)

data class SeasonDomain(
    val number: Long,
    val id: Long,
    val rating: Long,
    val title: String,
    val overview: String?,
    val firstAired: String?,
    val episodeCount: Long?,
    var episodes: List<EpisodeDomain>? = null,
    val isExpanded: Boolean = false
)