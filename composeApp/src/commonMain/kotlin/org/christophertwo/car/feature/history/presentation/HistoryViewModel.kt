package org.christophertwo.car.feature.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.christophertwo.car.core.common.RouteHome
import org.christophertwo.car.feature.navigation.controller.NavigationController
import org.christophertwo.car.feature.parking.domain.usecase.DeleteParkingSpotUseCase
import org.christophertwo.car.feature.parking.domain.usecase.GetAllParkingSpotsUseCase

class HistoryViewModel(
    private val getAllParkingSpotsUseCase: GetAllParkingSpotsUseCase,
    private val deleteParkingSpotUseCase: DeleteParkingSpotUseCase,
    private val navigationController: NavigationController,
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state = _state
        .onStart { loadParkingSpots() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HistoryState()
        )

    private fun loadParkingSpots() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAllParkingSpotsUseCase().collect { spots ->
                _state.update {
                    it.copy(
                        parkingSpots = spots,
                        filteredSpots = spots.filter { spot ->
                            it.searchQuery.isBlank() || spot.note.contains(it.searchQuery, ignoreCase = true) ||
                                    spot.savedAt.toString().contains(it.searchQuery)
                        },
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun onAction(action: HistoryAction) {
        when (action) {
            is HistoryAction.OnSearchQueryChanged -> {
                val query = action.query
                _state.update {
                    it.copy(
                        searchQuery = query,
                        filteredSpots = it.parkingSpots.filter { spot ->
                            query.isBlank() || spot.note.contains(query, ignoreCase = true) ||
                                    spot.savedAt.toString().contains(query)
                        }
                    )
                }
            }
            is HistoryAction.OnParkingSpotClicked -> {
                navigationController.navigateInTab(RouteHome.ParkingDetail(action.id))
            }
            is HistoryAction.OnDeleteParkingSpot -> {
                viewModelScope.launch {
                    deleteParkingSpotUseCase(action.id)
                }
            }
        }
    }
}

