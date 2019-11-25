package de.schnettler.tvtracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.repository.auth.AuthDataSourceRemote
import de.schnettler.tvtracker.data.repository.auth.AuthRepository
import kotlinx.coroutines.launch
import timber.log.Timber


class MainViewModel(val context: Application) : AndroidViewModel(context) {
    private val authRepository = AuthRepository(
        AuthDataSourceRemote(RetrofitClient.tvdbNetworkService, RetrofitClient.showsNetworkService),
        getDatabase(context).authDao)

    fun onAuthResponse(code: String) {
        Timber.i(code)
        viewModelScope.launch {
            val result = authRepository.refreshTraktAccessToken(code)
        }

    }
}
