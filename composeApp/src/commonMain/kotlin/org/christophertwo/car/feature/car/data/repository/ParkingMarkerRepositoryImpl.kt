package org.christophertwo.car.feature.car.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.christophertwo.car.feature.car.domain.model.ParkingMarker
import org.christophertwo.car.feature.car.domain.repository.ParkingMarkerRepository

/**
 * Implementación stub de [ParkingMarkerRepository].
 * TODO: reemplazar con llamada real a API/base de datos cuando se integre la librería de mapas.
 */
class ParkingMarkerRepositoryImpl : ParkingMarkerRepository {

    // Datos de ejemplo — se reemplazarán con datos reales
    private val stubMarkers = listOf(
        ParkingMarker(
            id = "1",
            latitude = 37.7980,
            longitude = -122.4060,
            name = "Montgomery St. Garage",
            availableSpots = 24,
            pricePerHour = 4.50,
        ),
        ParkingMarker(
            id = "2",
            latitude = 37.7850,
            longitude = -122.4194,
            name = "Civic Center Parking",
            availableSpots = 0,
            pricePerHour = 3.00,
        ),
    )

    override fun getMarkersInArea(
        latMin: Double,
        latMax: Double,
        lonMin: Double,
        lonMax: Double,
    ): Flow<List<ParkingMarker>> = flowOf(
        stubMarkers.filter { m ->
            m.latitude in latMin..latMax && m.longitude in lonMin..lonMax
        }
    )

    override suspend fun getMarkerById(id: String): ParkingMarker? =
        stubMarkers.find { it.id == id }
}

