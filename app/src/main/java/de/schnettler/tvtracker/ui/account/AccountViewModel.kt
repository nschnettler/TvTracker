package de.schnettler.tvtracker.ui.account

import android.app.Application
import androidx.lifecycle.*
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.models.AuthTokenDomain
import de.schnettler.tvtracker.data.models.AuthTokenType
import de.schnettler.tvtracker.data.repository.auth.AuthDataSourceRemote
import de.schnettler.tvtracker.data.repository.auth.AuthRepository
import kotlinx.coroutines.launch


class AccountViewModel(val context: Application) :ViewModel() {
    private val authRepository = AuthRepository(
        AuthDataSourceRemote(RetrofitClient.tvdbNetworkService, RetrofitClient.showsNetworkService),
        getDatabase(context).authDao)

    private val _startAuthentication = MutableLiveData<Boolean>()
    val startAuthentication: LiveData<Boolean>
        get() = _startAuthentication

    val traktAuthToken = authRepository.getAuthToken(AuthTokenType.TRAKT)
    val userAuthenticated = MediatorLiveData<Boolean>()

    init {
        userAuthenticated.addSource(traktAuthToken) {
            userAuthenticated.value = it != null
        }
    }




    fun onLoginClicked() {
        when(userAuthenticated.value) {
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
