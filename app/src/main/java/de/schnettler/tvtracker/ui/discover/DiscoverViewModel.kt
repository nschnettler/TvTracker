package de.schnettler.tvtracker.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.tvtracker.data.repository.show.IShowRepository
import de.schnettler.tvtracker.util.TopListType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DiscoverViewModel(private val repo: IShowRepository): ViewModel() {
    val trendingShows = repo.getTopList(TopListType.TRENDING)
    val popularShows = repo.getTopList(TopListType.POPULAR)
    val anticipatedShows = repo.getTopList(TopListType.ANTICIPATED)
    val recommendedShows = repo.getTopList(TopListType.RECOMMENDED)

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
             repo.refreshShowList(TopListType.TRENDING)
             repo.refreshShowList(TopListType.POPULAR)
             repo.refreshShowList(TopListType.ANTICIPATED)
         }
         _isRefreshing.value = false
    }

    fun onLoginChanged(newValue: Boolean, token: String) {
        _loggedIn.value = newValue

        if (newValue) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repo.refreshShowList(TopListType.RECOMMENDED, token)
                }
            }
        }
    }
}
