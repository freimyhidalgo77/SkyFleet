package edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import edu.ucne.skyplanerent.data.local.repository.TipoVueloRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    //saveTipoVuelo
    private fun save() {
        viewModelScope.launch {
            if (_uiState.value.rutaId < 0 || _uiState.value.tipoclienteId < 0 || _uiState.value.descripcionTipoVuelo.isNullOrBlank() || _uiState.value.precio <= 0.0) {
                _uiState.update {
                    it.copy(errorMessage = "Campos vacíos o inválidos")
                }
            } else {
                tipovuelorepository.saveTipoVuelo(_uiState.value.toEntity())
            }
        }
    }

    private fun nuevo() {
        _uiState.update {
            it.copy(
                vueloId = null,
                rutaId = 0,
                tipoclienteId = 0,
                descripcionTipoVuelo = "",
                precio = 0.0,
                errorMessage = null
            )
        }
    }

    //findTipoVuelo
    fun selectedTipoVuelo(vueloId: Int) {
        viewModelScope.launch {
            if (vueloId > 0) {
                val tipovuelo = tipovuelorepository.findTipoVuelo(vueloId)
                _uiState.update {
                    it.copy(
                        vueloId = tipovuelo?.vueloId,
                        rutaId = tipovuelo?.rutaId ?: 0,
                        tipoclienteId = tipovuelo?.tipoClienteId?: 0,
                        descripcionTipoVuelo = tipovuelo?.descripcionTipoVuelo ?: "",
                        precio = tipovuelo?.precio ?: 0.0
                    )
                }
            }
        }
    }

    //deleteTipoVuelo
    private fun delete() {
        viewModelScope.launch {
            tipovuelorepository.deleteTipoVuelo(_uiState.value.toEntity())
        }
    }

    private fun getTipoVuelo() {
        viewModelScope.launch {
            tipovuelorepository.getAll().collect { tipovuelo ->
                _uiState.update {
                    it.copy(tipovuelo = tipovuelo)
                }
            }
        }
    }

    private fun onRutaChange(rutaId: Int) {
        _uiState.update {
            it.copy(rutaId = rutaId)
        }
    }

    private fun onTipoClienteChange(tipocliente: Int) {
        _uiState.update {
            it.copy(tipoclienteId = tipocliente)
        }
    }

    private fun onDescripcionTipoVueloChange(descripciontipovuelo: String) {
        _uiState.update {
            it.copy(descripcionTipoVuelo = descripciontipovuelo)
        }
    }

    private fun onPrecioChange(precio: Double) {
        _uiState.update {
            it.copy(precio = precio)
        }
    }


    fun TipoVueloUiState.toEntity() = TipoVueloEntity(
        vueloId,
        rutaId = rutaId ?: 0,
        tipoClienteId = tipoclienteId ?: 0,
        descripcionTipoVuelo = descripcionTipoVuelo ?: "",
        precio = precio ?: 0.0
    )
}