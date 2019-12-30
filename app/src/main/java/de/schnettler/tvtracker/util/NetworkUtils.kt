package de.schnettler.tvtracker.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException
import de.schnettler.tvtracker.data.Result
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Wrap a suspending API [call] in try/catch. In case an exception is thrown, a [Result.Error] is
 * created based on the [errorMessage].
 */
suspend fun <T : Any> safeApiCall(call: suspend () -> Result<T>, errorMessage: String): Result<T> {
    return try {
        call()
    } catch (e: Exception) {
        // An exception was thrown when calling the API so we're converting this to an IOException
        Result.Error(IOException(errorMessage, e))
    }
}

fun provideOkHttpClient(vararg interceptor: Interceptor) = OkHttpClient().newBuilder()
    .apply { interceptor.forEach { addInterceptor(it) } }
    .build()

fun provideRetrofit(okHttpClient: OkHttpClient, endpoint: String) = Retrofit.Builder()
    .baseUrl(endpoint)
    .client(okHttpClient)
    .addConverterFactory(
        MoshiConverterFactory.create(
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        )
    ).build()