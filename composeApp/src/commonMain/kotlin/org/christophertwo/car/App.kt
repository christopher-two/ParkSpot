package org.christophertwo.car

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.map
import org.christophertwo.car.core.ui.CarLocateTheme
import org.christophertwo.car.di.FeaturesModules
import org.christophertwo.car.feature.navigation.wrappers.RootNavigationWrapper
import org.christophertwo.car.feature.parking.domain.usecase.GetOnboardingStatusUseCase
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    KoinApplication(
        configuration = koinConfiguration(declaration = { modules(FeaturesModules) }),
        content = {
            val getOnboardingStatusUseCase: GetOnboardingStatusUseCase = koinInject()
            val isOnboardingCompleted by getOnboardingStatusUseCase()
                .map<Boolean, Boolean?> { it }
                .collectAsStateWithLifecycle(initialValue = null)

            CarLocateTheme {
                RootNavigationWrapper(
                    isLoggedIn = isOnboardingCompleted
                )
            }
        }
    )
}
