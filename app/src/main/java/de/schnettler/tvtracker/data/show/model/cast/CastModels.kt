package de.schnettler.tvtracker.data.show.model.cast

import androidx.room.Entity

data class CastListRemote(
    val data: List<CastEntryRemote>
)

data class CastEntryRemote(
    val id: Long,
    val image: String?,
    val name: String,
    val role: String,
    val seriesId: Long
)

@Entity(tableName = "table_cast", primaryKeys = ["id", "showId"])
data class CastEntry(
    val id: Long,
    val showId: Long,
    val name: String,
    val role: String,
    val image: String?
)

fun List<CastEntryRemote>.asCastEntryList() = this.map {
    CastEntry(
        id = it.id,
        showId = it.seriesId,
        name = it.name,
        role = it.role,
        image = it.image
    )
}