@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.christophertwo.car.feature.parking.notification

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

private class AndroidParkingNotificationService(
    private val appContext: Context,
    private val ticketId: Long,
) : ParkingNotificationService {

    override fun startTimer(endTime: Long) {
        val intent = Intent(appContext, ParkingForegroundService::class.java).apply {
            action = ParkingForegroundService.ACTION_START
            putExtra(ParkingForegroundService.EXTRA_END_TIME_MILLIS, endTime)
            putExtra(ParkingForegroundService.EXTRA_TICKET_ID, ticketId)
        }
        ContextCompat.startForegroundService(appContext, intent)
    }

    override fun updateProgress(remaining: String) {
        val intent = Intent(appContext, ParkingForegroundService::class.java).apply {
            action = ParkingForegroundService.ACTION_UPDATE
            putExtra(ParkingForegroundService.EXTRA_REMAINING_LABEL, remaining)
            putExtra(ParkingForegroundService.EXTRA_TICKET_ID, ticketId)
        }
        appContext.startService(intent)
    }

    override fun cancel() {
        val intent = Intent(appContext, ParkingForegroundService::class.java).apply {
            action = ParkingForegroundService.ACTION_CANCEL
        }
        appContext.startService(intent)
    }
}

@Composable
actual fun rememberParkingNotificationService(ticketId: Long): ParkingNotificationService {
    val context = LocalContext.current.applicationContext
    return remember(context, ticketId) {
        AndroidParkingNotificationService(
            appContext = context,
            ticketId = ticketId,
        )
    }
}

