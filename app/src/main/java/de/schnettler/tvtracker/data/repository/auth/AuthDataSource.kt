package de.schnettler.tvtracker.data.repository.auth

import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.api.TvdbAPI
import de.schnettler.tvtracker.data.api.TraktAPI
import de.schnettler.tvtracker.data.models.*
import de.schnettler.tvtracker.util.safeApiCall
import java.io.IOException
import org.json.JSONObject
import javax.inject.Inject

class AuthDataSourceRemote @Inject constructor(
    private val tvdbService: TvdbAPI,
    private val traktService: TraktAPI
) {
    /*
     * TVDB Authentication Token
     */
    suspend fun getTvdbRefreshToken(login: Boolean, token: String) = safeApiCall(
        call = { refreshTvdbToken(login, token) },
        errorMessage = "Error getting Token"
    )

    private suspend fun refreshTvdbToken(login: Boolean, token: String): Result<TvdbAuthTokenResponse> {
        val paramObject = JSONObject()
        paramObject.put("apikey", TvdbAPI.API_KEY)
        val response = if (login) tvdbService.login(TvdbLoginData()) else tvdbService.refreshToken(TvdbAPI.AUTH_PREFIX + token)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting tvdb Token: ${response.code()} ${response.errorBody()?.string()}"))
    }


    /*
     * Trakt Token
     */
    suspend fun getTraktToken(code: String) = safeApiCall(
        call = { refreshTraktToken(code) },
        errorMessage = "Error gettin Trakt Token"
    )

    private suspend fun refreshTraktToken(code: String) : Result<TraktAuthTokenResponse> {
        val response = traktService.getToken(code = code, clientId = TraktAPI.CLIENT_ID, uri = TraktAPI.REDIRECT_URI, type = "authorization_code", secret = TraktAPI.SECRET)

        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting trakt Token: ${response.code()} ${response.errorBody()?.string()}"))
    }


    /*
     * Revoke Trakt Token
     */
    suspend fun logoutTrakt(token: String) = safeApiCall(
        call = { revokeTraktToken(token) },
        errorMessage = "Error revoking Trakt Token"
    )

    private suspend fun revokeTraktToken(token: String) : Result<Boolean> {
        val response = traktService.revokeToken(token = token, clientId = TraktAPI.CLIENT_ID, secret = TraktAPI.SECRET)

        if (response.isSuccessful) {
            return Result.Success(true)
        }
        return Result.Error(IOException("Error logging out: ${response.code()} ${response.errorBody()?.string()}"))
    }
}