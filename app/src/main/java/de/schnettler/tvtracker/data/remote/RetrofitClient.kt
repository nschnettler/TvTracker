/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.schnettler.tvtracker.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TRAKT_BASE_URL = "https://api.trakt.tv/"
private const val TMD_BASE_URL = "https://api.themoviedb.org/"

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
        .baseUrl(TRAKT_BASE_URL)
        .client(traktHttpClient)
        .build()

    private val tmdbRetrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(TMD_BASE_URL)
        .client(tmdbHttpClient)
        .build()

    val showsNetworkService: TraktApiService by lazy {
        traktRetrofit.create(TraktApiService::class.java)
    }

    val imagesNetworkService: TmdbApiService by lazy {
        tmdbRetrofit.create(TmdbApiService::class.java)
    }
}