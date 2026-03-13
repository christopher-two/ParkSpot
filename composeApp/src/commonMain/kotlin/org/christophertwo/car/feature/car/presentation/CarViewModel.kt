package org.christophertwo.car.feature.car.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import dev.icerock.moko.permissions.location.LOCATION
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.christophertwo.car.core.common.RouteHome
import org.christophertwo.car.feature.car.domain.usecase.GetCurrentLocationUseCase
import org.christophertwo.car.feature.navigation.controller.NavigationController
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot
import org.christophertwo.car.feature.parking.domain.usecase.GetAllParkingSpotsUseCase
import org.christophertwo.car.feature.parking.domain.usecase.SaveParkingSpotUseCase
import kotlin.time.Duration.Companion.minutes

class CarViewModel(
    private val saveParkingSpotUseCase: SaveParkingSpotUseCase,
    private val getAllParkingSpotsUseCase: GetAllParkingSpotsUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val permissionsController: PermissionsController,
    private val navigationController: NavigationController,
) : ViewModel() {

    private val _state = MutableStateFlow(CarState())
    val state = _state
        .onStart {
            try {
                permissionsController.providePermission(Permission.LOCATION)
                permissionsController.providePermission(Permission.COARSE_LOCATION)
                loadLocation()
            } catch (_: DeniedAlwaysException) {
                _state.update { it.copy(permissionsGranted = false) }
            } catch (_: DeniedException) {
                _state.update { it.copy(permissionsGranted = false) }
            }
            viewModelScope.launch {
                getAllParkingSpotsUseCase().collect { spots ->
                    // Solo mostrar en el mapa los spots activos
                    _state.update { it.copy(parkingSpots = spots.filter { s -> s.isActive }) }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CarState()
        )

    private fun loadLocation() {
        viewModelScope.launch {
            val location = getCurrentLocationUseCase()
            _state.update {
                it.copy(
                    userLocation = location,
                    locationReady = true,
                    permissionsGranted = true,
                    latitude = location.latitude,
                    longitude = location.longitude,
                )
            }
        }
    }

    fun onAction(action: CarAction) {
        when (action) {
            is CarAction.OnMapClicked -> { /* ignorado: usamos OnParkHere para guardar */ }
            is CarAction.OnSpotClicked -> {
                navigationController.navigateInTab(RouteHome.ParkingDetail(action.spot.id))
            }
            is CarAction.OnParkHere -> _state.update {
                it.copy(showAddSpotDialog = true, note = "", photoPaths = emptyList(), parkUntil = null)
            }
            is CarAction.OnAddSpotDismissed -> _state.update {
                it.copy(showAddSpotDialog = false, note = "", photoPaths = emptyList(), parkUntil = null)
            }
            is CarAction.OnZoomIn -> _state.update { it.copy(zoomLevel = minOf(it.zoomLevel + 1.0, 20.0)) }
            is CarAction.OnZoomOut -> _state.update { it.copy(zoomLevel = maxOf(it.zoomLevel - 1.0, 0.0)) }
            is CarAction.OnCenterLocation -> _state.update {
                it.copy(cameraCenterTrigger = it.cameraCenterTrigger + 1, zoomLevel = 16.0)
            }
            is CarAction.OnMarkerSelected -> _state.update { it.copy(selectedMarker = action.marker) }
            is CarAction.OnMarkerDismissed -> _state.update { it.copy(selectedMarker = null) }
            is CarAction.OnSave -> saveParking()
            is CarAction.OnDismissSuccess -> _state.update { it.copy(saveSuccess = false) }
            is CarAction.OnNoteChanged -> _state.update { it.copy(note = action.text) }
            is CarAction.OnPhotoAdded -> _state.update { it.copy(photoPaths = it.photoPaths + action.path) }
            is CarAction.OnPhotoRemoved -> _state.update {
                it.copy(photoPaths = it.photoPaths.toMutableList().also { l -> l.removeAt(action.index) })
            }
            is CarAction.OnSetParkUntilMinutes -> {
                val parkUntil = kotlin.time.Clock.System.now()
                    .plus(action.minutes.minutes)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                _state.update { it.copy(parkUntil = parkUntil) }
            }
            is CarAction.OnClearParkUntil -> _state.update { it.copy(parkUntil = null) }
        }
    }

    private fun saveParking() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            try {
                val now = kotlin.time.Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                val spot = ParkingSpot(
                    latitude = _state.value.userLocation.latitude,
                    longitude = _state.value.userLocation.longitude,
                    photoPaths = _state.value.photoPaths,
                    savedAt = now,
                    note = _state.value.note,
                    isActive = true,
                    parkUntil = _state.value.parkUntil,
                )
                saveParkingSpotUseCase(spot)
                _state.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = true,
                        showAddSpotDialog = false,
                        selectedMarker = null,
                        note = "",
                        photoPaths = emptyList(),
                        parkUntil = null,
                    )
                }
                loadLocation()
            } catch (e: Exception) {
                _state.update {
                    it.copy(isSaving = false, error = "Error al guardar: ${e.message}")
                }
            }
        }
    }
}
