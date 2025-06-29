package edu.ucne.skyplanerent.presentation.reserva

import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import java.util.Date

data class UiState (
    val reservaId: Int? = null,
    val estadoId:Int? = 0,
    val formularioId:Int? = 0,
    val metodoPagoId:Int? = 0,
    val tipoVueloId:Int? =0,
    val categoriaId:Int? = 0,
    val rutaId:Int? = 0,
    val fecha: Date? =  null,
    val impuesto: Double = 0.0,
    val tarifa:Double = 0.0,
    val pasajeros:Int? = 0,
    val successMessage:String? = "",
    val errorMessage:String? = "",
    val reservas:List<ReservaEntity> = emptyList()
)