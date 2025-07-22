package edu.ucne.skyplanerent.presentation.reserva

import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import java.util.Date

data class UiState (
    // Datos persistentes
    val reservaId: Int? = null,
    val estadoId: Int? = 0,
    val formularioId: Int? = 0,
    val metodoPagoId: Int? = 0,
    val tipoVueloId: Int? = 0,
    val categoriaId: Int? = 0,
    val rutaId: Int? = 0,
    val fecha: Date? = null,
    val impuesto: Double = 0.0,
    val tarifa: Double = 0.0,
    val pasajeros: Int? = 0,
    val precioTotal: Double = 0.0,
    val tipoCliente: Boolean? = false,
    val licenciaPiloto: String? = "",

    // UI extras para mostrar descripciones
    val tipoVueloSeleccionado: TipoVueloDTO? = null,
    val rutaSeleccionada: RutaDTO? = null,
    val aeronaveSeleccionada: AeronaveDTO? = null,

    // UI control
    val successMessage: String? = "",
    val errorMessage: String? = "",
    val reservaSeleccionada: ReservaEntity? = null,
    val reservas: List<ReservaEntity> = emptyList()
)
