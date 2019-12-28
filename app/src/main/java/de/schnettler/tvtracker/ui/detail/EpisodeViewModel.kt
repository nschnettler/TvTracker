package de.schnettler.tvtracker.ui.detail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.data.models.ShowEntity
import de.schnettler.tvtracker.data.repository.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.repository.show.ShowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EpisodeViewModel(var episode: EpisodeDomain, var show: ShowDomain, val context: Application) : ViewModel() {

    private val showRepository = ShowRepository(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService, RetrofitClient.imagesNetworkService),
        getDatabase(context).trendingShowsDao
    )
    val episodeList = showRepository.getSeasonEpisodes(episode.seasonId)

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                showRepository.refreshEpisodeDetails(showId = show.tmdbId, seasonNumber = episode.season, episodeNumber = episode.number, episodeId = episode.id)
            }
        }
    }
}