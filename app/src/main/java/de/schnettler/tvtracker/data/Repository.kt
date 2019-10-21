package de.schnettler.tvtracker.data

import de.schnettler.tvtracker.data.model.TrendingShowRemote
import de.schnettler.tvtracker.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class Repository {
    private var client = RetrofitClient.tractService

    suspend fun getTrendingShows(): Response<List<TrendingShowRemote>>  = withContext(Dispatchers.IO) {
        client.getTrendingShows()
    }

}