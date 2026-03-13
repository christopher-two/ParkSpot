package org.christophertwo.car.feature.parking.domain.usecase

import org.christophertwo.car.feature.parking.domain.repository.ParkingRepository

class DeleteParkingSpotUseCase(
    private val repository: ParkingRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteParkingSpot(id)
    }
}

