package de.schnettler.tvtracker.ui.detail

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.models.SeasonDomain
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.data.repository.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.repository.show.ShowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class DetailViewModel(var show: ShowDomain, val context: Application) : StateViewModel<DetailViewState>() {
    override val state = MediatorLiveData<DetailViewState>()
    private val showRepository = ShowRepository(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService, RetrofitClient.imagesNetworkService),
        getDatabase(context).trendingShowsDao
    )

    private val showDetails = showRepository.getShowDetails(show.id)
    private val showCast = showRepository.getShowCast(show.tvdbId!!)
    private val relatedShows = showRepository.getRelatedShows(show.id)
    private val seasons = showRepository.getSeasonsWithEpisodes(show.id)

    fun getIndexOfEpisode(episode: EpisodeDomain): Int {
        Timber.i("Getting Index of Season ${episode.season} Episode ${episode.number}")
        var result = 0

        //Lower Seasons
        seasons.value?.forEach {
            if (it.number < episode.season) {
                Timber.i("Season ${it.number} Episodes ${it.episodeCount}")
                result += it.episodeCount?.toInt() ?: 0
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
            //Details Changed
            Timber.i("Show Details Changed")
            updateState {state ->
                state.copy(details = it)
            }
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

    fun refreshCast(token: String) {
        Timber.i("Refreshing Cast with $token")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                show.tvdbId?.let {
                    showRepository.refreshCast(it, token)
                }
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
