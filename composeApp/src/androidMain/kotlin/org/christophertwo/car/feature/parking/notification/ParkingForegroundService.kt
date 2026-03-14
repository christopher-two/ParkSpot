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
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class ParkingForegroundService : Service() {

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val handler = Handler(Looper.getMainLooper())

    private var endTimeMillis: Long = 0L
    private var ticketId: Long = -1L
    private var remainingLabel: String = ""
    private var initialDurationMillis: Long = 0L
    private var milestone50Sent: Boolean = false
    private var milestone10Sent: Boolean = false
    private var completionSent: Boolean = false

    private val tickerRunnable = object : Runnable {
        override fun run() {
            val remainingMillis = (endTimeMillis - System.currentTimeMillis()).coerceAtLeast(0L)
            remainingLabel = formatDurationLabel(remainingMillis)

            maybeSendMilestones(remainingMillis)
            notifyCompat()

            if (remainingMillis <= 0L) {
                stopTicker()
                stopForegroundCompat()
                stopSelf()
                return
            }
            handler.postDelayed(this, 1_000L)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val newEndTime = intent.getLongExtra(EXTRA_END_TIME_MILLIS, 0L)
                val newTicketId = intent.getLongExtra(EXTRA_TICKET_ID, -1L)
                if (newEndTime <= 0L || newTicketId <= 0L) {
                    stopSelf()
                    return startNotStickyCompat()
                }

                val isNewTimer = (newTicketId != ticketId) || (newEndTime != endTimeMillis)
                ticketId = newTicketId
                endTimeMillis = newEndTime

                val remainingMillis = (endTimeMillis - System.currentTimeMillis()).coerceAtLeast(0L)
                if (isNewTimer) {
                    initialDurationMillis = remainingMillis.coerceAtLeast(1_000L)
                    milestone50Sent = false
                    milestone10Sent = false
                    completionSent = false
                }

                remainingLabel = formatDurationLabel(remainingMillis)
                startForegroundCompat(buildOngoingNotification())
                startTicker()
            }

            ACTION_UPDATE -> {
                remainingLabel = intent.getStringExtra(EXTRA_REMAINING_LABEL).orEmpty()
                val newTicketId = intent.getLongExtra(EXTRA_TICKET_ID, ticketId)
                if (newTicketId > 0L) ticketId = newTicketId
                notifyCompat()
            }

            ACTION_CANCEL -> {
                stopTicker()
                stopForegroundCompat()
                stopSelf()
            }
        }
        return startStickyCompat()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startTicker() {
        handler.removeCallbacks(tickerRunnable)
        handler.post(tickerRunnable)
    }

    private fun stopTicker() {
        handler.removeCallbacks(tickerRunnable)
    }

    private fun maybeSendMilestones(remainingMillis: Long) {
        if (initialDurationMillis <= 0L) return

        if (!milestone50Sent && remainingMillis <= (initialDurationMillis / 2L)) {
            milestone50Sent = true
            notifyMilestone(50)
        }

        val threshold10 = (initialDurationMillis / 10L).coerceAtLeast(1L)
        if (!milestone10Sent && remainingMillis <= threshold10) {
            milestone10Sent = true
            notifyMilestone(10)
        }

        if (!completionSent && remainingMillis <= 0L) {
            completionSent = true
            notifyCompletion()
        }
    }

    private fun formatDurationLabel(remainingMillis: Long): String {
        val totalSeconds = (remainingMillis / 1_000L).coerceAtLeast(0L)
        val hours = (totalSeconds / 3_600L).toString().padStart(2, '0')
        val minutes = ((totalSeconds % 3_600L) / 60L).toString().padStart(2, '0')
        val seconds = (totalSeconds % 60L).toString().padStart(2, '0')
        return "$hours:$minutes:$seconds"
    }

    private fun buildOngoingNotification(): Notification {
        val pendingIntent = buildContentPendingIntent()

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

    private fun buildMilestoneNotification(percentRemaining: Int): Notification {
        val pendingIntent = buildContentPendingIntent()
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Recordatorio de ticket")
            .setContentText("Te queda el $percentRemaining% del tiempo ($remainingLabel)")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun buildCompletionNotification(): Notification {
        val pendingIntent = buildContentPendingIntent()
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Ticket finalizado")
            .setContentText("Tu tiempo de parking ha terminado")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun buildContentPendingIntent(): PendingIntent {
        val launchIntent = buildLaunchIntent()
        return PendingIntent.getActivity(
            this,
            ticketId.toInt(),
            launchIntent,
            pendingIntentFlagsCompat(),
        )
    }

    @SuppressLint("MissingPermission")
    private fun notifyMilestone(percentRemaining: Int) {
        if (!canPostNotifications()) return
        notificationManager.notify(milestoneNotificationId(percentRemaining), buildMilestoneNotification(percentRemaining))
    }

    @SuppressLint("MissingPermission")
    private fun notifyCompletion() {
        if (!canPostNotifications()) return
        notificationManager.notify(completionNotificationId(), buildCompletionNotification())
    }

    private fun milestoneNotificationId(percentRemaining: Int): Int {
        return (ticketId.toInt().coerceAtLeast(1) * 100) + percentRemaining
    }

    private fun completionNotificationId(): Int {
        return (ticketId.toInt().coerceAtLeast(1) * 100) + 1
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
        notificationManager.notify(NOTIFICATION_ID, buildOngoingNotification())
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
