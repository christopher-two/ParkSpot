package org.christophertwo.car.feature.parking.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import org.christophertwo.car.feature.parking.data.local.dao.ParkingSpotDao
import org.christophertwo.car.feature.parking.data.local.mapper.toDomain
import org.christophertwo.car.feature.parking.data.local.mapper.toEntity
import org.christophertwo.car.feature.parking.domain.model.ParkingSpot
import org.christophertwo.car.feature.parking.domain.repository.ParkingRepository

class ParkingRepositoryImpl(
    private val dao: ParkingSpotDao
) : ParkingRepository {

    override suspend fun saveParkingSpot(spot: ParkingSpot): Long {
        return dao.insert(spot.toEntity())
    }

    override fun getAllParkingSpots(): Flow<List<ParkingSpot>> {
        return dao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getParkingSpotById(id: Long): Flow<ParkingSpot?> {
        return dao.getById(id).map { it?.toDomain() }
    }

    override suspend fun deleteParkingSpot(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun markSpotInactive(id: Long) {
        dao.markInactive(id)
    }

    override suspend fun updateParkUntil(id: Long, parkUntil: LocalDateTime?) {
        dao.updateParkUntil(id, parkUntil?.toString())
    }

    override suspend fun deactivateAllSpots() {
        dao.deactivateAll()
    }
}
