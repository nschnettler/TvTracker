package de.schnettler.tvtracker.data.repository.show

import androidx.lifecycle.LiveData
import de.schnettler.tvtracker.data.models.*
import de.schnettler.tvtracker.util.TopListType

interface IShowRepository {
    //Details
    suspend fun refreshShowDetails(showId: Long)
    fun getShowDetails(showId: Long): LiveData<ShowDetailDomain?>

    //Cast
    suspend fun refreshCast(showId: Long, token: String)
    fun getShowCast(showId: Long): LiveData<List<CastEntity>?>

    //Related
    suspend fun refreshRelatedShows(showId: Long)
    fun getRelatedShows(showId: Long): LiveData<List<ShowDomain>?>

    //Listing
    suspend fun refreshShowList(type: TopListType, token: String = "")
    fun getTopList(type: TopListType): LiveData<List<ShowDomain>?>

    //Season
    suspend fun refreshSeasons(showId: Long)
    fun getSeasonsWithEpisodes(showId: Long): LiveData<List<SeasonDomain>?>

    //Poster
    suspend fun refreshPosters(showsDB: List<ShowEntity>)
}