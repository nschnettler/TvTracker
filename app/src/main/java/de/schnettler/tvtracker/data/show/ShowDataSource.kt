package de.schnettler.tvtracker.data.show

import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.local.TrendingShowsDAO
import de.schnettler.tvtracker.data.show.model.ShowDetailsDB
import de.schnettler.tvtracker.data.show.model.ShowDetailsRemote
import de.schnettler.tvtracker.data.api.trakt.TraktService
import de.schnettler.tvtracker.util.safeApiCall
import java.io.IOException

/**
 * Work with the Trakt API to get shows. The class knows how to construct the requests.
 */
class ShowDataSourceRemote(private val service: TraktService) {
    suspend fun getShowDetails(showID: Long) = safeApiCall(
        call = {requestShowDetails(showID)},
        errorMessage = "Error getting Show Details"
    )

    private suspend fun requestShowDetails(showID: Long): Result<ShowDetailsRemote> {
        val response = service.getShowSummary(showID)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting show details: ${response.code()} ${response.message()}"))
    }
}


class ShowDataSourceLocal(private val dao: TrendingShowsDAO) {
    suspend fun insertShowDetails(showDetailsDB: ShowDetailsDB) {
        dao.insertShowDetails(showDetailsDB)
    }

    fun getShowDetail(showID: Long) = dao.getShowDetails(showID)
}