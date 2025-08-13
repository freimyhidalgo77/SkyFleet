package edu.ucne.skyplanerent.data.repository

import android.util.Log
import edu.ucne.skyplanerent.data.local.dao.AdminDao
import edu.ucne.skyplanerent.data.local.entity.AdminEntity
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.admins.AdminsDataSource
import edu.ucne.skyplanerent.data.remote.dto.AdminDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class AdminRepository @Inject constructor(
    private val dataSource: AdminsDataSource,
    private val dao: AdminDao
) {
    fun getAdmin(adminId: Int): Flow<Resource<List<AdminDTO>>> = flow {
        emit(Resource.Loading())
        try {
            Log.d("AdminRepository", "Intentando obtener admin con ID: $adminId desde API")
            val admin = dataSource.getAdmin(adminId)
            Log.d("AdminRepository", "Admin obtenido desde API: $admin")
            dao.save(listOf(admin.toEntity().copy(isPendingSync = false)))
            emit(Resource.Success(listOf(admin)))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("AdminRepository", "Error HTTP al obtener admin: $errorMessage")
            val localAdmin = dao.find(adminId)
            Log.d("AdminRepository", "Admin local encontrado: $localAdmin")
            if (localAdmin != null) {
                emit(Resource.Success(listOf(localAdmin.toDto())))
            } else {
                emit(Resource.Error("Error de conexión: $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e("AdminRepository", "Error al obtener admin: ${e.message}")
            val localAdmin = dao.find(adminId)
            Log.d("AdminRepository", "Admin local encontrado: $localAdmin")
            if (localAdmin != null) {
                emit(Resource.Success(listOf(localAdmin.toDto())))
            } else {
                emit(Resource.Error("Error inesperado: ${e.message}"))
            }
        }
    }

    fun getAdminByEmail(email: String, password: String): Flow<Resource<List<AdminDTO>>> = flow {
        emit(Resource.Loading())
        try {
            Log.d("AdminRepository", "Obteniendo admins desde la API para buscar por email")
            val admins = dataSource.getAdmins()
            val admin = admins.find { it.correo == email && it.contrasena == password }
            if (admin != null) {
                dao.save(listOf(admin.toEntity().copy(isPendingSync = false)))
                emit(Resource.Success(listOf(admin)))
            } else {
                dao.save(admins.map { it.toEntity().copy(isPendingSync = false) })
                emit(Resource.Success(emptyList()))
            }
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("AdminRepository", "Error HTTP al buscar admin por email: $errorMessage")
            val localAdmin = dao.findByEmail(email)
            if (localAdmin != null && localAdmin.contrasena == password) {
                emit(Resource.Success(listOf(localAdmin.toDto())))
            } else {
                emit(Resource.Success(emptyList()))
            }
        } catch (e: Exception) {
            Log.e("AdminRepository", "Error al buscar admin por email: ${e.message}")
            val localAdmin = dao.findByEmail(email)
            if (localAdmin != null && localAdmin.contrasena == password) {
                emit(Resource.Success(listOf(localAdmin.toDto())))
            } else {
                emit(Resource.Success(emptyList()))
            }
        }
    }

    private fun AdminDTO.toEntity() = AdminEntity(
        adminId = this.adminId,
        correo = this.correo,
        contrasena = this.contrasena,
        foto = this.foto,
        isPendingSync = false
    )

    private fun AdminEntity.toDto() = AdminDTO(
        adminId = this.adminId,
        correo = this.correo,
        contrasena = this.contrasena,
        foto = this.foto
    )
}