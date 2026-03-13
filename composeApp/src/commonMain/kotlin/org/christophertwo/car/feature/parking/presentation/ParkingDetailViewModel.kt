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
import org.christophertwo.car.feature.navigation.controller.NavigationController
import org.christophertwo.car.feature.parking.domain.usecase.GetParkingSpotByIdUseCase
import org.christophertwo.car.feature.parking.domain.usecase.MarkSpotInactiveUseCase
import org.christophertwo.car.feature.parking.domain.usecase.UpdateParkUntilUseCase

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
            _state.update { it.copy(remainingSeconds = null) }
            return
        }
        timerJob = viewModelScope.launch {
            while (true) {
                val now = kotlin.time.Clock.System.now()
                val until = parkUntil.toInstant(TimeZone.currentSystemDefault())
                val diff = (until - now).inWholeSeconds
                _state.update { it.copy(remainingSeconds = if (diff > 0) diff else 0L) }
                if (diff <= 0) break
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
                    navigationController.backInTab()
                }
            }

            is ParkingDetailAction.OnShowLocationMap ->
                _state.update { it.copy(showLocationMap = true) }

            is ParkingDetailAction.OnDismissLocationMap ->
                _state.update { it.copy(showLocationMap = false) }

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
                    _state.update { it.copy(remainingSeconds = null) }
                }
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}

