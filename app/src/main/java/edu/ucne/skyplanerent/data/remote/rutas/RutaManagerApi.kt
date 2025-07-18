package edu.ucne.skyplanerent.data.remote.rutas

import com.google.android.gms.common.api.Response
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RutaManagerApi {

    @GET("api/Ruta")
    suspend fun getRutas(): List<RutaDTO>

    @GET("api/Ruta/{id}")
    suspend fun getRuta(@Path("id")id: Int): RutaDTO

    @POST("api/Ruta")
    suspend fun saveRuta(@Body rutaDTO: RutaDTO): RutaDTO

    @PUT("api/Ruta/{id}")
    suspend fun actualizarRuta(
        @Path("id") rutaId: Int,
        @Body rutaDTO: RutaDTO
    ): RutaDTO

    @DELETE("api/Ruta/{id}")
    suspend fun deleteRuta(@Path("id") id: Int)
}