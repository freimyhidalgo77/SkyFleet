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
    private val dao: AeronaveDao
) {
    // Separador único para embeber imagePath en descripcionAeronave
    private val IMAGE_PATH_SEPARATOR = "|IMG_PATH:"

    // Extrae imagePath y descripción limpia de descripcionAeronave
    private fun extractImagePathAndDescription(descripcion: String?): Pair<String?, String?> {
        if (descripcion.isNullOrBlank()) return Pair(null, null)
        return if (descripcion.contains(IMAGE_PATH_SEPARATOR)) {
            val parts = descripcion.split(IMAGE_PATH_SEPARATOR)
            Pair(parts.last().takeIf { it.isNotBlank() }, parts.first().takeIf { it.isNotBlank() })
        } else {
            Pair(null, descripcion)
        }
    }

    // Combina descripción y imagePath en un solo string
    private fun combineDescriptionAndImagePath(descripcion: String?, imagePath: String?): String? {
        return when {
            descripcion.isNullOrBlank() && imagePath.isNullOrBlank() -> null
            descripcion.isNullOrBlank() -> "$IMAGE_PATH_SEPARATOR$imagePath"
            imagePath.isNullOrBlank() -> descripcion
            else -> "$descripcion$IMAGE_PATH_SEPARATOR$imagePath"
        }
    }

    fun getAeronave(aeronaveId: Int): Flow<Resource<List<AeronaveDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val aeronave = dataSource.getAeronave(aeronaveId)
            val (imagePath, cleanDescription) = extractImagePathAndDescription(aeronave.descripcionAeronave)
            val aeronaveWithImage = aeronave.copy(
                descripcionAeronave = cleanDescription.toString(),
                imagePath = imagePath ?: dao.find(aeronaveId)?.imagePath
            )
            dao.save(listOf(aeronaveWithImage.toEntity().copy(isPendingSync = false)))
            emit(Resource.Success(listOf(aeronaveWithImage)))
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
            // Limpiar aeronaves inválidas con AeronaveId = 0
            dao.clearInvalidAeronaves()

            // Sincronizar aeronaves pendientes
            val pendingAeronaves = dao.getPendingSync()
            for (pending in pendingAeronaves) {
                try {
                    val dto = pending.toDto()
                    val combinedDescription = combineDescriptionAndImagePath(dto.descripcionAeronave, dto.imagePath)
                    val dtoWithoutImage = dto.copy(descripcionAeronave = combinedDescription.toString(), imagePath = null)
                    if (pending.AeronaveId != null && pending.AeronaveId > 0) {
                        // Actualizar aeronave existente
                        val updatedAeronave = dataSource.putAeronave(pending.AeronaveId, dtoWithoutImage)
                        dao.deletePending(pending.AeronaveId)
                        val (imagePath, cleanDescription) = extractImagePathAndDescription(updatedAeronave.descripcionAeronave)
                        dao.save(listOf(updatedAeronave.toEntity().copy(
                            isPendingSync = false,
                            imagePath = imagePath ?: pending.imagePath,
                            DescripcionAeronave = cleanDescription ?: updatedAeronave.descripcionAeronave
                        )))
                    } else {
                        // Crear nueva aeronave
                        val savedAeronave = dataSource.PostAeronave(dtoWithoutImage)
                        dao.deletePending(pending.AeronaveId)
                        val (imagePath, cleanDescription) = extractImagePathAndDescription(savedAeronave.descripcionAeronave)
                        dao.save(listOf(savedAeronave.toEntity().copy(
                            isPendingSync = false,
                            imagePath = imagePath ?: pending.imagePath,
                            DescripcionAeronave = cleanDescription ?: savedAeronave.descripcionAeronave
                        )))
                    }
                } catch (e: Exception) {
                    Log.e("Aeronave", "Error al sincronizar aeronave ${pending.AeronaveId}: ${e.message}")
                }
            }

            // Obtener aeronaves del servidor
            val aeronavesFetched = dataSource.getAeronaves()
            // Actualizar solo las aeronaves no pendientes localmente
            val listaEntity = aeronavesFetched.map { aeronave ->
                val localAeronave = dao.find(aeronave.aeronaveId ?: 0)
                val (imagePath, cleanDescription) = extractImagePathAndDescription(aeronave.descripcionAeronave)
                aeronave.toEntity().copy(
                    isPendingSync = false,
                    imagePath = imagePath ?: localAeronave?.imagePath,
                    DescripcionAeronave = cleanDescription ?: aeronave.descripcionAeronave
                )
            }
            dao.save(listaEntity)

            // Emitir aeronaves locales actualizadas
            val listaRetorno = dao.getAll()
            val listaDto = listaRetorno.map { it.toDto() }
            emit(Resource.Success(listaDto))
        } catch (e: Exception) {
            Log.e("Aeronave", "Exception al obtener aeronaves: ${e.message}")
            val listaRetorno = dao.getAll()
            val listaDto = listaRetorno.map { it.toDto() }
            emit(Resource.Success(listaDto))
        }
    }

    suspend fun update(id: Int, aeronaveDTO: AeronaveDTO) {
        try {
            // Combinar descripción e imagePath para enviar al servidor
            val combinedDescription = combineDescriptionAndImagePath(aeronaveDTO.descripcionAeronave, aeronaveDTO.imagePath)
            val dtoWithoutImage = aeronaveDTO.copy(descripcionAeronave = combinedDescription.toString(), imagePath = null)
            val updatedAeronave = dataSource.putAeronave(id, dtoWithoutImage)
            val (imagePath, cleanDescription) = extractImagePathAndDescription(updatedAeronave.descripcionAeronave)
            dao.save(listOf(updatedAeronave.toEntity().copy(
                isPendingSync = false,
                imagePath = imagePath ?: aeronaveDTO.imagePath,
                DescripcionAeronave = cleanDescription ?: updatedAeronave.descripcionAeronave
            )))
        } catch (e: Exception) {
            Log.e("Aeronave", "Error al actualizar remoto: ${e.message}")
            val combinedDescription = combineDescriptionAndImagePath(aeronaveDTO.descripcionAeronave, aeronaveDTO.imagePath)
            dao.save(listOf(aeronaveDTO.toEntity().copy(
                isPendingSync = true,
                DescripcionAeronave = combinedDescription.toString()
            )))
        }
    }

    fun find(id: Int): Flow<Resource<AeronaveDTO>> = flow {
        emit(Resource.Loading())
        try {
            val aeronave = dataSource.getAeronave(id)
            val localAeronave = dao.find(id)
            val (imagePath, cleanDescription) = extractImagePathAndDescription(aeronave.descripcionAeronave)
            val aeronaveWithImage = aeronave.copy(
                descripcionAeronave = cleanDescription ?: aeronave.descripcionAeronave,
                imagePath = imagePath ?: localAeronave?.imagePath
            )
            dao.save(listOf(aeronaveWithImage.toEntity().copy(isPendingSync = false)))
            emit(Resource.Success(aeronaveWithImage))
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

    suspend fun saveAeronave(aeronaveDTO: AeronaveDTO): Resource<AeronaveDTO> {
        return try {
            // Combinar descripción e imagePath para enviar al servidor
            val combinedDescription = combineDescriptionAndImagePath(aeronaveDTO.descripcionAeronave, aeronaveDTO.imagePath)
            val dtoWithoutImage = aeronaveDTO.copy(descripcionAeronave = combinedDescription.toString(), imagePath = null)
            val savedAeronave = dataSource.PostAeronave(dtoWithoutImage)
            val (imagePath, cleanDescription) = extractImagePathAndDescription(savedAeronave.descripcionAeronave)
            val savedEntity = savedAeronave.toEntity().copy(
                isPendingSync = false,
                imagePath = imagePath ?: aeronaveDTO.imagePath,
                DescripcionAeronave = cleanDescription ?: savedAeronave.descripcionAeronave
            )
            dao.save(listOf(savedEntity))
            Resource.Success(savedAeronave.copy(
                imagePath = imagePath ?: aeronaveDTO.imagePath,
                descripcionAeronave = cleanDescription ?: savedAeronave.descripcionAeronave
            ))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e("Aeronave", "HttpException al guardar remoto: $errorMessage")
            val combinedDescription = combineDescriptionAndImagePath(aeronaveDTO.descripcionAeronave, aeronaveDTO.imagePath)
            dao.save(listOf(aeronaveDTO.toEntity().copy(
                isPendingSync = true,
                DescripcionAeronave = combinedDescription.toString()
            )))
            Resource.Success(aeronaveDTO)
        } catch (e: Exception) {
            Log.e("Aeronave", "Exception al guardar remoto: ${e.message}")
            val combinedDescription = combineDescriptionAndImagePath(aeronaveDTO.descripcionAeronave, aeronaveDTO.imagePath)
            dao.save(listOf(aeronaveDTO.toEntity().copy(
                isPendingSync = true,
                DescripcionAeronave = combinedDescription.toString()
            )))
            Resource.Success(aeronaveDTO)
        }
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

    suspend fun cleanInvalidAeronaves() {
        dao.clearInvalidAeronaves()
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
        Licencia = this.licencia,
        imagePath = this.imagePath,
        isPendingSync = false
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
        licencia = this.Licencia,
        imagePath = this.imagePath
    )
}