package org.christophertwo.car.feature.parking.domain.usecase

import org.christophertwo.car.feature.parking.domain.model.ParkingSpot
import org.christophertwo.car.feature.parking.domain.repository.ParkingRepository

class SaveParkingSpotUseCase(
    private val repository: ParkingRepository
) {
    suspend operator fun invoke(spot: ParkingSpot): Long {
        return repository.saveParkingSpot(spot)
    }
}
