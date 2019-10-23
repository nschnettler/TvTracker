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
                    for ((index, showTrending) in showsDataBase.withIndex()) {
                        val image = getPoster(showsRemote[index].show.ids.tmdb.toString())
                        if (image.isSuccessful) {
                            showTrending.show.posterUrl = image.body()!!.poster_path
                            //Update Cache with new Image
                            trendingDao.updateShow(showTrending.show)
                        }
                    }
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
                    for ((index, showPopular) in showsDataBase.withIndex()) {
                        val image = getPoster(showsRemote[index].ids.tmdb.toString())
                        if (image.isSuccessful) {
                            showPopular.show.posterUrl = image.body()!!.poster_path
                            //Update Cache with new Image
                            trendingDao.updateShow(showPopular.show)
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun getTrendingShows(): LiveData<List<Show>> = Transformations.map(trendingDao.getTrending()) {
        it.asTrendingShow()
    }

    fun getPopularShows(): LiveData<List<Show>> = Transformations.map(trendingDao.getPopular()) {
        it.asPopularShow()
    }
}