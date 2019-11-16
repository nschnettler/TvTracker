package de.schnettler.tvtracker.data.auth

import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.api.tvdb.TvdbService
import de.schnettler.tvtracker.data.auth.model.AuthTokenDB
import de.schnettler.tvtracker.data.auth.model.AuthTokenType
import de.schnettler.tvtracker.data.auth.model.TvdbAuthTokenResponse
import de.schnettler.tvtracker.data.auth.model.TvdbLoginData
import de.schnettler.tvtracker.data.db.TrendingShowsDAO
import de.schnettler.tvtracker.data.show.model.ShowDetailsDB
import de.schnettler.tvtracker.util.safeApiCall
import java.io.IOException
import org.json.JSONObject



class AuthDataSourceRemote(private val service: TvdbService) {
    //Show Details
    suspend fun getRefreshToken(login: Boolean, token: String) = safeApiCall(
        call = { refreshToken(login, token) },
        errorMessage = "Error getting Token"
    )

    private suspend fun refreshToken(login: Boolean, token: String): Result<TvdbAuthTokenResponse> {
        val paramObject = JSONObject()
        paramObject.put("apikey", TvdbService.API_KEY)
        val response = if (login) service.login(TvdbLoginData()) else service.refreshToken(TvdbService.AUTH_PREFIX + token)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting tvdb Token: ${response.code()} ${response.errorBody()?.string()}"))
    }
}


class AuthDataSourceLocal(private val dao: TrendingShowsDAO) {
    fun insertAuthToken(authTokenDB: AuthTokenDB) {
        dao.insertAuthToken(authTokenDB)
    }

    fun getAuthToken(type: AuthTokenType) = dao.getAuthToken(type.value)
}