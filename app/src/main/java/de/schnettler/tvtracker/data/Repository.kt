package de.schnettler.tvtracker.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import de.schnettler.tvtracker.data.local.TrendingShowsDAO
import de.schnettler.tvtracker.data.model.*
import de.schnettler.tvtracker.data.remote.RetrofitClient
import de.schnettler.tvtracker.util.TMDB_API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class Repository(private val trendingDao: TrendingShowsDAO) {
    private val traktClient = RetrofitClient.tractService
    private val tmdbClient = RetrofitClient.tmdbService


    private suspend fun getPoster(showId: String) = withContext(Dispatchers.IO) {
        tmdbClient.getShowPoster(showId, TMDB_API_KEY)
    }


    suspend fun refreshTrendingShows() = withContext(Dispatchers.IO) {
        try {
            //Load Data From Network
            val trendingShows = traktClient.getTrendingShows()

            //Save Data in Database
            if (trendingShows.isSuccessful) {
                val showsRemote = trendingShows.body()
                val showsDataBase = showsRemote?.asShowTrendingDB()
                showsDataBase.let {
                    // Update Cached Trending Shows
                    trendingDao.updateTrendingShows(showsDataBase!!)
                    refreshPosters(showsDataBase.map {
                        it.show
                    })
                }

            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    suspend fun refreshPopularShows() = withContext(Dispatchers.IO) {
        try {
            //Load Data From Network
            val popularShows = traktClient.getPopularShows()
            //Save In DB
            if (popularShows.isSuccessful) {
                val showsRemote = popularShows.body()
                val showsDataBase = showsRemote?.asShowPopularDB()
                showsDataBase.let {
                    trendingDao.updatePopularShows(showsDataBase!!)
                    refreshPosters(showsDataBase.map {
                        it.show
                    })
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    suspend fun refreshShowSummary(show_id: Long) = withContext(Dispatchers.IO) {
        val result = traktClient.getShowSummary(show_id)
        Timber.i(result.toString())
    }

    fun getTrendingShows(): LiveData<List<Show>> = Transformations.map(trendingDao.getTrending()) {
        it.asTrendingShow()
    }

    fun getPopularShows(): LiveData<List<Show>> = Transformations.map(trendingDao.getPopular()) {
        it.asPopularShow()
    }

    private suspend fun refreshPosters(showsDB: List<ShowDB>) {
        for (showDB in showsDB) {
            val image = getPoster(showDB.tmdbId)
            if (image.isSuccessful) {
                showDB.posterUrl = image.body()!!.poster_path
                showDB.backdropUrl = image.body()!!.backdrop_path
                trendingDao.updateShow(showDB)
            }
        }
    }
}