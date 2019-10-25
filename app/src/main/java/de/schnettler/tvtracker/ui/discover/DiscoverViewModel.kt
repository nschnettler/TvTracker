package de.schnettler.tvtracker.ui.discover

import android.app.Application
import androidx.lifecycle.*
import de.schnettler.tvtracker.data.Repository
import de.schnettler.tvtracker.data.local.getDatabase
import de.schnettler.tvtracker.data.model.Show
import kotlinx.coroutines.launch

class DiscoverViewModel(application: Application) : ViewModel() {
    private val dataBase = getDatabase(application)
    //Repository
    private val repo = Repository(dataBase.trendingShowsDao)

    //Trending Shows
    var trendingShows = repo.getTrendingShows()
    var popularShows = repo.getPopularShows()

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing

    init {
        onRefresh()
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

     fun onRefresh() {
         _isRefreshing.value = true
         viewModelScope.launch {
             repo.refreshTrendingShows()
             repo.refreshPopularShows()
         }
         _isRefreshing.value = false
    }

    fun onShowClicked(show: Show) {
        viewModelScope.launch {
            repo.refreshShowSummary(show.id)
        }

    }
}
