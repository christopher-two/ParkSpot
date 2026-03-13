package org.christophertwo.car.feature.parking.domain.usecase

import kotlinx.datetime.LocalDateTime
import org.christophertwo.car.feature.parking.domain.repository.ParkingRepository

class UpdateParkUntilUseCase(
    private val repository: ParkingRepository
) {
    suspend operator fun invoke(id: Long, parkUntil: LocalDateTime?) {
        repository.updateParkUntil(id, parkUntil)
    }
}

