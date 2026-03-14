@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.christophertwo.car.feature.parking.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.math.max

private class IosParkingNotificationService(
    private val ticketId: Long,
) : ParkingNotificationService {

    private val center: UNUserNotificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    private val notificationId: String = "parking-ticket-$ticketId"

    override fun startTimer(endTime: Long) {
        center.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge,
            completionHandler = { _, _ -> },
        )

        val nowMillis = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
        val seconds = max(1.0, (endTime - nowMillis).toDouble() / 1000.0)

        val content = UNMutableNotificationContent().apply {
            title = "Ticket de parking"
            body = "Tu ticket esta por finalizar."
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = seconds,
            repeats = false,
        )
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = notificationId,
            content = content,
            trigger = trigger,
        )
        center.addNotificationRequest(request) { _ -> }
    }

    override fun updateProgress(remaining: String) {
        // Esquema base iOS: no actualizamos una notificacion foreground en tiempo real.
    }

    override fun cancel() {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(notificationId))
    }
}

@Composable
actual fun rememberParkingNotificationService(ticketId: Long): ParkingNotificationService {
    return remember(ticketId) { IosParkingNotificationService(ticketId) }
}

