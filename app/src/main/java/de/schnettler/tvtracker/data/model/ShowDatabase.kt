package de.schnettler.tvtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_shows_trending")
data class ShowDatabase(
    val title: String,
    @PrimaryKey val traktId: Long,
    val tmdbId: Long,
    val year: Long,
    val watchers: Long,
    var posterUrl: String
)

fun List<ShowDatabase>.asDomainModel(): List<Show> {
    return map {
        Show (
            title = it.title,
            tmdbId = it.tmdbId,
            traktId = it.traktId,
            year = it.year,
            watchers = it.watchers,
            posterUrl = it.posterUrl
        )
    }
}