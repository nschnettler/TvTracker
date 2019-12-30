package de.schnettler.tvtracker

import android.app.Application
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class TVApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Stetho.initializeWithDefaults(this);
        startKoin{
            androidLogger()
            androidContext(this@TVApplication)
            modules(listOf(databaseModule, repositoryModule, viewModelModule))
        }
    }
}