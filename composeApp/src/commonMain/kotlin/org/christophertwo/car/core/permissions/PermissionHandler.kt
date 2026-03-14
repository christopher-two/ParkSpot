@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.christophertwo.car.core.permissions

import androidx.compose.runtime.Composable

interface PermissionHandler {
    fun hasPostNotificationsPermission(): Boolean
    fun requestPostNotificationsPermission(onResult: (granted: Boolean) -> Unit)
}

@Composable
expect fun rememberPermissionHandler(): PermissionHandler

