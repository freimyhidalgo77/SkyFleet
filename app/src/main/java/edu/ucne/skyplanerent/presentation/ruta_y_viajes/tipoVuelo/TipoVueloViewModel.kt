package edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.data.repository.TipoVueloRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TipoVueloViewModel @Inject constructor(
    private val tipovuelorepository: TipoVueloRepository

): ViewModel() {
    private val _uiState = MutableStateFlow(TipoVueloUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: TipoVueloEvent) {
        when (event) {
            TipoVueloEvent.Delete -> TODO()
            is TipoVueloEvent.DescripcionTipoVueloChange -> TODO()
            TipoVueloEvent.New -> TODO()
            is TipoVueloEvent.PrecioChange -> TODO()
            is TipoVueloEvent.RutaChange -> TODO()
            TipoVueloEvent.Save -> TODO()
            is TipoVueloEvent.TipoClienteChange -> TODO()
            is TipoVueloEvent.TipoVueloChange -> TODO()
        }
    }

    init {
        getTipoVuelo()
    }


    fun saveTipoVuelo() {
        viewModelScope.launch {
            if (_uiState.value.nombreVuelo.isBlank() || _uiState.value.descripcionTipoVuelo.isBlank()) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Los campos deben estar llenos!", successMessage = null
                    )
                }
                return@launch
            }
            try {
                tipovuelorepository.save(_uiState.value.toDTO())
                _uiState.update {
                    it.copy(
                        successMessage = "La ruta se ha guardado con exito!", errorMessage = null
                    )
                }
                nuevo()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Hubo un error al guardar la ruta", successMessage = null
                    )
                }
            }
        }
    }


        private fun nuevo() {
        _uiState.update {
            it.copy(
                tipoVueloId = null,
                descripcionTipoVuelo = "",
                successMessage = null,
                errorMessage = null
            )
        }
    }

    fun findTipoVuelo(vueloId: Int) {
        viewModelScope.launch {
            if (vueloId > 0) {
                val vueloDTO = tipovuelorepository.find(vueloId)
                if (vueloDTO.TipoVueloId!= 0) {
                    _uiState.update {
                        it.copy(
                            tipoVueloId = vueloDTO.TipoVueloId,
                            nombreVuelo = vueloDTO.nombreVuelo,
                            descripcionTipoVuelo = vueloDTO.descripcionTipoVuelo
                        )
                    }
                }
            }
        }
    }

    //deleteTipoVuelo
    private fun delete(id:Int) {
        viewModelScope.launch {
            tipovuelorepository.delete(id)
        }
    }

    fun getTipoVuelo() {
        viewModelScope.launch {
            tipovuelorepository.getTipoVuelos().collectLatest { getting ->
                when (getting) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                tipovuelo = getting.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = getting.message
                                    ?: "Hubo un error al cargar la retencion",
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }


    private fun onDescripcionTipoVueloChange(descripciontipovuelo: String) {
        _uiState.update {
            it.copy(descripcionTipoVuelo = descripciontipovuelo)
        }
    }


    fun TipoVueloUiState.toDTO() = TipoVueloDTO(
        TipoVueloId = tipoVueloId,
        nombreVuelo = nombreVuelo,
        descripcionTipoVuelo = descripcionTipoVuelo ?: "",

    )

}