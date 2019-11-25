package de.schnettler.tvtracker.data.repository.auth

import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.api.TVDB
import de.schnettler.tvtracker.data.api.Trakt
import de.schnettler.tvtracker.data.db.ShowDao
import de.schnettler.tvtracker.data.models.*
import de.schnettler.tvtracker.util.safeApiCall
import java.io.IOException
import org.json.JSONObject



class AuthDataSourceRemote(private val tvdbService: TVDB, private val traktService: Trakt) {
    /*
     * TVDB Authentication Token
     */
    suspend fun getTvdbRefreshToken(login: Boolean, token: String) = safeApiCall(
        call = { refreshTvdbToken(login, token) },
        errorMessage = "Error getting Token"
    )

    private suspend fun refreshTvdbToken(login: Boolean, token: String): Result<TvdbAuthTokenResponse> {
        val paramObject = JSONObject()
        paramObject.put("apikey", TVDB.API_KEY)
        val response = if (login) tvdbService.login(TvdbLoginData()) else tvdbService.refreshToken(TVDB.AUTH_PREFIX + token)
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
        val response = traktService.getToken(code = code, clientId = Trakt.CLIENT_ID, uri = Trakt.REDIRECT_URI, type = "authorization_code", secret = Trakt.SECRET)

        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting trakt Token: ${response.code()} ${response.errorBody()?.string()}"))
    }
}