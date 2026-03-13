package org.christophertwo.car.feature.car.presentation

import org.christophertwo.car.feature.car.domain.model.ParkingMarker
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot

sealed interface CarAction {
    // Mapa
    data class OnMapClicked(val latitude: Double, val longitude: Double) : CarAction
    data class OnSpotClicked(val spot: ParkingSpot) : CarAction
    // Abre el sheet de guardado con la ubicación actual del usuario
    data object OnParkHere : CarAction
    data object OnConfirmSpotSelection : CarAction
    data object OnAddSpotDismissed : CarAction
    
    // Controles de Mapa
    data object OnZoomIn : CarAction
    data object OnZoomOut : CarAction
    data object OnCenterLocation : CarAction
    
    data class OnMarkerSelected(val marker: ParkingMarker) : CarAction
    data object OnMarkerDismissed : CarAction
    // Guardado
    data object OnSave : CarAction
    data object OnDismissSuccess : CarAction
    // Campos del formulario de guardado
    data class OnNoteChanged(val text: String) : CarAction
    data class OnPhotoAdded(val path: String) : CarAction
    data class OnPhotoRemoved(val index: Int) : CarAction
    // Timer del aparcamiento desde el sheet inicial
    data class OnSetParkUntilMinutes(val minutes: Int) : CarAction
    data object OnClearParkUntil : CarAction
}