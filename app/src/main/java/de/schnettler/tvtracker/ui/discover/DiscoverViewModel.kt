package de.schnettler.tvtracker.ui.discover

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.schnettler.tvtracker.data.Repository
import de.schnettler.tvtracker.data.local.getDatabase
import kotlinx.coroutines.launch

class DiscoverViewModel(application: Application) : ViewModel() {
    private val dataBase = getDatabase(application)
    //Repository
    private val repo = Repository(dataBase.trendingShowsDao)

    //Trending Shows
    var trendingShows = repo.getTrendingShows()
    /*
    private val _trendingShows = MutableLiveData<List<Show>>()
    val trendingShows: LiveData<List<Show>>
        get() = _trendingShows */

    init {
        viewModelScope.launch {
            repo.refreshTrendingShows()
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
