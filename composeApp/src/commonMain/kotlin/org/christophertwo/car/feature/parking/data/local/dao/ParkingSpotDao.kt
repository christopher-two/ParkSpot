package org.christophertwo.car.feature.parking.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.christophertwo.car.feature.parking.data.local.entity.ParkingSpotEntity

@Dao
interface ParkingSpotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ParkingSpotEntity): Long

    @Query("SELECT * FROM parking_spots ORDER BY savedAt DESC")
    fun getAll(): Flow<List<ParkingSpotEntity>>

    @Query("SELECT * FROM parking_spots WHERE id = :id")
    fun getById(id: Long): Flow<ParkingSpotEntity?>

    @Query("DELETE FROM parking_spots WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Marca el spot como inactivo (ya no aparece en el mapa) */
    @Query("UPDATE parking_spots SET isActive = 0 WHERE id = :id")
    suspend fun markInactive(id: Long)

    /** Actualiza el tiempo límite de aparcamiento */
    @Query("UPDATE parking_spots SET parkUntil = :parkUntil WHERE id = :id")
    suspend fun updateParkUntil(id: Long, parkUntil: String?)

    /** Desactiva todos los spots activos (al guardar uno nuevo) */
    @Query("UPDATE parking_spots SET isActive = 0 WHERE isActive = 1")
    suspend fun deactivateAll()
}
