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
sealed class ShowListEntity

//Trending
@Entity(tableName = "table_trending")
data class TrendingEntity(
    @PrimaryKey val index: Int,
    val showId: Long,
    val watcher: Long
): ShowListEntity()

//Popular
@Entity(tableName = "table_popular")
data class PopularEntity(
    val showId: Long,
    @PrimaryKey val index: Int
): ShowListEntity()

//Anticipated
@Entity(tableName = "table_anticipated")
data class AnticipatedEntity(
    val showId: Long,
    @PrimaryKey val index: Int,
    val lists: Long
): ShowListEntity()



/*
 DataBase Relations
 */
open class ListingWithShow(
    @Ignore open val show: ShowEntity
)
class TrendingWithShow(
    @Embedded val trending: TrendingEntity,
    @Relation(
        parentColumn = "showId",
        entityColumn = "id"
    )
    override val show: ShowEntity
): ListingWithShow(show)
class PopularWithShow(
    @Embedded val popular: PopularEntity,
    @Relation(
        parentColumn = "showId",
        entityColumn = "id"
    )
    override val show: ShowEntity
): ListingWithShow(show)
class AnticipatedWithShow(
    @Embedded val anticipated: AnticipatedEntity,
    @Relation(
        parentColumn = "showId",
        entityColumn = "id"
    )
    override val show: ShowEntity
): ListingWithShow(show)