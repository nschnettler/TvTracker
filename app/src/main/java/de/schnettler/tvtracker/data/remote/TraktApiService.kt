package de.schnettler.tvtracker.data.remote

import de.schnettler.tvtracker.data.model.ShowRemote
import de.schnettler.tvtracker.data.model.TrendingShowRemote
import retrofit2.Response
import retrofit2.http.GET

interface TraktApiService {
    @GET("shows/trending")
    suspend fun getTrendingShows(): Response<List<TrendingShowRemote>>

    @GET("shows/poupular")
    suspend fun getPopularShows(): Response<List<ShowRemote>>
}
