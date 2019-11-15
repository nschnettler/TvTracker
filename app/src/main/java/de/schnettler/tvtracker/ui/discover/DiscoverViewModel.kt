package de.schnettler.tvtracker.ui.discover

import android.app.Application
import androidx.lifecycle.*
import de.schnettler.tvtracker.data.Repository
import de.schnettler.tvtracker.util.ShowListType
import kotlinx.coroutines.launch


class DiscoverViewModel(val context: Application) : AndroidViewModel(context) {
    private val repo = Repository(context, viewModelScope)

    //Trending Shows
    var trendingShows = repo.getTrendingShows()
    var popularShows = repo.getPopularShows()

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing

    init {
        onRefresh()
    }

     fun onRefresh() {
         _isRefreshing.value = true
         viewModelScope.launch {
             repo.loadNewShowListPage(page = 1, type = ShowListType.TRENDING)
             repo.loadNewShowListPage(page = 1, type = ShowListType.POPULAR)
         }
         _isRefreshing.value = false
    }
}
