package edu.ucne.skyplanerent.data.repository

import android.util.Log
import edu.ucne.skyplanerent.data.local.dao.TipoVueloDao
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.data.remote.tiposVuelos.TipoVueloDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class TipoVueloRepository @Inject constructor(
    private val dataSource: TipoVueloDataSource,
    private val dao: TipoVueloDao
) {
    fun getTipoVuelo(tipoVueloId: Int): Flow<Resource<List<TipoVueloDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val tipoVuelo = dataSource.getTipoVuelo(tipoVueloId)
            dao.save(listOf(tipoVuelo.toEntity())) // Guardar localmente
            emit(Resource.Success(listOf(tipoVuelo)))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("TipoVueloRepository", "HttpException: $errorMessage")
            val localTipoVuelo = dao.find(tipoVueloId)
            if (localTipoVuelo != null) {
                emit(Resource.Success(listOf(localTipoVuelo.toDto())))
            } else {
                emit(Resource.Error("Error de conexión: $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e("TipoVueloRepository", "Exception: ${e.message}")
            val localTipoVuelo = dao.find(tipoVueloId)
            if (localTipoVuelo != null) {
                emit(Resource.Success(listOf(localTipoVuelo.toDto())))
            } else {
                emit(Resource.Error("Error: ${e.message}"))
            }
        }
    }

    fun getTipoVuelos(): Flow<Resource<List<TipoVueloDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val tiposVuelosFetched = dataSource.getTiposVuelos()
            val listaEntity = tiposVuelosFetched.map { it.toEntity() }
            dao.save(listaEntity) // Guardar localmente
        } catch (e: Exception) {
            Log.e("TipoVueloRepository", "Exception al obtener tipos de vuelo: ${e.message}")
        }
        val listaRetorno = dao.getAll()
        val listaDto = listaRetorno.map { it.toDto() }
        emit(Resource.Success(listaDto))
    }

    suspend fun update(id: Int, tipoVueloDTO: TipoVueloDTO) {
        try {
            dataSource.putTipoVuelo(id, tipoVueloDTO)
        } catch (e: Exception) {
            Log.e("TipoVueloRepository", "Error al actualizar remoto: ${e.message}")
        }
        dao.save(listOf(tipoVueloDTO.toEntity())) // Guardar localmente
    }

    fun find(id: Int): Flow<Resource<TipoVueloDTO>> = flow {
        emit(Resource.Loading())
        try {
            val tipoVuelo = dataSource.getTipoVuelo(id)
            dao.save(listOf(tipoVuelo.toEntity())) // Actualiza localmente
            emit(Resource.Success(tipoVuelo))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("TipoVueloRepository", "HttpException: $errorMessage")
            val localTipoVuelo = dao.find(id)
            if (localTipoVuelo != null) {
                emit(Resource.Success(localTipoVuelo.toDto()))
            } else {
                emit(Resource.Error("Error de conexión: $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e("TipoVueloRepository", "Exception: ${e.message}")
            val localTipoVuelo = dao.find(id)
            if (localTipoVuelo != null) {
                emit(Resource.Success(localTipoVuelo.toDto()))
            } else {
                emit(Resource.Error("Error: ${e.message}"))
            }
        }
    }

    suspend fun saveTipoVuelo(tipoVueloDTO: TipoVueloDTO) {
        try {
            dataSource.postTipoVuelo(tipoVueloDTO)
        } catch (e: Exception) {
            Log.e("TipoVueloRepository", "Error al guardar remoto: ${e.message}")
        }
        dao.save(listOf(tipoVueloDTO.toEntity())) // Guardar localmente
    }

    suspend fun deleteTipoVuelo(id: Int): Resource<Unit> {
        return try {
            dataSource.deleteTipoVuelo(id)
            dao.delete(id)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("TipoVueloRepository", "HttpException: $errorMessage")
            dao.delete(id)
            Resource.Error("Error de conexión: $errorMessage")
        } catch (e: Exception) {
            Log.e("TipoVueloRepository", "Exception: ${e.message}")
            dao.delete(id)
            Resource.Error("Error desconocido: ${e.message}")
        }
    }

    private fun TipoVueloDTO.toEntity() = TipoVueloEntity(
        vueloId = this.tipoVueloId,
        nombreVuelo = this.nombreVuelo,
        descripcionTipoVuelo = this.descripcionTipoVuelo
    )

    private fun TipoVueloEntity.toDto() = TipoVueloDTO(
        tipoVueloId = this.vueloId,
        nombreVuelo = this.nombreVuelo,
        descripcionTipoVuelo = this.descripcionTipoVuelo
    )
}