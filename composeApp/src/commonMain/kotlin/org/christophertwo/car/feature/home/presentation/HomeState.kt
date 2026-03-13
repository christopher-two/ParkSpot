package org.christophertwo.car.feature.home.presentation

import org.christophertwo.car.core.common.AppTab

data class HomeState(
    val selectedTab: AppTab = AppTab.CAR
)