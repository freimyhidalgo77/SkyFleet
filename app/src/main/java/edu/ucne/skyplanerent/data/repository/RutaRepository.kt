package edu.ucne.skyplanerent.data.repository

import android.util.Log
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.remote.rutas.RutasDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class RutaRepository @Inject constructor(
    private val dataSource: RutasDataSource
){
    fun getRutas(rutaid: Int): Flow<Resource<List<RutaDTO>>> = flow {
        try{
            emit(Resource.Loading())
            val ruta = dataSource.getRutas()
            emit(Resource.Success(ruta))
        }catch (e: HttpException){
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("Ruta", "HttpException: $errorMessage")
            emit(Resource.Error("Error de conexion $errorMessage"))
        }catch (e: Exception){
            Log.e("Ruta", "Exception: ${e.message}")
            emit(Resource.Error("Error: ${e.message}"))

        }
    }
    fun getRuta(): Flow<Resource<List<RutaDTO>>> = flow {
        try{
            emit(Resource.Loading())
            val ruta = dataSource.getRutas()
            emit(Resource.Success(ruta))
        }catch (e: HttpException){
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("Ruta", "HttpException: $errorMessage")
            emit(Resource.Error("Error de conexion $errorMessage"))
        }catch (e: Exception){
            Log.e("Ruta", "Exception: ${e.message}")
            emit(Resource.Error("Error: ${e.message}"))

        }
    }
    suspend fun update(id: Int, rutaDTO: RutaDTO) =
        dataSource.putRuta(id, rutaDTO)

    suspend fun find(id: Int) = dataSource.getRuta(id)

    suspend fun saveRuta(rutaDTO: RutaDTO) = dataSource.postRuta(rutaDTO)

    suspend fun delete(id: Int) = dataSource.deleteRuta(id)
}