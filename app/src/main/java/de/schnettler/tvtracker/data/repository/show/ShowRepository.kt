package de.schnettler.tvtracker.data.repository.show

import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.Result.Error
import de.schnettler.tvtracker.data.Result.Success
import de.schnettler.tvtracker.data.api.HeaderProvider
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
    private val trakt: TraktAPI,
    private val tvdb: TvdbAPI,
    private val tmdb: TmdbAPI,
    private val headerProvider: HeaderProvider,
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


    fun getShowDetails(showId: Long) = detailsStore.stream(StoreRequest.cached(showId, true))
    fun getRelated(showId: Long) = relatedStore.stream(StoreRequest.cached(showId, true))
    fun getSeasons(showId: Long) = seasonStore.stream(StoreRequest.cached(showId, true))
    fun getTopList(listType: TopListType, accessToken: String? = null) = StoreBuilder
        .fromNonFlow { type: TopListType ->
            listedShowMapper.mapToDatabase(
                when (type) {
                    TopListType.TRENDING -> trakt.getTrendingShows()
                    TopListType.POPULAR -> trakt.getPopularShows()
                    TopListType.ANTICIPATED -> trakt.getAnticipated()
                    TopListType.RECOMMENDED -> trakt.getRecommended(headerProvider.getAuthenticatedHeaders(accessToken ?: ""))
                }
            )
        }
        .persister(
            reader = { type ->
                localDao.getTopList(type.name).mapLatest { listedShowMapper.mapToDomain(it) }
            },
            writer = { _, list ->
                run {
                    localDao.insertShows(list.map { it.show })
                    localDao.insertTopList(list.map { it.listing })
                    refreshPosters(list.map { it.show })
                }
            }
        ).build().stream(StoreRequest.cached(listType, true))

    fun getCast(showId: Long, token: String?) = StoreBuilder
        .fromNonFlow { id: Long ->
            tvdb.getActors(TvdbAPI.AUTH_PREFIX + token, id)
        }
        .persister(
            reader = { id ->
                localDao.getCast(id)
            },
            writer = { _, cast ->
                localDao.insertCast(cast.data.asCastEntryList())
            }
        ).build().stream(StoreRequest.cached(showId, true))

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

    //Poster
    private suspend fun getImages(tmdbId: String) = safeApiCall(
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