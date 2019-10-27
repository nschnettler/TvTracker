package de.schnettler.tvtracker.data.model

import androidx.room.*
import timber.log.Timber

//Trending Show
@Entity(tableName = "table_trending")
data class TrendingDB(
    @PrimaryKey val showId: Long,
    val watcher: Long
)

//Popular Show
@Entity(tableName = "table_popular")
data class PopularDB(
    @PrimaryKey val showId: Long,
    val index: Int
)

//Show (Short)
@Entity(tableName = "table_show")
data class ShowDB(
    @PrimaryKey val id: Long,
    val title: String,
    var posterUrl: String,
    var backdropUrl: String
) {
    @Ignore var tmdbId: String = ""

    constructor(id: Long, title: String,  posterUrl: String, backdropUrl: String, tmdbId: String): this(id, title, posterUrl, backdropUrl) {
        this.tmdbId = tmdbId
    }
}


fun List<ShowTrendingDB>.asTrendingShow(): List<Show> {
    return map {
        Show (
            id = it.show.id,
            title = it.show.title,
            posterUrl = it.show.posterUrl,
            backdropUrl = it.show.backdropUrl
        )
    }
}

fun List<ShowPopularDB>.asPopularShow(): List<Show> {
    return map {
        Show(
            id = it.show.id,
            title = it.show.title,
            posterUrl = it.show.posterUrl,
            backdropUrl = it.show.backdropUrl
        )
    }
}