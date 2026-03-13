package org.christophertwo.car.core.camera

import androidx.compose.runtime.Composable

/**
 * Handle para disparar la captura de foto.
 * La implementación real está en cada plataforma.
 */
interface CameraCaptureLauncher {
    fun launch()
}

/**
 * Lanzador de cámara multiplataforma.
 * Llama a [onPhotoTaken] con la ruta absoluta del archivo guardado,
 * o con null si el usuario canceló.
 */
@Composable
expect fun rememberCameraCaptureLauncher(
    onPhotoTaken: (path: String?) -> Unit
): CameraCaptureLauncher
