package org.christophertwo.car.feature.car.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.christophertwo.car.feature.car.domain.model.ParkingMarker
import org.christophertwo.car.feature.car.domain.repository.ParkingMarkerRepository

/**
 * Obtiene los marcadores de aparcamiento visibles en el área del mapa.
 */
class GetParkingMarkersUseCase(
    private val repository: ParkingMarkerRepository,
) {
    operator fun invoke(
        latMin: Double,
        latMax: Double,
        lonMin: Double,
        lonMax: Double,
    ): Flow<List<ParkingMarker>> =
        repository.getMarkersInArea(latMin, latMax, lonMin, lonMax)
}

