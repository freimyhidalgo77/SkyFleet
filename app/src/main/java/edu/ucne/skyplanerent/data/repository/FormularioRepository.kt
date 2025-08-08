package edu.ucne.skyplanerent.data.repository

import edu.ucne.skyplanerent.data.local.dao.FormularioDao
import edu.ucne.skyplanerent.data.local.entity.FormularioEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FormularioRepository @Inject constructor(
    val formularioDao: FormularioDao

){
    suspend fun saveFormulario(formulario: FormularioEntity): Int {
        return formularioDao.save(formulario).toInt()
    }

    suspend fun findFormulario(Id:Int): FormularioEntity = formularioDao.find(Id)

    suspend fun deleteFormulario(formulario: FormularioEntity) = formularioDao.delete(formulario)

    fun getAll(): Flow<List<FormularioEntity>> = formularioDao.getAll()

}