package org.christophertwo.car.feature.car.domain.usecase

import org.christophertwo.car.feature.car.domain.model.UserLocation
import org.christophertwo.car.feature.car.domain.repository.LocationRepository

/**
 * Obtiene la ubicación actual del usuario.
 */
class GetCurrentLocationUseCase(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(): UserLocation =
        repository.getCurrentLocation()
}

