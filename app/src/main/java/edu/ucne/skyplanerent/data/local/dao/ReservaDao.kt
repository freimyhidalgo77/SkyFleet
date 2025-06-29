package edu.ucne.skyplanerent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservaDao {

    @Upsert
    suspend fun save(reserva: ReservaEntity)

    @Query("SELECT * FROM Reservas WHERE reservaId = :id LIMIT 1")
    suspend fun find(id: Int): ReservaEntity

    @Delete
    suspend fun delete(mensaje: ReservaEntity)

    @Query("SELECT * FROM Reservas")
    fun getAll(): Flow<List<ReservaEntity>>

}