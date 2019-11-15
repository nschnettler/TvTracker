package de.schnettler.tvtracker.data.show.model

import androidx.room.Embedded
import androidx.room.Relation
import de.schnettler.tvtracker.data.person.model.CastDB
import de.schnettler.tvtracker.data.person.model.CastEntry
import de.schnettler.tvtracker.data.person.model.PersonDB


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
            actorId = person.id,
            posterUrl = person.imageUrl
        )
    }
}

fun List<ShowCastEntryDB>.toCastEntries() = this.map {
    it.toCastEntry()
}.flatten()