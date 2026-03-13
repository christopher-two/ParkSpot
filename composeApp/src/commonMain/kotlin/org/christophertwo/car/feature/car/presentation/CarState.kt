package org.christophertwo.car.feature.car.presentation

import kotlinx.datetime.LocalDateTime
import org.christophertwo.car.feature.car.domain.model.ParkingMarker
import org.christophertwo.car.feature.car.domain.model.UserLocation
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot

data class CarState(
    // Ubicación del usuario
    val userLocation: UserLocation = UserLocation(latitude = 0.0, longitude = 0.0),
    // true cuando ya se recibió una ubicación GPS real del dispositivo
    val locationReady: Boolean = false,
    // Marcadores del mapa legacy
    val markers: List<ParkingMarker> = emptyList(),
    // Marcador seleccionado (activa el bottom sheet legacy)
    val selectedMarker: ParkingMarker? = null,

    val parkingSpots: List<ParkingSpot> = emptyList(),

    // Controles de Cámara / Mapbox Viewport
    val zoomLevel: Double = 16.0,
    val cameraCenterTrigger: Int = 0,

    // Nuevos campos para ParkingSpots locales
    val showAddSpotDialog: Boolean = false,
    
    // Legacy — se mantiene hasta refactorizar el guardado completo
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val photoPaths: List<String> = emptyList(),
    val note: String = "",
    // Hora límite opcional configurada desde el sheet inicial
    val parkUntil: LocalDateTime? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
    val permissionsGranted: Boolean = false
)