@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.christophertwo.car.feature.parking.data.local.database

import androidx.room.RoomDatabaseConstructor

expect object CarLocateDatabaseConstructor : RoomDatabaseConstructor<CarLocateDatabase> {
    override fun initialize(): CarLocateDatabase
}