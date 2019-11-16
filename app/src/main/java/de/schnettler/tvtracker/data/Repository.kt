package de.schnettler.tvtracker.data

import android.app.Application
import androidx.lifecycle.Transformations
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.show.model.ShowDB
import de.schnettler.tvtracker.data.show.model.asShowPopularDB
import de.schnettler.tvtracker.data.show.model.asShowTrendingDB
import de.schnettler.tvtracker.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class Repository(private val context: Application, private val scope: CoroutineScope) {
    private val showsService = RetrofitClient.showsNetworkService
    private val imagesService = RetrofitClient.imagesNetworkService
    private val showsDao = getDatabase(context).trendingShowsDao

    private suspend fun getPoster(showId: String) = withContext(Dispatchers.IO) {
        imagesService.getShowPoster(showId, TMDB_API_KEY)
    }

    private suspend fun getPersonImage(personId: String) = withContext(Dispatchers.IO) {
        imagesService.getPersonImage(personId, TMDB_API_KEY)
    }

    suspend fun loadNewShowListPage(page: Int, limit: Int = 10, type: ShowListType) = withContext(Dispatchers.IO) {
        try {
            when (type) {
                ShowListType.TRENDING -> {
                    val trendingShows = showsService.getTrendingShows(page, limit)

                    //Save Data in Database
                    if (trendingShows.isSuccessful) {
                        val showsDB = trendingShows.body()?.asShowTrendingDB(page)
                        showsDB?.let {
                            // Update Cached Trending Shows
                            showsDao.insertTrendingShows(showsDB)
                            refreshPosters(showsDB.map { it.show })
                        }
                    }
                }
                ShowListType.POPULAR -> {
                    //Load Data From Network
                    val popularShows = showsService.getPopularShows(page, limit)

                    //Save Data in Database
                    if (popularShows.isSuccessful) {
                        val showsDB = popularShows.body()?.asShowPopularDB(page)
                        showsDB?.let {
                            // Update Cached Trending Shows
                            showsDao.insertPopularShows(showsDB)
                            refreshPosters(showsDB.map { it.show })
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun getTrendingShows()= Transformations.map(showsDao.getTrending()) {
        it?.map {trendingDB ->
            trendingDB.show.asShow(trendingDB.trending.index)
        }
    }

    fun getPopularShows() = Transformations.map(showsDao.getPopular()) {
        it?.map {trendingDB ->
            trendingDB.show.asShow(trendingDB.popular.index)
        }
    }

    private suspend fun refreshPosters(showsDB: List<ShowDB>) {
        for (showDB in showsDB) {
            val image = getPoster(showDB.tmdbId)
            if (image.isSuccessful) {
                showDB.posterUrl = image.body()!!.poster_path
                showDB.backdropUrl = image.body()!!.backdrop_path
                showsDao.updateShow(showDB)
            }
        }
    }

    suspend fun retrieveAccessToken(code: String) = withContext(Dispatchers.IO) {
        showsService.getToken(code = code, clientId = TRAKT_CLIENT_ID, uri = TRAKT_REDIRECT_URI, type = "authorization_code", secret = "***TRAKT_CLIENT_SECRET***")
    }
}