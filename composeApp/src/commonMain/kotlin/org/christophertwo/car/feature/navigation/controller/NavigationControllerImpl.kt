package org.christophertwo.car.feature.navigation.controller

import androidx.navigation3.runtime.NavKey
import org.christophertwo.car.core.common.AppTab
import org.christophertwo.car.core.common.RouteGlobal
import org.christophertwo.car.feature.navigation.navigator.GlobalNavigator
import org.christophertwo.car.feature.navigation.navigator.HomeNavigator

class NavigationControllerImpl(
    private val globalNavigator: GlobalNavigator,
    private val homeNavigator: HomeNavigator,
) : NavigationController {
    override fun navigateTo(route: RouteGlobal) {
        globalNavigator.navigateTo(route)
    }

    override fun back(): Boolean {
        globalNavigator.back()
        return globalNavigator.rootBackStack.isNotEmpty()
    }

    override fun backTo(route: RouteGlobal) {
        globalNavigator.backTo(route)
    }

    override fun clearAndNavigateTo(route: RouteGlobal) {
        globalNavigator.clearAndNavigateTo(route)
    }

    override fun switchTab(tab: AppTab) {
        homeNavigator.switchTab(tab)
    }

    override fun switchTabToRoot(tab: AppTab) {
        homeNavigator.switchTabToRoot(tab)
    }

    override fun navigateInTab(route: NavKey) {
        homeNavigator.navigateTo(route)
    }

    override fun backInTab(): Boolean {
        return homeNavigator.back()
    }

    override fun getCurrentTab(): AppTab {
        return homeNavigator.currentTab
    }
}