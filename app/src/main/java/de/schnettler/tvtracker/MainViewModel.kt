package de.schnettler.tvtracker

import android.app.Application
import androidx.lifecycle.*
import de.schnettler.tvtracker.data.Repository
import kotlinx.coroutines.launch
import timber.log.Timber


class MainViewModel(val context: Application) : AndroidViewModel(context) {
    private val repo = Repository(context, viewModelScope)

    fun onAuthResponse(code: String) {
        Timber.i(code)
        viewModelScope.launch {
            val result = repo.retrieveAccessToken(code)
            Timber.i(result.toString())
            if (result.isSuccessful) {
                Timber.i(result.body().toString())
            }
        }

    }

}
