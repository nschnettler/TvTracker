package de.schnettler.tvtracker.data.model

import androidx.room.*
import timber.log.Timber

//Trending Show
@Entity(tableName = "table_trending")
data class TrendingDB(
    val index: Int,
    @PrimaryKey val showId: Long,
    val watcher: Long
)

//Popular Show
@Entity(tableName = "table_popular")
data class PopularDB(
    val showId: Long,
    @PrimaryKey val index: Int
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

    fun asShow(index: Int): Show {
        return Show(
            id = this.id,
            title = this.title,
            posterUrl = this.posterUrl,
            backdropUrl = this.backdropUrl,
            index = index
        )
    }
}