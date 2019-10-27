package de.schnettler.tvtracker.ui.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.schnettler.tvtracker.data.model.Show
import de.schnettler.tvtracker.ui.discover.DiscoverViewModel

class DetailViewModel(val show: Show) : ViewModel() {
    // TODO: Implement the ViewModel



    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val show: Show) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(show) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
