package de.schnettler.tvtracker

import android.app.Application
import androidx.lifecycle.*
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.models.AuthTokenType
import de.schnettler.tvtracker.data.repository.auth.AuthDataSourceRemote
import de.schnettler.tvtracker.data.repository.auth.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class AuthViewModel(val context: Application) : ViewModel() {
    private val authRepository = AuthRepository(
        AuthDataSourceRemote(RetrofitClient.tvdbNetworkService, RetrofitClient.showsNetworkService),
        getDatabase(context).authDao)

    //State
    private val _startAuthentication = MutableLiveData<Boolean>()
    val startAuthentication: LiveData<Boolean>
        get() = _startAuthentication

    //Trakt Authentication
    val traktAuthToken = authRepository.getAuthToken(AuthTokenType.TRAKT)
    val traktLoginStatus = MediatorLiveData<Boolean>()
    val tvdbAuthToken = authRepository.getAuthToken(AuthTokenType.TVDB)
    val tvdbLoginStatus = MediatorLiveData<Boolean>()

    //Init
    init {
        traktLoginStatus.addSource(traktAuthToken) {
            traktLoginStatus.value = it != null
        }
        tvdbLoginStatus.addSource(tvdbAuthToken) {
            val currentTime = System.currentTimeMillis()  / 1000L
            var result = false
            if (it == null || it.createdAtMillis + 86400 <= currentTime) {
                startTvdbAuthentication(true)
            } else {
                if(it.createdAtMillis >= currentTime + 72000) {
                    startTvdbAuthentication(false)
                } else {
                    result = true
                }
            }
            tvdbLoginStatus.value = result
        }
    }

    private fun startTvdbAuthentication(loginNeeded: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Timber.i("Refreshing Auth Token (is login $loginNeeded)")
                authRepository.refreshTvdbAuthToken(loginNeeded, tvdbAuthToken.value?.token ?: "")
            }
        }
    }


    //On Code Received
    fun onAuthResponse(code: String) {
        viewModelScope.launch { authRepository.refreshTraktAccessToken(code) }
    }

    //Handle Login & Logout Clicks
    fun onLoginClicked() {
        when(traktLoginStatus.value) {
            true -> {
                traktAuthToken.value?.let {
                    viewModelScope.launch {
                        authRepository.revokeTraktToken(it.token)
                    }
                }
            }
            false -> _startAuthentication.value = true
        }
    }
    fun onLoginHandled() { _startAuthentication.value = false }
}
