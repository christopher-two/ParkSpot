package org.christophertwo.car.feature.car.domain.usecase

import org.christophertwo.car.feature.car.domain.model.ParkingMarker
import org.christophertwo.car.feature.car.domain.repository.ParkingMarkerRepository

/**
 * Obtiene el detalle de un marcador de aparcamiento por su identificador.
 */
class GetParkingMarkerDetailUseCase(
    private val repository: ParkingMarkerRepository,
) {
    suspend operator fun invoke(id: String): ParkingMarker? =
        repository.getMarkerById(id)
}

