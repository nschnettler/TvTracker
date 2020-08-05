package de.schnettler.tvtracker.data.repository.show

import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.api.TmdbAPI
import de.schnettler.tvtracker.data.api.TraktAPI
import de.schnettler.tvtracker.data.models.*
import de.schnettler.tvtracker.util.safeApiCall
import java.io.IOException
import java.util.*
import javax.inject.Inject

/**
 * Work with the Trakt API to get shows. The class knows how to construct the requests.
 */
class ShowDataSourceRemote @Inject constructor(
    val traktService: TraktAPI,
    val tmdbService: TmdbAPI
) {
    //Episodes
    suspend fun getEpisodesOfSeason(showID: Long, seasonNumber: Long) = safeApiCall(
        call = { refreshEpisodesOfSeason(showID, seasonNumber) },
        errorMessage = "Error loading Seasons"
    )

    private suspend fun refreshEpisodesOfSeason(
        showID: Long,
        seasonNumber: Long
    ): Result<List<EpisodeResponse>> {
        val response = traktService.getSeasonEpisodes(showID, seasonNumber)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting seasons: ${response.code()} ${response.message()}"))
    }


    //Episode Details
    //Episode Details
    suspend fun getEpisodeDetail(showID: String, seasonNumber: Long, episodeNumber: Long) = safeApiCall(
        call = { requestEpisodeDetails(showID, seasonNumber, episodeNumber) },
        errorMessage = "Error getting Episode Details"
    )

    private suspend fun requestEpisodeDetails(showID: String, seasonNumber: Long, episodeNumber: Long): Result<EpisodeDetailResponse> {
        val response = tmdbService.getEpisodeDetail(showID, seasonNumber, episodeNumber, TmdbAPI.API_KEY, Locale.getDefault().language)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(
            IOException("Error getting Episode details: ${response.code()} ${response.message()}")
        )
    }
}