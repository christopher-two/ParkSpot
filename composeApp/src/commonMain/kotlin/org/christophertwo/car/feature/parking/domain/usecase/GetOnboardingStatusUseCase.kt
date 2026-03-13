package org.christophertwo.car.feature.parking.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.christophertwo.car.feature.parking.domain.repository.OnboardingRepository

class GetOnboardingStatusUseCase(
    private val repository: OnboardingRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.isOnboardingCompleted()
}

