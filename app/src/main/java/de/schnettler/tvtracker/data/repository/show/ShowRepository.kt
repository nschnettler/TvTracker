package de.schnettler.tvtracker.data.repository.show

import androidx.lifecycle.Transformations
import de.schnettler.tvtracker.data.Result.Error
import de.schnettler.tvtracker.data.Result.Success
import de.schnettler.tvtracker.data.db.ShowDao
import de.schnettler.tvtracker.data.mapping.*
import de.schnettler.tvtracker.data.models.EpisodeEntity
import de.schnettler.tvtracker.data.models.ShowEntity
import de.schnettler.tvtracker.data.models.asCastEntryList
import de.schnettler.tvtracker.util.TopListType
import timber.log.Timber

/**
 * Class that knows how to get and store Shows
 */
class ShowRepository(
    private val remoteService: ShowDataSourceRemote,
    private val localDao: ShowDao
) : IShowRepository {
    private val relatedMapper = ListMapperWithId(ShowRelatedMapper)
    private val listedShowMapper = ListMapper(ListedSHowMapper)
    private val seasonMapper = ListMapperWithId(SeasonSummaryMapper)
    private val seasonWithEpisodeMapper = ListMapper(SeasonWithEpisodeMapper)

    /*
     * Show Details
     */
    override suspend fun refreshShowDetails(showId: Long) {
        when (val result = remoteService.getShowDetails(showId)) {
            is Success -> localDao.insertShowDetails(ShowDetailsMapper.mapToDatabase(result.data))
            is Error -> Timber.e(result.exception)
        }
    }

    override fun getShowDetails(showId: Long) =
        Transformations.map(localDao.getShowDetails(showId)) {
            it?.let { ShowDetailsMapper.mapToDomain(it) }
        }


    /*
     * Show Cast
     */
    override suspend fun refreshCast(showId: Long, token: String) {
        when (val result = remoteService.getCast(showId, token)) {
            is Success -> localDao.insertCast(result.data.data.asCastEntryList())
            is Error -> Timber.e(result.exception)
        }
    }

    override fun getShowCast(showId: Long) = localDao.getCast(showId)


    /*
     * Show´s related Shows
     */
    override suspend fun refreshRelatedShows(showId: Long) {
        when (val result = remoteService.getRelated(showId)) {
            is Success -> {
                //Insert in DB
                val showEntities = relatedMapper.mapToDatabase(result.data, showId)
                showEntities?.let { localDao.insertShowRelations(showEntities) }

                //Refresh Poster
                showEntities?.let {
                    refreshPosters(showEntities.map { it.relatedShow })
                }
            }
            is Error -> Timber.e(result.exception)
        }
    }

    override fun getRelatedShows(showId: Long) =
        Transformations.map(localDao.getShowRelations(showId)) {
            relatedMapper.mapToDomain(it)
        }

    override suspend fun refreshShowList(type: TopListType, token: String) {
        when (val result = remoteService.getTopList(type, token)) {
            is Success -> {
                val entities = listedShowMapper.mapToDatabase(result.data)
                entities?.let {
                    localDao.insertShows(entities.map { it.show })
                    localDao.insertTopList(entities.map { it.listing })
                    refreshPosters(entities.map { it.show })
                }
            }
        }
    }

    override fun getTopList(type: TopListType) =
        Transformations.map(localDao.getTopList(type.name)) {
            listedShowMapper.mapToDomain(it)
        }

    /*
     * Seasons
     */
    override suspend fun refreshSeasons(showId: Long) {
        when (val result = remoteService.getSeasonsOfShow(showId)) {
            is Success -> {
                //Insert Seasons
                seasonMapper.mapToDatabase(result.data, showId)?.let { localDao.insertSeasons(it) }
                //Insert Dummy Episodes
                val episodes = mutableListOf<EpisodeEntity>()
                result.data.forEach { season ->
                    if (season.number > 0) {
                        season.episodeCount?.let { episodeCount ->
                            for (i in 1..episodeCount) {
                                episodes.add(
                                    EpisodeEntity(
                                        seasonId = "${showId}_${season.number}",
                                        showId = showId,
                                        season = season.number,
                                        number = i,
                                        title = "Episode $i"
                                    )
                                )
                            }
                        }
                    }
                }
                localDao.insertDummyEpisodes(episodes)
            }
            is Error -> Timber.e(result.exception)
        }
    }

    override fun getSeasonsWithEpisodes(showId: Long) =
        Transformations.map(localDao.getSeasonsWithEpisodes(showId)) {
            seasonWithEpisodeMapper.mapToDomain(it)
        }

    /*
     * Show Poster
     */
    override suspend fun refreshPosters(showsDB: List<ShowEntity>) {
        showsDB.forEach {
            //Check if Show already in DB
            localDao.getShow(it.id)?.let { entity ->
                if (entity.posterUrl.isBlank()) {
                    val result = remoteService.getImages(entity.tmdbId)
                    if (result is Success) {
                        var changed = false
                        result.data.poster_path?.let { url ->
                            entity.posterUrl = url
                            changed = true
                        }
                        result.data.backdrop_path?.let { url ->
                            entity.backdropUrl = url
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