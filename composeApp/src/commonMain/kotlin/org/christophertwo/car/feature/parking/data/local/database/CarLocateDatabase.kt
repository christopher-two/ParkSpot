package org.christophertwo.car.feature.parking.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import org.christophertwo.car.feature.parking.data.local.converter.LocalDateTimeConverter
import org.christophertwo.car.feature.parking.data.local.converter.PhotoListConverter
import org.christophertwo.car.feature.parking.data.local.dao.ParkingSpotDao
import org.christophertwo.car.feature.parking.data.local.entity.ParkingSpotEntity

@Database(
    entities = [ParkingSpotEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(PhotoListConverter::class, LocalDateTimeConverter::class)
@ConstructedBy(CarLocateDatabaseConstructor::class)
abstract class CarLocateDatabase : RoomDatabase() {
    abstract fun parkingSpotDao(): ParkingSpotDao
}

