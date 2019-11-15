package de.schnettler.tvtracker.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "table_person")
data class PersonDB(
    @PrimaryKey val id: Long,
    val name: String,
    var tmdbId: String,
    var imageUrl: String? = null
)

@Entity(tableName = "table_cast", primaryKeys = ["showId", "personId"])
data class CastDB(
    val showId: Long,
    val personId: Long,
    val episodeCount: Long,
    val characters: List<String>
)