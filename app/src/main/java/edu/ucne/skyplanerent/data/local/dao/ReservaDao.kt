package edu.ucne.skyplanerent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservaDao {

    @Upsert
    suspend fun save(reserva: ReservaEntity)

    @Update
    suspend fun update(reserva: ReservaEntity)

    @Query("SELECT * FROM Reservas WHERE reservaId = :id LIMIT 1")
    suspend fun find(id: Int): ReservaEntity?

    @Delete
    suspend fun delete(reserva: ReservaEntity)

    @Query("DELETE FROM reservas WHERE reservaId = :id")
    suspend fun deleteReservaById(id: Int)

    @Query("SELECT * FROM Reservas")
    fun getAll(): Flow<List<ReservaEntity>>

    @Query("SELECT * FROM Reservas WHERE userId = :userId")
    fun getReservasByUserId(userId: String): Flow<List<ReservaEntity>>

}