package org.christophertwo.car.feature.car.domain.model

/**
 * Representa un marcador de aparcamiento visible en el mapa.
 * Modelo de dominio puro — sin dependencias de frameworks.
 */
data class ParkingMarker(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val availableSpots: Int,
    val pricePerHour: Double,
    val isFull: Boolean = availableSpots == 0,
)

