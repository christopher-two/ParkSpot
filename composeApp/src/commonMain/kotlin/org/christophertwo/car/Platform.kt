package org.christophertwo.car

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform