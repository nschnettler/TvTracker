package de.schnettler.tvtracker.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import de.schnettler.tvtracker.data.local.getDatabase
import de.schnettler.tvtracker.data.model.*
import de.schnettler.tvtracker.data.remote.RetrofitClient
import de.schnettler.tvtracker.util.TMDB_API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class Repository(private val context: Application) {
    private val showsNetwork = RetrofitClient.showsNetworkService
    private val imagesNetwork = RetrofitClient.imagesNetworkService
    private val showsDatabase = getDatabase(context).trendingShowsDao


    private suspend fun getPoster(showId: String) = withContext(Dispatchers.IO) {
        imagesNetwork.getShowPoster(showId, TMDB_API_KEY)
    }


    suspend fun refreshTrendingShows() = withContext(Dispatchers.IO) {
        try {
            //Load Data From Network
            val trendingShows = showsNetwork.getTrendingShows()

            //Save Data in Database
            if (trendingShows.isSuccessful) {
                val showsRM = trendingShows.body()
                val showsDB = showsRM?.asShowTrendingDB()
                showsDB?.let {
                    // Update Cached Trending Shows
                    showsDatabase.updateTrendingShows(showsDB)
                    refreshPosters(showsDB.map { it.show })
                }

            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    suspend fun refreshPopularShows() = withContext(Dispatchers.IO) {
        try {
            //Load Data From Network
            val popularShows = showsNetwork.getPopularShows()
            //Save In DB
            if (popularShows.isSuccessful) {
                val showsRemote = popularShows.body()
                val showsDataBase = showsRemote?.asShowPopularDB()
                showsDataBase?.let {
                    showsDatabase.updatePopularShows(showsDataBase)
                    refreshPosters(showsDataBase.map { it.show })
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    suspend fun refreshShowSummary(show_id: Long) = withContext(Dispatchers.IO) {
        val result = showsNetwork.getShowSummary(show_id)
        Timber.i(result.toString())
    }

    fun getTrendingShows(): LiveData<List<Show>> = Transformations.map(showsDatabase.getTrending()) {
        it.asTrendingShow()
    }

    fun getPopularShows(): LiveData<List<Show>> = Transformations.map(showsDatabase.getPopular()) {
        it.asPopularShow()
    }

    private suspend fun refreshPosters(showsDB: List<ShowDB>) {
        for (showDB in showsDB) {
            val image = getPoster(showDB.tmdbId)
            if (image.isSuccessful) {
                showDB.posterUrl = image.body()!!.poster_path
                showDB.backdropUrl = image.body()!!.backdrop_path
                showsDatabase.updateShow(showDB)
            }
        }
    }
}