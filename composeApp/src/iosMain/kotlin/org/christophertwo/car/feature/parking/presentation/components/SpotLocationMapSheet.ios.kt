package org.christophertwo.car.feature.parking.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot

@Composable
actual fun SpotLocationMapSheet(
    spot: ParkingSpot,
    onDismiss: () -> Unit,
    modifier: Modifier,
) {
    // iOS stub — replace with native map when available
    Box(
        modifier = modifier.fillMaxWidth().height(320.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("Map not available on iOS yet")
    }
}

