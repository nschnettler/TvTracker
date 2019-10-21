package de.schnettler.tvtracker.ui.discover

import androidx.lifecycle.*
import de.schnettler.tvtracker.data.Repository
import de.schnettler.tvtracker.data.model.Show
import de.schnettler.tvtracker.data.model.TrendingShowRemote
import de.schnettler.tvtracker.data.model.asShowModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

class DiscoverViewModel : ViewModel() {
    //Repository
    private val repo = Repository()

    //Trending Shows
    private val _trendingShows = MutableLiveData<List<Show>>()
    val trendingShows: LiveData<List<Show>>
        get() = _trendingShows
    val resultString = Transformations.map(_trendingShows) { show ->
        show.joinToString("\n") { "${it.title} - ${it.year} - ${it.watchers} Watchers" }
    }

    init {
        getTrendingShows()
    }


    private fun getTrendingShows() {
        viewModelScope.launch {
            val response = repo.getTrendingShows()
            if (response.isSuccessful) {
                response.body().let {
                    _trendingShows.value = it?.asShowModel()
                }
            } else {
                Timber.e("Error loading Trending Shows: ${response.code()}")
            }
        }
    }
}
