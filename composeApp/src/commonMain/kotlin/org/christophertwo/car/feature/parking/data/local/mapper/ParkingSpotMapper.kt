package org.christophertwo.car.feature.parking.data.local.mapper

import kotlinx.datetime.LocalDateTime
import org.christophertwo.car.feature.parking.data.local.entity.ParkingSpotEntity
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot

fun ParkingSpotEntity.toDomain(): ParkingSpot = ParkingSpot(
    id = id,
    latitude = latitude,
    longitude = longitude,
    photoPaths = if (photoPaths.isBlank()) emptyList() else photoPaths.split("|"),
    savedAt = LocalDateTime.parse(savedAt),
    note = note,
    isActive = isActive == 1,
    parkUntil = parkUntil?.let { LocalDateTime.parse(it) },
)

fun ParkingSpot.toEntity(): ParkingSpotEntity = ParkingSpotEntity(
    id = id,
    latitude = latitude,
    longitude = longitude,
    photoPaths = photoPaths.joinToString("|"),
    savedAt = savedAt.toString(),
    note = note,
    isActive = if (isActive) 1 else 0,
    parkUntil = parkUntil?.toString(),
)

