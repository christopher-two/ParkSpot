@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.christophertwo.car.core.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun rememberPermissionHandler(): PermissionHandler {
    val context = LocalContext.current
    var callback by remember { mutableStateOf<((Boolean) -> Unit)?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        callback?.invoke(granted)
        callback = null
    }

    return remember(context, launcher) {
        object : PermissionHandler {
            override fun hasPostNotificationsPermission(): Boolean {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
                return ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            }

            override fun requestPostNotificationsPermission(onResult: (granted: Boolean) -> Unit) {
                if (hasPostNotificationsPermission()) {
                    onResult(true)
                    return
                }
                callback = onResult
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
