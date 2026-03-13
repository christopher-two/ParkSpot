package org.christophertwo.car.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.christophertwo.car.core.common.RouteGlobal
import org.christophertwo.car.feature.navigation.controller.NavigationController
import org.christophertwo.car.feature.parking.domain.usecase.CompleteOnboardingUseCase

class OnboardingViewModel(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val navigationController: NavigationController,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = OnboardingState()
        )

    fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.OnNextPage -> {
                _state.update { it.copy(currentPage = (it.currentPage + 1).coerceAtMost(it.totalPages - 1)) }
            }
            is OnboardingAction.OnPreviousPage -> {
                _state.update { it.copy(currentPage = (it.currentPage - 1).coerceAtLeast(0)) }
            }
            is OnboardingAction.OnPageChanged -> {
                _state.update {
                    it.copy(currentPage = action.page.coerceIn(0, it.totalPages - 1))
                }
            }
            is OnboardingAction.OnComplete -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    completeOnboardingUseCase()
                    navigationController.clearAndNavigateTo(RouteGlobal.Home)
                }
            }
        }
    }

}