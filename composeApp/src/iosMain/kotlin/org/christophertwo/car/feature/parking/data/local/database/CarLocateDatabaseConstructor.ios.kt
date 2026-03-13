package org.christophertwo.car.feature.parking.data.local.database

import androidx.room.RoomDatabaseConstructor

actual object CarLocateDatabaseConstructor :
    RoomDatabaseConstructor<CarLocateDatabase> {
    actual override fun initialize(): CarLocateDatabase {
        TODO("Not yet implemented")
    }
}