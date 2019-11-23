package de.schnettler.tvtracker.ui.episode

import android.app.Application
import androidx.lifecycle.*
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import de.schnettler.tvtracker.data.models.ShowDomain

class EpisodeViewModel(var show: ShowDomain, val context: Application) : StateViewModel<EpisodeViewState>() {
    override val state = MediatorLiveData<EpisodeViewState>()
//
//    private val showRepository = ShowRepository(
//        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService, RetrofitClient.imagesNetworkService),
//        ShowDataSourceLocal(getDatabase(context).trendingShowsDao)
//    )
//    private val authRepository = AuthRepository(
//        AuthDataSourceRemote(RetrofitClient.tvdbNetworkService),
//        AuthDataSourceLocal(getDatabase(context).trendingShowsDao)
//    )
//
//    private val showDetails = showRepository.getShowDetails(show.id)
//    private val tvdbAuth = authRepository.getAuthToken(AuthTokenType.TVDB)
//    private val showCast = showRepository.getShowCast(show.tvdbId!!)
//    private val relatedShows = showRepository.getRelatedShows(show.id)
//    private val seasons = showRepository.getSeasonsWithEpisodes(show.id)
//
//    init {
//        initState { DetailViewState(show) }
//        stateInitialized
//
//        //Observe Details
//        state.addSource(showDetails) {
//            //Details Changed
//            Timber.i("Show Details Changed")
//            updateState {state ->
//                state.copy(details = it)
//            }
//        }
//
//        //Observe Auth State
//        state.addSource(tvdbAuth) {
//            var authState: String = "Unauthorized"
//            val currentTime = System.currentTimeMillis()  / 1000L
//            if (it == null || it.createdAtMillis + 86400 <= currentTime) {
//                //Login Needed
//                authState = "Login Needed"
//                startAuthentication(true)
//            } else {
//                val threshold = currentTime + 72000
//                if(it.createdAtMillis >= threshold) {
//                    authState = "Authorization expiring"
//                    startAuthentication(false)
//                } else {
//                    authState = "Authorized"
//                    show.tvdbId?.let {id ->
//                        if (showCast.value.isNullOrEmpty()) {
//                            refreshCast(id, it.token)
//                        }
//                    }
//
//                }
//            }
//            Timber.i("Auth State Changed: $authState")
//        }
//
//        //Observe Cast
//        state.addSource(showCast) {
//            Timber.i("Show Cast Changed")
//            updateState { state ->
//                state.copy(cast = it)
//            }
//        }
//
//        //Observe Related Shows
//        state.addSource(relatedShows) {
//            Timber.i("Related Shows Changed")
//            updateState { state ->
//                state.copy(relatedShows = it)
//            }
//        }
//
//        //Observe Seasons
//        state.addSource(seasons) {
//            Timber.i("Seasons Changed")
//            updateState { state ->
//                state.copy(seasons = it)
//            }
//        }
//
//        //Refresh Data
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                showRepository.refreshShowDetails(show.id)
//                showRepository.refreshRelatedShows(show.id)
//                showRepository.refreshSeasons(show.id)
//            }
//        }
//    }
//
//    private fun startAuthentication(loginNeeded: Boolean) {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                Timber.i("Refreshing Auth Token (is login $loginNeeded)")
//                authRepository.refreshTvdbAuthToken(loginNeeded, tvdbAuth.value?.token ?: "")
//            }
//        }
//    }
//
//    private fun refreshCast(showId: Long, token: String) {
//        Timber.i("Refreshing Cast")
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                showRepository.refreshCast(showId, token)
//            }
//        }
//    }
//
//    fun onChangeSeasonExpansion(season: SeasonDomain, expand: Boolean) {
//        //Change ExpansionState
//        when (expand) {
//            true -> {
//                updateState {
//                    it.copy(expandedSeasons = it.expandedSeasons + season.id)
//                }
//
//                //Refresh Episodes
//                viewModelScope.launch {
//                    withContext(Dispatchers.IO) {
//                        showRepository.refreshEpisodes(show.id, season.number, season.id)
//                    }
//                }
//            }
//            false -> {
//                updateState {
//                    it.copy(expandedSeasons = it.expandedSeasons - season.id)
//                }
//            }
//        }
//    }
}
