package org.christophertwo.car.feature.car.domain.repository

import org.christophertwo.car.feature.car.domain.model.UserLocation

/**
 * Contrato para obtener la ubicación actual del usuario.
 */
interface LocationRepository {
    /** Retorna la última ubicación conocida del dispositivo. */
    suspend fun getCurrentLocation(): UserLocation
}

