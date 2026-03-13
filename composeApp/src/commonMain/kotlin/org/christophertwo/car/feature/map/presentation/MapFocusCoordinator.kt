package org.christophertwo.car.feature.map.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MapFocusRequest(
    val latitude: Double,
    val longitude: Double,
    val requestId: Long,
)

object MapFocusCoordinator {
    private var nextRequestId: Long = 0L
    private val _focusRequest = MutableStateFlow<MapFocusRequest?>(null)
    val focusRequest = _focusRequest.asStateFlow()

    fun focusOn(latitude: Double, longitude: Double) {
        nextRequestId += 1L
        _focusRequest.value = MapFocusRequest(
            latitude = latitude,
            longitude = longitude,
            requestId = nextRequestId,
        )
    }
}

