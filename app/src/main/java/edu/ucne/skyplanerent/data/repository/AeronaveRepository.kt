package edu.ucne.skyplanerent.data.repository

import android.util.Log
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.aeronaves.AeronavesDataSource
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject



class AeronaveRepository @Inject constructor(
    private val dataSource: AeronavesDataSource
){
    fun getAeronave(aeronaveid: Int): Flow<Resource<List<AeronaveDTO>>> = flow {
        try{
            emit(Resource.Loading())
            val aeronave = dataSource.getAeronaves()
            emit(Resource.Success(aeronave))
        }catch (e: HttpException){
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("Aeronave", "HttpException: $errorMessage")
            emit(Resource.Error("Error de conexion $errorMessage"))
        }catch (e: Exception){
            Log.e("Aeronave", "Exception: ${e.message}")
            emit(Resource.Error("Error: ${e.message}"))

        }
    }
    fun getAeronaves(): Flow<Resource<List<AeronaveDTO>>> = flow {
        try{
            emit(Resource.Loading())
            val aeronave = dataSource.getAeronaves()
            emit(Resource.Success(aeronave))
        }catch (e: HttpException){
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("Aeronave", "HttpException: $errorMessage")
            emit(Resource.Error("Error de conexion $errorMessage"))
        }catch (e: Exception){
            Log.e("Aeronave", "Exception: ${e.message}")
            emit(Resource.Error("Error: ${e.message}"))

        }
    }
    suspend fun update(id: Int, aeronaveDTO: AeronaveDTO) =
        dataSource.putAeronave(id, aeronaveDTO)

    suspend fun find(id: Int) = dataSource.getAeronave(id)

    suspend fun saveAeronave(aeronaveDTO: AeronaveDTO) = dataSource.PostAeronave(aeronaveDTO)

    suspend fun deleteAeronave(id: Int) = dataSource.deleteAeronave(id)
}