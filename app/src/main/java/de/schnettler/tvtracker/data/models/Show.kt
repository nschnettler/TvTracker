package de.schnettler.tvtracker.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

data class ShowResponse(
    val title: String,
    val year: Long,
    val ids: ShowIdRemote
)

data class ShowIdRemote(
    val trakt: Long,
    val slug: String?,
    val tvdb: Long?,
    val imdb: String?,
    val tmdb: Long?
)

@Entity(tableName = "table_show")
data class ShowEntity(
    @PrimaryKey val id: Long,
    val tvdbId: Long?,
    val tmdbId: String,
    val title: String,
    var posterUrl: String,
    var backdropUrl: String
)

@Parcelize
data class ShowDomain(
    val id: Long,
    val tvdbId: Long?,
    val tmdbId: String,
    val title: String,
    var posterUrl: String = "",
    var backdropUrl: String = ""
): Parcelable

