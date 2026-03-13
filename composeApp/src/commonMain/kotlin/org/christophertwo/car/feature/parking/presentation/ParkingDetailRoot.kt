package org.christophertwo.car.feature.parking.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.regular.StickyNote
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.Calendar
import compose.icons.fontawesomeicons.solid.Clock
import compose.icons.fontawesomeicons.solid.MapMarkerAlt
import compose.icons.fontawesomeicons.solid.TimesCircle
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.christophertwo.car.core.common.format
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot
import org.christophertwo.car.feature.parking.presentation.components.SpotLocationMapSheet

@Composable
fun ParkingDetailRoot(
    id: Long,
    viewModel: ParkingDetailViewModel,
) {
    LaunchedEffect(id) { viewModel.loadSpot(id) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    ParkingDetailScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingDetailScreen(
    state: ParkingDetailState,
    onAction: (ParkingDetailAction) -> Unit,
) {
    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.spot == null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Aparcamiento no encontrado",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        else -> {
            ParkingDetailContent(
                spot = state.spot,
                remainingSeconds = state.remainingSeconds,
                onAction = onAction,
            )

            // BottomSheet con el mapa de ubicación
            if (state.showLocationMap) {
                SpotLocationMapSheet(
                    spot = state.spot,
                    onDismiss = { onAction(ParkingDetailAction.OnDismissLocationMap) },
                )
            }

            // Diálogo picker de hora límite
            if (state.showParkUntilPicker) {
                ParkUntilPickerDialog(
                    initialHours = state.pickerHours,
                    initialMinutes = state.pickerMinutes,
                    onHoursChanged = { onAction(ParkingDetailAction.OnPickerHoursChanged(it)) },
                    onMinutesChanged = { onAction(ParkingDetailAction.OnPickerMinutesChanged(it)) },
                    onConfirm = {
                        val now = kotlin.time.Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                        val parkUntil = LocalDateTime(
                            now.year,
                            now.month,
                            now.day,
                            state.pickerHours.coerceIn(0, 23),
                            state.pickerMinutes.coerceIn(0, 59),
                            0,
                            0,
                        )
                        onAction(ParkingDetailAction.OnSaveParkUntil(parkUntil))
                    },
                    onDismiss = { onAction(ParkingDetailAction.OnDismissParkUntilPicker) },
                )
            }
        }
    }
}

@Composable
private fun ParkingDetailContent(
    spot: ParkingSpot,
    remainingSeconds: Long?,
    onAction: (ParkingDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Timer card (si hay parkUntil activo)
        if (remainingSeconds != null || spot.parkUntil != null) {
            item {
                TimerCard(
                    spot = spot,
                    remainingSeconds = remainingSeconds,
                    onSetTimer = { onAction(ParkingDetailAction.OnShowParkUntilPicker) },
                    onClearTimer = { onAction(ParkingDetailAction.OnClearParkUntil) },
                )
            }
        }

        // Coordenadas — clickeable para abrir mapa
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(ParkingDetailAction.OnShowLocationMap) },
                shape = MaterialTheme.shapes.large,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.MapMarkerAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Ubicación exacta",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ver en mapa",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Latitud: ${spot.latitude.format(7)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Longitud: ${spot.longitude.format(7)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Toca para ver en el mapa →",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        // Timer — botón para configurar si no hay uno
        if (remainingSeconds == null && spot.parkUntil == null) {
            item {
                OutlinedButton(
                    onClick = { onAction(ParkingDetailAction.OnShowParkUntilPicker) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Clock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Configurar tiempo límite")
                }
            }
        }

        // Fecha y hora
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Calendar,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.secondary,
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Fecha",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${spot.savedAt.day.toString().padStart(2, '0')}/" +
                                    "${spot.savedAt.month.number.toString().padStart(2, '0')}/" +
                                    "${spot.savedAt.year}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Clock,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.secondary,
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Hora",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${spot.savedAt.hour.toString().padStart(2, '0')}:" +
                                    spot.savedAt.minute.toString().padStart(2, '0'),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }

        // Nota
        if (spot.note.isNotBlank()) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = FontAwesomeIcons.Regular.StickyNote,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.tertiary,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Nota", style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(text = spot.note, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        // Fotos
        if (spot.photoPaths.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = "Fotos (${spot.photoPaths.size})",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(spot.photoPaths) { _, path ->
                            OutlinedCard(
                                modifier = Modifier.size(120.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                AsyncImage(
                                    model = toImageModel(path),
                                    contentDescription = "Foto del aparcamiento",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(MaterialTheme.shapes.medium),
                                )
                            }
                        }
                    }
                }
            }
        }

        // Botón "Ya no estoy aquí"
        if (spot.isActive) {
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { onAction(ParkingDetailAction.OnMarkInactive) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.TimesCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Ya no estoy aquí")
                }
            }
        }
    }
}

