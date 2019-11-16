package de.schnettler.tvtracker.data.api.tvdb

import de.schnettler.tvtracker.data.auth.model.TvdbAuthTokenResponse
import de.schnettler.tvtracker.data.auth.model.TvdbLoginData
import de.schnettler.tvtracker.data.show.model.cast.CastListRemote
import retrofit2.Response
import retrofit2.http.*

interface TvdbService {
    companion object {
        const val ENDPOINT = "https://api.thetvdb.com/"
        const val IMAGE_ENDPOINT ="https://artworks.thetvdb.com/banners/"
        const val API_KEY = "***TVDB_API_KEY***"
        const val AUTH_PREFIX = "Bearer "
    }

    @GET("/series/{id}/actors")
    suspend fun getActors(@Header("Authorization") token: String, @Path("id") showId: Long): Response<CastListRemote>

    @POST("/login")
    suspend fun login(@Body body: TvdbLoginData): Response<TvdbAuthTokenResponse>

    @GET("/refresh_token")
    suspend fun refreshToken(@Header("Authorization") token: String): Response<TvdbAuthTokenResponse>
}
