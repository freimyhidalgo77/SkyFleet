package edu.ucne.skyplanerent.data.repository

import edu.ucne.skyplanerent.data.local.dao.ReservaDao
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReservaRepository @Inject constructor(
    val reservaDao: ReservaDao

){
    //Mejorando metodo para evaluar si se ha actualizado una reserva o si solo se va a guardar
    suspend fun saveReserva(reserva: ReservaEntity) {
        if (reserva.reservaId != null) {
            reservaDao.update(reserva) // <- Actualizar si tiene ID
        } else {
            reservaDao.save(reserva) // <- Insertar si es nuevo
        }
    }

    suspend fun findReserva(id:Int):ReservaEntity? = reservaDao.find(id)

    suspend fun deleteReserva(reserva: ReservaEntity) = reservaDao.delete(reserva)

    fun getAll(): Flow<List<ReservaEntity>> = reservaDao.getAll()

    //Metodo para filtrado de reserva por id de usuario
    fun getReservasByUserId(userId: String): Flow<List<ReservaEntity>> {
        return reservaDao.getReservasByUserId(userId)
    }

    // En ReservaRepository.kt
    /* suspend fun actualizarEstadoPago(reservaId: Int, metodoPago: String, comprobante: String?) {
        reservaDao.actualizarEstadoPago(reservaId, metodoPago, comprobante)
    }*/

}