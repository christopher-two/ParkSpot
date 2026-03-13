package org.christophertwo.car.feature.navigation.navigator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey
import org.christophertwo.car.core.common.AppTab
import org.christophertwo.car.core.common.RouteHome

class HomeNavigator {
    var currentTab by mutableStateOf(AppTab.CAR)
        private set

    private val stacks = mapOf(
        AppTab.CAR to mutableStateListOf<NavKey>(RouteHome.Car),
        AppTab.HISTORY to mutableStateListOf<NavKey>(RouteHome.History)
    )

    val currentStack: List<NavKey>
        get() = stacks[currentTab] ?: emptyList()


    fun switchTab(tab: AppTab) {
        currentTab = tab
    }

    fun switchTabToRoot(tab: AppTab) {
        currentTab = tab
        val stack = stacks[tab] ?: return
        if (stack.size > 1) {
            stack.subList(1, stack.size).clear()
        }
    }

    fun navigateTo(route: NavKey) {
        stacks[currentTab]?.add(route)
    }

    fun back(): Boolean {
        val activeStack = stacks[currentTab] ?: return false

        if (activeStack.size > 1) {
            activeStack.removeAt(activeStack.lastIndex)
            return true
        }

        if (currentTab != AppTab.HISTORY) {
            currentTab = AppTab.HISTORY
            return true
        }

        return false
    }
}