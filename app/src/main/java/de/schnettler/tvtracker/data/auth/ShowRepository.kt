package de.schnettler.tvtracker.data.auth

import androidx.lifecycle.Transformations
import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.auth.model.AuthTokenType
import de.schnettler.tvtracker.data.show.ShowDataSourceLocal
import de.schnettler.tvtracker.data.show.ShowDataSourceRemote
import timber.log.Timber

class AuthRepository(private val remoteService: AuthDataSourceRemote, private val localDao: AuthDataSourceLocal) {

    /*
     * Get the [ShowDetailsRemote] corresponding to [showId]
     */
    suspend fun refreshTvdbAuthToken(login: Boolean, oldToken: String) {
        //Refresh  from Network
        val result = remoteService.getRefreshToken(login, oldToken)

        if (result is Result.Success) {
            //Insert in DB
            localDao.insertAuthToken(result.data.toAuthTokenDB(AuthTokenType.TVDB))
        } else {
            val resultError = result as Result.Error
            resultError.exception.printStackTrace()
        }
    }

    fun getAuthToken(type: AuthTokenType) = localDao.getAuthToken(type)
}