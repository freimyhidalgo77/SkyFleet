package edu.ucne.skyplanerent.data.remote.admins

import javax.inject.Inject

class AdminsDataSource @Inject constructor(
    private val adminManagerApi: AdminManagerApi
) {
    suspend fun getAdmins() = adminManagerApi.getAdmins()

    suspend fun getAdmin(id: Int) = adminManagerApi.getAdmin(id)
}