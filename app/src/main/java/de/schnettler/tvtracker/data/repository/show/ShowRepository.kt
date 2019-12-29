package de.schnettler.tvtracker.data.repository.show

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.db.ShowDao
import de.schnettler.tvtracker.data.mapping.*
import de.schnettler.tvtracker.data.models.*
import de.schnettler.tvtracker.util.TopListType
import timber.log.Timber

/**
 * Class that knows how to get and store Shows
 */
class ShowRepository(private val remoteService: ShowDataSourceRemote, private val localDao: ShowDao) {
    private val relatedMapper = ListMapperWithId(ShowRelatedMapper)
    private val listedShowMapper = ListMapper(ListedSHowMapper)
    private val seasonMapper = ListMapperWithId(SeasonSummaryMapper)
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
        when (val result = remoteService.getCast(showId, token)) {
            is Result.Success ->  localDao.insertCast(result.data.data.asCastEntryList())
            is Result.Error -> Timber.e(result.exception)
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
            val showEntities = relatedMapper.mapToDatabase(result.data, showId)
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

    suspend fun refreshShowList(type: TopListType, token: String = "") {
        when (val result = remoteService.getTopList(type, token)) {
            is Result.Success -> {
                val entities = listedShowMapper.mapToDatabase(result.data)
                entities?.let {
                    localDao.insertShows(entities.map { it.show })
                    localDao.insertTopList(entities.map { it.listing })
                    refreshPosters(entities.map { it.show })
                }
            }
        }
    }
    fun getTopList(type: TopListType) =
        Transformations.map(localDao.getTopList(type.name)) {
            listedShowMapper.mapToDomain(it)
        }

    /*
     * Seasons
     */
    suspend fun refreshSeasons(showId: Long) {
        when(val result = remoteService.getSeasonsOfShow(showId)) {
            is Result.Success -> seasonMapper.mapToDatabase(result.data, showId)?.let { localDao.insertSeasons(it) }
            is Result.Error -> Timber.e(result.exception)
        }
    }
    fun getSeasonsWithEpisodes(showId: Long) = Transformations.map(localDao.getSeasonsWithEpisodes(showId)) {
        seasonWithEpisodeMapper.mapToDomain(it)
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