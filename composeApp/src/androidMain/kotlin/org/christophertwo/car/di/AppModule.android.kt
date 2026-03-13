package org.christophertwo.car.di

import dev.icerock.moko.permissions.PermissionsController
import org.christophertwo.car.feature.car.data.repository.AndroidLocationRepositoryImpl
import org.christophertwo.car.feature.car.domain.repository.LocationRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val AppModule: Module
    get() = module {
        single<PermissionsController> {
            PermissionsController(applicationContext = androidApplication())
        }
        // Reemplaza el stub de commonMain con GPS real del dispositivo
        single<LocationRepository> {
            AndroidLocationRepositoryImpl(context = androidContext())
        }
    }

