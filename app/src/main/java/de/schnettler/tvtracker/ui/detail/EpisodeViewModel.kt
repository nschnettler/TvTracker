package de.schnettler.tvtracker.ui.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.repository.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.repository.show.ShowRepository

class EpisodeViewModel(var seasonId: Long, val context: Application) : ViewModel() {

    private val showRepository = ShowRepository(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService, RetrofitClient.imagesNetworkService),
        getDatabase(context).trendingShowsDao
    )
    val episodeList = showRepository.getSeasonEpisodes(seasonId)
}