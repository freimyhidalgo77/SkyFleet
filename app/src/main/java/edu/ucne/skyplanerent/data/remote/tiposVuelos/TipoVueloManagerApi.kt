package edu.ucne.skyplanerent.data.remote.tiposVuelos

import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface TipoVueloManagerApi {

    @GET("api/TipoVueloControllers")
    suspend fun getTiposVuelos(): List<TipoVueloDTO>

    @GET("api/TipoVueloControllers/{id}")
    suspend fun getTipoVuelo(@Path("id")id: Int): TipoVueloDTO

    @POST("api/TipoVueloControllers")
    suspend fun saveTipoVuelo(@Body tipoVueloDTO: TipoVueloDTO): TipoVueloDTO

    @PUT("api/TipoVueloControllers/{id}")
    suspend fun actualizarTipoVuelo(
        @Path("id") tipoVueloId: Int,
        @Body tipoVueloDTO: TipoVueloDTO
    ): TipoVueloDTO

    @DELETE("api/TipoVueloControllers/{id}")
    suspend fun deleteTipoVuelo(@Path("id") id: Int): ResponseBody

}