package edu.ucne.skyplanerent.data.repository

import edu.ucne.skyplanerent.data.local.dao.UserDao
import edu.ucne.skyplanerent.data.local.entity.UserRegisterAccount
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UserRepository @Inject constructor(private val userDao: UserDao) {
    suspend fun insertUser(user: UserRegisterAccount) = userDao.insert(user)
    suspend fun getUserByEmail(email: String): UserRegisterAccount? = userDao.getUserByEmail(email)
    fun getAllUsers(): Flow<List<UserRegisterAccount>> = userDao.getAll()
}