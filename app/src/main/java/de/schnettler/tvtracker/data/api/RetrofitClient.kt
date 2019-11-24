package de.schnettler.tvtracker.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val traktHttpClient = OkHttpClient.Builder()
    .addInterceptor {
        val request = it.request().newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader(
                "trakt-api-key",
                Trakt.CLIENT_ID
            )
            .addHeader("trakt-api-version", "2")
            .build()
        it.proceed(request)
    }.addInterceptor {
        var request = it.request()
        val url =
            request.url.newBuilder().addQueryParameter("translations", Locale.getDefault().language)
                .build()
        request = request.newBuilder().url(url).build()
        it.proceed(request)

    }.addInterceptor(HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    })
    .build()


private val tvdbHttpClient = OkHttpClient.Builder()
    .addInterceptor {
        val request = it.request().newBuilder()
            .build()
        it.proceed(request)
    }.addInterceptor(HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    })
    .build()

private val tmdbHttpClient = OkHttpClient.Builder()
    .addInterceptor {
        val request = it.request().newBuilder()
            .addHeader("api_key", TMDb.API_KEY)
            .build()
        it.proceed(request)
    }.addInterceptor(HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }).build()


object RetrofitClient {
    private val traktRetrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(Trakt.ENDPOINT)
        .client(traktHttpClient)
        .build()

    private val tmdbRetrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(TMDb.ENDPOINT)
        .client(tmdbHttpClient)
        .build()

    private val tvdbRetrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(TVDB.ENDPOINT)
        .client(tvdbHttpClient)
        .build()

    val showsNetworkService: Trakt by lazy {
        traktRetrofit.create(Trakt::class.java)
    }

    val imagesNetworkService: TMDb by lazy {
        tmdbRetrofit.create(TMDb::class.java)
    }

    val tvdbNetworkService: TVDB by lazy {
        tvdbRetrofit.create(TVDB::class.java)
    }
}