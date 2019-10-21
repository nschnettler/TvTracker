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

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.schnettler.tvtracker.data.model.ShowRemote
import de.schnettler.tvtracker.data.model.TrendingShowRemote
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.trakt.tv/"

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor {
        val request = it.request().newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("trakt-api-key", "***TRAKT_CLIENT_ID***")
            .addHeader("trakt-api-version", "2")
            .build()
        it.proceed(request)
    }.build()


interface TraktApiService {
    @GET("shows/trending")
    suspend fun getTrendingShows(): Response<List<TrendingShowRemote>>
}


object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    val tractService: TraktApiService by lazy {
        retrofit.create(TraktApiService::class.java)
    }
}