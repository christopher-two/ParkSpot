@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
package org.christophertwo.car.core.camera

import androidx.compose.runtime.Composable

@Composable
actual fun rememberCameraCaptureLauncher(
    onPhotoTaken: (path: String?) -> Unit
): CameraCaptureLauncher = object : CameraCaptureLauncher {
    override fun launch() { /* No disponible en iOS aún */ }
}
