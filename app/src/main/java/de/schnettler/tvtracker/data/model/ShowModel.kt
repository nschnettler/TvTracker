package de.schnettler.tvtracker.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Show(
    val id: Long,
    val title: String,
    var posterUrl: String = "",
    var backdropUrl: String = "",
    val index: Int
): Parcelable