package de.schnettler.tvtracker.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import de.schnettler.tvtracker.data.local.ShowDao
import de.schnettler.tvtracker.data.model.Show
import de.schnettler.tvtracker.data.model.asDomainModel
import de.schnettler.tvtracker.data.model.asShowDatabase
import de.schnettler.tvtracker.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

const val TMDB_API_KEY = "***TMDB_API_KEY***"

class Repository(private val showDao: ShowDao) {
    private var traktClient = RetrofitClient.tractService
    private var tmdbClient = RetrofitClient.tmdbService


    suspend fun getPoster(showId: String) = withContext(Dispatchers.IO) {
        RetrofitClient.tmdbService.getShowPoster(showId, TMDB_API_KEY)
    }


    suspend fun refreshTrendingShows() = withContext(Dispatchers.IO) {
        try {
            //Load Data From Network
            val trendingShows = traktClient.getTrendingShows()
            Timber.i("Loaded ${trendingShows.body()?.size} Shows from Trakt.Tv")
            //Save Data in Database
            if (trendingShows.isSuccessful) {
                val showsDataBase = trendingShows.body()?.asShowDatabase()
                showsDataBase.let {
                    showDao.updateTrendingShows(showsDataBase!!)
                    for (show in showsDataBase) {
                        val image = getPoster(show.tmdbId.toString())
                        if (image.isSuccessful) {
                            show.posterUrl = image.body()!!.poster_path
                            showDao.updateShow(show)
                        }
                    }
                }

            }

        } catch (t: Throwable) {
            t.printStackTrace()
        }

    }

    fun getTrendingShows(): LiveData<List<Show>> = Transformations.map(showDao.getTrendingShows()) {
            it.asDomainModel()
    }
}