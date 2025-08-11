package edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.data.repository.TipoVueloRepository
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
class TipoVueloViewModel @Inject constructor(
    private val tipoVueloRepository: TipoVueloRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TipoVueloUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        getTipoVuelos()
    }

    fun onEvent(event: TipoVueloEvent) {
        when (event) {
            TipoVueloEvent.GetTipoVuelos -> getTipoVuelos()
            TipoVueloEvent.LimpiarErrorMessageTipoClienteChange -> limpiarErrorMessageTipoCliente()
            TipoVueloEvent.LimpiarErrorMessageDescripcionTipoVueloChange -> limpiarErrorMessageDescripcionTipoVuelo()
            is TipoVueloEvent.TipoVueloChange -> tipoVueloIdChange(event.vueloId)
            is TipoVueloEvent.DescripcionTipoVueloChange -> descripcionTipoVueloChange(event.descripciontipovuelo)
            is TipoVueloEvent.NombreVueloChange -> nombreVueloChange(event.nombrevuelo)
            TipoVueloEvent.New -> nuevo()
            TipoVueloEvent.PostTipoVuelo -> addTipoVuelo()
            TipoVueloEvent.ResetSuccessMessage -> _uiState.update { it.copy(isSuccess = false, successMessage = null) }
            is TipoVueloEvent.GetTipoVuelo -> findTipoVuelo(event.id)
            TipoVueloEvent.Save -> saveTipoVuelo()
            TipoVueloEvent.Delete -> deleteTipoVuelo()
        }
    }

    private fun limpiarErrorMessageTipoCliente() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorNombreVuelo = null)
            }
        }
    }

    private fun limpiarErrorMessageDescripcionTipoVuelo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(errorDescripcionTipovuelo = null)
            }
        }
    }

    private fun tipoVueloIdChange(id: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(tipoVueloId = id)
            }
        }
    }

    private fun descripcionTipoVueloChange(descripcion: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(descripcionTipoVuelo = descripcion)
            }
        }
    }

    private fun nombreVueloChange(nombrevuelo: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(nombreVuelo = nombrevuelo)
            }
        }
    }

    private fun nuevo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    tipoVueloId = null,
                    nombreVuelo = "",
                    descripcionTipoVuelo = "",
                    errorNombreVuelo = null,
                    errorDescripcionTipovuelo = null,
                    errorMessage = null
                )
            }
        }
    }

    private fun addTipoVuelo() {
        viewModelScope.launch {
            var error = false

            if (_uiState.value.nombreVuelo.isBlank()) {
                _uiState.update {
                    it.copy(errorNombreVuelo = "El nombre del vuelo es obligatorio *")
                }
                error = true
            }
            if (_uiState.value.descripcionTipoVuelo.isBlank()) {
                _uiState.update {
                    it.copy(errorDescripcionTipovuelo = "La descripción es obligatoria *")
                }
                error = true
            }
            if (error) return@launch

            try {
                tipoVueloRepository.saveTipoVuelo(_uiState.value.toEntity())

                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        successMessage = "Tipo de vuelo guardado correctamente",
                        errorMessage = null
                    )
                }

                getTipoVuelos()
                nuevo()
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 500) {
                    _uiState.update {
                        it.copy(
                            isSuccess = true,
                            successMessage = "Tipo de vuelo guardado. Falló sincronización con el servidor (500).",
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
                        errorMessage = "Error al guardar el tipo de vuelo: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    private fun saveTipoVuelo() {
        viewModelScope.launch {
            try {
                tipoVueloRepository.saveTipoVuelo(_uiState.value.toEntity())
                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        successMessage = "Tipo de vuelo guardado correctamente",
                        errorMessage = null
                    )
                }
                getTipoVuelos()
                nuevo()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al guardar el tipo de vuelo: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    private fun deleteTipoVuelo() {
        viewModelScope.launch {
            try {
                _uiState.value.tipoVueloId?.let { id ->
                    tipoVueloRepository.deleteTipoVuelo(id)
                    _uiState.update {
                        it.copy(
                            isSuccess = true,
                            successMessage = "Tipo de vuelo eliminado correctamente",
                            errorMessage = null
                        )
                    }
                    getTipoVuelos()
                    nuevo()
                    delay(2000)
                    _uiEvent.send(UiEvent.NavigateUp)
                } ?: run {
                    _uiState.update {
                        it.copy(
                            errorMessage = "No se seleccionó un tipo de vuelo para eliminar",
                            isSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al eliminar el tipo de vuelo: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    private fun findTipoVuelo(tipoVueloId: Int) {
        viewModelScope.launch {
            if (tipoVueloId > 0) {
                tipoVueloRepository.getTipoVuelo(tipoVueloId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val tipoVuelo = resource.data?.firstOrNull()
                            _uiState.update {
                                it.copy(
                                    tipoVueloId = tipoVuelo?.tipoVueloId,
                                    nombreVuelo = tipoVuelo?.nombreVuelo ?: "",
                                    descripcionTipoVuelo = tipoVuelo?.descripcionTipoVuelo ?: ""
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

    private fun getTipoVuelos() {
        viewModelScope.launch {
            tipoVueloRepository.getTipoVuelos().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                tipovuelo = result.data ?: emptyList(),
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

fun TipoVueloUiState.toEntity() = TipoVueloDTO(
    tipoVueloId = tipoVueloId,
    nombreVuelo = nombreVuelo,
    descripcionTipoVuelo = descripcionTipoVuelo
)