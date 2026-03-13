package org.christophertwo.car.feature.parking.domain.usecase

import org.christophertwo.car.feature.parking.domain.repository.OnboardingRepository

class CompleteOnboardingUseCase(
    private val repository: OnboardingRepository
) {
    suspend operator fun invoke() {
        repository.setOnboardingCompleted()
    }
}

