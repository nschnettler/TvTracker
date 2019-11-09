package de.schnettler.tvtracker.data

import androidx.paging.PagedList
import de.schnettler.tvtracker.data.model.Show
import de.schnettler.tvtracker.util.SHOW_LIST_PAGE_SIZE
import de.schnettler.tvtracker.util.ShowListType
import de.schnettler.tvtracker.util.SHOW_LIST_PAGE_MAX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class ShowBoundaryCallback(
   private val repo: Repository,
   private val scope: CoroutineScope,
   private val type: ShowListType
): PagedList.BoundaryCallback<Show>() {
    private var isLoading = false
    private var lastPage = 0
    override fun onZeroItemsLoaded() {
        if (!isLoading) {
            isLoading = true
            Timber.i("Loading Initial Page")
            scope.launch {
                repo.loadNewShowListPage(1, type = type)
            }
            lastPage = 1
            isLoading = false
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Show) {
        if (!isLoading) {
            isLoading = true
            val page = (itemAtEnd.index / SHOW_LIST_PAGE_SIZE) + 1
            if (page in (lastPage + 1)..SHOW_LIST_PAGE_MAX) {
                Timber.i("Loading Page $page?")
                scope.launch {
                    repo.loadNewShowListPage(page, type = type)
                }
                lastPage = page
            }
            isLoading = false
        }
    }
}