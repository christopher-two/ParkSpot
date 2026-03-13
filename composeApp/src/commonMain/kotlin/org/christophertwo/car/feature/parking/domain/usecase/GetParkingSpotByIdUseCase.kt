package org.christophertwo.car.feature.parking.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot
import org.christophertwo.car.feature.parking.domain.repository.ParkingRepository

class GetParkingSpotByIdUseCase(
    private val repository: ParkingRepository
) {
    operator fun invoke(id: Long): Flow<ParkingSpot?> = repository.getParkingSpotById(id)
}

