package de.schnettler.tvtracker.data.remote

import de.schnettler.tvtracker.data.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TraktApiService {
    @GET("shows/trending")
    suspend fun getTrendingShows(@Query("page") page: Int, @Query("limit") limit: Int): Response<List<TrendingShowRemote>>

    @GET("shows/popular")
    suspend fun getPopularShows(@Query("page") page: Int, @Query("limit") limit: Int): Response<List<ShowRemote>>

    @GET("shows/{tv_id}?extended=full")
    suspend fun getShowSummary(@Path("tv_id") id: Long): Response<ShowDetailsRemote>

    @GET("shows/{tv_id}/people")
    suspend fun getShowCast(@Path("tv_id") id: Long): Response<ShowCastListRemote>

    @POST("oauth/token")
    suspend fun getToken(@Query("code") code: String, @Query("client_id") clientId: String, @Query("client_secret") secret: String, @Query("redirect_uri") uri: String, @Query("grant_type") type: String) : Response<OAuthToken>
}