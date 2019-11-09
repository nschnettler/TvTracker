package de.schnettler.tvtracker.ui.account

import androidx.lifecycle.*


class AccountViewModel() :ViewModel() {
    private val _startAuthentication = MutableLiveData<Boolean>()
    val startAuthentication: LiveData<Boolean>
        get() = _startAuthentication


    fun onLoginClicked() { _startAuthentication.value = true }
    fun onLoginHandled() { _startAuthentication.value = false }
}
