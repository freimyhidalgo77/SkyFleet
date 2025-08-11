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
            dao.save(listOf(tipoVuelo.toEntity().copy(isPendingSync = false)))
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
            // Limpiar tipos de vuelo inválidos con vueloId = 0
            dao.clearInvalidTiposVuelo()

            // Sincronizar tipos de vuelo pendientes
            val pendingTiposVuelo = dao.getPendingSync()
            for (pending in pendingTiposVuelo) {
                try {
                    val savedTipoVuelo = dataSource.postTipoVuelo(pending.toDto())
                    dao.deletePending(pending.vueloId)
                    dao.save(listOf(savedTipoVuelo.toEntity().copy(isPendingSync = false)))
                } catch (e: Exception) {
                    Log.e("TipoVueloRepository", "Error al sincronizar tipo de vuelo ${pending.vueloId}: ${e.message}")
                }
            }

            // Obtener tipos de vuelo del servidor
            val tiposVuelosFetched = dataSource.getTiposVuelos()
            val listaEntity = tiposVuelosFetched.map { it.toEntity().copy(isPendingSync = false) }
            dao.save(listaEntity)

            // Emitir tipos de vuelo locales actualizados
            val listaRetorno = dao.getAll()
            val listaDto = listaRetorno.map { it.toDto() }
            emit(Resource.Success(listaDto))
        } catch (e: Exception) {
            Log.e("TipoVueloRepository", "Exception al obtener tipos de vuelo: ${e.message}")
            val listaRetorno = dao.getAll()
            val listaDto = listaRetorno.map { it.toDto() }
            emit(Resource.Success(listaDto))
        }
    }

    suspend fun update(id: Int, tipoVueloDTO: TipoVueloDTO) {
        try {
            dataSource.putTipoVuelo(id, tipoVueloDTO)
            dao.save(listOf(tipoVueloDTO.toEntity().copy(isPendingSync = false)))
        } catch (e: Exception) {
            Log.e("TipoVueloRepository", "Error al actualizar remoto: ${e.message}")
            dao.save(listOf(tipoVueloDTO.toEntity().copy(isPendingSync = true)))
        }
    }

    fun find(id: Int): Flow<Resource<TipoVueloDTO>> = flow {
        emit(Resource.Loading())
        try {
            val tipoVuelo = dataSource.getTipoVuelo(id)
            dao.save(listOf(tipoVuelo.toEntity().copy(isPendingSync = false)))
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

    suspend fun saveTipoVuelo(tipoVueloDTO: TipoVueloDTO): Resource<TipoVueloDTO> {
        return try {
            val savedTipoVuelo = dataSource.postTipoVuelo(tipoVueloDTO)
            dao.save(listOf(savedTipoVuelo.toEntity().copy(isPendingSync = false)))
            Resource.Success(savedTipoVuelo)
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("TipoVueloRepository", "HttpException al guardar remoto: $errorMessage")
            val localTipoVuelo = tipoVueloDTO.copy(tipoVueloId = null)
            dao.save(listOf(localTipoVuelo.toEntity().copy(isPendingSync = true, vueloId = null)))
            Resource.Success(localTipoVuelo)
        } catch (e: Exception) {
            Log.e("TipoVueloRepository", "Exception al guardar remoto: ${e.message}")
            val localTipoVuelo = tipoVueloDTO.copy(tipoVueloId = null)
            dao.save(listOf(localTipoVuelo.toEntity().copy(isPendingSync = true, vueloId = null)))
            Resource.Success(localTipoVuelo)
        }
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
        descripcionTipoVuelo = this.descripcionTipoVuelo,
        isPendingSync = false
    )

    private fun TipoVueloEntity.toDto() = TipoVueloDTO(
        tipoVueloId = this.vueloId,
        nombreVuelo = this.nombreVuelo,
        descripcionTipoVuelo = this.descripcionTipoVuelo
    )
}