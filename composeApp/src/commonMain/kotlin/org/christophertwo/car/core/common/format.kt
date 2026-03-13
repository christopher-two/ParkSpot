package org.christophertwo.car.core.common

import kotlin.math.pow
import kotlin.math.round

fun Double.format(decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = round(this * factor) / factor
    return rounded.toString()
}