package org.christophertwo.car.feature.map.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot
import org.christophertwo.car.feature.car.domain.model.UserLocation

@Composable
expect fun FullMap(
    modifier: Modifier = Modifier,
    userLocation: UserLocation,
    parkingSpots: List<ParkingSpot> = emptyList(),
    onMapClick: (latitude: Double, longitude: Double) -> Unit = { _, _ -> },
    onCameraCenterChanged: (latitude: Double, longitude: Double) -> Unit = { _, _ -> },
    onSpotClick: (ParkingSpot) -> Unit = {},
    cameraCenterTrigger: Int = 0,
    zoomLevel: Double = 16.0,
    locationReady: Boolean = false,
    selectedSpotLatitude: Double? = null,
    selectedSpotLongitude: Double? = null,
    isSelectingSpotLocation: Boolean = false,
    focusSpotLatitude: Double? = null,
    focusSpotLongitude: Double? = null,
    focusSpotTrigger: Int = 0,
)