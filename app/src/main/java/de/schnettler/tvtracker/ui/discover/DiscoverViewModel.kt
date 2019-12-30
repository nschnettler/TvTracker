package de.schnettler.tvtracker.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import de.schnettler.tvtracker.data.repository.show.IShowRepository
import de.schnettler.tvtracker.util.TopListType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DiscoverViewModel(private val repo: IShowRepository): StateViewModel<DiscoverViewState>() {
    override val state = MediatorLiveData<DiscoverViewState>()
    private val _loggedIn = MutableLiveData<Boolean>()
    val loggedIn: LiveData<Boolean> get() = _loggedIn

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    init {
        initState { DiscoverViewState() }
        stateInitialized

        state.addSource(repo.getTopList(TopListType.TRENDING)) {
            updateState { state -> state.copy(trendingShows = it) }
        }
        state.addSource(repo.getTopList(TopListType.POPULAR)) {
            updateState { state -> state.copy(popularShows = it) }
        }
        state.addSource(repo.getTopList(TopListType.ANTICIPATED)) {
            updateState { state -> state.copy(anticipatedShows = it) }
        }
        state.addSource(repo.getTopList(TopListType.RECOMMENDED)) {
            updateState { state -> state.copy(recommendedShows = it) }
        }
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
