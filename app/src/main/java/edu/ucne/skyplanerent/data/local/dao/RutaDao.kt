package edu.ucne.skyplanerent.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.skyplanerent.data.local.entity.RutaEntity


@Dao
interface RutaDao {

    @Upsert
    suspend fun save(ruta: List<RutaEntity>)

    @Query("SELECT * FROM Rutas WHERE rutaId = :id LIMIT 1")
    suspend fun find(id: Int): RutaEntity?

    @Query("DELETE FROM Rutas WHERE rutaId = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM Rutas")
    suspend fun getAll(): List<RutaEntity>

}