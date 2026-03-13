package org.christophertwo.car.feature.map.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.viewport
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.christophertwo.car.feature.car.domain.model.UserLocation
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot

@Composable
actual fun FullMap(
    modifier: Modifier,
    userLocation: UserLocation,
    parkingSpots: List<ParkingSpot>,
    onMapClick: (latitude: Double, longitude: Double) -> Unit,
    onSpotClick: (ParkingSpot) -> Unit,
    cameraCenterTrigger: Int,
    zoomLevel: Double,
    locationReady: Boolean,
) {
    if (!locationReady) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(userLocation.longitude, userLocation.latitude))
            zoom(zoomLevel)
            pitch(0.0)
            bearing(0.0)
        }
    }

    // Zoom in/out: animación suave de 300ms
    LaunchedEffect(zoomLevel) {
        mapViewportState.easeTo(
            cameraOptions = CameraOptions.Builder()
                .zoom(zoomLevel)
                .build(),
            animationOptions = MapAnimationOptions.mapAnimationOptions {
                duration(300L)
            }
        )
    }

    // Botón de centrar: flyTo animado
    LaunchedEffect(cameraCenterTrigger) {
        if (cameraCenterTrigger > 0) {
            mapViewportState.flyTo(
                cameraOptions = CameraOptions.Builder()
                    .center(Point.fromLngLat(userLocation.longitude, userLocation.latitude))
                    .zoom(zoomLevel)
                    .bearing(0.0)
                    .pitch(0.0)
                    .build(),
                animationOptions = MapAnimationOptions.mapAnimationOptions {
                    duration(1200L)
                }
            )
        }
    }

    MapboxMap(
        modifier = modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        onMapClickListener = { point ->
            onMapClick(point.latitude(), point.longitude())
            true
        },
        scaleBar = {},
        logo = {},
        attribution = {},
    ) {
        parkingSpots.forEach { spot ->
            ViewAnnotation(
                options = viewAnnotationOptions {
                    geometry(Point.fromLngLat(spot.longitude, spot.latitude))
                    allowOverlap(true)
                    allowOverlapWithPuck(true)
                }
            ) {
                SpotMapMarker(spot = spot, onClick = { onSpotClick(spot) })
            }
        }

        MapEffect(Unit) { mapView ->
            mapView.location.apply {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
            }
            mapView.viewport.transitionTo(
                targetState = mapView.viewport.makeFollowPuckViewportState(
                    options = FollowPuckViewportStateOptions.Builder()
                        .zoom(zoomLevel)
                        .pitch(0.0)
                        .build()
                ),
                transition = mapView.viewport.makeImmediateViewportTransition()
            )
        }
    }
}

/**
 * Marcador del spot en el mapa.
 * Si el spot tiene parkUntil, muestra un countdown en tiempo real.
 */
@Composable
private fun SpotMapMarker(
    spot: ParkingSpot,
    onClick: () -> Unit,
) {
    var remainingSeconds by remember(spot.parkUntil) {
        val initial: Long = spot.parkUntil?.let { until ->
            val now = kotlin.time.Clock.System.now()
            val untilInstant = until.toInstant(TimeZone.currentSystemDefault())
            val diff = untilInstant.epochSeconds - now.epochSeconds
            diff.coerceAtLeast(0L)
        } ?: -1L
        mutableLongStateOf(initial)
    }

    LaunchedEffect(spot.parkUntil) {
        if (spot.parkUntil != null) {
            while (remainingSeconds > 0L) {
                delay(1_000L)
                val now = kotlin.time.Clock.System.now()
                val untilInstant = spot.parkUntil.toInstant(TimeZone.currentSystemDefault())
                remainingSeconds = (untilInstant.epochSeconds - now.epochSeconds).coerceAtLeast(0L)
            }
        }
    }

    val hasTimer = spot.parkUntil != null
    val isExpired = hasTimer && remainingSeconds <= 0L
    val isWarning = hasTimer && remainingSeconds in 1L..300L

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() },
    ) {
        // Badge del timer (encima del pin)
        if (hasTimer) {
            val h = remainingSeconds / 3600L
            val m = (remainingSeconds % 3600L) / 60L
            val s = remainingSeconds % 60L
            val timerText = when {
                isExpired -> "EXP"
                h > 0L -> "${h}h${m.toString().padStart(2, '0')}m"
                else -> "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
            }
            Text(
                text = timerText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .shadow(2.dp, RoundedCornerShape(8.dp))
                    .background(
                        color = when {
                            isExpired -> MaterialTheme.colorScheme.error
                            isWarning -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primary
                        },
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .widthIn(min = 40.dp),
            )
        }

        // Pin circular
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = when {
                        isExpired -> MaterialTheme.colorScheme.error
                        isWarning -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.primary
                    },
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Aparcamiento",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}
