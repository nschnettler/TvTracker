package de.schnettler.tvtracker.ui.discover

import android.app.Application
import androidx.lifecycle.*
import de.schnettler.tvtracker.data.Repository
import de.schnettler.tvtracker.data.local.getDatabase
import de.schnettler.tvtracker.data.model.Show
import kotlinx.coroutines.launch
import timber.log.Timber

class DiscoverViewModel(application: Application) : ViewModel() {
    private val dataBase = getDatabase(application)
    //Repository
    private val repo = Repository(dataBase.showDao)

    //Trending Shows
    var trendingShows = repo.getTrendingShows()
    /*
    private val _trendingShows = MutableLiveData<List<Show>>()
    val trendingShows: LiveData<List<Show>>
        get() = _trendingShows */

    init {
        viewModelScope.launch {
            repo.refreshTrendingShows()

            val test = repo.getTrendingShows()
            Timber.i("Loaded ${test.value?.size} over the network")
        }
    }

    private fun getTrendingShows() {
        viewModelScope.launch {
            //_trendingShows = repo.getTrendingShows()
            /*
            val response = repo.getTrendingShows()
            if (response.isSuccessful) {
                response.body().let {
                    val showsLocal = it?.asShowModel()
                    _trendingShows.value = showsLocal
                    val newShowsLocal = ArrayList<Show>()
                    //Load Images
                    showsLocal.let {
                        for (item in showsLocal!!) {
                            val image = repo.getPoster(item.tmdbId.toString())
                            item.posterUrl = image.body()?.poster_path ?: ""
                            newShowsLocal.add(item)
                            Timber.i("LOADED IMAGE")
                        }
                    }
                    _trendingShows.value = showsLocal


                }
            } else {
                Timber.e("Error loading Trending Shows: ${response.code()}")
            } */
        }
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DiscoverViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DiscoverViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
