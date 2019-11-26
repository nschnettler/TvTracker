package de.schnettler.tvtracker.ui.discover

import android.app.Application
import androidx.lifecycle.*
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.repository.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.repository.show.ShowRepository
import de.schnettler.tvtracker.util.TopListType
import kotlinx.coroutines.launch


class DiscoverViewModel(val context: Application) : AndroidViewModel(context) {
    private val showRepository = ShowRepository(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService, RetrofitClient.imagesNetworkService),
        getDatabase(context).trendingShowsDao
    )

    //Trending Shows
    val trendingShows = showRepository.getTopList(TopListType.TRENDING)
    val popularShows = showRepository.getTopList(TopListType.POPULAR)
    val anticipatedShows = showRepository.getTopList(TopListType.ANTICIPATED)

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing

    init {
        onRefresh()
    }

     fun onRefresh() {
         _isRefreshing.value = true
         viewModelScope.launch {
             showRepository.refreshShowList(TopListType.TRENDING)
             showRepository.refreshShowList(TopListType.POPULAR)
             showRepository.refreshShowList(TopListType.ANTICIPATED)
         }
         _isRefreshing.value = false
    }
}
