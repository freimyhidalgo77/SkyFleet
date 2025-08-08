package edu.ucne.skyplanerent.presentation.rutayviajes.ruta

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest

import javax.inject.Inject

@HiltViewModel
class RutaViewModel @Inject constructor(
    private val rutaRepository: RutaRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RutaUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // Variables para búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<RutaDTO>>(emptyList())
    val searchResults: StateFlow<List<RutaDTO>> = _searchResults.asStateFlow()

    init {
        getRutas()
        viewModelScope.launch {
            _searchQuery
                .debounce(600)
                .distinctUntilChanged()
                .mapLatest { query ->
                    filterRutas(query)
                }
                .collectLatest { filtered ->
                    _searchResults.value = filtered
                }
        }
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
            RutaEvent.Delete -> deleteRuta()
            is RutaEvent.DestinoChange -> destinoChange(event.destino)
            is RutaEvent.DuracionEstimadaChange -> duracionEstimadaChange(event.duracionEstimada)
            RutaEvent.Save -> saveRuta()
            is RutaEvent.DistanciaChange -> distanciaChange(event.distancia)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private fun filterRutas(query: String): List<RutaDTO> {
        return if (query.isBlank()) {
            _uiState.value.rutas
        } else {
            _uiState.value.rutas.filter {
                it.origen.contains(query, ignoreCase = true) ||
                        it.destino.contains(query, ignoreCase = true)
            }
        }
    }

    private fun limpiarErrorMessageOrigen() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorOrigen = "") }
        }
    }

    private fun limpiarErrorMessageDestino() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorDestino = "") }
        }
    }

    private fun limpiarErrorMessageDistancia() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorDistancia = "") }
        }
    }

    private fun limpiarErrorMessageDuracionEstimada() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorDuracionEstimada = "") }
        }
    }

    private fun origenChange(origen: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(origen = origen) }
        }
    }

    private fun rutaIdChange(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(rutaId = id) }
        }
    }

    private fun destinoChange(destino: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(destino = destino) }
        }
    }

    private fun duracionEstimadaChange(duracion: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(duracionEstimada = duracion) }
        }
    }

    private fun distanciaChange(distancia: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(distancia = distancia) }
        }
    }

    private fun nuevo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    rutaId = null,
                    origen = "",
                    destino = "",
                    distancia = 0.0,
                    duracionEstimada = 0,
                    errorOrigen = "",
                    errorDestino = "",
                    errorDistancia = "",
                    errorDuracionEstimada = "",
                    errorMessage = ""
                )
            }
        }
    }

    private fun addRuta() {
        viewModelScope.launch {
            var error = false

            if (_uiState.value.origen.isNullOrBlank()) {
                _uiState.update { it.copy(errorOrigen = "El origen es obligatorio *") }
                error = true
            }
            if (_uiState.value.destino.isNullOrBlank()) {
                _uiState.update { it.copy(errorDestino = "El destino es obligatorio *") }
                error = true
            }
            if (_uiState.value.distancia == null || _uiState.value.distancia <= 0.0) {
                _uiState.update { it.copy(errorDistancia = "La distancia debe ser mayor que cero *") }
                error = true
            }
            if (_uiState.value.duracionEstimada <= 0) {
                _uiState.update { it.copy(errorDuracionEstimada = "La duración debe ser mayor que cero *") }
                error = true
            }
            if (error) return@launch

            try {
                rutaRepository.saveRuta(_uiState.value.toEntity())

                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        successMessage = "Ruta guardada correctamente",
                        errorMessage = null
                    )
                }

                getRutas()
                nuevo()
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 500) {
                    _uiState.update {
                        it.copy(
                            isSuccess = true,
                            successMessage = "Ruta guardada. Falló sincronización con el servidor (500).",
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
                        errorMessage = "Error al guardar la ruta: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    private fun saveRuta() {
        viewModelScope.launch {
            try {
                rutaRepository.saveRuta(_uiState.value.toEntity())
                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        successMessage = "Ruta guardada correctamente",
                        errorMessage = null
                    )
                }
                getRutas()
                nuevo()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al guardar la ruta: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    private fun deleteRuta() {
        viewModelScope.launch {
            try {
                _uiState.value.rutaId?.let { id ->
                    rutaRepository.deleteRuta(id)
                    _uiState.update {
                        it.copy(
                            isSuccess = true,
                            successMessage = "Ruta eliminada correctamente",
                            errorMessage = null
                        )
                    }
                    getRutas()
                    nuevo()
                    delay(2000)
                    _uiEvent.send(UiEvent.NavigateUp)
                } ?: run {
                    _uiState.update {
                        it.copy(
                            errorMessage = "No se seleccionó una ruta para eliminar",
                            isSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al eliminar la ruta: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    fun findRuta(rutaId: Int) {
        viewModelScope.launch {
            if (rutaId > 0) {
                rutaRepository.getRuta(rutaId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val ruta = resource.data?.firstOrNull()
                            _uiState.update {
                                it.copy(
                                    rutaId = ruta?.rutaId,
                                    origen = ruta?.origen ?: "",
                                    destino = ruta?.destino ?: "",
                                    distancia = ruta?.distancia ?: 0.0,
                                    duracionEstimada = ruta?.duracion ?: 0
                                )
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update { it.copy(errorMessage = resource.message) }
                        }
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
            }
        }
    }

    fun getRutas() {
        viewModelScope.launch {
            rutaRepository.getRutas().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
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
    origen = origen ?: "",
    destino = destino ?: "",
    distancia = distancia ?: 0.0,
    duracion = duracionEstimada ?: 0
)