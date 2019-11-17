package de.schnettler.tvtracker

import android.app.Application
import androidx.lifecycle.*
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.show.ShowDataSourceLocal
import de.schnettler.tvtracker.data.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.show.ShowRepository
import kotlinx.coroutines.launch
import timber.log.Timber


class MainViewModel(val context: Application) : AndroidViewModel(context) {
    private val showRepository = ShowRepository(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService, RetrofitClient.imagesNetworkService),
        ShowDataSourceLocal(getDatabase(context).trendingShowsDao)
    )

    fun onAuthResponse(code: String) {
        Timber.i(code)
        viewModelScope.launch {
            val result = showRepository.retrieveAccessToken(code)
            Timber.i(result.toString())
            if (result.isSuccessful) {
                Timber.i(result.body().toString())
            }
        }

    }

}
