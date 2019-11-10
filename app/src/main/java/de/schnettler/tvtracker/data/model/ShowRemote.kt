package de.schnettler.tvtracker.data.model

import com.squareup.moshi.Json
import timber.log.Timber

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
        val imdb: String?,
        val tmdb: Long?
)

data class ShowImagesRemote(
        val poster_path: String,
        val backdrop_path: String
)

data class ShowDetailsRemote(
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
    val genres: List<String>) {

    fun asShowDetailsDB(): ShowDetailsDB = ShowDetailsDB(
        showId = ids.trakt,
        overview = overview,
        firstAired = firstAired,
        runtime = runtime,
        network = network,
        trailer = trailer,
        status = status,
        rating = rating/*,
        genres = genres */
    )
}

class ShowAirInformationRemote(
        val day: String,
        val time: String,
        val timezone: String
)

fun List<TrendingShowRemote>.asShowTrendingDB(page: Int): List<ShowTrendingDB>? {
        return mapIndexed {localIndex, it ->
            ShowTrendingDB(
                TrendingDB(
                    index = localIndex + 10 * page,
                    showId = it.show.ids.trakt,
                    watcher = it.watchers
                ),
                ShowDB(
                    id = it.show.ids.trakt,
                    title = it.show.title,
                    tmdbId = it.show.ids.tmdb.toString(),
                    posterUrl = "",
                    backdropUrl = ""
                )
            )
        }
}

fun List<ShowRemote>.asShowPopularDB(page: Int): List<ShowPopularDB>? {
        return mapIndexed {localIndex, it ->
                ShowPopularDB(
                        PopularDB(
                            showId = it.ids.trakt,
                            index = localIndex + 10 * page
                        ),
                        ShowDB(
                            id = it.ids.trakt,
                            title = it.title,
                            tmdbId = it.ids.tmdb.toString(),
                            posterUrl = "",
                            backdropUrl = ""
                        )
                )
        }
}