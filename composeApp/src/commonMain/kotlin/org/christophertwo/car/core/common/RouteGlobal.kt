package org.christophertwo.car.core.common

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Rutas de alto nivel de la aplicación.
 * - Auth: flujo de autenticación
 * - Home: pantalla principal / sección Home
 */
@Serializable
sealed interface RouteGlobal : NavKey {
    @Serializable
    object Onboarding : RouteGlobal

    @Serializable
    object Home : RouteGlobal
}