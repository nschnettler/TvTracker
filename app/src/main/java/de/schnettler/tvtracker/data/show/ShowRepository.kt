package de.schnettler.tvtracker.data.show

import androidx.lifecycle.Transformations
import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.mapping.*
import de.schnettler.tvtracker.data.show.model.ShowDB
import de.schnettler.tvtracker.data.show.model.cast.asCastEntryList
import de.schnettler.tvtracker.util.ShowListType
import de.schnettler.tvtracker.util.TRAKT_CLIENT_ID
import de.schnettler.tvtracker.util.TRAKT_REDIRECT_URI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Class that knows how to get and store Shows
 */
class ShowRepository(private val remoteService: ShowDataSourceRemote, private val localDao: ShowDataSourceLocal) {
    private val relatedMapper = ListMapper(ShowRelatedMapper)
    private val trendingMapper = ListMapper(TrendingShowMapper)
    private val popularMapper = ListMapper(PopularShowMapper)

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
        Transformations.map(localDao.getShowDetail(showId)) {
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
            localDao.insertShowCast(result.data.data.asCastEntryList())
        } else {
            (result as Result.Error).exception.printStackTrace()
        }
    }
    fun getShowCast(showId: Long) = localDao.getShowCast(showId)


    /*
     * Show´s related Shows
     */
    suspend fun refreshRelatedShows(showId: Long) {
        val result = remoteService.getRelated(showId)
        if (result is Result.Success) {
            //Insert in DB
            val showEntities = relatedMapper.mapToDatabase(result.data, id = showId)
            localDao.insertRelatedShows(showEntities)

            //Refresh Poster
            showEntities?.let {
                refreshPosters(showEntities.map { it.relatedShow })
            }
        }
    }
    fun getRelatedShows(showId: Long) = Transformations.map(localDao.getRelatedShows(showId)) {
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
            localDao.insertTrending(entities)

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
            localDao.insertPopular(entities)

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
     * Show Poster
     */
    suspend fun refreshPosters(showsDB: List<ShowDB>) {
        for (showDB in showsDB) {
            val result = remoteService.getImages(showDB.tmdbId)
            if (result is Result.Success) {
                showDB.posterUrl = result.data.poster_path
                showDB.backdropUrl = result.data.backdrop_path
                localDao.updateShow(showDB)
            }
        }
    }

    suspend fun retrieveAccessToken(code: String) = withContext(Dispatchers.IO) {
        remoteService.trakt.getToken(code = code, clientId = TRAKT_CLIENT_ID, uri = TRAKT_REDIRECT_URI, type = "authorization_code", secret = "***TRAKT_CLIENT_SECRET***")
    }
}