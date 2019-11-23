package de.schnettler.tvtracker.data.repository.auth

import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.api.TVDB
import de.schnettler.tvtracker.data.models.AuthTokenDB
import de.schnettler.tvtracker.data.models.AuthTokenType
import de.schnettler.tvtracker.data.models.TvdbAuthTokenResponse
import de.schnettler.tvtracker.data.models.TvdbLoginData
import de.schnettler.tvtracker.data.db.ShowDao
import de.schnettler.tvtracker.util.safeApiCall
import java.io.IOException
import org.json.JSONObject



class AuthDataSourceRemote(private val service: TVDB) {
    //Show Details
    suspend fun getRefreshToken(login: Boolean, token: String) = safeApiCall(
        call = { refreshToken(login, token) },
        errorMessage = "Error getting Token"
    )

    private suspend fun refreshToken(login: Boolean, token: String): Result<TvdbAuthTokenResponse> {
        val paramObject = JSONObject()
        paramObject.put("apikey", TVDB.API_KEY)
        val response = if (login) service.login(TvdbLoginData()) else service.refreshToken(TVDB.AUTH_PREFIX + token)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting tvdb Token: ${response.code()} ${response.errorBody()?.string()}"))
    }
}


class AuthDataSourceLocal(private val dao: ShowDao) {
    fun insertAuthToken(authTokenDB: AuthTokenDB) {
        dao.insertAuthToken(authTokenDB)
    }

    fun getAuthToken(type: AuthTokenType) = dao.getAuthToken(type.value)
}