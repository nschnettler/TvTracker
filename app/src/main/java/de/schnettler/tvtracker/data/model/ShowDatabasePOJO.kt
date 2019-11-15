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

class ShowCastEntryDB(
    @Embedded val cast: CastDB,
    @Relation(
        parentColumn = "personId",
        entityColumn = "id"
    )
    val person: PersonDB
) {
    fun toCastEntry() = cast.characters.map {
        CastEntry(
            character = it,
            actor = person.name,
            actorId = person.id
        )
    }
}

fun List<ShowCastEntryDB>.toCastEntries() = this.map {
    it.toCastEntry()
}.flatten()