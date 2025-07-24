package edu.ucne.skyplanerent.presentation.reserva

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.data.repository.ReservaRepository
import edu.ucne.skyplanerent.data.repository.RutaRepository
import edu.ucne.skyplanerent.data.repository.TipoVueloRepository
import edu.ucne.skyplanerent.presentation.UiEvent
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaEvent
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.toEntity
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoLicencia
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReservaViewModel @Inject constructor(

    private val reservaRepository: ReservaRepository,
    private val tipoRutaRepository: TipoVueloRepository,
    private val rutaRepository: RutaRepository

): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState get() = _uiState.asStateFlow()

    //Seleccionar ruta de vuelo
    private val _rutaSeleccionadaId = MutableStateFlow<Int?>(null)
    val rutaSeleccionadaId: StateFlow<Int?> = _rutaSeleccionadaId

    fun seleccionarRuta(rutaId: Int) {
        _rutaSeleccionadaId.value = rutaId
    }

    //Seleccionar tipo de vuelo
    private val _tipoVueloSeleccionadoId = MutableStateFlow<Int?>(null)
    val tipoVueloSeleccionadoId: StateFlow<Int?> = _tipoVueloSeleccionadoId

    fun seleccionarTipoVuelo(tipoVueloId: Int) {
        _tipoVueloSeleccionadoId.value = tipoVueloId
    }
    //Seleciconar tipo aeronave
    private val _tipoAeronaveSeleccionadaId = MutableStateFlow<Int?>(null)
    val tipoAeronaveSeleccionadaId: StateFlow<Int?> = _tipoAeronaveSeleccionadaId

    fun seleccionarTipoAeronave(tipoAeronaveId: Int) {
        _tipoAeronaveSeleccionadaId.value = tipoAeronaveId
    }

    //Seleccionar fecha
    private val _fechaSeleccionada = MutableStateFlow<Date?>(null)
    val fechaSeleccionada: StateFlow<Date?> = _fechaSeleccionada

    fun seleccionarFecha(fecha: Date) {
        _fechaSeleccionada.value = fecha
    }

    //Seleccionar tipo cliente
    private val _tipoCliente = MutableStateFlow<Boolean?>(null)
    val tipoCliente: StateFlow<Boolean?> = _tipoCliente

    fun seleccionarTipoCliente(valor: Boolean) {
        _tipoCliente.value = valor
    }

    fun seleccionarLicenciaPiloto(licencia: TipoLicencia) {
        _uiState.update { it.copy(licenciaPiloto = licencia) }
    }

    fun categoriaIdChange(id: Int) {
        _uiState.update {
            it.copy(categoriaId = id)
        }

    }

    fun onChangeRuta(rutaId: Int) {
        _uiState.update { it.copy(rutaId = rutaId) }
    }



    init{
        getReserva()
    }


    /*fun seleccionarTipoVuelo(tipoVueloId: Int, tipoVueloDTO: TipoVueloDTO) {
       _tipoVueloSeleccionadoId.value = tipoVueloId
       _uiState.update { it.copy(tipoVueloSeleccionado = tipoVueloDTO) }
   }*/



    /* fun onEvent(event: ReservaEvent) {
         when (event) {
             is ReservaEvent.AeronaveChange -> {
             }
             is ReservaEvent.FechaChange -> {
                 _uiState.update {
                     it.copy(fecha = event.fecha)
                 }
             }
             is ReservaEvent.PasajeroChange -> {
                 onChangePasajeros(event.pasajero)
             }
             is ReservaEvent.RutaChange -> {
                 seleccionarRuta(event.rutaId)
             }
             ReservaEvent.Delte -> deleteReserva()
             ReservaEvent.save -> guardarReserva()
         }
     }*/



    fun getReserva(){
        viewModelScope.launch {
            reservaRepository.reservaDao.getAll().collect{reserva->
                _uiState.update {
                    it.copy(reservas = reserva)
                }
            }
        }
    }

    fun updateReserva() {
        viewModelScope.launch {
            try {
                val reserva = uiState.value.toEntity()
                reservaRepository.saveReserva(reserva)
                _uiState.update {
                    it.copy(successMessage = "Reserva actualizada correctamente")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Error al actualizar la reserva")
                }
            }
        }
    }



    fun guardarReserva(
        rutaId: Int,
        tipoVueloId: Int,
        aeronaveId: Int,
        tarifaBase: Double,
        impuesto: Double,
        precioTotal: Double,
        tipoCliente: Boolean?,
        pasajero: Int
    ) {
        viewModelScope.launch {
            val fecha = _fechaSeleccionada.value
            if (fecha == null) {
                _uiState.update {
                    it.copy(errorMessage = "Debe seleccionar una fecha válida.")
                }
                return@launch
            }

            val reserva = ReservaEntity(
                rutaId = rutaId,
                tipoVueloId = tipoVueloId,
                categoriaId = aeronaveId,
                fecha = fecha, // ✅ Se usa la fecha seleccionada correctamente
                tarifa = tarifaBase,
                impuesto = impuesto,
                tipoCliente = tipoCliente,
                precioTotal = precioTotal,
                pasajeros = pasajero
            )

            reservaRepository.saveReserva(reserva)

            _uiState.update {
                it.copy(successMessage = "Reserva guardada con éxito.", errorMessage = null)
            }
        }
    }


    fun deleteReserva() {
        viewModelScope.launch {
            try {
                val reserva = _uiState.value.reservas.firstOrNull { it.reservaId == _uiState.value.reservaId }
                if (reserva != null) {
                    reservaRepository.deleteReserva(reserva)
                    _uiState.update {
                        it.copy(successMessage = "Reserva eliminada!", errorMessage = null)
                    }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "No se encontró la reserva para eliminar.")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Ha ocurrido un error al eliminar la reserva.")
                }
            }
        }
    }

    fun getReservaPorId(id: Int): ReservaEntity? {
        return uiState.value.reservas.find { it.reservaId == id }
    }




    /*fun nuevaReserva(){
        _uiState.update {
            it.copy(
                reservaId = null,

            )
        }

    }*/

    fun selectReserva(reservaId: Int) {
        viewModelScope.launch {
            try {
                val reserva = reservaRepository.findReserva(reservaId)
                if (reserva != null) {
                    println("Reserva encontrada: $reserva")

                    _uiState.update {
                        it.copy(
                            reservaId = reserva.reservaId,
                            rutaId = reserva.rutaId,
                            estadoId = reserva.estadoId,
                            formularioId = reserva.formularioId,
                            metodoPagoId = reserva.metodoPagoId,
                            tipoVueloId = reserva.tipoVueloId,
                            categoriaId = reserva.categoriaId,
                            pasajeros = reserva.pasajeros,
                            fecha = reserva.fecha,
                            impuesto = reserva.impuesto ?: 0.0,
                            tarifa = reserva.tarifa ?: 0.0,
                            reservaSeleccionada = reserva
                        )
                    }
                } else {
                    println("No se encontró la reserva con ID: $reservaId")
                }
            } catch (e: Exception) {
                println("Error al buscar reserva: ${e.message}")
            }
        }
    }



    fun nuevaReserva(){
        _uiState.update {
            it.copy(
                successMessage = null, errorMessage = null
            )
        }
    }

    /*fun onChangeAreonave(aeronaves:String){
        _uiState.update {
            it.copy(
                aeronave = aeronaves
            )
        }
    }*/

    /*fun onChangeFecha(Fecha:String){
        _uiState.update {
            it.copy(
                descripcion = descripcion
            )
        }
    }*/

    fun onChangePasajeros(pasajero:Int){
        _uiState.update {
            it.copy(
                pasajeros = pasajero
            )
        }

    }

    /*fun onChangeRuta(ruta:Int){
        _uiState.update {
            it.copy(
                rutaId = ruta
            )
        }

    }*/

    fun UiState.toEntity() = ReservaEntity(
        reservaId = reservaId,
        rutaId = rutaId,
        estadoId = estadoId,
        formularioId = formularioId,
        metodoPagoId = metodoPagoId,
        tipoVueloId = tipoVueloId,
        categoriaId = categoriaId,
        fecha = fecha,
        impuesto = impuesto?:0.0,
        tarifa = tarifa?:0.0,
        precioTotal = precioTotal,
        tipoCliente = tipoCliente?:false,
        pasajeros = pasajeros,

        )

}