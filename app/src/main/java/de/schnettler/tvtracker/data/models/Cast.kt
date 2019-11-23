package de.schnettler.tvtracker.data.models

import androidx.room.Entity

data class CastListResponse(
    val data: List<CastEntryResponse>
)

data class CastEntryResponse(
    val id: Long,
    val image: String?,
    val name: String,
    val role: String,
    val seriesId: Long
)

@Entity(tableName = "table_cast", primaryKeys = ["id", "showId"])
data class CastEntity(
    val id: Long,
    val showId: Long,
    val name: String,
    val role: String,
    val image: String?
)

fun List<CastEntryResponse>.asCastEntryList() = this.map {
    CastEntity(
        id = it.id,
        showId = it.seriesId,
        name = it.name,
        role = it.role,
        image = it.image
    )
}