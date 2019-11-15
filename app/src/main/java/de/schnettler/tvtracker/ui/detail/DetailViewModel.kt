package de.schnettler.tvtracker.ui.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.schnettler.tvtracker.data.Repository
import de.schnettler.tvtracker.data.model.Show
import de.schnettler.tvtracker.ui.discover.DiscoverViewModel
import kotlinx.coroutines.launch

class DetailViewModel(val show: Show, val context: Application) : ViewModel() {
    private val repo = Repository(context, viewModelScope)

    var showDetails = repo.getShowDetails(show.id)
    var cast = repo.getCast(show.id)

    init {
        viewModelScope.launch {
            repo.refreshShowSummary(show.id)
            //repo.refreshShowCast(show.id)
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
