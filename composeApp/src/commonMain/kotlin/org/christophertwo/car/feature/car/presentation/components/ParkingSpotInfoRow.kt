package org.christophertwo.car.feature.car.presentation.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.christophertwo.car.core.common.format
import org.christophertwo.car.feature.car.domain.model.ParkingMarker

/**
 * Muestra el nombre y la información básica (disponibilidad y precio)
 * del spot de aparcamiento seleccionado.
 */
@Composable
fun ParkingSpotInfoRow(
    marker: ParkingMarker,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                text = marker.name,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        supportingContent = {
            val availability =
                if (marker.isFull) "Full" else "Available: ${marker.availableSpots} spots"
            Text(
                text = "$availability • ${marker.pricePerHour.format(2)}/hr",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

