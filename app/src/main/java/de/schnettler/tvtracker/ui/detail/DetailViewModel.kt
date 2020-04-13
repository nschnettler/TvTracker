package de.schnettler.tvtracker.ui.detail

import androidx.lifecycle.*
import com.dropbox.android.external.store4.StoreResponse
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.models.SeasonDomain
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.data.repository.show.EpisodeRepository
import de.schnettler.tvtracker.data.repository.show.ShowRepository
import kotlinx.coroutines.*
import timber.log.Timber

@ExperimentalCoroutinesApi
@FlowPreview
class DetailViewModel(
    var show: ShowDomain,
    private val showRepository: ShowRepository,
    private val episodeRepository: EpisodeRepository
) : StateViewModel<DetailViewState>() {
    override val state = MediatorLiveData<DetailViewState>()
    private val authToken = MutableLiveData<String>()

    private val showDetails
            = showRepository.getShowDetails(show.id).asLiveData(viewModelScope.coroutineContext)
    private val relatedShows
            = showRepository.getRelated(show.id).asLiveData(viewModelScope.coroutineContext)
    private val seasons
            = showRepository.getSeasons(show.id).asLiveData(viewModelScope.coroutineContext)
    private val showCast = Transformations.switchMap(authToken) {
        showRepository.getCast(show.tvdbId!!, it).asLiveData()
    }

    private val _status = MutableLiveData<String?>()
    val status: LiveData<String?> get() = _status

    fun getIndexOfEpisode(episode: EpisodeDomain): Int {
        Timber.i("Getting Index of Season ${episode.season} Episode ${episode.number}")
        var result = 0

        //Lower Seasons
        if (seasons.value is StoreResponse.Data) {
            (seasons.value as StoreResponse.Data<List<SeasonDomain>>).value.forEach {
                if (it.number < episode.season) {
                    Timber.i("Season ${it.number} Episodes ${it.episodeCount}")
                    result += it.episodeCount?.toInt() ?: 0
                }
            }
        }

        result += episode.number.toInt() - 1
        Timber.i("Index: $result")
        return result
    }

    init {
        initState { DetailViewState(show) }
        stateInitialized

        //Observe Details
        state.addSource(showDetails) {
            when (it) {
                is StoreResponse.Loading -> Timber.i("Loading Details")//Show Spinner
                is StoreResponse.Data -> updateState { state -> state.copy(details = it.value) }
                is StoreResponse.Error -> showErrorMessage("Error loading details", it.error)
            }
        }

        //Observe Cast
        state.addSource(showCast) {
            when (it) {
                is StoreResponse.Loading -> Timber.i("Loading Cast")//Show Spinner
                is StoreResponse.Data -> updateState { state -> state.copy(cast = it.value) }
                is StoreResponse.Error -> showErrorMessage("Error loading cast", it.error)
            }
        }

        //Observe Related Shows
        state.addSource(relatedShows) {
            when (it) {
                is StoreResponse.Loading -> Timber.i("Loading Related")//Show Spinner
                is StoreResponse.Data -> updateState { state -> state.copy(relatedShows = it.value) }
                is StoreResponse.Error -> showErrorMessage("Error loading related", it.error)
            }
        }

        //Observe Seasons
        state.addSource(seasons) {
            when (it) {
                is StoreResponse.Loading -> Timber.i("Loading Seasons")//Show Spinner
                is StoreResponse.Data -> updateState { state -> state.copy(seasons = it.value) }
                is StoreResponse.Error -> {
                    showErrorMessage("Error loading seasons", it.error)
                }
            }
        }
    }

    fun onChangeSeasonExpansion(season: SeasonDomain, expand: Boolean) {
        //Change ExpansionState
        when (expand) {
            true -> {
                updateState {
                    val expanded = it.expandedSeasons
                    expanded.add(season.number)
                    it.copy(expandedSeasons = expanded)
                }

                //Refresh Episodes
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        episodeRepository.refreshEpisodes(show.id, season.number)
                    }
                }
            }
            false -> {
                updateState {
                    val expanded = it.expandedSeasons
                    expanded.remove(season.number)
                    it.copy(expandedSeasons = expanded)
                }
            }
        }
    }

    fun resetStatus() {
        _status.value = null;
    }

    fun onLogin(token: String) {
        authToken.value = token
    }

    private fun showErrorMessage(newStatus: String, error: Throwable) {
        _status.value = "$newStatus: ${error.message}"
        Timber.e(error)
    }
}
