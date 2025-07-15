package edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.repository.RutaRepository
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
class RutaViewModel @Inject constructor(
    private val rutaRepository: RutaRepository
): ViewModel(){
    private val _uiState = MutableStateFlow(RutaUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init{
        getRutas()
    }

    fun onEvent(event: RutaEvent) {
        when (event) {
            RutaEvent.GetRutas -> getRutas()
            RutaEvent.LimpiarErrorMessageDistanciaChange -> limpiarErrorMessageDistancia()
            RutaEvent.LimpiarErrorMessageDestinoChange -> limpiarErrorMessageDestino()
            RutaEvent.LimpiarErrorMessageOrigenChange -> limpiarErrorMessageOrigen()
            RutaEvent.LimpiarErrorMessageDuracionEstimadaChange -> limpiarErrorMessageDuracionEstimada()
            is RutaEvent.OrigenChange -> origenChange(event.origen)
            RutaEvent.New -> nuevo()
            RutaEvent.PostRuta -> addRuta()
            is RutaEvent.RutaChange -> rutaIdChange(event.rutaId)
            RutaEvent.ResetSuccessMessage -> _uiState.update { it.copy(isSuccess = false, successMessage = null) }
            is RutaEvent.GetRuta -> findRuta(event.id)
            is RutaEvent.AeronaveChange -> TODO()
            RutaEvent.Delete -> TODO()
            is RutaEvent.DestinoChange -> TODO()
            is RutaEvent.DuracionEstimadaChange -> TODO()
            RutaEvent.Save -> TODO()
            is RutaEvent.DistanciaChange -> TODO()
        }
    }

    private fun limpiarErrorMessageOrigen() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorOrigen = "")
            }
        }
    }

    private fun limpiarErrorMessageDestino() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorDestino = "")
            }
        }
    }

    private fun limpiarErrorMessageDistancia() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorOrigen = "")
            }
        }
    }

    private fun limpiarErrorMessageDuracionEstimada() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorDuracionEstimada = "")
            }
        }
    }

    private fun origenChange(origen: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(origen = origen)
            }
        }
    }

    private fun rutaIdChange(id: Int){
        viewModelScope.launch {
            _uiState.update {
                it.copy(rutaId = id)
            }
        }
    }

    private fun distanciaChange(distancia: Double) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(distancia = distancia)
            }
        }
    }

    private fun nuevo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    origen = "",
                    destino = "",
                    distancia = 0.0,
                    duracionEstimada = 0,
                    errorAeronave = "",
                    errorOrigen = "",
                    errorDestino = "",
                    errorDistancia = "",
                    errorDuracionEstimada = "",
                    errorMessage = "",
                )
            }
        }
    }

    private fun addRuta() {
        viewModelScope.launch {
            var error = false

            if (_uiState.value.aeronaveId <= 0) {
                _uiState.update {
                    it.copy(errorAeronave = "Este campo es obligatorio *")
                }
                error = true
            }
            if (_uiState.value.origen.isNullOrBlank()) {
                _uiState.update {
                    it.copy(errorOrigen = "El origen es obligatorio *")
                }
                error = true
            }
            if (_uiState.value.destino.isNullOrBlank()) {
                _uiState.update {
                    it.copy(errorDestino = "El destino es obligatorio *")
                }
                error = true
            }
            if (_uiState.value.distancia <= 0.0) {
                _uiState.update {
                    it.copy(errorDistancia = "La distancia debe ser mayor que cero *")
                }
                error = true
            }
            if (_uiState.value.duracionEstimada <= 0) {
                _uiState.update {
                    it.copy(errorDuracionEstimada = "La duración debe ser mayor que cero *")
                }
                error = true
            }
            if (error) return@launch
            try {
                rutaRepository.saveRuta(_uiState.value.toEntity())

                // Actualizar estado con mensaje de éxito
                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        successMessage = "Usuario guardado correctamente",
                        errorMessage = null
                    )
                }

                getRutas()
                nuevo()

                // Navegar de regreso después de un breve retraso para que se vea el mensaje
                delay(2000) // Espera 2 segundos para mostrar el mensaje
                _uiEvent.send(UiEvent.NavigateUp)
            }catch (e: retrofit2.HttpException) {
                if (e.code() == 500) {
                    // Si es un error 500, usa los datos locales y notifica
                    _uiState.update {
                        it.copy(
                            isSuccess = true,
                            successMessage = "Usuario guardado. Falló sincronización con el servidor (500).",
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
                    return@launch // Salir si es otro error de API

                }
            }catch (e: Exception){
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al guardar el usuario: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }

            _uiEvent.send(UiEvent.NavigateUp)
        }
    }

    fun findRuta(RutaId: Int) {
        viewModelScope.launch {
            if (RutaId > 0) {
                rutaRepository.getRutas(RutaId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val ruta = resource.data?.firstOrNull()
                            _uiState.update {
                                it.copy(
                                    rutaId = ruta?.rutaId,
                                    aeronaveId = ruta?.aeronaveId ?: 0,
                                    origen = ruta?.origen ?: "",
                                    destino = ruta?.destino ?: "",
                                    distancia = ruta?.distancia ?: 0.0,
                                    duracionEstimada = ruta?.duracion ?: 0
                                )
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

    private fun getRutas() {
        viewModelScope.launch {
            rutaRepository.getRuta().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(isLoading = true)
                        }
                    }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                rutas = result.data ?: emptyList(),
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


fun RutaUiState.toEntity() = RutaDTO(
    rutaId = rutaId,
    aeronaveId = aeronaveId ?: 0,
    origen = origen ?: "",
    destino = destino ?: "",
    distancia = distancia ?: 0.0,
    duracion = duracionEstimada ?: 0
)
