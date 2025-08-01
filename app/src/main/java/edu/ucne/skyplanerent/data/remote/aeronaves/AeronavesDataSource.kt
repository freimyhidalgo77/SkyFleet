package edu.ucne.skyplanerent.data.remote.aeronaves

import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import javax.inject.Inject


class AeronavesDataSource @Inject constructor(
    private val aeronaevManagerApi: AeronavesManagerApi
){
    suspend fun getAeronaves() = aeronaevManagerApi.getAeronaves()

    suspend fun getAeronave(id: Int) = aeronaevManagerApi.getAeronave(id)

    suspend fun PostAeronave(aeronaveDto: AeronaveDTO) = aeronaevManagerApi.saveAeronave(aeronaveDto)

    suspend fun putAeronave(id: Int, aeronaveDTO: AeronaveDTO) = aeronaevManagerApi.actualizarAeronave(id, aeronaveDTO)

    suspend fun deleteAeronave(id: Int) = aeronaevManagerApi.deleteAeronave(id)

}