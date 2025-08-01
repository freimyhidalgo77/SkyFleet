package edu.ucne.skyplanerent.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.skyplanerent.data.local.entity.AdminEntity

@Dao
interface AdminDao {
    @Upsert
    suspend fun save(admin: List<AdminEntity>)

    @Query("SELECT * FROM Admin WHERE adminId = :id LIMIT 1")
    suspend fun find(id: Int): AdminEntity?

    @Query("SELECT * FROM Admin WHERE correo = :email LIMIT 1")
    suspend fun findByEmail(email: String): AdminEntity?

    @Query("DELETE FROM Admin WHERE adminId = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM Admin")
    suspend fun getAll(): List<AdminEntity>

    @Query("SELECT * FROM Admin WHERE isPendingSync = 1")
    suspend fun getPendingSync(): List<AdminEntity>

    @Query("DELETE FROM Admin WHERE isPendingSync = 1 AND (adminId IS :id OR adminId = 0)")
    suspend fun deletePending(id: Int?)

    @Query("DELETE FROM Admin WHERE adminId = 0")
    suspend fun clearInvalidAdmins()
}