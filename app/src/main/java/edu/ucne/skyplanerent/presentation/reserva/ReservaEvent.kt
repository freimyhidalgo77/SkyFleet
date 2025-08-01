package edu.ucne.skyplanerent.presentation.reserva

import java.util.Date

sealed interface ReservaEvent{

    data class AeronaveChange(val aeronaveId: Int) : ReservaEvent
    data class FechaChange(val fecha: Date?) : ReservaEvent
    data class PasajeroChange(val pasajero: Int) : ReservaEvent
    data class RutaChange(val rutaId: Int) : ReservaEvent
    data object save : ReservaEvent
    data object Delte : ReservaEvent
    //data object Nuevo : ReservaEvent

}
