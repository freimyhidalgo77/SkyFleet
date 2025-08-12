package edu.ucne.skyplanerent.presentation.rutayviajes.ruta

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.repository.RutaRepository
import edu.ucne.skyplanerent.presentation.UiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class RutaViewModel @Inject constructor(
    private val rutaRepository: RutaRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RutaUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

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

        viewModelScope.launch {
            _uiState
                .map { it.isSuccess to it.successMessage }
                .distinctUntilChanged()
                .collect { (isSuccess, successMessage) ->
                    if (isSuccess && !successMessage.isNullOrBlank()) {
                        _uiState.update { it.copy(showDialog = true) }
                    }
                }
        }

        viewModelScope.launch {
            _uiState
                .map { it.errorMessage }
                .distinctUntilChanged()
                .collect { errorMessage ->
                    if (!errorMessage.isNullOrBlank()) {
                        _uiEvent.send(UiEvent.ShowSnackbar(errorMessage))
                    }
                }
        }
    }

    fun initialize(rutaId: Int?) {
        viewModelScope.launch {
            if (_uiState.value.rutaId == null && _uiState.value.origen.isNullOrBlank() && _uiState.value.destino.isNullOrBlank()) {
                if (rutaId != null && rutaId > 0) {
                    onEvent(RutaEvent.GetRuta(rutaId))
                } else {
                    onEvent(RutaEvent.New)
                }
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
            RutaEvent.SubmitRuta -> submitRuta()
            is RutaEvent.RutaChange -> rutaIdChange(event.rutaId)
            RutaEvent.ResetSuccessMessage -> _uiState.update { it.copy(isSuccess = false, successMessage = null) }
            is RutaEvent.GetRuta -> findRuta(event.id)
            RutaEvent.Delete -> deleteRuta()
            is RutaEvent.DestinoChange -> destinoChange(event.destino)
            is RutaEvent.DuracionEstimadaChange -> duracionEstimadaChange(event.duracionEstimada)
            is RutaEvent.DistanciaChange -> distanciaChange(event.distancia)
            RutaEvent.CloseDialog -> closeDialog()
            RutaEvent.PostRuta -> addRuta()
            RutaEvent.Save -> saveRuta()
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
            _uiState.update { it.copy(errorDuracion = "") }
        }
    }

    private fun origenChange(origen: String) {
        viewModelScope.launch {
            val error = if (origen.isBlank()) "El origen no puede estar vacío" else ""
            _uiState.update {
                it.copy(
                    origen = origen,
                    errorOrigen = error,
                    isFormValid = isFormValid(),
                    submitButtonContentColor = if (isFormValid()) Color.Blue else Color.Gray,
                    submitButtonBorderColor = if (isFormValid()) Color.Blue else Color.Gray
                )
            }
        }
    }

    private fun rutaIdChange(id: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    rutaId = id,
                    title = "Editar Ruta",
                    submitButtonText = "Actualizar",
                    isFormValid = isFormValid(),
                    submitButtonContentColor = if (isFormValid()) Color.Blue else Color.Gray,
                    submitButtonBorderColor = if (isFormValid()) Color.Blue else Color.Gray
                )
            }
        }
    }

    private fun destinoChange(destino: String) {
        viewModelScope.launch {
            val error = if (destino.isBlank()) "El destino no puede estar vacío" else ""
            _uiState.update {
                it.copy(
                    destino = destino,
                    errorDestino = error,
                    isFormValid = isFormValid(),
                    submitButtonContentColor = if (isFormValid()) Color.Blue else Color.Gray,
                    submitButtonBorderColor = if (isFormValid()) Color.Blue else Color.Gray
                )
            }
        }
    }

    private fun duracionEstimadaChange(duracion: Int) {
        viewModelScope.launch {
            val error = if (duracion <= 0) "La duración debe ser mayor a 0" else ""
            _uiState.update {
                it.copy(
                    duracionEstimada = duracion,
                    errorDuracion = error,
                    isFormValid = isFormValid(),
                    submitButtonContentColor = if (isFormValid()) Color.Blue else Color.Gray,
                    submitButtonBorderColor = if (isFormValid()) Color.Blue else Color.Gray
                )
            }
        }
    }

    private fun distanciaChange(distancia: Double) {
        viewModelScope.launch {
            val error = if (distancia <= 0.0) "La distancia debe ser mayor a 0" else ""
            _uiState.update {
                it.copy(
                    distancia = distancia,
                    errorDistancia = error,
                    isFormValid = isFormValid(),
                    submitButtonContentColor = if (isFormValid()) Color.Blue else Color.Gray,
                    submitButtonBorderColor = if (isFormValid()) Color.Blue else Color.Gray
                )
            }
        }
    }

    private fun isFormValid(): Boolean {
        return _uiState.value.origen?.isNotBlank() == true &&
                _uiState.value.destino?.isNotBlank() == true &&
                _uiState.value.distancia > 0.0 &&
                _uiState.value.duracionEstimada > 0
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
                    errorDuracion = "",
                    errorMessage = "",
                    showDialog = false,
                    isFormValid = false,
                    title = "Nueva Ruta",
                    submitButtonText = "Guardar",
                    submitButtonContentColor = Color.Gray,
                    submitButtonDisabledContentColor = Color.Gray,
                    submitButtonBorderColor = Color.Gray
                )
            }
        }
    }

    private fun submitRuta() {
        viewModelScope.launch {
            if (_uiState.value.isFormValid) {
                if (_uiState.value.rutaId == null) {
                    addRuta()
                } else {
                    saveRuta()
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorOrigen = if (_uiState.value.origen.isNullOrBlank()) "El origen es obligatorio *" else "",
                        errorDestino = if (_uiState.value.destino.isNullOrBlank()) "El destino es obligatorio *" else "",
                        errorDistancia = if (_uiState.value.distancia <= 0.0) "La distancia debe ser mayor que cero *" else "",
                        errorDuracion = if (_uiState.value.duracionEstimada <= 0) "La duración debe ser mayor que cero *" else "",
                        isFormValid = isFormValid()
                    )
                }
            }
        }
    }

    private fun addRuta() {
        viewModelScope.launch {
            try {
                rutaRepository.saveRuta(_uiState.value.toEntity())
                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        successMessage = "Ruta guardada correctamente",
                        errorMessage = null,
                        showDialog = true
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
                            errorMessage = null,
                            showDialog = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Error en la API: ${e.code()} - ${e.message}",
                            isSuccess = false
                        )
                    }
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
                val id = _uiState.value.rutaId ?: return@launch // Asegúrate de tener el ID (ajusta 'rutaId' según el nombre real en tu UiState)
                rutaRepository.update(id, _uiState.value.toEntity())
                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        successMessage = "Ruta actualizada correctamente",
                        errorMessage = null,
                        showDialog = true
                    )
                }
                getRutas()
                nuevo()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al actualizar la ruta: ${e.localizedMessage}",
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
                            errorMessage = null,
                            showDialog = true
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

    private fun closeDialog() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showDialog = false,
                    isSuccess = false,
                    successMessage = null
                )
            }
            onEvent(RutaEvent.New)
        }
    }

    private fun findRuta(rutaId: Int) {
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
                                    duracionEstimada = ruta?.duracion ?: 0,
                                    isFormValid = isFormValid(),
                                    title = "Editar Ruta",
                                    submitButtonText = "Actualizar",
                                    submitButtonContentColor = if (isFormValid()) Color.Blue else Color.Gray,
                                    submitButtonBorderColor = if (isFormValid()) Color.Blue else Color.Gray
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

    private fun getRutas() {
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
    distancia = distancia,
    duracion = duracionEstimada
)