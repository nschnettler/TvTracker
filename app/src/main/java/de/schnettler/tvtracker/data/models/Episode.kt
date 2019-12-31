package de.schnettler.tvtracker.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

data class EpisodeResponse(
    val season: Long,
    val number: Long,
    val title: String?,
    val overview: String?,
    val ids: ShowIdRemote,
    val translations: List<EpisodeTranslationResponse>
)
data class EpisodeDetailResponse(
    @Json(name = "air_date") val airDate: String,
    @Json(name = "still_path") val stillPath: String?,
    @Json(name = "vote_average") val voteAverage: Float
)

@Entity(tableName = "table_episode", primaryKeys = ["showId", "season", "number"])
data class EpisodeEntity(
    val seasonId: String,
    val showId: Long,
    val season: Long,
    val number: Long,
    val title: String? = null,
    val overview: String? = null,
    val airDate: String? = null,
    val stillPath: String? = null,
    val voteAverage: Int? = null
)

@Parcelize
data class EpisodeDomain(
    val showId: Long,
    val season: Long,
    val number: Long,
    val title: String? = null,
    val overview: String? = null,
    val airDate: String? = null,
    val stillPath: String? = null,
    val voteAverage: Int? = null
): Parcelable

data class EpisodeTranslationResponse(
    val title: String?,
    val overview: String?,
    val language: String
)