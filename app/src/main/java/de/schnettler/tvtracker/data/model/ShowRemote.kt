package de.schnettler.tvtracker.data.model

import com.squareup.moshi.Json

data class TrendingShowRemote(
        val watchers: Long,
        val show: ShowRemote
)

data class ShowRemote(
        val title: String,
        val year: Long,
        val ids: ShowIdRemote)

data class ShowIdRemote(
        val trakt: Long,
        val slug: String,
        val tvdb: Long,
        val imdb: String,
        val tmdb: Long
)

data class ShowImagesRemote(
        val poster_path: String,
        val backdrop_path: String
)

data class ShowDetails(
    val title: String,
    val year: Long,
    val ids: ShowIdRemote,
    val overview : String,
    @Json(name = "first_aired") val firstAired : String,
    val airs: ShowAirInformationRemote,
    val runtime: String,
    val network: String,
    val trailer: String,
    val status: String,
    val rating: String,
    val genres: List<String>)

class ShowAirInformationRemote(
        val day: String,
        val time: String,
        val timezone: String
)

object Adapter {
}

fun List<TrendingShowRemote>.asShow(): List<Show>? {
        return map {
                Show (
                        id = it.show.ids.trakt,
                        title = it.show.title
                )
        }
}


fun List<TrendingShowRemote>.asShowTrendingDB(): List<ShowTrendingDB>? {
        return map {
                ShowTrendingDB(
                        TrendingDB(
                                showId = it.show.ids.trakt,
                                watcher = it.watchers
                        ),
                        ShowDB(
                                id = it.show.ids.trakt,
                                title = it.show.title,
                                tmdbId = it.show.ids.tmdb.toString(),
                                posterUrl = ""
                        )
                )
        }
}

fun List<ShowRemote>.asShowPopularDB(): List<ShowPopularDB>? {
        return mapIndexed {index, it ->
                ShowPopularDB(
                        PopularDB(
                                showId = it.ids.trakt,
                                index = index
                        ),
                        ShowDB(
                                id = it.ids.trakt,
                                title = it.title,
                                tmdbId = it.ids.tmdb.toString(),
                                posterUrl = ""
                        )
                )
        }
}