package org.christophertwo.car.feature.history.presentation

sealed interface HistoryAction {
    data class OnSearchQueryChanged(val query: String) : HistoryAction
    data class OnParkingSpotClicked(val id: Long) : HistoryAction
    data class OnDeleteParkingSpot(val id: Long) : HistoryAction
}