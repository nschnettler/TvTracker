package de.schnettler.tvtracker.ui.episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.data.repository.show.EpisodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EpisodeViewModel @AssistedInject constructor(
    @Assisted private val episode: EpisodeDomain,
    @Assisted private var show: ShowDomain,
    private val episodeRepository: EpisodeRepository
) : ViewModel() {

    val episodeList = episodeRepository.getEpisodes(episode.showId)

    init {
        startRefresh(episode.season, episode.number)
    }

    fun refreshNeighborEpisodes(position: Int) {
        val episodes = episodeList.value
        episodes?.let {
            refreshNeighbor(position)
            refreshNeighbor(position+1)
            refreshNeighbor(position-1)
        }
    }

    private fun refreshNeighbor(position: Int) {
        episodeList.value?.let {
            it.getOrNull(position)?.let {current ->
                if(current.stillPath.isNullOrEmpty()) {
                    startRefresh(current.season, current.number)
                }
            }
        }
    }

    private fun startRefresh(season: Long, number: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                episodeRepository.refreshEpisodeDetails(show.id, show.tmdbId ?: return@withContext, season, number)
            }
        }
    }

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(show: ShowDomain, episode: EpisodeDomain): EpisodeViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            show: ShowDomain,
            episode: EpisodeDomain
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(show, episode) as T
            }
        }
    }
}