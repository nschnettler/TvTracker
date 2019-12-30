package de.schnettler.tvtracker.data.repository.show

import androidx.paging.PagedList
import de.schnettler.tvtracker.data.models.EpisodeFullDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class EpisodeBoundaryCallback(
    private val repo: EpisodeRepository,
    private val scope: CoroutineScope
): PagedList.BoundaryCallback<EpisodeFullDomain>() {
    private var isLoading = false

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        Timber.i("ZERO ITEMS")
    }

    override fun onItemAtEndLoaded(itemAtEnd: EpisodeFullDomain) {
        if(isLoading) return
        isLoading = true
        super.onItemAtEndLoaded(itemAtEnd)
        Timber.i("Refreshing Back")
        refreshEpisodes(itemAtEnd.showId, itemAtEnd.season + 1)
        isLoading = false
    }

    override fun onItemAtFrontLoaded(itemAtFront: EpisodeFullDomain) {
        if(isLoading || itemAtFront.season.toInt() == 0) return
        isLoading = true
        super.onItemAtFrontLoaded(itemAtFront)
        Timber.i("Refreshing Front")
        refreshEpisodes(itemAtFront.showId, itemAtFront.season -1)
        isLoading = false
    }

    private fun refreshEpisodes(showId: Long, season: Long) {
        scope.launch {
            withContext(Dispatchers.IO) {
                repo.refreshEpisodes(showId, season)
            }
        }
    }
}