package org.christophertwo.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.icerock.moko.permissions.PermissionsController
import org.christophertwo.car.App
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val permissionsController: PermissionsController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionsController.bind(this)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}