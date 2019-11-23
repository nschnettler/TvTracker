package de.schnettler.tvtracker.data.api

import de.schnettler.tvtracker.data.models.OAuthToken
import de.schnettler.tvtracker.data.models.AnticipatedResponse
import de.schnettler.tvtracker.data.models.ShowDetailResponse
import de.schnettler.tvtracker.data.models.ShowResponse
import de.schnettler.tvtracker.data.models.TrendingResponse
import de.schnettler.tvtracker.data.models.EpisodeResponse
import de.schnettler.tvtracker.data.models.SeasonResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Trakt {
    companion object {
        const val ENDPOINT = "https://api.trakt.tv/"
        const val DISCOVER_AMOUNT = 15
    }

    @GET("shows/trending")
    suspend fun getTrendingShows(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<TrendingResponse>>

    @GET("shows/popular")
    suspend fun getPopularShows(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<ShowResponse>>

    @GET("shows/anticipated")
    suspend fun getAnticipated(): Response<List<AnticipatedResponse>>

    @GET("shows/{tv_id}?extended=full")
    suspend fun getShowSummary(@Path("tv_id") id: Long): Response<ShowDetailResponse>

    @GET("shows/{tv_id}/related")
    suspend fun getRelatedShows(@Path("tv_id") id: Long): Response<List<ShowResponse>>

    @GET("/shows/{tv_id}/seasons?extended=full")
    suspend fun getShowSeasons(@Path("tv_id") id: Long): Response<List<SeasonResponse>>

    @GET("shows/{tv_id}/seasons/{season}")
    suspend fun getSeasonEpisodes(
        @Path("tv_id") id: Long,
        @Path("season") seasonNumber: Long,
        @Query("translations") language: String
    ): Response<List<EpisodeResponse>>

    @POST("oauth/token")
    suspend fun getToken(
        @Query("code") code: String,
        @Query("client_id") clientId: String,
        @Query("client_secret") secret: String,
        @Query("redirect_uri") uri: String,
        @Query("grant_type") type: String
    ): Response<OAuthToken>
}