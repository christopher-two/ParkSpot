package org.christophertwo.car.feature.car.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.MapMarkerAlt
import compose.icons.fontawesomeicons.solid.Minus

@Composable
fun MapOverlayControls(
    modifier: Modifier = Modifier,
    onZoomInClick: () -> Unit,
    onZoomOutClick: () -> Unit,
    onCenterClick: () -> Unit,
    onParkHereClick: () -> Unit,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
        horizontalAlignment = Alignment.End,
    ) {
        // Botones de zoom + centrar a la derecha
        IconButton(
            onClick = onCenterClick,
            modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surface),
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "Centrar ubicación")
        }
        IconButton(
            onClick = onZoomInClick,
            modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surface),
        ) {
            Icon(Icons.Default.Add, contentDescription = "Acercar")
        }
        IconButton(
            onClick = onZoomOutClick,
            modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surface),
        ) {
            Icon(FontAwesomeIcons.Solid.Minus, contentDescription = "Alejar", modifier = Modifier.size(24.dp))
        }

        // FAB principal — Aparcar aquí
        ExtendedFloatingActionButton(
            onClick = onParkHereClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            icon = {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.MapMarkerAlt,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            },
            text = { Text("Aparcar aquí") },
        )
    }
}
