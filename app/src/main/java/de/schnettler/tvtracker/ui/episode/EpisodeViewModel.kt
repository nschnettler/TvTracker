package de.schnettler.tvtracker.ui.episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.repository.show.EpisodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EpisodeViewModel(var episode: EpisodeDomain, private var showTmdbId: String, private val episodeRepository: EpisodeRepository) : ViewModel() {

    val episodeList = episodeRepository.getEpisodes(episode.showId, viewModelScope)

    init {
        startRefresh(episode.season, episode.number, episode.id)
    }

    fun refreshDetails(position: Int) {
        val episodes = episodeList.value
        episodes?.let {
            it[position]?.let {episodeAt ->
                startRefresh(episodeAt.season, episodeAt.number, episodeAt.id)
            }

//            when (position) {
//                0 -> startRefresh(it[position + 1])//Refresh next
//                it.size - 2 -> startRefresh(it[position - 1])//Refresh previous
//                it.size - 1 -> return@let
//                else -> {
//                    startRefresh(it[position + 1])
//                    startRefresh(it[position - 1])
//                }
//            }
        }
    }

    private fun startRefresh(season: Long, number: Long, id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                episodeRepository.refreshEpisodeDetails(showTmdbId, season, number, id)
            }
        }
    }
}