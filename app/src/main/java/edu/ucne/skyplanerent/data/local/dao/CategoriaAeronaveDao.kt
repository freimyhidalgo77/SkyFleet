package edu.ucne.skyplanerent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.skyplanerent.data.local.entity.CategoriaAeronaveEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaAeronaveDao {

    @Upsert
    suspend fun saveCategoriaAeronave(categoriaAeronaves: List<CategoriaAeronaveEntity>)

    @Upsert
    suspend fun saveCategoriaAeronave(categoriaAeronaves: CategoriaAeronaveEntity)

    @Query("SELECT * FROM categoria_aeronave WHERE categoriaId = :id LIMIT 1")
    suspend fun find(id: Int): CategoriaAeronaveEntity?

    @Delete
    suspend fun deleteCategoriaAeronave(categoriaAeronave: CategoriaAeronaveEntity)

    @Query("SELECT * FROM categoria_aeronave")
    fun getAll(): Flow<List<CategoriaAeronaveEntity>>

    @Query("SELECT COUNT(*) FROM categoria_aeronave")
    suspend fun getCount(): Int // Nuevo: para verificar si la tabla está vacía
}