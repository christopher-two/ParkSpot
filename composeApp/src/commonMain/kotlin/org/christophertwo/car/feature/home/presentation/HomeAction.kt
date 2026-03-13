package org.christophertwo.car.feature.home.presentation

import org.christophertwo.car.core.common.AppTab

sealed interface HomeAction {
    data class OnNavigateToTab(val tab: AppTab) : HomeAction
}