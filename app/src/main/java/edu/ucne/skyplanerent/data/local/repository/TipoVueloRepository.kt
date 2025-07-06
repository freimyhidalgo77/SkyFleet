package edu.ucne.skyplanerent.data.local.repository

import edu.ucne.skyplanerent.data.local.dao.TipoVueloDao
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TipoVueloRepository @Inject constructor(
    val tipovuelodao: TipoVueloDao

){
    suspend fun saveTipoVuelo(tipovuelo: TipoVueloEntity) = tipovuelodao.save(tipovuelo)

    suspend fun findTipoVuelo(Id:Int): TipoVueloEntity = tipovuelodao.find(Id)

    suspend fun deleteTipoVuelo(tipovuelo: TipoVueloEntity) = tipovuelodao.delete(tipovuelo)

    fun getAll(): Flow<List<TipoVueloEntity>> = tipovuelodao.getAll()

}