package de.schnettler.tvtracker.ui.detail

import android.app.Application
import androidx.lifecycle.*
import de.schnettler.tvtracker.data.Repository
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.show.model.Show
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.auth.AuthDataSourceLocal
import de.schnettler.tvtracker.data.auth.AuthDataSourceRemote
import de.schnettler.tvtracker.data.auth.AuthRepository
import de.schnettler.tvtracker.data.auth.model.AuthTokenDB
import de.schnettler.tvtracker.data.auth.model.AuthTokenType
import de.schnettler.tvtracker.data.show.ShowDataSourceLocal
import de.schnettler.tvtracker.data.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.show.ShowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class DetailViewModel(val show: Show, val context: Application) : ViewModel() {
    private val repo = Repository(context, viewModelScope)
    private val showRepository = ShowRepository(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService),
        ShowDataSourceLocal(getDatabase(context).trendingShowsDao)
    )
    private val authRepository = AuthRepository(
        AuthDataSourceRemote(RetrofitClient.tvdbNetworkService),
        AuthDataSourceLocal(getDatabase(context).trendingShowsDao)
    )

    var showDetails = showRepository.getShowDetails(show.id)
    val tvdbAuth = authRepository.getAuthToken(AuthTokenType.TVDB)
    val showCast = showRepository.getShowCast(show.tvdbId!!)

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                showRepository.refreshShowDetails(show.id)
            }
        }
    }

    fun authenticate(loginNeeded: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Timber.i("Refreshing Auth Token (is login $loginNeeded)")
                authRepository.refreshTvdbAuthToken(loginNeeded, tvdbAuth.value?.token ?: "")
            }
        }
    }

    fun load(token: String) {
        if (showCast.value.isNullOrEmpty()) {
            Timber.i("Refreshing Cast")
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    show.tvdbId?.let {
                        showRepository.refreshCast(show.tvdbId, token)
                    }
                }
            }
        }
    }
//    fun validTokenLoaded() {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                showRepository.refreshCast(270915, tvdbAuth.value!!.token)
//            }
//        }
//    }
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
