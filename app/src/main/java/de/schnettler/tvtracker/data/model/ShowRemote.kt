package de.schnettler.tvtracker.data.model

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

fun List<TrendingShowRemote>.asShow(): List<Show>? {
        return map {
                Show (
                        id = it.show.ids.trakt,
                        title = it.show.title
                )
        }
}


fun List<TrendingShowRemote>.asShowDB(): List<ShowTrendingDB>? {
        return map {
                ShowTrendingDB(
                        TrendingDB(
                                showId = it.show.ids.trakt,
                                watcher = it.watchers
                        ),
                        ShowDB(
                                id = it.show.ids.trakt,
                                title = it.show.title,
                                posterUrl = ""
                        )
                )
        }
}