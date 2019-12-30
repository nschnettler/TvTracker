package de.schnettler.tvtracker.data.api

import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

class TraktAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("trakt-api-key", TraktAPI.CLIENT_ID)
            .addHeader("trakt-api-version", "2")
            .build()
        return chain.proceed(request)
    }
}

class TmdbAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("api_key", TmdbAPI.API_KEY)
            .build()
        return chain.proceed(request)
    }
}

class TranslationInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var req = chain.request()
        val url = req.url.newBuilder().addQueryParameter("translations", Locale.getDefault().language).build()
        req = req.newBuilder().url(url).build()
        return chain.proceed(req)
    }
}