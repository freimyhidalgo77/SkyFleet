package edu.ucne.skyplanerent.data.remote.rutas

import edu.ucne.skyplanerent.data.remote.aeronaves.AeronavesManagerApi
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import javax.inject.Inject


class RutasDataSource @Inject constructor(
    private val rutaManagerApi: RutaManagerApi
){
    suspend fun getRutas() = rutaManagerApi.getRutas()

    suspend fun getRuta(id: Int) = rutaManagerApi.getRuta(id)

    suspend fun postRuta(rutaDTO: RutaDTO) = rutaManagerApi.saveRuta(rutaDTO)

    suspend fun putRuta(id: Int, rutaDTO: RutaDTO) = rutaManagerApi.actualizarRuta(id, rutaDTO)

    suspend fun deleteRuta(id: Int) = rutaManagerApi.deleteRuta(id)

}