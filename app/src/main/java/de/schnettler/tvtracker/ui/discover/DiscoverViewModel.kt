package de.schnettler.tvtracker.ui.discover

import android.app.Application
import androidx.lifecycle.*
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.repository.show.ShowDataSourceLocal
import de.schnettler.tvtracker.data.repository.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.repository.show.ShowRepository
import kotlinx.coroutines.launch


class DiscoverViewModel(val context: Application) : AndroidViewModel(context) {
    private val showRepository = ShowRepository(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService, RetrofitClient.imagesNetworkService),
        ShowDataSourceLocal(getDatabase(context).trendingShowsDao)
    )

    //Trending Shows
    val trendingShows = showRepository.getTrending()
    val popularShows = showRepository.getPopular()
    val anticipatedShows = showRepository.getAnticipated()

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing

    init {
        onRefresh()
    }

     fun onRefresh() {
         _isRefreshing.value = true
         viewModelScope.launch {
             showRepository.refreshTrendingShows()
             showRepository.refreshPopularShows()
             showRepository.refreshAnticipatedShows()
         }
         _isRefreshing.value = false
    }
}
