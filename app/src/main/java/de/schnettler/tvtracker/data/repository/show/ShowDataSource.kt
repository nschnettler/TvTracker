package de.schnettler.tvtracker.data.repository.show

import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.api.TMDb
import de.schnettler.tvtracker.data.db.ShowDao
import de.schnettler.tvtracker.data.api.Trakt
import de.schnettler.tvtracker.data.api.TVDB
import de.schnettler.tvtracker.data.models.*
import de.schnettler.tvtracker.util.safeApiCall
import timber.log.Timber
import java.io.IOException

/**
 * Work with the Trakt API to get shows. The class knows how to construct the requests.
 */
class ShowDataSourceRemote(val traktService: Trakt, val tvdbService: TVDB, val tmdbService: TMDb) {
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
        val response = tvdbService.getActors(TVDB.AUTH_PREFIX + token, showID)
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
        val response = tmdbService.getShowPoster(tmdbId, TMDb.API_KEY)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting show images: ${response.code()} ${response.message()}"))
    }


    //Trending Shows
    suspend fun getTrendingShows() = safeApiCall(
        call = { requestTrendingShows() },
        errorMessage = "Error loading Trending Shows"
    )

    private suspend fun requestTrendingShows(): Result<List<TrendingResponse>> {
        val response = traktService.getTrendingShows(0, Trakt.DISCOVER_AMOUNT)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting trending shows: ${response.code()} ${response.message()}"))
    }


    //Popular Shows
    suspend fun getPopularShows() = safeApiCall(
        call = { requestPopularShows() },
        errorMessage = "Error loading Popular Shows"
    )

    private suspend fun requestPopularShows(): Result<List<ShowResponse>> {
        val response = traktService.getPopularShows(0, Trakt.DISCOVER_AMOUNT)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting popular shows: ${response.code()} ${response.message()}"))
    }

    //Anticipated Shows
    suspend fun getAnticipated() = safeApiCall(
        call = { refreshAnticipated() },
        errorMessage = "Error loading anticipated Shows"
    )

    private suspend fun refreshAnticipated(): Result<List<AnticipatedResponse>> {
        val response = traktService.getAnticipated()
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting anticipated shows: ${response.code()} ${response.message()}"))
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
        val response = traktService.getSeasonEpisodes(showID, seasonNumber, "de")
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting seasons: ${response.code()} ${response.message()}"))
    }
}


class ShowDataSourceLocal(val dao: ShowDao) {
    //Show
    suspend fun updateShow(show: ShowEntity) {
        dao.updateShow(show)
    }

    //Show Details
    suspend fun insertShowDetails(showDetailsDB: ShowDetailEntity) {
        dao.insertShowDetails(showDetailsDB)
    }

    fun getShowDetail(showID: Long) = dao.getShowDetails(showID)


    //ShowCast
    suspend fun insertShowCast(cast: List<CastEntity>) {
        dao.insertCast(cast)
    }

    fun getShowCast(showID: Long) = dao.getCast(showID)


    //Related Shows
    suspend fun insertRelatedShows(relatedShows: List<RelationWithShow>?) {
        relatedShows?.let { dao.insertShowRelations(relatedShows) }
    }

    fun getRelatedShows(showID: Long) = dao.getShowRelations(showID)


    //Trending Shows
    suspend fun insertTrending(shows: List<TrendingWithShow>?) {
        shows?.let { dao.insertTrendingShows(it) }
    }

    fun getTrending() = dao.getTrending()


    //Popular Shows
    suspend fun insertPopular(shows: List<PopularWithShow>?) {
        shows?.let { dao.insertPopularShows(it) }
    }

    fun getPopular() = dao.getPopular()


    //Anticipated Shows
    suspend fun insertAnticipated(shows: List<AnticipatedWithShow>?) {
        shows?.let { dao.insertAnticipatedShows(it) }
    }

    fun getAnticipated() = dao.getAnticipated()


    //Seasons & Episodes
    suspend fun insertSeasons(shows: List<SeasonEntity>?) {
        shows?.let { dao.insertSeasons(it) }
    }

    suspend fun insertEpisodes(episodes: List<EpisodeEntity>?) {
        episodes?.let { dao.insertEpisodes(it) }
    }

    fun getSeasonsWithEpisodes(showID: Long) = dao.getSeasonsWithEpisodes(showID)
}