package edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.local.repository.RutaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RutaViewModel @Inject constructor(
    private val rutasRepository: RutaRepository

): ViewModel() {
    private val _uiState = MutableStateFlow(RutaUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: RutaEvent) {
        when (event) {
            is RutaEvent.AeronaveChange -> TODO()
            RutaEvent.Delete -> TODO()
            is RutaEvent.DestinoChange -> TODO()
            is RutaEvent.DistanciaChange -> TODO()
            is RutaEvent.DuracionEstimadaChange -> TODO()
            RutaEvent.New -> TODO()
            is RutaEvent.OrigenChange -> TODO()
            is RutaEvent.RutaChange -> TODO()
            RutaEvent.Save -> TODO()
        }
    }

    init {
        getRuta()
    }

    //saveRuta
    private fun save() {
        viewModelScope.launch {
            if (_uiState.value.aeronaveId < 0 || _uiState.value.origen.isNullOrBlank() || _uiState.value.destino.isNullOrBlank() || _uiState.value.distancia.isNullOrBlank() || _uiState.value.duracionEstimada.isNullOrBlank()) {
                _uiState.update {
                    it.copy(errorMessage = "Campo vacios")
                }
            } else {
                rutasRepository.saveRuta(_uiState.value.toEntity())
            }
        }
    }

    private fun nuevo() {
        _uiState.update {
            it.copy(
                rutaId = null,
                aeronaveId = 0,
                origen = "",
                destino = "",
                distancia = "",
                duracionEstimada = "",
                errorMessage = null
            )
        }
    }

    //findRuta
    fun selectedRuta(rutaId: Int) {
        viewModelScope.launch {
            if (rutaId > 0) {
                val ruta = rutasRepository.findRuta(rutaId)
                _uiState.update {
                    it.copy(
                        rutaId = ruta?.rutaId,
                        aeronaveId = ruta?.aeronaveId ?: 0,
                        origen = ruta?.origen ?: "",
                        destino = ruta?.destino ?: "",
                        distancia = ruta?.distancia ?: "",
                        duracionEstimada = ruta?.duracionEstimada ?: "",
                    )
                }
            }
        }
    }

    //deleteRuta
    private fun delete() {
        viewModelScope.launch {
            rutasRepository.deleteRuta(_uiState.value.toEntity())
        }
    }

    private fun getRuta() {
        viewModelScope.launch {
            rutasRepository.getAll().collect { rutas ->
                _uiState.update {
                    it.copy(rutas = rutas)
                }
            }
        }
    }

    private fun onAeronaveChange(aeronaveId: Int) {
        _uiState.update {
            it.copy(aeronaveId = aeronaveId)
        }
    }

    private fun onOrigenChange(origen: String) {
        _uiState.update {
            it.copy(origen = origen)
        }
    }

    private fun onDestinoChange(destino: String) {
        _uiState.update {
            it.copy(destino = destino)
        }
    }

    private fun onDistanciaChange(distancia: String) {
        _uiState.update {
            it.copy(distancia = distancia)
        }
    }

    private fun onDuracionEstimadaChange(duracionEstimada: String) {
        _uiState.update {
            it.copy(duracionEstimada = duracionEstimada)
        }
    }


    fun RutaUiState.toEntity() = RutaEntity(
        rutaId = rutaId,
        aeronaveId = aeronaveId ?: 0,
        origen = origen ?: "",
        destino = destino ?: "",
        distancia = distancia ?: "",
        duracionEstimada = duracionEstimada ?: ""
    )
}