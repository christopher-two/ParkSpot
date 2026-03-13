package org.christophertwo.car.feature.car.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.christophertwo.car.core.ui.CarLocateTheme
import org.christophertwo.car.feature.car.domain.model.UserLocation
import org.christophertwo.car.feature.car.presentation.components.MapOverlayControls
import org.christophertwo.car.feature.car.presentation.components.SaveSpotSheet
import org.christophertwo.car.feature.map.presentation.FullMap

@Composable
fun CarRoot(viewModel: CarViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CarScreen(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarScreen(
    state: CarState,
    onAction: (CarAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            snackbarHostState.showSnackbar("¡Aparcamiento guardado!")
            onAction(CarAction.OnDismissSuccess)
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Capa 1 — Mapa
        FullMap(
            modifier = Modifier.fillMaxSize(),
            userLocation = state.userLocation,
            parkingSpots = state.parkingSpots,
            onMapClick = { lat, lon -> onAction(CarAction.OnMapClicked(lat, lon)) },
            onSpotClick = { spot -> onAction(CarAction.OnSpotClicked(spot)) },
            cameraCenterTrigger = state.cameraCenterTrigger,
            zoomLevel = state.zoomLevel,
            locationReady = state.locationReady,
            selectedSpotLatitude = state.selectedSpotLatitude,
            selectedSpotLongitude = state.selectedSpotLongitude,
            isSelectingSpotLocation = state.isSelectingSpotLocation,
        )

        // Capa 2 — Controles superpuestos
        MapOverlayControls(
            modifier = Modifier.fillMaxSize(),
            onZoomInClick = { onAction(CarAction.OnZoomIn) },
            onZoomOutClick = { onAction(CarAction.OnZoomOut) },
            onCenterClick = { onAction(CarAction.OnCenterLocation) },
            onParkHereClick = {
                if (state.isSelectingSpotLocation) onAction(CarAction.OnConfirmSpotSelection)
                else onAction(CarAction.OnParkHere)
            },
            isSelectingSpotLocation = state.isSelectingSpotLocation,
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    // BottomSheet de guardado
    if (state.showAddSpotDialog) {
        SaveSpotSheet(state = state, onAction = onAction)
    }
}

@Preview
@Composable
private fun CarScreenPreview() {
    CarLocateTheme {
        CarScreen(
            state = CarState(
                userLocation = UserLocation(
                    latitude = 37.7749,
                    longitude = -122.4194,
                    walkingMinutes = 8,
                    distanceMeters = 450,
                ),
            ),
            onAction = {},
        )
    }
}
