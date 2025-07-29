package edu.ucne.skyplanerent.presentation.reserva

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.data.repository.ReservaRepository
import edu.ucne.skyplanerent.data.repository.RutaRepository
import edu.ucne.skyplanerent.data.repository.TipoVueloRepository
import edu.ucne.skyplanerent.presentation.UiEvent
import edu.ucne.skyplanerent.presentation.login.SessionManager
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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReservaViewModel @Inject constructor(

    private val reservaRepository: ReservaRepository,
    private val tipoRutaRepository: TipoVueloRepository,
    private val rutaRepository: RutaRepository,
    private val auth: FirebaseAuth,
    private val sessionManager: SessionManager


): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


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

    fun onChangeTipoVuelo(tipoVueloId: Int) {
        _uiState.update { it.copy(tipoVueloId =  tipoVueloId) }
    }

    fun onFechaChange(nuevaFecha: String) {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaConvertida: Date? = try {
            formato.parse(nuevaFecha)
        } catch (e: Exception) {
            null
        }

        _uiState.update { it.copy(fecha = fechaConvertida) }
    }


   //Inicializando el metodo loadUserReserva para cargar el usuario perteneciente a esa reserva
    init{
        //getReserva()
        loadUserReservas()
    }


    //Cargar reserva por usuario
    fun loadUserReservas() {
        // Primero intenta con el usuario de Firebase (si está disponible)
        val firebaseUser = auth.currentUser
        val storedUserId = sessionManager.getCurrentUserId()

        val userId = firebaseUser?.uid ?: storedUserId

        if (userId != null) {
            viewModelScope.launch {
                reservaRepository.getReservasByUserId(userId)
                    .collect { reservas ->
                        _uiState.update { it.copy(reservas = reservas) }
                    }
            }
        } else {
            // No hay usuario autenticado ni en sesión
            _uiState.update { it.copy(reservas = emptyList()) }
        }
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
                val currentUserId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
                val reservaSeleccionada = uiState.value.reservaSeleccionada
                    ?: throw Exception("No hay reserva seleccionada")

                if (reservaSeleccionada.userId != currentUserId) {
                    throw Exception("La reserva no pertenece al usuario")
                }

                val reservaActualizada = uiState.value.toEntity().copy(
                    reservaId = reservaSeleccionada.reservaId,
                    userId = currentUserId,
                    // Asegurar que todos los campos se mantengan
                    rutaId = uiState.value.rutaId ?: reservaSeleccionada.rutaId,
                    tipoVueloId = uiState.value.tipoVueloId ?: reservaSeleccionada.tipoVueloId,
                    categoriaId = uiState.value.categoriaId ?: reservaSeleccionada.categoriaId,
                    fecha = uiState.value.fecha ?: reservaSeleccionada.fecha,
                    pasajeros = uiState.value.pasajeros ?: reservaSeleccionada.pasajeros ?: 1,
                    tipoCliente = uiState.value.tipoCliente ?: reservaSeleccionada.tipoCliente,
                    impuesto = uiState.value.impuesto ?: 0.0,
                    tarifa = uiState.value.tarifa ?: 0.0,

                )

                reservaRepository.saveReserva(reservaActualizada)
                loadUserReservas() // Recargar lista actualizada

                _uiState.update {
                    it.copy(
                        successMessage = "Reserva actualizada correctamente",
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Error al actualizar: ${e.message}")
                }
            }
        }
    }

    // En ReservaViewModel.kt
   /* fun actualizarEstadoPago(reservaId: Int, metodoPago: String, comprobante: String? = null) {
        viewModelScope.launch {
            reservaRepository.actualizarEstadoPago(reservaId, metodoPago, comprobante)
            // Actualizar el estado local si es necesario
            _uiState.update { it.copy(metodoPago = metodoPago, comprobante = comprobante) }
        }
    }*/

    fun guardarReserva(
        rutaId: Int,
        tipoVueloId: Int,
        aeronaveId: Int,
        formularioId:Int,
        tarifaBase: Double,
        impuesto: Double,
        precioTotal: Double,
        tipoCliente: Boolean?,
        pasajero: Int,
        metodoPago: String?,
        comprobante: String?

    ) {
        viewModelScope.launch {
            val fecha = _fechaSeleccionada.value
            val currentUser = auth.currentUser

            if (fecha == null) {
                _uiState.update {
                    it.copy(errorMessage = "Debe seleccionar una fecha válida.")
                }
                return@launch
            }

            if (currentUser == null) {
                _uiState.update {
                    it.copy(errorMessage = "Usuario no autenticado. Por favor inicie sesión.")
                }
                return@launch
            }

            val reserva = ReservaEntity(
                rutaId = rutaId,
                tipoVueloId = tipoVueloId,
                formularioId = formularioId,
                categoriaId = aeronaveId,
                fecha = fecha,
                tarifa = tarifaBase,
                impuesto = impuesto,
                tipoCliente = tipoCliente,
                precioTotal = precioTotal,
                pasajeros = pasajero,
                userId = currentUser.uid,
                estadoPago = if (metodoPago != null) "COMPLETADO" else "PENDIENTE",
                comprobante = comprobante
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
                val currentUserId = auth.currentUser?.uid
                if (currentUserId == null) {
                    _uiState.update { it.copy(errorMessage = "Usuario no autenticado") }
                    return@launch
                }

                val reserva = _uiState.value.reservas.firstOrNull {
                    it.reservaId == _uiState.value.reservaId && it.userId == currentUserId
                }

                if (reserva != null) {
                    reservaRepository.deleteReserva(reserva)
                    _uiState.update {
                        it.copy(
                            successMessage = "Reserva eliminada!",
                            errorMessage = null
                        )
                    }
                    loadUserReservas() // Recargar lista actualizada
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Reserva no encontrada o no pertenece al usuario")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Error al eliminar: ${e.message}")
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
                val currentUserId = auth.currentUser?.uid
                if (currentUserId == null) {
                    _uiState.update { it.copy(errorMessage = "Usuario no autenticado") }
                    return@launch
                }

                val reserva = uiState.value.reservas.find { it.reservaId == reservaId && it.userId == currentUserId }
                    ?: reservaRepository.findReserva(reservaId).takeIf { it?.userId == currentUserId }
                    ?: throw Exception("Reserva no encontrada")

                // Buscar primero en las reservas ya cargadas
                uiState.value.reservas.find { it.reservaId == reservaId && it.userId == currentUserId }?.let { reserva ->
                    _uiState.update {
                        it.copy(
                            reservaId = reserva.reservaId,
                            rutaId = reserva.rutaId,
                            estadoId = reserva.estadoId,
                            formularioId = reserva.formularioId,
                            metodoPago = reserva.metodoPago?:"",
                            tipoVueloId = reserva.tipoVueloId,
                            categoriaId = reserva.categoriaId,
                            pasajeros = reserva.pasajeros?:0,
                            fecha = reserva.fecha,
                            impuesto = reserva.impuesto ?: 0.0,
                            tarifa = reserva.tarifa ?: 0.0,
                            reservaSeleccionada = reserva
                        )
                    }
                    return@launch
                }

                // Si no esta en las cargadas, buscar en el repositorio
               // val reserva = reservaRepository.findReserva(reservaId)
                if (reserva != null && reserva.userId == currentUserId) {
                    _uiState.update {
                        it.copy(
                            reservaSeleccionada = reserva,
                            // Actualizar campos como arriba
                        )
                    }
                } else {
                    _uiState.update { it.copy(errorMessage = "Reserva no encontrada") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al cargar reserva") }
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
        metodoPago = metodoPago,
        tipoVueloId = tipoVueloId,
        categoriaId = categoriaId,
        fecha = fecha,
        impuesto = impuesto?:0.0,
        tarifa = tarifa?:0.0,
        precioTotal = precioTotal,
        tipoCliente = tipoCliente?:false,
        pasajeros = pasajeros,
        userId = userId,
         comprobante = comprobante

        )

}