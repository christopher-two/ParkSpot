package org.christophertwo.car.feature.car.domain.model

/**
 * Representa la ubicación actual del usuario con la distancia
 * calculada al spot seleccionado.
 */
data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val walkingMinutes: Int = 0,
    val distanceMeters: Int = 0,
)

