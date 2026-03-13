package org.christophertwo.car.core.common

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Rutas específicas de la sección "Home".
 * - Qr: pantalla de QR
 * - Account: pantalla de Cuenta
 */
@Serializable
sealed interface RouteHome : NavKey {
    @Serializable
    object Car : RouteHome
    @Serializable
    object History : RouteHome
    @Serializable
    data class ParkingDetail(val id: Long) : RouteHome
}