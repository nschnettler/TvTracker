package de.schnettler.tvtracker.ui.detail

import android.app.Application
import androidx.lifecycle.*
import de.schnettler.tvtracker.data.Repository
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.show.model.Show
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.show.ShowDataSourceLocal
import de.schnettler.tvtracker.data.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.show.ShowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(val show: Show, val context: Application) : ViewModel() {
    private val repo = Repository(context, viewModelScope)
    private val showRepository = ShowRepository(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService),
        ShowDataSourceLocal(getDatabase(context).trendingShowsDao)
    )

    var showDetails = showRepository.getShowDetails(show.id)
    var cast = repo.getCast(show.id)

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                showRepository.refreshShowDetails(show.id)
            }

            repo.refreshShowCast(show.id)
        }
    }
    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val show: Show, val app: Application ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(show, app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
