package de.schnettler.tvtracker.data.show

import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.db.TrendingShowsDAO
import de.schnettler.tvtracker.data.show.model.ShowDetailsDB
import de.schnettler.tvtracker.data.show.model.ShowDetailsRemote
import de.schnettler.tvtracker.data.api.trakt.TraktService
import de.schnettler.tvtracker.data.api.tvdb.TvdbService
import de.schnettler.tvtracker.data.show.model.cast.CastEntry
import de.schnettler.tvtracker.data.show.model.cast.CastListRemote
import de.schnettler.tvtracker.util.safeApiCall
import timber.log.Timber
import java.io.IOException

/**
 * Work with the Trakt API to get shows. The class knows how to construct the requests.
 */
class ShowDataSourceRemote(private val trakt: TraktService, private val tvdb: TvdbService) {
    //Show Details
    suspend fun getShowDetails(showID: Long) = safeApiCall(
        call = { requestShowDetails(showID) },
        errorMessage = "Error getting Show Details"
    )

    private suspend fun requestShowDetails(showID: Long): Result<ShowDetailsRemote> {
        val response = trakt.getShowSummary(showID)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting show details: ${response.code()} ${response.message()}"))
    }


    //Cast
    suspend fun getCast(showID: Long, token: String) = safeApiCall(
        call = { requestCast(showID, token) },
        errorMessage = "Error getting Cast"
    )

    private suspend fun requestCast(showID: Long, token: String): Result<CastListRemote> {
        val response = tvdb.getActors(TvdbService.AUTH_PREFIX + token, showID)
        Timber.i("RESPONSE $response.toString()")

        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting cast: ${response.code()} ${response.message()}"))
    }
}


class ShowDataSourceLocal(private val dao: TrendingShowsDAO) {
    suspend fun insertShowDetails(showDetailsDB: ShowDetailsDB) {
        dao.insertShowDetails(showDetailsDB)
    }

    fun getShowDetail(showID: Long) = dao.getShowDetails(showID)

    suspend fun insertShowCast(cast: List<CastEntry>) {
        dao.insertCast(cast)
    }

    fun getShowCast(showID: Long) = dao.getCast(showID)
}