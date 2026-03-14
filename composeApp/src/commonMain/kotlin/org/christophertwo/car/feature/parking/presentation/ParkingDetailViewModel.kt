package org.christophertwo.car.feature.parking.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.christophertwo.car.core.common.AppTab
import org.christophertwo.car.core.permissions.PermissionHandler
import org.christophertwo.car.feature.map.presentation.MapFocusCoordinator
import org.christophertwo.car.feature.navigation.controller.NavigationController
import org.christophertwo.car.feature.parking.domain.usecase.GetParkingSpotByIdUseCase
import org.christophertwo.car.feature.parking.domain.usecase.MarkSpotInactiveUseCase
import org.christophertwo.car.feature.parking.domain.usecase.UpdateParkUntilUseCase
import org.christophertwo.car.feature.parking.notification.NoOpParkingNotificationService
import org.christophertwo.car.feature.parking.notification.ParkingNotificationService

class ParkingDetailViewModel(
    private val getParkingSpotByIdUseCase: GetParkingSpotByIdUseCase,
    private val markSpotInactiveUseCase: MarkSpotInactiveUseCase,
    private val updateParkUntilUseCase: UpdateParkUntilUseCase,
    private val navigationController: NavigationController,
) : ViewModel() {

    private val _state = MutableStateFlow(ParkingDetailState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ParkingDetailState()
        )

    private var timerJob: Job? = null
    private var notificationService: ParkingNotificationService = NoOpParkingNotificationService
    private var permissionHandler: PermissionHandler? = null

    fun bindPlatformServices(
        notificationService: ParkingNotificationService,
        permissionHandler: PermissionHandler,
    ) {
        this.notificationService = notificationService
        this.permissionHandler = permissionHandler
    }

    fun loadSpot(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getParkingSpotByIdUseCase(id).collect { spot ->
                _state.update { it.copy(spot = spot, isLoading = false) }
                // Reiniciar timer si hay parkUntil
                restartTimer(spot?.parkUntil)
            }
        }
    }

    private fun restartTimer(parkUntil: kotlinx.datetime.LocalDateTime?) {
        timerJob?.cancel()
        if (parkUntil == null) {
            notificationService.cancel()
            _state.update { it.copy(remainingSeconds = null) }
            return
        }

        val untilInstant = parkUntil.toInstant(TimeZone.currentSystemDefault())
        val endTimeMillis = untilInstant.toEpochMilliseconds()
        val canNotify = permissionHandler?.hasPostNotificationsPermission() == true
        if (canNotify) {
            notificationService.startTimer(endTimeMillis)
        } else {
            notificationService.cancel()
        }

        timerJob = viewModelScope.launch {
            while (true) {
                val now = kotlin.time.Clock.System.now()
                val diff = (untilInstant - now).inWholeSeconds
                val safeDiff = if (diff > 0) diff else 0L
                _state.update { it.copy(remainingSeconds = safeDiff) }

                if (canNotify) {
                    notificationService.updateProgress(formatRemaining(safeDiff))
                }

                if (diff <= 0) {
                    notificationService.cancel()
                    break
                }
                delay(1_000L)
            }
        }
    }

    fun onAction(action: ParkingDetailAction) {
        when (action) {
            is ParkingDetailAction.OnBack -> navigationController.backInTab()

            is ParkingDetailAction.OnMarkInactive -> {
                val id = _state.value.spot?.id ?: return
                viewModelScope.launch {
                    markSpotInactiveUseCase(id)
                    notificationService.cancel()
                    navigationController.backInTab()
                }
            }

            is ParkingDetailAction.OnShowLocationMap -> {
                val spot = _state.value.spot ?: return
                MapFocusCoordinator.focusOn(spot.latitude, spot.longitude)
                navigationController.switchTabToRoot(AppTab.CAR)
            }

            is ParkingDetailAction.OnShowParkUntilPicker -> {
                // Inicializar el picker con la hora actual + 1 hora
                val now = kotlin.time.Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                _state.update {
                    it.copy(
                        showParkUntilPicker = true,
                        pickerHours = now.hour + 1,
                        pickerMinutes = 0,
                    )
                }
            }

            is ParkingDetailAction.OnDismissParkUntilPicker ->
                _state.update { it.copy(showParkUntilPicker = false) }

            is ParkingDetailAction.OnPickerHoursChanged ->
                _state.update { it.copy(pickerHours = action.hours) }

            is ParkingDetailAction.OnPickerMinutesChanged ->
                _state.update { it.copy(pickerMinutes = action.minutes) }

            is ParkingDetailAction.OnSaveParkUntil -> {
                val id = _state.value.spot?.id ?: return
                viewModelScope.launch {
                    updateParkUntilUseCase(id, action.parkUntil)
                    _state.update { it.copy(showParkUntilPicker = false) }
                    restartTimer(action.parkUntil)
                }
            }

            is ParkingDetailAction.OnClearParkUntil -> {
                val id = _state.value.spot?.id ?: return
                viewModelScope.launch {
                    updateParkUntilUseCase(id, null)
                    timerJob?.cancel()
                    notificationService.cancel()
                    _state.update { it.copy(remainingSeconds = null) }
                }
            }
        }
    }

    private fun formatRemaining(seconds: Long): String {
        val hours = (seconds / 3600).toString().padStart(2, '0')
        val minutes = ((seconds % 3600) / 60).toString().padStart(2, '0')
        val secs = (seconds % 60).toString().padStart(2, '0')
        return "$hours:$minutes:$secs"
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
