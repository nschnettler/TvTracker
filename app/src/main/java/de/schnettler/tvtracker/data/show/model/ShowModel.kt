package de.schnettler.tvtracker.data.show.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Show(
    val id: Long,
    val tvdbId: Long?,
    val tmdbId: String,
    val title: String,
    var posterUrl: String = "",
    var backdropUrl: String = ""
): Parcelable

data class ShowDetails(
    val showId: Long,
    val overview : String,
    val firstAired : String,
    //val airs: ShowAirInformationRemote,
    val runtime: String,
    val network: String,
    val trailer: String?,
    val status: String,
    val rating: String,
    val genres: List<String>
)