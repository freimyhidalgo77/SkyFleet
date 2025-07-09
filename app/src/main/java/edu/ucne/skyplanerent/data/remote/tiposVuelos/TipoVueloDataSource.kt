package edu.ucne.skyplanerent.data.remote.tiposVuelos

import edu.ucne.skyplanerent.data.remote.aeronaves.AeronavesManagerApi
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import javax.inject.Inject


class TipoVueloDataSource @Inject constructor(
    private val tipoVueloManagerApi: TipoVueloManagerApi
){
    suspend fun getTiposVuelos() = tipoVueloManagerApi.getTiposVuelos()

    suspend fun getTipoVuelo(id: Int) = tipoVueloManagerApi.getTipoVuelo(id)

    suspend fun PostTipoVuelo(tipoVueloDTO: TipoVueloDTO) = tipoVueloManagerApi.saveTipoVuelo(tipoVueloDTO)

    suspend fun putTipoVuelo(id: Int, tipoVueloDTO: TipoVueloDTO) = tipoVueloManagerApi.actualizarTipoVuelo(id, tipoVueloDTO)

    suspend fun deleteTipoVuelo(id: Int) = tipoVueloManagerApi.deleteTipoVuelo(id)

}