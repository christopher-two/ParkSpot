package org.christophertwo.car.feature.parking.presentation

import kotlinx.datetime.LocalDateTime

sealed interface ParkingDetailAction {
    data object OnBack : ParkingDetailAction
    /** El usuario indica que ya no está aparcado aquí */
    data object OnMarkInactive : ParkingDetailAction
    /** Abre el bottom sheet del mapa de ubicación */
    data object OnShowLocationMap : ParkingDetailAction
    /** Cierra el bottom sheet del mapa de ubicación */
    data object OnDismissLocationMap : ParkingDetailAction
    /** Abre el diálogo para configurar el tiempo límite */
    data object OnShowParkUntilPicker : ParkingDetailAction
    /** Cierra el diálogo del tiempo límite */
    data object OnDismissParkUntilPicker : ParkingDetailAction
    /** Guarda el tiempo límite de aparcamiento */
    data class OnSaveParkUntil(val parkUntil: LocalDateTime) : ParkingDetailAction
    /** Elimina el tiempo límite */
    data object OnClearParkUntil : ParkingDetailAction
    /** Actualiza la hora seleccionada en el picker (horas) */
    data class OnPickerHoursChanged(val hours: Int) : ParkingDetailAction
    /** Actualiza los minutos seleccionados en el picker */
    data class OnPickerMinutesChanged(val minutes: Int) : ParkingDetailAction
}

