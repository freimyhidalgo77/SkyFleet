package edu.ucne.skyplanerent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.skyplanerent.data.local.entity.AeronaveEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AeronaveDao {

    @Upsert
    suspend fun save(aeronave: List<AeronaveEntity>)

    @Query("SELECT * FROM Aeronaves WHERE aeronaveId = :id LIMIT 1")
    suspend fun find(id: Int): AeronaveEntity?

    @Query("DELETE FROM Aeronaves WHERE AeronaveId = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM Aeronaves")
    suspend fun getAll(): List<AeronaveEntity>

}