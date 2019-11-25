package de.schnettler.tvtracker.data.repository.auth

import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.api.Trakt
import de.schnettler.tvtracker.data.db.AuthDao
import de.schnettler.tvtracker.data.mapping.AuthMapper
import de.schnettler.tvtracker.data.models.AuthTokenType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class AuthRepository(private val remoteService: AuthDataSourceRemote, private val authDao: AuthDao) {

    /*
     * Get the [ShowDetailsRemote] corresponding to [showId]
     */
    suspend fun refreshTvdbAuthToken(login: Boolean, oldToken: String) {
        when (val result = remoteService.getTvdbRefreshToken(login, oldToken)) {
            is Result.Success -> authDao.insertAuthToken(AuthMapper.mapToDatabase(result.data))
            is Result.Error -> Timber.e(result.exception)
        }
    }
    suspend fun refreshTraktAccessToken(code: String) {
        when(val result = remoteService.getTraktToken(code)) {
            is Result.Success -> authDao.insertAuthToken(AuthMapper.mapToDatabase(result.data))
            is Result.Error -> Timber.e(result.exception)
        }
    }

    fun getAuthToken(type: AuthTokenType) = authDao.getAuthToken(type.value)
}