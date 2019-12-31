package de.schnettler.tvtracker.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

data class EpisodeResponse(
    val season: Long,
    val number: Long,
    val title: String?,
    val overview: String?,
    val ids: ShowIdRemote,
    val translations: List<EpisodeTranslationResponse>
)

@Entity(tableName = "table_episode", primaryKeys = ["showId", "season", "number"])
data class EpisodeEntity(
    val seasonId: String,
    val episodeId: String,
    val showId: Long,
    val season: Long,
    val number: Long,
    val title: String?,
    val overview: String?
)

@Parcelize
data class EpisodeDomain(
    val showId: Long,
    val season: Long,
    val number: Long,
    val title: String?,
    val overview: String?
): Parcelable

data class EpisodeTranslationResponse(
    val title: String?,
    val overview: String?,
    val language: String
)