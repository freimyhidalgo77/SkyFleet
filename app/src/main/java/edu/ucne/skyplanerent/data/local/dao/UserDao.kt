package edu.ucne.skyplanerent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.ucne.skyplanerent.data.local.entity.UserRegisterAccount
import kotlinx.coroutines.flow.Flow

// UserRegisterAccountDao.kt
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserRegisterAccount)

    @Query("SELECT * FROM UsersAccounts WHERE correo = :email")
    suspend fun getUserByEmail(email: String): UserRegisterAccount?

    @Query("SELECT * FROM UsersAccounts")
    fun getAll(): Flow<List<UserRegisterAccount>>
}