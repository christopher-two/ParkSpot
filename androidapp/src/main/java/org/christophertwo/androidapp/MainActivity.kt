package org.christophertwo.androidapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.icerock.moko.permissions.PermissionsController
import org.christophertwo.car.App
import org.christophertwo.car.feature.parking.notification.ParkingNotificationDeepLink
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val permissionsController: PermissionsController by inject()
    private var openTicketId: Long? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        permissionsController.bind(this)
        openTicketId = intent.extractTicketId()

        enableEdgeToEdge()
        setContent {
            App(
                openTicketId = openTicketId,
                onTicketIntentConsumed = { openTicketId = null },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openTicketId = intent.extractTicketId()
    }

    private fun Intent?.extractTicketId(): Long? {
        val value = this?.getLongExtra(ParkingNotificationDeepLink.EXTRA_TICKET_ID, -1L) ?: -1L
        return value.takeIf { it > 0L }
    }
}