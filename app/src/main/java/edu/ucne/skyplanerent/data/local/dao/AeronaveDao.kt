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
    suspend fun save(aeronave: AeronaveEntity)

    @Query("SELECT * FROM Aeronaves WHERE aeronaveId = :id LIMIT 1")
    suspend fun find(id: Int): AeronaveEntity

    @Delete
    suspend fun delete(aeronave: AeronaveEntity)

    @Query("SELECT * FROM Aeronaves")
    fun getAll(): Flow<List<AeronaveEntity>>

}