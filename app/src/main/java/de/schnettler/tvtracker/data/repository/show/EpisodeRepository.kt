package de.schnettler.tvtracker.data.repository.show

import androidx.paging.toLiveData
import de.schnettler.tvtracker.data.Result.Error
import de.schnettler.tvtracker.data.Result.Success
import de.schnettler.tvtracker.data.db.ShowDao
import de.schnettler.tvtracker.data.mapping.EpisodeDetailMapper
import de.schnettler.tvtracker.data.mapping.EpisodeFullMapper
import de.schnettler.tvtracker.data.mapping.EpisodeMapper
import de.schnettler.tvtracker.data.mapping.ListMapperWithId
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

class EpisodeRepository(
    private val remoteService: ShowDataSourceRemote,
    private val localDao: ShowDao
) {
    /*
     * Episode Details
     */
    fun getEpisodes(showID: Long, scope: CoroutineScope) = localDao.getEpisodes(showID).map {
        EpisodeFullMapper.mapToDomain(it)
    }.toLiveData(pageSize = 1)

    suspend fun refreshEpisodeDetails(id: Long, tmdbId: String, seasonNumber: Long, episodeNumber: Long) {
        when (val result = remoteService.getEpisodeDetail(tmdbId, seasonNumber, episodeNumber)) {
            is Success -> localDao.insertEpisodeDetail(EpisodeDetailMapper.mapToDatabase(result.data, id, seasonNumber, episodeNumber))
            is Error -> Timber.e(result.exception)
        }
    }

    /*
    * Episodes
    */
    suspend fun refreshEpisodes(showId: Long, seasonNumber: Long) {
        when (val result = remoteService.getEpisodesOfSeason(showId, seasonNumber)) {
            is Success -> ListMapperWithId(EpisodeMapper).mapToDatabase(result.data, showId)?.let { localDao.insertEpisodes(it) }
            is Error -> Timber.e(result.exception)
        }
    }
}