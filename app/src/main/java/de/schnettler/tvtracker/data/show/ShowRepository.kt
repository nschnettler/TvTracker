package de.schnettler.tvtracker.data.show

import androidx.lifecycle.Transformations
import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.mapping.ListMapper
import de.schnettler.tvtracker.data.mapping.ShowDetailsMapper
import de.schnettler.tvtracker.data.mapping.ShowRelatedMapper
import de.schnettler.tvtracker.data.show.model.ShowDB
import de.schnettler.tvtracker.data.show.model.cast.asCastEntryList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Class that knows how to get and store Shows
 */
class ShowRepository(private val remoteService: ShowDataSourceRemote, private val localDao: ShowDataSourceLocal) {
    private val relatedMapper = ListMapper(ShowRelatedMapper)
    /*
     * Get the [ShowDetailsRemote] corresponding to [showId]
     */
    suspend fun refreshShowDetails(showId: Long) {
        //Refresh Details from Network
        val result = remoteService.getShowDetails(showId)

        if (result is Result.Success) {
            //Insert in DB
            localDao.insertShowDetails(ShowDetailsMapper.mapToDatabase(result.data))
        }
    }

    fun getShowDetails(showId: Long) =
        Transformations.map(localDao.getShowDetail(showId)) {
            it?.let { ShowDetailsMapper.mapToDomain(it) }
        }


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


    suspend fun refreshRelatedShows(showId: Long) {
        val result = remoteService.getRelated(showId)
        if (result is Result.Success) {
            //Insert in DB
            localDao.insertRelatedShows(relatedMapper.mapToDatabase(result.data, id = showId))

            //TODO: Refresh Poster
        }
    }
    fun getRelatedShows(showId: Long) = Transformations.map(localDao.getRelatedShows(showId)) {
        relatedMapper.mapToDomain(it)
    }
}