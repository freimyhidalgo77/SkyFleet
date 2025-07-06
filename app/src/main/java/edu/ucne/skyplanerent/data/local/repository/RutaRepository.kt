package edu.ucne.skyplanerent.data.local.repository

import edu.ucne.skyplanerent.data.local.dao.RutaDao
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RutaRepository @Inject constructor(
    val rutaDao: RutaDao

){
    suspend fun saveRuta(ruta: RutaEntity) = rutaDao.save(ruta)

    suspend fun findRuta(Id:Int): RutaEntity = rutaDao.find(Id)

    suspend fun deleteRuta(ruta: RutaEntity) = rutaDao.delete(ruta)

    fun getAll(): Flow<List<RutaEntity>> = rutaDao.getAll()

}