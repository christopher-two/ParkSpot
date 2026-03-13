package org.christophertwo.car.feature.parking.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot

/**
 * Muestra un BottomSheet con el mapa mostrando la ubicación exacta del spot.
 * La implementación real es platform-specific (Android usa Mapbox).
 */
@Composable
expect fun SpotLocationMapSheet(
    spot: ParkingSpot,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
)

