package de.schnettler.tvtracker.data.api.trakt

import de.schnettler.tvtracker.data.auth.model.OAuthToken
import de.schnettler.tvtracker.data.show.model.ShowDetailsRemote
import de.schnettler.tvtracker.data.show.model.ShowRemote
import de.schnettler.tvtracker.data.show.model.TrendingShowRemote
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TraktService {
    companion object {
        const val ENDPOINT = "https://api.trakt.tv/"
    }

    @GET("shows/trending")
    suspend fun getTrendingShows(@Query("page") page: Int, @Query("limit") limit: Int): Response<List<TrendingShowRemote>>

    @GET("shows/popular")
    suspend fun getPopularShows(@Query("page") page: Int, @Query("limit") limit: Int): Response<List<ShowRemote>>

    @GET("shows/{tv_id}?extended=full")
    suspend fun getShowSummary(@Path("tv_id") id: Long): Response<ShowDetailsRemote>

    @GET("shows/{tv_id}/related")
    suspend fun getRelatedShows(@Path("tv_id") id: Long): Response<List<ShowRemote>>

    @POST("oauth/token")
    suspend fun getToken(@Query("code") code: String, @Query("client_id") clientId: String, @Query("client_secret") secret: String, @Query("redirect_uri") uri: String, @Query("grant_type") type: String) : Response<OAuthToken>
}