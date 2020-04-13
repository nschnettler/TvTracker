package de.schnettler.tvtracker.data.repository.show

import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.Result.Error
import de.schnettler.tvtracker.data.Result.Success
import de.schnettler.tvtracker.data.api.TmdbAPI
import de.schnettler.tvtracker.data.api.TraktAPI
import de.schnettler.tvtracker.data.api.TvdbAPI
import de.schnettler.tvtracker.data.db.ShowDao
import de.schnettler.tvtracker.data.mapping.*
import de.schnettler.tvtracker.data.models.*
import de.schnettler.tvtracker.util.TopListType
import de.schnettler.tvtracker.util.safeApiCall
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import timber.log.Timber
import java.io.IOException

/**
 * Class that knows how to get and store Shows
 */
@ExperimentalCoroutinesApi
@FlowPreview
class ShowRepository(
    private val remoteService: ShowDataSourceRemote,
    private val trakt: TraktAPI,
    private val tvdb: TvdbAPI,
    private val tmdb: TmdbAPI,
    private val localDao: ShowDao
) {
    private val relatedMapper = ListMapperWithId(ShowRelatedMapper)
    private val listedShowMapper = ListMapper(ListedSHowMapper)
    private val seasonMapper = ListMapperWithId(SeasonSummaryMapper)
    private val seasonWithEpisodeMapper = ListMapper(SeasonWithEpisodeMapper)


    private val detailsStore = StoreBuilder
        .fromNonFlow { showId: Long ->
            ShowDetailsMapper.mapToDatabase(trakt.getShowSummary(showId)) }
        .persister(
            reader = { showId ->
                localDao.getShowDetails(showId).map { detail -> detail?.let { ShowDetailsMapper.mapToDomain(it) } } },
            writer = { _, showDetails ->
                localDao.insertShowDetails(showDetails)}
        )
        .build()

    private val relatedStore = StoreBuilder
        .fromNonFlow { showId: Long ->
            relatedMapper.mapToDatabase(trakt.getRelatedShows(showId), showId) }
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
            seasonMapper.mapToDatabase(trakt.getShowSeasons(showId), showId)
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

    private val topListStore= StoreBuilder
        .fromNonFlow { type: TopListType ->
            listedShowMapper.mapToDatabase(
                when (type) {
                    TopListType.TRENDING -> trakt.getTrendingShows()
                    TopListType.POPULAR -> trakt.getPopularShows()
                    TopListType.ANTICIPATED -> trakt.getAnticipated()
                    TopListType.RECOMMENDED -> trakt.getAnticipated()
                }
            )
        }
        .persister(
            reader = { type ->
                localDao.getTopList(type.name).mapLatest { listedShowMapper.mapToDomain(it) }
            },
            writer = { type, list ->
                run {
                    localDao.insertShows(list.map { it.show })
                    localDao.insertTopList(list.map { it.listing })
                    refreshPosters(list.map { it.show })
                }
            }
        ).build()


    fun getShowDetails(showId: Long) = detailsStore.stream(StoreRequest.cached(showId, true))
    fun getRelated(showId: Long) = relatedStore.stream(StoreRequest.cached(showId, true))
    fun getSeasons(showId: Long) = seasonStore.stream(StoreRequest.cached(showId, true))
    fun getTopList(type: TopListType) = topListStore.stream(StoreRequest.cached(type, true))

    /*
    * Show Cast
    */
    suspend fun refreshCast(showId: Long, token: String) {
        when (val result = getCast(showId, token)) {
            is Success -> localDao.insertCast(result.data.data.asCastEntryList())
            is Error -> Timber.e(result.exception)
        }
    }

    fun getShowCast(showId: Long) = localDao.getCast(showId)

    /*
     * Show Poster
     */
    private suspend fun refreshPosters(showsDB: List<ShowEntity>) {
        showsDB.forEach {
            //Check if Show already in DB
            localDao.getShow(it.id)?.let { entity ->
                if (entity.posterUrl.isBlank()) {
                    val result = getImages(entity.tmdbId)
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

    /**
     * OLD WAY
     */

    //Cast
    suspend fun getCast(showID: Long, token: String) = safeApiCall(
        call = { requestCast(showID, token) },
        errorMessage = "Error getting Cast"
    )

    private suspend fun requestCast(showID: Long, token: String): Result<CastListResponse> {
        val response = tvdb.getActors(TvdbAPI.AUTH_PREFIX + token, showID)
        Timber.i("RESPONSE $response.toString()")

        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting cast: ${response.code()} ${response.message()}"))
    }

    //Poster
    suspend fun getImages(tmdbId: String) = safeApiCall(
        call = { requestShowImages(tmdbId) },
        errorMessage = "Error loading Poster for $tmdbId"
    )

    private suspend fun requestShowImages(tmdbId: String): Result<ShowImageResponse> {
        val response = tmdb.getShowPoster(tmdbId, TmdbAPI.API_KEY)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting show images: ${response.code()} ${response.message()}"))
    }
}