@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
package org.christophertwo.car.core.camera

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.FROYO)
@Composable
actual fun rememberCameraCaptureLauncher(
    onPhotoTaken: (path: String?) -> Unit
): CameraCaptureLauncher {
    val context = LocalContext.current
    val photoFile = remember { createImageFile(context) }
    val photoUri: Uri = remember(photoFile) {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }

    // Indicador para disparar la cámara una vez concedido el permiso
    var pendingLaunch by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        onPhotoTaken(if (success) photoFile.absolutePath else null)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(photoUri)
        } else {
            onPhotoTaken(null)
        }
        pendingLaunch = false
    }

    return remember(cameraLauncher, permissionLauncher, photoUri) {
        object : CameraCaptureLauncher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun launch() {
                val permission = Manifest.permission.CAMERA
                val granted = context.checkSelfPermission(permission) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
                if (granted) {
                    cameraLauncher.launch(photoUri)
                } else {
                    permissionLauncher.launch(permission)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.FROYO)
private fun createImageFile(context: Context): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir("Pictures") ?: context.filesDir
    return File.createTempFile("PARKING_${timestamp}_", ".jpg", storageDir)
}
