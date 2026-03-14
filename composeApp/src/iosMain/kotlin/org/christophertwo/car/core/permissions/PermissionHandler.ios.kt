@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.christophertwo.car.core.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberPermissionHandler(): PermissionHandler {
    return remember {
        object : PermissionHandler {
            override fun hasPostNotificationsPermission(): Boolean = true

            override fun requestPostNotificationsPermission(onResult: (granted: Boolean) -> Unit) {
                onResult(true)
            }
        }
    }
}

