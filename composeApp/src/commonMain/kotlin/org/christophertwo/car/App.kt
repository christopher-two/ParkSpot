package org.christophertwo.car

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.map
import org.christophertwo.car.core.common.AppTab
import org.christophertwo.car.core.common.RouteHome
import org.christophertwo.car.core.ui.CarLocateTheme
import org.christophertwo.car.di.FeaturesModules
import org.christophertwo.car.feature.navigation.controller.NavigationController
import org.christophertwo.car.feature.navigation.wrappers.RootNavigationWrapper
import org.christophertwo.car.feature.parking.domain.usecase.GetOnboardingStatusUseCase
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App(
    openTicketId: Long? = null,
    onTicketIntentConsumed: (() -> Unit)? = null,
) {
    KoinApplication(
        configuration = koinConfiguration(declaration = { modules(FeaturesModules) }),
        content = {
            val getOnboardingStatusUseCase: GetOnboardingStatusUseCase = koinInject()
            val navigationController: NavigationController = koinInject()
            val isOnboardingCompleted by getOnboardingStatusUseCase()
                .map<Boolean, Boolean?> { it }
                .collectAsStateWithLifecycle(initialValue = null)

            LaunchedEffect(openTicketId, isOnboardingCompleted) {
                val spotId = openTicketId ?: return@LaunchedEffect
                if (isOnboardingCompleted != true) return@LaunchedEffect

                navigationController.switchTabToRoot(AppTab.HISTORY)
                navigationController.navigateInTab(RouteHome.ParkingDetail(spotId))
                onTicketIntentConsumed?.invoke()
            }

            CarLocateTheme {
                RootNavigationWrapper(
                    isLoggedIn = isOnboardingCompleted
                )
            }
        }
    )
}
