package de.schnettler.tvtracker.data.repository.show

import androidx.paging.toLiveData
import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.db.ShowDao
import de.schnettler.tvtracker.data.mapping.EpisodeDetailMapper
import de.schnettler.tvtracker.data.mapping.EpisodeFullMapper
import de.schnettler.tvtracker.data.models.EpisodeDomain
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

class EpisodeRepository(private val remoteService: ShowDataSourceRemote, private val localDao: ShowDao) {
    fun getEpisodes(showID: Long) = localDao.getEpisodes(showID).map {
        EpisodeFullMapper.mapToDomain(it)
    }.toLiveData(pageSize = 1)

    /*
     * Episode Details
     */
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
}