package de.schnettler.tvtracker.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.toLiveData
import de.schnettler.tvtracker.data.local.getDatabase
import de.schnettler.tvtracker.data.model.*
import de.schnettler.tvtracker.data.remote.RetrofitClient
import de.schnettler.tvtracker.util.SHOW_LIST_PAGE_SIZE
import de.schnettler.tvtracker.util.ShowListType
import de.schnettler.tvtracker.util.TMDB_API_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class Repository(private val context: Application, private val scope: CoroutineScope) {
    private val showsService = RetrofitClient.showsNetworkService
    private val imagesService = RetrofitClient.imagesNetworkService
    private val showsDao = getDatabase(context).trendingShowsDao

    private suspend fun getPoster(showId: String) = withContext(Dispatchers.IO) {
        imagesService.getShowPoster(showId, TMDB_API_KEY)
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

    suspend fun refreshShowSummary(show_id: Long) = withContext(Dispatchers.IO) {
        val result = showsService.getShowSummary(show_id)
        Timber.i(result.toString())
    }

    fun getTrendingShows()= showsDao.getTrending().map{
        it.show.asShow(it.trending.index)
    }.toLiveData(pageSize = SHOW_LIST_PAGE_SIZE, boundaryCallback = ShowBoundaryCallback(this, scope, ShowListType.TRENDING))

    fun getPopularShows() = showsDao.getPopular().map{
        it.show.asShow(it.popular.index)
    }.toLiveData(pageSize = SHOW_LIST_PAGE_SIZE, boundaryCallback = ShowBoundaryCallback(this, scope, ShowListType.POPULAR))


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
}