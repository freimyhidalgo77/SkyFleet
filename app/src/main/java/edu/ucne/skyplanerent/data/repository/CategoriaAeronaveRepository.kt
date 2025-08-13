package edu.ucne.skyplanerent.data.repository

import edu.ucne.skyplanerent.data.local.dao.CategoriaAeronaveDao
import edu.ucne.skyplanerent.data.local.entity.CategoriaAeronaveEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

open class CategoriaAeronaveRepository @Inject constructor(
    private val dao: CategoriaAeronaveDao
) {
    suspend fun saveCategoriaAeronave(categoriaAeronaves: List<CategoriaAeronaveEntity>) =
        dao.saveCategoriaAeronave(categoriaAeronaves)

    suspend fun saveCategoriaAeronave(categoriaAeronaves: CategoriaAeronaveEntity) =
        dao.saveCategoriaAeronave(categoriaAeronaves)

    suspend fun find(id: Int): CategoriaAeronaveEntity? = dao.find(id)

    suspend fun deleteCategoriaAeronave(categoriaAeronave: CategoriaAeronaveEntity) =
        dao.deleteCategoriaAeronave(categoriaAeronave)

    open suspend fun getAll(): Flow<List<CategoriaAeronaveEntity>> = dao.getAll()

    suspend fun getCount(): Int = dao.getCount()
}
