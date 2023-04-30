package com.example.geotrackfamily
import android.app.Application
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class GeoTrackApplication : Application(), Configuration.Provider {
    override fun getWorkManagerConfiguration() : Configuration {
        return if (BuildConfig.DEBUG) {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build()
        } else {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.ERROR)
                .build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}