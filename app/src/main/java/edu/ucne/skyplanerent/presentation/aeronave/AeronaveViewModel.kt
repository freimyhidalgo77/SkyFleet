package edu.ucne.skyplanerent.presentation.aeronave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.data.repository.AeronaveRepository
import edu.ucne.skyplanerent.presentation.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AeronaveViewModel @Inject constructor(
    private val aeronaveRepository: AeronaveRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AeronaveUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        getAeronaves()
    }

    fun onEvent(event: AeronaveEvent) {
        when (event) {
            AeronaveEvent.GetAeronaves -> getAeronaves()
            AeronaveEvent.LimpiarErrorMessageEstadoIdChange -> limpiarErrorMessageEstadoId()
            AeronaveEvent.LimpiarErrorMessageModeloAvionChange -> limpiarErrorMessageModeloAvion()
            AeronaveEvent.LimpiarErrorMessageDescripcionCategoriaChange -> limpiarErrorMessageDescripcionCategoria()
            AeronaveEvent.LimpiarErrorMessageRegistracionChange -> limpiarErrorMessageRegistracion()
            AeronaveEvent.LimpiarErrorMessageCostoXHoraChange -> limpiarErrorMessageCostoXHora()
            AeronaveEvent.LimpiarErrorMessageDescripcionAeronaveChange -> limpiarErrorMessageDescripcionAeronave()
            AeronaveEvent.LimpiarErrorMessageVelocidadMaximaChange -> limpiarErrorMessageVelocidadMaxima()
            AeronaveEvent.LimpiarErrorMessageDescripcionMotorChange -> limpiarErrorMessageDescripcionMotor()
            AeronaveEvent.LimpiarErrorMessageCapacidadCombustibleChange -> limpiarErrorMessageCapacidadCombustible()
            AeronaveEvent.LimpiarErrorMessageConsumoXHoraChange -> limpiarErrorMessageConsumoXHora()
            AeronaveEvent.LimpiarErrorMessagePesoChange -> limpiarErrorMessagePeso()
            AeronaveEvent.LimpiarErrorMessageRangoChange -> limpiarErrorMessageRango()
            AeronaveEvent.LimpiarErrorMessageCapacidadPasajerosChange -> limpiarErrorMessageCapacidadPasajeros()
            AeronaveEvent.LimpiarErrorMessageAltitudMaximaChange -> limpiarErrorMessageAltitudMaxima()
            AeronaveEvent.LimpiarErrorMessageLicenciaChange -> limpiarErrorMessageLicencia()
            is AeronaveEvent.AeronaveChange -> aeronaveIdChange(event.aeronaveId)
            is AeronaveEvent.EstadoIdChange -> estadoIdChange(event.estadoId)
            is AeronaveEvent.ModeloAvionChange -> modeloAvionChange(event.modeloAvion)
            is AeronaveEvent.DescripcionCategoriaChange -> descripcionCategoriaChange(event.descripcionCategoria)
            is AeronaveEvent.RegistracionChange -> registracionChange(event.registracion)
            is AeronaveEvent.CostoXHoraChange -> costoXHoraChange(event.costoXHora)
            is AeronaveEvent.DescripcionAeronaveChange -> descripcionAeronaveChange(event.descripcionAeronave)
            is AeronaveEvent.VelocidadMaximaChange -> velocidadMaximaChange(event.velocidadMaxima)
            is AeronaveEvent.DescripcionMotorChange -> descripcionMotorChange(event.descripcionMotor)
            is AeronaveEvent.CapacidadCombustibleChange -> capacidadCombustibleChange(event.capacidadCombustible)
            is AeronaveEvent.ConsumoXHoraChange -> consumoXHoraChange(event.consumoXHora)
            is AeronaveEvent.PesoChange -> pesoChange(event.peso)
            is AeronaveEvent.RangoChange -> rangoChange(event.rango)
            is AeronaveEvent.CapacidadPasajerosChange -> capacidadPasajerosChange(event.capacidadPasajeros)
            is AeronaveEvent.AltitudMaximaChange -> altitudMaximaChange(event.altitudMaxima)
            is AeronaveEvent.LicenciaChange -> licenciaChange(event.licencia)
            AeronaveEvent.New -> nuevo()
            AeronaveEvent.postAeronave -> addAeronave()
            AeronaveEvent.ResetSuccessMessage -> _uiState.update { it.copy(isSuccess = false, successMessage = null) }
            is AeronaveEvent.GetAeronave -> findAeronave(event.id)
            AeronaveEvent.Save -> saveAeronave()
            AeronaveEvent.Delete -> deleteAeronave()
        }
    }

    // Nueva función para filtrar aeronaves por categoriaId
    fun filterAeronavesByCategoria(categoriaId: Int) {
        viewModelScope.launch {
            aeronaveRepository.getAeronaves().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        val filteredAeronaves = result.data?.filter { it.estadoId == categoriaId } ?: emptyList()
                        _uiState.update {
                            it.copy(
                                aeronaves = filteredAeronaves,
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = result.message ?: "Error desconocido",
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun limpiarErrorMessageEstadoId() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorEstadoId = null)
            }
        }
    }

    private fun limpiarErrorMessageModeloAvion() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorModeloAvion = null)
            }
        }
    }

    private fun limpiarErrorMessageDescripcionCategoria() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorDescripcionCategoria = null)
            }
        }
    }

    private fun limpiarErrorMessageRegistracion() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorRegistracion = null)
            }
        }
    }

    private fun limpiarErrorMessageCostoXHora() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorCostoXHora = null)
            }
        }
    }

    private fun limpiarErrorMessageDescripcionAeronave() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorDescripcionAeronave = null)
            }
        }
    }

    private fun limpiarErrorMessageVelocidadMaxima() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorVelocidadMaxima = null)
            }
        }
    }

    private fun limpiarErrorMessageDescripcionMotor() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorDescripcionMotor = null)
            }
        }
    }

    private fun limpiarErrorMessageCapacidadCombustible() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorCapacidadCombustible = null)
            }
        }
    }

    private fun limpiarErrorMessageConsumoXHora() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorConsumoXHora = null)
            }
        }
    }

    private fun limpiarErrorMessagePeso() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorPeso = null)
            }
        }
    }

    private fun limpiarErrorMessageRango() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorRango = null)
            }
        }
    }

    private fun limpiarErrorMessageCapacidadPasajeros() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorCapacidadPasajeros = null)
            }
        }
    }

    private fun limpiarErrorMessageAltitudMaxima() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorAltitudMaxima = null)
            }
        }
    }

    private fun limpiarErrorMessageLicencia() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorLicencia = null)
            }
        }
    }

     fun aeronaveIdChange(id: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(AeronaveId = id)
            }
        }
    }

    private fun estadoIdChange(estadoId: Int?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(estadoId = estadoId)
            }
        }
    }

     fun modeloAvionChange(modelo: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(ModeloAvion = modelo)
            }
        }
    }



    /*fun modeloAvionChange(descripcion: String) {
        val aeronave = aeronaves.find { it.descripcionAeronave == descripcion }
        _uiState.update {
            it.copy(categoriaId = aeronave?.aeronaveId ?: 0, aeronaveSeleccionada = aeronave)
        }
    }*/


    private fun descripcionCategoriaChange(descripcion: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(DescripcionCategoria = descripcion)
            }
        }
    }

    private fun registracionChange(registracion: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(Registracion = registracion)
            }
        }
    }

    private fun costoXHoraChange(costo: Double?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(CostoXHora = costo)
            }
        }
    }

    private fun descripcionAeronaveChange(descripcion: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(DescripcionAeronave = descripcion)
            }
        }
    }

    private fun velocidadMaximaChange(velocidad: Double?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(VelocidadMaxima = velocidad)
            }
        }
    }

    private fun descripcionMotorChange(descripcion: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(DescripcionMotor = descripcion)
            }
        }
    }

    private fun capacidadCombustibleChange(capacidad: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(CapacidadCombustible = capacidad)
            }
        }
    }

    private fun consumoXHoraChange(consumo: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(ConsumoXHora = consumo)
            }
        }
    }

    private fun pesoChange(peso: Double?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(Peso = peso)
            }
        }
    }

    private fun rangoChange(rango: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(Rango = rango)
            }
        }
    }

    private fun capacidadPasajerosChange(capacidad: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(CapacidadPasajeros = capacidad)
            }
        }
    }

    private fun altitudMaximaChange(altitud: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(AltitudMaxima = altitud)
            }
        }
    }

    private fun licenciaChange(licencia: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(Licencia = licencia)
            }
        }
    }



    private fun nuevo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    AeronaveId = null,
                    estadoId = 0,
                    ModeloAvion = "",
                    DescripcionCategoria = "",
                    Registracion = "",
                    CostoXHora = 0.0,
                    DescripcionAeronave = "",
                    VelocidadMaxima = 0.0,
                    DescripcionMotor = "",
                    CapacidadCombustible = 0,
                    ConsumoXHora = 0,
                    Peso = 0.0,
                    Rango = 0,
                    CapacidadPasajeros = 0,
                    AltitudMaxima = 0,
                    Licencia = "",
                    errorEstadoId = null,
                    errorModeloAvion = null,
                    errorDescripcionCategoria = null,
                    errorRegistracion = null,
                    errorCostoXHora = null,
                    errorDescripcionAeronave = null,
                    errorVelocidadMaxima = null,
                    errorDescripcionMotor = null,
                    errorCapacidadCombustible = null,
                    errorConsumoXHora = null,
                    errorPeso = null,
                    errorRango = null,
                    errorCapacidadPasajeros = null,
                    errorAltitudMaxima = null,
                    errorLicencia = null,
                    errorMessage = null
                )
            }
        }
    }

    private fun addAeronave() {
        viewModelScope.launch {
            var error = false

            if (_uiState.value.estadoId == null || _uiState.value.estadoId!! <= 0) {
                _uiState.update {
                    it.copy(errorEstadoId = "El ID de estado debe ser mayor que cero *")
                }
                error = true
            }
            if (_uiState.value.ModeloAvion.isBlank()) {
                _uiState.update {
                    it.copy(errorModeloAvion = "El modelo de avión es obligatorio *")
                }
                error = true
            }
            if (_uiState.value.DescripcionCategoria.isBlank()) {
                _uiState.update {
                    it.copy(errorDescripcionCategoria = "La descripción de categoría es obligatoria *")
                }
                error = true
            }
            if (_uiState.value.Registracion.isBlank()) {
                _uiState.update {
                    it.copy(errorRegistracion = "La registración es obligatoria *")
                }
                error = true
            }
            if (_uiState.value.CostoXHora == null || _uiState.value.CostoXHora!! <= 0.0) {
                _uiState.update {
                    it.copy(errorCostoXHora = "El costo por hora debe ser mayor que cero *")
                }
                error = true
            }
            if (_uiState.value.DescripcionAeronave.isBlank()) {
                _uiState.update {
                    it.copy(errorDescripcionAeronave = "La descripción de la aeronave es obligatoria *")
                }
                error = true
            }
            if (_uiState.value.VelocidadMaxima == null || _uiState.value.VelocidadMaxima!! <= 0.0) {
                _uiState.update {
                    it.copy(errorVelocidadMaxima = "La velocidad máxima debe ser mayor que cero *")
                }
                error = true
            }
            if (_uiState.value.DescripcionMotor.isBlank()) {
                _uiState.update {
                    it.copy(errorDescripcionMotor = "La descripción del motor es obligatoria *")
                }
                error = true
            }
            if (_uiState.value.CapacidadCombustible <= 0) {
                _uiState.update {
                    it.copy(errorCapacidadCombustible = "La capacidad de combustible debe ser mayor que cero *")
                }
                error = true
            }
            if (_uiState.value.ConsumoXHora <= 0) {
                _uiState.update {
                    it.copy(errorConsumoXHora = "El consumo por hora debe ser mayor que cero *")
                }
                error = true
            }
            if (_uiState.value.Peso == null || _uiState.value.Peso!! <= 0.0) {
                _uiState.update {
                    it.copy(errorPeso = "El peso debe ser mayor que cero *")
                }
                error = true
            }
            if (_uiState.value.Rango <= 0) {
                _uiState.update {
                    it.copy(errorRango = "El rango debe ser mayor que cero *")
                }
                error = true
            }
            if (_uiState.value.CapacidadPasajeros <= 0) {
                _uiState.update {
                    it.copy(errorCapacidadPasajeros = "La capacidad de pasajeros debe ser mayor que cero *")
                }
                error = true
            }
            if (_uiState.value.AltitudMaxima <= 0) {
                _uiState.update {
                    it.copy(errorAltitudMaxima = "La altitud máxima debe ser mayor que cero *")
                }
                error = true
            }
            if (_uiState.value.Licencia.isBlank()) {
                _uiState.update {
                    it.copy(errorLicencia = "La licencia es obligatoria *")
                }
                error = true
            }
            if (error) return@launch

            try {
                aeronaveRepository.saveAeronave(_uiState.value.toEntity())

                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        successMessage = "Aeronave guardada correctamente",
                        errorMessage = null
                    )
                }

                getAeronaves()
                nuevo()

                delay(2000)
                _uiEvent.send(UiEvent.NavigateUp)
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 500) {
                    _uiState.update {
                        it.copy(
                            isSuccess = true,
                            successMessage = "Aeronave guardada. Falló sincronización con el servidor (500).",
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Error en la API: ${e.code()} - ${e.message}",
                            isSuccess = false
                        )
                    }
                    return@launch
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al guardar la aeronave: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }

            _uiEvent.send(UiEvent.NavigateUp)
        }
    }

    private fun saveAeronave() {
        viewModelScope.launch {
            try {
                aeronaveRepository.saveAeronave(_uiState.value.toEntity())
                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        successMessage = "Aeronave guardada correctamente",
                        errorMessage = null
                    )
                }
                getAeronaves()
                nuevo()
                delay(2000)
                _uiEvent.send(UiEvent.NavigateUp)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al guardar la aeronave: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    private fun deleteAeronave() {
        viewModelScope.launch {
            try {
                _uiState.value.AeronaveId?.let { id ->
                    aeronaveRepository.deleteAeronave(id)
                    _uiState.update {
                        it.copy(
                            isSuccess = true,
                            successMessage = "Aeronave eliminada correctamente",
                            errorMessage = null
                        )
                    }
                    getAeronaves()
                    nuevo()
                    delay(2000)
                    _uiEvent.send(UiEvent.NavigateUp)
                } ?: run {
                    _uiState.update {
                        it.copy(
                            errorMessage = "No se seleccionó una aeronave para eliminar",
                            isSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al eliminar la aeronave: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    private fun findAeronave(aeronaveId: Int) {
        viewModelScope.launch {
            if (aeronaveId > 0) {
                aeronaveRepository.getAeronave(aeronaveId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val aeronave = resource.data?.firstOrNull()
                            aeronave?.let {
                                _uiState.update {
                                    it.copy(
                                        AeronaveId = aeronave.aeronaveId,
                                        estadoId = aeronave.estadoId,
                                        ModeloAvion = aeronave.modeloAvion ?: "",
                                        DescripcionCategoria = aeronave.descripcionCategoria ?: "",
                                        Registracion = aeronave.registracion ?: "",
                                        CostoXHora = aeronave.costoXHora,
                                        DescripcionAeronave = aeronave.descripcionAeronave ?: "",
                                        VelocidadMaxima = aeronave.velocidadMaxima,
                                        DescripcionMotor = aeronave.descripcionMotor ?: "",
                                        CapacidadCombustible = aeronave.capacidadCombustible ?: 0,
                                        ConsumoXHora = aeronave.consumoXHora ?: 0,
                                        Peso = aeronave.peso,
                                        Rango = aeronave.rango ?: 0,
                                        CapacidadPasajeros = aeronave.capacidadPasajeros ?: 0,
                                        AltitudMaxima = aeronave.altitudMaxima ?: 0,
                                        Licencia = aeronave.licencia ?: ""
                                    )
                                }
                            } ?: run {
                                _uiState.update { it.copy(errorMessage = "Aeronave no encontrada") }
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(errorMessage = resource.message)
                            }
                        }
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
            }
        }
    }

    private fun getAeronaves() {
        viewModelScope.launch {
            aeronaveRepository.getAeronaves().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                aeronaves = result.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = result.message ?: "Error desconocido",
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }
}

fun AeronaveUiState.toEntity() = AeronaveDTO(
    aeronaveId = AeronaveId,
    estadoId = estadoId ?: 0,
    modeloAvion = ModeloAvion ?: "",
    descripcionCategoria = DescripcionCategoria ?: "",
    registracion = Registracion ?: "",
    costoXHora = CostoXHora,
    descripcionAeronave = DescripcionAeronave ?: "",
    velocidadMaxima = VelocidadMaxima,
    descripcionMotor = DescripcionMotor ?: "",
    capacidadCombustible = CapacidadCombustible ?: 0,
    consumoXHora = ConsumoXHora ?: 0,
    peso = Peso,
    rango = Rango ?: 0,
    capacidadPasajeros = CapacidadPasajeros ?: 0,
    altitudMaxima = AltitudMaxima ?: 0,
    licencia = Licencia ?: ""
)
