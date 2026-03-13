package org.christophertwo.car.feature.onboarding.presentation

sealed interface OnboardingAction {
    data object OnNextPage : OnboardingAction
    data object OnPreviousPage : OnboardingAction
    data class OnPageChanged(val page: Int) : OnboardingAction
    data object OnComplete : OnboardingAction
}