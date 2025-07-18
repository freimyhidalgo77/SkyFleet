package edu.ucne.skyplanerent.data.remote.aeronaves

import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface AeronavesManagerApi {

    @GET("api/Aeronave")
    suspend fun getAeronaves(): List<AeronaveDTO>

    @GET("api/Aeronave/{id}")
    suspend fun getAeronave(@Path("id")id: Int): AeronaveDTO

    @POST("api/Aeronave")
    suspend fun saveAeronave(@Body aeronaveDto: AeronaveDTO?): AeronaveDTO

    @PUT("api/Aeronave/{id}")
    suspend fun actualizarAeronave(
        @Path("id") aeronaevId: Int,
        @Body aeronaveDto: AeronaveDTO
    ): AeronaveDTO

    @DELETE("api/Aeronave/{id}")
    suspend fun deleteAeronave(@Path("id") id: Int)

}