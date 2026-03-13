package org.christophertwo.car.feature.car.data.repository

import org.christophertwo.car.feature.car.domain.model.UserLocation
import org.christophertwo.car.feature.car.domain.repository.LocationRepository
import kotlin.random.Random

/**
 * Implementación stub de [LocationRepository].
 * TODO: reemplazar con GPS real usando la librería de ubicación multiplataforma.
 */
class LocationRepositoryImpl : LocationRepository {

    override suspend fun getCurrentLocation(): UserLocation {
        // Coordenadas simuladas cerca de San Francisco
        val lat = 37.7749 + (Random.nextDouble() - 0.5) * 0.02
        val lon = -122.4194 + (Random.nextDouble() - 0.5) * 0.02
        return UserLocation(
            latitude = lat,
            longitude = lon,
            walkingMinutes = 8,
            distanceMeters = 450,
        )
    }
}

