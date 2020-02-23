package de.schnettler.tvtracker.ui.discover

import androidx.lifecycle.*
import com.dropbox.android.external.store4.StoreResponse
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import de.schnettler.tvtracker.data.repository.show.ShowRepository
import de.schnettler.tvtracker.util.TopListType
import kotlinx.coroutines.*
import timber.log.Timber

@ExperimentalCoroutinesApi
@FlowPreview
class DiscoverViewModel(private val repo: ShowRepository): StateViewModel<DiscoverViewState>() {
    override val state = MediatorLiveData<DiscoverViewState>()
    private val _loggedIn = MutableLiveData<Boolean>()
    val loggedIn: LiveData<Boolean> get() = _loggedIn
    private val _status = MutableLiveData<String?>()
    val status: LiveData<String?> get() = _status

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val trending = repo.getTopList(TopListType.TRENDING).asLiveData(viewModelScope.coroutineContext)
    private val popular = repo.getTopList(TopListType.POPULAR).asLiveData(viewModelScope.coroutineContext)
    private val anticipated = repo.getTopList(TopListType.ANTICIPATED).asLiveData(viewModelScope.coroutineContext)

    init {
        initState { DiscoverViewState() }
        stateInitialized

        state.addSource(trending) {
            when (it) {
                is StoreResponse.Loading -> Timber.i("Loading Trending")
                is StoreResponse.Data -> updateState { state -> state.copy(trendingShows = it.value) }
                is StoreResponse.Error -> showErrorMessage("Error loading trending", it.error)
            }
        }
        state.addSource(popular) {
            when (it) {
                is StoreResponse.Loading -> Timber.i("Loading Popular")
                is StoreResponse.Data -> updateState { state -> state.copy(popularShows = it.value) }
                is StoreResponse.Error -> showErrorMessage("Error loading popular", it.error)
            }
        }
        state.addSource(anticipated) {
            when (it) {
                is StoreResponse.Loading -> Timber.i("Loading Anticipated")
                is StoreResponse.Data -> updateState { state -> state.copy(anticipatedShows = it.value) }
                is StoreResponse.Error -> showErrorMessage("Error loading popular", it.error)
            }
        }
        state.addSource(loggedIn) {
            updateState { state -> state.copy(loggedIn = it) }
        }

    }

     fun onRefresh() {
         _isRefreshing.value = true
         _isRefreshing.value = false
    }

    fun onLoginChanged(newValue: Boolean, token: String) {
        _loggedIn.value = newValue

        if (newValue) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
//                    repo.refreshShowList(TopListType.RECOMMENDED, token)
                }
            }
        }
    }

    private fun showErrorMessage(newStatus: String, error: Throwable) {
        _status.value = newStatus
        Timber.e(error)
    }
}
