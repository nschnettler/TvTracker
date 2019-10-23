package de.schnettler.tvtracker.data.model

import androidx.room.Embedded
import androidx.room.Relation


class ShowTrendingDB(
    @Embedded val trending: TrendingDB,
    @Relation(
        parentColumn = "showId",
        entityColumn = "id"
    )
    val show: ShowDB
)

class ShowPopularDB(
    @Embedded val popular: PopularDB,
    @Relation(
        parentColumn = "showId",
        entityColumn = "id"
    )
    val show: ShowDB
)