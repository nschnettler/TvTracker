package de.schnettler.tvtracker.data.repository.show

import androidx.paging.toLiveData
import de.schnettler.tvtracker.data.Result.Error
import de.schnettler.tvtracker.data.Result.Success
import de.schnettler.tvtracker.data.db.ShowDao
import de.schnettler.tvtracker.data.mapping.EpisodeMapper
import de.schnettler.tvtracker.data.mapping.ListMapperWithId
import timber.log.Timber
import kotlin.math.roundToInt

class EpisodeRepository(
    private val remoteService: ShowDataSourceRemote,
    private val localDao: ShowDao
) {
    /*
     * Episode Details
     */
    fun getEpisodes(showID: Long) = localDao.getEpisodes(showID).map {
        EpisodeMapper.mapToDomain(it)
    }.toLiveData(pageSize = 1)

    suspend fun refreshEpisodeDetails(showId: Long, tmdbId: String, seasonNumber: Long, episodeNumber: Long) {
        when (val result = remoteService.getEpisodeDetail(tmdbId, seasonNumber, episodeNumber)) {
            is Success -> {
                //Update Episode
                Timber.i("Updating Episode Details $showId - $seasonNumber - $episodeNumber")
                localDao.updateEpisode(showId, seasonNumber, episodeNumber, result.data.airDate, result.data.stillPath, result.data.voteAverage.times(10).roundToInt(), result.data.overview, result.data.name)
            }
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