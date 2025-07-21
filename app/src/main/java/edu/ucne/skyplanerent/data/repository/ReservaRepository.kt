package edu.ucne.skyplanerent.data.repository

import edu.ucne.skyplanerent.data.local.dao.ReservaDao
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReservaRepository @Inject constructor(
    val reservaDao: ReservaDao

){
    suspend fun saveReserva(reserva: ReservaEntity) = reservaDao.save(reserva)

    suspend fun findReserva(Id:Int):ReservaEntity? = reservaDao.find(Id)

    suspend fun deleteReserva(reserva: ReservaEntity) = reservaDao.delete(reserva)

    fun getAll(): Flow<List<ReservaEntity>> = reservaDao.getAll()


}