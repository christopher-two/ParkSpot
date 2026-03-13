package org.christophertwo.car.feature.parking.domain.usecase

import org.christophertwo.car.feature.parking.domain.repository.ParkingRepository

class MarkSpotInactiveUseCase(
    private val repository: ParkingRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.markSpotInactive(id)
    }
}

