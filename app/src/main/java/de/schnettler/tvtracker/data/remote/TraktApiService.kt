package de.schnettler.tvtracker.data.remote

import de.schnettler.tvtracker.data.model.ShowDetails
import de.schnettler.tvtracker.data.model.ShowRemote
import de.schnettler.tvtracker.data.model.TrendingShowRemote
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TraktApiService {
    @GET("shows/trending")
    suspend fun getTrendingShows(@Query("page") page: Int, @Query("limit") limit: Int): Response<List<TrendingShowRemote>>

    @GET("shows/popular")
    suspend fun getPopularShows(@Query("page") page: Int, @Query("limit") limit: Int): Response<List<ShowRemote>>

    @GET("shows/{tv_id}?extended=full")
    suspend fun getShowSummary(@Path("tv_id") id: Long): Response<ShowDetails>
}
