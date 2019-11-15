package de.schnettler.tvtracker.data.show.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

//Trending Show
@Entity(tableName = "table_trending")
data class TrendingDB(
    @PrimaryKey val index: Int,
    val showId: Long,
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

@Entity(tableName = "table_show_details")
data class ShowDetailsDB(
    @PrimaryKey val showId: Long,
    val overview : String,
    val firstAired : String,
    //val airs: ShowAirInformationRemote,
    val runtime: String,
    val network: String,
    val trailer: String?,
    val status: String,
    val rating: String,
    val genres: List<String>
) {
    fun asShowDetails(): ShowDetails {
        return ShowDetails(
            showId = showId,
            overview = overview,
            firstAired = firstAired,
            runtime = runtime,
            network = network,
            trailer = trailer,
            status = status,
            rating = rating,
            genres = genres
        )
    }
}