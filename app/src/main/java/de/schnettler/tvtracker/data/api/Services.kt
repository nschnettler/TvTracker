package de.schnettler.tvtracker.data.api

import de.schnettler.tvtracker.util.provideOkHttpClient
import de.schnettler.tvtracker.util.provideRetrofit

object RetrofitService {
    val traktService: TraktAPI by lazy {
        provideRetrofit(provideOkHttpClient(TraktAuthInterceptor(), TranslationInterceptor()), TraktAPI.ENDPOINT).create(TraktAPI::class.java)
    }
    val tmdbService: TmdbAPI by lazy {
        provideRetrofit(provideOkHttpClient(TmdbAuthInterceptor()), TmdbAPI.ENDPOINT).create(TmdbAPI::class.java)
    }

    val tvdbService: TvdbAPI by lazy {
        provideRetrofit(provideOkHttpClient(), TvdbAPI.ENDPOINT).create(TvdbAPI::class.java)
    }
}