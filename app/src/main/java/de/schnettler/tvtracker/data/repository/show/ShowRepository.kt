package de.schnettler.tvtracker.data.repository.show

import androidx.lifecycle.Transformations
import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.api.Trakt
import de.schnettler.tvtracker.data.db.ShowDao
import de.schnettler.tvtracker.data.mapping.*
import de.schnettler.tvtracker.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Class that knows how to get and store Shows
 */
class ShowRepository(private val remoteService: ShowDataSourceRemote, private val localDao: ShowDao) {
    private val relatedMapper = ListMapperWithId(ShowRelatedMapper)
    private val trendingMapper = ListMapper(TrendingShowMapper)
    private val popularMapper = ListMapper(PopularShowMapper)
    private val anticipatedMapper = ListMapper(AnticipatedShowMapper)
    private val seasonMapper = ListMapperWithId(SeasonSummaryMapper)
    private val episodeMapper = ListMapperWithId(EpisodeMapper)
    private val seasonWithEpisodeMapper = ListMapper(SeasonWithEpisodeMapper)

    /*
     * Show Details
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
        Transformations.map(localDao.getShowDetails(showId)) {
            it?.let { ShowDetailsMapper.mapToDomain(it) }
        }


    /*
     * Show Cast
     */
    suspend fun refreshCast(showId: Long, token: String) {
        //Refresh Details from Network
        val result = remoteService.getCast(showId, token)
        if (result is Result.Success) {
            //Insert in DB
            localDao.insertCast(result.data.data.asCastEntryList())
        } else {
            (result as Result.Error).exception.printStackTrace()
        }
    }
    fun getShowCast(showId: Long) = localDao.getCast(showId)


    /*
     * Show´s related Shows
     */
    suspend fun refreshRelatedShows(showId: Long) {
        val result = remoteService.getRelated(showId)
        if (result is Result.Success) {
            //Insert in DB
            val showEntities = relatedMapper.mapToDatabase(result.data, id = showId)
            showEntities?.let { localDao.insertShowRelations(showEntities) }

            //Refresh Poster
            showEntities?.let {
                refreshPosters(showEntities.map { it.relatedShow })
            }
        }
    }
    fun getRelatedShows(showId: Long) = Transformations.map(localDao.getShowRelations(showId)) {
        relatedMapper.mapToDomain(it)
    }

    /*
     * Trending Shows
     */
    suspend fun refreshTrendingShows() {
        val result = remoteService.getTrendingShows()
        if (result is Result.Success) {
            //Insert in DB
            val entities = trendingMapper.mapToDatabase(result.data)
            entities?.let { localDao.insertTrendingShows(it) }


            //Refresh Poster
            entities?.let {
                refreshPosters(entities.map { it.show })
            }
        }
    }
    fun getTrending() = Transformations.map(localDao.getTrending()) {
        trendingMapper.mapToDomain(it)
    }


    /*
    * Popular Shows
    */
    suspend fun refreshPopularShows() {
        val result = remoteService.getPopularShows()
        if (result is Result.Success) {
            //Insert in DB
            val entities = popularMapper.mapToDatabase(result.data)
            entities?.let { localDao.insertPopularShows(it) }

            //Refresh Poster
            entities?.let {
                refreshPosters(entities.map { it.show })
            }
        }
    }
    fun getPopular() = Transformations.map(localDao.getPopular()) {
        popularMapper.mapToDomain(it)
    }


    /*
   * Anticipated Shows
   */
    suspend fun refreshAnticipatedShows() {
        val result = remoteService.getAnticipated()
        if (result is Result.Success) {
            //Insert in DB
            val entities = anticipatedMapper.mapToDatabase(result.data)
            entities?.let { localDao.insertAnticipatedShows(it) }

            //Refresh Poster
            entities?.let {
                refreshPosters(entities.map { it.show })
            }
        }
    }
    fun getAnticipated() = Transformations.map(localDao.getAnticipated()) {
        anticipatedMapper.mapToDomain(it)
    }

    /*
     * Seasons
     */
    suspend fun refreshSeasons(showId: Long) {
        when(val result = remoteService.getSeasonsOfShow(showId)) {
            is Result.Success -> {
                //Insert in DB
                seasonMapper.mapToDatabase(result.data, showId)?.let { localDao.insertSeasons(it) }
            }
            is Result.Error -> {
                Timber.e(result.exception)
            }
        }
    }
    fun getSeasonsWithEpisodes(showId: Long) = Transformations.map(localDao.getSeasonsWithEpisodes(showId)) {
        seasonWithEpisodeMapper.mapToDomain(it)
    }


    /*
    * Season Episodes
    */
    suspend fun refreshEpisodes(showId: Long, seasonNumber: Long, seasonId: Long) {
        when(val result = remoteService.getEpisodesOfSeason(showId, seasonNumber)) {
            is Result.Success -> {
                //Insert in DB
                episodeMapper.mapToDatabase(result.data, seasonId)?.let { localDao.insertEpisodes(it) }
            }
            is Result.Error -> {
                Timber.e(result.exception)
            }
        }
    }

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
    fun getEpisodeDetails(episodeId: Long) = Transformations.map(localDao.getEpisodeDetails(episodeId)) {
        it?.let {entity ->
            EpisodeDetailMapper.mapToDomain(entity)
        }
    }

    /*
     * Show Poster
     */
    suspend fun refreshPosters(showsDB: List<ShowEntity>) {
        showsDB.forEach {
            //Check if Show already in DB
            localDao.getShow(it.id)?.let {entity ->
                if (entity.posterUrl.isBlank()) {
                    val result = remoteService.getImages(entity.tmdbId)
                    if (result is Result.Success) {
                        var changed = false
                        result.data.poster_path?.let {
                            entity.posterUrl = it
                            changed = true
                        }
                        result.data.backdrop_path?.let {
                            entity.backdropUrl = it
                            changed = true
                        }
                        if (changed) {
                            localDao.updateShow(entity)
                        }
                    }
                }
            }
        }
    }
}