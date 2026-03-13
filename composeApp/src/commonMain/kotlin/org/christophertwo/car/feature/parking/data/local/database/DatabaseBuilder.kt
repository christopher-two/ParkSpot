package org.christophertwo.car.feature.parking.data.local.database

import androidx.room.RoomDatabase

expect fun getDatabaseBuilder(): RoomDatabase.Builder<CarLocateDatabase>

