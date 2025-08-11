package edu.ucne.skyplanerent.data.repository

import edu.ucne.skyplanerent.data.local.dao.CategoriaAeronaveDao
import edu.ucne.skyplanerent.data.local.entity.CategoriaAeronaveEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoriaAeronaveRepository @Inject constructor(
    private val dao: CategoriaAeronaveDao
) {
    suspend fun saveCategoriaAeronave(categoriaaeronave: CategoriaAeronaveEntity) = dao.saveCategoriaAeronave(categoriaaeronave)
    suspend fun find(id: Int): CategoriaAeronaveEntity = dao.find(id)
    suspend fun deleteCategoriaAeronave(categoriaaeronave: CategoriaAeronaveEntity) = dao.deleteCategoriaAeronave(categoriaaeronave)
    fun getAll(): Flow<List<CategoriaAeronaveEntity>> = dao.getAll()
}
