package edu.ucne.skyplanerent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TipoVueloDao {
    @Upsert
    suspend fun save(tipovuelo: List<TipoVueloEntity>)

    @Query("SELECT * FROM TipoVuelo WHERE vueloId = :id LIMIT 1")
    suspend fun find(id: Int): TipoVueloEntity?

    @Query("DELETE FROM TipoVuelo WHERE vueloId = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM TipoVuelo")
    suspend fun getAll(): List<TipoVueloEntity>

    @Query("SELECT * FROM TipoVuelo WHERE isPendingSync = 1")
    suspend fun getPendingSync(): List<TipoVueloEntity>

    @Query("DELETE FROM TipoVuelo WHERE isPendingSync = 1 AND (vueloId IS :id OR vueloId = 0)")
    suspend fun deletePending(id: Int?)

    @Query("DELETE FROM TipoVuelo WHERE vueloId = 0")
    suspend fun clearInvalidTiposVuelo()
}