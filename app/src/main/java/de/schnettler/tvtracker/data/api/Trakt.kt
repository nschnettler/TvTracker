package de.schnettler.tvtracker.data.api

import de.schnettler.tvtracker.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface Trakt {
    companion object {
        const val ENDPOINT = "https://api.trakt.tv/"
        const val DISCOVER_AMOUNT = 15
        const val BASE_URL = "https://trakt.tv/"
        const val CLIENT_ID = "***TRAKT_CLIENT_ID***"
        const val REDIRECT_URI = "de.schnettler.tvtrack://auth"
        const val SECRET = "***TRAKT_CLIENT_SECRET***"
    }

    /*
     * Trending Shows
     */
    @GET("shows/trending")
    suspend fun getTrendingShows(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<TrendingResponse>>

    /*
     * Popular Shows
     */
    @GET("shows/popular")
    suspend fun getPopularShows(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<PopularResponse>>

    /*
     * Anticipated Shows
     */
    @GET("shows/anticipated")
    suspend fun getAnticipated(): Response<List<AnticipatedResponse>>

    /*
     * Recommended Shows
     */
    @GET("recommendations/shows?ignore_collected=true")
    suspend fun getRecommended(@Header("Authorization") authToken: String): Response<List<RecommendedResponse>>

    /*
     * Show Details
     */
    @GET("shows/{tv_id}?extended=full")
    suspend fun getShowSummary(@Path("tv_id") id: Long): Response<ShowDetailResponse>

    /*
     * Related Shows
     */
    @GET("shows/{tv_id}/related")
    suspend fun getRelatedShows(@Path("tv_id") id: Long): Response<List<ShowResponse>>

    /*
     * Seasons
     */
    @GET("/shows/{tv_id}/seasons?extended=full")
    suspend fun getShowSeasons(@Path("tv_id") id: Long): Response<List<SeasonResponse>>

    /*
     * Episodes
     */
    @GET("shows/{tv_id}/seasons/{season}")
    suspend fun getSeasonEpisodes(
        @Path("tv_id") id: Long,
        @Path("season") seasonNumber: Long
    ): Response<List<EpisodeResponse>>

    /*
     * OAuth
     */
    @POST("oauth/token")
    suspend fun getToken(
        @Query("code") code: String,
        @Query("client_id") clientId: String,
        @Query("client_secret") secret: String,
        @Query("redirect_uri") uri: String,
        @Query("grant_type") type: String
    ): Response<TraktAuthTokenResponse>

    @POST("oauth/revoke")
    suspend fun revokeToken(
        @Query("token") token: String,
        @Query("client_id") clientId: String,
        @Query("client_secret") secret: String
    ): Response<Any>
}