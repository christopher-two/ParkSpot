package org.christophertwo.car.feature.car.domain.repository

import kotlinx.coroutines.flow.Flow
import org.christophertwo.car.feature.car.domain.model.ParkingMarker

/**
 * Contrato que define cómo se obtienen los marcadores de aparcamiento
 * disponibles en el área visible del mapa.
 */
interface ParkingMarkerRepository {
    /**
     * Devuelve los marcadores dentro del bounding-box indicado.
     * Puede emitir actualizaciones en tiempo real si la fuente es reactiva.
     */
    fun getMarkersInArea(
        latMin: Double,
        latMax: Double,
        lonMin: Double,
        lonMax: Double,
    ): Flow<List<ParkingMarker>>

    /** Devuelve el detalle de un marcador concreto por su [id]. */
    suspend fun getMarkerById(id: String): ParkingMarker?
}

