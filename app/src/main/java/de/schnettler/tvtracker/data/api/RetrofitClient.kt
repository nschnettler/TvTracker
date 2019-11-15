package de.schnettler.tvtracker.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.schnettler.tvtracker.data.api.tmdb.TmdbApiService
import de.schnettler.tvtracker.data.api.trakt.TraktService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
private val traktHttpClient = OkHttpClient.Builder()
    .addInterceptor {
        val request = it.request().newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("trakt-api-key", "***TRAKT_CLIENT_ID***")
            .addHeader("trakt-api-version", "2")
            .build()
        it.proceed(request)
    }.build()

private val tmdbHttpClient = OkHttpClient.Builder()
    .addInterceptor {
        val request = it.request().newBuilder()
            .addHeader("api_key", "***TMDB_API_KEY***")
            .build()
        it.proceed(request)
    }.build()


object RetrofitClient {
    private val traktRetrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(TraktService.ENDPOINT)
        .client(traktHttpClient)
        .build()

    private val tmdbRetrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(TmdbApiService.ENDPOINT)
        .client(tmdbHttpClient)
        .build()

    val showsNetworkService: TraktService by lazy {
        traktRetrofit.create(TraktService::class.java)
    }

    val imagesNetworkService: TmdbApiService by lazy {
        tmdbRetrofit.create(TmdbApiService::class.java)
    }
}