package de.schnettler.tvtracker.data.repository.show

import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.api.TmdbAPI
import de.schnettler.tvtracker.data.api.TraktAPI
import de.schnettler.tvtracker.data.api.TvdbAPI
import de.schnettler.tvtracker.data.models.*
import de.schnettler.tvtracker.util.TopListType
import de.schnettler.tvtracker.util.safeApiCall
import timber.log.Timber
import java.io.IOException
import java.util.*

/**
 * Work with the Trakt API to get shows. The class knows how to construct the requests.
 */
class ShowDataSourceRemote(val traktService: TraktAPI, val tvdbService: TvdbAPI, val tmdbService: TmdbAPI) {
    //Show Details
    suspend fun getShowDetails(showID: Long) = safeApiCall(
        call = { requestShowDetails(showID) },
        errorMessage = "Error getting Show Details"
    )

    private suspend fun requestShowDetails(showID: Long): Result<ShowDetailResponse> {
        val response = traktService.getShowSummary(showID)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(
            IOException("Error getting show details: ${response.code()} ${response.message()}")
        )
    }


    //Cast
    suspend fun getCast(showID: Long, token: String) = safeApiCall(
        call = { requestCast(showID, token) },
        errorMessage = "Error getting Cast"
    )

    private suspend fun requestCast(showID: Long, token: String): Result<CastListResponse> {
        val response = tvdbService.getActors(TvdbAPI.AUTH_PREFIX + token, showID)
        Timber.i("RESPONSE $response.toString()")

        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting cast: ${response.code()} ${response.message()}"))
    }


    //Related Shows
    suspend fun getRelated(showID: Long) = safeApiCall(
        call = { requestRelatedShows(showID) },
        errorMessage = "Error getting Related Shows"
    )

    private suspend fun requestRelatedShows(showID: Long): Result<List<ShowResponse>> {
        val response = traktService.getRelatedShows(showID)

        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting related shows: ${response.code()} ${response.message()}"))
    }


    //Poster
    suspend fun getImages(tmdbId: String) = safeApiCall(
        call = { requestShowImages(tmdbId) },
        errorMessage = "Error loading Poster for $tmdbId"
    )

    private suspend fun requestShowImages(tmdbId: String): Result<ShowImageResponse> {
        val response = tmdbService.getShowPoster(tmdbId, TmdbAPI.API_KEY)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting show images: ${response.code()} ${response.message()}"))
    }


    suspend fun getTopList(type: TopListType, token: String) = safeApiCall(
        call = { refreshTopList(type, token) },
        errorMessage = "Error requesting TopList"
    )

    private suspend fun refreshTopList(type: TopListType, token: String): Result<List<ShowListResponse>> {
        val response = when(type) {
            TopListType.TRENDING -> traktService.getTrendingShows(0, TraktAPI.DISCOVER_AMOUNT)
            TopListType.POPULAR -> traktService.getPopularShows(0, TraktAPI.DISCOVER_AMOUNT)
            TopListType.ANTICIPATED -> traktService.getAnticipated()
            TopListType.RECOMMENDED -> traktService.getRecommended("Bearer $token")
        }
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting ${type.name} shows: ${response.code()} ${response.message()}"))
    }


    //Seasons
    suspend fun getSeasonsOfShow(showID: Long) = safeApiCall(
        call = { refreshSeasonsOfShow(showID) },
        errorMessage = "Error loading Seasons"
    )

    private suspend fun refreshSeasonsOfShow(showID: Long): Result<List<SeasonResponse>> {
        val response = traktService.getShowSeasons(showID)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting seasons: ${response.code()} ${response.message()}"))
    }


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