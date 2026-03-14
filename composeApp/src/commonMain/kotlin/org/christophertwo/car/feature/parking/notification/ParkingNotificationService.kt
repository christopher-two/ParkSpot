@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.christophertwo.car.feature.parking.notification

import androidx.compose.runtime.Composable

interface ParkingNotificationService {
    fun startTimer(endTime: Long)
    fun updateProgress(remaining: String)
    fun cancel()
}

object NoOpParkingNotificationService : ParkingNotificationService {
    override fun startTimer(endTime: Long) = Unit
    override fun updateProgress(remaining: String) = Unit
    override fun cancel() = Unit
}

@Composable
expect fun rememberParkingNotificationService(ticketId: Long): ParkingNotificationService

