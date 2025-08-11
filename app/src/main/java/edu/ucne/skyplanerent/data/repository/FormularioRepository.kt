package edu.ucne.skyplanerent.data.repository

import edu.ucne.skyplanerent.data.local.dao.FormularioDao
import edu.ucne.skyplanerent.data.local.entity.FormularioEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FormularioRepository @Inject constructor(
    private val formularioDao: FormularioDao

){
    suspend fun saveFormulario(formulario: FormularioEntity): Int {
        return formularioDao.save(formulario).toInt()
    }

    suspend fun findFormulario(id:Int): FormularioEntity = formularioDao.find(id)

    suspend fun deleteFormulario(formulario: FormularioEntity) = formularioDao.delete(formulario)

    fun getAll(): Flow<List<FormularioEntity>> = formularioDao.getAll()

}