package de.schnettler.tvtracker.data.api

import de.schnettler.tvtracker.BuildConfig
import de.schnettler.tvtracker.data.models.TvdbAuthTokenResponse
import de.schnettler.tvtracker.data.models.TvdbLoginData
import de.schnettler.tvtracker.data.models.CastListResponse
import retrofit2.Response
import retrofit2.http.*

interface TvdbAPI {
    companion object {
        const val ENDPOINT = "https://api.thetvdb.com/"
        const val IMAGE_ENDPOINT = "https://artworks.thetvdb.com/banners/"
        const val IMAGE_ENDPOINT_SHORT ="https://artworks.thetvdb.com"
        const val API_KEY = BuildConfig.TVDB_API_KEY
        const val AUTH_PREFIX = "Bearer "
    }

    @GET("/series/{id}/actors")
    suspend fun getActors(
        @Header("Authorization") token: String,
        @Path("id") showId: Long
    ): CastListResponse

    @POST("/login")
    suspend fun login(@Body body: TvdbLoginData): Response<TvdbAuthTokenResponse>

    @GET("/refresh_token")
    suspend fun refreshToken(
        @Header("Authorization") token: String
    ): Response<TvdbAuthTokenResponse>
}