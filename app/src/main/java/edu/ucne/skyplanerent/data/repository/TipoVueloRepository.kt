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
    private val dataSource: TipoVueloDataSource
){
    fun getTipoVuelos(): Flow<Resource<List<TipoVueloDTO>>> = flow {
        try{
            emit(Resource.Loading())
            val tiposVuelos= dataSource.getTiposVuelos()
            emit(Resource.Success(tiposVuelos))
        }catch (e: HttpException){
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("TipoVueloRepository", "HttpException: $errorMessage")
            emit(Resource.Error("Error de conexion $errorMessage"))
        }catch (e: Exception){
            Log.e("TipoVueloRepository", "Exception: ${e.message}")
            emit(Resource.Error("Error: ${e.message}"))

        }
    }
    suspend fun update(id: Int, tipoVueloDTO: TipoVueloDTO) =
        dataSource.putTipoVuelo(id, tipoVueloDTO)

    suspend fun find(id: Int) = dataSource.getTipoVuelo(id)

    suspend fun save(tipoVueloDTO: TipoVueloDTO) = dataSource.postTipoVuelo(tipoVueloDTO)

    suspend fun delete(id: Int) = dataSource.deleteTipoVuelo(id)
}