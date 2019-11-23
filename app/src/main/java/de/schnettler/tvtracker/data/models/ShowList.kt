package de.schnettler.tvtracker.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.squareup.moshi.Json

//Trending
data class TrendingResponse(
    val watchers: Long,
    val show: ShowResponse
)

@Entity(tableName = "table_trending")
data class TrendingEntity(
    @PrimaryKey val index: Int,
    val showId: Long,
    val watcher: Long
)

class TrendingWithShow(
    @Embedded val trending: TrendingEntity,
    @Relation(
        parentColumn = "showId",
        entityColumn = "id"
    )
    val show: ShowEntity
)


//Popular
@Entity(tableName = "table_popular")
data class PopularEntity(
    val showId: Long,
    @PrimaryKey val index: Int
)

class PopularWithShow(
    @Embedded val popular: PopularEntity,
    @Relation(
        parentColumn = "showId",
        entityColumn = "id"
    )
    val show: ShowEntity
)


//Anticipated
data class AnticipatedResponse(
    @Json(name = "list_count") val listCount: Long,
    val show: ShowResponse
)

@Entity(tableName = "table_anticipated")
data class AnticipatedEntity(
    val showId: Long,
    @PrimaryKey val index: Int,
    val lists: Long
)

class AnticipatedWithShow(
    @Embedded val anticipated: AnticipatedEntity,
    @Relation(
        parentColumn = "showId",
        entityColumn = "id"
    )
    val show: ShowEntity
)