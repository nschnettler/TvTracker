package de.schnettler.tvtracker.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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

//Show (Short)
@Entity(tableName = "table_show")
data class ShowDB(
    @PrimaryKey val id: Long,
    val title: String,
    var posterUrl: String
)

class ShowTrendingDB(@Embedded val trending: TrendingDB, @Embedded val show: ShowDB)

fun List<ShowTrendingDB>.asShow(): List<Show> {
    return map {
        Show (
            id = it.show.id,
            title = it.show.title,
            posterUrl = it.show.posterUrl
        )
    }
}