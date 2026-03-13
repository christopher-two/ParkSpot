package org.christophertwo.car.feature.parking.presentation

import org.christophertwo.car.feature.parking.domain.model.ParkingSpot

data class ParkingDetailState(
    val spot: ParkingSpot? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    /** Controla si el picker de hora límite está abierto */
    val showParkUntilPicker: Boolean = false,
    /** Horas seleccionadas en el picker (0..23) */
    val pickerHours: Int = 0,
    /** Minutos seleccionados en el picker (0, 15, 30, 45) */
    val pickerMinutes: Int = 0,
    /** Tiempo restante en segundos (null = sin timer) */
    val remainingSeconds: Long? = null,
)
