package de.schnettler.tvtracker.data.repository.show

import androidx.paging.toLiveData
import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.db.ShowDao
import de.schnettler.tvtracker.data.mapping.*
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

class EpisodeRepository(
    private val remoteService: ShowDataSourceRemote,
    private val localDao: ShowDao) {
    private val episodeMapper = ListMapperWithId(EpisodeMapper)

    /*
     * Episode Details
     */
    fun getEpisodes(showID: Long, scope: CoroutineScope) = localDao.getEpisodes(showID).map {
        EpisodeFullMapper.mapToDomain(it)
    }.toLiveData(pageSize = 1, boundaryCallback = EpisodeBoundaryCallback(this, scope))

    suspend fun refreshEpisodeDetails(showId: String, seasonNumber: Long, episodeNumber: Long, episodeId: Long) {
        when(val result = remoteService.getEpisodeDetail(showId, seasonNumber, episodeNumber)) {
            is Result.Success -> {
                localDao.insertEpisodeDetail(EpisodeDetailMapper.mapToDatabase(result.data, episodeId))
            }
            is Result.Error -> {
                Timber.e(result.exception)
            }
        }
    }

    /*
    * Episodes
    */
    suspend fun refreshEpisodes(showId: Long, seasonNumber: Long) {
        when(val result = remoteService.getEpisodesOfSeason(showId, seasonNumber)) {
            is Result.Success -> {
                //Get Season Id
                episodeMapper.mapToDatabase(result.data, showId)?.let { localDao.insertEpisodes(it) }
            }
            is Result.Error -> Timber.e(result.exception)
        }
    }
}