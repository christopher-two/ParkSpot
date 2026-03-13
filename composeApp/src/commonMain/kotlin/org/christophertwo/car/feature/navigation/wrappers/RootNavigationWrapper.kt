package org.christophertwo.car.feature.navigation.wrappers

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.christophertwo.car.core.common.RouteGlobal
import org.christophertwo.car.feature.navigation.navigator.GlobalNavigator
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun RootNavigationWrapper(
    isLoggedIn: Boolean?
) {
    val globalNavigator: GlobalNavigator = koinInject()
    val rootBackStack = globalNavigator.rootBackStack

    LaunchedEffect(isLoggedIn) {
        val targetRoute = when (isLoggedIn) {
            true -> RouteGlobal.Home
            false -> RouteGlobal.Onboarding
            null -> null
        }

        if (targetRoute != null && rootBackStack.lastOrNull() != targetRoute) {
            globalNavigator.clearAndNavigateTo(targetRoute)
        }
    }

    if (rootBackStack.isEmpty()) return

    NavDisplay(
        backStack = rootBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = { globalNavigator.back() },
        entryProvider = koinEntryProvider(),
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(250)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(250)
            )
        },
        popTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(250)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(250)
            )
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(250)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(250)
            )
        }
    )
}