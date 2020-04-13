package de.schnettler.tvtracker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

data class ShowDetailResponse(
    val title: String,
    val year: Long,
    val ids: ShowIdRemote,
    val overview: String,
    @Json(name = "first_aired") val firstAired: String,
    val airs: ShowAirResponse,
    val runtime: String,
    val network: String?,
    val trailer: String?,
    val status: String,
    val rating: String,
    val genres: List<String>
)

@Entity(tableName = "table_show_details")
data class ShowDetailEntity(
    @PrimaryKey val showId: Long,
    val overview : String,
    val firstAired : String,
    //val airs: ShowAirInformationRemote,
    val runtime: String,
    val network: String?,
    val trailer: String?,
    val status: String,
    val rating: String,
    val genres: List<String>
)

data class ShowDetailDomain(
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

class ShowAirResponse(
    val day: String?,
    val time: String?,
    val timezone: String?
)