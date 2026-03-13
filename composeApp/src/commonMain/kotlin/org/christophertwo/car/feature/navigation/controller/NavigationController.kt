package org.christophertwo.car.feature.navigation.controller

import androidx.navigation3.runtime.NavKey
import org.christophertwo.car.core.common.AppTab
import org.christophertwo.car.core.common.RouteGlobal

interface NavigationController {
    /**
     * Navigate to a global route (Auth, Home, Book).
     *
     * @param route The global route to navigate to
     */
    fun navigateTo(route: RouteGlobal)

    /**
     * Go back in the navigation stack.
     *
     * @return true if navigation was successful, false if no more screens to pop
     */
    fun back(): Boolean

    /**
     * Go back to a specific route in the stack.
     *
     * @param route The route to navigate back to
     */
    fun backTo(route: RouteGlobal)

    /**
     * Clear the entire navigation stack and navigate to a new route.
     * Useful for logout scenarios where you want to prevent back navigation.
     *
     * @param route The route to navigate to after clearing the stack
     */
    fun clearAndNavigateTo(route: RouteGlobal)

    // Home Navigation (Tab-based navigation within Home screen)

    /**
     * Switch to a different tab in the Home screen.
     *
     * @param tab The tab to switch to
     */
    fun switchTab(tab: AppTab)

    /**
     * Navigate to a route within the current Home tab.
     *
     * @param route The route to navigate to within the tab
     */
    fun navigateInTab(route: NavKey)

    /**
     * Go back within the current Home tab.
     *
     * @return true if navigation was successful within tab, false otherwise
     */
    fun backInTab(): Boolean

    /**
     * Get the current selected tab.
     *
     * @return The currently selected tab
     */
    fun getCurrentTab(): AppTab
}