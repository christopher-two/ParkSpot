package org.christophertwo.car.feature.parking.domain.model

import kotlinx.datetime.LocalDateTime

data class ParkingSpot(
    val id: Long = 0L,
    val latitude: Double,
    val longitude: Double,
    val photoPaths: List<String> = emptyList(),
    val savedAt: LocalDateTime,
    val note: String = "",
    /** true mientras este punto es el aparcamiento activo del usuario */
    val isActive: Boolean = true,
    /** Momento hasta el que tiene aparcado el carro (null = sin límite) */
    val parkUntil: LocalDateTime? = null,
)

