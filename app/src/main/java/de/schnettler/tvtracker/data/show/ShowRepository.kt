package de.schnettler.tvtracker.data.show

import androidx.lifecycle.Transformations
import de.schnettler.tvtracker.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class that knows how to get and store Shows
 */
class ShowRepository(private val remoteService: ShowDataSourceRemote, private val localDao: ShowDataSourceLocal) {

    /*
     * Get the [ShowDetailsRemote] corresponding to [showId]
     */
    suspend fun refreshShowDetails(showId: Long) {
        //Refresh Details from Network
        val result = remoteService.getShowDetails(showId)

        if (result is Result.Success) {
            //Insert in DB
            localDao.insertShowDetails(result.data.asShowDetailsDB())
        }
    }

    fun getShowDetails(showId: Long) =
        Transformations.map(localDao.getShowDetail(showId)) { it?.asShowDetails() }
}