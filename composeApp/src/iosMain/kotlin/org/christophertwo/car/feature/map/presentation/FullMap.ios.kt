package org.christophertwo.car.feature.map.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot
import org.christophertwo.car.feature.car.domain.model.UserLocation

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
    selectedSpotLatitude: Double?,
    selectedSpotLongitude: Double?,
    isSelectingSpotLocation: Boolean,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Map is not supported on iOS yet.")
    }
}
