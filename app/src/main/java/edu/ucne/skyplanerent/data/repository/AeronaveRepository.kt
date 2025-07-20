package edu.ucne.skyplanerent.data.repository

import android.util.Log
import edu.ucne.skyplanerent.data.local.dao.AeronaveDao
import edu.ucne.skyplanerent.data.local.entity.AeronaveEntity
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.aeronaves.AeronavesDataSource
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class AeronaveRepository @Inject constructor(
    private val dataSource: AeronavesDataSource,
    private val dao: AeronaveDao // Agregamos el DAO
) {
    fun getAeronave(aeronaveId: Int): Flow<Resource<List<AeronaveDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val aeronave = dataSource.getAeronave(aeronaveId)
            dao.save(listOf(aeronave.toEntity())) // Guardar localmente
            emit(Resource.Success(listOf(aeronave)))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("Aeronave", "HttpException: $errorMessage")
            val localAeronave = dao.find(aeronaveId)
            if (localAeronave != null) {
                emit(Resource.Success(listOf(localAeronave.toDto())))
            } else {
                emit(Resource.Error("Error de conexión: $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e("Aeronave", "Exception: ${e.message}")
            val localAeronave = dao.find(aeronaveId)
            if (localAeronave != null) {
                emit(Resource.Success(listOf(localAeronave.toDto())))
            } else {
                emit(Resource.Error("Error: ${e.message}"))
            }
        }
    }

    fun getAeronaves(): Flow<Resource<List<AeronaveDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val aeronavesFetched = dataSource.getAeronaves()
            val listaEntity = aeronavesFetched.map { it.toEntity() }
            dao.save(listaEntity) // Guardar localmente
        } catch (e: Exception) {
            Log.e("Aeronave", "Exception al obtener aeronaves: ${e.message}")
        }
        val listaRetorno = dao.getAll()
        val listaDto = listaRetorno.map { it.toDto() }
        emit(Resource.Success(listaDto))
    }

    suspend fun update(id: Int, aeronaveDTO: AeronaveDTO) {
        try {
            dataSource.putAeronave(id, aeronaveDTO)
        } catch (e: Exception) {
            Log.e("Aeronave", "Error al actualizar remoto: ${e.message}")
        }
        dao.save(listOf(aeronaveDTO.toEntity())) // Guardar localmente
    }

    fun find(id: Int): Flow<Resource<AeronaveDTO>> = flow {
        emit(Resource.Loading())
        try {
            val aeronave = dataSource.getAeronave(id)
            dao.save(listOf(aeronave.toEntity())) // Actualiza localmente
            emit(Resource.Success(aeronave))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("Aeronave", "HttpException: $errorMessage")
            val localAeronave = dao.find(id)
            if (localAeronave != null) {
                emit(Resource.Success(localAeronave.toDto()))
            } else {
                emit(Resource.Error("Error de conexión: $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e("Aeronave", "Exception: ${e.message}")
            val localAeronave = dao.find(id)
            if (localAeronave != null) {
                emit(Resource.Success(localAeronave.toDto()))
            } else {
                emit(Resource.Error("Error: ${e.message}"))
            }
        }
    }

    suspend fun saveAeronave(aeronaveDTO: AeronaveDTO) {
        try {
            dataSource.PostAeronave(aeronaveDTO)
        } catch (e: Exception) {
            Log.e("Aeronave", "Error al guardar remoto: ${e.message}")
        }
        dao.save(listOf(aeronaveDTO.toEntity())) // Guardar localmente
    }

    suspend fun deleteAeronave(id: Int): Resource<Unit> {
        return try {
            dataSource.deleteAeronave(id)
            dao.delete(id)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("Aeronave", "HttpException: $errorMessage")
            dao.delete(id)
            Resource.Error("Error de conexión: $errorMessage")
        } catch (e: Exception) {
            Log.e("Aeronave", "Exception: ${e.message}")
            dao.delete(id)
            Resource.Error("Error desconocido: ${e.message}")
        }
    }

    private fun AeronaveDTO.toEntity() = AeronaveEntity(
        AeronaveId = this.aeronaveId,
        estadoId = this.estadoId ?: 0,
        ModeloAvion = this.modeloAvion,
        DescripcionCategoria = this.descripcionCategoria,
        Registracion = this.registracion,
        CostoXHora = this.costoXHora ?: 0.0,
        DescripcionAeronave = this.descripcionAeronave,
        VelocidadMaxima = this.velocidadMaxima ?: 0.0,
        DescripcionMotor = this.descripcionMotor,
        CapacidadCombustible = this.capacidadCombustible,
        ConsumoXHora = this.consumoXHora,
        Peso = this.peso ?: 0.0,
        Rango = this.rango,
        CapacidadPasajeros = this.capacidadPasajeros,
        AltitudMaxima = this.altitudMaxima,
        Licencia = this.licencia
    )

    private fun AeronaveEntity.toDto() = AeronaveDTO(
        aeronaveId = this.AeronaveId,
        estadoId = this.estadoId ?: 0,
        modeloAvion = this.ModeloAvion,
        descripcionCategoria = this.DescripcionCategoria,
        registracion = this.Registracion,
        costoXHora = this.CostoXHora,
        descripcionAeronave = this.DescripcionAeronave,
        velocidadMaxima = this.VelocidadMaxima,
        descripcionMotor = this.DescripcionMotor,
        capacidadCombustible = this.CapacidadCombustible,
        consumoXHora = this.ConsumoXHora,
        peso = this.Peso,
        rango = this.Rango,
        capacidadPasajeros = this.CapacidadPasajeros,
        altitudMaxima = this.AltitudMaxima,
        licencia = this.Licencia
    )
}