package edu.ucne.skyplanerent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TipoVueloDao {

    @Upsert
    suspend fun save(tipovuelo: TipoVueloEntity)

    @Query("SELECT * FROM TipoVuelo WHERE vueloId = :id LIMIT 1")
    suspend fun find(id: Int): TipoVueloEntity

    @Delete
    suspend fun delete(mensaje: TipoVueloEntity)

    @Query("SELECT * FROM TipoVuelo")
    fun getAll(): Flow<List<TipoVueloEntity>>

}