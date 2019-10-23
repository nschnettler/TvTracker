package de.schnettler.tvtracker.data.model

import androidx.room.*
import timber.log.Timber

//Trending Show
@Entity(tableName = "table_trending", foreignKeys = [ForeignKey(
    entity = ShowDB::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("showId"))]
)
data class TrendingDB(
    @PrimaryKey val showId: Long,
    val watcher: Long
)

//Popular Show
@Entity(tableName = "table_popular", foreignKeys = [ForeignKey(
    entity = ShowDB::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("showId"))]
)
data class PopularDB(
    @PrimaryKey val showId: Long,
    val index: Int
)

//Show (Short)
@Entity(tableName = "table_show")
data class ShowDB(
    @PrimaryKey val id: Long,
    val title: String,
    var posterUrl: String
) {
    @Ignore var tmdbId: String = ""

    constructor(id: Long, title: String,  posterUrl: String, tmdbId: String): this(id, title, posterUrl) {
        this.tmdbId = tmdbId
    }
}



class ShowTrendingDB(@Embedded val trending: TrendingDB, @Embedded val show: ShowDB)
class ShowPopularDB(@Embedded val popular: PopularDB, @Embedded val show: ShowDB)


fun List<ShowTrendingDB>.asTrendingShow(): List<Show> {
    return map {
        Show (
            id = it.show.id,
            title = it.show.title,
            posterUrl = it.show.posterUrl
        )
    }
}

fun List<ShowPopularDB>.asPopularShow(): List<Show> {
    return map {
        Show(
            id = it.show.id,
            title = it.show.title,
            posterUrl = it.show.posterUrl
        )
    }
}