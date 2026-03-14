package org.christophertwo.car.feature.parking.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot

interface ParkingRepository {
    suspend fun saveParkingSpot(spot: ParkingSpot): Long
    fun getAllParkingSpots(): Flow<List<ParkingSpot>>
    fun getParkingSpotById(id: Long): Flow<ParkingSpot?>
    suspend fun deleteParkingSpot(id: Long)
    /** Marca el spot como inactivo: deja de mostrarse en el mapa */
    suspend fun markSpotInactive(id: Long)
    /** Actualiza el momento hasta el que el usuario va a estar aparcado */
    suspend fun updateParkUntil(id: Long, parkUntil: LocalDateTime?)
    /** Desactiva todos los spots activos (al guardar uno nuevo) */
    suspend fun deactivateAllSpots()
}
