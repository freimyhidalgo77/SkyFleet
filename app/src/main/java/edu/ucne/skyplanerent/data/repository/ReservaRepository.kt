package edu.ucne.skyplanerent.data.repository

import edu.ucne.skyplanerent.data.local.dao.ReservaDao
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReservaRepository @Inject constructor(
    val reservaDao: ReservaDao

){
    suspend fun saveReserva(reserva: ReservaEntity) {
        if (reserva.reservaId != null) {
            reservaDao.update(reserva) // <- Actualizar si tiene ID
        } else {
            reservaDao.save(reserva) // <- Insertar si es nuevo
        }
    }

    suspend fun findReserva(Id:Int):ReservaEntity? = reservaDao.find(Id)

    suspend fun deleteReserva(reserva: ReservaEntity) = reservaDao.delete(reserva)

    suspend fun deleteReservaById(id:Int) = reservaDao.deleteReservaById(id)

    fun getAll(): Flow<List<ReservaEntity>> = reservaDao.getAll()

    fun getReservasByUserId(userId: String): Flow<List<ReservaEntity>> {
        return reservaDao.getReservasByUserId(userId)
    }

}