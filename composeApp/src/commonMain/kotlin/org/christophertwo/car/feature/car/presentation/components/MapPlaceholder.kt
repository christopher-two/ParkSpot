package org.christophertwo.car.feature.car.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Placeholder del mapa hasta que se integre la librería de mapas real.
 *
 * TODO: reemplazar el contenido interno con el composable del mapa
 *       y conectar [onMarkerClick] al evento de tap de cada marcador.
 */
@Composable
fun MapPlaceholder(
    modifier: Modifier = Modifier,
    onMarkerClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        // TODO: Map library goes here
        Text(
            text = "Mapa",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
        )
    }
}

