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

    private val _rutaSeleccionadaId = MutableStateFlow<Int?>(null)
    val rutaSeleccionadaId: StateFlow<Int?> = _rutaSeleccionadaId

    fun seleccionarRuta(rutaId: Int) {
        _rutaSeleccionadaId.value = rutaId
    }

    private val _tipoVueloSeleccionadoId = MutableStateFlow<Int?>(null)
    val tipoVueloSeleccionadoId: StateFlow<Int?> = _tipoVueloSeleccionadoId

    fun seleccionarTipoVuelo(tipoVueloId: Int) {
        _tipoVueloSeleccionadoId.value = tipoVueloId
    }

    private val _tipoAeronaveSeleccionadaId = MutableStateFlow<Int?>(null)
    val tipoAeronaveSeleccionadaId: StateFlow<Int?> = _tipoAeronaveSeleccionadaId

    fun seleccionarTipoAeronave(tipoAeronaveId: Int) {
        _tipoAeronaveSeleccionadaId.value = tipoAeronaveId
    }

    private val _fechaSeleccionada = MutableStateFlow<Date?>(null)
    val fechaSeleccionada: StateFlow<Date?> = _fechaSeleccionada

    fun seleccionarFecha(fecha: Date) {
        _fechaSeleccionada.value = fecha
    }


    init{
        getReserva()
    }


    /*fun seleccionarTipoVuelo(tipoVueloId: Int, tipoVueloDTO: TipoVueloDTO) {
       _tipoVueloSeleccionadoId.value = tipoVueloId
       _uiState.update { it.copy(tipoVueloSeleccionado = tipoVueloDTO) }
   }*/



    fun onEvent(event: ReservaEvent) {
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
            ReservaEvent.save -> saveReserva()
        }
    }



    fun getReserva(){
        viewModelScope.launch {
            reservaRepository.reservaDao.getAll().collect{reserva->
                _uiState.update {
                    it.copy(reservas = reserva)
                }
            }
        }
    }

    fun saveReserva(){
        viewModelScope.launch {
            try{
                reservaRepository.saveReserva(_uiState.value.toEntity())
                _uiState.update {
                    it.copy(
                        successMessage = "Reserva guardada con exito!", errorMessage = null
                    )
                }
               // nuevaReserva()
            }catch(e:Exception){
                _uiState.update {
                    it.copy(
                        errorMessage = "Ha ocurrido un error al guardar la reserva", successMessage = null
                    )
                }
            }
        }
    }

    fun deleteReserva(){
        viewModelScope.launch {
            try{
                reservaRepository.deleteReserva(_uiState.value.toEntity())
                _uiState.update {
                    it.copy(
                        successMessage = "Reserva  eliminada!", errorMessage = null
                    )
                }

            }catch(e:Exception){
                _uiState.update {
                    it.copy(
                        errorMessage = "Ha ocurrido un error al eliminar la reserva"
                    )
                }
            }
        }

    }

    /*fun nuevaReserva(){
        _uiState.update {
            it.copy(
                reservaId = null,

            )
        }

    }*/

    fun selectReserva(reservaId:Int){
        viewModelScope.launch {
            val reserva = reservaRepository.findReserva(reservaId)
            if(reservaId > 0){
                _uiState.update {
                    it.copy(
                        reservaId = reserva?.reservaId,
                        rutaId = reserva?.rutaId,
                        estadoId = reserva?.estadoId,
                        formularioId = reserva?.formularioId,
                        metodoPagoId = reserva?.metodoPagoId,
                        tipoVueloId = reserva?.tipoVueloId,
                        categoriaId = reserva?.categoriaId,
                        pasajeros = reserva?.pasajeros,
                        fecha = reserva?.fecha,
                        impuesto = reserva?.impuesto?:0.0,
                        tarifa = reserva.tarifa?:0.0

                    )
                }
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
        tipoCliente = tipoCliente?:false

    )

}