package de.schnettler.tvtracker.ui.detail

import android.app.Application
import androidx.lifecycle.*
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.show.model.Show
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.auth.AuthDataSourceLocal
import de.schnettler.tvtracker.data.auth.AuthDataSourceRemote
import de.schnettler.tvtracker.data.auth.AuthRepository
import de.schnettler.tvtracker.data.auth.model.AuthTokenType
import de.schnettler.tvtracker.data.show.ShowDataSourceLocal
import de.schnettler.tvtracker.data.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.show.ShowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class DetailViewModel(val show: Show, val context: Application) : StateViewModel<DetailViewState>() {
    override val state = MediatorLiveData<DetailViewState>()
    private val showRepository = ShowRepository(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService, RetrofitClient.imagesNetworkService),
        ShowDataSourceLocal(getDatabase(context).trendingShowsDao)
    )
    private val authRepository = AuthRepository(
        AuthDataSourceRemote(RetrofitClient.tvdbNetworkService),
        AuthDataSourceLocal(getDatabase(context).trendingShowsDao)
    )

    private val showDetails = showRepository.getShowDetails(show.id)
    private val tvdbAuth = authRepository.getAuthToken(AuthTokenType.TVDB)
    private val showCast = showRepository.getShowCast(show.tvdbId!!)
    private val relatedShows = showRepository.getRelatedShows(show.id)

    init {
        initState { DetailViewState(show) }
        stateInitialized

        //Observe Details
        state.addSource(showDetails) {
            //Details Changed
            Timber.i("Show Details Changed")
            updateState {state ->
                state.copy(details = it)
            }
        }

        //Observe Auth State
        state.addSource(tvdbAuth) {
            var authState: String = "Unauthorized"

            if (it == null) {
                //Login Needed
                startAuthentication(true)
            } else {
                val threshold = System.currentTimeMillis() / 1000L + 72000
                if(it.createdAtMillis >= threshold) {
                   authState = "Authorization expiring"
                    startAuthentication(false)
                } else {
                    authState = "Authorized"
                    show.tvdbId?.let {id ->
                        if (showCast.value.isNullOrEmpty()) {
                            refreshCast(id, it.token)
                        }
                    }

                }
            }
            Timber.i("Auth State Changed: $authState")
        }

        //Observe Cast
        state.addSource(showCast) {
            Timber.i("Show Cast Changed")
            updateState { state ->
                state.copy(cast = it)
            }
        }

        //Observe Related Shows
        state.addSource(relatedShows) {
            Timber.i("Related Shows Changed")
            updateState { state ->
                state.copy(relatedShows = it)
            }
        }

        //Refresh Data
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                showRepository.refreshShowDetails(show.id)
                showRepository.refreshRelatedShows(show.id)
            }
        }
    }

    private fun startAuthentication(loginNeeded: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Timber.i("Refreshing Auth Token (is login $loginNeeded)")
                authRepository.refreshTvdbAuthToken(loginNeeded, tvdbAuth.value?.token ?: "")
            }
        }
    }

    private fun refreshCast(showId: Long, token: String) {
        Timber.i("Refreshing Cast")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                showRepository.refreshCast(showId, token)
            }
        }
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val show: Show, val app: Application ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(show, app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
