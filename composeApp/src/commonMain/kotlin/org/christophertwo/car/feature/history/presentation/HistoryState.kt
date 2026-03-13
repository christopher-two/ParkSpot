package org.christophertwo.car.feature.history.presentation

import org.christophertwo.car.feature.parking.domain.model.ParkingSpot

data class HistoryState(
    val parkingSpots: List<ParkingSpot> = emptyList(),
    val filteredSpots: List<ParkingSpot> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
)