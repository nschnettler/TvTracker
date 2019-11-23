package de.schnettler.tvtracker.data.api

import de.schnettler.tvtracker.data.models.PersonImageResponse
import de.schnettler.tvtracker.data.models.ShowImageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDb {
    companion object {
        const val ENDPOINT = "https://api.themoviedb.org/"
        const val API_KEY = "***TMDB_API_KEY***"
    }

    @GET("/3/tv/{tv_id}")
    suspend fun getShowPoster(
        @Path("tv_id") id: String,
        @Query("api_key") apiKey: String
    ): Response<ShowImageResponse>

    @GET("/3/person/{person_id}")
    suspend fun getPersonImage(
        @Path("person_id") id: String,
        @Query("api_key") apiKey: String
    ): Response<PersonImageResponse>
}