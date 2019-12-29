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

@Entity(tableName = "table_episode")
data class EpisodeEntity(
    @PrimaryKey val id: Long,
    val showId: Long,
    val seasonId: Long,
    val season: Long,
    val number: Long,
    val title: String?,
    val overview: String?
)

@Parcelize
data class EpisodeDomain(
    val id: Long,
    val showId: Long,
    val seasonId: Long,
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