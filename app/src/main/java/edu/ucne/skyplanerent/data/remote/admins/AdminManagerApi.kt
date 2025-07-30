package edu.ucne.skyplanerent.data.remote.admins

import edu.ucne.skyplanerent.data.remote.dto.AdminDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface AdminManagerApi {

    @GET("api/AdminControllers")
    suspend fun getAdmins(): List<AdminDTO>

    @GET("api/AdminControllers/{id}")
    suspend fun getAdmin(@Path("id")id: Int): AdminDTO

}