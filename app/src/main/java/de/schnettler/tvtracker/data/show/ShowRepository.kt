package de.schnettler.tvtracker.data.show

import androidx.lifecycle.Transformations
import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.show.model.cast.asCastEntryList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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


    suspend fun refreshCast(showId: Long, token: String) {
        //Refresh Details from Network
        val result = remoteService.getCast(showId, token)
        if (result is Result.Success) {
            //Insert in DB
            localDao.insertShowCast(result.data.data.asCastEntryList())
        } else {
            (result as Result.Error).exception.printStackTrace()
        }
    }

    fun getShowCast(showId: Long) = localDao.getShowCast(showId)
}