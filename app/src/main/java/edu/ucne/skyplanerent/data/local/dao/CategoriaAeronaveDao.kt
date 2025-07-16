package edu.ucne.skyplanerent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.skyplanerent.data.local.entity.AeronaveEntity
import edu.ucne.skyplanerent.data.local.entity.CategoriaAeronaveEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaAeronaveDao {

    @Upsert
    suspend fun save(categoriaaeronave: CategoriaAeronaveEntity)

    @Query("SELECT * FROM categoria_aeronave WHERE categoriaId = :id LIMIT 1")
    suspend fun find(id: Int): CategoriaAeronaveEntity

    @Delete
    suspend fun delete(categoriaaeronave: CategoriaAeronaveEntity)

    @Query("SELECT * FROM categoria_aeronave")
    fun getAll(): Flow<List<CategoriaAeronaveEntity>>

}