package edu.ucne.skyplanerent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.skyplanerent.data.local.entity.AeronaveEntity
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AeronaveDao {
    @Upsert
    suspend fun save(aeronave: List<AeronaveEntity>)

    @Query("SELECT * FROM Aeronaves WHERE AeronaveId = :id LIMIT 1")
    suspend fun find(id: Int): AeronaveEntity?

    @Query("DELETE FROM Aeronaves WHERE AeronaveId = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM Aeronaves")
    suspend fun getAll(): List<AeronaveEntity>

    @Query("SELECT * FROM Aeronaves WHERE isPendingSync = 1")
    suspend fun getPendingSync(): List<AeronaveEntity>

    @Query("DELETE FROM Aeronaves WHERE isPendingSync = 1 AND (AeronaveId IS :id OR AeronaveId = 0)")
    suspend fun deletePending(id: Int?)

    @Query("DELETE FROM Aeronaves WHERE AeronaveId = 0")
    suspend fun clearInvalidAeronaves()
}