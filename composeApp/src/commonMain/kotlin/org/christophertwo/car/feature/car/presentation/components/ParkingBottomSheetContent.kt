package org.christophertwo.car.feature.car.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.christophertwo.car.feature.car.domain.model.ParkingMarker
import org.christophertwo.car.feature.car.domain.model.UserLocation

/**
 * Contenido del BottomSheet que aparece al seleccionar un marcador del mapa.
 * Compone [UserCarInfoRow], [ParkingSpotInfoRow] y [ParkHereButton].
 */
@Composable
fun ParkingBottomSheetContent(
    marker: ParkingMarker,
    userLocation: UserLocation,
    isSaving: Boolean,
    onRouteClick: () -> Unit,
    onParkHereClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        UserCarInfoRow(
            userLocation = userLocation,
            onRouteClick = onRouteClick,
        )

        ParkingSpotInfoRow(
            marker = marker,
        )

        ParkHereButton(
            onClick = onParkHereClick,
            isSaving = isSaving,
        )
    }
}

