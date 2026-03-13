package org.christophertwo.car.feature.parking.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot
import com.mapbox.maps.extension.compose.MapEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun SpotLocationMapSheet(
    spot: ParkingSpot,
    onDismiss: () -> Unit,
    modifier: Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(spot.longitude, spot.latitude))
            zoom(16.0)
            pitch(0.0)
            bearing(0.0)
        }
    }

    LaunchedEffect(spot) {
        mapViewportState.easeTo(
            cameraOptions = CameraOptions.Builder()
                .center(Point.fromLngLat(spot.longitude, spot.latitude))
                .zoom(16.0)
                .build()
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "Ubicación del aparcamiento",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${spot.latitude.toBigDecimal().setScale(6, java.math.RoundingMode.HALF_UP)}, " +
                        "${spot.longitude.toBigDecimal().setScale(6, java.math.RoundingMode.HALF_UP)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
            ) {
                MapboxMap(
                    modifier = Modifier.matchParentSize(),
                    mapViewportState = mapViewportState,
                    scaleBar = {},
                    logo = {},
                    attribution = {},
                ) {
                    ViewAnnotation(
                        options = viewAnnotationOptions {
                            geometry(Point.fromLngLat(spot.longitude, spot.latitude))
                            allowOverlap(true)
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
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

                    MapEffect(Unit) { mapView ->
                        mapView.location.apply {
                            locationPuck = createDefault2DPuck(withBearing = false)
                            enabled = true
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

