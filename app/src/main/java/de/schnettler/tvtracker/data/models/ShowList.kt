package de.schnettler.tvtracker.data.models

import androidx.room.*
import com.squareup.moshi.Json

/*
 * Network
 */
sealed class ShowListResponse(
    open val show: ShowResponse,
    val ranking: Long? = null
)

//Trending
data class TrendingResponse(
    val watchers: Long,
    override val show: ShowResponse
): ShowListResponse (show, watchers)

//Popular
data class PopularResponse(
    val title: String,
    val year: Long,
    val ids: ShowIdRemote
): ShowListResponse(ShowResponse(title, year, ids))

//Anticipated
data class AnticipatedResponse(
    @Json(name = "list_count") val listCount: Long,
    override val show: ShowResponse
): ShowListResponse(show, listCount)


/*
 * DataBase
 */
@Entity(tableName = "table_discover", primaryKeys = ["type", "index"])
data class TopListEntity(
    val type: String,
    val index: Int,
    val showId: Long,
    val ranking: Long? = null
)

/*
 DataBase Relations
 */
data class TopListWithShow(
    @Embedded val listing: TopListEntity,
    @Relation(
        parentColumn = "showId",
        entityColumn = "id"
    )
    val show: ShowEntity
)