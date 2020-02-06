package de.schnettler.tvtracker.data.repository.show

import androidx.lifecycle.Transformations
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import de.schnettler.tvtracker.data.Result.Error
import de.schnettler.tvtracker.data.Result.Success
import de.schnettler.tvtracker.data.db.ShowDao
import de.schnettler.tvtracker.data.mapping.*
import de.schnettler.tvtracker.data.models.*
import de.schnettler.tvtracker.util.TopListType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import timber.log.Timber

/**
 * Class that knows how to get and store Shows
 */
@ExperimentalCoroutinesApi
@FlowPreview
class ShowRepository(
    private val remoteService: ShowDataSourceRemote,
    private val localDao: ShowDao
) {
    private val relatedMapper = ListMapperWithId(ShowRelatedMapper)
    private val listedShowMapper = ListMapper(ListedSHowMapper)
    private val seasonMapper = ListMapperWithId(SeasonSummaryMapper)
    private val seasonWithEpisodeMapper = ListMapper(SeasonWithEpisodeMapper)


    private val detailsStore = StoreBuilder
        .fromNonFlow { showId: Long ->
            ShowDetailsMapper.mapToDatabase(remoteService.traktService.getShowSummary(showId)) }
        .persister(
            reader = { showId ->
                localDao.getShowDetails(showId).map { detail -> detail?.let { ShowDetailsMapper.mapToDomain(it) } } },
            writer = { _, showDetails ->
                localDao.insertShowDetails(showDetails)}
        )
        .build()

    private val relatedStore = StoreBuilder
        .fromNonFlow { showId: Long ->
            relatedMapper.mapToDatabase(remoteService.traktService.getRelatedShows(showId), showId) }
        .persister(
            reader = { showId ->
                localDao.getShowRelations(showId).mapLatest { relatedMapper.mapToDomain(it) } },
            writer = { _, related->
                run {
                    localDao.insertShowRelations(related)
                    refreshPosters(related.map { it.relatedShow })
                }
            }
        ).build()

    private val seasonStore = StoreBuilder
        .fromNonFlow { showId: Long ->
            seasonMapper.mapToDatabase(remoteService.traktService.getShowSeasons(showId), showId)
        }
        .persister(
            reader = { showId ->
                localDao.getSeasonsWithEpisodes(showId).mapLatest { seasonWithEpisodeMapper.mapToDomain(it) }
            },
            writer = { showId, seasons->
                kotlin.run {
                    localDao.insertSeasons(seasons)
                    //Insert Dummy Episodes
                    val episodes = mutableListOf<EpisodeEntity>()
                    seasons.forEach { season ->
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
            }
        ).build()


    fun getShowDetails(showId: Long) = detailsStore.stream(StoreRequest.cached(showId, true))
    fun getRelated(showId: Long) = relatedStore.stream(StoreRequest.cached(showId, true))
    fun getSeasons(showId: Long) = seasonStore.stream(StoreRequest.cached(showId, true))

    /*
    * Show Cast
    */
    suspend fun refreshCast(showId: Long, token: String) {
        when (val result = remoteService.getCast(showId, token)) {
            is Success -> localDao.insertCast(result.data.data.asCastEntryList())
            is Error -> Timber.e(result.exception)
        }
    }

    fun getShowCast(showId: Long) = localDao.getCast(showId)


    suspend fun refreshShowList(type: TopListType, token: String = "") {
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

    fun getTopList(type: TopListType) =
        Transformations.map(localDao.getTopList(type.name)) {
            listedShowMapper.mapToDomain(it)
        }
    /*
     * Show Poster
     */
    private suspend fun refreshPosters(showsDB: List<ShowEntity>) {
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