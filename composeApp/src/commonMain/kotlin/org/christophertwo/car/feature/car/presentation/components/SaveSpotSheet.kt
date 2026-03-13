package org.christophertwo.car.feature.car.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Camera
import compose.icons.fontawesomeicons.solid.MapMarkerAlt
import org.christophertwo.car.core.camera.rememberCameraCaptureLauncher
import org.christophertwo.car.feature.car.presentation.CarAction
import org.christophertwo.car.feature.car.presentation.CarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveSpotSheet(
    state: CarState,
    onAction: (CarAction) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val cameraLauncher = rememberCameraCaptureLauncher { path ->
        path?.let { onAction(CarAction.OnPhotoAdded(it)) }
    }

    ModalBottomSheet(
        onDismissRequest = { onAction(CarAction.OnAddSpotDismissed) },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // — Título —
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.MapMarkerAlt,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Guardar aparcamiento",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
            }

            // — Coordenadas (informativo) —
            val lat = state.userLocation.latitude
            val lon = state.userLocation.longitude
            Text(
                text = "📍 ${(lat * 100000).toLong() / 100000.0}, ${(lon * 100000).toLong() / 100000.0}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // — Nota —
            OutlinedTextField(
                value = state.note,
                onValueChange = { onAction(CarAction.OnNoteChanged(it)) },
                label = { Text("Nota (opcional)") },
                placeholder = { Text("Ej: Planta 2, junto al ascensor…") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                shape = MaterialTheme.shapes.medium,
            )

            // — Sección fotos —
            Text(
                text = "Fotos",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                itemsIndexed(state.photoPaths) { index, path ->
                    PhotoThumbnail(
                        path = path,
                        onRemove = { onAction(CarAction.OnPhotoRemoved(index)) },
                    )
                }
                item {
                    AddPhotoCard(onClick = { cameraLauncher.launch() })
                }
            }

            // — Sección timer rápido —
            Text(
                text = "Tiempo límite (opcional)",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { onAction(CarAction.OnSetParkUntilMinutes(30)) },
                    label = { Text("+30m") },
                )
                AssistChip(
                    onClick = { onAction(CarAction.OnSetParkUntilMinutes(60)) },
                    label = { Text("+1h") },
                )
                AssistChip(
                    onClick = { onAction(CarAction.OnSetParkUntilMinutes(120)) },
                    label = { Text("+2h") },
                )
            }
            state.parkUntil?.let { until ->
                Text(
                    text = "Activo hasta ${until.hour.toString().padStart(2, '0')}:${until.minute.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                OutlinedButton(onClick = { onAction(CarAction.OnClearParkUntil) }) {
                    Text("Quitar timer")
                }
            }

            Spacer(Modifier.height(4.dp))

            // — Botón guardar —
            Button(
                onClick = { onAction(CarAction.OnSave) },
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Guardando…")
                } else {
                    Text("Guardar aparcamiento")
                }
            }
        }
    }
}

@Composable
private fun PhotoThumbnail(
    path: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(80.dp)) {
        AsyncImage(
            model = toImageModel(path),
            contentDescription = "Foto",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(MaterialTheme.shapes.medium),
        )
        // Botón eliminar
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .size(20.dp)
                .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                .clickable { onRemove() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Eliminar foto",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

private fun toImageModel(path: String): String {
    if (path.startsWith("file://") || path.startsWith("content://") || path.startsWith("http")) {
        return path
    }
    return "file://$path"
}

@Composable
private fun AddPhotoCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Camera,
                contentDescription = "Añadir foto",
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Añadir",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
