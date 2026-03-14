package org.christophertwo.car.feature.parking.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class ParkingForegroundService : Service() {

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    private var endTimeMillis: Long = 0L
    private var ticketId: Long = -1L
    private var remainingLabel: String = ""

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                endTimeMillis = intent.getLongExtra(EXTRA_END_TIME_MILLIS, 0L)
                ticketId = intent.getLongExtra(EXTRA_TICKET_ID, -1L)
                if (endTimeMillis <= 0L || ticketId <= 0L) {
                    stopSelf()
                    return startNotStickyCompat()
                }
                startForegroundCompat(buildNotification())
            }

            ACTION_UPDATE -> {
                remainingLabel = intent.getStringExtra(EXTRA_REMAINING_LABEL).orEmpty()
                val newTicketId = intent.getLongExtra(EXTRA_TICKET_ID, ticketId)
                if (newTicketId > 0L) ticketId = newTicketId
                notifyCompat()
            }

            ACTION_CANCEL -> {
                stopForegroundCompat()
                stopSelf()
            }
        }
        return startStickyCompat()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val launchIntent = buildLaunchIntent()

        val pendingIntent = PendingIntent.getActivity(
            this,
            ticketId.toInt(),
            launchIntent,
            pendingIntentFlagsCompat(),
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Ticket de parking activo")
            .setContentText(
                if (remainingLabel.isBlank()) "Tiempo restante en curso" else "Restante: $remainingLabel"
            )
            .setWhen(endTimeMillis)
            .setUsesChronometer(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setChronometerCountDown(true)
                }
            }
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Parking Timer",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Muestra el contador del ticket de parking en primer plano"
        }
        manager.createNotificationChannel(channel)
    }

    private fun canPostNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun buildLaunchIntent(): Intent {
        val launchIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            packageManager.getLaunchIntentForPackage(packageName)
        } else {
            null
        } ?: Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                setPackage(packageName)
            }
        }

        launchIntent.apply {
            putExtra(ParkingNotificationDeepLink.EXTRA_TICKET_ID, ticketId)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        return launchIntent
    }

    private fun pendingIntentFlagsCompat(): Int {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE -> {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            else -> 0
        }
    }

    @SuppressLint("MissingPermission")
    private fun notifyCompat() {
        if (!canPostNotifications()) return
        notificationManager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun startForegroundCompat(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            startForeground(NOTIFICATION_ID, notification)
        } else {
            notifyCompat()
        }
    }

    private fun stopForegroundCompat() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR -> {
                stopForeground(true)
            }
            else -> {
                notificationManager.cancel(NOTIFICATION_ID)
            }
        }
    }

    private fun startStickyCompat(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) START_STICKY else 1
    }

    private fun startNotStickyCompat(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) START_NOT_STICKY else 2
    }

    companion object {
        const val ACTION_START = "org.christophertwo.car.notification.START"
        const val ACTION_UPDATE = "org.christophertwo.car.notification.UPDATE"
        const val ACTION_CANCEL = "org.christophertwo.car.notification.CANCEL"

        const val EXTRA_END_TIME_MILLIS = "extra_end_time_millis"
        const val EXTRA_REMAINING_LABEL = "extra_remaining_label"
        const val EXTRA_TICKET_ID = "extra_ticket_id"

        private const val CHANNEL_ID = "parking_timer_channel"
        private const val NOTIFICATION_ID = 43121
    }
}
