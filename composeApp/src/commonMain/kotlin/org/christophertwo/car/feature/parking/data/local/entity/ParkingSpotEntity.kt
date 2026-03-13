package org.christophertwo.car.feature.parking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parking_spots")
data class ParkingSpotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val latitude: Double,
    val longitude: Double,
    val photoPaths: String = "",
    val savedAt: String,
    val note: String = "",
    val isActive: Int = 1,       // 1 = activo, 0 = inactivo (SQLite no tiene Boolean)
    val parkUntil: String? = null, // ISO LocalDateTime string o null
)

