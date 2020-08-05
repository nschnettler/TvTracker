package de.schnettler.tvtracker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.schnettler.tvtracker.data.api.*
import de.schnettler.tvtracker.util.provideOkHttpClient
import de.schnettler.tvtracker.util.provideRetrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideTrakt(): TraktAPI = provideRetrofit(
        provideOkHttpClient(TraktAuthInterceptor()), TraktAPI.ENDPOINT
    ).create(TraktAPI::class.java)

    @Provides
    @Singleton
    fun provideTmdb(): TmdbAPI = provideRetrofit(
        provideOkHttpClient(TmdbAuthInterceptor()), TmdbAPI.ENDPOINT
    ).create(TmdbAPI::class.java)

    @Provides
    @Singleton
    fun provideTvdb(): TvdbAPI = provideRetrofit(
        provideOkHttpClient(), TvdbAPI.ENDPOINT
    ).create(TvdbAPI::class.java)
}