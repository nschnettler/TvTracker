package de.schnettler.tvtracker.ui.detail

import android.app.Application
import androidx.lifecycle.*
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.repository.auth.AuthDataSourceLocal
import de.schnettler.tvtracker.data.repository.auth.AuthDataSourceRemote
import de.schnettler.tvtracker.data.repository.auth.AuthRepository
import de.schnettler.tvtracker.data.repository.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.repository.show.ShowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import androidx.lifecycle.Transformations
import de.schnettler.tvtracker.data.models.*


class DetailViewModel(var show: ShowDomain, val context: Application) : StateViewModel<DetailViewState>() {
    override val state = MediatorLiveData<DetailViewState>()
    private val showRepository = ShowRepository(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService, RetrofitClient.imagesNetworkService),
        getDatabase(context).trendingShowsDao
    )
    private val authRepository = AuthRepository(
        AuthDataSourceRemote(RetrofitClient.tvdbNetworkService),
        AuthDataSourceLocal(getDatabase(context).trendingShowsDao)
    )

    private val _episode = MutableLiveData<EpisodeDomain>()
    val episode: LiveData<EpisodeDomain>
        get() = _episode

    val episodeDetails = Transformations.switchMap(episode) { it->
        showRepository.getEpisodeDetails(it.id)
    };

//    val episodeDetail = Transformations.switchMap(episode) { episode ->
//        //repository.getDataForUser(user)
//    }

    private val showDetails = showRepository.getShowDetails(show.id)
    private val tvdbAuth = authRepository.getAuthToken(AuthTokenType.TVDB)
    private val showCast = showRepository.getShowCast(show.tvdbId!!)
    private val relatedShows = showRepository.getRelatedShows(show.id)
    private val seasons = showRepository.getSeasonsWithEpisodes(show.id)

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
            val authState: String
            val currentTime = System.currentTimeMillis()  / 1000L
            if (it == null || it.createdAtMillis + 86400 <= currentTime) {
                //Login Needed
                authState = "Login Needed"
                startAuthentication(true)
            } else {
                val threshold = currentTime + 72000
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

        //Observe Seasons
        state.addSource(seasons) {
            Timber.i("Seasons Changed")
            updateState { state ->
                state.copy(seasons = it)
            }
        }

        //Refresh Data
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                showRepository.refreshShowDetails(show.id)
                showRepository.refreshRelatedShows(show.id)
                showRepository.refreshSeasons(show.id)
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

    fun onChangeSeasonExpansion(season: SeasonDomain, expand: Boolean) {
        //Change ExpansionState
        when (expand) {
            true -> {
                updateState {
                    it.copy(expandedSeasons = it.expandedSeasons + season.id)
                }

                //Refresh Episodes
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        showRepository.refreshEpisodes(show.id, season.number, season.id)
                    }
                }
            }
            false -> {
                updateState {
                    it.copy(expandedSeasons = it.expandedSeasons - season.id)
                }
            }
        }
    }

    fun onEpisodeSelected(episode: EpisodeDomain) {
        _episode.value = episode
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                showRepository.refreshEpisodeDetails(showId = show.tmdbId, seasonNumber = episode.season, episodeNumber = episode.number, episodeId = episode.id)
            }
        }
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val show: ShowDomain, val app: Application ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(show, app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
