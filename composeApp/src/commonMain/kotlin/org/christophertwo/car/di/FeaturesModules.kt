@file:OptIn(org.koin.core.annotation.KoinExperimentalAPI::class)

package org.christophertwo.car.di

import dev.icerock.moko.permissions.PermissionsController
import org.christophertwo.car.core.common.RouteGlobal
import org.christophertwo.car.core.common.RouteHome
import org.christophertwo.car.feature.car.data.repository.LocationRepositoryImpl
import org.christophertwo.car.feature.car.data.repository.ParkingMarkerRepositoryImpl
import org.christophertwo.car.feature.car.domain.repository.LocationRepository
import org.christophertwo.car.feature.car.domain.repository.ParkingMarkerRepository
import org.christophertwo.car.feature.car.domain.usecase.GetCurrentLocationUseCase
import org.christophertwo.car.feature.car.domain.usecase.GetParkingMarkerDetailUseCase
import org.christophertwo.car.feature.car.domain.usecase.GetParkingMarkersUseCase
import org.christophertwo.car.feature.car.presentation.CarRoot
import org.christophertwo.car.feature.car.presentation.CarViewModel
import org.christophertwo.car.feature.history.presentation.HistoryRoot
import org.christophertwo.car.feature.history.presentation.HistoryViewModel
import org.christophertwo.car.feature.parking.presentation.ParkingDetailRoot
import org.christophertwo.car.feature.parking.presentation.ParkingDetailViewModel
import org.christophertwo.car.feature.home.presentation.HomeRoot
import org.christophertwo.car.feature.home.presentation.HomeViewModel
import org.christophertwo.car.feature.navigation.controller.NavigationController
import org.christophertwo.car.feature.navigation.controller.NavigationControllerImpl
import org.christophertwo.car.feature.navigation.navigator.GlobalNavigator
import org.christophertwo.car.feature.navigation.navigator.HomeNavigator
import org.christophertwo.car.feature.onboarding.presentation.OnboardingRoot
import org.christophertwo.car.feature.onboarding.presentation.OnboardingViewModel
import org.christophertwo.car.feature.parking.data.datastore.createDataStore
import org.christophertwo.car.feature.parking.data.local.database.CarLocateDatabase
import org.christophertwo.car.feature.parking.data.local.database.getDatabaseBuilder
import org.christophertwo.car.feature.parking.data.repository.OnboardingRepositoryImpl
import org.christophertwo.car.feature.parking.data.repository.ParkingRepositoryImpl
import org.christophertwo.car.feature.parking.domain.repository.OnboardingRepository
import org.christophertwo.car.feature.parking.domain.repository.ParkingRepository
import org.christophertwo.car.feature.parking.domain.usecase.CompleteOnboardingUseCase
import org.christophertwo.car.feature.parking.domain.usecase.DeleteParkingSpotUseCase
import org.christophertwo.car.feature.parking.domain.usecase.GetAllParkingSpotsUseCase
import org.christophertwo.car.feature.parking.domain.usecase.GetOnboardingStatusUseCase
import org.christophertwo.car.feature.parking.domain.usecase.GetParkingSpotByIdUseCase
import org.christophertwo.car.feature.parking.domain.usecase.MarkSpotInactiveUseCase
import org.christophertwo.car.feature.parking.domain.usecase.UpdateParkUntilUseCase
import org.christophertwo.car.feature.parking.domain.usecase.SaveParkingSpotUseCase
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val FeaturesModules: Module
    get() = module {
        single<GlobalNavigator>()
        single<HomeNavigator>()
        single<NavigationControllerImpl>().bind(NavigationController::class)

        includes(
            ParkingDataModule,
            CarModule,
            HistoryModule,
            HomeModule,
            OnboardingModule,
            AppModule
        )
    }

val ParkingDataModule: Module
    get() = module {
        // Database
        single<CarLocateDatabase> { getDatabaseBuilder().build() }
        single { get<CarLocateDatabase>().parkingSpotDao() }

        // DataStore
        single { createDataStore() }

        // Repositories
        single<ParkingRepositoryImpl>().bind(ParkingRepository::class)
        single<OnboardingRepositoryImpl>().bind(OnboardingRepository::class)

        // Use cases
        factory<SaveParkingSpotUseCase>()
        factory<GetAllParkingSpotsUseCase>()
        factory<GetParkingSpotByIdUseCase>()
        factory<DeleteParkingSpotUseCase>()
        factory<MarkSpotInactiveUseCase>()
        factory<UpdateParkUntilUseCase>()
        factory<GetOnboardingStatusUseCase>()
        factory<CompleteOnboardingUseCase>()
    }

val CarModule: Module
    get() = module {
        // Repositories
        single<LocationRepositoryImpl>().bind(LocationRepository::class)
        single<ParkingMarkerRepositoryImpl>().bind(ParkingMarkerRepository::class)

        // Use cases
        factory<GetCurrentLocationUseCase>()
        factory<GetParkingMarkersUseCase>()
        factory<GetParkingMarkerDetailUseCase>()

        // ViewModel — NavigationController inyectado automáticamente desde FeaturesModules
        viewModel<CarViewModel>()

        navigation<RouteHome.Car> { CarRoot(koinViewModel()) }
    }

val HistoryModule: Module
    get() = module {
        viewModel<HistoryViewModel>()
        viewModel<ParkingDetailViewModel>()

        navigation<RouteHome.History> { HistoryRoot(koinViewModel()) }
        navigation<RouteHome.ParkingDetail> { route ->
            ParkingDetailRoot(id = route.id, viewModel = koinViewModel())
        }
    }

val HomeModule: Module
    get() = module {
        viewModel<HomeViewModel>()

        navigation<RouteGlobal.Home> { HomeRoot(koinViewModel()) }
    }

val OnboardingModule: Module
    get() = module {
        viewModel<OnboardingViewModel>()

        navigation<RouteGlobal.Onboarding> { OnboardingRoot(koinViewModel()) }
    }