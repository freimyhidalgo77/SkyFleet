package edu.ucne.skyplanerent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RutaDao {

    @Upsert
    suspend fun save(ruta: RutaEntity)

    @Query("SELECT * FROM Rutas WHERE rutaId = :id LIMIT 1")
    suspend fun find(id: Int): RutaEntity

    @Delete
    suspend fun delete(mensaje: RutaEntity)

    @Query("SELECT * FROM Rutas")
    fun getAll(): Flow<List<RutaEntity>>

}