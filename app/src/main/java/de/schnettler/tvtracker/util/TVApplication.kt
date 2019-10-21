package de.schnettler.tvtracker.util

import android.app.Application
import timber.log.Timber

class TVApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}