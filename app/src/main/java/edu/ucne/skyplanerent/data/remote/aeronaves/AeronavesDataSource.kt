package edu.ucne.skyplanerent.data.remote.aeronaves

import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import javax.inject.Inject


class AeronavesDataSource @Inject constructor(
    private val aeronaveManagerApi: AeronavesManagerApi
){
    suspend fun getAeronaves() = aeronaveManagerApi.getAeronaves()

    suspend fun getAeronave(id: Int) = aeronaveManagerApi.getAeronave(id)

    suspend fun PostAeronave(aeronaveDto: AeronaveDTO) = aeronaveManagerApi.saveAeronave(aeronaveDto)

    suspend fun putAeronave(id: Int, aeronaveDTO: AeronaveDTO) = aeronaveManagerApi.actualizarAeronave(id, aeronaveDTO)

    suspend fun deleteAeronave(id: Int) = aeronaveManagerApi.deleteAeronave(id)

}