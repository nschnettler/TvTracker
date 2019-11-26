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
open class ShowListEntity(
    @Ignore open val index: Int,
    @Ignore open val showId: Long,
    @Ignore open val ranking: Long? = null
)

//Trending
@Entity(tableName = "table_trending")
data class TrendingEntity(
    @PrimaryKey override val index: Int,
    override val showId: Long,
    val watcher: Long
): ShowListEntity(index, showId, watcher)

//Popular
@Entity(tableName = "table_popular")
data class PopularEntity(
    override val showId: Long,
    @PrimaryKey override val index: Int
): ShowListEntity(index, showId)

//Anticipated
@Entity(tableName = "table_anticipated")
data class AnticipatedEntity(
    override val showId: Long,
    @PrimaryKey override val index: Int,
    val lists: Long
): ShowListEntity(index, showId, lists)



/*
 DataBase Relations
 */
open class ListingWithShow(
    @Ignore open val listing: ShowListEntity,
    @Ignore open val show: ShowEntity
)
class TrendingWithShow(
    @Embedded val trending: TrendingEntity,
    @Relation(
        parentColumn = "showId",
        entityColumn = "id"
    )
    override val show: ShowEntity
): ListingWithShow(trending, show)
class PopularWithShow(
    @Embedded val popular: PopularEntity,
    @Relation(
        parentColumn = "showId",
        entityColumn = "id"
    )
    override val show: ShowEntity
): ListingWithShow(popular, show)
class AnticipatedWithShow(
    @Embedded val anticipated: AnticipatedEntity,
    @Relation(
        parentColumn = "showId",
        entityColumn = "id"
    )
    override val show: ShowEntity
): ListingWithShow(anticipated, show)