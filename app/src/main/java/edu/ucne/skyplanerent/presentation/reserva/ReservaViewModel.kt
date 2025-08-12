package edu.ucne.skyplanerent.presentation.reserva


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.repository.ReservaRepository
import edu.ucne.skyplanerent.data.repository.RutaRepository
import edu.ucne.skyplanerent.data.repository.TipoVueloRepository
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.login.SessionManager
import edu.ucne.skyplanerent.presentation.rutayviajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo.TipoLicencia
import edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo.TipoVueloUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ReservaViewModel @Inject constructor(
    private val reservaRepository: ReservaRepository,
    val auth: FirebaseAuth,
    val sessionManager: SessionManager

): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiState_ruta = MutableStateFlow(RutaUiState())
    val rutaUiState = _uiState_ruta.asStateFlow()

    private val _uiState_aeronaves = MutableStateFlow(AeronaveUiState())
    val aeronaveUiState = _uiState_aeronaves.asStateFlow()

    private val _uiState_tipoVuelo = MutableStateFlow(TipoVueloUiState())
    val tipoVueloUiState = _uiState_tipoVuelo.asStateFlow()

    private var _precioOriginal: Double = 0.0
    private var _precioCalculado: Double = 0.0
    private var _camposRelevantesCambiados: Boolean = false

    // Modificar la función actualizarPrecio
    fun actualizarPrecio() {
        viewModelScope.launch {
            try {
                // Verificar que tenemos todos los datos necesarios
                val rutaId = _uiState.value.rutaId
                val categoriaId = _uiState.value.categoriaId

                if (rutaId == null || categoriaId == null) {
                    return@launch
                }

                // Obtener los datos actualizados
                val duracionVuelo = rutaUiState.value.rutas.find { it.rutaId == rutaId }?.duracion ?: 0
                val costoXHora = aeronaveUiState.value.aeronaves.find { it.aeronaveId == categoriaId }?.costoXHora ?: 0.0
                val pasajeros = _uiState.value.pasajeros ?: 1

                if (duracionVuelo == 0 || costoXHora == 0.0) {
                    return@launch
                }

                val tarifaBase = duracionVuelo * costoXHora
                val impuesto = tarifaBase * 0.10
                _precioCalculado = tarifaBase + impuesto

                _uiState.update {
                    it.copy(
                        tarifa = tarifaBase,
                        impuesto = impuesto,
                        precioTotal = _precioCalculado
                    )
                }
            } catch (e: Exception) {
                // Manejar error si es necesario
            }
        }
    }


    //Seleccionar ruta de vuelo
    private val _rutaSeleccionadaId = MutableStateFlow<Int?>(null)
    val rutaSeleccionadaId: StateFlow<Int?> = _rutaSeleccionadaId

    fun seleccionarRuta(rutaId: Int) {
        _rutaSeleccionadaId.value = rutaId
        actualizarPrecio()
    }

    //Seleccionar tipo de vuelo
    private val _tipoVueloSeleccionadoId = MutableStateFlow<Int?>(null)
    val tipoVueloSeleccionadoId: StateFlow<Int?> = _tipoVueloSeleccionadoId

    fun seleccionarTipoVuelo(tipoVueloId: Int) {
        _tipoVueloSeleccionadoId.value = tipoVueloId
        actualizarPrecio()
    }
    //Seleciconar tipo aeronave
    private val _tipoAeronaveSeleccionadaId = MutableStateFlow<Int?>(null)
    val tipoAeronaveSeleccionadaId: StateFlow<Int?> = _tipoAeronaveSeleccionadaId

    fun seleccionarTipoAeronave(tipoAeronaveId: Int) {
        _tipoAeronaveSeleccionadaId.value = tipoAeronaveId
        actualizarPrecio()
    }

    //Seleccionar fecha
    private val _fechaSeleccionada = MutableStateFlow<Date?>(null)
    val fechaSeleccionada: StateFlow<Date?> = _fechaSeleccionada

    fun seleccionarFecha(fecha: Date) {
        _fechaSeleccionada.value = fecha
        actualizarPrecio()
    }

    //Seleccionar tipo cliente
    private val _tipoCliente = MutableStateFlow(false)
    val tipoCliente: StateFlow<Boolean> = _tipoCliente

    fun seleccionarTipoCliente(valor: Boolean) {
        _tipoCliente.value = valor
        actualizarPrecio()
    }
    fun seleccionarLicenciaPiloto(licencia: TipoLicencia) {
        _uiState.update { it.copy(licenciaPiloto = licencia) }
    }



    fun categoriaIdChange(id: Int) {
        _camposRelevantesCambiados = true
        _uiState.update { it.copy(categoriaId = id) }
        actualizarPrecio()
    }

    fun onChangeRuta(rutaId: Int) {
        _camposRelevantesCambiados = true
        _uiState.update { it.copy(rutaId = rutaId) }
        actualizarPrecio()
    }

    fun onChangeTipoVuelo(tipoVueloId: Int) {
        _camposRelevantesCambiados = true
        _uiState.update { it.copy(tipoVueloId = tipoVueloId) }
        actualizarPrecio()
    }


    fun onChangePasajeros(pasajero: Int) {
        _camposRelevantesCambiados = true
        _uiState.update { it.copy(pasajeros = pasajero) }

        viewModelScope.launch {

            delay(50)
            actualizarPrecio()
        }
    }

    // Para campos que no afectan el precio, no marcar como relevantes
    fun onFechaChange(nuevaFecha: String) {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaConvertida: Date? = try {
            formato.parse(nuevaFecha)
        } catch (e: Exception) {
            null
        }
        _uiState.update { it.copy(fecha = fechaConvertida) }
    }

    // En ReservaViewModel
    fun onPrecioChange(precio: Double) {
        _uiState.update { it.copy(precioTotal = precio) }
    }

    fun onEstadoPagoChange(estado: String) {
        _uiState.update { it.copy(estadoPago = estado) }
    }


    //Inicializando el metodo loadUserReserva para cargar el usuario perteneciente a esa reserva
    init{
        //getReserva()
        loadUserReservas()
    }


    //Cargar reserva por usuario

    fun loadUserReservas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val userId = obtenerUsuarioConReintento()
                if (userId == null) {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Usuario no autenticado",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                reservaRepository.getReservasByUserId(userId).collect { reservas ->
                    _uiState.update {
                        it.copy(
                            reservas = reservas,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al cargar reservas: ${e.message}",
                        isLoading = false
                    )
                }
            }
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

                // Usar _precioCalculado si hay cambios relevantes, sino _precioOriginal
                val precioFinal = if (_camposRelevantesCambiados) _precioCalculado else _precioOriginal

                // Preservar todos los datos del formulario existente
                val formularioId = reservaSeleccionada.formularioId
                val metodoPago = reservaSeleccionada.metodoPago ?: uiState.value.metodoPago
                val comprobante = reservaSeleccionada.comprobante ?: uiState.value.comprobante

                val reservaActualizada = uiState.value.toEntity().copy(
                    reservaId = reservaSeleccionada.reservaId,
                    userId = currentUserId,
                    rutaId = uiState.value.rutaId ?: reservaSeleccionada.rutaId,
                    tipoVueloId = uiState.value.tipoVueloId ?: reservaSeleccionada.tipoVueloId,
                    categoriaId = uiState.value.categoriaId ?: reservaSeleccionada.categoriaId,
                    fecha = uiState.value.fecha ?: reservaSeleccionada.fecha,
                    pasajeros = uiState.value.pasajeros ?: reservaSeleccionada.pasajeros ?: 1,
                    tipoCliente = uiState.value.tipoCliente ?: reservaSeleccionada.tipoCliente,
                    impuesto = uiState.value.impuesto ?: reservaSeleccionada.impuesto ?: 0.0,
                    tarifa = uiState.value.tarifa ?: reservaSeleccionada.tarifa ?: 0.0,
                    precioTotal = precioFinal,
                    formularioId = formularioId,
                    metodoPago = metodoPago,
                    comprobante = comprobante
                )

                reservaRepository.saveReserva(reservaActualizada)
                loadUserReservas()

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
        formularioId: Int,
        tarifaBase: Double,
        impuesto: Double,
        precioTotal: Double,
        tipoCliente: Boolean?,
        pasajero: Int,
        metodoPago: String?,
        comprobante: String?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // 1. Verificar autenticación con reintento
                val userId = obtenerUsuarioConReintento()
                if (userId == null) {
                    _uiState.update {
                        it.copy(
                            errorMessage = "No se pudo autenticar al usuario",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                // 2. Validar campos obligatorios
                val fecha = _fechaSeleccionada.value ?: run {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Debe seleccionar una fecha válida",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                // 3. Crear y guardar reserva
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
                    userId = userId,
                    estadoPago = if (metodoPago != null) "COMPLETADO" else "PENDIENTE",
                    comprobante = comprobante
                )

                reservaRepository.saveReserva(reserva)

                // 4. Actualizar estado y lista
                _uiState.update {
                    it.copy(
                        successMessage = "Reserva guardada con éxito",
                        errorMessage = null,
                        isLoading = false
                    )
                }

                // 5. Recargar reservas
                loadUserReservas()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al guardar: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun obtenerUsuarioConReintento(): String? {
        // Primero intentar con Firebase Auth
        auth.currentUser?.uid?.let { return it }

        // Si no hay usuario en Firebase, intentar con SessionManager
        sessionManager.getFirebaseUid()?.let { return it }

        // Esperar un poco y reintentar (para casos de reinicio de app)
        delay(1000)
        return auth.currentUser?.uid ?: sessionManager.getFirebaseUid()
    }

    fun deleteReserva() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // 1. Obtener usuario con reintento
                val userId = obtenerUsuarioConReintento()
                if (userId == null) {
                    _uiState.update {
                        it.copy(
                            errorMessage = "No se pudo autenticar al usuario",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                // 2. Verificar reserva seleccionada
                val reserva = uiState.value.reservaSeleccionada ?: run {
                    _uiState.update {
                        it.copy(
                            errorMessage = "No hay reserva seleccionada",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                // 3. Verificar propiedad
                if (reserva.userId != userId) {
                    _uiState.update {
                        it.copy(
                            errorMessage = "No tienes permiso para eliminar esta reserva",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                // 4. Eliminar reserva
                reservaRepository.deleteReserva(reserva)

                // 5. Actualizar estado
                _uiState.update {
                    it.copy(
                        reservaSeleccionada = null,
                        successMessage = "Reserva eliminada correctamente",
                        errorMessage = null,
                        isLoading = false
                    )
                }

                // 6. Recargar lista
                loadUserReservas()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al eliminar: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }


    fun getReservaPorId(id: Int): ReservaEntity? {
        return uiState.value.reservas.find { it.reservaId == id }
    }


///Aqui quede///

    /*fun nuevaReserva(){
        _uiState.update {
            it.copy(
                reservaId = null,

            )
        }

    }*/

    fun selectReserva(reservaId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Buscar en la lista local primero
                val reservaLocal = _uiState.value.reservas.find { it.reservaId == reservaId }

                // Si no está local, buscar en el repositorio
                val reserva = reservaLocal ?: reservaRepository.findReserva(reservaId)

                if (reserva != null) {
                    // Actualizar todos los estados relacionados
                    _fechaSeleccionada.value = reserva.fecha
                    _tipoCliente.value = reserva.tipoCliente?:false
                    _precioOriginal = reserva.precioTotal ?: 0.0
                    _precioCalculado = _precioOriginal
                    _camposRelevantesCambiados = false

                    _uiState.update {
                        it.copy(
                            reservaSeleccionada = reserva,
                            rutaId = reserva.rutaId,
                            tipoVueloId = reserva.tipoVueloId,
                            categoriaId = reserva.categoriaId,
                            pasajeros = reserva.pasajeros,
                            fecha = reserva.fecha,
                            tarifa = reserva.tarifa,
                            impuesto = reserva.impuesto,
                            precioTotal = reserva.precioTotal,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Reserva no encontrada",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al cargar reserva: ${e.message}",
                        isLoading = false
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

    /*fun onChangePasajeros(pasajero:Int){
        _uiState.update {
            it.copy(
                pasajeros = pasajero
            )
        }

    }*/

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