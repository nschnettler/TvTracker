package de.schnettler.tvtracker.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.squareup.moshi.Json

data class EpisodeDetailResponse(
    @Json(name = "air_date") val airDate: String,
    @Json(name = "still_path") val stillPath: String?,
    @Json(name = "vote_average") val voteAverage: Float
)

@Entity(tableName = "table_episode_details")
data class EpisodeDetailEntity(
    @PrimaryKey val episodeId: Long,
    val airDate: String,
    val stillPath: String?,
    val voteAverage: Int
)

data class EpisodeDetailDomain(
    val episodeId: Long,
    val airDate: String,
    val stillPath: String?,
    val voteAverage: Int
)

data class EpisodeFullDomain(
    val id: Long,
    val seasonId: Long,
    val season: Long,
    val number: Long,
    val title: String?,
    val overview: String?,
    val airDate: String?,
    val stillPath: String?,
    val voteAverage: Int?
)

class EpisodeWithDetails(
    @Embedded val episode: EpisodeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "episodeId"
    )
    val details: EpisodeDetailEntity?
)