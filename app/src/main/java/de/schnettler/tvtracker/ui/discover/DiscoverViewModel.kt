package de.schnettler.tvtracker.ui.discover

import android.app.Application
import androidx.lifecycle.*
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.repository.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.repository.show.ShowRepositoryImpl
import de.schnettler.tvtracker.util.TopListType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DiscoverViewModel(val context: Application) : AndroidViewModel(context) {
    private val showRepository = ShowRepositoryImpl(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService, RetrofitClient.imagesNetworkService),
        getDatabase(context).trendingShowsDao
    )

    //Trending Shows
    val trendingShows = showRepository.getTopList(TopListType.TRENDING)
    val popularShows = showRepository.getTopList(TopListType.POPULAR)
    val anticipatedShows = showRepository.getTopList(TopListType.ANTICIPATED)
    val recommendedShows = showRepository.getTopList(TopListType.RECOMMENDED)

    private val _loggedIn = MutableLiveData<Boolean>()
    val loggedIn: LiveData<Boolean> get() = _loggedIn

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

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

    fun onLoginChanged(newValue: Boolean, token: String) {
        _loggedIn.value = newValue

        if (newValue) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    showRepository.refreshShowList(TopListType.RECOMMENDED, token)
                }
            }
        }
    }
}
