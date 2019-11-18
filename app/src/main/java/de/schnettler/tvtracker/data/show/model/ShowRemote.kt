package de.schnettler.tvtracker.data.show.model

import com.squareup.moshi.Json
import kotlin.math.roundToInt

data class TrendingShowRemote(
    val watchers: Long,
    val show: ShowRemote
)

data class AnticipatedShowRemote(
    @Json(name = "list_count") val listCount: Long,
    val show: ShowRemote
)

data class ShowRemote(
    val title: String,
    val year: Long,
    val ids: ShowIdRemote
)

data class ShowIdRemote(
    val trakt: Long,
    val slug: String,
    val tvdb: Long?,
    val imdb: String?,
    val tmdb: Long?
)

data class ShowImagesRemote(
    val poster_path: String,
    val backdrop_path: String
)

data class PersonImageRemote(
    val profile_path: String
)

data class ShowDetailsRemote(
    val title: String,
    val year: Long,
    val ids: ShowIdRemote,
    val overview: String,
    @Json(name = "first_aired") val firstAired: String,
    val airs: ShowAirInformationRemote,
    val runtime: String,
    val network: String,
    val trailer: String?,
    val status: String,
    val rating: String,
    val genres: List<String>
)

class ShowAirInformationRemote(
    val day: String?,
    val time: String?,
    val timezone: String?
)