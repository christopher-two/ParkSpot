package org.christophertwo.androidapp

import android.app.Application
import org.christophertwo.car.di.FeaturesModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApp : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApp.applicationContext)
            androidLogger(Level.ERROR)
            modules(FeaturesModules)
        }
    }
}