@Composable
private fun TimerCard(
    spot: ParkingSpot,
    remainingSeconds: Long?,
    onSetTimer: () -> Unit,
    onClearTimer: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Clock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (remainingSeconds != null && remainingSeconds <= 300L)
                            MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Tiempo de aparcamiento",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                IconButton(onClick = onClearTimer, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.TimesCircle,
                        contentDescription = "Eliminar timer",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            if (remainingSeconds != null) {
                val h = remainingSeconds / 3600
                val m = (remainingSeconds % 3600) / 60
                val s = remainingSeconds % 60
                val isExpired = remainingSeconds <= 0L
                val isWarning = remainingSeconds in 1..300

                Text(
                    text = if (isExpired) "¡Tiempo expirado!" else
                        "${h.toString().padStart(2, '0')}:${
                            m.toString().padStart(2, '0')
                        }:${s.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.displaySmall,
                    color = when {
                        isExpired -> MaterialTheme.colorScheme.error
                        isWarning -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )
            }

            spot.parkUntil?.let { until ->
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Hasta las ${
                        until.hour.toString().padStart(2, '0')
                    }:${until.minute.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onSetTimer) {
                Text("Cambiar hora límite")
            }
        }
    }
}

@Composable
private fun ParkUntilPickerDialog(
    initialHours: Int,
    initialMinutes: Int,
    onHoursChanged: (Int) -> Unit,
    onMinutesChanged: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Hasta qué hora?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Selecciona la hora hasta la que estarás aparcado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // Horas
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Horas", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(
                                onClick = { onHoursChanged((initialHours - 1).coerceIn(0, 23)) },
                                contentPadding = PaddingValues(4.dp),
                            ) { Text("−") }
                            Text(
                                text = initialHours.toString().padStart(2, '0'),
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.width(48.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                            TextButton(
                                onClick = { onHoursChanged((initialHours + 1).coerceIn(0, 23)) },
                                contentPadding = PaddingValues(4.dp),
                            ) { Text("+") }
                        }
                    }

                    Text(":", style = MaterialTheme.typography.headlineMedium)

                    // Minutos (0, 15, 30, 45)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Minutos", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        val minuteOptions = listOf(0, 15, 30, 45)
                        val currentIdx = minuteOptions.indexOf(initialMinutes).coerceAtLeast(0)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(
                                onClick = {
                                    val prev = if (currentIdx == 0) minuteOptions.last()
                                    else minuteOptions[currentIdx - 1]
                                    onMinutesChanged(prev)
                                },
                                contentPadding = PaddingValues(4.dp),
                            ) { Text("−") }
                            Text(
                                text = initialMinutes.toString().padStart(2, '0'),
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.width(48.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                            TextButton(
                                onClick = {
                                    val next =
                                        if (currentIdx == minuteOptions.lastIndex) minuteOptions.first()
                                        else minuteOptions[currentIdx + 1]
                                    onMinutesChanged(next)
                                },
                                contentPadding = PaddingValues(4.dp),
                            ) { Text("+") }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}

private fun toImageModel(path: String): String {
    if (path.startsWith("file://") || path.startsWith("content://") || path.startsWith("http")) {
        return path
    }
    return "file://$path"
}
