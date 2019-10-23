package de.schnettler.tvtracker.data.remote

import de.schnettler.tvtracker.data.model.ShowImagesRemote
import de.schnettler.tvtracker.data.model.TrendingShowRemote
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("/3/tv/{tv_id}")
    suspend fun getShowPoster(@Path("tv_id") id: String, @Query("api_key") apiKey: String): Response<ShowImagesRemote>
}
