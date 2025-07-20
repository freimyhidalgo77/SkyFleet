package edu.ucne.skyplanerent.data.repository

import android.util.Log
import edu.ucne.skyplanerent.data.local.dao.RutaDao
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.remote.rutas.RutasDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RutaRepository @Inject constructor(
    private val dataSource: RutasDataSource,
    private val dao: RutaDao
) {
    fun getRuta(rutaId: Int): Flow<Resource<List<RutaDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val ruta = dataSource.getRuta(rutaId)
            dao.save(listOf(ruta.toEntity()))
            emit(Resource.Success(listOf(ruta)))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("Ruta", "HttpException: $errorMessage")
            val localRuta = dao.find(rutaId)
            if (localRuta != null) {
                emit(Resource.Success(listOf(localRuta.toDto())))
            } else {
                emit(Resource.Error("Error de conexión: $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e("Ruta", "Exception: ${e.message}")
            val localRuta = dao.find(rutaId)
            if (localRuta != null) {
                emit(Resource.Success(listOf(localRuta.toDto())))
            } else {
                emit(Resource.Error("Error: ${e.message}"))
            }
        }
    }

    fun getRutas(): Flow<Resource<List<RutaDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val listaRutasFetched = dataSource.getRutas()
            val listaEntity = listaRutasFetched.map { it.toEntity() }
            dao.save(listaEntity)
        } catch (e: Exception) {
            Log.e("Ruta", "Exception al obtener rutas: ${e.message}")
        }
        val listaRetorno = dao.getAll()
        val listaDto = listaRetorno.map { it.toDto() }
        emit(Resource.Success(listaDto))
    }

    suspend fun update(id: Int, rutaDTO: RutaDTO) {
        try {
            dataSource.putRuta(id, rutaDTO)
        } catch (e: Exception) {
            Log.e("Ruta", "Error al actualizar remoto: ${e.message}")
        }
        dao.save(listOf(rutaDTO.toEntity()))
    }

    fun find(id: Int): Flow<Resource<RutaDTO>> = flow {
        emit(Resource.Loading())
        try {
            val ruta = dataSource.getRuta(id)
            dao.save(listOf(ruta.toEntity())) // Actualiza localmente
            emit(Resource.Success(ruta))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("Ruta", "HttpException: $errorMessage")
            val localRuta = dao.find(id)
            if (localRuta != null) {
                emit(Resource.Success(localRuta.toDto()))
            } else {
                emit(Resource.Error("Error de conexión: $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e("Ruta", "Exception: ${e.message}")
            val localRuta = dao.find(id)
            if (localRuta != null) {
                emit(Resource.Success(localRuta.toDto()))
            } else {
                emit(Resource.Error("Error: ${e.message}"))
            }
        }
    }

    suspend fun saveRuta(rutaDTO: RutaDTO) {
        try {
            dataSource.postRuta(rutaDTO)
        } catch (e: Exception) {
            Log.e("Ruta", "Error al guardar remoto: ${e.message}")
        }
        dao.save(listOf(rutaDTO.toEntity()))
    }

    suspend fun deleteRuta(id: Int): Resource<Unit> {
        return try {
            dataSource.deleteRuta(id)
            dao.delete(id)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("Ruta", "HttpException: $errorMessage")
            dao.delete(id)
            Resource.Error("Error de conexión: $errorMessage")
        } catch (e: Exception) {
            Log.e("Ruta", "Exception: ${e.message}")
            dao.delete(id)
            Resource.Error("Error desconocido: ${e.message}")
        }
    }

    private fun RutaDTO.toEntity() = RutaEntity(
        rutaId = this.rutaId,
        origen = this.origen ?: "",
        destino = this.destino ?: "",
        distancia = this.distancia,
        duracion = this.duracion ?: 0
    )

    private fun RutaEntity.toDto() = RutaDTO(
        rutaId = this.rutaId,
        origen = this.origen ?: "",
        destino = this.destino ?: "",
        distancia = this.distancia,
        duracion = this.duracion ?: 0
    )
}