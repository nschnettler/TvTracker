package de.schnettler.tvtracker.data.api.tmdb

import de.schnettler.tvtracker.data.show.model.PersonImageRemote
import de.schnettler.tvtracker.data.show.model.ShowImagesRemote
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    companion object {
        const val ENDPOINT = "https://api.themoviedb.org/"
        const val API_KEY = "***TMDB_API_KEY***"
    }

    @GET("/3/tv/{tv_id}")
    suspend fun getShowPoster(@Path("tv_id") id: String, @Query("api_key") apiKey: String): Response<ShowImagesRemote>

    @GET("/3/person/{person_id}")
    suspend fun getPersonImage(@Path("person_id") id: String, @Query("api_key") apiKey: String): Response<PersonImageRemote>
}
