package org.christophertwo.car.core.common

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.History

enum class AppTab(
    val label: String,
    val icon: ImageVector
) {
    CAR(label = "Car", icon = FontAwesomeIcons.Solid.Car),
    HISTORY(label = "History", icon = FontAwesomeIcons.Solid.History),
}